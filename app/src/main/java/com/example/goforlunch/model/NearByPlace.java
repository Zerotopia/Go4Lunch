package com.example.goforlunch.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * POJO Class for the result of NearByPlace Request.
 */
public class NearByPlace {

    @SerializedName("results")
    private ArrayList<Place> mResults;

    public ArrayList<Place> getResults() {
        return mResults;
    }
}
