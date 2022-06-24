
package com.rsdesign.wallpaper.model.categoryList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("display_name")
    @Expose
    private String displayName;
    @SerializedName("parent_category_id")
    @Expose
    private Object parentCategoryId;

    @SerializedName("image")
    @Expose
    private Object image;

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Object getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(Object parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

}
