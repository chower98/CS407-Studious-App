package com.example.studious;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatchRunnable implements Runnable {
    private String username;
    private DatabaseReference dataRef;

    MatchRunnable(String username) {
        this.username = username;
        dataRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void run() {
        DatabaseReference userMatches = dataRef.child("UserMatches").child(username);

        userMatches.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String matchString = dataSnapshot.child("matches").getValue().toString();
                method1(matchString);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void method1(String matchString) {
        String[] array = matchString.split(", ");
        List<String> list = Arrays.asList(array);
        ArrayList<String> matchesList = new ArrayList<String>(list);
        method3(matchesList);
    }

    private void method3(ArrayList<String> matchesList) {
        DatabaseReference allUsers = dataRef.child("UserPref");
        allUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            ArrayList<String> users;
            ArrayList<String> courses;
            ArrayList<String> days;
            ArrayList<String> locations;
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot postSnapshot: dataSnapshot.getChildren()) {
                    users.add(postSnapshot.getKey());
                    for(DataSnapshot userChild: postSnapshot.getChildren()) {
                        courses.add(userChild.child("Courses:").getValue().toString());
                        days.add(userChild.child("Days:").getValue().toString());
                        locations.add(userChild.child("Locations:").getValue().toString());
                    }
                    method2(matchesList, users, courses, days, locations);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void method2(ArrayList<String> matches, ArrayList<String> users, ArrayList<String> courses,
                        ArrayList<String> days, ArrayList<String> locations) {
        ArrayList<ArrayList<String>> courses2 = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> days2 = new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<String>> locations2 = new ArrayList<ArrayList<String>>();

        for(int i = 0; i < courses2.size(); i++) {
            String[] array = courses.get(i).split(", ");
            List<String> placeHolder = Arrays.asList(array);

            for(int j = 0; j < placeHolder.size(); j++) {
                courses2.get(0).add(placeHolder.get(j));
            }
        }

        for(int i = 0; i < courses2.size(); i++) {
            String[] array = days.get(i).split(", ");
            List<String> placeHolder = Arrays.asList(array);

            for(int j = 0; j < placeHolder.size(); j++) {
                days2.get(0).add(placeHolder.get(j));
            }
        }

        for(int i = 0; i < courses2.size(); i++) {
            String[] array = locations.get(i).split(", ");
            List<String> placeHolder = Arrays.asList(array);

            for(int j = 0; j < placeHolder.size(); j++) {
                locations2.get(0).add(placeHolder.get(j));
            }
        }

        matchMaker(matches, users, courses2, days2, locations2);
    }

    public void matchMaker(ArrayList<String> matches, ArrayList<String> users,
                           ArrayList<ArrayList<String>> courses, ArrayList<ArrayList<String>> days,
                           ArrayList<ArrayList<String>> locations) {


    }
}
