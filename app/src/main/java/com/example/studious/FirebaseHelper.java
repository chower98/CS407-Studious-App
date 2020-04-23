package com.example.studious;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FirebaseHelper {
    FirebaseDatabase database;
    private DatabaseReference dataRef;
    DatabaseReference userInfo;
    DatabaseReference userPref;
    DatabaseReference userMatches;
    DatabaseReference userConnections;

    private final String USER_INFO = "userInfo";
    private final String USER_PREF = "UserPref";
    private final String USER_MATCHES = "userMatches";
    private final String USER_CONNECTIONS = "userConnections";


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
        // user info will be stored under the user's email
        String userEmail = newUser.getEmail();
        userEmail = userEmail.substring(0, userEmail.length() - 9); // remove "@wisc.edu"

        // add new user's info to database
        DatabaseReference newUserRef = userInfo.child(userEmail);
        newUserRef.setValue(newUser);

        // add listener for data change of user
        newUserRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User changedUser = dataSnapshot.getValue(User.class);
                // TODO: maybe need this to do something if the user chooses to change their info??
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: not sure what should be here?
            }
        });
    }

    // TODO: method that checks whether email and password match
    // this method is used in Signup to check if an account already exists for an email
    // also used in login to check that a user's login info is correct
    public int checkUserLogin(String email, String password) {
        // return 0 if email does not have an account
        // return 1 if user exists and password matches
        // return 2 if user exists but password does NOT match
        int userLoginStatus = -1;
        final User[] userToLogIn = {null};

        String shortenedEmail = email.substring(0, email.length() - 9);
        userInfo.child(shortenedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
        //userInfo.orderByChild("email").equalTo(shortenedEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userToLogIn[0] = dataSnapshot.getValue(User.class);
                Log.e("user exists?", dataSnapshot.getValue(User.class).getName());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: not sure if something needs to be done here??
            }
        });

        // check if email is stored in allUserEmails
        //boolean userExists = allUserEmails.contains(shortenedEmail);

        if(userToLogIn[0] == null){ // user does not exist
            userLoginStatus = 0;

        } else { // user exists
            if (userToLogIn[0].getEmail().equals(email) && userToLogIn[0].getPassword().equals(password)) {
                userLoginStatus = 1;
            } else {
                userLoginStatus = 2;
            }
        }

        return userLoginStatus; // return value to indicate login status
    }



    /// Sofia: I added these two methods. Both should add entries under UserPref in the database. Under useremail, there'll be
    /// entries for user courses (String arraylist), user days (String ArrayList), and user locations (String arraylist)
    

    //this is to be used in Preferences page. To update user's days & locations in DB.
    public void addUserPrefs(String userEmail, ArrayList <String> days, ArrayList <String> locations) {

        DatabaseReference pref = userPref.child(userEmail);
        pref.child("Days").setValue(days);
        pref.child("Locations").setValue(locations);

    }

}
