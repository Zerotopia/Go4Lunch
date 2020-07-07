package com.example.goforlunch.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.repository.NetworkRepository;

public class NetworkViewModel extends ViewModel {

    private MutableLiveData<NearByPlace> mNetworkObservable;
    private NetworkRepository mNetworkRepository;

    public NetworkViewModel(NetworkRepository networkRepository) {
        mNetworkRepository = networkRepository;
    }

    public void init() {
        if (mNetworkObservable != null) return;
        mNetworkObservable = mNetworkRepository.getNearByPlace();
    }

    public LiveData<NearByPlace> getNetworkObservable() {
        Log.d("TAG", "getNetworkObservable: ");
        return mNetworkObservable;
    }
}
