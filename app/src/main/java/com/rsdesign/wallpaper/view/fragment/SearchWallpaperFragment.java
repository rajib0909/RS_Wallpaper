package com.rsdesign.wallpaper.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapterWithAd;
import com.rsdesign.wallpaper.databinding.FragmentSearchWallpaperBinding;
import com.rsdesign.wallpaper.model.allWallpaper.Datum;
import com.rsdesign.wallpaper.util.utils;
import com.rsdesign.wallpaper.view.LoginActivity;
import com.rsdesign.wallpaper.view.MainActivity;
import com.rsdesign.wallpaper.viewModel.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchWallpaperFragment extends Fragment {

    FragmentSearchWallpaperBinding searchWallpaperBinding;
    private List<Object> photoResults;
    private ShowAllPhotoAdapterWithAd allPhotoAdapterWithAd;
    private boolean isLogin = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private ViewModel viewModel;
    private String token = "";
    private String userId = "";
    private String searchTag = "";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        searchWallpaperBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_wallpaper, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // searchWallpaperBinding.btnBack.setOnClickListener(l -> getActivity().onBackPressed());
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        isLogin = preferences.getBoolean("isLogin", false);
        token = preferences.getString("token", "");
        userId = String.valueOf(preferences.getInt("userId", 0));


        Bundle arguments = getArguments();
        if (searchTag.length() == 0){
            if (arguments != null) {
                searchTag = arguments.getString("searchString");
            }
        }

        searchWallpaperBinding.uploadImageButton.setOnClickListener(l -> {
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
        photoResults = new ArrayList<>();

        allPhotoAdapterWithAd = new ShowAllPhotoAdapterWithAd(new ArrayList<>(), getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 1;
                } else if (position % utils.AD_PER_PHOTO != 0) {
                    return 1; // ITEMS AT POSITION 1 AND 6 OCCUPY 2 SPACES
                } else {
                    return 2; // OTHER ITEMS OCCUPY ONLY A SINGLE SPACE
                }
            }
        });
        searchWallpaperBinding.trendingPostList.setLayoutManager(layoutManager);
        searchWallpaperBinding.trendingPostList.setAdapter(allPhotoAdapterWithAd);

        allPhotoAdapterWithAd.setOnClickPhoto(new ShowAllPhotoAdapterWithAd.OnClickPhoto() {
                                                  @Override
                                                  public void onClickPhoto(Datum datum) {
                                                      NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                                      Bundle bundle = new Bundle();
                                                      bundle.putSerializable("PhotoDetails", datum);
                                                      navController.navigate(R.id.navigation_view_photo, bundle);

                                                  }
                                              }
        );
        allPhotoAdapterWithAd.setOnClickFavorite(new ShowAllPhotoAdapterWithAd.OnClickFavorite() {
            @Override
            public void onClickPhoto(int photoId) {
                viewModel.likeWallpaper(token, String.valueOf(photoId));
            }
        });


        searchWallpaperBinding.loading.setVisibility(View.VISIBLE);
        if (isLogin) {
            viewModel.searchWallpaper(token, userId, searchTag);
        } else
            viewModel.searchWallpaper(searchTag);
        observerSearchWallpapersViewModel();


        return searchWallpaperBinding.getRoot();
    }

    private void observerSearchWallpapersViewModel() {

        viewModel.searchWallpaperMutableLiveData.observe(
                getViewLifecycleOwner(),
                allWallpaper -> {
                    if (allWallpaper.getSuccess()) {
                        photoResults.addAll(allWallpaper.getData());
                        addBannerAds();
                        allPhotoAdapterWithAd.updatePhotoList(photoResults);
                        allPhotoAdapterWithAd.notifyDataSetChanged();
                        searchWallpaperBinding.loading.setVisibility(View.GONE);
                    }

                    viewModel.searchWallpaperMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.searchWallpaperLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            searchWallpaperBinding.loading.setVisibility(View.GONE);
                        }
                        viewModel.searchWallpaperLoadError = new MutableLiveData<>();
                    }
                }
        );
    }

    private void observerAllWallpapersViewModel() {
        viewModel.allWallpaperMutableLiveData.observe(
                getViewLifecycleOwner(),
                allWallpaper -> {
                    if (allWallpaper.getSuccess()) {
                        photoResults.addAll(allWallpaper.getData());
                        addBannerAds();
                        allPhotoAdapterWithAd.updatePhotoList(photoResults);
                        allPhotoAdapterWithAd.notifyDataSetChanged();
                        searchWallpaperBinding.loading.setVisibility(View.GONE);
                    }

                    viewModel.allWallpaperMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.allWallpaperLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            searchWallpaperBinding.loading.setVisibility(View.GONE);
                        }
                        viewModel.allWallpaperLoadError = new MutableLiveData<>();
                    }
                }
        );

    }

    private void addBannerAds() {
        for (int i = utils.AD_PER_PHOTO; i < photoResults.size(); i += utils.AD_PER_PHOTO) {
            AdView adView = new AdView(getContext());
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
            photoResults.add(i, adView);

            loadBannerAds();
        }
    }

    private void loadBannerAds() {
        loadBannerAds(utils.AD_PER_PHOTO);
    }

    private void loadBannerAds(int index) {

        if (index >= photoResults.size()) {
            return;
        }

        Object item = photoResults.get(index);
        if (!(item instanceof AdView)) {
            throw new ClassCastException("Expected item at index " + index + " to be a banner ad" + " ad.");
        }

        final AdView adView = (AdView) item;

        // Set an AdListener on the AdView to wait for the previous banner ad
        // to finish loading before loading the next ad in the items list.
        adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                loadBannerAds(index + utils.AD_PER_PHOTO);
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                loadBannerAds(index + utils.AD_PER_PHOTO);
            }
        });

        // Load the banner ad.
        adView.loadAd(new AdRequest.Builder().build());
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.action_menu, menu);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.btn_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search wallpaper...");
        searchView.setIconified(false);

        if (searchTag.length()!= 0){
            MenuItem sItem = menu.findItem(R.id.btn_search);
            sItem.expandActionView();
            searchView.setQuery(searchTag , false);
        }


        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchTag = searchView.getQuery().toString();
                searchWallpaperBinding.loading.setVisibility(View.VISIBLE);
                allPhotoAdapterWithAd.clearPhotoList();
                photoResults.clear();
                if (isLogin) {
                    viewModel.searchWallpaper(token, userId, searchTag);
                } else
                    viewModel.searchWallpaper(searchTag);
                observerSearchWallpapersViewModel();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });

        ImageView clearButton = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        clearButton.setOnClickListener(v -> {
            if (searchView.getQuery().length() == 0) {
                searchView.setIconified(true);
                searchView.clearFocus();
            } else {
                searchView.clearFocus();
                searchView.setQuery("", false);
            }
            searchWallpaperBinding.loading.setVisibility(View.VISIBLE);
            allPhotoAdapterWithAd.clearPhotoList();
            photoResults.clear();
            if (isLogin) {
                viewModel.allWallpaper(token, userId);
            } else
                viewModel.allWallpaper();

            observerAllWallpapersViewModel();
        });


        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onResume() {
        for (Object item : photoResults) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.resume();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        for (Object item : photoResults) {
            if (item instanceof AdView) {
                AdView adView = (AdView) item;
                adView.pause();
            }
        }
        super.onPause();
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