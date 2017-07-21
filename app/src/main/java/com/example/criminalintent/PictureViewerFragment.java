package com.example.criminalintent;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;

/**
 * Created by jarod on 6/8/2017.
 */

public class PictureViewerFragment extends DialogFragment{
    private ImageView mZoomImageView;
    private File mPhotoFile;
    public static final String ARG_FILE = "file";

    public static PictureViewerFragment newInstance(File file){
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE,file);
        PictureViewerFragment fragment = new PictureViewerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPhotoFile = (File) getArguments().getSerializable(ARG_FILE);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_image,null);
        mZoomImageView = (ImageView) v.findViewById(R.id.zoom_crime_photo);
        if(mPhotoFile == null || !mPhotoFile.exists()){
            mZoomImageView.setImageDrawable(null);
        }else{
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            mZoomImageView.setImageBitmap(bitmap);
        }
        return v;

    }
}
