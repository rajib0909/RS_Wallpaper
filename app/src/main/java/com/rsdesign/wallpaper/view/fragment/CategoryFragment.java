package com.rsdesign.wallpaper.view.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllCategoryAdapter;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapter;
import com.rsdesign.wallpaper.databinding.FragmentCategoryBinding;
import com.rsdesign.wallpaper.model.Result;

import java.util.ArrayList;
import java.util.List;


public class CategoryFragment extends Fragment {

    FragmentCategoryBinding categoryBinding;
    private List<Result> results;
    private ShowAllCategoryAdapter categoryAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        categoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false);

        categoryBinding.btnBack.setOnClickListener(l-> getActivity().onBackPressed());

        categoryAdapter = new ShowAllCategoryAdapter(new ArrayList<>(), getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        categoryBinding.categoryList.setLayoutManager(layoutManager);
        categoryBinding.categoryList.setAdapter(categoryAdapter);

        categoryAdapter.setOnClickCategory(new ShowAllCategoryAdapter.OnClickCategory() {
            @Override
            public void onClickCategory(int id) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_CategoryPhoto);
            }
        });



        results = new ArrayList<>();
        for (int i =0; i<10;i++){
            Result result = new Result();
            result.setTitle("test");
            results.add(result);
        }

        categoryAdapter.updateCategoryList(results);
        categoryAdapter.notifyDataSetChanged();



        return categoryBinding.getRoot();
    }
}