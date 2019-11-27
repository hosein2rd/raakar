package group.tamasha.rockaar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.rahatarmanahmed.cpv.CircularProgressView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends AppCompatActivity {

    Button Retry;
    TextView Hint;
    CircularProgressView progressView;

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
        setContentView(R.layout.activity_splash);
        Retry = (Button) findViewById(R.id.splash_button);
        Hint = (TextView) findViewById(R.id.splash_text);
        Retry.setVisibility(View.GONE);
        Hint.setVisibility(View.GONE);
        progressView = (CircularProgressView) findViewById(R.id.progress_view);
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
            progressView.startAnimation();
            Thread myThread = new Thread(){
                @Override
                public void run() {
                    try {
                        sleep(3000);
                        Intent intn = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intn);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            myThread.start();
        }else{
            progressView.setVisibility(View.GONE);
            Retry.setVisibility(View.VISIBLE);
            Hint.setVisibility(View.VISIBLE);
        }
        Retry.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                progressView.setVisibility(View.VISIBLE);
                Retry.setVisibility(View.GONE);
                Hint.setVisibility(View.GONE);
                if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
                    progressView.startAnimation();
                    Thread myThread = new Thread(){
                        @Override
                        public void run() {
                            try {
                                sleep(3000);
                                Intent intn = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intn);
                                finish();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    myThread.start();
                }else{
                    progressView.setVisibility(View.GONE);
                    Retry.setVisibility(View.VISIBLE);
                    Hint.setVisibility(View.VISIBLE);
                }
            }});
    }
}
