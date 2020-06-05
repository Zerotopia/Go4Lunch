package com.example.go4lunch.repository;

import com.example.go4lunch.model.NearByPlace;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MapService {

    @GET("nearbysearch/json?location=47.390289,0.688850&radius=1500&type=restaurant&key=AIzaSyBsJuEIP1m7ZIB5NcD_wuFQW_mAyEaOAL0")
    Call<NearByPlace> test();

}
