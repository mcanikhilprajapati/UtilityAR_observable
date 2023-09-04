package com.utility.app.models;


public class Observations {

    // variables for our course name,
    // description and duration.
    private String courseName;
    private String courseModes;
    private String courseTracks;
    private String courseImg;

    // constructor class.
    public Observations(String courseName) {
        this.courseName = courseName;
    }

    // getter and setter methods.
    public String getCourseImg() {
        return courseImg;
    }

    public void setCourseImg(String courseImg) {
        this.courseImg = courseImg;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getcourseModes() {
        return courseModes;
    }

    public void setcourseModes(String courseModes) {
        this.courseModes = courseModes;
    }

    public String getcourseTracks() {
        return courseTracks;
    }

    public void setcourseTracks(String courseTracks) {
        this.courseTracks = courseTracks;
    }
}
