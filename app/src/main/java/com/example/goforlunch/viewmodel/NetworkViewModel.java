package com.example.goforlunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.repository.NetworkRepository;

import java.util.List;

public class NetworkViewModel extends ViewModel {

    private MutableLiveData<NearByPlace> mNetworkObservable;
    private MutableLiveData<List<String>> mRestaurantObservable;
    private NetworkRepository mNetworkRepository;

    public NetworkViewModel(NetworkRepository networkRepository) {
        mNetworkRepository = networkRepository;
    }

    public void init() {
        if (mNetworkObservable != null) return;
        mNetworkObservable = mNetworkRepository.getNearByPlace();
        mRestaurantObservable = mNetworkRepository.getReservedRestaurant();
    }

    public LiveData<NearByPlace> getNetworkObservable() {
        Log.d("TAG", "getNetworkObservable: ");
        return mNetworkObservable;
    }

    public LiveData<List<String>> getRestaurantObservable() {
        return mRestaurantObservable;
    }
}
