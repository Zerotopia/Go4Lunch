package com.example.goforlunch.model;

import com.google.gson.annotations.SerializedName;

/**
 * POJO Class for photos of a Place
 */
public class DataPhoto {

    @SerializedName("photo_reference")
    private String mPhotoRef;

    public DataPhoto() {
    }

    public String getPhotoRef() {
        String request = "https://maps.googleapis.com/maps/api/place/photo?maxheight=100&maxwidth=100&photoreference=";
        String key = "&key=";
        return request + mPhotoRef + key;
    }
}
