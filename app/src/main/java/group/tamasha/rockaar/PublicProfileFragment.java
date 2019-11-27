package group.tamasha.rockaar;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class PublicProfileFragment extends Fragment {


    public PublicProfileFragment() {
        // Required empty public constructor
    }

    String url = "https://raakar.ir/getProfile", token, username;
    TextView PP_Name, PP_Expertise, PP_Skills, PP_Aboutme, PP_Score, PP_Works;
    SharedPreferences Token, usernameOfOffer;
    ImageView pp_picture;
    PrettyDialog pDialog;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_publicprofile, container, false);

        popupForCheckInternet();

        PP_Name = (TextView)view.findViewById(R.id.pp_name);
        PP_Expertise = (TextView)view.findViewById(R.id.pp_expertise);
        PP_Skills = (TextView)view.findViewById(R.id.pp_skillstext);
        PP_Aboutme = (TextView)view.findViewById(R.id.pp_aboutmetext);
        PP_Score = (TextView)view.findViewById(R.id.pp_score);
        PP_Works = (TextView)view.findViewById(R.id.pp_works);
        pp_picture = (ImageView)view.findViewById(R.id.pp_picture);

        usernameOfOffer = getActivity().getSharedPreferences("username", Context.MODE_PRIVATE);
        username = usernameOfOffer.getString("userName", "");

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject info = jsonObject.getJSONObject("info");

                            PP_Name.setText(info.getString("name"));
                            PP_Expertise.setText(info.getString("expertise"));
                            PP_Aboutme.setText(info.getString("portfolio"));
                            PP_Skills.setText(info.getString("skills"));
                            PP_Score.setText(info.getString("avrpoint"));
                            PP_Works.setText(info.getString("doneworks"));

                            JSONObject myImg = info.getJSONObject("myImg");

                            String URL_string = "https://raakar.ir";
                            String filePath = "";
                            filePath = myImg.getString("path");
                            filePath = filePath.substring(6,filePath.length());
                            URL_string += filePath;

                            Picasso.with(getActivity()).load(URL_string).into(pp_picture);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", username);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
                headers.put("token", token);
                return headers;
            }
        };
        Singleton.getInstance(getActivity()).addToRequestqueue(postRequest);

        return view;
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
            pDialog = new PrettyDialog(getActivity());
            pDialog.setCancelable(false);
            pDialog
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
                                    pDialog.dismiss();
                                }
                            }
                    )
                    .show();
        }

    }

}
