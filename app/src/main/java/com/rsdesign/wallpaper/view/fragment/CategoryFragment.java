package com.rsdesign.wallpaper.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import static com.rsdesign.wallpaper.util.utils.categoryId;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllCategoryAdapter;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapter;
import com.rsdesign.wallpaper.databinding.FragmentCategoryBinding;
import com.rsdesign.wallpaper.model.Result;
import com.rsdesign.wallpaper.view.LoginActivity;
import com.rsdesign.wallpaper.viewModel.ViewModel;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter;


public class CategoryFragment extends Fragment {
    FragmentCategoryBinding categoryBinding;
    private ShowAllCategoryAdapter categoryAdapter;
    private ViewModel viewModel;
    private boolean isLogin = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        categoryBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_category, container, false);
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        setHasOptionsMenu(true);
        categoryBinding.btnBack.setOnClickListener(l-> getActivity().onBackPressed());

        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        isLogin = preferences.getBoolean("isLogin", false);

        categoryAdapter = new ShowAllCategoryAdapter(new ArrayList<>(), getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        categoryBinding.categoryList.setLayoutManager(layoutManager);
        categoryBinding.categoryList.setAdapter(categoryAdapter);

        categoryBinding.loading.setVisibility(View.VISIBLE);
        viewModel.allCategory();
        observerAllCategoryViewModel();
      /*  ScaleInAnimationAdapter scaleInAnimationAdapter = new ScaleInAnimationAdapter(categoryAdapter);
        scaleInAnimationAdapter.setDuration(500);
        scaleInAnimationAdapter.setInterpolator(new AccelerateDecelerateInterpolator());
        scaleInAnimationAdapter.setFirstOnly(false);
        categoryBinding.categoryList.setAdapter(scaleInAnimationAdapter);*/

        categoryAdapter.setOnClickCategory(new ShowAllCategoryAdapter.OnClickCategory() {
            @Override
            public void onClickCategory(int id) {
                categoryId = id;
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_CategoryPhoto);
            }
        });





        categoryBinding.uploadImageButton.setOnClickListener(l -> {
            if (isLogin) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_upload_image);
            } else {
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Alert")
                        .setMessage("Please, Login first!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(getActivity(), LoginActivity.class));
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

        });



        return categoryBinding.getRoot();
    }

    private void observerAllCategoryViewModel() {
        viewModel.categoryListMutableLiveData.observe(
                getViewLifecycleOwner(),
                categoryList -> {
                    if (categoryList.getSuccess()) {
                        categoryAdapter.updateCategoryList(categoryList.getData());
                        categoryAdapter.notifyDataSetChanged();
                        categoryBinding.loading.setVisibility(View.GONE);
                    }

                    viewModel.categoryListMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.categoryListLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError){
                            categoryBinding.loading.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        viewModel.categoryListLoadError = new MutableLiveData<>();
                    }
                }
        );
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_menu, menu);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.btn_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search wallpaper...");
        searchView.setIconified(false);

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //utils.searchJobString = searchView.getQuery().toString();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                Bundle bundle = new Bundle();
                bundle.putString("searchString", searchView.getQuery().toString());
                navController.navigate(R.id.navigation_search_wallpaper, bundle);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        super.onCreateOptionsMenu(menu, inflater);
    }
}