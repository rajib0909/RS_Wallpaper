package com.rsdesign.wallpaper.api;


import com.rsdesign.wallpaper.model.allWallpaper.AllWallpaper;
import com.rsdesign.wallpaper.model.categoryList.CategoryList;
import com.rsdesign.wallpaper.model.categoryWallpaper.CategoryWallpaper;
import com.rsdesign.wallpaper.model.view.ViewCount;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface Api {

    @Headers({"Accept: application/json"})
    @GET("wallpaper/all")
    Single<AllWallpaper> allWallpaper();

    @Headers({"Accept: application/json"})
    @GET("category/all")
    Single<CategoryList> allCategory();

    @Headers({"Accept: application/json"})
    @PUT("wallpaper/view-count")
    Single<ViewCount> viewCount(
            @Query("wallpaper_id") String id
    );

    @Headers({"Accept: application/json"})
    @PUT("wallpaper/download")
    Single<ViewCount> downloadCount(
            @Query("wallpaper_id") String id
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/category")
    Single<CategoryWallpaper> categoryWallpaper(
            @Query("category_id") String id
    );


}
