package com.rsdesign.wallpaper.api;


import com.rsdesign.wallpaper.model.allWallpaper.AllWallpaper;
import com.rsdesign.wallpaper.model.categoryList.CategoryList;
import com.rsdesign.wallpaper.model.categoryWallpaper.CategoryWallpaper;
import com.rsdesign.wallpaper.model.view.ViewCount;

import java.util.Map;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static final String BASE_URL = "http://wallpaper.rsdesignerhub.com/api/v1/rswp/";

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

    public Single<AllWallpaper> allWallpaper() {
        return api.allWallpaper();
    }
    public Single<CategoryList> allCategory() { return api.allCategory(); }
    public Single<ViewCount> viewCount(String id) { return api.viewCount(id); }
    public Single<ViewCount> downloadCount(String id) { return api.downloadCount(id); }
    public Single<CategoryWallpaper> categoryWallpaper(String id) { return api.categoryWallpaper(id); }


}
