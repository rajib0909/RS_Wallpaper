package com.rsdesign.wallpaper.view.fragment;

import android.os.Bundle;

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
import com.rsdesign.wallpaper.databinding.FragmentTrendingBinding;
import com.rsdesign.wallpaper.model.Result;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    FragmentTrendingBinding trendingBinding;

    private List<Result> results;
    private ShowAllPhotoAdapter allPhotoAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        trendingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trending, container, false);
        trendingBinding.btnBack.setOnClickListener(l-> getActivity().onBackPressed());

        allPhotoAdapter = new ShowAllPhotoAdapter(new ArrayList<>(), getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        trendingBinding.trendingPostList.setLayoutManager(layoutManager);
        trendingBinding.trendingPostList.setAdapter(allPhotoAdapter);

        allPhotoAdapter.setOnClickPhoto(new ShowAllPhotoAdapter.OnClickPhoto() {
            @Override
            public void onClickPhoto(int id) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_view_photo);
            }
        });

        results = new ArrayList<>();
        for (int i =0; i<10;i++){
            Result result = new Result();
            result.setTitle("test");
            results.add(result);
        }

        allPhotoAdapter.updatePhotoList(results);
        allPhotoAdapter.notifyDataSetChanged();

        return trendingBinding.getRoot();
    }
}