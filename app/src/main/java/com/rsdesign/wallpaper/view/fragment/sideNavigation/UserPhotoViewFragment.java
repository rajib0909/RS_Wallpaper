package com.rsdesign.wallpaper.view.fragment.sideNavigation;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.FragmentUserPhotoViewBinding;

public class UserPhotoViewFragment extends Fragment {

    FragmentUserPhotoViewBinding photoViewBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        photoViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_photo_view, container, false);

        return photoViewBinding.getRoot();
    }
}