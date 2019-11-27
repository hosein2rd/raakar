package group.tamasha.rockaar;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import group.tamasha.rockaar.util.IabHelper;
import group.tamasha.rockaar.util.IabResult;
import group.tamasha.rockaar.util.Inventory;
import group.tamasha.rockaar.util.Purchase;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import me.tankery.lib.circularseekbar.CircularSeekBar;

import static group.tamasha.rockaar.Utilities.getCurrentShamsidate;


public class DashboardFragment extends Fragment {

    SharedPreferences Token, Username;

    String token, username;

    CircularSeekBar seekBarb, seekBarr, seekBarg;

    String doneWorksString = "0", worksCountString, allAmountString = "0", gottenAmountString = "0", nameOfLastProject,
            currentTimeFa, url_raiseOffer = "https://raakar.ir/simpleRaiseOffer";

    String base64EncodedPublicKey = "MIHNMA0GCSqGSIb3DQEBAQUAA4G7ADCBtwKBrwC8cbvYE2VTVs/7Jj37yDmT+ybhtOhSenOuagFtqOSsKnjGVRdnVaIRW66DTE36/lFvc4aT9gerbq2hIsE0WmAnPWv4Ap/rJXOLH2iAEjNSJBbiVNyafyIy2O+dqSjH8gcpP/i71BsgKurvC6yxyzLim5RMpaTnbNmi13nWBO9XZ+9EDzP9p/RR9QRdd2X2eoGZ+5SdbXgnT6bbyNmhbldCqPEkyQc+QPU88c+TupsCAwEAAQ==";

    Integer doneWorks = 0, worksCount = 0, allAmount = 0, gottenAmount = 0;

    TextView numberred, numbergreen, numberblue, timelinetime, timelinetitle;

    SeekBar timeline;

    static final String TAG = "______________________";

    String membership;

    PrettyDialog pDialog;

    ImageView Medal;

    TextView MedalText;

    static final String SKU_BRONZE = "sku_bronze";
    static final String SKU_SILVER = "sku_silver";
    static final String SKU_GOLD = "sku_gold";

//    IabHelper mHelper;

    int currentYear, currentMonth, currentDay,
            startDay, startMonth, startYear,
            endDay, endMonth, endYear, deadlineDay,
            myoffernumber, finaloffernumber;

    public DashboardFragment() {
        // Required empty public constructor
    }

    Button AccountButton;
    String URLgetInfo = "https://raakar.ir/info";

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        popupForCheckInternet();

        AccountButton=(Button) view.findViewById(R.id.medalbutton);
        seekBarb = (CircularSeekBar)view.findViewById(R.id.seekbarblue);
        seekBarr = (CircularSeekBar)view.findViewById(R.id.seekbarred);
        seekBarg = (CircularSeekBar)view.findViewById(R.id.seekbargreen);
        numberred = (TextView)view.findViewById(R.id.numberred);
        numbergreen = (TextView)view.findViewById(R.id.numbergreen);
        numberblue = (TextView)view.findViewById(R.id.numberblue);
        timelinetitle = (TextView)view.findViewById(R.id.timelinetitle);
        timeline = (SeekBar)view.findViewById(R.id.timeline);
        timelinetime = (TextView)view.findViewById(R.id.timelinetime);

        timeline.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);

        token = Token.getString("token", null);

        Username = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);

        username = Username.getString("user", null);

        Medal = (ImageView)view.findViewById(R.id.medalimg);
        MedalText = (TextView)view.findViewById(R.id.medaltype);

        final JsonObjectRequest njsonRequest = new JsonObjectRequest
                (Request.Method.GET, "https://raakar.ir/info", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response:", response.toString());
                        try {
                            JSONObject myinfo = response.getJSONObject("info");

                            membership = myinfo.getString("membership");
                            if (membership.equals("0"))
                            {
                                Medal.setImageResource(R.drawable.nomedal);
                                MedalText.setText("برای خرید لمس کنید");
                                MedalText.setTextSize(10);
                            }
                            else if (membership.equals("1"))
                            {
                                Medal.setImageResource(R.drawable.bronzemedal);
                                MedalText.setText("برنزی");
                                MedalText.setTextSize(12);
                            }
                            else if (membership.equals("2"))
                            {
                                Medal.setImageResource(R.drawable.silvermedal);
                                MedalText.setText("نقره ای");
                                MedalText.setTextSize(12);
                            }
                            else if (membership.equals("3"))
                            {
                                Medal.setImageResource(R.drawable.goldmedal);
                                MedalText.setText("طلایی");
                                MedalText.setTextSize(12);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){@Override
        public Map<String, String> getHeaders() throws AuthFailureError
        {
            HashMap<String, String> headers = new HashMap<String, String>();
            //headers.put("Content-Type", "application/json");
            headers.put("token", token);
            return headers;
        }};
        Singleton.getInstance(getActivity()).addToRequestqueue(njsonRequest);

        if (membership == "0")
        {
            Medal.setImageResource(R.drawable.nomedal);
            MedalText.setText("برای خرید لمس کنید");
            MedalText.setTextSize(10);
        }
        else if (membership == "1")
        {
            Medal.setImageResource(R.drawable.bronzemedal);
            MedalText.setText("برنزی");
            MedalText.setTextSize(12);
        }
        else if (membership == "2")
        {
            Medal.setImageResource(R.drawable.silvermedal);
            MedalText.setText("نقره ای");
            MedalText.setTextSize(12);
        }
        else if (membership == "3")
        {
            Medal.setImageResource(R.drawable.goldmedal);
            MedalText.setText("طلایی");
            MedalText.setTextSize(12);
        }

        final JsonArrayRequest mjsonRequest = new JsonArrayRequest
                (Request.Method.GET, "https://raakar.ir/getMyOffers", null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response:", response.toString());
                        try {
                            for (int i = 0; i < response.length(); i++){

                                JSONObject getMyOffers = response.getJSONObject(i);
                                JSONArray offersArray = getMyOffers.getJSONArray("offersArray");

                                endDay = 0;
                                startDay = 0;
                                deadlineDay = 0;

                                for (int j = 0; j < offersArray.length(); j++){

                                    JSONObject offerArrayItems = offersArray.getJSONObject(j);

                                    if (username.equals(offerArrayItems.getString("creatorUsername"))){
                                        if (offerArrayItems.getBoolean("status") == true){
                                            nameOfLastProject = getMyOffers.getString("name");

                                            timelinetitle.setText(nameOfLastProject);

                                            currentTimeFa = getCurrentShamsidate();

                                            String[] separated = currentTimeFa.split("/");
                                            currentYear = Integer.parseInt(separated[0]);
                                            currentMonth = Integer.parseInt(separated[1]);
                                            currentDay = Integer.parseInt(separated[2]);

                                            startDay = offerArrayItems.getInt("acceptionDate");
                                            startMonth = offerArrayItems.getInt("acceptionMonth");
                                            startYear = offerArrayItems.getInt("acceptionYear");

                                            endDay = startDay + offerArrayItems.getInt("deadline");
                                            endMonth = startMonth;
                                            endYear = startYear;

                                            if (
                                                    startMonth == 1 ||
                                                            startMonth == 2 ||
                                                            startMonth == 3 ||
                                                            startMonth == 4 ||
                                                            startMonth == 5 ||
                                                            startMonth == 6
                                                    ){
                                                if (endDay > 31){
                                                    endDay = endDay - 31;
                                                    endMonth++;
                                                }
                                            }else {
                                                if (endMonth != 12){
                                                    if (endDay > 30){
                                                        endDay = endDay - 30;
                                                        endMonth++;
                                                    }
                                                }else {
                                                    if (endDay > 29){
                                                        endDay = endDay - 29;
                                                        endMonth = 1;
                                                        endYear++;
                                                    }
                                                }

                                            }

                                            if (endYear - currentYear > 0 ){
                                                deadlineDay += (endYear - currentYear) * 365;
                                            }

                                            if (endMonth - currentMonth > 0){
                                                if (endMonth == 1 || endMonth == 2 || endMonth == 3 || endMonth == 4 || endMonth == 5 || endMonth ==6){
                                                    deadlineDay += (endMonth - currentMonth) * 31;
                                                }else {
                                                    deadlineDay += (endMonth - currentMonth) * 30;
                                                }
                                            }

                                            if (endDay - currentDay > 0){
                                                deadlineDay += endDay - currentDay;
                                            }

                                            timeline.setProgress(0);

                                            timeline.setProgress((100*deadlineDay)/offerArrayItems.getInt("deadline"));

                                            timelinetime.setText("مدت زمان باقی مانده به روز " + String.valueOf(deadlineDay));
                                        }
                                    }
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("token", token);
                return headers;
            }};
        Singleton.getInstance(getActivity()).addToRequestqueue(mjsonRequest);

        final JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, URLgetInfo, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response:", response.toString());
                        try {
                            JSONObject info = response.getJSONObject("info");

                            doneWorksString = info.getString("doneworks");
                            Log.d("doneWorksString", doneWorksString);
                            worksCountString = info.getString("workscount");
                            Log.d("worksCountString", worksCountString);

                            seekBarb.setProgress(0);
                            myoffernumber = info.getInt("myoffernumber");
                            finaloffernumber = info.getInt("finaloffernumber");
                            seekBarb.setProgress((100 * myoffernumber) / finaloffernumber);
                            numberblue.setText(String.valueOf(myoffernumber));

                            JSONObject credit = info.getJSONObject("credit");
                            int total = credit.getInt("total");
                            int current = credit.getInt("current");
                            int allAmount = total + current;
                            int total100 = total/1000;


                            if (worksCountString.equals("0")){
                                seekBarr.setProgress(0);
                                int seekredValue = ((doneWorks.parseInt(doneWorksString) * 100) / 1);
                                seekBarr.setProgress(seekredValue);
                                numberred.setText(doneWorksString);
                            }else {
                                seekBarr.setProgress(0);
                                int seekredValue = ((doneWorks.parseInt(doneWorksString) * 100) / allAmount);
                                seekBarr.setProgress(seekredValue);
                                numberred.setText(doneWorksString);
                            }

                            if (allAmount == 0){
                                seekBarg.setProgress(0);
                                int seekgreenValue = ((total * 100) / 1);
                                seekBarg.setProgress(seekgreenValue);
                                numbergreen.setText(String.valueOf(total100));
                            }else {
                                int seekgreenValue = ((total * 100) / allAmount);
                                seekBarg.setProgress(seekgreenValue);
                                numbergreen.setText(String.valueOf(total100));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Response:", "_______________________________error");
                    }
                }){@Override
        public Map<String, String> getHeaders() throws AuthFailureError
        {
            HashMap<String, String> headers = new HashMap<String, String>();
            //headers.put("Content-Type", "application/json");
            headers.put("token", token);
            return headers;
        }};
        Singleton.getInstance(getActivity()).addToRequestqueue(jsonRequest);

        AccountButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {

                AccountFragment account = new AccountFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mycontainer, account,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
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
}
