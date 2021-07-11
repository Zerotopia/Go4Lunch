package com.example.goforlunch.model;

import java.util.List;

/**
 * Restaurant Model.
 */
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

    public int getRatio() {
        return Ratio;
    }

    public void setRatio(int ratio) {
        Ratio = ratio;
    }

    public Restaurant() {

    }

    public Restaurant(List<String> likers, String id) {
        Likers = likers;
        Id = id;
    }

    public List<String> getLikers() {
        return Likers;
    }
}
