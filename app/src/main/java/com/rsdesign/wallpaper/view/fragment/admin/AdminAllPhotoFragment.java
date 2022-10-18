package com.rsdesign.wallpaper.view.fragment.admin;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
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
    private String searchTag = "";
    private int page = 1;

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
        if (searchTag.equalsIgnoreCase("")){
            viewModel.allWallpaper(token, userId, page);
            observerAllWallpapersViewModel();
        }else {
            viewModel.searchWallpaper(token, userId, searchTag);
            observerSearchWallpapersViewModel();
        }



        photoBinding.uploadImageButton.setOnClickListener(l -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_upload_image);
        });

        photoBinding.etSearch.setOnEditorActionListener((textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_SEARCH) {
                if (photoBinding.etSearch.getText().length() != 0) {
                    searchTag = photoBinding.etSearch.getText().toString();
                    photoBinding.loading.setVisibility(View.VISIBLE);
                    viewModel.searchWallpaper(token, userId, photoBinding.etSearch.getText().toString());
                    observerSearchWallpapersViewModel();

                    View view = getActivity().getCurrentFocus();
                    if (view != null) {
                        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }

                return true;
            }
            return false;
        });


        photoBinding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 0){
                    searchTag = "";
                    photoBinding.loading.setVisibility(View.VISIBLE);
                    viewModel.allWallpaper(token, userId, page);
                    observerAllWallpapersViewModel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        allPhotoAdapter.setOnClickPhotoDelete((id, position) -> {
            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Alert")
                    .setMessage("Do you want to delete the photo?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            photoBinding.loading.setVisibility(View.VISIBLE);
                            viewModel.deleteWallpaperResponse(token, String.valueOf(id));
                            observerDeleteWallpapersViewModel(position);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        return photoBinding.getRoot();
    }

    private void observerDeleteWallpapersViewModel(int position) {
        viewModel.deleteWallpaperMutableLiveData.observe(
                getViewLifecycleOwner(),
                deleteWallpaper -> {
                    photoBinding.loading.setVisibility(View.GONE);
                    if (deleteWallpaper.getSuccess()) {
                        Toast.makeText(getContext(), "Delete successfully", Toast.LENGTH_SHORT).show();
                        photoResults.remove(position);
                        allPhotoAdapter.clearAll();
                        allPhotoAdapter.updatePhotoList(photoResults);
                        allPhotoAdapter.notifyDataSetChanged();
                    }
                    viewModel.deleteWallpaperMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.deleteWallpaperLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            photoBinding.loading.setVisibility(View.GONE);
                        }
                        viewModel.deleteWallpaperLoadError = new MutableLiveData<>();
                    }
                }
        );
    }

    private void observerAllWallpapersViewModel() {
        photoBinding.notFound.setVisibility(View.GONE);
        viewModel.allWallpaperMutableLiveData.observe(
                getViewLifecycleOwner(),
                allWallpaper -> {
                    if (allWallpaper.getSuccess()) {
                        allPhotoAdapter.clearAll();
                        photoResults.clear();
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

    private void observerSearchWallpapersViewModel() {
        viewModel.searchWallpaperMutableLiveData.observe(
                getViewLifecycleOwner(),
                allWallpaper -> {
                    if (allWallpaper.getSuccess()) {
                        allPhotoAdapter.clearAll();
                        photoResults.clear();
                        allPhotoAdapter.notifyDataSetChanged();
                        photoBinding.loading.setVisibility(View.GONE);
                        if (allWallpaper.getData().size() != 0){
                            photoBinding.notFound.setVisibility(View.GONE);
                            photoResults.addAll(allWallpaper.getData());
                            allPhotoAdapter.updatePhotoList(photoResults);
                            allPhotoAdapter.notifyDataSetChanged();
                        }else {
                            photoBinding.notFound.setVisibility(View.VISIBLE);
                        }

                    }

                    viewModel.searchWallpaperMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.searchWallpaperLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            photoBinding.notFound.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            photoBinding.loading.setVisibility(View.GONE);
                        }
                        viewModel.searchWallpaperLoadError = new MutableLiveData<>();
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