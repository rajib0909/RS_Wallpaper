package com.rsdesign.wallpaper.viewModel;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.rsdesign.wallpaper.api.NetworkService;
import com.rsdesign.wallpaper.model.allWallpaper.AllWallpaper;
import com.rsdesign.wallpaper.model.categoryList.CategoryList;
import com.rsdesign.wallpaper.model.deleteWallpaper.DeleteWallpaperResponse;
import com.rsdesign.wallpaper.model.followUser.FollowUserResponse;
import com.rsdesign.wallpaper.model.imageUpload.ImageUploadResponse;
import com.rsdesign.wallpaper.model.likePhoto.PhotoLikeResponse;
import com.rsdesign.wallpaper.model.login.LoginResponse;
import com.rsdesign.wallpaper.model.uploaderProfile.UploaderProfile;
import com.rsdesign.wallpaper.model.userProfile.UserProfileResponse;
import com.rsdesign.wallpaper.model.view.ViewCount;

import java.io.File;
import java.net.UnknownHostException;
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

public class ViewModel extends androidx.lifecycle.ViewModel {


    /**
     * only exposes immutable Auth LiveData objects to observe users
     */

    public MutableLiveData<LoginResponse> loginResponseMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<AllWallpaper> allWallpaperMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<AllWallpaper> trendingWallpaperMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<AllWallpaper> categoryWallpaperMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<AllWallpaper> searchWallpaperMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ViewCount> viewCountMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<CategoryList> categoryListMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<PhotoLikeResponse> photoLikeMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<FollowUserResponse> followUserMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<ImageUploadResponse> imageUploadMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<UserProfileResponse> userProfileMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<UploaderProfile> uploaderProfileMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<DeleteWallpaperResponse> deleteWallpaperMutableLiveData = new MutableLiveData<>();


    /**
     * only exposes immutable Boolen LiveData objects to observe usersLoadError
     */
    public MutableLiveData<Boolean> loginLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> allWallpaperLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> trendingWallpaperLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> categoryWallpaperLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> searchWallpaperLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> viewCountLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> categoryListLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> photoLikeLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> followUserLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> imageUploadLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> userProfileLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> uploaderProfileLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> deleteWallpaperLoadError = new MutableLiveData<>();


    /**
     * only exposes immutable Boolen LiveData objects to observe loading
     */
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    public MutableLiveData<Boolean> noInternet = new MutableLiveData<>();

    /**
     * Call network service
     */
    private NetworkService networkService = NetworkService.getInstance();

    private CompositeDisposable disposable = new CompositeDisposable();


    public void userLogin(Map<String, String> value) {
        disposable.add(
                networkService.userLogin(value)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<LoginResponse>() {
                            @Override
                            public void onSuccess(@NonNull LoginResponse login) {
                                loginResponseMutableLiveData.setValue(login);
                                loginLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                loginLoadError.setValue(true);
                            }
                        })
        );
    }

    public void allWallpaper(int page) {
        disposable.add(
                networkService.allWallpaper(page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper allWallpaper) {
                                allWallpaperMutableLiveData.setValue(allWallpaper);
                                allWallpaperLoadError.setValue(false);
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    allWallpaperLoadError.setValue(false);
                                } else {
                                    allWallpaperLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
                            }
                        })
        );
    }

    public void allWallpaper(String token, String userId, int page) {
        disposable.add(
                networkService.allWallpaper(token, userId, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper allWallpaper) {
                                allWallpaperMutableLiveData.setValue(allWallpaper);
                                allWallpaperLoadError.setValue(false);
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    allWallpaperLoadError.setValue(false);
                                } else {
                                    allWallpaperLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
                            }
                        })
        );
    }


    public void trendingWallpaper(int page) {
        disposable.add(
                networkService.trendingWallpaper(page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper allWallpaper) {
                                trendingWallpaperMutableLiveData.setValue(allWallpaper);
                                trendingWallpaperLoadError.setValue(false);
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    trendingWallpaperLoadError.setValue(false);
                                } else {
                                    trendingWallpaperLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
                            }
                        })
        );
    }

    public void trendingWallpaper(String token, String userId, int page) {
        disposable.add(
                networkService.trendingWallpaper(token, userId, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper allWallpaper) {
                                trendingWallpaperMutableLiveData.setValue(allWallpaper);
                                trendingWallpaperLoadError.setValue(false);
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    trendingWallpaperLoadError.setValue(false);
                                } else {
                                    trendingWallpaperLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
                            }
                        })
        );
    }


    public void searchWallpaper(String tag, int page) {
        disposable.add(
                networkService.searchWallpaper(tag, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper allWallpaper) {
                                searchWallpaperMutableLiveData.setValue(allWallpaper);
                                searchWallpaperLoadError.setValue(false);
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    searchWallpaperLoadError.setValue(false);
                                } else {
                                    searchWallpaperLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
                            }
                        })
        );
    }

    public void searchWallpaper(String token, String userId, String tag, int page) {
        disposable.add(
                networkService.searchWallpaper(token, userId, tag, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper allWallpaper) {
                                searchWallpaperMutableLiveData.setValue(allWallpaper);
                                searchWallpaperLoadError.setValue(false);
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    searchWallpaperLoadError.setValue(false);
                                } else {
                                    searchWallpaperLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
                            }
                        })
        );
    }

    public void categoryWallpaper(String id, int page) {
        disposable.add(
                networkService.categoryWallpaper(id, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper categoryWallpaper) {
                                categoryWallpaperMutableLiveData.setValue(categoryWallpaper);
                                categoryWallpaperLoadError.setValue(false);
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    categoryWallpaperLoadError.setValue(false);
                                } else {
                                    categoryWallpaperLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
                            }
                        })
        );
    }

    public void categoryWallpaper(String id, String userId, int page) {
        disposable.add(
                networkService.categoryWallpaper(id, userId, page)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<AllWallpaper>() {
                            @Override
                            public void onSuccess(@NonNull AllWallpaper categoryWallpaper) {
                                categoryWallpaperMutableLiveData.setValue(categoryWallpaper);
                                categoryWallpaperLoadError.setValue(false);
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    categoryWallpaperLoadError.setValue(false);
                                } else {
                                    categoryWallpaperLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
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
                                noInternet.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                if (e instanceof UnknownHostException) {
                                    noInternet.setValue(true);
                                    categoryListLoadError.setValue(false);
                                } else {
                                    categoryListLoadError.setValue(true);
                                    noInternet.setValue(false);
                                }
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

    public void reportPhoto(String id) {
        disposable.add(
                networkService.reportPhoto(id)
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

    public void likeWallpaper(String token, String id) {
        disposable.add(
                networkService.likeWallpaper(token, id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PhotoLikeResponse>() {
                            @Override
                            public void onSuccess(@NonNull PhotoLikeResponse likeResponse) {
                                photoLikeMutableLiveData.setValue(likeResponse);
                                photoLikeLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                photoLikeLoadError.setValue(true);
                            }
                        })
        );
    }


    public void userProfile(String token) {
        disposable.add(
                networkService.userProfile(token)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserProfileResponse>() {
                            @Override
                            public void onSuccess(@NonNull UserProfileResponse profile) {
                                userProfileMutableLiveData.setValue(profile);
                                userProfileLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                userProfileLoadError.setValue(true);
                            }
                        })
        );
    }

    public void uploaderProfile(String userId, String uploaderId) {
        disposable.add(
                networkService.uploaderProfile(userId, uploaderId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UploaderProfile>() {
                            @Override
                            public void onSuccess(@NonNull UploaderProfile profile) {
                                uploaderProfileMutableLiveData.setValue(profile);
                                uploaderProfileLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                uploaderProfileLoadError.setValue(true);
                            }
                        })
        );
    }

    public void followUserWallpaper(String token, String uploaderId) {
        Map<String, String> value = new HashMap<>();
        value.put("following", uploaderId);
        disposable.add(
                networkService.followUserWallpaper(token, value)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<FollowUserResponse>() {
                            @Override
                            public void onSuccess(@NonNull FollowUserResponse followUserResponse) {
                                followUserMutableLiveData.setValue(followUserResponse);
                                followUserLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                followUserLoadError.setValue(true);
                            }
                        })
        );
    }

    public void deleteWallpaperResponse(String token, String id) {
        disposable.add(
                networkService.deleteWallpaperResponse(token, id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<DeleteWallpaperResponse>() {
                            @Override
                            public void onSuccess(@NonNull DeleteWallpaperResponse response) {
                                deleteWallpaperMutableLiveData.setValue(response);
                                deleteWallpaperLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                deleteWallpaperLoadError.setValue(true);
                            }
                        })
        );
    }


    public void wallpaperUpload(String token, String tags, String categories, String title, String description, File photo) {
        RequestBody tagsRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), tags);
        RequestBody categoriesRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), categories);
        RequestBody titleRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), title);
        RequestBody descriptionRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), description);

        final RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), photo);
        MultipartBody.Part partImage = MultipartBody.Part.createFormData("image", photo.getName(), requestBody);

        disposable.add(
                networkService.wallpaperUpload(token, tagsRequestBody, categoriesRequestBody, titleRequestBody, descriptionRequestBody, partImage)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ImageUploadResponse>() {
                            @Override
                            public void onSuccess(@NonNull ImageUploadResponse uploadResponse) {
                                imageUploadMutableLiveData.setValue(uploadResponse);
                                imageUploadLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                imageUploadLoadError.setValue(true);
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
