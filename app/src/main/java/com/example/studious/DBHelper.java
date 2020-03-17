package com.example.studious;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHelper {
    SQLiteDatabase sqLiteDatabase;

    public DBHelper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void createUserTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users" +
                "(id INTEGER PRIMARY KEY, email TEXT, password TEXT)");
    }

    public int checkUserLogin(String email, String password) {
        // returns 0: user does not exist or incorrect password
        // returns 1: user exists, correct password
        createUserTable();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM users WHERE email like %s", email), null);

        int emailIndex = c.getColumnIndex("email");
        int passwordIndex = c.getColumnIndex("password");

        c.moveToFirst();

        if (c.getString(0) != null) { // email is found in database
            if (c.getString(1).equals(password)) { // correct password inputted
                return 1;
            } else { // incorrect password inputted
                return 0;
            }
        } else { // email not found
            return 0;
        }
    }

    public void addUser(String email, String password) {
        createUserTable();
        sqLiteDatabase.execSQL(String.format("INSERT INTO users (email, password) VALUES ('%s', '%s')",
                email, password));
    }

    public void createCoursesTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS courses " +
                "(id INTEGER PRIMARY KEY, userEmail TEXT, date TEXT, title TEXT, status TEXT, src TEXT)");
    }

    public ArrayList<Course> readCourses(String userEmail) {
        createCoursesTable();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM courses WHERE userEmail like '%s'", userEmail), null);

        int dateIndex = c.getColumnIndex("date");
        int titleIndex = c.getColumnIndex("title");
        int statusIndex = c.getColumnIndex("status");

        c.moveToFirst();

        ArrayList<Course> coursesList = new ArrayList<>();

        while (!c.isAfterLast()) {
            String title = c.getString(titleIndex);
            String date = c.getString(dateIndex);
            String status = c.getString(statusIndex);

            Course note = new Course(date, userEmail, title, status);
            coursesList.add(note);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();

        return coursesList;
    }

    public void saveCourses(String userEmail, String title, String status, String date) {
        createCoursesTable();
        sqLiteDatabase.execSQL(String.format("INSERT INTO courses (userEmail, date, title, status) VALUES ('%s', '%s', '%s', '%s')",
                userEmail, date, title, status));
    }

}
