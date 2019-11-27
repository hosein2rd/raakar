package group.tamasha.rockaar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RatingActivity extends AppCompatActivity {

    PrettyDialog pDialog1;
    PrettyDialog pDialog2;
    PrettyDialog pDialog3;
    ImageView Star1, Star2, Star3, Star4, Star5, Line1, Line2, Line3, Line4, Line5;
    TextView Text1, Text2, Text3, Text4, Text5;
    Button RatingButton;
    int Rate = 0;
    SharedPreferences LaunchCounter, Token;
    SharedPreferences.Editor LaunchCounterEditor;
    int Counter;
    String url = "https://raakar.ir/rate", token;
    String projectOwnerUserName, projectIndex, projectOwerName;
    TextView rating_name, rating_expertise;

    public void setupFont()
    {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSansMobile.TTF")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFont();
        setContentView(R.layout.activity_rating);

        popupForCheckInternet();

//        final Bundle bundle = getArguments();

        final SharedPreferences category,creatorName,projectOwnerUsername,projectIndex;

        category = getSharedPreferences("category", Context.MODE_PRIVATE);
        creatorName = getSharedPreferences("creatorName", Context.MODE_PRIVATE);
        projectOwnerUsername = getSharedPreferences("creatorUsername", Context.MODE_PRIVATE);
        projectIndex = getSharedPreferences("projectIndex", Context.MODE_PRIVATE);

        Log.e("creatorUsername", projectOwnerUsername.getString("creatorUsername", ""));
        Log.e("projectIndex", projectIndex.getString("projectIndex", ""));

        rating_name = (TextView)findViewById(R.id.rating_name);
        rating_expertise = (TextView)findViewById(R.id.rating_expertise);

        rating_name.setText(creatorName.getString("creatorName", ""));
        rating_expertise.setText(category.getString("category", ""));

        Token = getSharedPreferences("token", Context.MODE_PRIVATE);

        token = Token.getString("token", null);

        Star1 = (ImageView)findViewById(R.id.rating_star1);
        Star2 = (ImageView)findViewById(R.id.rating_star2);
        Star3 = (ImageView)findViewById(R.id.rating_star3);
        Star4 = (ImageView)findViewById(R.id.rating_star4);
        Star5 = (ImageView)findViewById(R.id.rating_star5);
        Line1 = (ImageView)findViewById(R.id.rating_line1);
        Line2 = (ImageView)findViewById(R.id.rating_line2);
        Line3 = (ImageView)findViewById(R.id.rating_line3);
        Line4 = (ImageView)findViewById(R.id.rating_line4);
        Line5 = (ImageView)findViewById(R.id.rating_line5);
        Text1 = (TextView)findViewById(R.id.rating_text1);
        Text2 = (TextView)findViewById(R.id.rating_text2);
        Text3 = (TextView)findViewById(R.id.rating_text3);
        Text4 = (TextView)findViewById(R.id.rating_text4);
        Text5 = (TextView)findViewById(R.id.rating_text5);
        RatingButton = (Button)findViewById(R.id.rating_button);

        Star1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Star1.setBackgroundResource(R.drawable.stary);
                Star2.setBackgroundResource(R.drawable.starg);
                Star3.setBackgroundResource(R.drawable.starg);
                Star4.setBackgroundResource(R.drawable.starg);
                Star5.setBackgroundResource(R.drawable.starg);
                Line1.setBackgroundResource(R.drawable.ratingline_yu);
                Line2.setBackgroundResource(R.drawable.ratingline_gd);
                Line3.setBackgroundResource(R.drawable.ratingline_gu);
                Line4.setBackgroundResource(R.drawable.ratingline_gd);
                Line5.setBackgroundResource(R.drawable.ratingline_gu);
                Text1.setTextColor(Color.parseColor("#e5bc17"));
                Text2.setTextColor(Color.parseColor("#999999"));
                Text3.setTextColor(Color.parseColor("#999999"));
                Text4.setTextColor(Color.parseColor("#999999"));
                Text5.setTextColor(Color.parseColor("#999999"));
                Rate = 1;
                Log.e("Rate", String.valueOf(Rate));
            }
        });

        Star2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Star1.setBackgroundResource(R.drawable.stary);
                Star2.setBackgroundResource(R.drawable.stary);
                Star3.setBackgroundResource(R.drawable.starg);
                Star4.setBackgroundResource(R.drawable.starg);
                Star5.setBackgroundResource(R.drawable.starg);
                Line1.setBackgroundResource(R.drawable.ratingline_yu);
                Line2.setBackgroundResource(R.drawable.ratingline_yd);
                Line3.setBackgroundResource(R.drawable.ratingline_gu);
                Line4.setBackgroundResource(R.drawable.ratingline_gd);
                Line5.setBackgroundResource(R.drawable.ratingline_gu);
                Text1.setTextColor(Color.parseColor("#e5bc17"));
                Text2.setTextColor(Color.parseColor("#e5bc17"));
                Text3.setTextColor(Color.parseColor("#999999"));
                Text4.setTextColor(Color.parseColor("#999999"));
                Text5.setTextColor(Color.parseColor("#999999"));
                Rate = 2;
                Log.e("Rate", String.valueOf(Rate));
            }
        });

        Star3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Star1.setBackgroundResource(R.drawable.stary);
                Star2.setBackgroundResource(R.drawable.stary);
                Star3.setBackgroundResource(R.drawable.stary);
                Star4.setBackgroundResource(R.drawable.starg);
                Star5.setBackgroundResource(R.drawable.starg);
                Line1.setBackgroundResource(R.drawable.ratingline_yu);
                Line2.setBackgroundResource(R.drawable.ratingline_yd);
                Line3.setBackgroundResource(R.drawable.ratingline_yu);
                Line4.setBackgroundResource(R.drawable.ratingline_gd);
                Line5.setBackgroundResource(R.drawable.ratingline_gu);
                Text1.setTextColor(Color.parseColor("#e5bc17"));
                Text2.setTextColor(Color.parseColor("#e5bc17"));
                Text3.setTextColor(Color.parseColor("#e5bc17"));
                Text4.setTextColor(Color.parseColor("#999999"));
                Text5.setTextColor(Color.parseColor("#999999"));
                Rate = 3;
                Log.e("Rate", String.valueOf(Rate));
            }
        });

        Star4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Star1.setBackgroundResource(R.drawable.stary);
                Star2.setBackgroundResource(R.drawable.stary);
                Star3.setBackgroundResource(R.drawable.stary);
                Star4.setBackgroundResource(R.drawable.stary);
                Star5.setBackgroundResource(R.drawable.starg);
                Line1.setBackgroundResource(R.drawable.ratingline_yu);
                Line2.setBackgroundResource(R.drawable.ratingline_yd);
                Line3.setBackgroundResource(R.drawable.ratingline_yu);
                Line4.setBackgroundResource(R.drawable.ratingline_yd);
                Line5.setBackgroundResource(R.drawable.ratingline_gu);
                Text1.setTextColor(Color.parseColor("#e5bc17"));
                Text2.setTextColor(Color.parseColor("#e5bc17"));
                Text3.setTextColor(Color.parseColor("#e5bc17"));
                Text4.setTextColor(Color.parseColor("#e5bc17"));
                Text5.setTextColor(Color.parseColor("#999999"));
                Rate = 4;
                Log.e("Rate", String.valueOf(Rate));

            }
        });

        Star5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Star1.setBackgroundResource(R.drawable.stary);
                Star2.setBackgroundResource(R.drawable.stary);
                Star3.setBackgroundResource(R.drawable.stary);
                Star4.setBackgroundResource(R.drawable.stary);
                Star5.setBackgroundResource(R.drawable.stary);
                Line1.setBackgroundResource(R.drawable.ratingline_yu);
                Line2.setBackgroundResource(R.drawable.ratingline_yd);
                Line3.setBackgroundResource(R.drawable.ratingline_yu);
                Line4.setBackgroundResource(R.drawable.ratingline_yd);
                Line5.setBackgroundResource(R.drawable.ratingline_yu);
                Text1.setTextColor(Color.parseColor("#e5bc17"));
                Text2.setTextColor(Color.parseColor("#e5bc17"));
                Text3.setTextColor(Color.parseColor("#e5bc17"));
                Text4.setTextColor(Color.parseColor("#e5bc17"));
                Text5.setTextColor(Color.parseColor("#e5bc17"));
                Rate = 5;
                Log.e("Rate", String.valueOf(Rate));
            }
        });

        LaunchCounter = getApplicationContext().getSharedPreferences("launch", Context.MODE_PRIVATE);
        LaunchCounterEditor = LaunchCounter.edit();
        Counter = LaunchCounter.getInt("launch",0);
        RatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Rate == 0) {
                    popupForEmptyRating();
                } else {

                    if (Counter == 0) {
                        Intent intent = new Intent(RatingActivity.this, DashboardActivity.class);
                        startActivity(intent);
                        Counter = 0;
                    } else if (Counter > 0) {

//                        RequestBody pointRequestBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(Rate).trim());
//                        RequestBody offererRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectOwnerUsername.getString("creatorUsername", "").trim());
//                        RequestBody projectIndexRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectIndex.getString("projectIndex", "").trim());
//
//                        Client client = ServiceGenerator.createService(Client.class);
//
//                        Call<AddProjectResponse> call = client.rate(
//                                token,
//                                pointRequestBody,
//                                offererRequestBody,
//                                projectIndexRequestBody
//                        );
//
//                        call.enqueue(new Callback<AddProjectResponse>() {
//                            @Override
//                            public void onResponse(Call<AddProjectResponse> call, retrofit2.Response<AddProjectResponse> response) {
//                                Log.e("response code", String.valueOf(response.code()));
//                                if (response.isSuccessful()){
//                                    popupForSuccessfullRating();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<AddProjectResponse> call, Throwable t) {
//                                Log.e("error", t.getMessage());
//                            }
//                        });

                        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // response
                                        Log.e("Response", response);
                                        popupForSuccessfullRating();
                                        Counter = 0;
                                        LaunchCounter.edit().putInt("launch", Counter).apply();
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // error
                                        Log.e("Error.Response", error.toString());
                                    }
                                }
                        ) {
                            @Override
                            protected Map<String, String> getParams() {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("point", Rate + "");
                                params.put("offerer", projectOwnerUsername.getString("creatorUsername", ""));
                                params.put("projectIndex", projectIndex.getString("projectIndex", ""));

                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("token", token);
                                return headers;
                            }
                        };
                        rate(postRequest);
                    }
                }
            }});
        }


    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
        }else{
            pDialog1 = new PrettyDialog(this);
            pDialog1.setCancelable(false);
            pDialog1
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
                                    pDialog1.dismiss();
                                }
                            }
                    )
                    .show();
        }

    }

    public void popupForSuccessfullRating(){
        pDialog2 = new PrettyDialog(this);
        pDialog2.setCancelable(false);
        pDialog2
                .setTitle("تبریک!")
                .setMessage("ثبت امتیاز با موفقیت انجام شد")
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
                                Intent intent = new Intent(RatingActivity.this, DashboardActivity.class);
                                startActivity(intent);
                            }
                        }
                )
                .show();
    }

    public void popupForEmptyRating(){
        pDialog3 = new PrettyDialog(this);
        pDialog3.setCancelable(false);
        pDialog3
                .setTitle("خطا!")
                .setMessage("لطفا امتیاز مورد نظر را ثبت نمایید")
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

    public void rate(StringRequest postRequest){
        Singleton.getInstance(this).addToRequestqueue(postRequest);
    }
}
