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
        userInfo = database.getReference(USER_INFO);
        userPref = database.getReference(USER_PREF);
        userMatches = database.getReference(USER_MATCHES);
        userConnections = database.getReference(USER_CONNECTIONS);
    }

}
