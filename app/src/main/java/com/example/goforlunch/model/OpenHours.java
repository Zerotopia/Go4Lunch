package com.example.goforlunch.model;

import com.google.gson.annotations.SerializedName;

public class OpenHours {
    @SerializedName("open_now")
    private boolean mOp;

    public boolean isOp() {
        return mOp;
    }

    public OpenHours() {
    }
}
