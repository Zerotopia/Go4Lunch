package com.example.goforlunch.viewmodel;

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
import com.google.android.libraries.places.api.model.RectangularBounds;

import java.util.List;

public class NetworkViewModel extends ViewModel {

    private NetworkRepository mNetworkRepository;

    private LocationBias mBias;
    private Integer mTotalUsers;

    private MutableLiveData<String> mQuery = new MutableLiveData<>();
    private MutableLiveData<String> mPlaceId = new MutableLiveData<>();
    private MutableLiveData<NearByPlace> mPlaces = new MutableLiveData<>();

    private final LiveData<List<AutocompletePrediction>> mPredictionObservable
            = Transformations.switchMap(mQuery, (query) -> mNetworkRepository.getPlacePredictions(query, mBias));
    private final LiveData<LatLng> mLocationObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mNetworkRepository.getPlaceLocation(placeId));
    private final LiveData<List<String>> mLikersObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mNetworkRepository.getLikers(placeId));
    private final LiveData<List<Integer>> mRatioObservable =
            Transformations.switchMap(mPlaces, (nearbyPlace) -> mNetworkRepository.getRatio(nearbyPlace, mTotalUsers));
    private final LiveData<List<Integer>> mNumberOfLuncherObservable =
            Transformations.switchMap(mPlaces, (nearbyplace) -> mNetworkRepository.getNumberOfLunchers(nearbyplace));

    private MutableLiveData<NearByPlace> mNetworkObservable;
    private MutableLiveData<List<User>> mWorkersObservable;
    private MutableLiveData<List<String>> mRestaurantObservable;
    private MutableLiveData<Integer> mFragmentIdObservable;
    private MutableLiveData<Integer> mTotalUsersObservable;

    public NetworkViewModel(NetworkRepository networkRepository) {
        mNetworkRepository = networkRepository;
    }

    public void init(String userId, LocationBias bias) {
        mBias = bias;
        if (mNetworkObservable != null) return;
        mNetworkObservable = mNetworkRepository.getNearByPlace(initPosFromBias(mBias));
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

    public void initTotalUsers() {
        mTotalUsersObservable = mNetworkRepository.getTotalUsers();
    }

    public void setTotalusers(int totalusers) {
        mTotalUsers = totalusers;
    }

    public void newQuery(String query) {
        mQuery.setValue(query);
    }

    public void newPos(String placeId) {
        mPlaceId.setValue(placeId);
    }

    ;

    public void newPlaces(NearByPlace places) {
        mPlaces.setValue(places);
    }

    /**
     * Function to have initial position for the nearbysearch request.
     *
     * @param bias
     * @return
     */
    public LatLng initPosFromBias(LocationBias bias) {
        LatLng ne = ((RectangularBounds) bias).getNortheast();
        LatLng sw = ((RectangularBounds) bias).getSouthwest();

        double lat = (ne.latitude + sw.latitude) / 2;
        double lng = (ne.longitude + sw.longitude) / 2;

        return new LatLng(lat, lng);
    }

    public LiveData<NearByPlace> getNetworkObservable() {
        return mNetworkObservable;
    }

    public LiveData<List<String>> getRestaurantObservable() {
        return mRestaurantObservable;
    }

    public LiveData<List<User>> getWorkersObservable() {
        return mWorkersObservable;
    }

    public final LiveData<List<AutocompletePrediction>> getPredictionObservable() {
        return mPredictionObservable;
    }

    public final LiveData<LatLng> getLocationObservable() {
        return mLocationObservable;
    }

    public final LiveData<List<String>> getmLikersObservable() {
        return mLikersObservable;
    }

    public LiveData<List<Integer>> getRatioObservable() {
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
