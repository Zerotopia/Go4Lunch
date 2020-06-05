package com.example.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.go4lunch.repository.NetworkRepository;
import com.example.go4lunch.repository.PredictionRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {


    private NetworkRepository mNetworkRepository;
    private PredictionRepository mPredictionRepository;

    public ViewModelFactory(NetworkRepository networkRepository, PredictionRepository predictionRepository) {
        mNetworkRepository = networkRepository;
        mPredictionRepository = predictionRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NetworkViewModel.class))
            return (T) new NetworkViewModel(mNetworkRepository);
        else  if (modelClass.isAssignableFrom(PredictionViewModel.class))
            return (T) new PredictionViewModel(mPredictionRepository);
        return null;
    }
}
