package com.example.studious;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString("email", "");

        // get SQLiteDatabase instance and initiate notes ArrayList by using DBHelper
        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("data", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        courses = dbHelper.readCourses(currentUser);

        // create ArrayList<String> by iterating courses object
        ArrayList<String> displayCourses = new ArrayList<>();
        for (Course course : courses) {
            displayCourses.add(String.format("Course: %s\nDate Added: %s\nStatus: %s\n", course.getTitle(),
                    course.getDate(), course.getStatus()));
        }

        // use ListView view to display courses on screen
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, displayCourses);
        ListView listView = findViewById(R.id.classHolder);
        listView.setAdapter(adapter);
    }

    public void addClass(View view) {
        Spinner courseList = findViewById(R.id.courseList);
        EditText courseNumber = findViewById(R.id.classNumber);
        // get full course name
        String courseName = courseList.getSelectedItem().toString() + " " +courseNumber.getText().toString();

        Context context = getApplicationContext();
        SQLiteDatabase sqLiteDatabase = context.openOrCreateDatabase("data", Context.MODE_PRIVATE, null);
        DBHelper dbHelper = new DBHelper(sqLiteDatabase);

        SharedPreferences sharedPreferences = getSharedPreferences("com.example.studious", Context.MODE_PRIVATE);
        String username = sharedPreferences.getString("email", "");

        String title;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        String date = dateFormat.format(new Date());

        if (courseId == -1) { // add new course
            title = courseName;
            String status = "No Matches Yet";
            dbHelper.addCourses(username, title, status, date);
        } else {
            // TODO: course is already added, show some kind of dialog/alert???
        }

        refresh();
    }

    public void refresh() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }

}
