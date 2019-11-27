package group.tamasha.rockaar;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class SupportFragment extends Fragment {


    public SupportFragment() {
        // Required empty public constructor
    }

    Button MakeCall, OpenWeb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_support, container, false);

        MakeCall = (Button)view.findViewById(R.id.support_makecall);
        OpenWeb = (Button)view.findViewById(R.id.support_openweb);

        MakeCall.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:+989016081024"));
                startActivity(callIntent);
            }});

        OpenWeb.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://raakar.ir"));
                startActivity(intent);
            }});

        return view;
    }

}
