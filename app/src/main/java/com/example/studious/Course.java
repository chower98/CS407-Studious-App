package com.example.studious;

public class Course {
    private String username;
    private String date;
    private String title;
    private String status;

    public Course(String date, String username, String title, String status) {
        this.username = username;
        this.date = date;
        this.title = title;
        this.status = status;
    }

    public String getDate() { return date; }

    public String getUsername() { return username; }

    public String getTitle() { return title; }

    public String getStatus() { return status; }
}
