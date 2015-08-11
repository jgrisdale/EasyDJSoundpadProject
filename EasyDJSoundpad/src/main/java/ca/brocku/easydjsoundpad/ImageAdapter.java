package ca.brocku.easydjsoundpad;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public void setImage(int position, int image){
        mThumbIds[position] = image;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            //change the size of the images here
            imageView.setLayoutParams(new GridView.LayoutParams(80, 100));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(0, 0, 0, 0);
        } else {
            imageView = (ImageView) convertView;
        }

        imageView.setImageResource(mThumbIds[position]);
        return imageView;
    }

    // references to our images
    public Integer[] mThumbIds = {
            R.drawable.button, R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button,R.drawable.button,
            R.drawable.button
    };
}