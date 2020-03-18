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

    // strings for table "courses"
    private static final String TABLE_COURSES = "courses";
        private static final String KEY_DATE = "date";
        private static final String KEY_COURSE_NAME = "course_name";
        private static final String KEY_STATUS = "status";

    // strings to create tables
    private static final String CREATE_TABLE_USERS = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS +
            "(";

    public DBHelper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void createTables() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users " +
                "(id INTEGER PRIMARY KEY, email TEXT, password TEXT)");
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS courses " +
                "(id INTEGER PRIMARY KEY, email TEXT, date TEXT, title TEXT, status TEXT)");


    }

    public void createUserTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users " +
                "(id INTEGER PRIMARY KEY, email TEXT, password TEXT, src TEXT)");
    }

    public int checkUserLogin(String email, String password) {
        // returns 0: user does not exist
        // returns 1: user exists, correct password
        // returns 2: user exists, incorrect password
        createTables(); //createUserTable();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM users"), null);

        int emailIndex = c.getColumnIndex("email");
        int passwordIndex = c.getColumnIndex("password");

        c.moveToFirst();

        while (!c.isAfterLast()) {
            if (c.getString(emailIndex).equals(email)) {
                if (c.getString(passwordIndex).equals(password)) {
                    return 1;
                } else {
                    return 2;
                }
            } else {
                return 0;
            }
        }
        return 0;
    }

    public void addUser(String email, String password) {
        createTables(); //createUserTable();
        sqLiteDatabase.execSQL(String.format("INSERT INTO users (email, password) VALUES ('%s', '%s')",
                email, password));

        printEmailsInLog(); // for debugging purposes, delete later
        printCoursesInLog();
    }

    public void printEmailsInLog() {
        createTables(); //createCoursesTable();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM users"), null);

        int emailIndex = c.getColumnIndex("email");
        int passwordIndex = c.getColumnIndex("password");

        c.moveToFirst();

        ArrayList<String> emails = new ArrayList<>();

        while (!c.isAfterLast()) {
            String email = c.getString(emailIndex);

            emails.add(email);
            c.moveToNext();
        }
        //c.close();
        //sqLiteDatabase.close();

        Log.e("Added Emails", emails.toString());
    }

    public void printCoursesInLog() {
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM courses"), null);

        int titleIndex = c.getColumnIndex("title");

        c.moveToFirst();

        ArrayList<String> coursesList = new ArrayList<>();

        while (!c.isAfterLast()) {
            String title = c.getString(titleIndex);
            coursesList.add(title);
            c.moveToNext();
        }
        //c.close();
        //sqLiteDatabase.close();

        Log.e("Added Courses", coursesList.toString());
    }

    public void createCoursesTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS courses " +
                "(id INTEGER PRIMARY KEY, email TEXT, date TEXT, title TEXT, status TEXT, src TEXT)");
    }

    public ArrayList<Course> readCourses(String email) {
        createTables(); //createCoursesTable();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM courses WHERE email like '%s'", email), null);

        int dateIndex = c.getColumnIndex("date");
        int titleIndex = c.getColumnIndex("title");
        int statusIndex = c.getColumnIndex("status");

        c.moveToFirst();

        ArrayList<Course> coursesList = new ArrayList<>();

        while (!c.isAfterLast()) {
            String title = c.getString(titleIndex);
            String date = c.getString(dateIndex);
            String status = c.getString(statusIndex);

            Course note = new Course(date, email, title, status);
            coursesList.add(note);
            c.moveToNext();
        }
        //c.close();
        //sqLiteDatabase.close();

        return coursesList;
    }

    public void saveCourses(String email, String title, String status, String date) {
        createTables(); //createCoursesTable();
        sqLiteDatabase.execSQL(String.format("INSERT INTO courses (email, date, title, status) VALUES ('%s', '%s', '%s', '%s')",
                email, date, title, status));
        printCoursesInLog();
    }

}
