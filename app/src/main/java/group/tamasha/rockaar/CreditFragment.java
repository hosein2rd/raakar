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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import group.tamasha.rockaar.util.IabHelper;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class CreditFragment extends Fragment {

    Button credit_shaba_button, credit_request_button;

    SharedPreferences Cardnumber, Token;

    String cardnumber, token, url_depositeRequest = "https://raakar.ir/depositeRequest";

    TextView credit_text_left_bubble, credit_text_center_bubble, credit_text_right_bubble;

    PrettyDialog pDialog1;
    PrettyDialog pDialog2;
    PrettyDialog pDialog3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_credit, container, false);
        popupForCheckInternet();

        credit_shaba_button = (Button)view.findViewById(R.id.credit_request_button);
        credit_request_button = (Button)view.findViewById(R.id.credit_transaction_button);
        credit_text_right_bubble = (TextView)view.findViewById(R.id.credit_text2_right_bubble);
        credit_text_center_bubble = (TextView)view.findViewById(R.id.credit_text2_center_bubble);
        credit_text_left_bubble = (TextView)view.findViewById(R.id.credit_text2_left_bubble);

        Cardnumber = getActivity().getSharedPreferences("cardnumber", Context.MODE_PRIVATE);
        cardnumber = Cardnumber.getString("cardnumber", "");

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        final JsonObjectRequest pjsonRequest = new JsonObjectRequest
                (Request.Method.GET, "https://raakar.ir/info", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response:", response.toString());
                        try {
                            JSONObject info = response.getJSONObject("info");
                            JSONObject credit = info.getJSONObject("credit");
                            String total = credit.getString("total");
                            String current = credit.getString("current");
                            String withdraw = credit.getString("withdrew");
                            credit_text_right_bubble.setText(withdraw);
                            credit_text_center_bubble.setText(current);
                            credit_text_left_bubble.setText(total);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("error", error.toString());
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

        credit_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cardnumber.equals("")){
                    popupForEmptyCardnumber();
                }else {
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url_depositeRequest,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d("Response", response);
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

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError
                        {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Content-Type", "application/x-www-form-urlencoded");
                            headers.put("token", token);
                            return headers;
                        }
                    };
                    Singleton.getInstance(getActivity()).addToRequestqueue(postRequest);

                    final JsonObjectRequest pjsonRequest = new JsonObjectRequest
                            (Request.Method.GET, "https://raakar.ir/info", null, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Response:", response.toString());
                                    try {
                                        JSONObject info = response.getJSONObject("info");
                                        JSONObject credit = info.getJSONObject("credit");
                                        String total = credit.getString("total");
                                        String current = credit.getString("current");
                                        String withdraw = credit.getString("withdrew");
                                        credit_text_right_bubble.setText(withdraw);
                                        credit_text_center_bubble.setText(current);
                                        credit_text_left_bubble.setText(total);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("error", error.toString());
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

                    popupForSuccessDepositeRequest();
                }
            }
        });

        credit_shaba_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddShabaFragment addShabaFragment = new AddShabaFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.mycontainer, addShabaFragment).addToBackStack("back").commit();

            }
        });



        return view;
    }

    public void popupForEmptyCardnumber(){
        pDialog1 = new PrettyDialog(getActivity());
        pDialog1.setCancelable(false);
        pDialog1
                .setTitle("توجه!")
                .setMessage("برای وارد کردن شماره کارت افزودن شماره کارت را کلیک کنید")
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

    public void popupForSuccessDepositeRequest(){
        pDialog3 = new PrettyDialog(getActivity());
        pDialog3.setCancelable(false);
        pDialog3
                .setTitle("تبریک!")
                .setMessage("درخواست واریز وجه با موفقیت انجام شد")
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
                                pDialog3.dismiss();
                                CreditFragment creditFragment = new CreditFragment();
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                manager.beginTransaction().replace(R.id.mycontainer, creditFragment).commit();
                            }
                        }
                )
                .show();
    }


}
