package group.tamasha.rockaar;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.android.vending.billing.IInAppBillingService;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import group.tamasha.rockaar.util.IabHelper;
import group.tamasha.rockaar.util.IabResult;
import group.tamasha.rockaar.util.Inventory;
import group.tamasha.rockaar.util.Purchase;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DashboardActivity extends AppCompatActivity {

    Fragment projects = new ProjectsFragment();
    Fragment credit = new CreditFragment();
    Fragment dashboard = new DashboardFragment();
    Fragment messages = new MessagesFragment();
    Fragment profile = new ProfileFragment();
    private Tracker mTracker;
    SharedPreferences LaunchCounter;
    SharedPreferences.Editor LaunchCounterEditor;
    int Counter;
    PrettyDialog pDialog, pDialog3;
    // Debug tag, for logging
    static final String TAG = "______________________";

    static final String SKU_BRONZE = "sku_bronze";
    static final String SKU_SILVER = "sku_silver";
    static final String SKU_GOLD = "sku_gold";
    IabHelper mHelper;
    Inventory inventory1;
    static final int RC_REQUEST = 3;

    String  url_raiseOffer = "https://raakar.ir/simpleRaiseOffer",
            token;

    // SKUs for our products: the premium upgrade (non-consumable)

    // Does the user have the premium upgrade?

    // (arbitrary) request code for the purchase flow
    IInAppBillingService mService;


    public void switchprojects() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mycontainer, projects)
                //.addToBackStack("Back")
                .commit();
    }

    public void switchcredit() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mycontainer, credit)
                //.addToBackStack("Back")
                .commit();
    }

    public void switchdashboard() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mycontainer,  dashboard)
                //.addToBackStack("Back")
                .commit();
    }

    public void switchmessages() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mycontainer, messages)
                //.addToBackStack("Back")
                .commit();
    }

    public void switchprofile() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.mycontainer, profile)
                //.addToBackStack("Back")
                .commit();

    }

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
        setContentView(R.layout.activity_dashboard);
        switchdashboard();

        String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC8cbvYE2VTVs/7Jj37yDmT+ybhtOhSenOuagFtqOSsKnjGVRdnVaIRW66DTE36/lFvc4aT9gerbq2hIsE0WmAnPWv4Ap/rJXOLH2iAEjNSJBbiVNyafyIy2O+dqSjH8gcpP/i71BsgKurvC6yxyzLim5RMpaTnbNmi13nWBO9XZ+9EDzP9p/RR9QRdd2X2eoGZ+5SdbXgnT6bbyNmhbldCqPEkyQc+QPU88c+TupsCAwEAAQ==";

        mHelper = new IabHelper(this, base64EncodedPublicKey);


        Log.e(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.e(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.e(TAG, "Problem setting up In-app Billing: " + result);

                }
                // Hooray, IAB is fully set up!
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });

        LaunchCounter = getApplicationContext().getSharedPreferences("launch", MODE_PRIVATE);
        LaunchCounterEditor = LaunchCounter.edit();
        Counter = LaunchCounter.getInt("launch",0);
        if (Counter > 0)
        {
            Intent intent = new Intent(DashboardActivity.this, RatingActivity.class);
            startActivity(intent);
        }

        //Analytics Codes
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        mTracker.setScreenName((String) Build.MANUFACTURER + " " + Build.PRODUCT);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        GoogleAnalytics.getInstance(this).setLocalDispatchPeriod(1);


        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);

        bottomNavigationView.setSelectedItemId(R.id.navigation_dashboard);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_projects:
                        switchprojects();
                        break;


                    case R.id.navigation_credit:
                        switchcredit();
                        break;

                    case R.id.navigation_dashboard:
                        switchdashboard();
                        break;

                    case R.id.navigation_messages:
                        switchmessages();
                        break;

                    case R.id.navigation_profile:
                        switchprofile();
                        break;
                }
                return true;

            }
        });

        SharedPreferences Token = getSharedPreferences("token", MODE_PRIVATE);
        token = Token.getString("token", "");

    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.e("purche", "_________________________now here in purche");
            if (result.isFailure()) {
                Log.e("result", "___________________Error purchasing: " + result);
                return;
            }
            else if (purchase.getSku().equals(SKU_GOLD)) {
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
            else if (purchase.getSku().equals(SKU_SILVER)){
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
            else if (purchase.getSku().equals(SKU_BRONZE)){
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        }
    };

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            inventory1 = inventory;
            Log.e(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                Log.e(TAG, "Failed to query inventory: " + result);
                return;
            }
            else {
                Log.e(TAG, "Query inventory was successful.");
                // does the user have the premium upgrade?

                if (inventory.hasPurchase(SKU_GOLD)){
//                    mHelper.launchPurchaseFlow(getActivity(), SKU_FREE, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
                    mHelper.consumeAsync(inventory.getPurchase(SKU_GOLD),
                            mConsumeFinishedListener);
                }
                else if (inventory.hasPurchase(SKU_SILVER)){
                    mHelper.consumeAsync(inventory.getPurchase(SKU_SILVER),
                            mConsumeFinishedListener);
                }
                else if (inventory.hasPurchase(SKU_BRONZE)){
                    mHelper.consumeAsync(inventory.getPurchase(SKU_BRONZE),
                            mConsumeFinishedListener);
                }

                // update UI accordingly

            }

            Log.e(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener =
            new IabHelper.OnConsumeFinishedListener() {
                public void onConsumeFinished(Purchase purchase, IabResult result) {

                    // provision the in-app purchase to the user
                    // (for example, credit 50 gold coins to player's character)
                    Log.e(TAG, "request for consuming & checking result ");

                    if (result.isSuccess()) {
                        if (purchase.getSku().equals(SKU_BRONZE)){
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url_raiseOffer,
                                    new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            Log.e("Response", response);

                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.e("Error.Response", "Error");

                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams()
                                {
                                    Map<String, String>  params = new HashMap<String, String>();
                                    params.put("membership", "1");

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
                            Singleton.getInstance(getApplicationContext()).addToRequestqueue(postRequest);
                        }
                        else if (purchase.getSku().equals(SKU_SILVER)){
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url_raiseOffer,
                                    new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            Log.e("Response", response);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.e("Error.Response", "Error");
                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams()
                                {
                                    Map<String, String>  params = new HashMap<String, String>();
                                    params.put("membership", "2");

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
                            Singleton.getInstance(getApplicationContext()).addToRequestqueue(postRequest);
                        }
                        else if (purchase.getSku().equals(SKU_GOLD)){
                            Log.e(TAG, "anjam shdo");
                            StringRequest postRequest = new StringRequest(Request.Method.POST, url_raiseOffer,
                                    new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response) {
                                            // response
                                            Log.e("Response", response);
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            // error
                                            Log.e("Error.Response", "Error");
                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams()
                                {
                                    Map<String, String>  params = new HashMap<String, String>();
                                    params.put("membership", "3");

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
                            Singleton.getInstance(getApplicationContext()).addToRequestqueue(postRequest);
                        }
                    }
                    else if (purchase.getSku().equals(SKU_GOLD)){
                        Log.e(TAG, "___________________________anjam shd");
                    }
                    else {
                        Log.e("error in consume", result.getMessage() + "  result string:" + result.toString());
                    }
                }
            };

    //For back handle
    @Override
    public void onBackPressed() {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.mycontainer);
        if (f instanceof ProjectsFragment){
            popupForExit();
        } else
        if (f instanceof CreditFragment){
            popupForExit();
        } else
        if (f instanceof DashboardFragment){
            popupForExit();
        } else
        if (f instanceof MessagesFragment){
            popupForExit();
        } else
        if (f instanceof ProfileFragment){
            popupForExit();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }


    public void popupForExit(){
        pDialog = new PrettyDialog(this);
        pDialog
                .setTitle("")
                .setMessage("آیا می خواهید از برنامه خارج شوید؟")
                .setIcon(
                        R.drawable.pdlg_icon_info,     // icon resource
                        R.color.RakaarColor, null)
                .addButton(
                        "بله",
                        R.color.pdlg_color_white,
                        R.color.RakaarColor,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                                homeIntent.addCategory( Intent.CATEGORY_HOME );
                                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(homeIntent);
                                System.exit(1);
                            }
                        }
                )
                .addButton(
                        "خیر",
                        R.color.pdlg_color_white,
                        R.color.RakaarColor,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog.dismiss();
                            }
                        }
                )
                .show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        if (resultCode == -1){
            popupForSuccessfullPurche();

//            if (inventory1 != null){
//                if (inventory1.hasPurchase(SKU_FREE)){
////                    mHelper.launchPurchaseFlow(getActivity(), SKU_FREE, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
//                    mHelper.consumeAsync(inventory1.getPurchase(SKU_FREE),
//                            mConsumeFinishedListener);
//                }
//                else if (inventory1.hasPurchase(SKU_SILVER)){
//                    mHelper.consumeAsync(inventory1.getPurchase(SKU_SILVER),
//                            mConsumeFinishedListener);
//                }
//                else if (inventory1.hasPurchase(SKU_BRONZE)){
//                    mHelper.consumeAsync(inventory1.getPurchase(SKU_BRONZE),
//                            mConsumeFinishedListener);
//                }
//            }
        }
        else if (requestCode == 0){

        }

        // Pass on the activity result to
    }

    public void popupForSuccessfullPurche(){
        pDialog3 = new PrettyDialog(this);
        pDialog3.setCancelable(false);
        pDialog3
                .setTitle("تبریک!")
                .setMessage("خرید شما با موفقیت انجام شد")
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
                                pay();
                                pDialog3.dismiss();
                            }
                        }
                )
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    public void pay(){
        mHelper.queryInventoryAsync(mGotInventoryListener);
    }
}


