package com.example.goforlunch.model;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.local.ReferenceSet;

import java.util.List;

public class Restaurant {

  private List<String> Likers;

    public Restaurant() {
    }

    public List<String> getLikers() {
        return Likers;
    }

    public void setLikers(List<String> likers) {
        Likers = likers;
    }


}
