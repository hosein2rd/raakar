package group.tamasha.rockaar;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import group.tamasha.rockaar.adapter.ViewPagerAdapter;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import me.relex.circleindicator.CircleIndicator;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


public class MainActivity extends AppCompatActivity {

    // Declare Variables
    ViewPager viewPager;
    PagerAdapter adapter;
    String[] name;
    String[] describe;
    int[] back;
    Button buttonE;
    Button buttonS;
    RequestQueue queue;
    SharedPreferences PassWord, UserName, Token;
    String user, pass, token, url = "https://raakar.ir/login";
    StringRequest postRequest;
    private Tracker mTracker;
    PrettyDialog pDialog;

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
        setContentView(R.layout.activity_main);

        //Analytics Codes
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName((String)Build.MANUFACTURER + " " + Build.PRODUCT);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(1);

        buttonE=(Button) findViewById(R.id.btnE);
        buttonS=(Button) findViewById(R.id.btnS);

        UserName = getSharedPreferences("user", MODE_PRIVATE);
        PassWord = getSharedPreferences("pass", MODE_PRIVATE);
        user = UserName.getString("user", null);
        pass = PassWord.getString("pass", null);

        if (user != null && pass != null){
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);
            Login();
        }

        // Generate sample data
        name = new String[] { " ", " ", " " };

        describe = new String[] {
                "مهارت خاصی داری و دنبال کار پاره وقت میگردی؟ با اپلیکیشن راکار خیلی راحت میتونی با مهارتت کار پاره وقت پیدا کنی!",
                "فقط کافیه مهارتات رو ثبت کنی و بعدش رایگان مشغول به کار بشی!",
                "پروژت رو از طریق راکار تحویل کارفرما میدی و هزینش رو دریافت میکنی!"
        };

        back = new int[] { R.drawable.w1, R.drawable.w2,
                R.drawable.w3 };

        // Locate the ViewPager in activity_main.xml
        viewPager = (ViewPager) findViewById(R.id.pager);
        // Pass results to ViewPagerAdapter Class
        adapter = new ViewPagerAdapter(MainActivity.this, name, describe, back);
        // Binds the Adapter to the ViewPager
        viewPager.setAdapter(adapter);

        CircleIndicator indicator=(CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        //        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            @Override
//            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
//
//            @Override
//            public void onPageSelected(int i) {
//                currentpage=1;
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//                if(i==ViewPager.SCROLL_STATE_IDLE)
//                {
//
//                    if(currentpage==0)
//                    {
//                        buttonE.setBackgroundResource(R.drawable.enterbuttonb);
//                        buttonS.setBackgroundResource(R.drawable.signupbuttonb);
//                        buttonS.setTextColor(Color.argb(255,68,180,194));
//                    }
//                    else if(currentpage==1)
//                    {
//                        buttonE.setBackgroundResource(R.drawable.enterbuttonr);
//                        buttonS.setBackgroundResource(R.drawable.signupbuttonr);
//                        buttonS.setTextColor(Color.argb(255,139,21,21));
//                    }
//                    else if(currentpage==2)
//                    {
//                        buttonE.setBackgroundResource(R.drawable.enterbuttony);
//                        buttonS.setBackgroundResource(R.drawable.signupbuttony);
//                        buttonS.setTextColor(Color.argb(255,175,135,26));
//                    }
//
//                    int pagecount=name.length;
//                    if(currentpage==0)
//                    {
//                        viewPager.setCurrentItem(pagecount-1,false);
//                    }
//                    else if (currentpage==pagecount-1)
//                    {
//                        viewPager.setCurrentItem(0,false);
//                    }
//                }
//            }
//        });
//
//        final Handler handler=new Handler();
//        final Runnable update=new Runnable() {
//            @Override
//            public void run() {
//                if(currentpage==numpages)
//                {
//                    currentpage=0;
//                }
//                viewPager.setCurrentItem(currentpage++,true);
//            }
//        };
//        Timer swipe=new Timer();
//        swipe.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(update);
//            }
//        },5000,5000);

    }

    public void opensign(View view) {
        Intent intent = new Intent(MainActivity.this, SignActivity.class);
        startActivity(intent);
    }

    public void openregister(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void popupForLoginError(){
//        new AwesomeErrorDialog(this)
//                .setTitle("خطا در ورود!")
//                .setMessage(" لطفا دوباره وارد شوید")
//                .setColoredCircle(R.color.dialogErrorBackgroundColor)
//                .setDialogIconAndColor(R.drawable.ic_dialog_error, R.color.white)
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
        pDialog = new PrettyDialog(this);
        pDialog
                .setTitle("خطا در ورود!")
                .setMessage(" لطفا دوباره وارد شوید")
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
                                pDialog.dismiss();
                            }
                        }
                )
                .show();
    }

    public void Login(){

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
//                                Toast.makeText( SignActivity.this, response,
//                                        Toast.LENGTH_SHORT).show();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("success").equals("true")) {
                                //refrence_code = jsonObject.getString("referenceCode");
                                token = jsonObject.getString("token");
                                Log.d("Token : ", token);

                                Token = getSharedPreferences( "token" , MODE_PRIVATE );
                                SharedPreferences.Editor editor = Token.edit();
                                editor.clear().commit();
                                editor.putString("token", token);
                                editor.commit();
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
                        popupForLoginError();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username", user);
                params.put("password", pass);

                return params;
            }
        };
        Singleton.getInstance(this).addToRequestqueue(postRequest);
    }
}
