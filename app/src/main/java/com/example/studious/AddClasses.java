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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddClasses extends AppCompatActivity {

    int courseId = -1;
    public static ArrayList<Course> courses;
    private String currentUser;
    private DBHelper dbHelper;

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";
    private final static String DATABASE_NAME = "data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString(EMAIL_KEY, "");

        // get SQLiteDatabase instance and initiate notes ArrayList by using DBHelper
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        courses = dbHelper.readCourses(currentUser);

        // create ArrayList<String> by iterating courses object
        ArrayList<String> displayCourses = new ArrayList<>();
        for (Course course : courses) {
            displayCourses.add(String.format("Course: %s\nDate Added: %s\nStatus: %s\n", course.getName(),
                    course.getDate(), course.getStatus()));
        }

        // use ListView view to display courses on screen
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, displayCourses);
        ListView listView = findViewById(R.id.classHolder);
        listView.setAdapter(adapter);

        // add onItemClickListener for each item in the list
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: have fragment pop up with info and delete option
                loadFragment(new CourseFragment());
            }
        });
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
        String courseName = department + " " + number;

        // check to make sure course has not already been added
        boolean duplicateExists = dbHelper.duplicateCourseCheck(currentUser, courseName);
        if (duplicateExists) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AddClasses.this);

            builder.setMessage("Class has already been added!");
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
            return; // end method so that duplicate class is not added
        }

        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        String username = sharedPreferences.getString(EMAIL_KEY, "");

        String name;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());

        if (courseId == -1) { // add new course
            name = courseName;
            String status = "No Matches Yet";
            dbHelper.addCourses(username, name, status, date);
        } else {
            // TODO: not sure what is going on here? why is this if-else loop needed
        }

        refresh(); // method to refresh courses page to reflect changes
    }

    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

    // TODO: SOMETHING IS WRONG
    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.classHolder, fragment);
        fragmentTransaction.commit();
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
