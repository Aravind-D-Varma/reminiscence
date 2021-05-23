package com.example.nostalgia;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import java.util.List;

public class CustomAdapter extends BaseAdapter {

    Context context;
    List<Bitmap> photos;
    LayoutInflater inflater;

    public CustomAdapter(Context applicationContext, List<Bitmap> photos) {
        this.context = applicationContext;
        this.photos = photos;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return photos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.photogallery_item, null);
        ImageView icon = (ImageView) convertView.findViewById(R.id.memory_photo);
        icon.setImageBitmap(photos.get(position));
        return convertView;
    }
}
