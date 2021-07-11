package com.example.goforlunch.interfaces;

import com.example.goforlunch.model.NearByPlace;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit request to have the list of the around restaurant with a
 * nearbysearch request.
 */
public interface MapService {

    @GET("nearbysearch/json?&radius=1500&type=restaurant&key=AIzaSyDTsp6M3ByyI2dQksEgqzn03HoPGqatzL4")
    Call<NearByPlace> aroundRestaurants(@Query("location") String initPos);

}
