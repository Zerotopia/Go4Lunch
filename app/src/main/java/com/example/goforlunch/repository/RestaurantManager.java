package com.example.goforlunch.repository;

import com.example.goforlunch.model.Restaurant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Class to manage firebase database of restaurants.
 */
public class RestaurantManager {

    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createRestaurant(String uid) {
        Restaurant restaurantToCreate = new Restaurant();
        return getRestaurantsCollection().document(uid).set(restaurantToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getRestaurant(String uid) {
        return getRestaurantsCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getAllRestaurant() {
        return getRestaurantsCollection().get();
    }
    // --- UPDATE ---

    public static Task<Void> updateRestaurantLikers(List<String> likers, String uid) {
        return getRestaurantsCollection().document(uid).update("Likers", likers);
    }

    public static Task<Void> updateRestaurantName(String name, String uid) {
        return getRestaurantsCollection().document(uid).update("Name", name);
    }

    public static Task<Void> updateRestaurantLunchers(int numberOfLuncher, String uid) {
        return getRestaurantsCollection().document(uid).update("NumberOfLunchers", numberOfLuncher);
    }

    public static Task<Void> updateRestaurantRatio(int ratio, String uid) {
        return getRestaurantsCollection().document(uid).update("Ratio", ratio);
    }
    // --- DELETE ---

    public static Task<Void> deleteRestaurant(String uid) {
        return getRestaurantsCollection().document(uid).delete();
    }
}
