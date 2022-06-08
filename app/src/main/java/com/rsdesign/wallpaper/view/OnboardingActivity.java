package com.rsdesign.wallpaper.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.ActivityOnboardingBinding;

public class OnboardingActivity extends AppCompatActivity {

    ActivityOnboardingBinding onboardingBinding;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding);
        preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();

        onboardingBinding.btnCustomizeNow.setOnClickListener(l->{
            editor.putBoolean("isOnBoarding", true);
            editor.commit();
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}