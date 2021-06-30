package my.project.nostalgia.supplementary;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MediaAndURI {

    private Context mContext;

    public MediaAndURI(Context context){
        this.mContext = context;
    }
    public MediaAndURI(){}

    public Uri getMediaUriOf(String mediaPath){
        return FileProvider.getUriForFile(mContext,mContext.getPackageName()+".fileprovider",new File(mediaPath));
    }
    public boolean isThisImageFile(String path) {
        String mimeType = getMimeType(path);
        return mimeType != null && mimeType.startsWith("image");
    }
    public boolean isThisVideoFile(String path) {
        String mimeType = getMimeType(path);
        return mimeType != null && mimeType.startsWith("video");
    }
    private String getMimeType(String path) {
        String mimeType = "";
        String extension = getExtension(path);
        if (MimeTypeMap.getSingleton().hasExtension(extension)) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return mimeType;
    }
    private String getExtension(String fileName){
        char[] arrayOfFilename = fileName.toCharArray();
        for(int i = arrayOfFilename.length-1; i > 0; i--){
            if(arrayOfFilename[i] == '.'){
                return fileName.substring(i+1);
            }
        }
        return "";
    }
    public ArrayList<Uri> getUrisFromPaths(String[] mediaPaths) {
        ArrayList<Uri> mediaUri = new ArrayList<>();
        for (String path : mediaPaths) {
            File file = new File(path);
            Uri uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
            mediaUri.add(uri);
        }
        return mediaUri;
    }
    public boolean isDuplicate(String string, String[] strings) {
        return Arrays.asList(strings).contains(string);
    }
    public Intent getFromMediaIntent() {
        Intent getmoreImage = new Intent(Intent.ACTION_GET_CONTENT);
        getmoreImage.setType("*/*");
        getmoreImage.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        getmoreImage.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        return getmoreImage;
    }
    /**
     * Creates an intent which allows user to share the memory: photos/videos and title.
     * @param mediaUri list of all Uri which contain filepaths of photos and videos of a memory.
     */
    public Intent shareMemoryIntent(ArrayList<Uri> mediaUri, String title) {
        Intent share = new Intent(Intent.ACTION_SEND_MULTIPLE);
        share.putParcelableArrayListExtra(Intent.EXTRA_STREAM, mediaUri);
        share.setType("*/*");
        share.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
        share.putExtra(Intent.EXTRA_TEXT, title);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        return share;
    }
    /**
     * @see #getSpecificContentUri(Uri)
     * @see #getSelectionArgumentsForCursor(Uri)
     * @see #isImage(Uri)
     * @see #getMimeType(Uri)
     * @return String containing filepath
     */
    public String getMediaPathFromUri(Uri mMediaUri) {

        String imageEncoded;
        Uri contentUri;
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        String[] selectionArgs = getSelectionArgumentsForCursor(mMediaUri);
        String selection = "_id=?";
        contentUri = getSpecificContentUri(mMediaUri);

        Cursor cursor = mContext.getContentResolver().query(contentUri,filePathColumn, selection, selectionArgs, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
        imageEncoded  = cursor.getString(columnIndex);
        cursor.close();

        return imageEncoded;
    }
    private Uri getSpecificContentUri(Uri mImageUri) {
        if(isImage(mImageUri))
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        else
            return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
    }
    private boolean isImage(Uri mImageUri) {
        return getMimeType(mImageUri).startsWith("image");
    }
    private String getMimeType(Uri uri) {
        String mimeType;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = mContext.getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }
        return mimeType;
    }
    private String[] getSelectionArgumentsForCursor(Uri mImageUri) {
        String docId = DocumentsContract.getDocumentId(mImageUri);
        String[] split = docId.split(":");
        return new String[] {split[1]};
    }
}
