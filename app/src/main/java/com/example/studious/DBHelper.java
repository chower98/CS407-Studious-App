package com.example.studious;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class DBHelper {
    SQLiteDatabase sqLiteDatabase;

    public DBHelper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS courses " +
                "(id INTEGER PRIMARY KEY, username TEXT, date TEXT, title TEXT, status TEXT, src TEXT)");
    }

    public ArrayList<Course> readCourses(String username) {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * FROM courses WHERE username like '%s'", username), null);

        int dateIndex = c.getColumnIndex("date");
        int titleIndex = c.getColumnIndex("title");
        int statusIndex = c.getColumnIndex("status");

        c.moveToFirst();

        ArrayList<Course> coursesList = new ArrayList<>();

        while (!c.isAfterLast()) {
            String title = c.getString(titleIndex);
            String date = c.getString(dateIndex);
            String status = c.getString(statusIndex);

            Course note = new Course(date, username, title, status);
            coursesList.add(note);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();

        return coursesList;
    }

    public void saveCourses(String username, String title, String status, String date) {
        createTable();
        sqLiteDatabase.execSQL(String.format("INSERT INTO courses (username, date, title, status) VALUES ('%s', '%s', '%s', '%s')",
                username, date, title, status));
    }

}
