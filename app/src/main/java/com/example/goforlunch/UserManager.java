package com.example.goforlunch;

import com.example.goforlunch.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;


public class UserManager {
    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String userName, String email, String photo) {
        User userToCreate = new User(userName, email, photo);
        return UserManager.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserManager.getUsersCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getAllUser(){
        return UserManager.getUsersCollection().get();
    }

    public static Task<QuerySnapshot>getUsersInRestaurant(String uid) {
        return UserManager.getUsersCollection().whereEqualTo("RestaurantId",uid).get();
    }
    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserManager.getUsersCollection().document(uid).update("Username", username);
    }

    public static Task<Void> updateUserRestaurant(String restaurantId, String uid) {
        return UserManager.getUsersCollection().document(uid).update("RestaurantId", restaurantId);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserManager.getUsersCollection().document(uid).delete();
    }
}