package com.rsdesign.wallpaper.view.fragment;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.transition.Fade;
import androidx.transition.TransitionManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.FragmentPhotoViewBinding;
import com.rsdesign.wallpaper.view.MainActivity;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class PhotoViewFragment extends Fragment {

    FragmentPhotoViewBinding photoViewBinding;
    private boolean showDetails = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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
               /* String url = "https://img.freepik.com/free-photo/black-t-shirts-with-copy-space_53876-102012.jpg?t=st=1654632155~exp=1654632755~hmac=28c8be6d8a5c32da11f993d19d383f9c1de121684325af0eccbdb0d1efa11bf1&w=1060";
                Glide.with(getContext())
                        .asBitmap()
                        .load(url).into(new SimpleTarget<Bitmap>() {
                            @Override
                            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                //WallpaperManager.getInstance(getActivity().getApplicationContext()).setBitmap(resource);
                                try {
                                    // set the wallpaper by calling the setResource function and
                                    // passing the drawable file
                                    wallpaperManager.setBitmap(resource);
                                } catch (IOException e) {
                                    // here the errors can be logged instead of printStackTrace
                                    e.printStackTrace();
                                }
                            }

                        });*/


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