package com.example.studious;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Preferences extends AppCompatActivity {

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";

    private Button homeButton;
    private Button saveButton;
    private boolean newUser;
    private String currentUserEmail; // full email of current user
    private String currentNetID; // netID of current user
    private DatabaseReference currentUserDays; // database reference to specific user's courses
    private DatabaseReference currentUserLocations; // database reference to specific user's courses
    private ArrayList<String> userLocations = new ArrayList<>(); // ArrayList of current locations
    private ArrayList<String> userDays=  new ArrayList<>(); // ArrayList of current days

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        //all checkbox values on preferences xml.
        final CheckBox monday = findViewById(R.id.monday);
        final CheckBox tuesday = findViewById(R.id.tuesday);
        final CheckBox wednesday = findViewById(R.id.wednesday);
        final CheckBox thursday = findViewById(R.id.thursday);
        final CheckBox friday = findViewById(R.id.friday);
        final CheckBox saturday = findViewById(R.id.saturday);
        final CheckBox sunday = findViewById(R.id.sunday);
        final CheckBox lakeshore = findViewById(R.id.lakeshore);
        final CheckBox college = findViewById(R.id.college);
        final CheckBox southeast = findViewById(R.id.southeast);
        final CheckBox state  = findViewById(R.id.state);
        final CheckBox engineering = findViewById(R.id.engineering);

        homeButton = findViewById(R.id.homeButton);
        saveButton = findViewById(R.id.saveButton);

        // get email and net id from sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("email", "");
        currentNetID = currentUserEmail.substring(0, currentUserEmail.length() - 9); // remove "@wisc.edu" from email

        // initialize FireBase reference and add listener to read data
        // set reference to child userID
        //
        currentUserLocations = FirebaseDatabase.getInstance().getReference().child("UserPref").child(currentNetID).child("Locations");
//        currentUserLocations.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // helper method to convert dataSnapshot into ArrayList<String>
//                currentLocations = readCourseData(dataSnapshot);
//
////                // set adapter of the listView
////                ArrayAdapter adapter = new ArrayAdapter(Preferences.this, android.R.layout.simple_list_item_1, currentLocations);
////                displayCourseList.setAdapter(adapter);
////                displayCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                    @Override
////                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                        createDeleteDialog(position);
////                    }
////                });
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // TODO: not sure if something needs to be done here??
//            }
//        });
        currentUserDays = FirebaseDatabase.getInstance().getReference().child("UserPref").child(currentNetID).child("Days");
//        currentUserDays.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                // helper method to convert dataSnapshot into ArrayList<String>
//                currentDays = readCourseData(dataSnapshot);
//
////                // set adapter of the listView
////                ArrayAdapter adapter = new ArrayAdapter(Preferences.this, android.R.layout.simple_list_item_1, currentDays);
////                displayCourseList.setAdapter(adapter);
////                displayCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////                    @Override
////                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                        createDeleteDialog(position);
////                    }
////                });
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // TODO: not sure if something needs to be done here??
//            }
//        });



        // get intent and get the value of newUser

        Intent intent = getIntent();
        newUser = intent.getBooleanExtra("newUser", false);

        if (newUser) { // new user choosing preferences, so buttonHome will say "Finished!"
            homeButton.setText("Finished!");
            saveButton.setVisibility(View.GONE); // it's going to save b/c this is first time, don't need a save button yet

            // show dialog telling new user what to do
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Now that you've added all your classes, let's set your preferences. " +
                    "This will help us match you to study buddies with similar studying " +
                    "preferences! Once you are done, press the Finished button at the bottom of " +
                    "your screen to complete your sign up.\n" +
                    "If you want to change your options, you can come back to this page at any time. " +
                    "Changing your preferences will not affect any matches you already have, it will only " +
                    "change how you are matched to others in the future.");
            builder.setTitle("Continuing On!");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // When the user click yes button, the dialog will close
                    dialog.dismiss();
                }
            });
            AlertDialog newUserWelcome = builder.create();
            newUserWelcome.show();

            //when the "Finished!" button is clicked by new users, the prefs will be saved to firebase
            homeButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(monday.isChecked() ){ addToDaysArray("Monday"); }
                    if(tuesday.isChecked() ){ addToDaysArray("Tuesday"); }
                    if(wednesday.isChecked() ){ addToDaysArray("Wednesday"); }
                    if(thursday.isChecked() ){addToDaysArray("Thursday");}
                    if(friday.isChecked() ){addToDaysArray("Friday");}
                    if(saturday.isChecked() ){addToDaysArray("Saturday");}
                    if(sunday.isChecked() ){addToDaysArray("Sunday"); }
                    if(lakeshore.isChecked() ){addToLocArray("Lakeshore"); }
                    if(college.isChecked() ){addToLocArray("College");}
                    if(state.isChecked() ){addToLocArray("State");}
                    if(engineering.isChecked() ){addToLocArray("Engineering");}
                    if(southeast.isChecked() ){addToLocArray("Southeast");  }

                    currentUserDays.setValue(userDays);
                    currentUserLocations.setValue(userLocations);

                    goHome(v);
                }
            });

        } else { // not a new user, do not show dialog, and buttonHome should say "Back to Home" & there should be a save changes button
            homeButton.setText("Back to Home"); //clicking back to home will *not* save changes.
            saveButton.setText("Save Changes");
            //add functionality for save changes button click

            //when the save button is clicked, the day or location is added to its respective arrayList.
            // here, for OLD USERS.
            saveButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(monday.isChecked() ){ addToDaysArray("Monday"); }
                    if(tuesday.isChecked() ){ addToDaysArray("Tuesday"); }
                    if(wednesday.isChecked() ){ addToDaysArray("Wednesday"); }
                    if(thursday.isChecked() ){addToDaysArray("Thursday");}
                    if(friday.isChecked() ){addToDaysArray("Friday");}
                    if(saturday.isChecked() ){addToDaysArray("Saturday");}
                    if(sunday.isChecked() ){addToDaysArray("Sunday"); }
                    if(lakeshore.isChecked() ){addToLocArray("Lakeshore"); }
                    if(college.isChecked() ){addToLocArray("College");}
                    if(state.isChecked() ){addToLocArray("State");}
                    if(engineering.isChecked() ){addToLocArray("Engineering");}
                    if(southeast.isChecked() ){addToLocArray("Southeast");  }

                    currentUserDays.setValue(userDays);
                    currentUserLocations.setValue(userLocations);

                    goHome(v);
                    //go home.

                }
            });
        }
    }

    public void backToHome(View view){
        Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra("newUser", newUser);
        startActivity(intent);
    }

    public void goHome(View view){
        Intent intent = new Intent(this, HomeScreen.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
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


    private void addToDaysArray(String day){
        if(!userDays.contains(day)){
            userDays.add(day);
        }
    }
    private void addToLocArray(String loc){
        if(!userLocations.contains(loc)){
            userLocations.add(loc);
        }
    }

//    // helper method to convert dataSnapshot into ArrayList<String>
//    private ArrayList<String> readCourseData(DataSnapshot dataSnapshot) {
//        String allCourses = dataSnapshot.getValue(String.class); // string of all classes
//        ArrayList<String> coursesArrayList = new ArrayList<String>(); // ArrayList to hold classes
//
//        if (allCourses != null) {
//            String[] courseArray = allCourses.split(", "); // split into string array at ", "
//
//            // move course names to ArrayList
//            for (String course : courseArray) {
//                coursesArrayList.add(course);
//            }
//        }
//
//        return coursesArrayList;
//    }

}
