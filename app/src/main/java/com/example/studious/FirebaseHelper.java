package com.example.studious;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {
    FirebaseDatabase database;
    private DatabaseReference dataRef;

    private final String USER_INFO = "userInfo";
    private final String USER_PREF = "userPreferences";
    private final String USER_MATCHES = "userMatches";
    private final String USER_CONNECTIONS = "userConnections";

    DatabaseReference userInfo;
    DatabaseReference userPref;
    DatabaseReference userMatches;
    DatabaseReference userConnections;

    FirebaseHelper() {
        database = FirebaseDatabase.getInstance(); // get Firebase database
        dataRef = FirebaseDatabase.getInstance().getReference(); // get reference to database

        // get references to the 4 main nodes
        userInfo = database.getReference().child(USER_INFO);
        userPref = database.getReference().child(USER_PREF);
        userMatches = database.getReference().child(USER_MATCHES);
        userConnections = database.getReference().child(USER_CONNECTIONS);
    }

    public void addUserInfo(User newUser) {
        String userEmail = newUser.getEmail(); // user info will be stored under the user's email
        userEmail = userEmail.substring(0, userEmail.length() - 9);
        userInfo.child(userEmail).setValue(newUser);
    }

    // TODO: method that checks whether email and password match
    public int checkUserLogin(String email, String password) {

        if( false){

            return 0;
        } else if( false) {

            return 1;
        } else if(false) {

            return 2;
        }
        // TODO: return 0 if email does not have an account
        // TODO: return 1 if user exists and password matches
        // TODO: return 2 if user exists but password does NOT match

        // this method is used in Signup to check if an account already exists for an email
        // also used in login to check that a user's login info is correct

        return -1; // TODO: dummy return, change once method is written
    }

}
