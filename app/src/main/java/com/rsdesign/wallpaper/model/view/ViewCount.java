package com.rsdesign.wallpaper.model.view;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ViewCount {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Object data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
