package com.example.studious;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        String email = sharedPreferences.getString(EMAIL_KEY, "");
        netID = email.substring(0, email.length() - 9);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(ConnectionsFragment.newInstance("", ""));
    }
        BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.fragment_connections:
                                openFragment(ConnectionsFragment.newInstance("", ""));
                                retrieveMatches();
                                return true;
                            case R.id.fragment_requests:
                                openFragment(RequestFragment.newInstance("", ""));
                                return true;
                            case R.id.fragment_recommendations:
                                openFragment(RecommendationsFragment.newInstance("", ""));
                                return true;
                            case R.id.home_screen:
                                openFragment(HomeScreenFragment.newInstance("", ""));
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
        dataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String matches = dataSnapshot.child("UserMatches").child(netID).child("Matches:").getValue().toString();
                String[] matchesArray = matches.split(", ");
                List<String> matchesList = Arrays.asList(matchesArray);
                ArrayList<String> userMatches = new ArrayList<String>(matchesList);

                matchesNames = new ArrayList<String>();
                matchesNumber = new ArrayList<String>();


                for(int i = 0; i < userMatches.size(); i++) {
                    matchesNames.add(dataSnapshot.child("UserInfo").child(userMatches.get(i)).child("name:").getValue().toString());
                    matchesNumber.add(dataSnapshot.child("UserInfo").child(userMatches.get(i)).child("number:").getValue().toString());
                }

                ListView matchList = findViewById(R.id.listView);
                // set adapter of the listView
                ArrayAdapter adapter = new ArrayAdapter(Matches.this, android.R.layout.simple_list_item_1, matchesNames);
                matchList.setAdapter(adapter);
                matchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        createUserDialog(position, matchesNames.get(position), matchesNumber.get(position));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createUserDialog(final int position, final String name, final String number) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Matches.this);
        builder.setMessage(name).setTitle("Studious Partner").setPositiveButton("Send Intro SMS?", new DialogInterface.OnClickListener() {
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
        });

        AlertDialog dialog = builder.create();
        dialog.show();;

    }


    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
