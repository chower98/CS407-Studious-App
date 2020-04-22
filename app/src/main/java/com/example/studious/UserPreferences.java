package com.example.studious;

import java.util.ArrayList;

public class UserPreferences {
    private ArrayList<String> days;
    private ArrayList<String> locations;
    private ArrayList<String> courses;

    public UserPreferences() {
        // default constructor for database
    }

    public UserPreferences(ArrayList<String> days, ArrayList<String> locations, ArrayList<String> courses) {
        this.days = days;
        this.locations = locations;
        this.courses = courses;
    }

    public ArrayList<String> getDays() {
        return days;
    }

    public void setDays(ArrayList<String> days) {
        this.days = days;
    }

    public ArrayList<String> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<String> locations) {
        this.locations = locations;
    }

    public ArrayList<String> getCourses() {
        return courses;
    }

    public void setCourses(ArrayList<String> courses) {
        this.courses = courses;
    }

}
