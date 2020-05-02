package com.example.studious;

public class Connection {
    private String course;
    private String userName;

    public Connection(String course, String userName) {
      this.course = course;
      this.userName = userName;
    }

    public String getCourse() { return course; }

    public String getUserName() { return userName; }

}
