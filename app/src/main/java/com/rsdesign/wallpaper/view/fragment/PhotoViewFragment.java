package com.rsdesign.wallpaper.view.fragment;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.FragmentPhotoViewBinding;
import com.rsdesign.wallpaper.view.MainActivity;

import java.io.IOException;


public class PhotoViewFragment extends Fragment {

    FragmentPhotoViewBinding photoViewBinding;
    private boolean showDetails = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        photoViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_view, container, false);
        photoViewBinding.btnBack.setOnClickListener(l -> getActivity().onBackPressed());
        // creating the instance of the WallpaperManager
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());

        photoViewBinding.btnArrow.setOnClickListener(l -> {
            if (showDetails) {
                showDetails = false;
                photoViewBinding.btnArrow.setImageResource(R.drawable.ic_arrow_up);
                photoViewBinding.detailsView.setVisibility(View.GONE);
                photoViewBinding.reportPhoto.setVisibility(View.GONE);
            } else {
                showDetails = true;
                photoViewBinding.btnArrow.setImageResource(R.drawable.ic_arrow_down);
                photoViewBinding.detailsView.setVisibility(View.VISIBLE);
                // photoViewBinding.detailsView.animate().translationY(0);
                photoViewBinding.reportPhoto.setVisibility(View.VISIBLE);
            }
        });


        photoViewBinding.btnSetWallpaper.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onClick(View v) {
                try {
                    // set the wallpaper by calling the setResource function and
                    // passing the drawable file
                    wallpaperManager.setResource(R.drawable.demo_image);
                } catch (IOException e) {
                    // here the errors can be logged instead of printStackTrace
                    e.printStackTrace();
                }
            }
        });


        return photoViewBinding.getRoot();
    }


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.hideBottomNav();
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.showBottomNav();
    }
}