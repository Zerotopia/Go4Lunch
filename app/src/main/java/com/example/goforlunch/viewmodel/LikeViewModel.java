package com.example.goforlunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.goforlunch.repository.LikeRepository;

public class LikeViewModel extends ViewModel {

    private LikeRepository mLikeRepository;

    private MutableLiveData<String> mRestaurantId = new MutableLiveData<>();

    private final LiveData<Boolean> mLikeObservable =
            Transformations.switchMap(mRestaurantId, (restaurantId) -> mLikeRepository.isLike(restaurantId));
    private final  LiveData<Boolean> mUserRestaurantObservable =
            Transformations.switchMap(mRestaurantId, (restaurantId) -> mLikeRepository.isLunch(restaurantId));

    public LikeViewModel(LikeRepository likeRepository) {
        mLikeRepository = likeRepository;
    }

    public void isUpdate(String restaurantId) {mRestaurantId.setValue(restaurantId);}
    
    public final LiveData<Boolean> getIsLike() {return mLikeObservable; }

    public final LiveData<Boolean> getIsLunch() { return  mUserRestaurantObservable; }

}
