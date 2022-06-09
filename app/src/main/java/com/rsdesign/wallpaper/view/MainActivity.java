package com.rsdesign.wallpaper.view;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    static BottomNavigationView navigationView;
    static NavController navController;
    AppBarConfiguration appBarConfiguration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(mainBinding.toolbar);

        mainBinding.sideNav.setOnClickListener(l->{
            mainBinding.drawerLayout.openDrawer(GravityCompat.START);
        });



        navigationView = mainBinding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_category, R.id.navigation_trending, R.id.navigation_download)
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

        mainBinding.btnAboutUs.setOnClickListener(l-> showDialog("About Us !"));
        mainBinding.btnContactUs.setOnClickListener(l-> showDialog("Contact Us !"));
        mainBinding.btnPrivacyPolicy.setOnClickListener(l-> showDialog("Privacy Policy !"));


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
        alertDialog.setCanceledOnTouchOutside(false);

        TextView okButton = view.findViewById(R.id.btn_ok);
        TextView tvTitle = view.findViewById(R.id.tv_title);

        tvTitle.setText(title);

        okButton.setOnClickListener(l -> {
            alertDialog.cancel();
        });

        alertDialog.show();
       // customSizeAlertDialog(alertDialog, getActivity(), 0.7f);

        Rect displayRectangle = new Rect();
        Window window = this.getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        alertDialog.getWindow().setLayout((int) (displayRectangle.width() *
                0.9f), alertDialog.getWindow().getAttributes().height);
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