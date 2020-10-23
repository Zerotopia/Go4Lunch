package com.example.goforlunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.goforlunch.repository.LikeRepository;
import com.example.goforlunch.repository.NetworkRepository;
import com.example.goforlunch.repository.PredictionRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {


    private NetworkRepository mNetworkRepository;
    private PredictionRepository mPredictionRepository;
    private LikeRepository mLikeRepository;

    public ViewModelFactory(NetworkRepository networkRepository, PredictionRepository predictionRepository, LikeRepository likeRepository) {
        mNetworkRepository = networkRepository;
        mPredictionRepository = predictionRepository;
        mLikeRepository = likeRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NetworkViewModel.class))
            return (T) new NetworkViewModel(mNetworkRepository);
        else  if (modelClass.isAssignableFrom(PredictionViewModel.class))
            return (T) new PredictionViewModel(mPredictionRepository);
        else  if (modelClass.isAssignableFrom(LikeViewModel.class))
            return (T) new LikeViewModel(mLikeRepository);
        return null;
    }
}
