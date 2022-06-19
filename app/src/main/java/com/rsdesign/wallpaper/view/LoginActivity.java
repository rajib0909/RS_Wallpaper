package com.rsdesign.wallpaper.view;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.rsdesign.wallpaper.R;
import com.rsdesign.wallpaper.viewModel.ViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    GoogleSignInClient googleSignInClient;
    CallbackManager callbackManager;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    private AdView bannerAdView;
    private ViewModel viewModel;
    private LottieAnimationView loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        editor = preferences.edit();
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        MaterialButton fbButton = findViewById(R.id.btn_facebook);
        MaterialButton googleButton = findViewById(R.id.btn_google);
        bannerAdView = findViewById(R.id.adView);
        loading = findViewById(R.id.loading);

        AdRequest adRequest = new AdRequest.Builder().build();
        bannerAdView.loadAd(adRequest);

       /* AdLoader adLoader = new AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
                .forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                        NativeTemplateStyle styles = new
                                NativeTemplateStyle.Builder().build();
                        TemplateView template = findViewById(R.id.my_template);
                        template.setStyles(styles);
                        template.setNativeAd(nativeAd);
                    }
                })
                .build();

        adLoader.loadAd(new AdRequest.Builder().build());*/


        // Facebook login
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {


                        AccessToken accessToken = AccessToken.getCurrentAccessToken();
                        GraphRequest request = GraphRequest.newMeRequest(
                                accessToken,
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject object,
                                            GraphResponse response) {
                                        // Application code
                                        try {
                                            Map<String, String> accountMap = new HashMap<>();
                                            accountMap.put("media_key", object.getString("id"));
                                            accountMap.put("name", object.getString("name"));
                                            accountMap.put("media", "facebook");
                                           // String imageUrl = "https://graph.facebook.com/" + object.getString("id")+ "/picture?return_ssl_resources=1";
                                        //    String url = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                            editor.putString("userName", object.getString("name"));
                                            editor.putString("provider", "facebook");
                                            editor.commit();
                                            loading.setVisibility(View.VISIBLE);
                                            viewModel.userLogin(accountMap);
                                            observerLoginViewModel();
                                            Log.d("fbId", object.getString("id"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,link");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });


        fbButton.setOnClickListener(l -> {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile"));
        });



        // Google login

        ActivityResultLauncher<Intent> launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleResult(task);
                }
                if (result.getResultCode() == RESULT_CANCELED) {
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });


        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, options);


        googleButton.setOnClickListener(l -> {
            Intent intent = googleSignInClient.getSignInIntent();
            launcher.launch(intent);

        });

    }

    private void observerLoginViewModel() {
        viewModel.loginResponseMutableLiveData.observe(
                this,
                loginResponse -> {
                    loading.setVisibility(View.GONE);
                    if (loginResponse.getSuccess()) {
                        editor.putBoolean("isLogin", true);
                        editor.putString("token", "Bearer " + loginResponse.getData().getToken());
                        editor.putInt("userId", loginResponse.getData().getUser().getId());
                        editor.commit();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finishAffinity();
                    }else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                    viewModel.loginResponseMutableLiveData = new MutableLiveData<>();
                }
        );
        viewModel.loginLoadError.observe(
                this, isError -> {
                    if (isError != null) {
                        if (isError) {
                            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                            loading.setVisibility(View.GONE);
                        }
                        viewModel.loginLoadError = new MutableLiveData<>();
                    }
                }
        );
    }


    private void handleResult(Task<GoogleSignInAccount> task) {
        task.addOnSuccessListener(googleSignInAccount -> {
            Map<String, String> accountMap = new HashMap<>();
            accountMap.put("media_key", googleSignInAccount.getId());
            accountMap.put("email", googleSignInAccount.getEmail());
            accountMap.put("name", googleSignInAccount.getDisplayName());
            accountMap.put("media", "google");
            editor.putString("userName", googleSignInAccount.getDisplayName());
            editor.putString("provider", "google");
            editor.commit();
            viewModel.userLogin(accountMap);
            observerLoginViewModel();

        }).addOnFailureListener(e -> {
            Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}