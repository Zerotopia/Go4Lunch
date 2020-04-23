package com.example.go4lunch.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class NearByPlace {

    @SerializedName("results")
    private ArrayList<Place> mResults;

    public ArrayList<Place> getResults() {
        return mResults;
    }
}
