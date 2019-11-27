package group.tamasha.rockaar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignActivity extends AppCompatActivity {

    RequestQueue queue;
    String url = "https://raakar.ir/login", token, user, pass;
    EditText Username , Password;
    Button Login;
    SharedPreferences Token, UserName, PassWord;
    AVLoadingIndicatorView av;
    ConstraintLayout constrant;
    PrettyDialog pDialog1;
    PrettyDialog pDialog2;
    PrettyDialog pDialog3;
    Button forgetbuton;

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
        setContentView(R.layout.activity_sign);

        av = (AVLoadingIndicatorView)findViewById(R.id.avi);
        Username = (EditText)findViewById(R.id.emailtxt);
        Password = (EditText)findViewById(R.id.passwordtxt);

        forgetbuton = (Button)findViewById(R.id.forgetbuton);

        user = Username.getText().toString();
        pass = Password.getText().toString();

        Login = (Button)findViewById(R.id.signinbutton);

        queue = Volley.newRequestQueue(this);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constrant = (ConstraintLayout)findViewById(R.id.constrant);
                constrant.setAlpha(0.5f);
                av.setVisibility(View.VISIBLE);
                startAnim();
                av.setAlpha(1f);
                if (Username.getText().toString().equals("") || Password.getText().toString().equals("")){
                    stopAnim();
                    constrant.setAlpha(1f);
                    popupForErrorEmptyField();
                }else {
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    // response
                                    Log.d("Response", response);

                                    UserName = getSharedPreferences("user", MODE_PRIVATE);
                                    PassWord = getSharedPreferences("pass", MODE_PRIVATE);

                                    SharedPreferences.Editor userEditor = UserName.edit();
                                    userEditor.putString("user", Username.getText().toString());
                                    userEditor.commit();

                                    SharedPreferences.Editor passEditor = PassWord.edit();
                                    passEditor.putString("pass", Password.getText().toString());
                                    passEditor.commit();

                                    try {
                                        JSONObject jsonObject = new JSONObject(response);
                                        if (jsonObject.getString("success").equals("true")) {
                                            //refrence_code = jsonObject.getString("referenceCode");
//                                            if(user != null && pass != null){
                                            token = jsonObject.getString("token");
                                            Log.d("Token : ", token);

                                            Token = getSharedPreferences( "token" , MODE_PRIVATE );
                                            SharedPreferences.Editor editor = Token.edit();
                                            editor.putString("token", token);
                                            editor.apply();
                                            Intent intent = new Intent(SignActivity.this, DashboardActivity.class);
                                            startActivity(intent);
                                            stopAnim();
//                                            }
                                        }
                                    }
                                    catch (JSONException e) {
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // error
                                    Log.d("Error.Response", "Error");
                                    stopAnim();
                                    constrant.setAlpha(1f);
                                    popupForErrorLogin();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("username", Username.getText().toString());
                            params.put("password", Password.getText().toString());

                            return params;
                        }
                    };
                    login(postRequest);
                }
            }
        });

        forgetbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpForForgottenPassword();
            }
        });

        final Handler ha=new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);

                                UserName = getSharedPreferences("user", MODE_PRIVATE);
                                PassWord = getSharedPreferences("pass", MODE_PRIVATE);

                                SharedPreferences.Editor userEditor = UserName.edit();
                                userEditor.putString("user", Username.getText().toString());
                                userEditor.commit();

                                SharedPreferences.Editor passEditor = PassWord.edit();
                                passEditor.putString("pass", Password.getText().toString());
                                passEditor.commit();

                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getString("success").equals("true")) {
                                        //refrence_code = jsonObject.getString("referenceCode");
//                                            if(user != null && pass != null){
                                        token = jsonObject.getString("token");
                                        Log.d("Token : ", token);

                                        Token = getSharedPreferences( "token" , MODE_PRIVATE );
                                        SharedPreferences.Editor editor = Token.edit();
                                        editor.putString("token", token);
                                        editor.apply();
                                        Intent intent = new Intent(SignActivity.this, DashboardActivity.class);
                                        startActivity(intent);
                                        stopAnim();
//                                            }
                                    }
                                }
                                catch (JSONException e) {
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // error
                                Log.d("Error.Response", "Error");
                                stopAnim();
                                constrant.setAlpha(1f);
                                popupForErrorLogin();
                            }
                        }
                ) {
                    @Override
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<String, String>();
                        params.put("username", Username.getText().toString());
                        params.put("password", Password.getText().toString());

                        return params;
                    }
                };
                login(postRequest);

                ha.postDelayed(this, 1800000);
            }
        }, 1800000);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void popupForErrorLogin(){
//        new AwesomeErrorDialog(this)
//                .setTitle("خطا!")
//                .setMessage("نام کاربری یا رمز عبور اشتباه است")
//                .setColoredCircle(R.color.dialogErrorBackgroundColor)
//                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
//                .setCancelable(true).setButtonText(getString(R.string.dialog_ok_button))
//                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
//                .setButtonText(getString(R.string.dialog_ok_button))
//                .setErrorButtonClick(new Closure() {
//                    @Override
//                    public void exec() {
//                        // click
//                    }
//                })
//                .show();
        pDialog1 = new PrettyDialog(this);
        pDialog1.setCancelable(false);
        pDialog1
                .setTitle("خطا!")
                .setMessage("نام کاربری یا رمز عبور اشتباه است")
                .setIcon(
                        R.drawable.pdlg_icon_info,     // icon resource
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

    public void popupForErrorEmptyField(){
//        new AwesomeNoticeDialog(this)
//                .setTitle("خطا!")
//                .setMessage("لطفا همه فیلد ها را پر کنید")
//                .setColoredCircle(R.color.dialogErrorBackgroundColor)
//                .setDialogIconAndColor(R.drawable.ic_dialog_info, R.color.white)
//                .setCancelable(true)
//                .setButtonText(getString(R.string.dialog_ok_button))
//                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
//                .setButtonText(getString(R.string.dialog_ok_button))
//                .setNoticeButtonClick(new Closure() {
//                    @Override
//                    public void exec() {
//                        // click
//                    }
//                })
//                .show();
        pDialog2 = new PrettyDialog(this);
        pDialog2.setCancelable(false);
        pDialog2
                .setTitle("خطا!")
                .setMessage("لطفا همه فیلد ها را پر کنید")
                .setIcon(
                        R.drawable.pdlg_icon_info,     // icon resource
                        R.color.pdlg_color_red, null)
                .addButton(
                        "باشه",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog2.dismiss();
                            }
                        }
                )
                .show();
    }

    public void login(StringRequest postRequest){
        Singleton.getInstance(this).addToRequestqueue(postRequest);
    }

    public void startAnim(){
        av.smoothToShow();
        // or avi.smoothToShow();
    }

    public void stopAnim(){
        av.smoothToHide();
        // or avi.smoothToHide();
    }

    public void popUpForForgottenPassword(){
        pDialog3 = new PrettyDialog(this);
        pDialog3.setCancelable(false);
        pDialog3
                .setTitle("توجه!")
                .setMessage("در صورتی که رمز خود را فراموش کرده اید با پشتیبانی تماس بگیرید \n شماره پشتیبانی: 09016081024")
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
                                pDialog3.dismiss();
                            }
                        }
                )
                .show();
    }
}


