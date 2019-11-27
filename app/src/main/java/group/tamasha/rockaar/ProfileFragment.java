package group.tamasha.rockaar;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;


public class ProfileFragment extends Fragment {


    public ProfileFragment() {
        // Required empty public constructor
    }

    PrettyDialog pDialog1;
    PrettyDialog pDialog2, pDialog3;
    Button InviteButton, SupportButton, SkillsEdit, AboutmeEdit, SkillsOK, AboutmeOK, signout;
    SharedPreferences UserName, PassWord, Token, NamePref, ExpertisePref, SkillsPref, AboutMePref,
            WorksPref , ScorePref, imagePref, loadImage;
    String token;
    String url = "https://raakar.ir/info",
            skills_url = "https://raakar.ir/addSkills",
            aboutme_url = "https://raakar.ir/addPortfolio",
            userProfileImagePath,
            profileName, profileExpertise, profileSkills, profileAboutMe;
    int profileWorks, profileScore;
    File file;
    RequestBody requestFile = null;
    MultipartBody.Part body = null;
    Client client;
    TextView MyName, MyExpertise, MySkills, MyAboutme, MyWorks, MyScore;
    EditText SkillsText, AboutmeText;
    SharedPreferences.Editor profileNameEditor, profileExpertiseEditor, imgagePrefEditor;
    ImageView profile_picture;
    private static int RESULT_LOAD_IMAGE = 1;
    Bitmap bmp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        popupForCheckInternet();

        InviteButton=(Button) view.findViewById(R.id.invitebutton);
        SupportButton=(Button) view.findViewById(R.id.supportbutton);
        signout = (Button)view.findViewById(R.id.profile_signout);

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        SkillsEdit = (Button)view.findViewById(R.id.profile_skillsbtn);
        AboutmeEdit = (Button)view.findViewById(R.id.profile_aboutmebtn);
        SkillsOK = (Button)view.findViewById(R.id.profile_skillsok);
        AboutmeOK = (Button)view.findViewById(R.id.profile_aboutmeok);
        MyName = (TextView)view.findViewById(R.id.profile_name);
        MyExpertise = (TextView)view.findViewById(R.id.profile_expertise);
        MySkills = (TextView)view.findViewById(R.id.profile_skillstext);
        MyAboutme = (TextView)view.findViewById(R.id.profile_aboutmetext);
        SkillsText = (EditText) view.findViewById(R.id.profile_skillsedit);
        AboutmeText = (EditText) view.findViewById(R.id.profile_aboutmeedit);
        MyWorks = (TextView) view.findViewById(R.id.profile_works);
        MyScore = (TextView) view.findViewById(R.id.profile_score);
        profile_picture = (ImageView)view.findViewById(R.id.profile_picture);

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog3 = new PrettyDialog(getActivity());
                pDialog3
                        .setTitle("")
                        .setMessage("")
                        .setIcon(
                                R.drawable.pdlg_icon_info,     // icon resource
                                R.color.RakaarColor, null)
                        .addButton(
                                "تغیر عکس پروفایل",
                                R.color.pdlg_color_white,
                                R.color.RakaarColor,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        pDialog3.dismiss();
                                        Intent i = new Intent(Intent.ACTION_PICK,
                                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                                    }
                                }
                        )
                        .addButton(
                                "حذف عکس",
                                R.color.pdlg_color_white,
                                R.color.RakaarColor,
                                new PrettyDialogCallback() {
                                    @Override
                                    public void onClick() {
                                        pDialog3.dismiss();
                                        client = ServiceGenerator.createService(Client.class);

                                        Call<AddProjectResponse> call = client.deleteProfilePhoto(
                                                token,
                                                ""
                                        );

                                        call.enqueue(new Callback<AddProjectResponse>() {
                                            @Override
                                            public void onResponse(retrofit2.Call<AddProjectResponse> call, retrofit2.Response<AddProjectResponse> response) {
                                                Log.e("response code", String.valueOf(response.code()));
                                                if (response.isSuccessful()){
                                                    profile_picture.setImageResource(R.drawable.profile_circle);
                                                    imagePref = getActivity().getSharedPreferences("profileImage", Context.MODE_PRIVATE);
                                                    imgagePrefEditor = imagePref.edit();
                                                    imgagePrefEditor.clear().apply();
                                                }else {

                                                }
                                            }

                                            @Override
                                            public void onFailure(retrofit2.Call<AddProjectResponse> call, Throwable t) {
                                                Log.e("error", t.getMessage());
                                            }
                                        });
                                    }
                                }
                        )
                        .show();

            }
        });
        loadImage = getActivity().getSharedPreferences("profileImage", Context.MODE_PRIVATE);
        String bitmapString = loadImage.getString("profileImage", "");

        if (!bitmapString.equals("")){
            bmp = decodeToBase64(bitmapString);
            profile_picture.setImageBitmap(bmp);
        }

        WorksPref = getActivity().getSharedPreferences("doneworks", Context.MODE_PRIVATE);
        String worksprefText = WorksPref.getString("worksText", "0");
        MyWorks.setText(worksprefText);

        ScorePref = getActivity().getSharedPreferences("avrpoint", Context.MODE_PRIVATE);
        String scoreprefText = ScorePref.getString("scoreText", "0");
        MyScore.setText(scoreprefText);

        SkillsPref = getActivity().getSharedPreferences("skills", Context.MODE_PRIVATE);
        String skillsprefText = SkillsPref.getString("skillsText", "مهارت ها");
        MySkills.setText(skillsprefText);

        AboutMePref = getActivity().getSharedPreferences("aboutme", Context.MODE_PRIVATE);
        String aboutmeprefText = AboutMePref.getString("aboutmeText", "درباره من");
        MyAboutme.setText(aboutmeprefText);

        SkillsText.setVisibility(View.GONE);
        AboutmeText.setVisibility(View.GONE);
        SkillsOK.setVisibility(View.GONE);
        AboutmeOK.setVisibility(View.GONE);

        if (MySkills.getText().toString().equals("") || MySkills.getText().toString().equals("null")) {
            MySkills.setText("مهارت ها");
        }

        if (MyAboutme.getText().toString().equals("") || MyAboutme.getText().toString().equals("null")) {
            MyAboutme.setText("درباره من");
        }

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.
                            INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

                }
                return true;
            }
        });

        SkillsEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                MySkills.setVisibility(View.GONE);
                SkillsText.setVisibility(View.VISIBLE);
                SkillsEdit.setVisibility(View.GONE);
                SkillsOK.setVisibility(View.VISIBLE);

                SkillsText.setInputType(InputType.TYPE_NULL);
                SkillsText.setText(MySkills.getText().toString());

                if (MySkills.getText().toString().equals("مهارت ها")) {
                    SkillsText.setText("");
                }

                SkillsText.setInputType(InputType.TYPE_CLASS_TEXT);
                SkillsText.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(SkillsText, InputMethodManager.SHOW_FORCED);

            }});

        AboutmeEdit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                MyAboutme.setVisibility(View.GONE);
                AboutmeText.setVisibility(View.VISIBLE);
                AboutmeEdit.setVisibility(View.GONE);
                AboutmeOK.setVisibility(View.VISIBLE);

                AboutmeText.setInputType(InputType.TYPE_NULL);
                AboutmeText.setText(MyAboutme.getText().toString());

                if (MyAboutme.getText().toString().equals("درباره من")) {
                    AboutmeText.setText("");
                }

                AboutmeText.setInputType(InputType.TYPE_CLASS_TEXT);
                AboutmeText.requestFocus();
                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.showSoftInput(AboutmeText, InputMethodManager.SHOW_FORCED);
            }});

        SkillsOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                MySkills.setVisibility(View.VISIBLE);
                SkillsText.setVisibility(View.GONE);
                SkillsEdit.setVisibility(View.VISIBLE);
                SkillsOK.setVisibility(View.GONE);

                MySkills.setText(SkillsText.getText().toString());
                SharedPreferences.Editor SkillsPrefEdit = SkillsPref.edit();
                SkillsPrefEdit.putString("skillsText", MySkills.getText().toString());
                SkillsPrefEdit.apply();

                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(SkillsText.getWindowToken(), 0);

                if (MySkills.getText().toString().equals("")) {
                    MySkills.setText("مهارت ها");
                }

                StringRequest postRequest = new StringRequest(Request.Method.POST, skills_url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);

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
                        params.put("mySkill", MySkills.getText().toString());
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
            }});

        AboutmeOK.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                MyAboutme.setVisibility(View.VISIBLE);
                AboutmeText.setVisibility(View.GONE);
                AboutmeEdit.setVisibility(View.VISIBLE);
                AboutmeOK.setVisibility(View.GONE);



                if (AboutmeText.getText().toString().equals("") && SkillsText.getVisibility() == View.VISIBLE){
                    MyAboutme.setText("درباره من");
                }else {
                    MyAboutme.setText(AboutmeText.getText().toString());
                    AboutMePref = getActivity().getSharedPreferences("aboutme", Context.MODE_PRIVATE);
                    SharedPreferences.Editor AboutMePrefEdit = AboutMePref.edit();
                    AboutMePrefEdit.putString("aboutmeText", MyAboutme.getText().toString());
                    AboutMePrefEdit.commit();

                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(AboutmeText.getWindowToken(), 0);

                    if (MyAboutme.getText().toString().equals("")) {
                        MyAboutme.setText("درباره من");
                    }
                }

                StringRequest postRequest = new StringRequest(Request.Method.POST, aboutme_url,
                        new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response) {
                                // response
                                Log.d("Response", response);

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
                        params.put("myPortfolio", MyAboutme.getText().toString());
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
            }});

        NamePref = getActivity().getSharedPreferences("profileName", Context.MODE_PRIVATE);
        profileNameEditor = NamePref.edit();

        final String proName, proExpertise, proSkills, proAboutMe, pn = "", pe = "";

        ExpertisePref = getActivity().getSharedPreferences("profileExpertise", Context.MODE_PRIVATE);
        profileExpertiseEditor = ExpertisePref.edit();

        proName = NamePref.getString("profileName", null);
        proExpertise = ExpertisePref.getString("expertise", null);
        proAboutMe = AboutMePref.getString("aboutmeText", null);

        if (proName == null  && proExpertise == null){
            final JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("Response:", response.toString());
                            try {
                                JSONObject info = response.getJSONObject("info");

                                profileName = info.getString("name");
                                profileNameEditor.putString("profileName", profileName).apply();

                                profileExpertise = info.getString("expertise");
                                profileExpertiseEditor.putString("expertise", profileExpertise).apply();

                                profileSkills = info.getString("skills");
                                SharedPreferences.Editor SkillsPrefEdit = SkillsPref.edit();

                                if (profileSkills.equals(null) || profileSkills.equals("null") || profileSkills.equals("")){
                                    SkillsPrefEdit.putString("skillsText", "مهارت ها");
                                    SkillsPrefEdit.commit();
                                }else {
                                    SkillsPrefEdit.putString("skillsText", profileSkills);
                                    SkillsPrefEdit.commit();
                                }

                                profileAboutMe = info.getString("portfolio");
                                AboutMePref = getActivity().getSharedPreferences("aboutme", Context.MODE_PRIVATE);
                                SharedPreferences.Editor AboutMePrefEdit = AboutMePref.edit();

                                if (profileAboutMe.equals(null) || profileSkills.equals("null") || profileSkills.equals("")){
                                    AboutMePrefEdit.putString("aboutmeText", "درباره من");
                                    AboutMePrefEdit.commit();
                                }else {
                                    AboutMePrefEdit.putString("aboutmeText", profileAboutMe);
                                    AboutMePrefEdit.commit();
                                }

                                MyName.setText(profileName);
                                MyExpertise.setText(profileExpertise);
                                MySkills.setText(profileSkills);
                                MyAboutme.setText(profileAboutMe);

                                profileWorks = info.getInt("doneworks");
                                MyWorks.setText(String.valueOf(profileWorks));
                                profileScore = info.getInt("avrpoint");
                                MyScore.setText(String.valueOf(profileScore));

                                JSONObject myImg = info.getJSONObject("myImg");
                                String URL_string = "https://raakar.ir";
                                String filePath = "";
                                filePath = myImg.getString("path");
                                filePath = filePath.substring(6,filePath.length());
                                URL_string += filePath;

                                Picasso.with(getActivity()).load(URL_string).into(profile_picture);

//                                Bitmap bm = profile_picture.getDrawingCache(true);
////                                profile_picture.setDrawingCacheEnabled(true);
////                                profile_picture.buildDrawingCache();
////                                Bitmap bitmap = profile_picture.getDrawingCache();
//
//                                imagePref = getActivity().getSharedPreferences("profileImage", Context.MODE_PRIVATE);
//                                imgagePrefEditor = imagePref.edit();
//                                imgagePrefEditor.putString("profileImage", encodeToBase64(bm));
//                                imgagePrefEditor.apply();

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
            Singleton.getInstance(getActivity()).addToRequestqueue(jsonRequest);
        }else {
            MyExpertise.setText(proExpertise);
            MyName.setText(proName);
            MyAboutme.setText(proAboutMe);
        }

        final JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("Response:", response.toString());
                        try {
                            JSONObject info = response.getJSONObject("info");
                            JSONObject myImg = info.getJSONObject("myImg");
                            String URL_string = "https://raakar.ir";
                            String filePath = "";
                            filePath = myImg.getString("path");
                            filePath = filePath.substring(6,filePath.length());
                            URL_string += filePath;

                            Picasso.with(getActivity()).load(URL_string).into(profile_picture);
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
        Singleton.getInstance(getActivity()).addToRequestqueue(jsonRequest);

        InviteButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                InviteFragment invite = new InviteFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mycontainer, invite,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }});

        SupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SupportFragment invite = new SupportFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.mycontainer, invite,"findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });

        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupForSignout();
            }
        });

        return view;
    }

    public void popupForSignout(){
        pDialog1 = new PrettyDialog(getActivity());
        pDialog1.setCancelable(false);
        pDialog1
                .setTitle("")
                .setMessage("آیا می خواهید از حساب کاربری خود خارج شوید؟")
                .setIcon(
                        R.drawable.pdlg_icon_close,     // icon resource
                        R.color.pdlg_color_red, null)
                .addButton(
                        "بله",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog1.dismiss();
                                UserName = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
                                SharedPreferences.Editor userEditor = UserName.edit();
                                userEditor.clear().commit();

                                PassWord = getActivity().getSharedPreferences("pass", Context.MODE_PRIVATE);
                                SharedPreferences.Editor passEditor = PassWord.edit();
                                passEditor.clear().commit();

                                profileNameEditor.clear().commit();

                                profileExpertiseEditor.clear().commit();

                                SharedPreferences usernameOfOffer = getActivity().getSharedPreferences("username", Context.MODE_PRIVATE);
                                SharedPreferences.Editor usernameOfOfferEditor = usernameOfOffer.edit();
                                usernameOfOfferEditor.clear().commit();

                                SharedPreferences TabPosition = getActivity().getSharedPreferences("tabposition", Context.MODE_PRIVATE);
                                SharedPreferences.Editor TabPositionEditor = TabPosition.edit();
                                TabPositionEditor.clear().commit();

                                SharedPreferences Cardnumber = getActivity().getSharedPreferences("cardnumber", Context.MODE_PRIVATE);
                                SharedPreferences.Editor CardnumberEditor = Cardnumber.edit();
                                CardnumberEditor.clear().commit();

                                SharedPreferences deleteProImage = getActivity().getSharedPreferences("profileImage", Context.MODE_PRIVATE);
                                SharedPreferences.Editor deleteProImageEditor = deleteProImage.edit();
                                deleteProImageEditor.clear().apply();

                                Intent i = new Intent(getActivity(), MainActivity.class);
                                startActivity(i);
                            }
                        }
                )
                .addButton(
                        "خیر",
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

    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())) {
        } else {
            pDialog2 = new PrettyDialog(getActivity());
            pDialog2.setCancelable(false);
            pDialog2
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
                                    pDialog2.dismiss();

                                }
                            }
                    )
                    .show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            userProfileImagePath = cursor.getString(columnIndex);
            cursor.close();

            try {
                bmp = getBitmapFromUri(selectedImage);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


            if (userProfileImagePath != null) {
                try {
                    file = new File(userProfileImagePath);
                    Uri uri = Uri.fromFile(file);
                    if (file.exists()) {
                        Log.e("finaltest", "found");
                        requestFile =
                                RequestBody.create(MediaType.parse(getMimeType(uri)), file);
                        body =
                                MultipartBody.Part.createFormData("myImg", file.getName(), requestFile);

                        client = ServiceGenerator.createService(Client.class);

                        Call<AddProjectResponse> call = client.changeProfilePhoto(
                                token,
                                body
                        );

                        call.enqueue(new Callback<AddProjectResponse>() {
                            @Override
                            public void onResponse(retrofit2.Call<AddProjectResponse> call, retrofit2.Response<AddProjectResponse> response) {
                                Log.e("response code", String.valueOf(response.code()));
                                if (response.isSuccessful()){
                                    profile_picture.setImageBitmap(bmp);

                                    imagePref = getActivity().getSharedPreferences("profileImage", Context.MODE_PRIVATE);
                                    imgagePrefEditor = imagePref.edit();
                                    imgagePrefEditor.putString("profileImage", encodeToBase64(bmp));
                                    imgagePrefEditor.apply();

                                }else {

                                }
                            }

                            @Override
                            public void onFailure(retrofit2.Call<AddProjectResponse> call, Throwable t) {
                                Log.e("error", t.getMessage());
                            }
                        });
                    } else {
                        Log.e("finaltest", "not found");
                    }
                } catch (Exception e) {

                }
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    public String getMimeType(Uri uri) {
        String mimeType = "";
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = getActivity().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    public static String encodeToBase64(Bitmap image) {
        Bitmap immage = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    public static Bitmap decodeToBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

}
