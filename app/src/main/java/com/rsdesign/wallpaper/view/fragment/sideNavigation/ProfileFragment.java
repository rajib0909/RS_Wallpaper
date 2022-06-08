package com.rsdesign.wallpaper.view.fragment.sideNavigation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapter;
import com.rsdesign.wallpaper.databinding.FragmentProfileBinding;
import com.rsdesign.wallpaper.model.Result;
import com.rsdesign.wallpaper.view.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding profileBinding;
    private List<Result> results;
    private ShowAllPhotoAdapter allPhotoAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Inflate the layout for this fragment
        profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);

        allPhotoAdapter = new ShowAllPhotoAdapter(new ArrayList<>(), getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        profileBinding.photoList.setLayoutManager(layoutManager);
        profileBinding.photoList.setAdapter(allPhotoAdapter);


        allPhotoAdapter.setOnClickPhoto(id -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_view_photo);
        });

        results = new ArrayList<>();
        for (int i =0; i<10;i++){
            Result result = new Result();
            result.setTitle("test");
            results.add(result);
        }

        allPhotoAdapter.updatePhotoList(results);
        allPhotoAdapter.notifyDataSetChanged();

        return profileBinding.getRoot();
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