package com.rsdesign.wallpaper.view.fragment;
import static android.content.Context.MODE_PRIVATE;
import static com.rsdesign.wallpaper.util.utils.convertCount;
import static com.rsdesign.wallpaper.util.utils.isLoginUser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.FragmentPhotoViewBinding;
import com.rsdesign.wallpaper.model.allWallpaper.Datum;
import com.rsdesign.wallpaper.view.LoginActivity;
import com.rsdesign.wallpaper.view.MainActivity;
import com.rsdesign.wallpaper.viewModel.ViewModel;
import java.io.File;
import java.io.IOException;


public class PhotoViewFragment extends Fragment {

    FragmentPhotoViewBinding photoViewBinding;
    private boolean showDetails = false;
    private InterstitialAd mInterstitialAd;
    private RewardedAd mRewardedAd;
    private Datum data = null;
    private boolean isLogin = false;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    private int photoWidth = 0;
    private int photoHeight = 0;
    private ViewModel viewModel;
    private String token = "";
    private boolean isWallpaperSet = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        // Inflate the layout for this fragment
        photoViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_view, container, false);
        photoViewBinding.btnBack.setOnClickListener(l -> getActivity().onBackPressed());
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        isLogin = preferences.getBoolean("isLogin", false);
        token = preferences.getString("token", "");


        // creating the instance of the WallpaperManager
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());

        Bundle arguments = getArguments();
        if (arguments != null) {
            data = (Datum) arguments.getSerializable("PhotoDetails");
        }

        if (data != null) {
            viewModel.viewCount(String.valueOf(data.getId()));
        }

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo);

        Glide.with(getContext()).load(data.getImage()).apply(options).into(photoViewBinding.image);

        Glide.with(this)
                .asBitmap()
                .load(data.getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        photoHeight = resource.getHeight();
                        photoWidth = resource.getWidth();
                        photoViewBinding.resolutionCount.setText(String.valueOf(photoHeight) + "x" + String.valueOf(photoWidth));
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });

        Glide.with(this)
                .asFile()     // get size image url
                .load(data.getImage())
                .into(new SimpleTarget<File>() {
                    @Override
                    public void onResourceReady(@NonNull File resource, @Nullable
                            Transition<? super File> transition) {
                        long test = resource.length();     //  /1024 kb
                        photoViewBinding.sizeCount.setText(String.valueOf(resource.length() / 1024) + " KB");
                    }
                });

        photoViewBinding.viewCount.setText(convertCount(Integer.parseInt(data.getViewCount())+ 1));
        photoViewBinding.downloadCount.setText(convertCount(Integer.parseInt(data.getDownload())));
        photoViewBinding.uploaderName.setText(data.getUploader().getName().split(" ")[0]);
        photoViewBinding.followerCount.setText(convertCount(Integer.parseInt(data.getUploader().getFollowers())));
        if (data.getLikes())
            photoViewBinding.favourite.setImageResource(R.drawable.ic_heart_filled);
        else
            photoViewBinding.favourite.setImageResource(R.drawable.ic_heart);

        if (data.getUploader().getFollowedByMe())
            photoViewBinding.btnFollow.setImageResource(R.drawable.ic_unfollow);
        else
            photoViewBinding.btnFollow.setImageResource(R.drawable.ic_follow);


        loadInterstitialAd();
        loadRewordAd();


        photoViewBinding.btnArrow.setOnClickListener(l -> {
            if (showDetails) {
                showDetails = false;
                photoViewBinding.btnArrow.setImageResource(R.drawable.ic_arrow_up);
                photoViewBinding.detailsView.setVisibility(View.GONE);
                photoViewBinding.reportPhoto.setVisibility(View.GONE);

            } else {
                showDetails = true;
                photoViewBinding.btnArrow.setImageResource(R.drawable.ic_arrow_down);
                photoViewBinding.detailsView.setVisibility(View.VISIBLE);
                // photoViewBinding.detailsView.animate().translationY(0);
                photoViewBinding.reportPhoto.setVisibility(View.VISIBLE);

            }
        });


        photoViewBinding.btnSetWallpaper.setOnClickListener(v -> {

            if (!isWallpaperSet){
                Glide.with(getContext())
                        .asBitmap()
                        .load(data.getImage()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        //WallpaperManager.getInstance(getActivity().getApplicationContext()).setBitmap(resource);
                        try {
                            // set the wallpaper by calling the setResource function and
                            // passing the drawable file
                            wallpaperManager.setBitmap(resource);
                            isWallpaperSet = true;
                            Toast.makeText(getContext(), "Wallpaper updated", Toast.LENGTH_SHORT).show();
                        } catch (IOException e) {
                            // here the errors can be logged instead of printStackTrace
                            e.printStackTrace();
                        }
                    }

                });

                if (mRewardedAd != null) {
                    Activity activityContext = getActivity();
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            Log.d("googleAd", "The user earned the reward.");
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    // Toast.makeText(getContext(), "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                    Log.d("googleAd", "The rewarded ad wasn't ready yet.");
                }
            }else {
                Toast.makeText(getContext(), "Wallpaper already set", Toast.LENGTH_SHORT).show();
            }
        });


        photoViewBinding.reportPhoto.setOnClickListener(l -> {
            if (isLogin){
                new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Alert")
                    .setMessage("Are you sure to report this photo?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            viewModel.reportPhoto(String.valueOf(data.getId()));
                            observerReportPhotoViewModel();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();

            }else {
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
        photoViewBinding.btnDownload.setOnClickListener(l -> {
            viewModel.downloadCount(String.valueOf(data.getId()));
            downloadImageNew("RS wallpaper", data.getImage());
            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
            } else {
                Toast.makeText(getContext(), "Ad Failed", Toast.LENGTH_SHORT).show();
            }
        });

        photoViewBinding.favourite.setOnClickListener(l->{
            if (isLoginUser){
                viewModel.likeWallpaper(token, String.valueOf(data.getId()));
                if (data.getLikes()){
                    photoViewBinding.favourite.setImageResource(R.drawable.ic_heart);
                    data.setLikes(false);
                }
                else{
                    photoViewBinding.favourite.setImageResource(R.drawable.ic_heart_filled);
                    data.setLikes(true);
                }
            }else {
                Toast.makeText(getContext(), "Please, login first.", Toast.LENGTH_SHORT).show();
            }

        });

        photoViewBinding.btnFollow.setOnClickListener(l->{
            if (isLoginUser){
                viewModel.followUserWallpaper(token, String.valueOf(data.getUploader().getId()));
                if (data.getUploader().getFollowedByMe()){
                    photoViewBinding.btnFollow.setImageResource(R.drawable.ic_follow);
                    data.getUploader().setFollowedByMe(false);
                }
                else{
                    photoViewBinding.btnFollow.setImageResource(R.drawable.ic_unfollow);
                    data.getUploader().setFollowedByMe(true);
                }
            }else {
                Toast.makeText(getContext(), "Please, login first.", Toast.LENGTH_SHORT).show();
            }

        });

        photoViewBinding.btnShare.setOnClickListener(l -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            String shareBody = data.getImage();
            shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(shareIntent, "Share via"));

        });

        return photoViewBinding.getRoot();
    }


    private void observerReportPhotoViewModel() {
        viewModel.viewCountMutableLiveData.observe(
                getViewLifecycleOwner(),
                viewCount -> {
                    if (viewCount.getSuccess()) {
                        Toast.makeText(getContext(), "Successfully reported", Toast.LENGTH_SHORT).show();
                    }
                    viewModel.viewCountMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.viewCountLoadError.observe(
                getViewLifecycleOwner(), isError -> {
                    if (isError != null) {
                        if (isError)
                            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                        viewModel.viewCountLoadError = new MutableLiveData<>();
                    }
                }
        );

    }


    private void loadRewordAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(getContext(), getResources().getString(R.string.reword_ad_unit_id),
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d("googleAd", loadAdError.getMessage());
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Log.d("googleAd", "Ad was loaded.");

                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d("googleAd", "Ad was shown.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.d("googleAd", "Ad failed to show.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d("googleAd", "Ad was dismissed.");
                                mRewardedAd = null;
                            }
                        });
                    }
                });
    }


    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(), getResources().getString(R.string.interstitial_ad_unit_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i("googleAd", "onAdLoaded");

                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i("googleAd", loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void downloadImageNew(String filename, String downloadUrlOfImage) {
        try {
            DownloadManager dm = (DownloadManager) getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            Uri downloadUri = Uri.parse(downloadUrlOfImage);
            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                    .setAllowedOverRoaming(false)
                    .setTitle(filename)
                    .setMimeType("image/jpeg") // Your file type. You can use this code to download other file types also.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, File.separator + filename + ".jpg");
            dm.enqueue(request);
            Toast.makeText(getContext(), "Image download started.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Image download failed.", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.hideBottomNav();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.showBottomNav();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}