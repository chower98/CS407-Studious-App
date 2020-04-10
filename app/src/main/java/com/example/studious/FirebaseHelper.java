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
        dataRef = FirebaseDatabase.getInstance().getReference();
        // get references to the 4 main nodes
        userInfo = database.getReference().child(USER_INFO);
        userPref = database.getReference().child(USER_PREF);
        userMatches = database.getReference().child(USER_MATCHES);
        userConnections = database.getReference().child(USER_CONNECTIONS);
    }

    FirebaseHelper(FirebaseDatabase database) {
        this.database = database;
        dataRef = database.getReference();
        // get references to the 4 main nodes
        userInfo = database.getReference().child(USER_INFO);
        userPref = database.getReference().child(USER_PREF);
        userMatches = database.getReference().child(USER_MATCHES);
        userConnections = database.getReference().child(USER_CONNECTIONS);
    }

    public void addUserInfo(User newUser) {
        dataRef = FirebaseDatabase.getInstance().getReference();
        String userEmail = newUser.getEmail(); // user info will be stored under the user's email
        userEmail = userEmail.substring(0, userEmail.length() - 9);
        String userPassword = newUser.getPassword();
        String userPhone = newUser.getPhone();
        String userName = newUser.getName();
        // get reference to child that the info will be stored at
        //DatabaseReference newUserRef = userInfo.child(userID);
        //newUserRef.setValue(newUser); // store info in firebase

     //   database.getReference().child(USER_INFO).child(userEmail).setValue(newUser);
        dataRef.child(USER_INFO).child(userEmail).child("Password:").setValue(userPassword);
        dataRef.child(USER_INFO).child(userEmail).child("Phone:").setValue(userPhone);
        dataRef.child(USER_INFO).child(userEmail).child("Name:").setValue(userName);
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
