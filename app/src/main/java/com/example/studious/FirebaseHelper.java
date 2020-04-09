package com.example.studious;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    FirebaseDatabase database;

    private final String USER_INFO = "userInfo";
    private final String USER_PREF = "userPreferences";
    private final String USER_MATCHES = "userMatches";
    private final String USER_CONNECTIONS = "userConnections";

    DatabaseReference userInfo;
    DatabaseReference userPref;
    DatabaseReference userMatches;
    DatabaseReference userConnections;

    public FirebaseHelper() {
        database = FirebaseDatabase.getInstance(); // get Firebase database

        // get references to the 4 main nodes
        userInfo = database.getReference().child(USER_INFO);
        userPref = database.getReference().child(USER_PREF);
        userMatches = database.getReference().child(USER_MATCHES);
        userConnections = database.getReference().child(USER_CONNECTIONS);
    }

    public void addUserInfo(User newUser) {
        String userEmail = newUser.getEmail(); // user info will be stored under the user's email

        // get reference to child that the info will be stored at
        //DatabaseReference newUserRef = userInfo.child(userID);
        //newUserRef.setValue(newUser); // store info in firebase

        database.getReference().child(USER_INFO).child(userEmail).setValue(newUser);
    }

    // TODO: method that checks whether email and password match
    public int checkUserLogin(String email, String password) {
        // TODO: return 0 if email does not have an account
        // TODO: return 1 if user exists and password matches
        // TODO: return 2 if user exists but password does NOT match

        // this method is used in Signup to check if an account already exists for an email
        // also used in login to check that a user's login info is correct

        return 0; // TODO: dummy return, change once method is written
    }

}
