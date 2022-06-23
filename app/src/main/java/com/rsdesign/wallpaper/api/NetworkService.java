package com.rsdesign.wallpaper.api;


import com.rsdesign.wallpaper.model.allWallpaper.AllWallpaper;
import com.rsdesign.wallpaper.model.categoryList.CategoryList;
import com.rsdesign.wallpaper.model.deleteWallpaper.DeleteWallpaperResponse;
import com.rsdesign.wallpaper.model.followUser.FollowUserResponse;
import com.rsdesign.wallpaper.model.imageUpload.ImageUploadResponse;
import com.rsdesign.wallpaper.model.likePhoto.PhotoLikeResponse;
import com.rsdesign.wallpaper.model.login.LoginResponse;
import com.rsdesign.wallpaper.model.userProfile.UserProfileResponse;
import com.rsdesign.wallpaper.model.view.ViewCount;

import java.util.Map;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static final String BASE_URL = "https://wallpaper.rsdesignerhub.com/api/v1/rswp/";

    private static NetworkService instance;

    private Api api = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(Api.class);


    private NetworkService() {
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }


    public Single<LoginResponse> userLogin(Map<String, String> value) {
        return api.userLogin(value);
    }

    public Single<AllWallpaper> allWallpaper() {
        return api.allWallpaper();
    }

    public Single<AllWallpaper> allWallpaper(String token, String userId) {
        return api.allWallpaper(token, userId);
    }

    public Single<AllWallpaper> trendingWallpaper() {
        return api.trendingWallpaper();
    }

    public Single<AllWallpaper> trendingWallpaper(String token, String userId) {
        return api.trendingWallpaper(token, userId);
    }

    public Single<AllWallpaper> searchWallpaper(String tag) {
        return api.searchWallpaper(tag);
    }

    public Single<AllWallpaper> searchWallpaper(String token, String userId, String tag) {
        return api.searchWallpaper(token, userId, tag);
    }

    public Single<CategoryList> allCategory() {
        return api.allCategory();
    }

    public Single<ViewCount> viewCount(String id) {
        return api.viewCount(id);
    }

    public Single<ViewCount> downloadCount(String id) {
        return api.downloadCount(id);
    }

    public Single<ViewCount> reportPhoto(String id) {
        return api.reportPhoto(id);
    }

    public Single<AllWallpaper> categoryWallpaper(String id) {
        return api.categoryWallpaper(id);
    }

    public Single<AllWallpaper> categoryWallpaper(String id, String userId) {
        return api.categoryWallpaper(id, userId);
    }

    public Single<PhotoLikeResponse> likeWallpaper(String token, String id) {
        return api.likeWallpaper(token, id);
    }

    public Single<UserProfileResponse> userProfile(String token) {
        return api.userProfile(token);
    }

    public Single<FollowUserResponse> followUserWallpaper(String token, Map<String, String> value) {
        return api.followUserWallpaper(token, value);
    }

    public Single<DeleteWallpaperResponse> deleteWallpaperResponse(String token, String id) {
        return api.deleteWallpaperResponse(token, id);
    }

    public Single<ImageUploadResponse> wallpaperUpload(String token, RequestBody tags, RequestBody categories, RequestBody title, RequestBody description, MultipartBody.Part file) {
        return api.wallpaperUpload(token, tags, categories, title, description, file);
    }


}
