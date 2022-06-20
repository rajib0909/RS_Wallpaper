package com.rsdesign.wallpaper.view.fragment.sideNavigation;

import static android.content.Context.MODE_PRIVATE;

import static com.rsdesign.wallpaper.util.utils.convertCount;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapter;
import com.rsdesign.wallpaper.adapter.ShowUserPhotoAdapterWithAd;
import com.rsdesign.wallpaper.databinding.FragmentProfileBinding;
import com.rsdesign.wallpaper.model.Result;
import com.rsdesign.wallpaper.util.utils;
import com.rsdesign.wallpaper.view.LoginActivity;
import com.rsdesign.wallpaper.view.MainActivity;
import com.rsdesign.wallpaper.viewModel.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {

    FragmentProfileBinding profileBinding;
    private List<Object> photoResults;
    private ViewModel viewModel;
    private boolean isLogin = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private String token = "";
    private String userId = "";
    private ShowUserPhotoAdapterWithAd userPhotoAdapterWithAd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        // Inflate the layout for this fragment
        profileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        setHasOptionsMenu(true);
        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        isLogin = preferences.getBoolean("isLogin", false);
        token = preferences.getString("token", "");
        userId = String.valueOf(preferences.getInt("userId", 0));


        photoResults = new ArrayList<>();
        userPhotoAdapterWithAd = new ShowUserPhotoAdapterWithAd(new ArrayList<>(), getContext());
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
        profileBinding.photoList.setLayoutManager(layoutManager);
        profileBinding.photoList.setAdapter(userPhotoAdapterWithAd);

        profileBinding.loading.setVisibility(View.VISIBLE);
        profileBinding.scrollView.setVisibility(View.GONE);
        viewModel.userProfile(token);
        observerProfileViewModel();

        profileBinding.uploadImageButton.setOnClickListener(l -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_upload_image);

        });

        return profileBinding.getRoot();
    }

    private void observerProfileViewModel() {
        viewModel.userProfileMutableLiveData.observe(
                getViewLifecycleOwner(),
                profileResponse -> {
                    if (profileResponse.getSuccess()) {
                        profileBinding.loading.setVisibility(View.GONE);
                        profileBinding.scrollView.setVisibility(View.VISIBLE);
                        profileBinding.userName.setText(profileResponse.getData().getName());
                        profileBinding.followingCount.setText(convertCount(Integer.parseInt(profileResponse.getData().getFollowers())));
                        if (profileResponse.getData().getWallpapers().size() != 0){
                            photoResults.addAll(profileResponse.getData().getWallpapers());
                            addBannerAds();
                            userPhotoAdapterWithAd.updatePhotoList(photoResults);
                            userPhotoAdapterWithAd.notifyDataSetChanged();
                            profileBinding.notFound.setVisibility(View.GONE);
                        }else {
                            profileBinding.notFound.setVisibility(View.VISIBLE);
                        }


                    }

                    viewModel.userProfileMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.userProfileLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError) {
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                            profileBinding.loading.setVisibility(View.GONE);
                            // profileBinding.scrollView.setVisibility(View.VISIBLE);
                        }
                        viewModel.userProfileLoadError = new MutableLiveData<>();
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