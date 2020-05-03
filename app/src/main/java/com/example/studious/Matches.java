package com.example.studious;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Matches extends AppCompatActivity {

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";
    private String netID;
    BottomNavigationView bottomNavigation;
    private boolean newUser;
    private ArrayList<String> matchesNames;
    private ArrayList<String> matchesNumber;
    private ArrayList<String> userMatches;

    private ArrayList<String> sharedCourses = new ArrayList<String>();
    private String currentUserCourses;
    private String matchedUserCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, "");
        netID = email.substring(0, email.length() - 9);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);

        // TODO: make connections a part of the actual layout file and not a fragment
        FragmentManager connectionsFragmentManager = getSupportFragmentManager();
        connectionsFragmentManager.popBackStack("root_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

        // Add the new tab fragment
        connectionsFragmentManager.beginTransaction()
                .replace(R.id.container, ConnectionsFragment.newInstance("",""))
                .addToBackStack("root_fragment")
                .commit();
        retrieveMatches();
    }
        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.fragment_connections:
                                FragmentManager connectionsFragmentManager = getSupportFragmentManager();
                                connectionsFragmentManager.popBackStack("new_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                // Add the new tab fragment
                                connectionsFragmentManager.beginTransaction()
                                        .replace(R.id.container, ConnectionsFragment.newInstance("",""))
                                        .addToBackStack("new_fragment")
                                        .commit();
                                retrieveMatches();
                                return true;
                            case R.id.fragment_requests:
                                FragmentManager requestsFragmentManager = getSupportFragmentManager();
                                requestsFragmentManager.popBackStack("new_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                // Add the new tab fragment
                                requestsFragmentManager.beginTransaction()
                                        .replace(R.id.container, RequestFragment.newInstance("",""))
                                        .addToBackStack("new_fragment")
                                        .commit();
                                return true;
                            case R.id.fragment_recommendations:
                                FragmentManager recFragmentManager = getSupportFragmentManager();
                                recFragmentManager.popBackStack("new_fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                                // Add the new tab fragment
                                recFragmentManager.beginTransaction()
                                        .replace(R.id.container, RecommendationsFragment.newInstance("",""))
                                        .addToBackStack("new_fragment")
                                        .commit();
                                return true;
                        }
                        return false;
                    }
                };

    public void backToHome(View view){
        Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra("newUser", newUser);
        startActivity(intent);
    }
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.main_menu, menu);
//        return true;
//    }
    private void retrieveMatches() {
        DatabaseReference dataRef = FirebaseDatabase.getInstance().getReference();
        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String matches = dataSnapshot.child("UserMatches").child(netID).child("Matches").getValue(String.class);

                String[] matchesArray = matches.split(", ");
                List<String> matchesList = Arrays.asList(matchesArray);
                userMatches = new ArrayList<String>(matchesList);
                if(userMatches.get(0).equals(""))
                    userMatches = new ArrayList<String>();

                matchesNames = new ArrayList<String>();
                matchesNumber = new ArrayList<String>();


                for(int i = 0; i < userMatches.size(); i++) {
                    matchesNames.add(dataSnapshot.child("UserInfo").child(userMatches.get(i)).child("name").getValue().toString());
                    matchesNumber.add(dataSnapshot.child("UserInfo").child(userMatches.get(i)).child("phone").getValue().toString());
                }

                ListView matchList = findViewById(R.id.listView);
                // set adapter of the listView
                ArrayAdapter adapter = new ArrayAdapter(Matches.this, android.R.layout.simple_list_item_1, matchesNames);
                matchList.setAdapter(adapter);
                matchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        createUserDialog(position, matchesNames.get(position), matchesNumber.get(position), userMatches.get(position));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createUserDialog(final int position, final String name, final String number, String matchedID) {
        findSharedClasses(matchedID); // find and store classes matched to sharedCourses
        String matchedCourses = "Courses that you share: ";
        for (String course : sharedCourses) {
            matchedCourses = matchedCourses + course + ", ";
        }
        matchedCourses = matchedCourses.substring(0, matchedCourses.length()-3); // remove last ", "

        AlertDialog.Builder builder = new AlertDialog.Builder(Matches.this);
        builder.setMessage(name + "\n" + matchedCourses).setTitle("Studious Partner");
        builder.setPositiveButton("Send SMS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SendIntroSMS smsSender = new SendIntroSMS();
                boolean sent = smsSender.send(netID, name, number);
                if(sent == true){
                    Toast.makeText(getApplicationContext(), "Message Sent!", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(getApplicationContext(), "Message Failed", Toast.LENGTH_SHORT);
                }
            }
        }).setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {}
        }).setNegativeButton("Remove Match", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference dataRef = database.getReference().child("UserMatches");
                dataRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String otherUserMatches = dataSnapshot.child(name).child("Matches").getValue(String.class);
                        String[] otherUserMatchesArray = otherUserMatches.split(", ");
                        if(otherUserMatchesArray.length == 1)
                            dataRef.child(name).child("Matches").setValue("");
                        else {
                            List<String> otherUserMatchesList = Arrays.asList(otherUserMatchesArray);
                            otherUserMatchesList.remove(netID);
                            String otherMatches = "";
                            for(int i = 0; i < otherUserMatchesList.size(); i++) {
                                if(i == 0)
                                    otherMatches = otherUserMatchesList.get(0);
                                else
                                otherMatches = otherMatches + otherUserMatchesList.get(i);
                            }
                            dataRef.child(name).child("Matches").setValue(otherMatches);
                        }
                        matchesNames.remove(name);
                        String matches = "";
                        for(int i = 0; i < matchesNames.size(); i++) {
                            if(i == 0)
                                matches = matchesNames.get(0);
                            else
                                matches = matches + matchesNames.get(i);
                        }

                        String otherUnmatches = dataSnapshot.child(name).child("Unmatches").getValue(String.class);
                        otherUnmatches = otherUnmatches + ", " + netID;
                        dataRef.child(name).child("Unmatches").setValue(otherUnmatches);

                        String userUnmatches = dataSnapshot.child(netID).child("Unmatches").getValue(String.class);
                        userUnmatches = userUnmatches + ", " + name;
                        dataRef.child(netID).child("Unmatches").setValue(otherUnmatches);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();;

    }

    private void findSharedClasses(String matchedPerson) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference currentUser = firebaseDatabase.getReference().child("UserPref").child(netID).child("Courses");
        DatabaseReference matchedUser = firebaseDatabase.getReference().child("UserPref").child(matchedPerson).child("Courses");

        currentUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentUserCourses = dataSnapshot.getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        matchedUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                matchedUserCourses = dataSnapshot.getValue().toString();
                findSharedClassesPart2();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void findSharedClassesPart2() {
        String[] currentUserList = currentUserCourses.split(", ");
        String[] matchedUserTemp = matchedUserCourses.split(", ");
        ArrayList<String> matchedUserList = new ArrayList<>();
        for (String matchedCourse : matchedUserTemp) { // convert matchedUser to ArrayList
            matchedUserList.add(matchedCourse);
        }

        for (String userCourse : currentUserList) {
            if (matchedUserList.contains(userCourse)) { // check if matched user has the same course as current user
                sharedCourses.add(userCourse);
            }
        }
    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                // remove data kept in the instance for the user since they are logging out
                SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
                sharedPreferences.edit().remove(EMAIL_KEY).apply();
                sharedPreferences.edit().remove(PASSWORD_KEY).apply();

                Intent logoutIntent = new Intent(this, Login.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(logoutIntent);
                return true;

            case R.id.preferences:
                Intent preferencesIntent = new Intent(this, Preferences.class);
                startActivity(preferencesIntent);
                return true;

            default: return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.logout:
//                // remove data kept in the instance for the user since they are logging out
//                SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
//                sharedPreferences.edit().remove(EMAIL_KEY).apply();
//                sharedPreferences.edit().remove(PASSWORD_KEY).apply();
//
//                Intent logoutIntent = new Intent(this, Login.class);
//                startActivity(logoutIntent);
//                return true;
//
//            case R.id.preferences:
//                Intent preferencesIntent = new Intent(this, Preferences.class);
//                startActivity(preferencesIntent);
//                return true;
//
//            default: return super.onOptionsItemSelected(item);
//        }
//    }

}
