package com.example.goforlunch.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * POJO Class for Place.
 */
public class Place {

    @SerializedName("geometry")
    private Geometry mGeometry;

    @SerializedName("reference")
    private String mId;

    @SerializedName("photos")
    private ArrayList<DataPhoto> mPhotos;

    @SerializedName("name")
    private String mName;

    @SerializedName("vicinity")
    private String mAddress;

    @SerializedName("rating")
    private double mRatio;

    @SerializedName("opening_hours")
    private OpenHours mOpen;

    public Place(String id, String name, String address) {
        mId = id;
        mName = name;
        mAddress = address;
    }

    public Geometry getGeometry() {
        return mGeometry;
    }

    public String getId() {
        return mId;
    }

    public ArrayList<DataPhoto> getPhotos() {
        return mPhotos;
    }

    public String getName() {
        return mName;
    }

    public String getAddress() {
        return mAddress;
    }

    public Double getRatio() {
        return mRatio;
    }

    public OpenHours getOpen() {
        return mOpen;
    }
}
