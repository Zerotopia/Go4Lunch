package com.example.goforlunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.goforlunch.repository.DetailRepository;
import com.example.goforlunch.repository.NetworkRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private NetworkRepository mNetworkRepository;
    private DetailRepository mDetailRepository;

    public ViewModelFactory(NetworkRepository networkRepository, DetailRepository detailRepository) {
        mNetworkRepository = networkRepository;
        mDetailRepository = detailRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(NetworkViewModel.class))
            return (T) new NetworkViewModel(mNetworkRepository);
        else if (modelClass.isAssignableFrom(DetailViewModel.class))
            return (T) new DetailViewModel(mDetailRepository);
        return null;
    }
}
