package com.example.nostalgia;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ImagePickerFragment extends DialogFragment {

    public static final String ARG_PHOTO = "photo_id";

    public static ImagePickerFragment getInstance(Bitmap photoBitmap) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_PHOTO, photoBitmap);
        ImagePickerFragment imagePickerFragment = new ImagePickerFragment();
        imagePickerFragment.setArguments(args);

        return imagePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo, null);
        Bitmap bitmap = (Bitmap) getArguments().getParcelable(ARG_PHOTO);
        ImageView mImageView = v.findViewById(R.id.dialog_imageview);
        mImageView.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setView(v).show();
    }
}
