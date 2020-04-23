package com.example.go4lunch.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataPhoto {

    @SerializedName("html_attributuions")
    private ArrayList<String> mHtml;

    public ArrayList<String> getHtml() {
        return mHtml;
    }

}
