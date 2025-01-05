package com.example.managerportal.Models;

public class NewCourse {
    private String Name;
    private String Image;

    public NewCourse(){} // Required for Firestore deserialization
    public String getName() {
        return Name;
    }

    public String getImage() {
        return Image;
    }

    public NewCourse(String name, String image) {
        Name = name;
        Image = image;
    }

}
