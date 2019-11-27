package group.tamasha.rockaar;


import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.nononsenseapps.filepicker.Utils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AddFragment extends Fragment {

    String token, userProfileImagePath;
    Button add;
    PrettyDialog pDialog1;
    PrettyDialog pDialog2;
    PrettyDialog pDialog3;
    PrettyDialog pDialog4, pDialog5;
    EditText name, amount, deadline, description;
    Spinner category;
    ImageView add_attachback, imageToUpload, upload;
    SharedPreferences Token;
    private static int RESULT_LOAD_IMAGE = 1;
    TextView add_attachtitle;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public AddFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        popupForCheckInternet();

        add =(Button)view.findViewById(R.id.add_button);
        name = (EditText)view.findViewById(R.id.add_titletext);
        amount = (EditText)view.findViewById(R.id.add_pricenumber);
        description = (EditText)view.findViewById(R.id.add_infotext);
        category = (Spinner)view.findViewById(R.id.add_spinnercategory);
        deadline = (EditText)view.findViewById(R.id.add_timenumber);
        imageToUpload = (ImageView)view.findViewById(R.id.add_imageToUpload);
        add_attachtitle = (TextView)view.findViewById(R.id.add_attachtitle);
        upload = (ImageView)view.findViewById(R.id.add_upload);

        imageToUpload.setVisibility(View.GONE);

        add_attachback = (ImageView)view.findViewById(R.id.add_attachback);

        upload.setOnClickListener(new View.OnClickListener() {
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
                    openImagePicker();
                }
            }
        });


        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        add.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("") ||
                        description.getText().toString().equals("") ||
                        deadline.getText().toString().equals("")) {
                    popupForErrorEmptyField();
                } else {



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
                                        MultipartBody.Part.createFormData("projectFile", file.getName(), requestFile);
                            } else {
                                Log.i("finaltest", "not found");
                                pDialog5 = new PrettyDialog(getActivity());
                                pDialog5
                                        .setTitle("خطا!")
                                        .setMessage("حجم فایل مورد نظر زیاد می باشد")
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
                                                        pDialog5.dismiss();
                                                    }
                                                }
                                        )
                                        .show();
                            }
                        } catch (Exception e) {

                        }
                    }

                    long Amount;

                    String T = amount.getText().toString();
                    String Text = T.replaceAll("[^0-9]+","");

                    RequestBody nameRequestBody = RequestBody.create(MediaType.parse("text/plain"), name.getText().toString().trim());
                    if (amount.getText().toString().equals("")){
                        Amount = 0;
                    } else {
                        Amount = Long.parseLong(Text);
                    }

                    RequestBody descriptionRequestBody = RequestBody.create(MediaType.parse("text/plain"), description.getText().toString().trim());
                    RequestBody categoryRequestBody = RequestBody.create(MediaType.parse("text/plain"), category.getSelectedItem().toString());
                    RequestBody deadlineRequestBody = RequestBody.create(MediaType.parse("text/plain"), deadline.getText().toString().trim());
                    Call<AddProjectResponse> call = client.addProject(
                            token,
                            body,
                            Amount,
                            nameRequestBody,
                            descriptionRequestBody,
                            categoryRequestBody,
                            deadlineRequestBody);

                    call.enqueue(new Callback<AddProjectResponse>() {
                        @Override
                        public void onResponse(Call<AddProjectResponse> call,
                                               Response<AddProjectResponse> response) {
                            Log.e("xcxc", "code:" + response.code());
                            if (response.isSuccessful()) {
                                Log.e("xcxc", "success:");
                                popupForSuccessAddingProject();
                            } else {
                                Toast.makeText(getContext(), "مشکلی در ثبت پروژه به وجود آمد!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AddProjectResponse> call, Throwable t) {
                            Log.e("test123", t.getMessage());
                            popupForErrorToAddProject();
                        }
                    });
                }
            }
        });

        amount.addTextChangedListener(onTextChangedListener());

        return view;
    }

    public void openImagePicker() {
        Log.i("sss", "call");
//        Intent i = new Intent(
//                Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, RESULT_LOAD_IMAGE);






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

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContext().getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == -1 && null != data) {
            add_attachtitle.setText("فایل با موفقیت ضمیمه شد!");

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

            Log.d("file path", userProfileImagePath);
            Log.d("uri", selectedImageUri.toString());

            Bitmap bmp = null;
            try {
                bmp = getBitmapFromUri(selectedImageUri);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
//            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
//                // For JellyBean and above
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                    ClipData clip = data.getClipData();
//
//                    if (clip != null) {
//                        for (int i = 0; i < clip.getItemCount(); i++) {
//                            Uri uri = clip.getItemAt(i).getUri();
//                            // Do something with the URI
//                        }
//                    }
//                    // For Ice Cream Sandwich
//                } else {
//                    ArrayList<String> paths = data.getStringArrayListExtra
//                            (FilePickerActivity.EXTRA_PATHS);
//
//                    if (paths != null) {
//                        for (String path: paths) {
//                            Uri uri = Uri.parse(path);
//                            // Do something with the URI
//                        }
//                    }
//                }
//
//            } else {
//                Uri uri = data.getData();
//                // Do something with the URI
//
//                add_attachtitle.setText("فایل با موفقیت ضمیمه شد!");
//
//                Uri selectedImageUri = data.getData();
//
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//
//                Cursor cursor = getContext().getContentResolver().query(selectedImageUri,
//                        filePathColumn, null, null, null);
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                userProfileImagePath = cursor.getString(columnIndex);
//                cursor.close();
//
//                Bitmap bmp = null;
//                try {
//                    bmp = getBitmapFromUri(selectedImageUri);
//                } catch (IOException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    openImagePicker();
                } else {
                    // Permission Denied
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void popupForErrorEmptyField(){
        pDialog2 = new PrettyDialog(getActivity());
        pDialog2.setCancelable(false);
        pDialog2
                .setTitle("خطا!")
                .setMessage("فیلدهای عنوان، مدت زمان انجام و توضیحات نباید خالی باشد")
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
                                pDialog2.dismiss();
                            }
                        }
                )
                .show();
    }

    public void popupForErrorToAddProject(){
        pDialog1 = new PrettyDialog(getActivity());
        pDialog1.setCancelable(false);
        pDialog1
                .setTitle("خطا!")
                .setMessage("مشکلی در ثبت پروژه پیش آمده است")
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
                                pDialog1.dismiss();
                            }
                        }
                )
                .show();

    }

    public void popupForSuccessAddingProject(){
//        new AwesomeSuccessDialog(getContext())
//                .setTitle("ثبت با موفقیت انجام شد!")
//                .setMessage("پروژه شما در حال بررسی توسط ادمین می باشد")
//                .setPositiveButtonText(getString(R.string.dialog_ok_button))
//                .setPositiveButtonClick(new Closure() {
//                    @Override
//                    public void exec() {
//                        ProjectsFragment projectsFragment = new ProjectsFragment();
//                        FragmentManager manager = getActivity().getSupportFragmentManager();
//                        manager.beginTransaction().replace(R.id.mycontainer, projectsFragment).commit();
//                    }
//                })
//                .show();
        pDialog3 = new PrettyDialog(getActivity());
        pDialog3.setCancelable(false);
        pDialog3
                .setTitle("ثبت با موفقیت انجام شد!")
                .setMessage("پروژه شما در حال بررسی توسط ادمین می باشد")
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
                                pDialog3.dismiss();
                                ProjectsFragment projectsFragment = new ProjectsFragment();
                                FragmentManager manager = getActivity().getSupportFragmentManager();
                                manager.beginTransaction().replace(R.id.mycontainer, projectsFragment).commit();
                            }
                        }
                )
                .show();
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

    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
        }else{
            pDialog4 = new PrettyDialog(getActivity());
            pDialog4.setCancelable(false);
            pDialog4
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
                                    pDialog4.dismiss();
                                }
                            }
                    )
                    .show();

        }

    }

    private TextWatcher onTextChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                amount.removeTextChangedListener(this);

                try {
                    String originalString = s.toString();

                    Long longval;
                    if (originalString.contains(",")) {
                        originalString = originalString.replaceAll(",", "");
                    }
                    longval = Long.parseLong(originalString);

                    DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
                    formatter.applyPattern("#,###,###,###");
                    String formattedString = formatter.format(longval);

                    //setting text after format to EditText
                    amount.setText(formattedString);
                    amount.setSelection(amount.getText().length());
                } catch (NumberFormatException nfe) {
                    nfe.printStackTrace();
                }

                amount.addTextChangedListener(this);
            }
        };
    }

}
