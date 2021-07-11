package com.example.goforlunch.model;

/**
 * Class that get together all criterion that will be use
 * for the sorting features.
 */
public class InfoRestaurant {
    private final Place mPlace;
    private final Integer mRatio;
    private final Integer mHeadCount;
    private final Integer mDistance;

    public InfoRestaurant(Place restaurant, Integer ratio, Integer headCount, Integer distance) {
        mPlace = restaurant;
        mRatio = ratio;
        mHeadCount = headCount;
        mDistance = distance;
    }

    public Place getPlace() {
        return mPlace;
    }

    public Integer getRatio() {
        return mRatio;
    }

    public Integer getHeadCount() {
        return mHeadCount;
    }

    public Integer getDistance() {
        return mDistance;
    }
}
