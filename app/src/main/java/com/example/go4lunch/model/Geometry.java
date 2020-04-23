package com.example.go4lunch.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    private Coordinate mCoordinate;

    public LatLng getCoordinate() {
        return new LatLng(mCoordinate.getLat(), mCoordinate.getLng());
    }
}
