package com.example.studious;

public class Course {
    private String userEmail;
    private String date;
    private String name;
    private String status;

    public Course(String date, String userEmail, String name, String status) {
        this.userEmail = userEmail;
        this.date = date;
        this.name = name;
        this.status = status;
    }

    public String getDate() { return date; }

    public String getUserEmail() { return userEmail; }

    public String getName() { return name; }

    public String getStatus() { return status; }

    }
