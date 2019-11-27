package group.tamasha.rockaar;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

import static group.tamasha.rockaar.Utilities.getCurrentShamsidate;
import static java.lang.String.valueOf;


public class MyWorkInfoFragment extends Fragment {

    PrettyDialog pDialog1;
    PrettyDialog pDialog2;
    PrettyDialog pDialog3;
    PrettyDialog pDialog4;

    TextView
            mwi_title,
            mwi_undertitle,
            mwi_infotext,
            mwi_start,
            mwi_end,
            mwi_seeknumber;

    SeekBar
            mwi_seekbar;

    ArrayList<OffererInformation> offererInformations = new ArrayList<OffererInformation>();

    int
            endDay,
            endMonth,
            endYear,
            startDay,
            startMonth,
            startYear,
            currentDay,
            currentMonth,
            currentYear,
            deadlineDay,
            seekbarValue;

    ImageView
            Download;

    TextView Link;

    SharedPreferences LaunchCounter;
    SharedPreferences.Editor LaunchCounterEditor;
    int Counter, status;

    String Farsi_Date=null;
    String English_Date=null;
    String temp ;

    int The_Year  ;
    int The_Month ;
    int The_Day   ;

    SharedPreferences Token, statusSHP, finalFile;

    String
            token,
            URLgetOfferDeadline = "https://raakar.ir/getOfferDeadline",
            projectDate,
            tempDateEn,
            currentTimeFa,
            finalFileLink;

    public MyWorkInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_work_info, container, false);

        final Bundle bundle = getArguments();

        popupForCheckInternet();

        mwi_seekbar = (SeekBar)view.findViewById(R.id.mwi_seekbar);

        Download = (ImageView)view.findViewById(R.id.mwi_download);
        Link = (TextView)view.findViewById(R.id.mwi_linktitle);

        currentTimeFa = getCurrentShamsidate();

        String[] separated = currentTimeFa.split("/");
        currentYear = Integer.parseInt(separated[0]);
        currentMonth = Integer.parseInt(separated[1]);
        currentDay = Integer.parseInt(separated[2]);

        String T = bundle.getString("amount");
        String Text = T.replaceAll("[^0-9]+","");
        long Number = Long.parseLong(Text);
        DecimalFormat formatter = new DecimalFormat("#,###,###,###,###");
        String yourFormattedString = formatter.format(Number);

        Token = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        token = Token.getString("token", null);

        mwi_title = (TextView)view.findViewById(R.id.mwi_title);
        mwi_title.setText(bundle.getString("name"));

        mwi_undertitle = (TextView)view.findViewById(R.id.mwi_undertitle);
        mwi_undertitle.setText(yourFormattedString + " تومان (" + bundle.getInt("deadline") + " روز )");

        mwi_infotext = (TextView)view.findViewById(R.id.mwi_infotext);
        mwi_infotext.setText(bundle.getString("description"));

        startDay = bundle.getInt("acceptionDate");
        startMonth = bundle.getInt("acceptionMonth");
        startYear = bundle.getInt("acceptionYear");

        mwi_start = (TextView)view.findViewById(R.id.mwi_start);
        mwi_start.setText(startYear + "/" + startMonth + "/" + startDay);

        endDay = bundle.getInt("acceptionDate") + bundle.getInt("deadline");
        endMonth = bundle.getInt("acceptionMonth");
        endYear = bundle.getInt("acceptionYear");

        projectDate = bundle.getString("projectDate");

        offererInformations.add(new OffererInformation(bundle.getString("creatorName"),
                bundle.getString("category"),
                bundle.getString("creatorUsername"),
                bundle.getString("projectIndex")));

        SharedPreferences creatorNameSHP = getActivity().getSharedPreferences("creatorName", Context.MODE_PRIVATE);
        SharedPreferences.Editor creatorNameSHPEdi = creatorNameSHP.edit();
        creatorNameSHPEdi.putString("creatorName",bundle.getString("creatorName"));
        creatorNameSHPEdi.apply();

        SharedPreferences categorySHP = getActivity().getSharedPreferences("category", Context.MODE_PRIVATE);
        SharedPreferences.Editor categorySHPEdi = categorySHP.edit();
        categorySHPEdi.putString("category",bundle.getString("category"));
        categorySHPEdi.apply();

        SharedPreferences projectOwnerUsernameSHP = getActivity().getSharedPreferences("creatorUsername", Context.MODE_PRIVATE);
        SharedPreferences.Editor projectOwnerUsernameSHPEdi = projectOwnerUsernameSHP.edit();
        projectOwnerUsernameSHPEdi.putString("creatorUsername",bundle.getString("creatorUsername"));
        projectOwnerUsernameSHPEdi.apply();

        SharedPreferences projectIndexSHP = getActivity().getSharedPreferences("projectIndex", Context.MODE_PRIVATE);
        SharedPreferences.Editor projectIndexSHPEdi = projectIndexSHP.edit();
        projectIndexSHPEdi.putString("projectIndex",bundle.getString("projectIndex"));
        projectIndexSHPEdi.apply();

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

        mwi_end = (TextView)view.findViewById(R.id.mwi_end);
        mwi_end.setText(endYear + "/" + endMonth + "/" + endDay);


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

//        deadlineDay -= 1;

        mwi_seeknumber = (TextView)view.findViewById(R.id.mwi_seeknumber);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences deadlineSHR = getActivity().getSharedPreferences("deadlineMyWork", Context.MODE_PRIVATE);
                SharedPreferences.Editor deadlineEditor = deadlineSHR.edit();

                SharedPreferences getDeadline = getActivity().getSharedPreferences("deadlineMyWork", Context.MODE_PRIVATE);
                int getDeadlineDay = getDeadline.getInt("deadlineMyWork", deadlineDay);
                mwi_seeknumber.setText(String.valueOf(deadlineDay));
                getDeadlineDay--;
                deadlineEditor.putInt("deadlineMyWork", getDeadlineDay).apply();
            }
        }, 86400000);

        seekbarValue = (100 * deadlineDay)/bundle.getInt("deadline");

        mwi_seekbar.setProgress(seekbarValue);

        mwi_seekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        LaunchCounter = getActivity().getApplicationContext().getSharedPreferences("launch", Context.MODE_PRIVATE);
        LaunchCounterEditor = LaunchCounter.edit();

        statusSHP = getActivity().getSharedPreferences("statusMyWork", Context.MODE_PRIVATE);
        status = statusSHP.getInt("statusMyWork", 0);

        finalFile = getActivity().getSharedPreferences("finalLink", Context.MODE_PRIVATE);
        finalFileLink = finalFile.getString("finalLink", "لینکی برای نمایش وجود ندارد!");

        if (status == 5)
            Link.setText(finalFileLink);

        Download.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (status == 4)
                {
                    popUpForNotPayment();
                }
                if (status == 5){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalFileLink));
                    startActivity(intent);

                    Counter = LaunchCounter.getInt("launch",0);
                    Counter = 1;
                    LaunchCounter.edit().putInt("launch",Counter).apply();
                }else {
                    popUpForNotSendingFinalFile();
                }

            }});

        Link.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                if (status == 4)
                {
                    popUpForNotPayment();
                }
                if (status == 5){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(finalFileLink));
                    startActivity(intent);

                    Counter = LaunchCounter.getInt("launch",0);
                    Counter = 1;
                    LaunchCounter.edit().putInt("launch",Counter).apply();
                }else {
                    popUpForNotSendingFinalFileLink();
                }

            }});

        return view;
    }

    public void popupForCheckInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(getActivity().CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiInfo != null && wifiInfo.isConnected()) || (mobileInfo != null && mobileInfo.isConnected())){
        }else{
            pDialog1 = new PrettyDialog(getActivity());
            pDialog1.setCancelable(false);
            pDialog1
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
                                    pDialog1.dismiss();

                                }
                            }
                    )
                    .show();
        }

    }

    public void popUpForNotSendingFinalFile(){
        pDialog2 = new PrettyDialog(getActivity());
        pDialog2.setCancelable(false);
        pDialog2
                .setTitle("")
                .setMessage("فایل نهایی ارسال نشده یا در انتظار تأیید ادمین است")
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

    public void popUpForNotSendingFinalFileLink(){
        pDialog3 = new PrettyDialog(getActivity());
        pDialog3.setCancelable(false);
        pDialog3
                .setTitle("")
                .setMessage("لینک فایل نهایی ارسال نشده یا در انتظار تأیید ادمین است")
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
                                pDialog3.dismiss();
                            }
                        }
                )
                .show();
    }

    public void popUpForNotPayment(){
        pDialog4 = new PrettyDialog(getActivity());
        pDialog4.setCancelable(false);
        pDialog4
                .setTitle("")
                .setMessage("پرداخت نهایی انجام نشده است! از طریق منوی پیام ها، پرداخت نهایی را انجام دهید")
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
                                pDialog4.dismiss();
                            }
                        }
                )
                .show();
    }

}
