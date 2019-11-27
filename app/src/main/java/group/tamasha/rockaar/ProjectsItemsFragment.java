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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

import static android.content.ContentValues.TAG;


public class ProjectsItemsFragment extends Fragment {

    ArrayList<OtherBidsAdapterItems> mlistnewsData = new ArrayList<OtherBidsAdapterItems>();

    public ProjectsItemsFragment() {
        // Required empty public constructor
    }

    SharedPreferences Token, Username, filePath;
    String token, url1 = "https://raakar.ir/addOffer", position, username;
    String URLgetallprojects = "https://raakar.ir/getAllProjects";
    EditText description, deadline, amount;
    Button sendBid;
    SharedPreferences pos;
    ScrollView ParentScroll, ChildScroll;
    ListView ChildListView;
    TextView Name, ProjectOwner, Amount, Deadline, OfferNumber, Description, pi_nulltext, pi_timetoman;
    ImageView pi_download_imageview;
    String url_download;
    PrettyDialog pDialog1;
    PrettyDialog pDialog2;
    PrettyDialog pDialog8;
    PrettyDialog pDialog6;

    int offerNumber;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_projects_items, container, false);

        popupForCheckInternet();

        Name = (TextView)view.findViewById(R.id.pi_header);
        ProjectOwner = (TextView)view.findViewById(R.id.pi_underheader);
        Amount = (TextView)view.findViewById(R.id.pi_pricevalue);
        Deadline = (TextView)view.findViewById(R.id.pi_timevalue);
        OfferNumber = (TextView)view.findViewById(R.id.pi_bidsvalue);
        Description = (TextView)view.findViewById(R.id.pi_infotext);
        pi_nulltext = (TextView)view.findViewById(R.id.pi_nulltext);
        pi_timetoman = (TextView)view.findViewById(R.id.pi_timetoman);
        pi_download_imageview = (ImageView)view.findViewById(R.id.pi_download_imageview);

        final Bundle bundle = getArguments();

        Log.e("projectOwnerUsername", bundle.getString("projectOwnerUsername"));
        Log.e("projectIndex", bundle.getString("projectIndex"));

        Name.setText(bundle.getString("name"));
        ProjectOwner.setText(bundle.getString("projectOwnerName") + " / " + bundle.getString("category"));
//        Amount.setText(bundle.getString("amount"));
        Deadline.setText(bundle.getString("deadline"));
        OfferNumber.setText(bundle.getString("offerNumber"));
        Description.setText(bundle.getString("description"));
        final ListView pi_listview = (ListView)view.findViewById(R.id.pi_listview);
        mlistnewsData.clear();

        String T = bundle.getString("amount");
        String Text = T.replaceAll("[^0-9]+","");
        long Number = Long.parseLong(Text);
        DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
        String yourFormattedString = formatter.format(Number);

        pi_timetoman.setText("تومان");

        if (bundle.getString("amount").equals("0")){

            Amount.setText("توافقی");
            pi_timetoman.setText("");

        }else {

            Amount.setText(yourFormattedString);
            pi_timetoman.setText("تومان");
        }

        filePath = getActivity().getSharedPreferences("filePath", Context.MODE_PRIVATE);
        url_download = filePath.getString("filePath", "");
        Log.e("url_download", url_download);

        pi_download_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_download));
                startActivity(intent);
            }
        });

        final JsonArrayRequest pjsonRequest = new JsonArrayRequest
                (Request.Method.GET, URLgetallprojects, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response:", response.toString());
//                        JSONArray offerArray = response.getJSONArray("offersArray");
                        try {

                            JSONObject selectedProject = response.getJSONObject(bundle.getInt("position"));
                            JSONArray otherOffers = selectedProject.getJSONArray("offersArray");

                            if(otherOffers.length() == 0){
                                pi_listview.setVisibility(View.GONE);
                                pi_nulltext.setVisibility(View.VISIBLE);
                            }else {
                                pi_listview.setVisibility(View.VISIBLE);
                                pi_nulltext.setVisibility(View.GONE);

                                for (int i = 0; i < otherOffers.length(); i++){
                                    JSONObject offer = otherOffers.getJSONObject(i);
                                    String T = offer.getString("amount");
                                    String Text = T.replaceAll("[^0-9]+","");
                                    long Number = Long.parseLong(Text);
                                    DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
                                    String yourFormattedString = formatter.format(Number);
                                    mlistnewsData.add(new OtherBidsAdapterItems(0,
                                            offer.getString("creatorName"),
                                            yourFormattedString + " تومان",
                                            offer.getString("deadline") + " روز " ,
                                            offer.getString("creationDate")));
                                }

                                ProjectsItemsFragment.OtherBidsAdapter madapter;
                                madapter = new ProjectsItemsFragment.OtherBidsAdapter(mlistnewsData);
                                pi_listview.setAdapter(madapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pi_listview.setVisibility(View.INVISIBLE);
                        pi_nulltext.setText("مشکل در اتصال به اینترنت!");
                        pi_listview.setVisibility(View.VISIBLE);
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                return headers;
            }};
        Singleton.getInstance(getActivity()).addToRequestqueue(pjsonRequest);

        final JsonObjectRequest pjsonRequest1 = new JsonObjectRequest
                (Request.Method.GET, "https://raakar.ir/info", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response:", response.toString());
                        try {
                            JSONObject info = response.getJSONObject("info");
                            offerNumber = info.getInt("myoffernumber");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                       Log.e("response", error.toString());
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                return headers;
            }};
        Singleton.getInstance(getActivity()).addToRequestqueue(pjsonRequest1);

        ParentScroll = (ScrollView)view.findViewById(R.id.parent_scroll);
        ChildScroll = (ScrollView)view.findViewById(R.id.pi_infoscroll);
        ChildListView = (ListView)view.findViewById(R.id.pi_listview);

        ParentScroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                Log.v(TAG,"PARENT TOUCH");
                v.findViewById(R.id.pi_infoscroll).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });

        ChildScroll.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event)
            {
                Log.v(TAG,"CHILD TOUCH");
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        ChildListView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event)
            {
                Log.v(TAG,"CHILD TOUCH");
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        Username = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        username = Username.getString("user", null);

        description = (EditText)view.findViewById(R.id.pi_decription_edittext);
        deadline = (EditText)view.findViewById(R.id.pi_timenumber);
        amount = (EditText)view.findViewById(R.id.pi_pricenumber);
        sendBid = (Button)view.findViewById(R.id.pi_button);

        pos = getActivity().getSharedPreferences("position", Context.MODE_PRIVATE);
        position = pos.getString("position", null);
        SharedPreferences.Editor posEdit = pos.edit();
        posEdit.clear().commit();

        sendBid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!amount.getText().toString().equals("") && !deadline.getText().toString().equals("")){

                    if (offerNumber != 0){
                        String T = amount.getText().toString();
                        final String Text = T.replaceAll("[^0-9]+","");

                        StringRequest postRequest = new StringRequest(Request.Method.POST, url1,
                                new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response) {
                                        // response
                                        Log.d("Response", response);
                                        popupForSuccessSendBid();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // error
                                        Log.d("Error.Response", error.toString());
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams()
                            {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("amount", Text);
                                params.put("description", description.getText().toString());
                                params.put("deadline", deadline.getText().toString());
                                params.put("deadlineType", "" + 0);
                                params.put("projectIndex", bundle.getString("projectIndex"));
                                params.put("projectOwnerUsername",bundle.getString("projectOwnerUsername"));

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
                    else {
                        pDialog8 = new PrettyDialog(getActivity());
                        pDialog8.setCancelable(false);
                        pDialog8
                                .setTitle("خطا!")
                                .setMessage("تعداد پیشنهادات شما برای ارسال کافی نیست. برای خرید از منوی داشبورد اقدام کنید")
                                .setIcon(
                                        R.drawable.pdlg_icon_close,     // icon resource
                                        R.color.pdlg_color_red, null)
                                .addButton(
                                        "باشه",
                                        R.color.pdlg_color_white,
                                        R.color.pdlg_color_red,
                                        new PrettyDialogCallback() {
                                            @Override
                                            public void onClick() {
                                                pDialog8.dismiss();
                                            }
                                        }
                                )
                                .show();
                    }

                }else {
//                    new AwesomeNoticeDialog(getActivity())
//                            .setTitle("خطا!")
//                            .setMessage("فیلدهای مدت زمان انجام و قیمت پیشنهادی نباید خالی باشد")
//                            .setColoredCircle(R.color.dialogErrorBackgroundColor)
//                            .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
//                            .setCancelable(true)
//                            .setButtonText(getString(R.string.dialog_ok_button))
//                            .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
//                            .setButtonText(getString(R.string.dialog_ok_button))
//                            .setNoticeButtonClick(new Closure() {
//                                @Override
//                                public void exec() {
//                                    // click
//                                }
//                            })
//                            .show();
                    pDialog6 = new PrettyDialog(getActivity());
                    pDialog6.setCancelable(false);
                    pDialog6
                            .setTitle("خطا!")
                            .setMessage("فیلدهای مدت زمان انجام و قیمت پیشنهادی نباید خالی باشد")
                            .setIcon(
                                    R.drawable.pdlg_icon_close,     // icon resource
                                    R.color.pdlg_color_red, null)
                            .addButton(
                                    "باشه",
                                    R.color.pdlg_color_white,
                                    R.color.pdlg_color_red,
                                    new PrettyDialogCallback() {
                                        @Override
                                        public void onClick() {
                                            pDialog6.dismiss();
                                        }
                                    }
                            )
                            .show();
                }
            }
        });

        amount.addTextChangedListener(onTextChangedListener());

        return view;
    }

    public void popupForSuccessSendBid(){
//        new AwesomeSuccessDialog(getContext())
//                .setTitle("تبریک!")
//                .setMessage("پیشنهاد شما با موفقیت ارسال شد")
//                .setPositiveButtonText("باشه")
//                .setPositiveButtonClick(new Closure() {
//                    @Override
//                    public void exec() {
//                        ProjectsFragment projectsFragment = new ProjectsFragment();
//                        getActivity().getSupportFragmentManager().beginTransaction()
//                                .replace(R.id.mycontainer, projectsFragment,"findThisFragment")
//                                .addToBackStack(null)
//                                .commit();
//                    }
//                })
//                .show();
        pDialog1 = new PrettyDialog(getActivity());
        pDialog1.setCancelable(false);
        pDialog1
                .setTitle("تبریک!")
                .setMessage("پیشنهاد شما با موفقیت ارسال شد")
                .setIcon(
                        R.drawable.pdlg_icon_success,     // icon resource
                        R.color.pdlg_color_green, null)
                .addButton(
                        "باشه",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_green,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog1.dismiss();
                                ProjectsFragment projectsFragment = new ProjectsFragment();
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.mycontainer, projectsFragment,"findThisFragment")
                                        .addToBackStack(null)
                                        .commit();
                            }
                        }
                )
                .show();
    }

    private class OtherBidsAdapter extends BaseAdapter {
        public ArrayList<OtherBidsAdapterItems> mlistnewsDataAdpater ;

        public OtherBidsAdapter(ArrayList<OtherBidsAdapterItems>  mlistnewsDataAdpater) {
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
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getActivity().getLayoutInflater();
            View myView = mInflater.inflate(R.layout.otherbids_item, null);

            final OtherBidsAdapterItems m = mlistnewsDataAdpater.get(position);

            TextView name = (TextView)myView.findViewById(R.id.ob_name);
            name.setText(m.mname);

            TextView price = (TextView)myView.findViewById(R.id.ob_price);
            price.setText(m.mprice);

            TextView time = (TextView)myView.findViewById(R.id.ob_time);
            time.setText(m.mtime);

            TextView date = (TextView)myView.findViewById(R.id.ob_date);
            date.setText(m.mdate);

            return myView;
        }

    }

    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
        }else{
//            new AwesomeNoticeDialog(getActivity())
//                    .setTitle("خطا!")
//                    .setMessage("لطفا اتصال به اینترنت را برقرار نمایید")
//                    .setColoredCircle(R.color.dialogErrorBackgroundColor)
//                    .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
//                    .setCancelable(false)
//                    .setButtonText("تلاش مجدد")
//                    .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
//                    .setButtonText("تلاش مجدد")
//                    .setNoticeButtonClick(new Closure() {
//                        @Override
//                        public void exec() {
//                            popupForCheckInternet();
//                        }
//                    })
//                    .show();

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

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                amount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    amount.setText(formattedString);
                    amount.setSelection(amount.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                amount.addTextChangedListener(this);
            }
        };
    }
}
