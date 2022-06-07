package com.rsdesign.wallpaper.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.databinding.ActivityOnboardingBinding;

public class OnboardingActivity extends AppCompatActivity {

    ActivityOnboardingBinding onboardingBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onboardingBinding = DataBindingUtil.setContentView(this, R.layout.activity_onboarding);

        onboardingBinding.btnCustomizeNow.setOnClickListener(l->{
            Intent intent = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}