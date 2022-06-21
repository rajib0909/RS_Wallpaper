package com.rsdesign.wallpaper.view.fragment.admin;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapter;
import com.rsdesign.wallpaper.databinding.FragmentAdminAllPhotoBinding;
import com.rsdesign.wallpaper.model.allWallpaper.Datum;
import com.rsdesign.wallpaper.view.LoginActivity;
import com.rsdesign.wallpaper.view.MainActivity;
import com.rsdesign.wallpaper.viewModel.ViewModel;

import java.util.ArrayList;
import java.util.List;


public class AdminAllPhotoFragment extends Fragment {

    FragmentAdminAllPhotoBinding photoBinding;
    private List<Datum> photoResults;
    private ShowAllPhotoAdapter allPhotoAdapter;
    private ViewModel viewModel;
    private boolean isLogin = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private String token = "";
    private String userId = "";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        photoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_admin_all_photo, container, false);
        photoBinding.btnBack.setOnClickListener(l -> getActivity().onBackPressed());

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        isLogin = preferences.getBoolean("isLogin", false);
        token = preferences.getString("token", "");
        userId = String.valueOf(preferences.getInt("userId", 0));
        photoResults = new ArrayList<>();

        allPhotoAdapter = new ShowAllPhotoAdapter(new ArrayList<>(), getContext());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        photoBinding.photoList.setLayoutManager(layoutManager);
        photoBinding.photoList.setAdapter(allPhotoAdapter);

        photoBinding.loading.setVisibility(View.VISIBLE);
        viewModel.allWallpaper(token, userId);
        observerAllWallpapersViewModel();


        photoBinding.uploadImageButton.setOnClickListener(l -> {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                navController.navigate(R.id.navigation_upload_image);
        });

        return photoBinding.getRoot();
    }

    private void observerAllWallpapersViewModel() {
        viewModel.allWallpaperMutableLiveData.observe(
                getViewLifecycleOwner(),
                allWallpaper -> {
                    if (allWallpaper.getSuccess()) {
                        photoResults.addAll(allWallpaper.getData());
                        allPhotoAdapter.updatePhotoList(photoResults);
                        allPhotoAdapter.notifyDataSetChanged();
                        photoBinding.loading.setVisibility(View.GONE);
                    }

                    viewModel.allWallpaperMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.allWallpaperLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            photoBinding.loading.setVisibility(View.GONE);
                        }
                        viewModel.allWallpaperLoadError = new MutableLiveData<>();
                    }
                }
        );

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