package com.example.goforlunch.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ListInfoRestaurant {

    private final List<InfoRestaurant> mInfoRestaurantList;

    public ListInfoRestaurant(List<Place> restaurants, List<Integer> ratioRestaurant, List<Integer> headCountRestaurant, List<Integer> distanceRestaurant) {
        List<InfoRestaurant> result = new ArrayList<>();
        for (int i = 0; i < restaurants.size(); i ++)
            result.add(new InfoRestaurant(restaurants.get(i), ratioRestaurant.get(i), headCountRestaurant.get(i), distanceRestaurant.get(i)));
        mInfoRestaurantList = result;
    }

    public void sortByNames() {
        Collections.sort(mInfoRestaurantList, (infoRestaurant, t1) -> infoRestaurant.getPlace().getName().compareTo(t1.getPlace().getName()));
    }

    public void sortByRatios() {
        Collections.sort(mInfoRestaurantList, (infoRestaurant, t1) -> t1.getRatio().compareTo(infoRestaurant.getRatio()));
    }

    public void sortByHeadCounts() {
        Collections.sort(mInfoRestaurantList, (infoRestaurant, t1) -> t1.getHeadCount().compareTo(infoRestaurant.getHeadCount()));
    }

    public void sortByDistances() {
        Collections.sort(mInfoRestaurantList, (infoRestaurant, t1) -> infoRestaurant.getDistance().compareTo(t1.getDistance()));
    }

    public List<InfoRestaurant> getInfoRestaurantList() {
        return mInfoRestaurantList;
    }
}
