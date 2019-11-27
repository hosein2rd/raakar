package group.tamasha.rockaar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class RegisterActivity extends AppCompatActivity {

    TranslateAnimation slideup,slidedown;
    LinearLayout wheellinear, buttonlinear;
    TextView Expertise_Text, expertise;
    Button Expertise_Button, Register_Button;
    WheelPicker Expertise;
    ArrayList Expertises;
    String Expertise_String, token, MailValid, MobileValid;
    RequestQueue queue;
    String url = "https://raakar.ir/register", url_login = "https://raakar.ir/login";
    EditText Username , Password , Email , Mellicode , Phonenumber , Name;
    SharedPreferences UserName, PassWord, Token, ExpertisePref;
    AVLoadingIndicatorView av;
    ConstraintLayout constraint;
    int MailV, MobileV;
    PrettyDialog pDialog1;
    PrettyDialog pDialog2;
    PrettyDialog pDialog3;
    PrettyDialog pDialog4;
    PrettyDialog pDialog5;
    PrettyDialog pDialog6;
    PrettyDialog pDialog7;

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
        setContentView(R.layout.activity_register);

        Username = (EditText)findViewById(R.id.editun);
        Password = (EditText)findViewById(R.id.editpw);
        Email = (EditText)findViewById(R.id.editem);
        Name = (EditText)findViewById(R.id.editfn);
        Mellicode = (EditText)findViewById(R.id.editmc);
        Phonenumber = (EditText)findViewById(R.id.editpn);
        Expertise_Text = (TextView)findViewById(R.id.editex);
        buttonlinear = (LinearLayout)findViewById(R.id.wheelbutton);
        wheellinear = (LinearLayout)findViewById(R.id.wheelpicker);
        Register_Button = (Button)findViewById(R.id.regbutton);
        Expertise_Button = (Button)findViewById(R.id.regexbutton);
        Expertise = (WheelPicker)findViewById(R.id.main_wheel_center);
        av = (AVLoadingIndicatorView)findViewById(R.id.avi);
        expertise = (TextView)findViewById(R.id.editex);
        constraint = (ConstraintLayout)findViewById(R.id.constraint);

        expertise.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(expertise, InputMethodManager.SHOW_IMPLICIT);


        Expertise_Text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSoftKeyboard(v);
            }
        });

        slideup = new TranslateAnimation(0,0,400,0);
        slidedown = new TranslateAnimation(0,0,0,800);

        slideup.setDuration(400);
        slideup.setFillAfter(true);

        slidedown.setDuration(600);
        slidedown.setFillAfter(true);

        Expertises = new ArrayList<>();

        Expertises.add("--- کارفرما ---");
        Expertises.add("برنامه نویسی");
        Expertises.add("طراحی");
        Expertises.add("ترجمه و محتوا");
        Expertises.add("تحقیقات");
        Expertises.add("تجارت و حسابداری");

        for (int i=0; i<6; i++)
        {
            Expertises.add(i+1);
        }

        Expertise_Text.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view)
            {
//                rootView.startAnimation(slideup2);
                buttonlinear.setVisibility(View.VISIBLE);
                wheellinear.setVisibility(View.VISIBLE);

                Register_Button.setVisibility(View.GONE);

                Register_Button.setAlpha(0);

                buttonlinear.startAnimation(slideup);
                wheellinear.startAnimation(slideup);

                Expertises = new ArrayList<>();

                Expertises.add("--- کارفرما ---");
                Expertises.add("برنامه نویسی");
                Expertises.add("طراحی");
                Expertises.add("ترجمه و محتوا");
                Expertises.add("تحقیقات");
                Expertises.add("تجارت و حسابداری");

                if (Expertise.getCurrentItemPosition() == 0 ) {
                    Expertise_String = "--- کارفرما ---";
                }else if (Expertise.getCurrentItemPosition() == 1) {
                    Expertise_String = "برنامه نویسی";
                }else if (Expertise.getCurrentItemPosition() == 2) {
                    Expertise_String = "طراحی";
                }else if (Expertise.getCurrentItemPosition() == 3) {
                    Expertise_String = "ترجمه و محتوا";
                }else if (Expertise.getCurrentItemPosition() == 4) {
                    Expertise_String = "تحقیقات";
                }else if (Expertise.getCurrentItemPosition() == 5) {
                    Expertise_String = "تجارت و حسابداری";
                }

                Typeface IRANSansFaNum = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.TTF");

                Expertise.setData(Expertises);
                Expertise.setTypeface(IRANSansFaNum);

            }
        });

        Expertise_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Register_Button.setVisibility(View.VISIBLE);

                buttonlinear.startAnimation(slidedown);
                wheellinear.startAnimation(slidedown);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            buttonlinear.setVisibility(View.GONE);
                                            wheellinear.setVisibility(View.GONE);}
                                    }
                        , 550);

                final Handler handler1 = new Handler();
                handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Register_Button.setAlpha(0.5F);
                                            Register_Button.setAlpha(1F);}
                                    }
                        , 300);

                Expertises.add("--- کارفرما ---");
                Expertises.add("برنامه نویسی");
                Expertises.add("طراحی");
                Expertises.add("ترجمه و محتوا");
                Expertises.add("تحقیقات");
                Expertises.add("تجارت و حسابداری");

                if (Expertise.getCurrentItemPosition() == 0 ) {
                    Expertise_String = "--- کارفرما ---";
                }else if (Expertise.getCurrentItemPosition() == 1) {
                    Expertise_String = "برنامه نویسی";
                }else if (Expertise.getCurrentItemPosition() == 2) {
                    Expertise_String = "طراحی";
                }else if (Expertise.getCurrentItemPosition() == 3) {
                    Expertise_String = "ترجمه و محتوا";
                }else if (Expertise.getCurrentItemPosition() == 4) {
                    Expertise_String = "تحقیقات";
                }else if (Expertise.getCurrentItemPosition() == 5) {
                    Expertise_String = "تجارت و حسابداری";
                }

                Typeface IRANSansFaNum = Typeface.createFromAsset(getAssets(), "fonts/IRANSansMobile.TTF");

                Expertise.setData(Expertises);
                Expertise.setTypeface(IRANSansFaNum);

                Expertise_Text.setText( Expertise_String );
                Expertise_Text.setTextColor(Color.parseColor("#222222"));

            }
        });

        queue = Volley.newRequestQueue(this);
        Register_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                av.setVisibility(View.VISIBLE);
                constraint.setAlpha(0.5f);
                startAnim();
                if (Username.getText().toString().equals("") ||
                        Password.getText().toString().equals("") ||
                        Email.getText().toString().equals("") ||
                        Name.getText().toString().equals("") ||
                        Mellicode.getText().toString().equals("") ||
                        Phonenumber.getText().toString().equals("")){
                    stopAnim();
                    constraint.setAlpha(1f);
                    popupForErrorEmptyField();

                }else {

                    MailValid = Email.getText().toString();
                    MobileValid = Phonenumber.getText().toString();

                    if (isValidMail(MailValid))
                    { MailV = 1; } else { MailV = 0; }

                    if (isValidMobile(MobileValid))
                    { MobileV = 1; } else { MobileV = 0; }

                    if (MailV == 0 && MobileV == 0)
                    {
                        stopAnim();
                        constraint.setAlpha(1f);
                        popupForNotValidBoth();
                    }

                    if (MailV == 0 && MobileV == 1)
                    {
                        stopAnim();
                        constraint.setAlpha(1f);
                        popupForNotValidMail();
                    }

                    if (MailV == 1 && MobileV == 0)
                    {
                        stopAnim();
                        constraint.setAlpha(1f);
                        popupForNotValidMobile();
                    }

                    if (MailV == 1 && MobileV == 1)
                    {
                        popUpForAcceptRegister();
                    }
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        return true;
    }

    public void popupForSuccessfullRegister(){
//        new AwesomeSuccessDialog(this)
//                .setTitle("تبریک!")
//                .setMessage("ثبت نام شما با موفقیت انجام شد")
//                .setPositiveButtonText(getString(R.string.dialog_ok_button))
//                .setPositiveButtonClick(new Closure() {
//                    @Override
//                    public void exec() {
//                        Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
//                        startActivity(intent);
//                        StringRequest postRequest = new StringRequest(Request.Method.POST, url_login,
//                                new Response.Listener<String>()
//                                {
//                                    @Override
//                                    public void onResponse(String response) {
//                                        // response
//                                        Log.d("Response", response);
//                                        UserName = getSharedPreferences("user", MODE_PRIVATE);
//                                        SharedPreferences.Editor userEditor = UserName.edit();
//                                        userEditor.putString("user", Username.getText().toString());
//                                        userEditor.commit();
//
//                                        PassWord = getSharedPreferences("pass", MODE_PRIVATE);
//                                        SharedPreferences.Editor passEditor = PassWord.edit();
//                                        passEditor.putString("pass", Password.getText().toString());
//                                        passEditor.commit();
//
//                                        try {
//                                            JSONObject jsonObject = new JSONObject(response);
//                                            if (jsonObject.getString("success").equals("true")) {
//
//                                                token = jsonObject.getString("token");
//                                                Log.d("Token : ", token);
//
//                                                Token = getSharedPreferences( "token" , MODE_PRIVATE );
//                                                SharedPreferences.Editor editor = Token.edit();
//                                                editor.putString("token", token);
//                                                editor.apply();
//                                            }
//                                        }
//                                        catch (JSONException e) {
//                                        }
//                                    }
//                                },
//                                new Response.ErrorListener()
//                                {
//                                    @Override
//                                    public void onErrorResponse(VolleyError error) {
//                                        // error
//                                        Log.d("Error.Response", "Error");
//                                    }
//                                }
//                        ) {
//                            @Override
//                            protected Map<String, String> getParams()
//                            {
//                                Map<String, String> params = new HashMap<String, String>();
//                                params.put("username", Username.getText().toString());
//                                params.put("password", Password.getText().toString());
//
//                                return params;
//                            }
//                        };
//                        register(postRequest);
//                    }
//                })
//                .show();
        pDialog1 = new PrettyDialog(this);
        pDialog1.setCancelable(false);
        pDialog1
                .setTitle("تبریک!")
                .setMessage("ثبت نام شما با موفقیت انجام شد")
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
                                Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
                                startActivity(intent);
                                StringRequest postRequest = new StringRequest(Request.Method.POST, url_login,
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                Log.d("Response", response);
                                                UserName = getSharedPreferences("user", MODE_PRIVATE);
                                                SharedPreferences.Editor userEditor = UserName.edit();
                                                userEditor.putString("user", Username.getText().toString());
                                                userEditor.commit();

                                                PassWord = getSharedPreferences("pass", MODE_PRIVATE);
                                                SharedPreferences.Editor passEditor = PassWord.edit();
                                                passEditor.putString("pass", Password.getText().toString());
                                                passEditor.commit();

                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    if (jsonObject.getString("success").equals("true")) {

                                                        token = jsonObject.getString("token");
                                                        Log.d("Token : ", token);

                                                        Token = getSharedPreferences( "token" , MODE_PRIVATE );
                                                        SharedPreferences.Editor editor = Token.edit();
                                                        editor.putString("token", token);
                                                        editor.apply();
                                                    }
                                                }
                                                catch (JSONException e) {
                                                }
                                            }
                                        },
                                        new Response.ErrorListener()
                                        {
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
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("username", Username.getText().toString());
                                        params.put("password", Password.getText().toString());

                                        return params;
                                    }
                                };
                                register(postRequest);
                            }
                        }
                )
                .show();
    }

    public void popupForUsernameExist(){
//        new AwesomeWarningDialog(this)
//                .setTitle("خطا!")
//                .setMessage("نام کاربری که وارد کرده اید قبلا استفاده شده است")
//                .setColoredCircle(R.color.dialogErrorBackgroundColor)
//                .setDialogIconAndColor(R.drawable.ic_dialog_info ,R.color.white)
//                .setButtonBackgroundColor(R.color.dialogErrorBackgroundColor)
//                .setButtonText(getString(R.string.dialog_ok_button))
//                .setButtonTextColor(R.color.white)
//                .setWarningButtonClick(new Closure() {
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
                .setMessage("نام کاربری که وارد کرده اید قبلا استفاده شده است")
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
        pDialog3 = new PrettyDialog(this);
        pDialog3.setCancelable(false);
        pDialog3
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
                                pDialog3.dismiss();
                            }
                        }
                )
                .show();
    }

    public void register(StringRequest postRequest){
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

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private boolean isValidMail(String email) {
        boolean check;
        Pattern p;
        Matcher m;

        String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        p = Pattern.compile(EMAIL_STRING);

        m = p.matcher(email);
        check = m.matches();

        return check;
    }

    private boolean isValidMobile(String phone) {
        boolean check;
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            if(phone.length() < 6 || phone.length() > 13) {
                // if(phone.length() != 10) {
                check = false;
            } else {
                check = true;
            }
        } else {
            check=false;
        }
        return check;
    }

    public void popupForNotValidMail(){
//        new AwesomeNoticeDialog(this)
//                .setTitle("خطا!")
//                .setMessage("ایمیل وارد شده معتبر نیست")
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
        pDialog4 = new PrettyDialog(this);
        pDialog4.setCancelable(false);
        pDialog4
                .setTitle("خطا!")
                .setMessage("ایمیل وارد شده معتبر نیست")
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
                                pDialog4.dismiss();
                            }
                        }
                )
                .show();
    }

    public void popupForNotValidMobile(){
//        new AwesomeNoticeDialog(this)
//                .setTitle("خطا!")
//                .setMessage("شماره همراه وارد شده معتبر نیست")
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
        pDialog5 = new PrettyDialog(this);
        pDialog5.setCancelable(false);
        pDialog5
                .setTitle("خطا!")
                .setMessage("شماره همراه وارد شده معتبر نیست")
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
                                pDialog5.dismiss();
                            }
                        }
                )
                .show();
    }

    public void popupForNotValidBoth(){
//        new AwesomeNoticeDialog(this)
//                .setTitle("خطا!")
//                .setMessage("ایمیل و شماره همراه وارد شده معتبر نیست")
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
        pDialog6 = new PrettyDialog(this);
        pDialog6.setCancelable(false);
        pDialog6
                .setTitle("خطا!")
                .setMessage("ایمیل و شماره همراه وارد شده معتبر نیست")
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
                                pDialog6.dismiss();
                            }
                        }
                )
                .show();
    }

    public void popUpForAcceptRegister(){
        pDialog7 = new PrettyDialog(this);
        pDialog7.setCancelable(false);
        pDialog7
                .setTitle("توجه!")
                .setMessage("راکار متعهد میشود که فضای امنی برای کاربران خود فراهم کند و در صورت نا امن شدن پلتفرم و یا احتمال خطر آن مسوولیت آن را بر عهده بگیرد. \n" +
                        " راکار این حق را دارد که پروژه ای را لغو و یا حذف کند. همچنین حق تایید پروژه ها نیز بر عهده برنامه راکار است.\n" +
                        "کارفرما متعهد میشود که با اشتراک گذاری پروژه 10% از مبلغ کل قرار داد را به عنوان پیش پرداخت در نظر بگیرد و در صورت لغو آن تمام پول پیش پرداخت به او باز گردد.")
                .setIcon(
                        R.drawable.pdlg_icon_info,     // icon resource
                        R.color.RakaarColor, null)
                .addButton(
                        "شرایط را می پذیرم",
                        R.color.pdlg_color_white,
                        R.color.RakaarColor,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog7.dismiss();
                                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                Log.d("Response", response);
                                                popupForSuccessfullRegister();
                                                stopAnim();
                                                constraint.setAlpha(1f);
                                                UserName = getSharedPreferences("user", MODE_PRIVATE);
                                                SharedPreferences.Editor userEditor = UserName.edit();
                                                userEditor.putString("user", Username.getText().toString());
                                                userEditor.commit();

                                                PassWord = getSharedPreferences("pass", MODE_PRIVATE);
                                                SharedPreferences.Editor passEditor = PassWord.edit();
                                                passEditor.putString("pass", Password.getText().toString());
                                                passEditor.commit();

                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    if (jsonObject.getString("success").equals("true")) {

                                                        token = jsonObject.getString("token");
                                                        Log.d("Token : ", token);

                                                        Token = getSharedPreferences( "token" , MODE_PRIVATE );
                                                        SharedPreferences.Editor editor = Token.edit();
                                                        editor.putString("token", token);
                                                        editor.apply();
                                                    }
                                                }
                                                catch (JSONException e) {
                                                }
                                            }
                                        },
                                        new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // error
                                                Log.d("Error.Response", "Error");
                                                stopAnim();
                                                constraint.setAlpha(1f);
                                                switch(error.networkResponse.statusCode){
                                                    case 303:
                                                        popupForUsernameExist();
                                                        break;
                                                }
                                            }
                                        }
                                ) {
                                    @Override
                                    protected Map<String, String> getParams()
                                    {
                                        Map<String, String> params = new HashMap<String, String>();
                                        params.put("username", Username.getText().toString());
                                        params.put("password", Password.getText().toString());
                                        params.put("email", Email.getText().toString());
                                        params.put("name", Name.getText().toString());
                                        params.put("mellicode", Mellicode.getText().toString());
                                        params.put("phonenumber", Phonenumber.getText().toString());
                                        params.put("expertise", expertise.getText().toString());
                                        //Ezafe shoddd :-P
                                        params.put("workscount", "0");
                                        return params;
                                    }
                                };
                                register(postRequest);
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
                                pDialog7.dismiss();
                                stopAnim();
                                constraint.setAlpha(1f);
                            }
                        }
                )
                .show();
    }

}
