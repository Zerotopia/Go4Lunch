package com.example.goforlunch.viewmodel;

import android.graphics.Bitmap;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.goforlunch.repository.DetailRepository;
import com.google.android.libraries.places.api.model.Place;

public class DetailViewModel extends ViewModel {

    private DetailRepository mDetailRepository;
    private MutableLiveData<String> mPlaceId = new MutableLiveData<>();
    private final LiveData<Place> mPlaceObservable =
            Transformations.switchMap(mPlaceId, (placeId) -> mDetailRepository.getPlace(placeId));
    private final LiveData<Bitmap> mPhotoObservable =
            Transformations.switchMap(mPlaceId,(placeId) -> mDetailRepository.getPhotos(placeId));

    public DetailViewModel(DetailRepository detailRepository) {
        mDetailRepository = detailRepository;
    }

    public void setId (String placeId) {mPlaceId.setValue(placeId);}

    public final LiveData<Place> getPlaceObservable() { return  mPlaceObservable; }
    public final LiveData<Bitmap> getPhotoObservable() { return mPhotoObservable; }
}
