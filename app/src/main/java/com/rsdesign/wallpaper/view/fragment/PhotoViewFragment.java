package com.rsdesign.wallpaper.view.fragment;

import static android.content.Context.MODE_PRIVATE;
import static com.rsdesign.wallpaper.util.utils.convertCount;
import static com.rsdesign.wallpaper.util.utils.isLoginUser;
import static com.rsdesign.wallpaper.util.utils.uploaderId;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
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
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.canhub.cropper.CropImageView;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


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
    private Bitmap imageBitmap = null;
    private WallpaperManager wallpaperManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        photoViewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_photo_view, container, false);
        photoViewBinding.btnBack.setOnClickListener(l -> getActivity().onBackPressed());
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        preferences = getContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        isLogin = preferences.getBoolean("isLogin", false);
        token = preferences.getString("token", "");


        // creating the instance of the WallpaperManager
        wallpaperManager = WallpaperManager.getInstance(requireContext());

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
                        imageBitmap = resource;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        resource.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] imageInByte = stream.toByteArray();
                        long length = imageInByte.length;
                        photoViewBinding.sizeCount.setText(String.valueOf((length / 1024) * 2) + " KB");
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });


        photoViewBinding.viewCount.setText(convertCount(Integer.parseInt(data.getViewCount()) + 1));
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


       /* loadInterstitialAd();
        loadRewordAd();*/


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
            if (!isWallpaperSet) {
                Toast.makeText(getContext(), "Please wait, Setting wallpaper is in progress", Toast.LENGTH_SHORT).show();
                Glide.with(getContext())
                        .asBitmap()
                        .load(data.getImage()).into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        try {
                            wallpaperManager.setBitmap(resource);
                            isWallpaperSet = true;
                            Toast.makeText(getContext(), "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
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
                        } catch (IOException e) {
                            // here the errors can be logged instead of printStackTrace
                            e.printStackTrace();
                        }
                    }

                });
            } else {
                Toast.makeText(getContext(), "Wallpaper already set", Toast.LENGTH_SHORT).show();
            }
        });


       /* photoViewBinding.btnSetWallpaper.setOnClickListener(v -> {
            if (!isWallpaperSet) {


                Picasso.get().load(data.getImage()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        try {
                            photoViewBinding.loading.setVisibility(View.GONE);
                            // set the wallpaper by calling the setResource function and
                            // passing the drawable file
                            wallpaperManager.setBitmap(bitmap);
                            isWallpaperSet = true;
                            Toast.makeText(getContext(), "Wallpaper updated", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            // here the errors can be logged instead of printStackTrace
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Toast.makeText(getContext(), "Sorry, Wallpaper not set", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        photoViewBinding.loading.setVisibility(View.VISIBLE);
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
            } else {
                Toast.makeText(getContext(), "Wallpaper already set", Toast.LENGTH_SHORT).show();
            }
        });*/


        photoViewBinding.reportPhoto.setOnClickListener(l -> {
            if (isLogin) {
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
        photoViewBinding.btnDownload.setOnClickListener(l -> {
            viewModel.downloadCount(String.valueOf(data.getId()));
            /* downloadImageNew("RS wallpaper", data.getImage());
            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
            } else {
                Toast.makeText(getContext(), "Ad Failed", Toast.LENGTH_SHORT).show();
            }*/
            photoViewBinding.loading.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    photoViewBinding.loading.setVisibility(View.GONE);
                    storeImage(imageBitmap);
                }
            }, 2000);
        });

        photoViewBinding.favourite.setOnClickListener(l -> {
            if (isLoginUser) {
                viewModel.likeWallpaper(token, String.valueOf(data.getId()));
                if (data.getLikes()) {
                    photoViewBinding.favourite.setImageResource(R.drawable.ic_heart);
                    data.setLikes(false);
                } else {
                    photoViewBinding.favourite.setImageResource(R.drawable.ic_heart_filled);
                    data.setLikes(true);
                }
            } else {
                Toast.makeText(getContext(), "Please, login first.", Toast.LENGTH_SHORT).show();
            }

        });

        photoViewBinding.showUploader.setOnClickListener(l -> {
            uploaderId = String.valueOf(data.getUploader().getId());
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_uploader_profile);
        });

        photoViewBinding.btnFollow.setOnClickListener(l -> {
            if (isLoginUser) {
                viewModel.followUserWallpaper(token, String.valueOf(data.getUploader().getId()));
                if (data.getUploader().getFollowedByMe()) {
                    photoViewBinding.btnFollow.setImageResource(R.drawable.ic_follow);
                    data.getUploader().setFollowedByMe(false);
                } else {
                    photoViewBinding.btnFollow.setImageResource(R.drawable.ic_unfollow);
                    data.getUploader().setFollowedByMe(true);
                }
            } else {
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


        photoViewBinding.btnCrop.setOnClickListener(l -> {
           // loadRewordAd();
           // loadInterstitialAd();
            Dialog mDialog = new Dialog(getContext(), R.style.AppBaseTheme);
            mDialog.setContentView(R.layout.dialog_image_crop);
            CropImageView cropImageView = mDialog.findViewById(R.id.cropImageView);
            ImageView btnClose = mDialog.findViewById(R.id.btnClose);
            ImageView btnDownload = mDialog.findViewById(R.id.btnDownload);
            TextView btnSetWallpaper = mDialog.findViewById(R.id.btnSetWallpaper);
            LottieAnimationView loading = mDialog.findViewById(R.id.loading);

            btnClose.setOnClickListener(view -> {
               // loadRewordAd();
               // loadInterstitialAd();
                mDialog.cancel();
            });

            btnSetWallpaper.setOnClickListener(view -> {
                Toast.makeText(getContext(), "Please wait, Setting wallpaper is in progress", Toast.LENGTH_SHORT).show();
                try {
                    wallpaperManager.setBitmap(cropImageView.getCroppedImage());
                    Toast.makeText(getContext(), "Wallpaper set successfully", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
            });

            btnDownload.setOnClickListener(view -> {
                loading.setVisibility(View.VISIBLE);
                viewModel.downloadCount(String.valueOf(data.getId()));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.setVisibility(View.GONE);
                        storeImage(cropImageView.getCroppedImage());
                    }
                }, 2000);
            });

            cropImageView.setImageBitmap(imageBitmap);
            mDialog.show();
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

    private void storeImage(Bitmap image) {
        photoViewBinding.loading.setVisibility(View.VISIBLE);
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("TAG",
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            if (mInterstitialAd != null) {
                mInterstitialAd.show(getActivity());
            } else {
                Toast.makeText(getContext(), "Ad Failed", Toast.LENGTH_SHORT).show();
            }
            photoViewBinding.loading.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Image saved", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.d("TAG", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("TAG", "Error accessing file: " + e.getMessage());
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/RS Wallpaper");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "RS_Wallpaper" + timeStamp + ".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }


    @Override
    public void onStart() {
        super.onStart();
        MainActivity.hideBottomNav();
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public void onStop() {
        super.onStop();
        MainActivity.showBottomNav();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }


}