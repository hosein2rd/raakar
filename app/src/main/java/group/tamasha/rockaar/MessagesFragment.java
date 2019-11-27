package group.tamasha.rockaar;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class MessagesFragment extends Fragment {


    public MessagesFragment() {
        // Required empty public constructor
    }

    String url = "https://raakar.ir/getMyMessages", token, username, nextUsername, lastText;
    SharedPreferences Token;
    ListView lv;
    MyCustomAdapter myadapter;
    TextView MessagesNullText;
    ArrayList<MessagesItems> listnewsData = new ArrayList<MessagesItems>();
    ArrayList<MessagesItems> result = new ArrayList<MessagesItems>();
    AVLoadingIndicatorView av;
    ConstraintLayout constraint;
    String nextSenderusername, nextRecieverusername, nextText;
    boolean nextstatus;
    Stack<String> message;
    PrettyDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messages, container, false);

        popupForCheckInternet();

        lv = (ListView)view.findViewById(R.id.messages_listView);
        av = (AVLoadingIndicatorView)view.findViewById(R.id.messages_av);
        constraint = (ConstraintLayout)view.findViewById(R.id.messages_constraint);

        message = new Stack<>();

        MessagesNullText = (TextView)view.findViewById(R.id.messages_nulltext);

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        av.setVisibility(View.VISIBLE);
        constraint.setAlpha(0.5f);
        startAnim();

        listnewsData.clear();

        final JsonArrayRequest jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Response:", response.toString());
                        MessagesNullText.setVisibility(View.GONE);
                        try {
                            for (int i = 0 ; i < response.length() ; i++){
                                JSONObject ListOfmessages = response.getJSONObject(i);
                                String senderusername = ListOfmessages.getString("senderusername");
                                String recieverusername = ListOfmessages.getString("recieverusername");
                                String text = ListOfmessages.getString("text");
                                boolean status = ListOfmessages.getBoolean("status");

                                lastText = text;

                                int notEqual = 0;
                                for (int t = 0; t < i ; t++){

                                    JSONObject checkingListOfmessages = response.getJSONObject(t);
                                    String checkingSenderusername = checkingListOfmessages.getString("senderusername");
                                    String checkingRecieverusername = checkingListOfmessages.getString("recieverusername");
                                    String checkingText = checkingListOfmessages.getString("text");
                                    boolean checkingStatus = checkingListOfmessages.getBoolean("status");

                                    if (t == i){
                                        for (int j = i + 1; j < response.length(); j++){
                                            JSONObject nextListOfmessages = response.getJSONObject(j);
                                            String nextSenderusername = nextListOfmessages.getString("senderusername");
                                            String nextRecieverusername = nextListOfmessages.getString("recieverusername");
                                            String nextText = nextListOfmessages.getString("text");
                                            boolean nextStatus = nextListOfmessages.getBoolean("status");

                                            if (status && nextStatus && recieverusername.equals(nextRecieverusername)){
                                                lastText = nextText;
                                            }else if (status && !nextStatus && recieverusername.equals(nextSenderusername)){
                                                lastText = nextText;
                                            }else if (!status && nextStatus && senderusername.equals(nextRecieverusername)){
                                                lastText = nextText;
                                            }else if (!status && !nextStatus && senderusername.equals(nextSenderusername)){
                                                lastText = nextText;
                                            }
                                        }

                                        if (status){
                                            username = recieverusername;
                                        }else {
                                            username = senderusername;
                                        }
                                        listnewsData.add(new MessagesItems(0, username, "تیترها", lastText, "", "2"));
                                    }

                                    if (status && checkingStatus && !recieverusername.equals(checkingRecieverusername)){
                                        notEqual++;
                                    }else if (status && !checkingStatus && !recieverusername.equals(checkingSenderusername)){
                                        notEqual++;
                                    }else if (!status && checkingStatus && !senderusername.equals(checkingRecieverusername)){
                                        notEqual++;
                                    }else if (!status && !checkingStatus && !senderusername.equals(checkingSenderusername)){
                                        notEqual++;
                                    }
                                }

                                if (notEqual == i){
                                    for (int j = i + 1; j < response.length(); j++){
                                        JSONObject nextListOfmessages = response.getJSONObject(j);
                                        String nextSenderusername = nextListOfmessages.getString("senderusername");
                                        String nextRecieverusername = nextListOfmessages.getString("recieverusername");
                                        String nextText = nextListOfmessages.getString("text");
                                        boolean nextStatus = nextListOfmessages.getBoolean("status");

                                        if (status && nextStatus && recieverusername.equals(nextRecieverusername)){
                                            lastText = nextText;
                                        }else if (status && !nextStatus && recieverusername.equals(nextSenderusername)){
                                            lastText = nextText;
                                        }else if (!status && nextStatus && senderusername.equals(nextRecieverusername)){
                                            lastText = nextText;
                                        }else if (!status && !nextStatus && senderusername.equals(nextSenderusername)){
                                            lastText = nextText;
                                        }
                                    }

                                    if (status){
                                        username = recieverusername;
                                    }else {
                                        username = senderusername;
                                    }
                                    listnewsData.add(new MessagesItems(0, username, "تیترها", lastText, "", "2"));
                                }
                            }

                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                        myadapter = new MyCustomAdapter(listnewsData);
                        lv.setAdapter(myadapter);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                ChatFragment chat = new ChatFragment();
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                manager.beginTransaction().replace(R.id.mycontainer, chat).addToBackStack("chat").commit();

                                TextView messages_name = (TextView)view.findViewById(R.id.messages_name);

                                SharedPreferences positionOfChat = getActivity().getSharedPreferences("positionOfChat", Context.MODE_PRIVATE);
                                SharedPreferences.Editor positionEditor = positionOfChat.edit();
                                positionEditor.putInt("position", position);
                                positionEditor.commit();

                                Bundle senderName = new Bundle();
                                senderName.putString("senderName", messages_name.getText().toString());
                                chat.setArguments(senderName);
                            }
                        });
                        stopAnim();
                        constraint.setAlpha(1f);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        stopAnim();
                        constraint.setAlpha(1f);
                        MessagesNullText.setText("مشکل در اتصال به اینترنت!");
                        MessagesNullText.setVisibility(View.VISIBLE);
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

        return view;
    }

    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<MessagesItems> listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<MessagesItems>  listnewsDataAdpater) {
            this.listnewsDataAdpater = listnewsDataAdpater;
        }

        @Override
        public int getCount() {
            return listnewsDataAdpater.size();
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
            View myView = mInflater.inflate(R.layout.title_of_messages, null);

            final MessagesItems s = listnewsDataAdpater.get(position);

            TextView name = (TextView)myView.findViewById(R.id.messages_name);
            name.setText(s.name);

            TextView text = (TextView)myView.findViewById(R.id.messages_text);
            text.setText(s.text);

            TextView date = (TextView)myView.findViewById(R.id.messages_date);
            date.setText(s.date);

            TextView num = (TextView)myView.findViewById(R.id.messages_num);
            num.setText(s.num);

            return myView;
        }

    }

    public void startAnim(){
        av.smoothToShow();
    }

    public void stopAnim(){
        av.smoothToHide();
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
