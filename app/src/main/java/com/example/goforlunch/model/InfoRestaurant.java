package com.example.goforlunch.model;

public class InfoRestaurant {
    private Place mPlace;
    private Integer mRatio;
    private Integer mHeadCount;
    private Integer mDistance;

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
