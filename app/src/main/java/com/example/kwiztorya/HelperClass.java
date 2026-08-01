package com.example.kwiztorya;

public class HelperClass {
    // Field Variables (only username and password)
    String username, password;

    // Default (Empty) Constructor (REQUIRED by Firebase)
    public HelperClass() {
    }

    // Parameterized Constructor (only username and password)
    public HelperClass(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and Setters (REQUIRED by Firebase)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}