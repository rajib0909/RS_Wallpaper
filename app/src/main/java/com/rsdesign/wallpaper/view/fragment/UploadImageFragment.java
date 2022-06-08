package com.rsdesign.wallpaper.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.FragmentUploadImageBinding;
import com.rsdesign.wallpaper.view.MainActivity;

public class UploadImageFragment extends Fragment {


    FragmentUploadImageBinding uploadImageBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        uploadImageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_image, container, false);

        uploadImageBinding.choosePhoto.setOnClickListener(l->{
            ImagePicker.Companion.with(this)
                    .crop(9f, 16f)                   //Crop image(Optional), Check Customization for more option
                    .compress(500)            //Final image size will be less than 1 MB(Optional)
                    .galleryOnly()
                    //.maxResultSize(512, 512)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });

        return uploadImageBinding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            //  selectPhoto = true;
            Intent fileUri = data;

            Uri uri = data.getData();

            uploadImageBinding.srcImage.setVisibility(View.VISIBLE);
            uploadImageBinding.srcImage.setImageURI(uri);
            //You can get File object from intent
           // file = ImagePicker.Companion.getFile(data);


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Log.d("tanvir", "Task Cancelled");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        MainActivity.hideBottomNav();

    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
        MainActivity.showBottomNav();

    }
}