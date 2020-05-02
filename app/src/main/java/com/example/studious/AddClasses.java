package com.example.studious;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddClasses extends AppCompatActivity {

    private String currentUserEmail; // full email of current user
    private String currentNetID; // netID of current user
    private boolean newUser; // checks whether user is new or returning
    private Button nextButton; // Button for new users to proceed; doesn't show for returning users

    private ArrayList<String> currentCourses; // ArrayList of current courses
    private ListView displayCourseList; // ListView that displays courses for users
    private DatabaseReference currentUserCourses; // database reference to specific user's courses
    private String newCourse; // holds name of course to add

    // for shared preferences
    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        // initialize fields
        nextButton = findViewById(R.id.nextButton);
        displayCourseList = findViewById(R.id.classHolder);
        currentCourses = new ArrayList<>();

        // get email and net id from sharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        currentUserEmail = sharedPreferences.getString("email", "");
        currentNetID = currentUserEmail.substring(0, currentUserEmail.length() - 9); // remove "@wisc.edu" from email

        // initialize FireBase reference and add listener to read data
        currentUserCourses = FirebaseDatabase.getInstance().getReference().child("UserPref").child(currentNetID).child("Courses");
        currentUserCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // helper method to convert dataSnapshot into ArrayList<String>
                currentCourses = readCourseData(dataSnapshot);

                // set adapter of the listView
                ArrayAdapter adapter = new ArrayAdapter(AddClasses.this, android.R.layout.simple_list_item_1, currentCourses);
                displayCourseList.setAdapter(adapter);
                displayCourseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        createDeleteDialog(position);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: not sure if something needs to be done here??
            }
        });

        // get intent and get the value of newUser
        Intent intent = getIntent();
        newUser = intent.getBooleanExtra("newUser", false);

        if (newUser) { // new user adding classes, so nextButton must be visible
            nextButton.setVisibility(View.VISIBLE);
            createNewUserDialog(); // create dialog for new user

        } else { // not a new user, do not show nextButton or welcome dialog
            nextButton.setVisibility(View.GONE);
        }
    }

    private ArrayList<String> readCourseData(DataSnapshot dataSnapshot) {
        String allCourses = dataSnapshot.getValue(String.class); // string of all classes
        ArrayList<String> coursesArrayList = new ArrayList<String>(); // ArrayList to hold classes

        if (allCourses != null) {
            String[] courseArray = allCourses.split(", "); // split into string array at ", "

            // move course names to ArrayList
            for (String course : courseArray) {
                coursesArrayList.add(course);
            }
        }

        return coursesArrayList;
    }

    public void addClass(View view) {
        Spinner courseList = findViewById(R.id.courseList);
        EditText courseNumber = findViewById(R.id.classNumber);

        // get course details
        String department = courseList.getSelectedItem().toString();
        String number = courseNumber.getText().toString();

        // check to make sure class to add is fully specified
        if (department.contains("Select Subject") || number.isEmpty()) {
            createCourseAlertDialog();
            return; // end method so that class without enough details is not added
        }

        newCourse = department + " " + number; // get full course name

        // single value event listener to add new class
        currentUserCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String courseList = dataSnapshot.getValue(String.class); // get current courseList
                if (courseList == null) {
                    courseList = newCourse;
                } else {
                    courseList = courseList + ", " + newCourse; // add new class to courseList
                }
                currentUserCourses.setValue(courseList); // update database value of courseList
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: not sure if something needs to be done here??
            }
        });
    }

    private void createNewUserDialog() {
        // show dialog telling new user what to do
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Welcome to Studious, an app that will help you connect to " +
                "other study partners at UW-Madison! Please add the classes that you would " +
                "like to find study buddies for. When you are finished, click the Continue " +
                "button. You can always come back to this page from your home screen if you'd " +
                "like to edit your list of classes!");
        builder.setTitle("Hi There!");

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // When the user click yes button, the dialog will close
                dialog.dismiss();
            }
        });
        AlertDialog newUserWelcome = builder.create();
        newUserWelcome.show();
    }

    private void createCourseAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddClasses.this);

        builder.setMessage("Cannot add class without a subject or course number!");
        builder.setTitle("Alert!");

        builder.setCancelable(false);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // When the user click yes button, then dialog will close
                dialog.dismiss();
            }
        });
        AlertDialog duplicateEmailAlert = builder.create();
        duplicateEmailAlert.show();
    }

    private void createDeleteDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddClasses.this);
        builder.setMessage("Remove " + currentCourses.get(position) + " from Courses?")
                .setTitle("Remove Course?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String toRemove = currentCourses.get(position);
                        deleteCourse(toRemove); // call helper method to remove course
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteCourse(final String courseToDelete) {
        // single value event listener to delete class
        currentUserCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // helper method to convert dataSnapshot into ArrayList<String>
                ArrayList<String> current = readCourseData(dataSnapshot);

                current.remove(courseToDelete); // remove course from ArrayList current

                // convert ArrayList to string of all courses
                String updatedCourses = null;
                if (!current.isEmpty()) {
                    for (String course : current) {
                        if (updatedCourses == null) {
                            updatedCourses = course;
                        } else {
                            updatedCourses = updatedCourses + ", " + course;
                        }
                    }
                }

                currentUserCourses.setValue(updatedCourses); // update database value of Courses
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: not sure if something needs to be done here??
            }
        });
    }

    public void continueSignup(View view) {
        Intent continueIntent = new Intent(this, Preferences.class);
        continueIntent.putExtra("newUser", newUser);
        startActivity(continueIntent);
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

}
