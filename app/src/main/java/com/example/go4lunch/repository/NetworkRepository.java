package com.example.go4lunch.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.MapClient;
import com.example.go4lunch.model.NearByPlace;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkRepository {

    private MapService mMapService;

    public NetworkRepository(MapService mapService) {
        mMapService = mapService;
    }


    public MutableLiveData<NearByPlace> getNearByPlace() {
        final MutableLiveData<NearByPlace> data = new MutableLiveData<>();
        mMapService.test().enqueue(new Callback<NearByPlace>() {
            @Override
            public void onResponse(Call<NearByPlace> call, Response<NearByPlace> response) {
                if (response.isSuccessful()) {
                    data.setValue(response.body());
                    Log.d("TAG", "onResponse: data setValue OK" + response.body().getResults().size() );
                } else Log.d("TAG", "onResponse: FAILLLLLLL");
            }

            @Override
            public void onFailure(Call<NearByPlace> call, Throwable t) {
                Log.d("TAG", "onFailure: getNearbyplace null ");
            }
        });
        return data;
    }
}
