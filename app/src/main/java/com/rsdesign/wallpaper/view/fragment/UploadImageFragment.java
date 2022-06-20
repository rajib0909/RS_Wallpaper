package com.rsdesign.wallpaper.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.FragmentUploadImageBinding;
import com.rsdesign.wallpaper.model.categoryList.Datum;
import com.rsdesign.wallpaper.view.MainActivity;
import com.rsdesign.wallpaper.viewModel.ViewModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadImageFragment extends Fragment {

    FragmentUploadImageBinding uploadImageBinding;
    private ViewModel viewModel;
    private boolean isLogin = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private String token = "";
    private String userId = "";
    private File file;
    private boolean selectPhoto = false;

    private List<String> categoryTitle;
    private Map<String, Integer> categoryMap;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        uploadImageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_image, container, false);
        uploadImageBinding.btnBack.setOnClickListener(l -> getActivity().onBackPressed());

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();
        file = new File("");
        isLogin = preferences.getBoolean("isLogin", false);
        token = preferences.getString("token", "");
        userId = String.valueOf(preferences.getInt("userId", 0));
        categoryTitle = new ArrayList<>();
        categoryMap = new HashMap<>();
        uploadImageBinding.loading.setVisibility(View.VISIBLE);
        viewModel.allCategory();
        observerAllCategoryViewModel();


        uploadImageBinding.choosePhoto.setOnClickListener(l -> {
            ImagePicker.Companion.with(this)
                    .crop(9f, 16f)                   //Crop image(Optional), Check Customization for more option
                    .compress(500)            //Final image size will be less than 1 MB(Optional)
                    .galleryOnly()
                    //.maxResultSize(512, 512)    //Final image resolution will be less than 1080 x 1080(Optional)
                    .start();
        });


        uploadImageBinding.btnUploadNow.setOnClickListener(l -> {
            int categoryId = 0;
            String wallpaperName = "";
            String tags = "";
            String description = "";
            if (!selectPhoto) {
                Toast.makeText(getContext(), "Please, select wallpaper", Toast.LENGTH_SHORT).show();
                return;
            }
            if (uploadImageBinding.etName.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "Please, input name", Toast.LENGTH_SHORT).show();
                return;
            } else {
                wallpaperName = uploadImageBinding.etName.getText().toString();
            }
            if (uploadImageBinding.spinnerCategory.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "Please, select category", Toast.LENGTH_SHORT).show();
                return;
            } else {
                categoryId = categoryMap.get(uploadImageBinding.spinnerCategory.getText().toString());
            }
            if (uploadImageBinding.etTag.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "Please, input Tag", Toast.LENGTH_SHORT).show();
                return;
            } else {
                tags = uploadImageBinding.etTag.getText().toString();
            }
            if (uploadImageBinding.etAbout.getText().toString().equalsIgnoreCase("")) {
                Toast.makeText(getContext(), "Please, input Description", Toast.LENGTH_SHORT).show();
                return;
            } else {
                description = uploadImageBinding.etAbout.getText().toString();
            }
            uploadImageBinding.loading.setVisibility(View.VISIBLE);
            uploadImageBinding.btnUploadNow.setEnabled(false);
            viewModel.wallpaperUpload(token, tags, String.valueOf(categoryId), wallpaperName, description, file);
            observerUploadWallpaperViewModel();
        });

        return uploadImageBinding.getRoot();
    }

    private void observerUploadWallpaperViewModel() {
        viewModel.imageUploadMutableLiveData.observe(
                getViewLifecycleOwner(),
                imageUploadResponse -> {
                    if (imageUploadResponse.getSuccess()) {
                        Toast.makeText(getContext(), "Wallpaper Upload Done", Toast.LENGTH_SHORT).show();
                        uploadImageBinding.etName.setText("");
                        uploadImageBinding.spinnerCategory.setText("");
                        uploadImageBinding.etTag.setText("");
                        uploadImageBinding.etAbout.setText("");
                        selectPhoto = false;
                        uploadImageBinding.btnUploadNow.setEnabled(true);
                        uploadImageBinding.srcImage.setVisibility(View.GONE);
                        uploadImageBinding.loading.setVisibility(View.GONE);
                    }

                    viewModel.imageUploadMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.imageUploadLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            uploadImageBinding.loading.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        viewModel.imageUploadLoadError = new MutableLiveData<>();
                    }
                }
        );
    }


    private void observerAllCategoryViewModel() {
        viewModel.categoryListMutableLiveData.observe(
                getViewLifecycleOwner(),
                categoryList -> {
                    if (categoryList.getSuccess()) {
                        for (Datum datum : categoryList.getData()) {
                            categoryTitle.add(datum.getDisplayName());
                            categoryMap.put(datum.getDisplayName(), datum.getId());
                        }
                        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>
                                (getContext(), android.R.layout.select_dialog_item, categoryTitle);
                        uploadImageBinding.spinnerCategory.setAdapter(categoryAdapter);
                        uploadImageBinding.loading.setVisibility(View.GONE);
                    }

                    viewModel.categoryListMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.categoryListLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            uploadImageBinding.loading.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        viewModel.categoryListLoadError = new MutableLiveData<>();
                    }
                }
        );
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            selectPhoto = true;
            Intent fileUri = data;

            Uri uri = data.getData();

            uploadImageBinding.srcImage.setVisibility(View.VISIBLE);
            uploadImageBinding.srcImage.setImageURI(uri);
            //You can get File object from intent
            file = ImagePicker.Companion.getFile(data);


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