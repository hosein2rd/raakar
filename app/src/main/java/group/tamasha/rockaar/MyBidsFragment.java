package group.tamasha.rockaar;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class MyBidsFragment extends Fragment {

    ListView mybids_listview;
    ArrayList<MyBidsAdapterItems> mlistnewsData = new ArrayList<MyBidsAdapterItems>();
    String url = "https://raakar.ir/getMyProjects", token, projectIndex, url_accept = "https://raakar.ir/acceptOffer", offerIndex;
    SharedPreferences Token, usernameOfOffer;
    Bundle bundle;
    int position;
    TextView mybids_title, mybids_details, mybids_nulltext;
    ArrayList<String> usernames = new ArrayList<String>();
    SharedPreferences.Editor usernameEditor;
    String paymentURL = "https://raakar.ir", trimmed, amountString;
    PrettyDialog pDialog1;
    PrettyDialog pDialog2;

    public MyBidsFragment() {
        // Required empty public constructor
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mybids, container, false);

        popupForCheckInternet();

        bundle = getArguments();
        position = bundle.getInt("position");

        mybids_title = (TextView)view.findViewById(R.id.mybids_title);
        mybids_details = (TextView)view.findViewById(R.id.mybids_details);
        mybids_nulltext = (TextView)view.findViewById(R.id.mybids_nulltext);

        mybids_title.setText(bundle.getString("name"));

        mybids_listview = (ListView)view.findViewById(R.id.mybids_listview);

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        usernameOfOffer = getActivity().getSharedPreferences("username", Context.MODE_PRIVATE);
        usernameEditor = usernameOfOffer.edit();

        final JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response:", response.toString());
                        mlistnewsData.clear();
                        try {

                            mybids_nulltext.setVisibility(View.GONE);

                            JSONArray myProjects = response.getJSONArray("yourProjects");
                            JSONObject offerInfo = myProjects.getJSONObject(position);

                            projectIndex = offerInfo.getString("projectIndex");

                            amountString = offerInfo.getString("amount");
                            String Text = amountString.replaceAll("[^0-9]+","");
                            long Number = Long.parseLong(Text);
                            DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
                            String yourFormattedString = formatter.format(Number);

                            if (yourFormattedString.equals("0")){
                                mybids_details.setText("توافقی");
                            }else {
                                mybids_details.setText(yourFormattedString + " تومان " + " ( " + offerInfo.getString("deadline") + " روز ) " );
                            }

                            JSONArray offerArray = offerInfo.getJSONArray("offersArray");

                            if (offerArray.length() == 0){
                                mybids_nulltext.setVisibility(View.VISIBLE);
                            }else {
                                for (int j = 0; j < offerArray.length(); j++){
                                    JSONObject infoOfOffer = offerArray.getJSONObject(j);

                                    String TXT = infoOfOffer.getString("amount");
                                    String MyText = TXT.replaceAll("[^0-9]+","");
                                    long MyNumber = Long.parseLong(MyText);
                                    DecimalFormat myformatter = new DecimalFormat("#,###,###,###,###");
                                    String myFormattedString = myformatter.format(MyNumber);

//                                    int day = infoOfOffer.getInt("");
                                    if (infoOfOffer.getBoolean("status") == false){
                                        mlistnewsData.add(new MyBidsAdapterItems(1,
                                                1,
                                                infoOfOffer.getString("creatorName"),
                                                infoOfOffer.getString("description"),
                                                infoOfOffer.getString("creationDate"),
                                                myFormattedString,
                                                "-1000000",
                                                infoOfOffer.getString("deadline"),
                                                "+10"));
                                        usernames.add(infoOfOffer.getString("creatorUsername"));
                                    }
                                }

                                MyBidsFragment.MyBidsAdapter madapter;
                                madapter = new MyBidsAdapter(mlistnewsData);
                                mybids_listview.setAdapter(madapter);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){@Override
        public Map<String, String> getHeaders() throws AuthFailureError
        {
            HashMap<String, String> headers = new HashMap<String, String>();
            //headers.put("Content-Type", "application/json");
            headers.put("token", token);
            return headers;
        }};
        Singleton.getInstance(getActivity()).addToRequestqueue(jsonRequest);

        mybids_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                PublicProfileFragment PublicProfileFragment = new PublicProfileFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.mycontainer, PublicProfileFragment).addToBackStack("Back").commit();

                String username = usernames.get(position);
                usernameEditor.putString("userName", username).commit();
            }
        });

        return view;
    }

    private class MyBidsAdapter extends BaseAdapter {
        public ArrayList<MyBidsAdapterItems> mlistnewsDataAdpater ;

        public MyBidsAdapter(ArrayList<MyBidsAdapterItems>  mlistnewsDataAdpater) {
            this.mlistnewsDataAdpater=mlistnewsDataAdpater;
        }

        @Override
        public int getCount() {
            return mlistnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getActivity().getLayoutInflater();
            View myView = mInflater.inflate(R.layout.mybids_item, null);

            final MyBidsAdapterItems m = mlistnewsDataAdpater.get(position);

            TextView name = (TextView)myView.findViewById(R.id.mybids_name);
            name.setText(m.mname);

            TextView describtion = (TextView)myView.findViewById(R.id.mybids_describtion);
            describtion.setText(m.mdescribtion);

            TextView date = (TextView)myView.findViewById(R.id.mybids_date);
            date.setText(m.mdate);

            TextView bidprice = (TextView)myView.findViewById(R.id.mybids_bidprice);
            bidprice.setText(m.mbidprice);

            TextView bidtime = (TextView)myView.findViewById(R.id.mybids_bidtime);
            bidtime.setText(m.mbidtime);

            ImageView mybids_accept_button = (ImageView)myView.findViewById(R.id.mybids_accept_button);

            mybids_accept_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    offerIndex = String.valueOf(mybids_listview.getPositionForView(v));
                    popupForAccept();
                }
            });

            return myView;
        }

    }

    public void popupForAccept(){
//        new AwesomeInfoDialog(getActivity())
//                .setTitle("توجه!")
//                .setMessage("برای پذیرش پیشنهاد شما باید 20% مبلغ پیشنهادی را پرداخت نمایید!")
//                .setColoredCircle(R.color.RakaarColor)
//                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
//                .setCancelable(true)
//                .setPositiveButtonText("باشه")
//                .setPositiveButtonbackgroundColor(R.color.RakaarColor)
//                .setPositiveButtonTextColor(R.color.white)
//                .setPositiveButtonClick(new Closure() {
//                    @Override
//                    public void exec() {
//                        StringRequest postRequest = new StringRequest(Request.Method.POST, url_accept,
//                                new Response.Listener<String>()
//                                {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        // response
//                                        Log.d("Response", response);
//                                        trimmed = response.substring(1, response.length()-1);
//                                        paymentURL += trimmed;
//                                        Log.d("payment", paymentURL);
//                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentURL));
//                                        startActivity(intent);
//                                        paymentURL = "https://raakar.ir";
//                                        trimmed = "";
//                                    }
//                                },
//                                new Response.ErrorListener() {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        // error
//                                        Log.d("Error.Response", "Error");
//
//                                    }
//                                }
//                        ) {
//                            @Override
//                            protected Map<String, String> getParams()
//                            {
//                                Map<String, String>  params = new HashMap<String, String>();
//                                params.put("projectIndex", projectIndex);
//                                params.put("offerIndex", offerIndex);
//                                params.put("description", "پیش پرداخت انجام پروژه برای راکار");
//                                params.put("email", "raakarapp@gmail.com");
//                                params.put("phonenumber", "09016081024");
//
//                                return params;
//                            }
//
//                            @Override
//                            public Map<String, String> getHeaders() throws AuthFailureError
//                            {
//                                HashMap<String, String> headers = new HashMap<String, String>();
//                                //headers.put("Content-Type", "application/json");
//                                headers.put("token", token);
//                                return headers;
//                            }
//                        };
//                        Singleton.getInstance(getActivity()).addToRequestqueue(postRequest);
//                    }
//                })
//                .setNegativeButtonText("نه")
//                .setNegativeButtonbackgroundColor(R.color.RakaarColor)
//                .setNegativeButtonTextColor(R.color.white)
//                .setNegativeButtonClick(new Closure() {
//                    @Override
//                    public void exec() {
//                        //click
//                    }
//                })
//                .show();
        pDialog1 = new PrettyDialog(getActivity());
        pDialog1
                .setTitle("توجه!")
                .setMessage("برای پذیرش پیشنهاد شما باید 10% مبلغ پیشنهادی را پرداخت نمایید!")
                .setIcon(
                        R.drawable.pdlg_icon_info,     // icon resource
                        R.color.RakaarColor, null)
                .addButton(
                        "باشه",
                        R.color.pdlg_color_white,
                        R.color.RakaarColor,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog1.dismiss();
                                StringRequest postRequest = new StringRequest(Request.Method.POST, url_accept,
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                Log.d("Response", response);
                                                trimmed = response.substring(1, response.length()-1);
                                                paymentURL += trimmed;
                                                Log.d("payment", paymentURL);
                                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(paymentURL));
                                                startActivity(intent);
                                                paymentURL = "https://raakar.ir";
                                                trimmed = "";
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // error
                                                Log.d("Error.Response", "Error");

                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams()
                                    {
                                        Map<String, String>  params = new HashMap<String, String>();
                                        params.put("projectIndex", projectIndex);
                                        params.put("offerIndex", offerIndex);
                                        params.put("description", "پیش پرداخت انجام پروژه برای راکار");
                                        params.put("email", "raakarapp@gmail.com");
                                        params.put("phonenumber", "09016081024");

                                        return params;
                                    }

                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError
                                    {
                                        HashMap<String, String> headers = new HashMap<String, String>();
                                        //headers.put("Content-Type", "application/json");
                                        headers.put("token", token);
                                        return headers;
                                    }
                                };
                                Singleton.getInstance(getActivity()).addToRequestqueue(postRequest);
                            }
                        }
                )
                .addButton(
                        "نه",
                        R.color.pdlg_color_white,
                        R.color.RakaarColor,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog1.dismiss();
                            }
                        }
                )
                .show();
    }

    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
        }else{
            pDialog2 = new PrettyDialog(getActivity());
            pDialog2.setCancelable(false);
            pDialog2
                    .setTitle("خطا!")
                    .setMessage("لطفا اتصال به اینترنت را برقرار نمایید")
                    .setIcon(
                            R.drawable.pdlg_icon_close,     // icon resource
                            R.color.pdlg_color_red, null)
                    .addButton(
                            "تلاش مجدد",
                            R.color.pdlg_color_white,
                            R.color.pdlg_color_red,
                            new PrettyDialogCallback() {
                                @Override
                                public void onClick() {
                                    popupForCheckInternet();
                                    pDialog2.dismiss();
                                }
                            }
                    )
                    .show();
        }

    }

}
