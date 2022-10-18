package com.rsdesign.wallpaper.view.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapterWithAd;
import com.rsdesign.wallpaper.databinding.FragmentHomeBinding;
import com.rsdesign.wallpaper.model.allWallpaper.Datum;
import com.rsdesign.wallpaper.util.utils;
import com.rsdesign.wallpaper.view.LoginActivity;
import com.rsdesign.wallpaper.viewModel.ViewModel;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    FragmentHomeBinding homeBinding;
    private List<Object> photoResults;
    private ShowAllPhotoAdapterWithAd allPhotoAdapterWithAd;
    private ViewModel viewModel;
    private boolean isLogin = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private String token = "";
    private String userId = "";
    private int page = 1;
    private boolean isLoad = true;
    private int i = utils.AD_PER_PHOTO;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        homeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        // ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setHasOptionsMenu(true);
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        isLogin = preferences.getBoolean("isLogin", false);
        token = preferences.getString("token", "");
        userId = String.valueOf(preferences.getInt("userId", 0));
        utils.isLoginUser = isLogin;


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
        homeBinding.photoList.setLayoutManager(layoutManager);
        homeBinding.photoList.setAdapter(allPhotoAdapterWithAd);

        homeBinding.loading.setVisibility(View.VISIBLE);
        homeBinding.photoList.setVisibility(View.GONE);

        if (isLogin) {
            viewModel.allWallpaper(token, userId, page);
        } else
            viewModel.allWallpaper(page);
        observerAllWallpapersViewModel();

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


        homeBinding.uploadImageButton.setOnClickListener(l -> {
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

        homeBinding.refresh.setOnRefreshListener(() -> {
            homeBinding.refresh.setRefreshing(false);
            homeBinding.loading.setVisibility(View.VISIBLE);
           page = 1;
            if (isLogin) {
                viewModel.allWallpaper(token, userId, page);
            } else
                viewModel.allWallpaper(page);
            observerAllWallpapersViewModel();
        });


        homeBinding.scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                    Log.d("Tanvir", "Api call Hit" + page);
                    Toast.makeText(getContext(), "Hit " + String.valueOf(page), Toast.LENGTH_SHORT).show();
                    page++;
                    homeBinding.loading.setVisibility(View.VISIBLE);
                    if (isLogin) {
                        viewModel.allWallpaper(token, userId, page);
                    } else
                        viewModel.allWallpaper(page);
                    observerAllWallpapersViewModel();

                }
            }
        });


        return homeBinding.getRoot();
    }


    private void observerAllWallpapersViewModel() {
        viewModel.allWallpaperMutableLiveData.observe(
                getActivity(),
                allWallpaper -> {
                    //isLoad = true;
                    if (allWallpaper != null) {
                        if (allWallpaper.getSuccess()) {
                            photoResults.clear();
                            if (page == 1)
                              allPhotoAdapterWithAd.clearPhotoList();
                            photoResults.addAll(allWallpaper.getData());
                              addBannerAds();
                            allPhotoAdapterWithAd.updatePhotoList(photoResults);
                            allPhotoAdapterWithAd.notifyDataSetChanged();
                            homeBinding.loading.setVisibility(View.GONE);
                            homeBinding.photoList.setVisibility(View.VISIBLE);
                            homeBinding.noInternet.setVisibility(View.GONE);
                        }

                        viewModel.allWallpaperMutableLiveData = new MutableLiveData<>();

                    }

                }
        );
        viewModel.allWallpaperLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            homeBinding.loading.setVisibility(View.GONE);
                            homeBinding.photoList.setVisibility(View.VISIBLE);
                            homeBinding.noInternet.setVisibility(View.GONE);
                        }
                        viewModel.allWallpaperLoadError = new MutableLiveData<>();
                    }
                }
        );

        viewModel.noInternet.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            Glide.with(getContext()).load(R.drawable.no_internet_1).into(homeBinding.noInternet);
                            homeBinding.loading.setVisibility(View.GONE);
                            homeBinding.noInternet.setVisibility(View.VISIBLE);
                            homeBinding.photoList.setVisibility(View.VISIBLE);
                        }
                        viewModel.noInternet = new MutableLiveData<>();
                    }
                }
        );

    }

    private void addBannerAds() {
        for (; i < photoResults.size(); i += utils.AD_PER_PHOTO) {
            AdView adView = new AdView(getContext());
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));
            photoResults.add(i, adView);
            loadBannerAds();
        }
        i = 3;
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

}