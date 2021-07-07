package com.example.goforlunch.viewmodel;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.goforlunch.model.NearByPlace;
import com.example.goforlunch.model.User;
import com.example.goforlunch.repository.NetworkRepository;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.LocationBias;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;

import java.util.List;

public class NetworkViewModel extends ViewModel {

    private MutableLiveData<NearByPlace> mNetworkObservable;
    private MutableLiveData<List<String>> mRestaurantObservable;
    private MutableLiveData<List<User>> mWorkersObservable;
    private NetworkRepository mNetworkRepository;
    private LocationBias mBias;
    private int mTotalUsers;
    private LiveData<Integer> mTotalUsersObservable; //
    private MutableLiveData<Integer> mFragmentIdObservable;


    public NetworkViewModel(NetworkRepository networkRepository) {
        mNetworkRepository = networkRepository;
    }

    public void initTotalUsers() {
        mTotalUsersObservable = mNetworkRepository.getTotalUsers();
        Log.d("VIEWMODELTAG", "initTotalUsers: ");
    }

    public void init(String userId, LocationBias bias, int totalUsers) {
        mBias = bias;
        mTotalUsers = totalUsers;
        if (mNetworkObservable != null) return;
        mNetworkObservable = mNetworkRepository.getNearByPlace();
        mWorkersObservable = mNetworkRepository.getWorkers(userId);
    }

    public void initReservedRestaurant() {
        mRestaurantObservable = mNetworkRepository.getReservedRestaurant();
    }

    public void initFragment(int id) {
      mFragmentIdObservable = mNetworkRepository.getFragmentId(id);
    }



    public void changeFragment(int id) {
        mFragmentIdObservable.setValue(id);
    }

    public LiveData<NearByPlace> getNetworkObservable() {
        Log.d("TAG", "getNetworkObservable: ");
        return mNetworkObservable;
    }

    public LiveData<List<String>> getRestaurantObservable() {
        return mRestaurantObservable;
    }

    public LiveData<List<User>> getWorkersObservable() { return  mWorkersObservable; }

    private MutableLiveData<String> mQuery = new MutableLiveData<>();
    private MutableLiveData<String> mPlaceId = new MutableLiveData<>();
    private MutableLiveData<NearByPlace> mPlaces = new MutableLiveData<>();
//    private LocationBias BIAS = RectangularBounds.newInstance(
//            new LatLng(47.38545, 0.67909), // SW lat, lng
    //        new LatLng(47.390289, 0.688850);
//            new LatLng(47.39585, 0.69519) );// NE lat, lng
    private final LiveData<List<AutocompletePrediction>> mPredictionObservable
            = Transformations.switchMap(mQuery, (query) -> mNetworkRepository.getPlacePredictions(query, mBias));
    private final LiveData<LatLng> mLocationObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mNetworkRepository.getPlaceLocation(placeId));
    private  final LiveData<Place> mPhoneObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mNetworkRepository.getPlacePhone(placeId));
    //    private final LiveData<Place> mPlaceObservable =
//            Transformations.switchMap(mPlaceId, (placeId) -> mPredictionRepository.getPlace(placeId));
    private final LiveData<com.example.goforlunch.model.Place> mPlaceObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mNetworkRepository.getPlace(placeId));
    private  final LiveData<Bitmap> mPhotoObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mNetworkRepository.getPhotos(placeId));


    private final LiveData<List<String>> mLikersObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mNetworkRepository.getLikers(placeId));

    private final LiveData<List<Double>> mRatioObservable =
            Transformations.switchMap(mPlaces, (nearbyPlace) -> mNetworkRepository.getRatio(nearbyPlace, mTotalUsers));

    private final LiveData<List<Integer>> mNumberOfLuncherObservable =
            Transformations.switchMap(mPlaces, (nearbyplace) -> mNetworkRepository.getNumberOfLuncher(nearbyplace));

    public void newQuery(String query) {
        mQuery.setValue(query);
    }
    public void newPos (String placeId) {mPlaceId.setValue(placeId);};
    public void newPlaces (NearByPlace places) {mPlaces.setValue(places);}


    public final LiveData<List<AutocompletePrediction>> getPredictionObservable() {
        return mPredictionObservable;
    }

    public final LiveData<LatLng> getLocationObservable() { return mLocationObservable; }

    public final LiveData<Place> getmPhoneObservable() { return  mPhoneObservable; }
    //public final LiveData<Place> getPlaceObservable() { return mPlaceObservable; }

    public  final LiveData<List<String>> getmLikersObservable() { return  mLikersObservable; }

    public final LiveData<com.example.goforlunch.model.Place> getPlaceObservable() { return mPlaceObservable; }
    public final LiveData<Bitmap> getPhotoObservable() {return mPhotoObservable; }

    public LiveData<List<Double>> getRatioObservable() {
        return mRatioObservable;
    }

    public LiveData<Integer> getTotalUsersObservable() {
        return mTotalUsersObservable;
    }

    public LiveData<List<Integer>> getNumberOfLuncherObservable() {
        return mNumberOfLuncherObservable;
    }

    public final LiveData<Integer> getFragmentIdObservable() {
        return mFragmentIdObservable;
    }
}
