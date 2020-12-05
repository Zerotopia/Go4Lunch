package com.example.goforlunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.goforlunch.model.User;
import com.example.goforlunch.repository.LikeRepository;

import java.util.List;

public class LikeViewModel extends ViewModel {

  //  private String mRestaurantId;
  //  private String mUserId;

    private LikeRepository mLikeRepository;

    private MutableLiveData<String> mRestaurantId = new MutableLiveData<>();

    private MutableLiveData<Boolean> mLikeObservable;
    //= Transformations.switchMap(mRestaurantId, (restaurantId) -> mLikeRepository.isLike(restaurantId));
    private MutableLiveData<Boolean> mUserRestaurantObservable;
    // = Transformations.switchMap(mRestaurantId, (restaurantId) -> mLikeRepository.isLunch(restaurantId));
    private LiveData<List<User>> mUsersObservable;

    public LikeViewModel(LikeRepository likeRepository) {
        mLikeRepository = likeRepository;
    }

    public void init(String restaurantId, String userId) {
        mLikeObservable = mLikeRepository.isLike(restaurantId,userId);
        mUserRestaurantObservable = mLikeRepository.isLunch(restaurantId,userId);
        mUsersObservable = mLikeRepository.getUsers(restaurantId,userId);
    }

    public void changeLike() {
        mLikeObservable.setValue(!mLikeObservable.getValue());
    }
    public void changeUserRestaurant() {mUserRestaurantObservable.setValue(!mUserRestaurantObservable.getValue());}
    // public void isUpdate(String restaurantId) {mRestaurantId.setValue(restaurantId);}
    
    public final LiveData<Boolean> getIsLike() {return mLikeObservable; }

    public final LiveData<Boolean> getIsLunch() { return  mUserRestaurantObservable; }

    public final LiveData<List<User>> getUsersLunch() { return mUsersObservable; }


}
