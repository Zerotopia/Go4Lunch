package com.example.goforlunch.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.goforlunch.model.User;
import com.example.goforlunch.repository.DetailRepository;
import com.google.android.libraries.places.api.model.Place;

import java.util.List;

public class DetailViewModel extends ViewModel {

    private DetailRepository mDetailRepository;
    private MutableLiveData<String> mPlaceId = new MutableLiveData<>();
    private final LiveData<Place> mPlaceObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mDetailRepository.getPlace(placeId));
    private final LiveData<Bitmap> mPhotoObservable =
            Transformations.switchMap(mPlaceId,(placeId) -> mDetailRepository.getPhotos(placeId));

    private MutableLiveData<String> mRestaurantId = new MutableLiveData<>();

    private MutableLiveData<Boolean> mLikeObservable;
    //= Transformations.switchMap(mRestaurantId, (restaurantId) -> mLikeRepository.isLike(restaurantId));
    private MutableLiveData<Boolean> mUserRestaurantObservable;
    // = Transformations.switchMap(mRestaurantId, (restaurantId) -> mLikeRepository.isLunch(restaurantId));
    private LiveData<List<User>> mUsersObservable;

    private MutableLiveData<String> mCurrentRestaurantIdObservable;

    private MutableLiveData<Integer> mRatioObservable;


    public DetailViewModel(DetailRepository detailRepository) {
        mDetailRepository = detailRepository;
    }

    public void init(String restaurantId, String userId) {
        mLikeObservable = mDetailRepository.isLike(restaurantId,userId);
        mUserRestaurantObservable = mDetailRepository.isLunch(restaurantId,userId);
        mUsersObservable = mDetailRepository.getLunchers(restaurantId,userId);
        mCurrentRestaurantIdObservable = mDetailRepository.getCurrentRestaurantId(userId);
        mRatioObservable = mDetailRepository.getRatio(restaurantId);
    }

    public void setId (String placeId) {mPlaceId.setValue(placeId);}
    //public void setIsLunch (boolean isLunch) {mUserRestaurantObservable.setValue(isLunch);}

    public final LiveData<Place> getPlaceObservable() { return  mPlaceObservable; }
    public final LiveData<Bitmap> getPhotoObservable() { return mPhotoObservable; }

    public void changeLike() {
        mLikeObservable.setValue(!mLikeObservable.getValue());
    }
    public void changeUserRestaurant() {
        mUserRestaurantObservable.setValue(!mUserRestaurantObservable.getValue()); }

    public void updateCurrentRestaurant() {mCurrentRestaurantIdObservable.setValue(mCurrentRestaurantIdObservable.getValue());}
    // public void isUpdate(String restaurantId) {mRestaurantId.setValue(restaurantId);}

    public final LiveData<Boolean> getIsLike() {return mLikeObservable; }

    public final LiveData<Boolean> getIsLunch() { return  mUserRestaurantObservable; }

    public final LiveData<List<User>> getUsersLunch() { return mUsersObservable; }

    public final LiveData<String> getCurrentRestaurantId() {return mCurrentRestaurantIdObservable;}

    public final LiveData<Integer> getRatioRestaurant() {return mRatioObservable; }
}
