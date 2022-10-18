package com.rsdesign.wallpaper.api;


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

import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Api {

    @Headers({"Accept: application/json"})
    @POST("user/signup")
    Single<LoginResponse> userLogin(
            @Body Map<String, String> value
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/all")
    Single<AllWallpaper> allWallpaper(
            @Query("page") int page
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/all")
    Single<AllWallpaper> allWallpaper(
            @Header("Authorization") String token,
            @Query("user_id") String id,
            @Query("page") int page
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/trending")
    Single<AllWallpaper> trendingWallpaper();

    @Headers({"Accept: application/json"})
    @GET("wallpaper/trending")
    Single<AllWallpaper> trendingWallpaper(
            @Header("Authorization") String token,
            @Query("user_id") String id
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/search")
    Single<AllWallpaper> searchWallpaper(
            @Query("tag") String tag
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/search")
    Single<AllWallpaper> searchWallpaper(
            @Header("Authorization") String token,
            @Query("user_id") String id,
            @Query("tag") String tag
    );


    @Headers({"Accept: application/json"})
    @GET("category/all")
    Single<CategoryList> allCategory();

    @Headers({"Accept: application/json"})
    @PUT("wallpaper/view-count")
    Single<ViewCount> viewCount(
            @Query("wallpaper_id") String id
    );

    @Headers({"Accept: application/json"})
    @PUT("wallpaper/report")
    Single<ViewCount> reportPhoto(
            @Query("wallpaper_id") String id
    );

    @Headers({"Accept: application/json"})
    @PUT("wallpaper/download")
    Single<ViewCount> downloadCount(
            @Query("wallpaper_id") String id
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/category")
    Single<AllWallpaper> categoryWallpaper(
            @Query("category_id") String id
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/category")
    Single<AllWallpaper> categoryWallpaper(
            @Query("category_id") String id,
            @Query("user_id") String userId
    );

    @Headers({"Accept: application/json"})
    @PUT("wallpaper/like")
    Single<PhotoLikeResponse> likeWallpaper(
            @Header("Authorization") String token,
            @Query("wallpaper_id") String id
    );

    @Headers({"Accept: application/json"})
    @POST("wallpaper/follow-uploader")
    Single<FollowUserResponse> followUserWallpaper(
            @Header("Authorization") String token,
            @Body Map<String, String> value
    );


    @Headers({"Accept: application/json"})
    @GET("user/wallpaper")
    Single<UserProfileResponse> userProfile(
            @Header("Authorization") String token
    );

    @Headers({"Accept: application/json"})
    @GET("wallpaper/user/profile")
    Single<UploaderProfile> uploaderProfile(
            @Query("user_id") String userId,
            @Query("uploader_id") String uploaderId
    );

    @Headers({"Accept: application/json"})
    @DELETE("wallpaper/{id}")
    Single<DeleteWallpaperResponse> deleteWallpaperResponse(
            @Header("Authorization") String token,
            @Path("id") String id
    );


    @Multipart
    @Headers({"Accept: application/json"})
    @POST("wallpaper/create")
    Single<ImageUploadResponse> wallpaperUpload(
            @Header("Authorization") String token,
            @Part("tags") RequestBody tags,
            @Part("categories") RequestBody categories,
            @Part("title") RequestBody title,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );


}
