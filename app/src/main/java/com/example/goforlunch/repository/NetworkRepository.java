package com.example.goforlunch.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.goforlunch.model.NearByPlace;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                    Log.d("TAG", "onResponse in getNearbyplace Network repository: data setValue OK" + response.body().getResults().size() );
                } else Log.d("TAG", "onResponse in getNearbyplace Network repository: FAILLLLLLL");
            }

            @Override
            public void onFailure(Call<NearByPlace> call, Throwable t) {
                Log.d("TAG", "onFailure: getNearbyplace networkRepository null ");
            }
        });
        return data;
    }
}
