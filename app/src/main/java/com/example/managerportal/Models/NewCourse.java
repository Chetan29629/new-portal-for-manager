package com.example.managerportal.Models;

public class NewCourse {
    private String Name;
    private String URL;

    public NewCourse(){} // Required for Firestore deserialization
    public String getName() {
        return Name;
    }

    public String getURL() {
        return URL;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void setURL(String URL) {
        this.URL = URL;

    }

    public NewCourse(String name, String URL) {
        Name = name;
        this.URL = URL;
    }

}
