package com.rsdesign.wallpaper.view.fragment;
import static android.content.Context.MODE_PRIVATE;

import static com.rsdesign.wallpaper.util.utils.convertCount;
import static com.rsdesign.wallpaper.util.utils.uploaderId;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapterWithAd;
import com.rsdesign.wallpaper.adapter.ShowUserPhotoAdapterWithAd;
import com.rsdesign.wallpaper.databinding.FragmentUploaderProfileBinding;
import com.rsdesign.wallpaper.model.allWallpaper.Datum;
import com.rsdesign.wallpaper.model.userProfile.Wallpaper;
import com.rsdesign.wallpaper.util.utils;
import com.rsdesign.wallpaper.view.MainActivity;
import com.rsdesign.wallpaper.viewModel.ViewModel;

import java.util.ArrayList;
import java.util.List;


public class UploaderProfileFragment extends Fragment {

    FragmentUploaderProfileBinding uploaderProfileBinding;
    private List<Object> photoResults;
    private ViewModel viewModel;
    private boolean isLogin = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private String token = "";
    private String userId = "";
    private ShowAllPhotoAdapterWithAd allPhotoAdapterWithAd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        uploaderProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_uploader_profile, container, false);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        setHasOptionsMenu(true);
        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        isLogin = preferences.getBoolean("isLogin", false);
        token = preferences.getString("token", "");
        userId = String.valueOf(preferences.getInt("userId", 0));


        photoResults = new ArrayList<>();
        allPhotoAdapterWithAd = new ShowAllPhotoAdapterWithAd(new ArrayList<>(), getContext());
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
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
        uploaderProfileBinding.photoList.setLayoutManager(layoutManager);
        uploaderProfileBinding.photoList.setAdapter(allPhotoAdapterWithAd);

        uploaderProfileBinding.loading.setVisibility(View.VISIBLE);
        uploaderProfileBinding.scrollView.setVisibility(View.GONE);
        viewModel.uploaderProfile(userId, uploaderId);
        observerProfileViewModel();

        uploaderProfileBinding.uploadImageButton.setOnClickListener(l -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_upload_image);

        });

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


        return uploaderProfileBinding.getRoot();
    }



    private void observerProfileViewModel() {
        viewModel.uploaderProfileMutableLiveData.observe(
                getViewLifecycleOwner(),
                profileResponse -> {
                    if (profileResponse.getSuccess()) {
                        uploaderProfileBinding.loading.setVisibility(View.GONE);
                        uploaderProfileBinding.scrollView.setVisibility(View.VISIBLE);
                        uploaderProfileBinding.userName.setText(profileResponse.getData().getName());
                        uploaderProfileBinding.followingCount.setText(convertCount(Integer.parseInt(profileResponse.getData().getFollowers())));
                        uploaderProfileBinding.wallpaperCount.setText(convertCount(profileResponse.getData().getWallpapers().size()));

                        if (profileResponse.getData().getWallpapers().size() != 0){
                            photoResults.addAll(profileResponse.getData().getWallpapers());
                            addBannerAds();
                            allPhotoAdapterWithAd.updatePhotoList(photoResults);
                            allPhotoAdapterWithAd.notifyDataSetChanged();
                        }else {
                        }


                    }

                    viewModel.uploaderProfileMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.uploaderProfileLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            uploaderProfileBinding.loading.setVisibility(View.GONE);
                            // profileBinding.scrollView.setVisibility(View.VISIBLE);
                        }
                        viewModel.uploaderProfileLoadError = new MutableLiveData<>();
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