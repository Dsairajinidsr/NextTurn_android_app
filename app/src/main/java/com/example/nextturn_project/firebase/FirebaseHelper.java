package com.example.nextturn_project.firebase;

import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseHelper {

    private static FirebaseFirestore firestore;

    public static FirebaseFirestore getFirestore() {

        if (firestore == null) {
            firestore = FirebaseFirestore.getInstance();
        }

        return firestore;
    }
}