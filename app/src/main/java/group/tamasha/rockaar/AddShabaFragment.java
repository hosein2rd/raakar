package group.tamasha.rockaar;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class AddShabaFragment extends Fragment {

    SharedPreferences Cardnumber, Token;

    Button addshaba_acceptcard_button;

    EditText addshaba_cardnumber_edittext;

    String token;

    CreditCardFormatTextWatcher tv;

    PrettyDialog pDialog1;

    PrettyDialog pDialog2;

    PrettyDialog pDialog3, pDialog4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_shaba, container, false);

        addshaba_cardnumber_edittext = (EditText)view.findViewById(R.id.addshaba_cardnumber_edittext);
        addshaba_acceptcard_button = (Button)view.findViewById(R.id.addshaba_acceptcard_button);

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        tv = new CreditCardFormatTextWatcher(addshaba_cardnumber_edittext);
        addshaba_cardnumber_edittext.addTextChangedListener(tv);

        addshaba_acceptcard_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addshaba_cardnumber_edittext.length() == 0){
                    popupForEmptyEditText();
                }else {
                    if (addshaba_cardnumber_edittext.length() != 16){
                        popupForLengthOfEditText();
                    }else {

                        StringRequest postRequest = new StringRequest(Request.Method.POST, "https://raakar.ir/addCardNumber",
                                new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String response) {
                                        Log.d("Response", response);

                                        Cardnumber = getActivity().getSharedPreferences("cardnumber", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor CardnumberEditor = Cardnumber.edit();
                                        CardnumberEditor.putString("cardnumber", addshaba_cardnumber_edittext.getText().toString());
                                        CardnumberEditor.commit();

                                        popupForSuccessAddCardNumber();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d("Error.Response", "Error");

                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams()
                            {
                                Map<String, String>  params = new HashMap<String, String>();
                                params.put("cardNumber", addshaba_cardnumber_edittext.getText().toString());

                                return params;
                            }

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

            }
        });

        return view;
    }

    public void popupForLengthOfEditText(){
        pDialog1 = new PrettyDialog(getActivity());
        pDialog1.setCancelable(false);
        pDialog1
                .setTitle("توجه!")
                .setMessage("شماره کارت باید 16 رقمی باشد")
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

    public void popupForSuccessAddCardNumber(){
        pDialog2 = new PrettyDialog(getActivity());
        pDialog2.setCancelable(false);
        pDialog2
                .setTitle("تبریک!")
                .setMessage("شماره کارت شما با موفقیت ثبت شد")
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
                                pDialog2.dismiss();
                                CreditFragment creditFragment = new CreditFragment();
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                manager.beginTransaction().replace(R.id.mycontainer, creditFragment).commit();
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
            pDialog4 = new PrettyDialog(getActivity());
            pDialog4.setCancelable(false);
            pDialog4
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
                                    pDialog4.dismiss();
                                }
                            }
                    )
                    .show();

        }

    }

    public void popupForEmptyEditText(){
        pDialog3 = new PrettyDialog(getActivity());
        pDialog3.setCancelable(false);
        pDialog3
                .setTitle("توجه!")
                .setMessage("شماره کارت را وارد کنید")
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
                                pDialog3.dismiss();
                            }
                        }
                )
                .show();
    }

}
