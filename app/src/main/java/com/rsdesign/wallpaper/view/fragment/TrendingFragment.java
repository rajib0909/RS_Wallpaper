package com.rsdesign.wallpaper.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapter;
import com.rsdesign.wallpaper.adapter.ShowAllPhotoAdapterWithAd;
import com.rsdesign.wallpaper.databinding.FragmentTrendingBinding;
import com.rsdesign.wallpaper.model.Result;
import com.rsdesign.wallpaper.util.utils;

import java.util.ArrayList;
import java.util.List;

public class TrendingFragment extends Fragment {

    FragmentTrendingBinding trendingBinding;

    private List<Object> photoResults;
    private ShowAllPhotoAdapterWithAd allPhotoAdapterWithAd;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        trendingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trending, container, false);
        trendingBinding.btnBack.setOnClickListener(l-> getActivity().onBackPressed());

        photoResults = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            Result result = new Result();
            result.setTitle("test");
            photoResults.add(result);
        }

        addBannerAds();


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
        trendingBinding.trendingPostList.setLayoutManager(layoutManager);
        trendingBinding.trendingPostList.setAdapter(allPhotoAdapterWithAd);


        allPhotoAdapterWithAd.setOnClickPhoto(new ShowAllPhotoAdapterWithAd.OnClickPhoto() {
                                                  @Override
                                                  public void onClickPhoto(int id) {
                                                      NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
                                                      navController.navigate(R.id.navigation_view_photo);
                                                  }
                                              }
        );

        allPhotoAdapterWithAd.updatePhotoList(photoResults);
        allPhotoAdapterWithAd.notifyDataSetChanged();

        return trendingBinding.getRoot();
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

