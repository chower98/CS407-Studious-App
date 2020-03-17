package com.example.studious;

public class Course {
    private String userEmail;
    private String date;
    private String title;
    private String status;

    public Course(String date, String userEmail, String title, String status) {
        this.userEmail = userEmail;
        this.date = date;
        this.title = title;
        this.status = status;
    }

    public String getDate() { return date; }

    public String getUserEmail() { return userEmail; }

    public String getTitle() { return title; }

    public String getStatus() { return status; }
}
