package com.rsdesign.wallpaper.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mainBinding;
    static BottomNavigationView navigationView;
    static NavController navController;
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
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
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
}