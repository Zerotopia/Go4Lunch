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

    public static Task<Void> createUser(String uid, String username, String email, String urlPicture) {
        User userToCreate = new User(uid, username, email, urlPicture);
        return UserManager.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---

    public static Task<DocumentSnapshot> getUser(String uid){
        return UserManager.getUsersCollection().document(uid).get();
    }

    public static Task<QuerySnapshot> getAllUser(){
        return UserManager.getUsersCollection().get();
    }
    // --- UPDATE ---

    public static Task<Void> updateUsername(String username, String uid) {
        return UserManager.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updateIsMentor(String uid, Boolean isMentor) {
        return UserManager.getUsersCollection().document(uid).update("isMentor", isMentor);
    }

    // --- DELETE ---

    public static Task<Void> deleteUser(String uid) {
        return UserManager.getUsersCollection().document(uid).delete();
    }
}
