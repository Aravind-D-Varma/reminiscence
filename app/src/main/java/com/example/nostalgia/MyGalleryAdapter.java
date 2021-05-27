package com.example.nostalgia;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyGalleryAdapter extends RecyclerView.Adapter<MyGalleryAdapter.ViewHolder> {

    Context context;
    List<Bitmap> photos;
    String[] mediaPaths;
    List<URI> videos;
    private static final int IMAGE = 0;
    private static final int VIDEO = 1;

    public MyGalleryAdapter(Context applicationContext, String[] mediaPaths) {
        this.context = applicationContext;
        this.mediaPaths = mediaPaths;
        this.photos = getPhotoBM(mediaPaths);
        this.videos = getVideoURI(mediaPaths);
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.photogallery_item, parent, false);
        switch(viewType){
            case VIDEO:
                return new MyVideoViewHolder(v);
            case IMAGE:
                return new MyImageViewHolder(v);
        }
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch(holder.getItemViewType()){
            case VIDEO:
                holder.video.setVideoURI(videos.get(position));
                break;
            case IMAGE:
                holder.image.setImageBitmap(photos.get(position));
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        return photos.size();
    }
    
    @Override
    public int getItemViewType(int position) {
       if(isVideoFile(mediaPaths[position]))
            return VIDEO;
       else if((isVideoFile(mediaPaths[position]))
            return IMAGE;
       return IMAGE;
    }

    public class MyImageViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        public MyViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.memory_photo);
        }
    }
    public class MyVideoViewHolder extends RecyclerView.ViewHolder{
        VideoView video;
        public MyViewHolder(View itemView) {
            super(itemView);
            video = (VideoView) itemView.findViewById(R.id.memory_video);
        }
    }
                
    private List<Bitmap> getPhotoBM(String[] photoPaths) {
          
        photos = new ArrayList<Bitmap>();
        for (int i = 0; i < photoPaths.length; i++) {
            if(isImageFile(photoPaths[i])) {
                Bitmap bpimg = BitmapFactory.decodeFile(photoPaths[i]);
                photos.add(bpimg);
            }
        }
        return photos;
    }
  
    private List<URI> getVideoURI(String[] photoPaths) {
          
        videos = new ArrayList<URI>();
        for (int i = 0; i < photoPaths.length; i++) {
            if(isVideoFile(photoPaths[i])) {
                URiI uriVid = FileProvider.getUriForFile(context, context.getPackageName(), new File(photoPaths[i]));
                videos.add(bpimg);
            }
        }
        return videos;
    }          
               
    public static boolean isImageFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("image");
    }
    public static boolean isVideoFile(String path) {
        String mimeType = URLConnection.guessContentTypeFromName(path);
        return mimeType != null && mimeType.startsWith("video");
    }
}

