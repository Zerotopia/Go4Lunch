package com.example.goforlunch.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class Geometry {

    @SerializedName("location")
    private Coordinate mCoordinate;

    public LatLng getCoordinate() {
        return new LatLng(mCoordinate.getLat(), mCoordinate.getLng());
    }

    public Geometry(Coordinate coordinate) {
        mCoordinate = coordinate;
    }
}
