package com.rsdesign.wallpaper.viewModel;

import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.rsdesign.wallpaper.api.NetworkService;
import com.rsdesign.wallpaper.model.allWallpaper.AllWallpaper;
import com.rsdesign.wallpaper.model.categoryList.CategoryList;
import com.rsdesign.wallpaper.model.categoryWallpaper.CategoryWallpaper;
import com.rsdesign.wallpaper.model.view.ViewCount;

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
    public MutableLiveData<CategoryWallpaper> categoryWallpaperMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ViewCount> viewCountMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<CategoryList> categoryListMutableLiveData = new MutableLiveData<>();



    /**
     * only exposes immutable Boolen LiveData objects to observe usersLoadError
     */
    public MutableLiveData<Boolean> allWallpaperLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> categoryWallpaperLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> viewCountLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> categoryListLoadError = new MutableLiveData<>();



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

    public void categoryWallpaper(String id) {
        disposable.add(
                networkService.categoryWallpaper(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CategoryWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull CategoryWallpaper categoryWallpaper) {
                                categoryWallpaperMutableLiveData.setValue(categoryWallpaper);
                                categoryWallpaperLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                categoryWallpaperLoadError.setValue(true);
                            }
                        })
        );
    }

    public void allCategory() {
        disposable.add(
                networkService.allCategory()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<CategoryList>() {
                            @Override
                            public void onSuccess(@NonNull CategoryList categoryList) {
                                categoryListMutableLiveData.setValue(categoryList);
                                categoryListLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                categoryListLoadError.setValue(true);
                            }
                        })
        );
    }

    public void viewCount(String id) {
        disposable.add(
                networkService.viewCount(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ViewCount>() {
                            @Override
                            public void onSuccess(@NonNull ViewCount count) {
                                viewCountMutableLiveData.setValue(count);
                                viewCountLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                viewCountLoadError.setValue(true);
                            }
                        })
        );
    }

    public void downloadCount(String id) {
        disposable.add(
                networkService.downloadCount(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ViewCount>() {
                            @Override
                            public void onSuccess(@NonNull ViewCount count) {
                                viewCountMutableLiveData.setValue(count);
                                viewCountLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                viewCountLoadError.setValue(true);
                            }
                        })
        );
    }

    /**
     * Using clear CompositeDisposable, but can accept new disposable
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
