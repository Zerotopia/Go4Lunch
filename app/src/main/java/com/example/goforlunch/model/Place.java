package com.example.goforlunch.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Place {

    @SerializedName("geometry")
    private Geometry mGeometry;

    @SerializedName("icon")
    private String mIconUrl;

    @SerializedName("id")
    private String mId;

    @SerializedName("photos")
    private ArrayList<DataPhoto> mPhotos;

    @SerializedName("name")
    private String mName;

    public Geometry getGeometry() {
        return mGeometry;
    }

    public String getIconUrl() {
        return mIconUrl;
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
}
