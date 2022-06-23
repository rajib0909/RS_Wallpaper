
package com.rsdesign.wallpaper.model.uploaderProfile;

import java.io.Serializable;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.rsdesign.wallpaper.model.allWallpaper.Datum;

public class Data implements Serializable {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("followers")
    @Expose
    private String followers;
    @SerializedName("following")
    @Expose
    private String following;
    @SerializedName("followed_by_me")
    @Expose
    private Boolean followedByMe;
    @SerializedName("wallpapers")
    @Expose
    private List<Datum> wallpapers = null;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public Boolean getFollowedByMe() {
        return followedByMe;
    }

    public void setFollowedByMe(Boolean followedByMe) {
        this.followedByMe = followedByMe;
    }

    public List<Datum> getWallpapers() {
        return wallpapers;
    }

    public void setWallpapers(List<Datum> wallpapers) {
        this.wallpapers = wallpapers;
    }

}
