package com.example.goforlunch.model;

import com.google.gson.annotations.SerializedName;

/**
 * POJO Class for OpenHours of a Place
 */
public class OpenHours {
    @SerializedName("open_now")
    private boolean mOp;

    public boolean isOp() {
        return mOp;
    }

    public OpenHours() {
    }
}
