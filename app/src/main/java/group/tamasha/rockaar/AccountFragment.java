package group.tamasha.rockaar;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import group.tamasha.rockaar.util.IabHelper;
import group.tamasha.rockaar.util.IabResult;
import group.tamasha.rockaar.util.Inventory;
import group.tamasha.rockaar.util.Purchase;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class AccountFragment extends Fragment {


    public AccountFragment() {
        // Required empty public constructor
    }


    // Debug tag, for logging
    static final String TAG = "______________________";

    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_BRONZE = "sku_bronze";
    static final String SKU_SILVER = "sku_silver";
    static final String SKU_GOLD = "sku_gold";

    // Does the user have the premium upgrade?
    boolean mIsPremium1 = true;


    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 3;

    IabHelper mHelper;

    Button Gold, Silver, Bronze;
    SharedPreferences Token;
    String  url_raiseOffer = "https://raakar.ir/simpleRaiseOffer",
            token;

    PrettyDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        popupForCheckInternet();

        Gold = (Button)view.findViewById(R.id.goldbutton);
        Silver = (Button)view.findViewById(R.id.silverbutton);
        Bronze = (Button)view.findViewById(R.id.bronzebutton);

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC8cbvYE2VTVs/7Jj37yDmT+ybhtOhSenOuagFtqOSsKnjGVRdnVaIRW66DTE36/lFvc4aT9gerbq2hIsE0WmAnPWv4Ap/rJXOLH2iAEjNSJBbiVNyafyIy2O+dqSjH8gcpP/i71BsgKurvC6yxyzLim5RMpaTnbNmi13nWBO9XZ+9EDzP9p/RR9QRdd2X2eoGZ+5SdbXgnT6bbyNmhbldCqPEkyQc+QPU88c+TupsCAwEAAQ==";

        mHelper = new IabHelper(getContext(), base64EncodedPublicKey);

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


        Gold.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (mHelper != null) mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(getActivity(), SKU_GOLD, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
            }});

        Silver.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (mHelper != null) mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(getActivity(), SKU_SILVER, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
            }});

        Bronze.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (mHelper != null) mHelper.flagEndAsync();
                mHelper.launchPurchaseFlow(getActivity(), SKU_BRONZE, RC_REQUEST, mPurchaseFinishedListener, "payload-string");
            }});

        return view;
    }

    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
        }else{
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

                Log.e(TAG, "User is " + (mIsPremium1 ? "PREMIUM" : "NOT PREMIUM"));
            }

            Log.e(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
//
//        // Pass on the activity result to the helper for handling
//        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
//            super.onActivityResult(requestCode, resultCode, data);
//        } else {
//            Log.d(TAG, "onActivityResult handled by IABUtil.");
//        }
//    }

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
                            Singleton.getInstance(getActivity()).addToRequestqueue(postRequest);
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
                            Singleton.getInstance(getActivity()).addToRequestqueue(postRequest);
                        }
                        else if (purchase.getSku().equals(SKU_GOLD)){
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
                            Singleton.getInstance(getActivity()).addToRequestqueue(postRequest);
                        }
                    }
                    else {
                        Log.e("error in consume", result.getMessage() + "  result string:" + result.toString());
                    }
                }
            };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }
}
