package com.rsdesign.wallpaper.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.rsdesign.wallpaper.api.NetworkService;
import com.rsdesign.wallpaper.model.allWallpaper.AllWallpaper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class ViewModel extends androidx.lifecycle.ViewModel {


    /**
     * only exposes immutable Auth LiveData objects to observe users
     */
    public MutableLiveData<AllWallpaper> allWallpaperMutableLiveData = new MutableLiveData<>();



    /**
     * only exposes immutable Boolen LiveData objects to observe usersLoadError
     */
    public MutableLiveData<Boolean> allWallpaperLoadError = new MutableLiveData<>();



    /**
     * only exposes immutable Boolen LiveData objects to observe loading
     */
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    /**
     * Call network service
     */
    private NetworkService networkService = NetworkService.getInstance();

    private CompositeDisposable disposable = new CompositeDisposable();



    public void allWallpaper() {
        disposable.add(
                networkService.allWallpaper()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper allWallpaper) {
                                allWallpaperMutableLiveData.setValue(allWallpaper);
                                allWallpaperLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                allWallpaperLoadError.setValue(true);
                            }
                        })
        );
    }


    /*
    public void getAppInfoResponse(String token, Map<String, Object> param) {
        disposable.add(
                networkService.getAppInfoResponse(token, param)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SetAppInfoResponse>() {
                            @Override
                            public void onSuccess(@NonNull SetAppInfoResponse appInfoResponse) {
                                appInfoResponseMutableLiveData.setValue(appInfoResponse);
                                appInfoResponseError.setValue(false);
                            }


                            @Override
                            public void onError(@NonNull Throwable e) {
                                appInfoResponseError.setValue(true);
                            }
                        })
        );
    }
*/



    /**
     * Using clear CompositeDisposable, but can accept new disposable
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
