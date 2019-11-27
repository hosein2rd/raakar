package group.tamasha.rockaar;


import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nononsenseapps.filepicker.FilePickerActivity;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.DecimalFormat;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import me.tankery.lib.circularseekbar.CircularSeekBar;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static group.tamasha.rockaar.Utilities.getCurrentShamsidate;
import static java.lang.String.valueOf;


public class DeadlineFragment extends Fragment {

    PrettyDialog pDialog1;
    PrettyDialog pDialog2;
    PrettyDialog pDialog3;

    TextView
            deadline_title,
            deadline_undertitle,
            deadline_price,
            deadline_start,
            deadline_time,
            deadline_end,
            deadline_infotext,
            deadline_seeknumber,
            deadline_filetitle;

    int getDeadlineDay;

    int
        endDay,
        endMonth,
        endYear,
        startDay,
        startMonth,
        startYear,
        currentYear,
        currentMonth,
        currentDay,
        deadlineDay,
        seekbarValue;

    CircularSeekBar
            deadline_seekbar;

    String
        currentTimeFa,
        userProfileImagePath,
        token;

    ImageView
            deadline_fileback,
            deadline_upload_finalFile,
            deadline_dowload;

    Button
            deadline_button;

    SharedPreferences
        Token, projectOwnerUserName, projectIndex, DLfile, statusSHP;

    int status;

    EditText
            add_linktext;

    String download_file;

    Call<AddProjectResponse> call;

    private static int RESULT_LOAD_IMAGE = 1;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public DeadlineFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_deadline, container, false);

        popupForCheckInternet();

        statusSHP = getActivity().getSharedPreferences("statusMyWork", Context.MODE_PRIVATE);
        status = statusSHP.getInt("statusMyWork", 0);

        deadline_title = (TextView)view.findViewById(R.id.deadline_title);
        deadline_undertitle = (TextView)view.findViewById(R.id.deadline_undertitle);
        deadline_price = (TextView)view.findViewById(R.id.deadline_price);
        deadline_start = (TextView)view.findViewById(R.id.deadline_start);
        deadline_time = (TextView)view.findViewById(R.id.deadline_time);
        deadline_end = (TextView)view.findViewById(R.id.deadline_end);
        deadline_infotext = (TextView)view.findViewById(R.id.deadline_infotext);
        deadline_seeknumber = (TextView)view.findViewById(R.id.deadline_seeknumber);
        deadline_filetitle = (TextView)view.findViewById(R.id.deadline_filetitle);
        deadline_upload_finalFile = (ImageView)view.findViewById(R.id.deadline_upload);
        add_linktext = (EditText)view.findViewById(R.id.add_linktext);
        deadline_dowload = (ImageView)view.findViewById(R.id.deadline_dowload);

        deadline_seekbar = (CircularSeekBar)view.findViewById(R.id.deadline_seekbar);

        deadline_fileback =(ImageView)view.findViewById(R.id.deadline_fileback);

        deadline_button = (Button)view.findViewById(R.id.deadline_button);

        DLfile = getActivity().getSharedPreferences("filePathDeadline", Context.MODE_PRIVATE);
        download_file = DLfile.getString("filePathDeadline", "");

        deadline_dowload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(download_file));
                startActivity(intent);
            }
        });

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        Log.e("token", token);

        currentTimeFa = getCurrentShamsidate();

        String[] separated = currentTimeFa.split("/");
        currentYear = Integer.parseInt(separated[0]);
        currentMonth = Integer.parseInt(separated[1]);
        currentDay = Integer.parseInt(separated[2]);

        final Bundle bundle = getArguments();

        String T = bundle.getString("amount");
        String Text = T.replaceAll("[^0-9]+","");
        long Number = Long.parseLong(Text);
        DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
        String yourFormattedString = formatter.format(Number);

        deadline_title.setText(bundle.getString("name"));
        deadline_undertitle.setText(bundle.getString("projectOwnerName") + " / " + bundle.getString("category"));
        deadline_price.setText(yourFormattedString + " تومان");

        startDay = bundle.getInt("acceptionDate");
        startMonth = bundle.getInt("acceptionMonth");
        startYear = bundle.getInt("acceptionYear");

        deadline_start.setText(startYear + "/" + startMonth + "/" + startDay);

        deadline_time.setText(bundle.getInt("deadline") + " روز");

        deadline_infotext.setText(bundle.getString("description"));

        endDay = startDay + bundle.getInt("deadline");
        endMonth = startMonth;
        endYear = startYear;

        if (
                bundle.getInt("acceptionMonth") == 1 ||
                        bundle.getInt("acceptionMonth") == 2 ||
                        bundle.getInt("acceptionMonth") == 3 ||
                        bundle.getInt("acceptionMonth") == 4 ||
                        bundle.getInt("acceptionMonth") == 5 ||
                        bundle.getInt("acceptionMonth") == 6
                ){
            for (;endDay>31;){
                if (endDay > 31){
                    endDay = endDay - 31;
                    endMonth++;
                }
            }
        }else if (bundle.getInt("acceptionMonth") == 7 ||
                bundle.getInt("acceptionMonth") == 8 ||
                bundle.getInt("acceptionMonth") == 9 ||
                bundle.getInt("acceptionMonth") == 10 ||
                bundle.getInt("acceptionMonth") == 11){
            for (;endDay>30;){
                if (endMonth != 12){
                    if (endDay > 30){
                        endDay = endDay - 30;
                        endMonth++;
                    }
                }
            }
        }
        else {
            for(;endDay>29;){
                if (endDay > 30){
                    endDay = endDay - 30;
                    endMonth = 1;
                    endYear++;
                }

            }
        }

        deadline_end.setText(endYear + "/" + endMonth + "/" + endDay);

        int difDay, difYear, difMonth;

        difDay = endDay - startDay;
        difMonth = endMonth - startMonth;
        difYear = endYear - startYear;

        deadlineDay = 0;

        if (difMonth > 0){
            deadlineDay += difDay;
            deadlineDay += difYear*365;
            if (1 <= startMonth && startMonth <= 6){
                if (1 <= endMonth && endMonth <= 6){
                    deadlineDay += difMonth*31;


                }else if (7 <= endMonth && endMonth <=11){
                    deadlineDay += (7 - startMonth)*31 + (endMonth - 7)*30;


                }else if (endMonth == 12){
                    deadlineDay += (7 - startMonth)*31 + (endMonth - 7)*30;


                }


            }else if (7 <= startMonth && startMonth <=11){
                if (1 <= endMonth && endMonth <= 6){
                    deadlineDay += (endMonth - 1)*31 + (13 - startMonth)*30 - 1 - 365;


                }else if (7 <= endMonth && endMonth <=11){
                    deadlineDay += difMonth*30;


                }else if (endMonth == 12){
                    deadlineDay += difMonth*30;
                }


            }else if (startMonth == 12){
                if (1 <= endMonth && endMonth <= 6){
                    deadlineDay += (endMonth - 1)*31 + (13 - startMonth)*30 - 1 - 365;


                }else if (7 <= endMonth && endMonth <=11){
                    deadlineDay += difMonth*30;


                }else if (endMonth == 12){

                    deadlineDay += 0;

                }
            }
        }else if (difMonth < 0){

            deadlineDay += difDay;
            deadlineDay += difYear*365;
            if (1 <= startMonth && startMonth <= 6){
                if (1 <= endMonth && endMonth <= 6){
                    deadlineDay += 179 + ((7 - startMonth) + (endMonth - 1))*31 - 365;


                }else if (7 <= endMonth && endMonth <=11){



                }else if (endMonth == 12){



                }


            }else if (7 <= startMonth && startMonth <=11){
                if (1 <= endMonth && endMonth <= 6){
                    deadlineDay += (13 - startMonth)*30 - 1 + (endMonth - 1)*31 - 365;


                }else if (7 <= endMonth && endMonth <=11){
                    deadlineDay += 215 + ((12 - startMonth) + (endMonth - 7))*30 - 365;



                }else if (endMonth == 12){

                }


            }else if (startMonth == 12){
                if (1 <= endMonth && endMonth <= 6){
                    deadlineDay += (13 - startMonth)*30 - 1 + (endMonth - 1)*31 - 365;


                }else if (7 <= endMonth && endMonth <=11){
                    deadlineDay += 186 + (endMonth - 6)*30 - 1 - 365;


                }else if (endMonth == 12){


                }
            }

        }else if (difMonth == 0){
            deadlineDay += difDay;
            deadlineDay += difYear;
        }

        SharedPreferences getDeadline = getActivity().getSharedPreferences("deadline", Context.MODE_PRIVATE);
        getDeadlineDay = getDeadline.getInt("deadlineDay", deadlineDay);

        deadline_seeknumber.setText(valueOf(getDeadlineDay));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences deadlineSHR = getActivity().getSharedPreferences("deadline", Context.MODE_PRIVATE);
                SharedPreferences.Editor deadlineEditor = deadlineSHR.edit();

                deadline_seeknumber.setText(valueOf(getDeadlineDay));
                getDeadlineDay--;
                deadlineEditor.putInt("deadlineDay", getDeadlineDay).apply();
            }
        }, 86400000);



        seekbarValue = (100 * deadlineDay)/bundle.getInt("deadline");

        deadline_seekbar.setProgress(seekbarValue);

        projectOwnerUserName = getActivity().getSharedPreferences("projectOwnerUsername", Context.MODE_PRIVATE);
        projectIndex = getActivity().getSharedPreferences("projectIndex", Context.MODE_PRIVATE);

        Log.e("projetIndex", "_____________________________" + projectIndex.getString("projectIndex", ""));
        String proIndex = projectIndex.getString("projectIndex", "");
        Log.e("projectOwnerUsername","_____________________________" + projectOwnerUserName.getString("projectOwnerUsername", "null"));
        String proUsername = projectOwnerUserName.getString("projectOwnerUsername", "null");
        Log.e("token", "" + token);

        deadline_upload_finalFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("sss", "onClick");
                int permission = ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    Log.i("sss", "!PERMISSION_GRANTED");
                    requestPermissions(
                            PERMISSIONS_STORAGE,
                            REQUEST_EXTERNAL_STORAGE
                    );
                    return;
                } else {
                    Log.i("sss", "PERMISSION_GRANTED");
                    chooseFile();
                }

            }
        });
        deadline_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Client client = ServiceGenerator.createService(Client.class);
                File file = null;
                RequestBody requestFile = null;
                MultipartBody.Part body = null;
                if (userProfileImagePath != null) {
                    try {
                        file = new File(userProfileImagePath);
                        Uri uri = Uri.fromFile(file);
                        if (file.exists()) {
                            Log.i("finaltest", "found");
                            requestFile =
                                    RequestBody.create(MediaType.parse(getMimeType(uri)), file);
                            body =
                                    MultipartBody.Part.createFormData("finalFile", file.getName(), requestFile);
                        } else {
                            Log.i("finaltest", "not found");
                        }
                    } catch (Exception e) {

                    }
                }

                RequestBody proIndexRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectIndex.getString("projectIndex", "").trim());
                RequestBody proUsernameRequestBody = RequestBody.create(MediaType.parse("text/plain"), projectOwnerUserName.getString("projectOwnerUsername", "").trim());
                RequestBody finalLinkRequestBody = RequestBody.create(MediaType.parse("text/plain"), add_linktext.getText().toString().trim());


                if (add_linktext.getText().toString().equals("")){
                    call = client.sendFinalProject(
                            token,
                            proUsernameRequestBody,
                            proIndexRequestBody,
                            finalLinkRequestBody,
                            body);
                }else {
                    call = client.sendFinalProject(
                            token,
                            proUsernameRequestBody,
                            proIndexRequestBody,
                            finalLinkRequestBody,
                            body);
                }

                call.enqueue(new Callback<AddProjectResponse>() {
                    @Override
                    public void onResponse(Call<AddProjectResponse> call,
                                           Response<AddProjectResponse> response) {
                        Log.e("xcxc", "code:" + response.code());
                        Log.e("eeeeee", response.body() + "");
                        if (response.isSuccessful()) {
                            Log.e("xcxc", "success:");
                            popupForSuccessAddingFinalFile();
                        } else {
                            popupForErrorAddingFinalFile();
                        }
                    }

                    @Override
                    public void onFailure(Call<AddProjectResponse> call, Throwable t) {
                        Log.e("test123", t.getMessage());
                    }
                });

            }
        });

        return view;
    }

    public void chooseFile(){

        Intent i = new Intent(getActivity(), FilePickerActivity.class);
        // This works if you defined the intent filter
        // Intent i = new Intent(Intent.ACTION_GET_CONTENT);

        // Set these depending on your use case. These are the defaults.
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);

        // Configure initial directory by specifying a String.
        // You could specify a String like "/storage/emulated/0/", but that can
        // dangerous. Always use Android's API calls to get paths to the SD-card or
        // internal memory.
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());

        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
            deadline_filetitle.setText("فایل با موفقیت ضمیمه شد!");

            Uri selectedImageUri = data.getData();

            if ("content".equalsIgnoreCase(selectedImageUri.getScheme())) {
                String[] projection = { "_data" };
                Cursor cursor = null;

                try {
                    cursor = getContext().getContentResolver().query(selectedImageUri, projection, null, null, null);
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        userProfileImagePath = cursor.getString(column_index);
                    }
                } catch (Exception e) {
                    // Eat it
                }
            }
            else if ("file".equalsIgnoreCase(selectedImageUri.getScheme())) {
                userProfileImagePath = selectedImageUri.getPath();
            }

            Log.e("file path", userProfileImagePath);
            Log.e("uri", selectedImageUri.toString());

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImageUri);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
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

    public void popupForSuccessAddingFinalFile(){
        pDialog1 = new PrettyDialog(getActivity());
        pDialog1.setCancelable(false);
        pDialog1
                .setTitle("تبریک!")
                .setMessage("فایل نهایی شما با موفقیت ارسال شد")
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
                                ProjectsFragment projectsFragment = new ProjectsFragment();
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                manager.beginTransaction().replace(R.id.mycontainer, projectsFragment).commit();
                            }
                        }
                )
                .show();
    }

    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
        }else{
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

    public void popupForErrorAddingFinalFile(){
        pDialog3 = new PrettyDialog(getActivity());
        pDialog3.setCancelable(false);
        pDialog3
                .setTitle("خطا!")
                .setMessage("مشکلی در ارسال فایل بوجود آمده است")
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
                                pDialog3.dismiss();
                            }
                        }
                )
                .show();
    }

}
