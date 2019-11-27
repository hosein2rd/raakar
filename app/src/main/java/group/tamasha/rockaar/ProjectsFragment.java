package group.tamasha.rockaar;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.wang.avi.AVLoadingIndicatorView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import co.ceryle.radiorealbutton.RadioRealButton;
import co.ceryle.radiorealbutton.RadioRealButtonGroup;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class ProjectsFragment extends Fragment  {

    PrettyDialog pDialog;
    PrettyDialog pDialog2;
    RadioRealButton myworks_btn, sentbids_btn, existprojects_btn;
    RadioRealButtonGroup group;
    ListView project_listView;
    SharedPreferences Token, UserName, TabPosition;
    SharedPreferences.Editor TabPositionEditor;
    TextView project_nulltext;
    ArrayList<MyWorksAdapterItems> mlistnewsData = new ArrayList<MyWorksAdapterItems>();
    ArrayList<ProjectsAdapterItems> plistnewsData = new ArrayList<ProjectsAdapterItems>();
    ArrayList<SentBidsAdapterItems> alistnewsData = new ArrayList<SentBidsAdapterItems>();
    ArrayList<String> status = new ArrayList<String>();
    ArrayList<String> acception = new ArrayList<String>();
    int green = 1, red = 0;
    AVLoadingIndicatorView Projects_av;
    String URLgetallprojects = "https://raakar.ir/getAllProjects", URLgetmyprojects = "https://raakar.ir/getMyProjects", token;
    String URLgetmyoffers = "https://raakar.ir/getMyOffers", username;
    int TabP;

    public ProjectsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_projects, container, false);

        popupForCheckInternet();

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        myworks_btn = (RadioRealButton)view.findViewById(R.id.project_myworks_btn);
        sentbids_btn = (RadioRealButton)view.findViewById(R.id.project_sentbids_btn);
        existprojects_btn = (RadioRealButton)view.findViewById(R.id.project_existprojects_btn);
        group = (RadioRealButtonGroup)view.findViewById(R.id.project_segment);
        project_listView = (ListView)view.findViewById(R.id.project_listView);
        Projects_av = (AVLoadingIndicatorView)view.findViewById(R.id.projects_av);
        project_nulltext = (TextView)view.findViewById(R.id.project_nulltext);

        TabPosition = getActivity().getSharedPreferences("tabposition", Context.MODE_PRIVATE);
        TabPositionEditor = TabPosition.edit();
        TabP = TabPosition.getInt("tabposition", 2);
        group.setPosition(TabP);

        if (TabP == 2){
            group.setPosition(TabP);

            ProjectsAdapter exsitProjectAdapter;

            startAnim();
            final JsonArrayRequest jsonRequest = new JsonArrayRequest
                    (Request.Method.GET, URLgetallprojects, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("Response:", response.toString());
                            try {

                                plistnewsData.clear();

                                if(response.length() == 0){
                                    stopAnim();
                                    project_listView.setVisibility(View.GONE);
                                    project_nulltext.setText("هیچ پروژه ای برای نمایش وجود ندارد!");
                                    project_nulltext.setVisibility(View.VISIBLE);
                                }else {
                                    project_listView.setVisibility(View.VISIBLE);
                                    project_nulltext.setVisibility(View.GONE);
                                    for (int i=0; i<response.length(); i++)
                                    {
                                        JSONObject Projects = response.getJSONObject(i);
                                        String T = Projects.getString("amount");
                                        String Text = T.replaceAll("[^0-9]+","");
                                        long Number = Long.parseLong(Text);
                                        DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
                                        String yourFormattedString = formatter.format(Number);
                                        JSONArray offerArrayLength = Projects.getJSONArray("offersArray");

                                        if (T.equals("0")){
                                            plistnewsData.add(new ProjectsAdapterItems(
                                                    Projects.getString("name")
                                                    , Projects.getString("category") + "، " + Projects.getString("description")
                                                    , "توافقی"
                                                    , Projects.getString("deadline") + " روز"
                                                    , Projects.getString("creationDate")
                                                    , "تعداد پیشنهادات: " + String.valueOf(offerArrayLength.length())));
                                        }else {
                                            plistnewsData.add(new ProjectsAdapterItems(
                                                    Projects.getString("name")
                                                    , Projects.getString("category") + "، " + Projects.getString("description")
                                                    , yourFormattedString + " تومان"
                                                    , Projects.getString("deadline") + " روز"
                                                    , Projects.getString("creationDate")
                                                    , String.valueOf("تعداد پیشنهادات: " + offerArrayLength.length())));
                                        }
                                    }
                                    stopAnim();
                                    ProjectsAdapter exsitProjectAdapter;
                                    exsitProjectAdapter = new ProjectsAdapter(plistnewsData);
                                    project_listView.setAdapter(exsitProjectAdapter);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            project_listView.setVisibility(View.GONE);
                            project_nulltext.setText("هیچ پروژه ای برای نمایش وجود ندارد!");
                            project_nulltext.setVisibility(View.VISIBLE);
                            stopAnim();
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("token", token);
                    return headers;
                }};
            Singleton.getInstance(getActivity()).addToRequestqueue(jsonRequest);

            TabPositionEditor.putInt("tabposition", 2).apply();

            exsitProjectAdapter = new ProjectsAdapter(plistnewsData);
            project_listView.setAdapter(exsitProjectAdapter);
        }

        if (TabP == 1)
        {
            group.setPosition(TabP);

            alistnewsData.clear();
            startAnim();
            final JsonArrayRequest offerjsonRequest = new JsonArrayRequest
                    (Request.Method.GET, URLgetmyoffers, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.d("Response:", response.toString());
                            try {
                                UserName = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                                username = UserName.getString("user", null);
                                if(response.length() == 0){
                                    stopAnim();
                                    project_listView.setVisibility(View.GONE);
                                    project_nulltext.setText("شما پیشنهادی ارسال نکرده اید!");
                                    project_nulltext.setVisibility(View.VISIBLE);
                                }else {
                                    project_listView.setVisibility(View.VISIBLE);
                                    project_nulltext.setVisibility(View.GONE);
                                    for (int j = 0; j < response.length() ; j++){
                                        JSONObject offers = response.getJSONObject(j);
                                        acception.add(offers.getString("status"));
                                        JSONArray myOffer = offers.getJSONArray("offersArray");

                                        for (int i = 0; i < myOffer.length(); i++){
                                            JSONObject infoOffer = myOffer.getJSONObject(i);
                                            String T = infoOffer.getString("amount");
                                            String Text = T.replaceAll("[^0-9]+","");
                                            long Number = Long.parseLong(Text);
                                            DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
                                            String yourFormattedString = formatter.format(Number);
                                            if (username.equals(infoOffer.getString("creatorUsername"))){
                                                if (infoOffer.getBoolean("status") == false){
                                                    alistnewsData.add(new SentBidsAdapterItems(red,
                                                            "ویرایش",
                                                            "حذف",
                                                            offers.getString("name"),
                                                            yourFormattedString + " تومان" + " ( " + offers.getString("deadline") + " روز" + " ) ",
                                                            "وضعیت: تایید نشده",
                                                            ""));
                                                }else if (infoOffer.getBoolean("status") == true){
                                                    alistnewsData.add(new SentBidsAdapterItems(green,
                                                            "ویرایش",
                                                            "حذف",
                                                            offers.getString("name"),
                                                            yourFormattedString + " تومان" + " ( " + offers.getString("deadline") + " روز" + " ) ",
                                                            "وضعیت: تایید شده",
                                                            infoOffer.getString("acceptionYear") + "/" + infoOffer.getString("acceptionMonth") + "/" + infoOffer.getString("acceptionDate")));
                                                }
                                            }
                                        }
                                    }
                                    stopAnim();
                                    SentBidsAdapter sadapter;
                                    sadapter = new SentBidsAdapter(alistnewsData);
                                    project_listView.setAdapter(sadapter);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            project_listView.setVisibility(View.GONE);
                            project_nulltext.setText("هیچ پروژه ای برای نمایش وجود ندارد!");
                            project_nulltext.setVisibility(View.VISIBLE);
                            stopAnim();
                        }
                    }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError
                {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("token", token);
                    return headers;
                }
            };
            Singleton.getInstance(getActivity()).addToRequestqueue(offerjsonRequest);

            TabPositionEditor.putInt("tabposition", 1).apply();
        }

        if (TabP == 0)
        {
            group.setPosition(TabP);

            startAnim();
            final JsonObjectRequest mjsonRequest = new JsonObjectRequest
                    (Request.Method.GET, URLgetmyprojects, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response:", response.toString());
                            try {

                                plistnewsData.clear();
                                alistnewsData.clear();
                                mlistnewsData.clear();
                                status.clear();

                                if(response.toString().equals("your project array is empty")){
                                    stopAnim();
                                    project_listView.setVisibility(View.GONE);
                                    project_nulltext.setText("شما پروژه ای ثبت نکرده اید!");
                                    project_nulltext.setVisibility(View.VISIBLE);
                                }else {
                                    stopAnim();
                                    project_listView.setVisibility(View.VISIBLE);
                                    project_nulltext.setVisibility(View.GONE);
                                    JSONArray MyWorks = response.getJSONArray("yourProjects");
                                    for (int i=0; i < MyWorks.length(); i++)
                                    {
                                        JSONObject Index = MyWorks.getJSONObject(i);
                                        JSONArray indexLenght = Index.getJSONArray("offersArray");
                                        if (Index.getString("status").equals("0")){
                                            stopAnim();
                                            project_listView.setVisibility(View.GONE);
                                            project_nulltext.setText("شما پروژه ای ثبت نکرده اید!");
                                            project_nulltext.setVisibility(View.VISIBLE);
                                        }else {
                                            stopAnim();
                                            project_listView.setVisibility(View.VISIBLE);
                                            project_nulltext.setVisibility(View.GONE);
                                            status.add(Index.getString("status"));
                                            mlistnewsData.add(new MyWorksAdapterItems("حذف"
                                                    , Index.getString("name")
                                                    , Index.getString("description")
                                                    , "تعداد پیشنهادات: " + String.valueOf(indexLenght.length())
                                                    , Index.getString("creationDate")));
                                        }
                                    }
                                    stopAnim();
                                    MyWorksAdapter myWorksAdapter;
                                    myWorksAdapter = new MyWorksAdapter(mlistnewsData);
                                    project_listView.setAdapter(myWorksAdapter);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            project_listView.setVisibility(View.GONE);
                            project_nulltext.setText("شما پروژه ای ثبت نکرده اید!");
                            project_nulltext.setVisibility(View.VISIBLE);
                            stopAnim();
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

            TabPosition = getActivity().getSharedPreferences("tabposition", Context.MODE_PRIVATE);
            TabPositionEditor = TabPosition.edit();
            TabPositionEditor.putInt("tabposition", 0).apply();
        }

        Typeface IRANSansFaNum = Typeface.createFromAsset(getActivity().getAssets(), "fonts/IRANSansMobile.TTF");

        myworks_btn.setTypeface(IRANSansFaNum);
        sentbids_btn.setTypeface(IRANSansFaNum);
        existprojects_btn.setTypeface(IRANSansFaNum);

        group.setOnClickedButtonListener(new RadioRealButtonGroup.OnClickedButtonListener() {
            @Override
            public void onClickedButton(RadioRealButton button, int position) {
                switch (position){
                    case 0:
                        startAnim();
                        final JsonObjectRequest mjsonRequest = new JsonObjectRequest
                                (Request.Method.GET, URLgetmyprojects, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Response:", response.toString());
                                        try {

                                            plistnewsData.clear();
                                            alistnewsData.clear();
                                            mlistnewsData.clear();
                                            status.clear();

                                            if(response.toString().equals("your project array is empty")){
                                                stopAnim();
                                                project_listView.setVisibility(View.GONE);
                                                project_nulltext.setText("شما پروژه ای ثبت نکرده اید!");
                                                project_nulltext.setVisibility(View.VISIBLE);
                                            }else {
                                                stopAnim();
                                                project_listView.setVisibility(View.VISIBLE);
                                                project_nulltext.setVisibility(View.GONE);
                                                JSONArray MyWorks = response.getJSONArray("yourProjects");
                                                for (int i=0; i < MyWorks.length(); i++)
                                                {
                                                    JSONObject Index = MyWorks.getJSONObject(i);
                                                    JSONArray indexLenght = Index.getJSONArray("offersArray");
                                                    if (Index.getString("status").equals("0")){
                                                        stopAnim();
                                                        project_listView.setVisibility(View.GONE);
                                                        project_nulltext.setText("شما پروژه ای ثبت نکرده اید!");
                                                        project_nulltext.setVisibility(View.VISIBLE);
                                                    }else {
                                                        stopAnim();
                                                        project_listView.setVisibility(View.VISIBLE);
                                                        project_nulltext.setVisibility(View.GONE);
                                                        status.add(Index.getString("status"));
                                                        mlistnewsData.add(new MyWorksAdapterItems("حذف"
                                                                , Index.getString("name")
                                                                , Index.getString("description")
                                                                , "تعداد پیشنهادات: " + String.valueOf(indexLenght.length())
                                                                , Index.getString("creationDate")));
                                                    }
                                                }
                                                stopAnim();
                                                MyWorksAdapter myWorksAdapter;
                                                myWorksAdapter = new MyWorksAdapter(mlistnewsData);
                                                project_listView.setAdapter(myWorksAdapter);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        project_listView.setVisibility(View.GONE);
                                        project_nulltext.setText("شما پروژه ای ثبت نکرده اید!");
                                        project_nulltext.setVisibility(View.VISIBLE);
                                        stopAnim();
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

                        TabPosition = getActivity().getSharedPreferences("tabposition", Context.MODE_PRIVATE);
                        TabPositionEditor = TabPosition.edit();
                        TabPositionEditor.putInt("tabposition", 0).apply();

                        break;

                    case 1:
                        alistnewsData.clear();
                        startAnim();
                        final JsonArrayRequest offerjsonRequest = new JsonArrayRequest
                                (Request.Method.GET, URLgetmyoffers, null, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.d("Response:", response.toString());
                                        try {
                                            UserName = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                                            username = UserName.getString("user", null);
                                            if(response.length() == 0){
                                                stopAnim();
                                                project_listView.setVisibility(View.GONE);
                                                project_nulltext.setText("شما پیشنهادی ارسال نکرده اید!");
                                                project_nulltext.setVisibility(View.VISIBLE);
                                            }else {
                                                project_listView.setVisibility(View.VISIBLE);
                                                project_nulltext.setVisibility(View.GONE);
                                                for (int j = 0; j < response.length() ; j++){
                                                    JSONObject offers = response.getJSONObject(j);
                                                    acception.add(offers.getString("status"));
                                                    JSONArray myOffer = offers.getJSONArray("offersArray");

                                                    for (int i = 0; i < myOffer.length(); i++){
                                                        JSONObject infoOffer = myOffer.getJSONObject(i);
                                                        String T = infoOffer.getString("amount");
                                                        String Text = T.replaceAll("[^0-9]+","");
                                                        long Number = Long.parseLong(Text);
                                                        DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
                                                        String yourFormattedString = formatter.format(Number);
                                                        if (username.equals(infoOffer.getString("creatorUsername"))){
                                                            if (infoOffer.getBoolean("status") == false){
                                                                alistnewsData.add(new SentBidsAdapterItems(red,
                                                                        "ویرایش",
                                                                        "حذف",
                                                                        offers.getString("name"),
                                                                        yourFormattedString + " تومان" + " ( " + offers.getString("deadline") + " روز" + " ) ",
                                                                        "وضعیت: تایید نشده",
                                                                        ""));
                                                            }else if (infoOffer.getBoolean("status") == true){
                                                                alistnewsData.add(new SentBidsAdapterItems(green,
                                                                        "ویرایش",
                                                                        "حذف",
                                                                        offers.getString("name"),
                                                                        yourFormattedString + " تومان" + " ( " + offers.getString("deadline") + " روز" + " ) ",
                                                                        "وضعیت: تایید شده",
                                                                        infoOffer.getString("acceptionYear") + "/" + infoOffer.getString("acceptionMonth") + "/" + infoOffer.getString("acceptionDate")));
                                                            }
                                                        }
                                                    }
                                                }
                                                stopAnim();
                                                SentBidsAdapter sadapter;
                                                sadapter = new SentBidsAdapter(alistnewsData);
                                                project_listView.setAdapter(sadapter);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        project_listView.setVisibility(View.GONE);
                                        project_nulltext.setText("شما پیشنهادی ارسال نکرده اید!");
                                        project_nulltext.setVisibility(View.VISIBLE);
                                        stopAnim();
                                    }
                                }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError
                            {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("token", token);
                                return headers;
                            }};
                        Singleton.getInstance(getActivity()).addToRequestqueue(offerjsonRequest);

                        TabPosition = getActivity().getSharedPreferences("tabposition", Context.MODE_PRIVATE);
                        TabPositionEditor = TabPosition.edit();
                        TabPositionEditor.putInt("tabposition", 1).apply();

                        break;

                    case 2:
                        ProjectsAdapter exsitProjectAdapter;

                        startAnim();
                        final JsonArrayRequest jsonRequest = new JsonArrayRequest
                                (Request.Method.GET, URLgetallprojects, null, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.d("Response:", response.toString());
                                        try {

                                            plistnewsData.clear();

                                            if(response.length() == 0){
                                                stopAnim();
                                                project_listView.setVisibility(View.GONE);
                                                project_nulltext.setText("هیچ پروژه ای برای نمایش وجود ندارد!");
                                                project_nulltext.setVisibility(View.VISIBLE);
                                            }else {
                                                project_listView.setVisibility(View.VISIBLE);
                                                project_nulltext.setVisibility(View.GONE);
                                                for (int i=0; i<response.length(); i++)
                                                {
                                                    JSONObject Projects = response.getJSONObject(i);
                                                    String T = Projects.getString("amount");
                                                    String Text = T.replaceAll("[^0-9]+","");
                                                    long Number = Long.parseLong(Text);
                                                    DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
                                                    String yourFormattedString = formatter.format(Number);
                                                    JSONArray offerArrayLength = Projects.getJSONArray("offersArray");

                                                    if (T.equals("0")){
                                                        plistnewsData.add(new ProjectsAdapterItems(
                                                                Projects.getString("name")
                                                                , Projects.getString("category") + "، " + Projects.getString("description")
                                                                , "توافقی"
                                                                , Projects.getString("deadline") + " روز"
                                                                , Projects.getString("creationDate")
                                                                , "تعداد پیشنهادات: " + String.valueOf(offerArrayLength.length())));
                                                    }else {
                                                        plistnewsData.add(new ProjectsAdapterItems(
                                                                Projects.getString("name")
                                                                , Projects.getString("category") + "، " + Projects.getString("description")
                                                                , yourFormattedString + " تومان"
                                                                , Projects.getString("deadline") + " روز"
                                                                , Projects.getString("creationDate")
                                                                , String.valueOf("تعداد پیشنهادات: " + offerArrayLength.length())));
                                                    }
                                                }
                                                stopAnim();
                                                ProjectsAdapter exsitProjectAdapter;
                                                exsitProjectAdapter = new ProjectsAdapter(plistnewsData);
                                                project_listView.setAdapter(exsitProjectAdapter);
                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, new Response.ErrorListener() {

                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        project_listView.setVisibility(View.GONE);
                                        project_nulltext.setText("هیچ پروژه ای برای نمایش وجود ندارد!");
                                        project_nulltext.setVisibility(View.VISIBLE);
                                        stopAnim();
                                    }
                                }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError
                            {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("token", token);
                                return headers;
                            }};
                        Singleton.getInstance(getActivity()).addToRequestqueue(jsonRequest);

                        TabPositionEditor.putInt("tabposition", 2).apply();

                        exsitProjectAdapter = new ProjectsAdapter(plistnewsData);
                        project_listView.setAdapter(exsitProjectAdapter);

                        break;
                }
            }
        });

        Log.d("position:", "____________________________________________________" + group.getPosition());


        project_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (group.getPosition() == 0){

                    if (status.get(position).equals("1")){
                        MyBidsFragment MyBidsFragment = new MyBidsFragment();
                        FragmentManager manager = getActivity().getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.mycontainer, MyBidsFragment).addToBackStack("Back").commit();

                        TextView name = (TextView)view.findViewById(R.id.myworks_name);

                        Bundle bundle = new Bundle();
                        bundle.putInt("position", position);
                        bundle.putString("name", name.getText().toString());
                        MyBidsFragment.setArguments(bundle);
                    }

                    if (status.get(position).equals("2") || status.get(position).equals("4") || status.get(position).equals("3") || status.get(position).equals("5")){

                        final JsonObjectRequest pjsonRequest = new JsonObjectRequest
                                (Request.Method.GET, URLgetmyprojects, null, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.d("Response:", response.toString());
                                        try {
                                            String projectDate;
                                            JSONArray yourProjects = response.getJSONArray("yourProjects");
                                            JSONObject myProject = yourProjects.getJSONObject(position);

                                            int status = myProject.getInt("status");

                                            SharedPreferences statusSHP = getActivity().getSharedPreferences("statusMyWork", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor statusSHPE = statusSHP.edit();
                                            statusSHPE.putInt("statusMyWork", status);
                                            statusSHPE.apply();

                                            Bundle myWorkBundle = new Bundle();
                                            myWorkBundle.putString("name", myProject.getString("name"));
                                            myWorkBundle.putString("projectIndex", myProject.getString("projectIndex"));
                                            myWorkBundle.putString("category", myProject.getString("category"));

                                            JSONArray offer = myProject.getJSONArray("offersArray");

                                            for (int i = 0; i < offer.length(); i++){
                                                JSONObject acceptOffer = offer.getJSONObject(i);
                                                if (acceptOffer.getBoolean("status") == true){

                                                    myWorkBundle.putString("amount", acceptOffer.getString("amount"));
                                                    myWorkBundle.putInt("deadline", acceptOffer.getInt("deadline"));
                                                    myWorkBundle.putString("description", acceptOffer.getString("description"));
                                                    myWorkBundle.putInt("acceptionYear", acceptOffer.getInt("acceptionYear"));
                                                    myWorkBundle.putInt("acceptionMonth", acceptOffer.getInt("acceptionMonth"));
                                                    myWorkBundle.putInt("acceptionDate", acceptOffer.getInt("acceptionDate"));
                                                    myWorkBundle.putString("offerIndex", String.valueOf(i));
                                                    myWorkBundle.putString("creatorName", acceptOffer.getString("creatorName"));
                                                    myWorkBundle.putString("creatorUsername", acceptOffer.getString("creatorUsername"));

                                                    projectDate = acceptOffer.getInt("acceptionYear") + "/" + acceptOffer.getInt("acceptionMonth") + "/" + acceptOffer.getInt("acceptionDate");

                                                    myWorkBundle.putString("projectDate" , projectDate);

                                                    MyWorkInfoFragment myWorkInfoFragment = new MyWorkInfoFragment();
                                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                                    manager.beginTransaction().replace(R.id.mycontainer, myWorkInfoFragment).addToBackStack("Back").commit();
                                                    myWorkInfoFragment.setArguments(myWorkBundle);

                                                }
                                            }

                                            final String finallink = myProject.getString("finalLink");
                                            SharedPreferences finalfile = getActivity().getSharedPreferences("finalLink", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor finalFileE = finalfile.edit();
                                            finalFileE.putString("finalLink", finallink);
                                            finalFileE.apply();

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
                        Singleton.getInstance(getActivity()).addToRequestqueue(pjsonRequest);
                    }


                }else if (group.getPosition() == 2){

                    final ProjectsItemsFragment ProjectsItemsFragment = new ProjectsItemsFragment();

                    final JsonArrayRequest pjsonRequest = new JsonArrayRequest
                            (Request.Method.GET, URLgetallprojects, null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    Log.d("Response:", response.toString());
                                    try {

                                        JSONObject infoProject = response.getJSONObject(position);
                                        String name = infoProject.getString("name");
                                        String amount = infoProject.getString("amount");
                                        String deadline = infoProject.getString("deadline");
                                        String offerNumber = infoProject.getString("offersnumber");
                                        String description = infoProject.getString("description");
                                        String category = infoProject.getString("category");
                                        String projectOwnerName = infoProject.getString("projectOwnerName");
                                        String projectIndex = infoProject.getString("projectIndex");
                                        String projectOwnerUsername = infoProject.getString("projectOwnerUsername");

                                        Bundle bundle = new Bundle();
                                        bundle.putInt("position", position);
                                        bundle.putString("name", name);
                                        bundle.putString("amount", amount);
                                        bundle.putString("deadline", deadline);
                                        bundle.putString("offerNumber", offerNumber);
                                        bundle.putString("description", description);
                                        bundle.putString("category", category);
                                        bundle.putString("projectOwnerName", projectOwnerName);
                                        bundle.putString("projectIndex", projectIndex);
                                        bundle.putString("projectOwnerUsername", projectOwnerUsername);

                                        ProjectsItemsFragment.setArguments(bundle);

                                        JSONObject projectFile = infoProject.getJSONObject("projectFile");
                                        String filePath = "";
                                        String urlString = "";
                                        filePath = projectFile.getString("path");
                                        urlString = "https://raakar.ir";

                                        filePath = filePath.substring(6, filePath.length());

                                        urlString += filePath;

                                        SharedPreferences filePathSHP = getActivity().getSharedPreferences("filePath", Context.MODE_PRIVATE);
                                        SharedPreferences.Editor fileSHPE = filePathSHP.edit();
                                        fileSHPE.putString("filePath", urlString);
                                        fileSHPE.apply();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    FragmentManager manager = getActivity().getSupportFragmentManager();
                                    manager.beginTransaction().replace(R.id.mycontainer, ProjectsItemsFragment).addToBackStack("Back").commit();
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
                    Singleton.getInstance(getActivity()).addToRequestqueue(pjsonRequest);

                }else if (group.getPosition() == 1){

                    if (acception.get(position).equals("2")){

                        final JsonArrayRequest offerjsonRequest = new JsonArrayRequest
                                (Request.Method.GET, URLgetmyoffers, null, new Response.Listener<JSONArray>() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        Log.d("Response:", response.toString());
                                        try {
                                            Bundle myOfferBundle = new Bundle();

                                            JSONObject projectSelected = response.getJSONObject(position);

                                            myOfferBundle.putString("name", projectSelected.getString("name"));
                                            myOfferBundle.putString("category", projectSelected.getString("category"));
                                            myOfferBundle.putString("projectOwnerName", projectSelected.getString("projectOwnerName"));
                                            myOfferBundle.putString("description", projectSelected.getString("description"));
                                            myOfferBundle.putString("projectIndex", projectSelected.getString("projectIndex"));
                                            myOfferBundle.putString("projectOwnerUsername", projectSelected.getString("projectOwnerUsername"));

                                            SharedPreferences projectIndex = getActivity().getSharedPreferences("projectIndex", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor projectIndexEditor = projectIndex.edit();
                                            projectIndexEditor.putString("projectIndex", projectSelected.getString("projectIndex"));
                                            projectIndexEditor.apply();

                                            SharedPreferences projectOwnerName = getActivity().getSharedPreferences("projectOwnerUsername", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor projectOwnerNameEditor = projectOwnerName.edit();
                                            projectOwnerNameEditor.putString("projectOwnerUsername", projectSelected.getString("projectOwnerUsername"));
                                            projectOwnerNameEditor.apply();

                                            JSONArray offersArray = projectSelected.getJSONArray("offersArray");
                                            for (int i = 0; i < offersArray.length(); i++){
                                                JSONObject myOffer = offersArray.getJSONObject(i);
                                                if (myOffer.getString("creatorUsername").equals(username)){
                                                    if (myOffer.getBoolean("status") == true){
                                                        myOfferBundle.putInt("deadline", myOffer.getInt("deadline"));
                                                        myOfferBundle.putInt("acceptionYear", myOffer.getInt("acceptionYear"));
                                                        myOfferBundle.putInt("acceptionMonth", myOffer.getInt("acceptionMonth"));
                                                        myOfferBundle.putInt("acceptionDate", myOffer.getInt("acceptionDate"));
                                                        myOfferBundle.putString("amount", myOffer.getString("amount"));

                                                        DeadlineFragment deadlineFragment = new DeadlineFragment();
                                                        final FragmentManager manager = getActivity().getSupportFragmentManager();
                                                        manager.beginTransaction().replace(R.id.mycontainer, deadlineFragment)
                                                                .addToBackStack("Back").commit();

                                                        deadlineFragment.setArguments(myOfferBundle);
                                                    }
                                                }
                                            }

                                            JSONObject projectFile = projectSelected.getJSONObject("projectFile");

                                            String filePath = "";
                                            String url_download = "";
                                            filePath = projectFile.getString("path");
                                            filePath = filePath.substring(6, filePath.length());
                                            url_download = "https://raakar.ir";

                                            url_download += filePath;

                                            SharedPreferences DownloadFileForDeadline = getActivity().getSharedPreferences("filePathDeadline", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = DownloadFileForDeadline.edit();
                                            editor.putString("filePathDeadline", url_download);
                                            editor.apply();

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
                            }
                        };
                        Singleton.getInstance(getActivity()).addToRequestqueue(offerjsonRequest);

                    }else if (acception.get(position).equals("3")){
                        pDialog2 = new PrettyDialog(getActivity());
                        pDialog2
                                .setTitle("")
                                .setMessage("شما فایل نهایی را ارسال کرده اید!")
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
                                                pDialog2.dismiss();
                                            }
                                        }
                                )
                                .show();
                    }
                }
            }
        });

        Button plus = (Button)view.findViewById(R.id.projects_plus_button);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddFragment Add = new AddFragment();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                manager.beginTransaction().replace(R.id.mycontainer, Add).addToBackStack("Back").commit();
            }
        });
        return view;
    }

    private class MyWorksAdapter extends BaseAdapter {
        public ArrayList<MyWorksAdapterItems> mlistnewsDataAdpater ;

        public MyWorksAdapter(ArrayList<MyWorksAdapterItems>  mlistnewsDataAdpater) {
            this.mlistnewsDataAdpater=mlistnewsDataAdpater;
        }

        @Override
        public int getCount() {
            return mlistnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getActivity().getLayoutInflater();
            View myView = mInflater.inflate(R.layout.title_of_secondlistview, null);

            final MyWorksAdapterItems m = mlistnewsDataAdpater.get(position);

            TextView name = (TextView)myView.findViewById(R.id.myworks_name);
            name.setText(m.mname);

            TextView details = (TextView)myView.findViewById(R.id.myworks_details);
            details.setText(m.mdetails);

            TextView amount_of_bids = (TextView)myView.findViewById(R.id.myworks_amount_of_bids);
            amount_of_bids.setText(m.amount_of_bids);

            TextView date = (TextView)myView.findViewById(R.id.myworks_date);
            date.setText(m.date);

            return myView;
        }

    }

    private class ProjectsAdapter extends BaseAdapter {
        public ArrayList<ProjectsAdapterItems> plistnewsDataAdpater ;

        public ProjectsAdapter(ArrayList<ProjectsAdapterItems>  plistnewsDataAdpater) {
            this.plistnewsDataAdpater=plistnewsDataAdpater;
        }

        @Override
        public int getCount() {
            return plistnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getActivity().getLayoutInflater();
            View myView = mInflater.inflate(R.layout.title_of_firstlistview, null);

            final ProjectsAdapterItems p = plistnewsDataAdpater.get(position);

            TextView name = (TextView)myView.findViewById(R.id.projects_name);
            name.setText(p.pname);

            TextView details = (TextView)myView.findViewById(R.id.projects_details);
            details.setText(p.pdetails);

            TextView cost = (TextView)myView.findViewById(R.id.projects_cost);
            cost.setText(p.pcost);

            TextView day = (TextView)myView.findViewById(R.id.projects_day);
            day.setText(p.day);

            TextView date = (TextView)myView.findViewById(R.id.projects_date);
            date.setText(p.date);

            TextView bids = (TextView)myView.findViewById(R.id.project_propsal);
            bids.setText(p.bids);

            return myView;
        }

    }

    private class SentBidsAdapter extends BaseAdapter {
        public ArrayList<SentBidsAdapterItems> plistnewsDataAdpater ;

        public SentBidsAdapter(ArrayList<SentBidsAdapterItems>  plistnewsDataAdpater) {
            this.plistnewsDataAdpater=plistnewsDataAdpater;
        }

        @Override
        public int getCount() {
            return plistnewsDataAdpater.size();
        }

        @Override
        public String getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            SentBidsAdapterItems color = plistnewsDataAdpater.get(position);
            if (color.color == 0){
                return 0;
            }else
            if (color.color == 1){
                return 1;
            }else
            if (color.color == 2){
                return 2;
            }else
                return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getActivity().getLayoutInflater();

            final SentBidsAdapterItems p = plistnewsDataAdpater.get(position);

            if (getItemViewType(position) == 0){
                View myView = mInflater.inflate(R.layout.sentbids_red, null);
                TextView name = (TextView)myView.findViewById(R.id.sentbids_red_name);
                name.setText(p.name);

                TextView details = (TextView)myView.findViewById(R.id.sentbids_red_details);
                details.setText(p.details);

                TextView date = (TextView)myView.findViewById(R.id.sentbids_red_date);
                date.setText(p.date);

                TextView status = (TextView)myView.findViewById(R.id.sentbids_red_status);
                status.setText(p.status);

                return myView;
            }else {
                View myView = mInflater.inflate(R.layout.sentbids_green, null);
                TextView name = (TextView)myView.findViewById(R.id.sentbids_green_name);
                name.setText(p.name);

                TextView details = (TextView)myView.findViewById(R.id.sentbids_green_details);
                details.setText(p.details);

                TextView date = (TextView)myView.findViewById(R.id.sentbids_green_date);
                date.setText(p.date);

                TextView status = (TextView)myView.findViewById(R.id.sentbids_green_status);
                status.setText(p.status);

                return myView;
            }
        }

    }

    public void startAnim(){
        Projects_av.smoothToShow();
        // or avi.smoothToShow();
    }

    public void stopAnim(){
        Projects_av.smoothToHide();
        // or avi.smoothToHide();
    }

    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
        } else {
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
