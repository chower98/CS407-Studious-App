package com.example.studious;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

public class DBHelper {
    SQLiteDatabase sqLiteDatabase;

    // strings for table "users"
    private static final String TABLE_USERS = "users";
        private static final String KEY_ID = "id";
        private static final String KEY_EMAIL = "email";
        private static final String KEY_PASSWORD = "password";

    // strings for table "courses"
    private static final String TABLE_COURSES = "courses";
        private static final String KEY_DATE = "date";
        private static final String KEY_COURSE_NAME = "course_name";
        private static final String KEY_STATUS = "status";

    // strings to create tables
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS +
            "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_EMAIL + " TEXT, " + KEY_PASSWORD + " TEXT)";
    private static final String CREATE_TABLE_COURSES = "CREATE TABLE IF NOT EXISTS " + TABLE_COURSES +
            "(" + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_EMAIL + " TEXT, " + KEY_DATE + " TEXT, " +
            KEY_COURSE_NAME + " TEXT, " + KEY_STATUS + " TEXT)";

    public DBHelper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void createTables() {
        sqLiteDatabase.execSQL(CREATE_TABLE_USERS);
        sqLiteDatabase.execSQL(CREATE_TABLE_COURSES);
    }

    public int checkUserLogin(String email, String password) {
        // returns 0: user does not exist
        // returns 1: user exists, correct password
        // returns 2: user exists, incorrect password
        createTables();

        // create cursor for a query of TABLE_USERS with email passed into method
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM " + TABLE_USERS + " WHERE " +
                KEY_EMAIL + " like '%s'", email), null);

        int passwordIndex = c.getColumnIndex(KEY_PASSWORD);

        c.moveToFirst();
        while (!c.isAfterLast()) { // only goes through while loop if the email exists in TABLE_USERS
            if (c.getString(passwordIndex).equals(password)) {
                return 1; // password passed into method matches stored password
            } else {
                return 2; // user exists, but password did not match the stored one
            }
        }
        return 0; // email was not found in TABLE_USERS
    }

    public void addUser(String email, String password) {
        createTables();
        // insert email and password in TABLE_USERS
        sqLiteDatabase.execSQL(String.format("INSERT INTO " + TABLE_USERS + " (" +
                        KEY_EMAIL + ", " + KEY_PASSWORD + ") VALUES ('%s', '%s')", email, password));
    }

    public ArrayList<Course> readCourses(String email) {
        createTables();
        // create cursor for a query of TABLE_COURSES with email passed into method
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM " + TABLE_COURSES +
                " WHERE " + KEY_EMAIL + " like '%s'", email), null);

        int dateIndex = c.getColumnIndex(KEY_DATE);
        int courseNameIndex = c.getColumnIndex(KEY_COURSE_NAME);
        int statusIndex = c.getColumnIndex(KEY_STATUS);

        ArrayList<Course> coursesList = new ArrayList<>();

        c.moveToFirst();
        while (!c.isAfterLast()) {
            String courseName = c.getString(courseNameIndex);
            String date = c.getString(dateIndex);
            String status = c.getString(statusIndex);

            Course course = new Course(date, email, courseName, status);
            coursesList.add(course);
            c.moveToNext();
        }

        return coursesList;
    }

    public boolean duplicateCourseCheck(String email, String courseName) {
        // returns true if duplicate course found; returns false if course is not added yet
        ArrayList<Course> courses = this.readCourses(email);

        for (Course course : courses) {
            if (course.getName().equals(courseName)) {
                return true; // return true if duplicate course found
            }
        }

        return false; // no duplicate course found
    }

    public void addCourses(String email, String name, String status) {
        createTables();
        // insert new course with parameters passed in into TABLE_COURSES
        sqLiteDatabase.execSQL(String.format("INSERT INTO " + TABLE_COURSES + " (" + KEY_EMAIL +
                        ", " + KEY_COURSE_NAME + ", " + KEY_STATUS +
                        ") VALUES ('%s', '%s', '%s', '%s')", email, name, status));
    }

    public void deleteCourse(String email, String name) {
        createTables();

        sqLiteDatabase.execSQL(String.format("DELETE FROM " + TABLE_COURSES + " WHERE " + KEY_EMAIL
                + " like '%s' AND " + KEY_COURSE_NAME + " like '%s'", email, name), null);
    }

    private void printEmailsInLog() { // TODO: FOR DEBUGGING PURPOSES, DELETE LATER
        createTables();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM users"), null);
        int emailIndex = c.getColumnIndex("email");
        c.moveToFirst();
        ArrayList<String> emails = new ArrayList<>();
        while (!c.isAfterLast()) {
            String email = c.getString(emailIndex);
            emails.add(email);
            c.moveToNext();
        }

        Log.d("Added Emails", emails.toString());
    }

    private void printCoursesInLog() { // TODO: FOR DEBUGGING PURPOSES, DELETE LATER
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM courses"), null);
        int nameIndex = c.getColumnIndex("name");
        c.moveToFirst();
        ArrayList<String> coursesList = new ArrayList<>();
        while (!c.isAfterLast()) {
            String name = c.getString(nameIndex);
            coursesList.add(name);
            c.moveToNext();
        }
        Log.d("Added Courses", coursesList.toString());
    }

}