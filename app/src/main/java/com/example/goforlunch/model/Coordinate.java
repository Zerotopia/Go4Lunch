package com.example.goforlunch.model;

import com.google.gson.annotations.SerializedName;

public class Coordinate {

    @SerializedName("lat")
    private double mLat;
    @SerializedName("lng")
    private double mLng;

    public double getLat() {
        return mLat;
    }

    public double getLng() {
        return mLng;
    }

}
