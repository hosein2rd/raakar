package group.tamasha.rockaar;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;


public class ChatFragment extends Fragment {


    public ChatFragment() {
        // Required empty public constructor
    }

    ListView lv;
    ArrayList<ChatItems> listnewsData = new ArrayList<ChatItems>();
    ChatFragment.MyCustomAdapter myadapter;
    String token, url_receive = "https://raakar.ir/getMyMessages", url_sending = "https://raakar.ir/Message";
    int sendMessage = 0, receiveMessage = 1;
    SharedPreferences Token;
    Button sending, off;
    EditText sending_text;
    TextView chat_title;
    String senderusername, receiverusername, username;
    ArrayList<String> Chat = new ArrayList<String>();
    AVLoadingIndicatorView chat_av2;
    JsonArrayRequest jsonRequest;
    PrettyDialog pDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_chat, container, false);

        popupForCheckInternet();

        sending = (Button)view.findViewById(R.id.chat_send_icon);
        off = (Button)view.findViewById(R.id.chat_off_icon);
        sending_text = (EditText)view.findViewById(R.id.chat_text);

        lv = (ListView)view.findViewById(R.id.chat_listview);
        chat_title = (TextView)view.findViewById(R.id.chat_title);
        chat_av2 = (AVLoadingIndicatorView)view.findViewById(R.id.chat_av2);

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        final Bundle senderName = getArguments();
        chat_title.setText(senderName.getString("senderName"));

        sending_text.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (sending_text.getText().toString().equals("") || sending_text.getText().toString().equals(" ") || sending_text.getText().toString().equals("  ") || sending_text.getText().toString().equals("   ") || sending_text.getText().toString().equals("    ") || sending_text.getText().toString().equals("     ") || sending_text.getText().toString().equals("      ") || sending_text.getText().toString().equals("       ") || sending_text.getText().toString().equals("        ") || sending_text.getText().toString().equals("         ") || sending_text.getText().toString().equals("          ") || sending_text.getText().toString().equals("           ") || sending_text.getText().toString().equals("            ") || sending_text.getText().toString().equals("             ") || sending_text.getText().toString().equals("              ") || sending_text.getText().toString().equals("               ") || sending_text.getText().toString().equals("                ") || sending_text.getText().toString().equals("                 ") || sending_text.getText().toString().equals("                  ") || sending_text.getText().toString().equals("                   ") || sending_text.getText().toString().equals("                    "))
                {
                    sending.setVisibility(View.INVISIBLE);
                    off.setVisibility(View.VISIBLE);
                }
                else
                {
                    off.setVisibility(View.INVISIBLE);
                    sending.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (sending_text.getText().toString().equals("") || sending_text.getText().toString().equals(" ") || sending_text.getText().toString().equals("  ") || sending_text.getText().toString().equals("   ") || sending_text.getText().toString().equals("    ") || sending_text.getText().toString().equals("     ") || sending_text.getText().toString().equals("      ") || sending_text.getText().toString().equals("       ") || sending_text.getText().toString().equals("        ") || sending_text.getText().toString().equals("         ") || sending_text.getText().toString().equals("          ") || sending_text.getText().toString().equals("           ") || sending_text.getText().toString().equals("            ") || sending_text.getText().toString().equals("             ") || sending_text.getText().toString().equals("              ") || sending_text.getText().toString().equals("               ") || sending_text.getText().toString().equals("                ") || sending_text.getText().toString().equals("                 ") || sending_text.getText().toString().equals("                  ") || sending_text.getText().toString().equals("                   ") || sending_text.getText().toString().equals("                    "))
                {
                    sending.setVisibility(View.INVISIBLE);
                    off.setVisibility(View.VISIBLE);
                }
                else
                {
                    off.setVisibility(View.INVISIBLE);
                    sending.setVisibility(View.VISIBLE);
                }
            }
        });

        startAnim();

        jsonRequest = new JsonArrayRequest
                (Request.Method.GET, url_receive, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        Log.d("Response:", response.toString());
                        try {
                            SharedPreferences positionOfChat = getActivity().getSharedPreferences("positionOfChat", Context.MODE_PRIVATE);
                            int index = positionOfChat.getInt("position", 0);
                            for (int i = 0; i < response.length(); i++){
                                JSONObject ListOfmessages = response.getJSONObject(i);
                                Log.d("list of messages :", ListOfmessages.toString());
                                senderusername = ListOfmessages.getString("senderusername");
                                receiverusername = ListOfmessages.getString("recieverusername");
                                String text = ListOfmessages.getString("text");
                                boolean status = ListOfmessages.getBoolean("status");

                                if (status){
                                    if (chat_title.getText().toString().equals(receiverusername)){
                                        listnewsData.add(new ChatItems(sendMessage, text));
                                        username = receiverusername;
                                    }
                                }else {
                                    if (chat_title.getText().toString().equals(senderusername)){
                                        listnewsData.add(new ChatItems(receiveMessage, text));
                                        username = senderusername;
                                    }
                                }
                            }
                            myadapter = new MyCustomAdapter(listnewsData);
                            stopAnim();
                            lv.setAdapter(myadapter);
//                            lv.setSelection(lv.getAdapter().getCount()-1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("token", token);
                return headers;
            }};
        Singleton.getInstance(getActivity()).addToRequestqueue(jsonRequest);




        sending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sending_text.getText().toString() == "") {
                    Toast.makeText(getActivity(), "لطفا پیامی را جهت ارسال وارد نمایید!",
                            Toast.LENGTH_LONG).show();
                }

                else if (sending_text.getText().toString() != "")
                {
                    StringRequest postRequest = new StringRequest(Request.Method.POST, url_sending,
                            new Response.Listener<String>()
                            {
                                @Override
                                public void onResponse(String response) {
                                    listnewsData.add(new ChatItems(sendMessage, sending_text.getText().toString()));
                                    myadapter = new MyCustomAdapter(listnewsData);
                                    lv.setAdapter(myadapter);
                                    sending_text.setText("");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams()
                        {
                            Map<String, String>  params = new HashMap<String, String>();
                            params.put("username", username);
                            params.put("text", sending_text.getText().toString());

                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            //headers.put("Content-Type", "application/json");
                            headers.put("token", token);
                            return headers;
                        }
                    };
                    Singleton.getInstance(getActivity()).addToRequestqueue(postRequest);
                    VolleyLog.DEBUG = true;
                }
            }
        });

        chat_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chat_title.getText().toString().equals("admin")){
                    PublicProfileFragment PublicProfileFragment = new PublicProfileFragment();
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    manager.beginTransaction().replace(R.id.mycontainer, PublicProfileFragment).addToBackStack("Back").commit();

                    SharedPreferences username = getActivity().getSharedPreferences("username", Context.MODE_PRIVATE);
                    SharedPreferences.Editor usernameEditor = username.edit();

                    String usernameText = chat_title.getText().toString();
                    usernameEditor.putString("userName", usernameText).apply();
                }
            }
        });


        return view;
    }

    public void startAnim(){
        chat_av2.smoothToShow();
        // or avi.smoothToShow();
    }

    public void stopAnim(){
        chat_av2.smoothToHide();
        // or avi.smoothToHide();
    }

    private class MyCustomAdapter extends BaseAdapter {
        public ArrayList<ChatItems> listnewsDataAdpater ;

        public MyCustomAdapter(ArrayList<ChatItems>  listnewsDataAdpater) {
            this.listnewsDataAdpater=listnewsDataAdpater;
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
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            ChatItems color = listnewsDataAdpater.get(position);
            if (color.color == 0) {
                return 0;
            } else if (color.color == 1) {
                return 1;
            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            LayoutInflater mInflater = getActivity().getLayoutInflater();

            final ChatItems s = listnewsDataAdpater.get(position);

            if (getItemViewType(position) == 0){
                View myView = mInflater.inflate(R.layout.chat_sending_table, null);

                TextView chat = (TextView)myView.findViewById(R.id.chat_text_sending);
                chat.setText(s.chat);

                return myView;
            }else if (getItemViewType(position) == 1){
                View myView = mInflater.inflate(R.layout.chat_receiving_table, null);

                TextView chat = (TextView)myView.findViewById(R.id.chat_text_receiving);
                chat.setText(s.chat);
                return myView;
            }else {
                View myView = mInflater.inflate(R.layout.chat_sending_table, null);

                TextView chat = (TextView)myView.findViewById(R.id.chat_text_sending);
                chat.setText(s.chat);
                return myView;
            }
        }

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
