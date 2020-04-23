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

    int courseId = -1;
    public static ArrayList<String> courses;
    private ArrayList<String> displayCourses;
    private String currentUser;
    private DBHelper dbHelper;
    private Button nextButton;
    private boolean newUser;

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";
    private final static String DATABASE_NAME = "data";
    //SQLiteDatabase sqLiteDatabase;

    DatabaseReference currentUserCourses;
    String newCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString("email", "");

        nextButton = findViewById(R.id.nextButton); // reference to nextButton

        displayCourses = new ArrayList<>();
        String userName = currentUser.substring(0, currentUser.length() - 9); // remove "@wisc.edu" from email
        currentUserCourses = FirebaseDatabase.getInstance().getReference().child("UserPref").child(userName).child("Courses");
        currentUserCourses.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = dataSnapshot.exists();
                if (exists) // TODO: for debugging
                    Log.e("does data even exist?", "true");
                else
                    Log.e("does data even exist?", "false");
                readCourseData(dataSnapshot);
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
        } else { // not a new user, do not show nextButton or welcome dialog
            nextButton.setVisibility(View.GONE);
        }

        Log.e("courses to display 1", displayCourses.toString());
        refresh();
    }

    private void readCourseData(DataSnapshot dataSnapshot) {
        String courseList = dataSnapshot.getValue(String.class);
        Log.e("reading data", courseList + " ");
        String[] courseArray = courseList.split(",");
        displayCourses.clear();
        for (String course : courseArray) {
            displayCourses.add(course);
        }
        Log.e("courses to display 2", displayCourses.toString());
    }

    public void addClass(View view) {
        Spinner courseList = findViewById(R.id.courseList);
        EditText courseNumber = findViewById(R.id.classNumber);

        // get course details
        String department = courseList.getSelectedItem().toString();
        String number = courseNumber.getText().toString();

        // check to make sure class to add is fully specified
        if (department.contains("Select Subject") || number.isEmpty()) {
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
            return; // end method so that class without enough details is not added
        }

        // get full course name
        newCourse = department + " " + number;

        // instantiate FirebaseHelper and add new course
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        //firebaseHelper.addCourse(newCourse); // TODO: not functional yet

        currentUserCourses.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String newCourseList = addClassHelper(dataSnapshot);
                currentUserCourses.setValue(newCourseList);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // TODO: not sure if something needs to be done here??
            }
        });

        //displayCourses.add(newCourse);
        refresh();
    }

    // helper method that does Firebase storing
    private String addClassHelper(DataSnapshot dataSnapshot) {
        String courseList = dataSnapshot.getValue(String.class);
        courseList = courseList + ", " + newCourse;
        return courseList;
    }

    public void refresh() {
        //SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        //currentUser = sharedPreferences.getString(EMAIL_KEY, "");

        // get SQLiteDatabase instance and initiate notes ArrayList by using DBHelper
        Context context = getApplicationContext();
        //sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        //dbHelper = new DBHelper(sqLiteDatabase);
        //courses = dbHelper.readCourses(currentUser);

        // create ArrayList<String> by iterating courses object

//        for (Course course : courses) {
//            displayCourses.add(String.format("Course: %s\nStatus: %s\n", course.getName(), course.getStatus()));
//        }

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, displayCourses);
        ListView listView = findViewById(R.id.classHolder);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDialog(position);
            }
        });
    }

    public void openDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(AddClasses.this);
        builder.setMessage("Remove " + displayCourses.get(position) + " from Courses?")
                .setTitle("Remove Course?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        displayCourses.remove(position);
//                        dbHelper.deleteCourse(EMAIL_KEY, currentUser);
//                        courses = dbHelper.readCourses(currentUser);
                        refresh();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {}
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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
