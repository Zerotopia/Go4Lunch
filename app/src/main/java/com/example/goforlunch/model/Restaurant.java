package com.example.goforlunch.model;

import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.local.ReferenceSet;

import java.util.List;

public class Restaurant {

  private List<String> Likers;
  private String Id;
  private String Name;
  private int NumberOfLunchers;
  private int Ratio;

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getNumberOfLunchers() {
        return NumberOfLunchers;
    }

    public void setNumberOfLunchers(int numberOfLunchers) {
        NumberOfLunchers = numberOfLunchers;
    }

    public int getRatio() {
        return Ratio;
    }

    public void setRatio(int ratio) {
        Ratio = ratio;
    }

    public Restaurant() {
    }

    public List<String> getLikers() {
        return Likers;
    }

    public void setLikers(List<String> likers) {
        Likers = likers;
    }

    public static Restaurant createRestaurantFromPlace(Place place) {
        Restaurant restaurant = new Restaurant();
        restaurant.setName(place.getName());
        restaurant.setId(place.getId());
        return restaurant;
    }


}
