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
    private ArrayList<String> displayCourses;
    private String currentUser;
    private DBHelper dbHelper;

    private final static String EMAIL_KEY = "email";
    private final static String PASSWORD_KEY = "password";
    private final static String PACKAGE_NAME = "com.example.studious";
    private final static String DATABASE_NAME = "data";
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_classes);
        refresh();
    }

    public void refresh() {
        SharedPreferences sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        currentUser = sharedPreferences.getString(EMAIL_KEY, "");

        // get SQLiteDatabase instance and initiate notes ArrayList by using DBHelper
        Context context = getApplicationContext();
        sqLiteDatabase = context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
        dbHelper = new DBHelper(sqLiteDatabase);
        courses = dbHelper.readCourses(currentUser);

        // create ArrayList<String> by iterating courses object
        displayCourses = new ArrayList<String>();
        for (Course course : courses) {
            displayCourses.add(String.format("Course: %s\nDate Added: %s\nStatus: %s\n", course.getName(),
                    course.getDate(), course.getStatus()));
        }

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
                        dbHelper.deleteCourse(EMAIL_KEY, currentUser);
                        courses = dbHelper.readCourses(currentUser);
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

    public void addClass(View view) {
        Spinner courseList = findViewById(R.id.courseList);
        EditText courseNumber = findViewById(R.id.classNumber);

        // get course details
        String department = courseList.getSelectedItem().toString();
        String number = courseNumber.getText().toString();
        String courseToAdd = department + number;

        DBHelper dbHelper = new DBHelper(sqLiteDatabase);
        dbHelper.addCourses(currentUser, EMAIL_KEY, courseToAdd);
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
