package com.example.goforlunch;

import com.example.goforlunch.model.Restaurant;
import com.example.goforlunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class RestaurantManager {

    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createRestaurant(String uid) {
        Restaurant userToCreate = new Restaurant();
        return getRestaurantsCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String uid) {
        return getRestaurantsCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getAllRestaurant() {
        return getRestaurantsCollection().get();
    }
    // --- UPDATE ---

    public static Task<Void> updateRestaurantname(List<String> likers, String uid) {
        return getRestaurantsCollection().document(uid).update("Likers", likers);
    }

    // --- DELETE ---

    public static Task<Void> deleteRestaurant(String uid) {
        return getRestaurantsCollection().document(uid).delete();
    }
}
