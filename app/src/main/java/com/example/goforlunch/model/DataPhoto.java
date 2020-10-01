package com.example.goforlunch.model;

import android.content.res.Resources;
import android.util.Log;

import com.example.goforlunch.R;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataPhoto {

    //@SerializedName("html_attributions")
    //private ArrayList<String> mHtml;

    @SerializedName("photo_reference")
    private String mPhotoRef;

//    public ArrayList<String> getHtml() {
//        ArrayList<String > html = new ArrayList<>();
//        for (String s : mHtml.get(0).split("\""))
//            Log.d("TAG", "getHtml: on a : " + s);
//        if (mHtml != null)
//        html.add(mHtml.get(0).split("\"")[1]);
//        return html;
//    }


    public DataPhoto() { }

    public String getPhotoRef () {
        String request = "https://maps.googleapis.com/maps/api/place/photo?maxheight=100&maxwidth=100&photoreference=";
        String key = "&key=";
        return request + mPhotoRef + key;
    }
}
