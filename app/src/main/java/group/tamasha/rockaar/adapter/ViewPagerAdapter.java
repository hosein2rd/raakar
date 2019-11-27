package group.tamasha.rockaar.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import group.tamasha.rockaar.R;

public class ViewPagerAdapter extends PagerAdapter {
    // Declare Variables
    Context context;
    String[] name;
    String[] describe;
    int[] back;
    LayoutInflater inflater;

    public ViewPagerAdapter(Context context,
                            String[] name,
                            String[] describe,
                            int[] back) {
        this.context = context;
        this.name = name;
        this.describe = describe;
        this.back = back;
    }

    @Override
    public int getCount() {
        return name.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        // Declare Variables
        TextView txtname;
        TextView txtdescribe;
        ImageView imgflag;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item, container,
                false);

        // Locate the TextViews in item.xml
        txtname = (TextView) itemView.findViewById(R.id.name);
        txtdescribe = (TextView) itemView.findViewById(R.id.describe);

        // Capture position and set to the TextViews
        txtname.setText(name[position]);
        txtdescribe.setText(describe[position]);

        // Locate the ImageView in activity_item.xml
        imgflag = (ImageView) itemView.findViewById(R.id.back);
        // Capture position and set to the ImageView
        imgflag.setImageResource(back[position]);

        // Add item.xml to ViewPager
        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove item.xml from ViewPager
        ((ViewPager) container).removeView((RelativeLayout) object);

    }
}

//public class ViewPagerAdapter extends PagerAdapter {
//
//    int[] image;
//    LayoutInflater Inflater;
//    Context context;
//
//    public ViewPagerAdapter(MainActivity mainActivity,int[] img)
//    {
//        this.context=mainActivity;
//        this.image=img;
//    }
//
//    @Override
//    public int getCount() {
//        return image.length;
//    }
//
//    @Override
//    public boolean isViewFromObject(View view, Object object) {
//        return view== object;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        ImageView trailing;
//        Inflater=(LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//        View itemview=Inflater.inflate(R.layout.item,container,false);
//        trailing=(ImageView) itemview.findViewById(R.id.trailing);
//        trailing.setImageResource(image[position]);
//
//        container.addView(itemview);
//        return itemview;
//    }
//
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        container.removeView((RelativeLayout)object);
//    }
//}
