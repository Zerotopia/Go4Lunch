package com.example.go4lunch;

import com.example.go4lunch.model.NearByPlace;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MapService {

    @GET("nearbysearch/json?location=-33.8670522,151.1957362&radius=1500&type=restaurant&key=AIzaSyBsJuEIP1m7ZIB5NcD_wuFQW_mAyEaOAL0")
    Call<NearByPlace> test();
}
