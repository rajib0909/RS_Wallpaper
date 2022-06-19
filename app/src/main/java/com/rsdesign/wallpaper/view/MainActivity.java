package com.rsdesign.wallpaper.view;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    GoogleSignInClient googleSignInClient;
    ActivityMainBinding mainBinding;
    static BottomNavigationView navigationView;
    static NavController navController;
    AppBarConfiguration appBarConfiguration;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private String userName, provider;
    private boolean isLogin = false;
    private RewardedAd mRewardedAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        userName = preferences.getString("userName", "User Name");
        provider = preferences.getString("provider", "");
        isLogin = preferences.getBoolean("isLogin", false);


        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, options);

        setSupportActionBar(mainBinding.toolbar);

        mainBinding.sideNav.setOnClickListener(l->{
            mainBinding.drawerLayout.openDrawer(GravityCompat.START);
        });

        if (isLogin){
            mainBinding.userName.setText(userName);
            mainBinding.btnLogin.setVisibility(View.GONE);
            mainBinding.btnLogout.setVisibility(View.VISIBLE);

        }

        loadRewordAd();


        navigationView = mainBinding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_category, R.id.navigation_trending)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mainBinding.navView, navController);


        mainBinding.btnLogin.setOnClickListener(l-> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
        mainBinding.btnProfile.setOnClickListener(l-> {
            mainBinding.drawerLayout.close();
            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
            navController.navigate(R.id.navigation_profile);
        });

        mainBinding.btnAboutUs.setOnClickListener(l-> {
            loadRewordAd();
            showDialog("About Us !");
        });
        mainBinding.btnContactUs.setOnClickListener(l-> {
            loadRewordAd();
            showDialog("Contact Us !");
        });
        mainBinding.btnPrivacyPolicy.setOnClickListener(l-> {
            loadRewordAd();
            showDialog("Privacy Policy !");
        });
        mainBinding.btnLogout.setOnClickListener(l-> showLogoutDialog());



    }

    private void showLogoutDialog() {
        mainBinding.drawerLayout.close();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_log_out, null);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      //  alertDialog.setCanceledOnTouchOutside(false);

        Button yesButton = view.findViewById(R.id.btn_yes);
        Button noButton = view.findViewById(R.id.btn_no);


        yesButton.setOnClickListener(l->{
            if (provider.equalsIgnoreCase("google")){
                googleSignInClient.signOut().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                    }
                });
            }else {
                LoginManager.getInstance().logOut();
            }
            editor.putString("userName", "");
            editor.putString("provider", "");
            editor.putString("token", "");
            editor.putBoolean("isLogin", false);
            userName = "";
            provider = "";
            editor.commit();
            alertDialog.cancel();
            mainBinding.userName.setText("Hey");
            mainBinding.btnLogin.setVisibility(View.VISIBLE);
            mainBinding.btnLogout.setVisibility(View.GONE);
        });

        noButton.setOnClickListener(l -> alertDialog.cancel());

        alertDialog.show();
        // customSizeAlertDialog(alertDialog, getActivity(), 0.7f);

        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        alertDialog.getWindow().setLayout((int) (displayRectangle.width() *
                0.7f), alertDialog.getWindow().getAttributes().height);
    }

    /**
     * TODO: showRunningTempoChangeDialog
     * AlertDialog for showRunningTempoChangeDialog
     */
    private void showDialog(String title) {
        mainBinding.drawerLayout.close();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog, null);
        builder.setView(view);
        builder.setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      //  alertDialog.setCanceledOnTouchOutside(false);

        TextView okButton = view.findViewById(R.id.btn_ok);
        TextView tvTitle = view.findViewById(R.id.tv_title);

        tvTitle.setText(title);

        okButton.setOnClickListener(l -> {
            alertDialog.cancel();
            if (mRewardedAd != null) {
                Activity activityContext = MainActivity.this;
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
                Toast.makeText(this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                Log.d("googleAd", "The rewarded ad wasn't ready yet.");
            }
        });

        alertDialog.show();
       // customSizeAlertDialog(alertDialog, getActivity(), 0.7f);

        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        alertDialog.getWindow().setLayout((int) (displayRectangle.width() *
                0.9f), alertDialog.getWindow().getAttributes().height);
    }


    private void loadRewordAd(){
        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, getResources().getString(R.string.reword_ad_unit_id),
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) menu.findItem(R.id.btn_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search wallpaper...");
        searchView.setIconified(false);


       // menu.findItem(R.id.btn_bell).setVisible(false);
        return true;
    }




    /**
     * Hide bottom Navigation
     */
    public static void hideBottomNav() {
        navigationView.setVisibility(View.GONE);
    }

    /**
     * Show bottom Navigation
     */
    public static void showBottomNav() {
        navigationView.setVisibility(View.VISIBLE);
    }


    /**
     * Hide bottom Navigation
     */

}