package com.example.goforlunch.repository;

import com.example.goforlunch.model.NearByPlace;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MapService {

    @GET("nearbysearch/json?location=47.390289,0.688850&radius=1500&type=restaurant&key=AIzaSyDTsp6M3ByyI2dQksEgqzn03HoPGqatzL4")
    Call<NearByPlace> test();

}
