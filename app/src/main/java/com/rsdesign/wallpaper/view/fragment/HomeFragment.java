package com.rsdesign.wallpaper.view.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapter;
import com.rsdesign.wallpaper.databinding.FragmentHomeBinding;
import com.rsdesign.wallpaper.model.Result;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    FragmentHomeBinding homeBinding;
    private List<Result> results;
    private ShowAllPhotoAdapter allPhotoAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
       // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);





        allPhotoAdapter = new ShowAllPhotoAdapter(new ArrayList<>(), getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        homeBinding.photoList.setLayoutManager(layoutManager);
        homeBinding.photoList.setAdapter(allPhotoAdapter);


        allPhotoAdapter.setOnClickPhoto(new ShowAllPhotoAdapter.OnClickPhoto() {
            @Override
            public void onClickPhoto(int id) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_view_photo);
            }
        });


        homeBinding.uploadImageButton.setOnClickListener(l->{
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_upload_image);
        });

        results = new ArrayList<>();
        for (int i =0; i<10;i++){
            Result result = new Result();
            result.setTitle("test");
            results.add(result);
        }

        allPhotoAdapter.updatePhotoList(results);
        allPhotoAdapter.notifyDataSetChanged();


        return homeBinding.getRoot();
    }
}