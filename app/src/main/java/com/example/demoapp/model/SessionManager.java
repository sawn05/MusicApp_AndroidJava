package com.example.demoapp.model;

public class SessionManager {

    private static SessionManager instance;
    private String currentUsername;

    private SessionManager() {
    }

    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void setUsername(String username) {
        this.currentUsername = username;
    }

    public String getUsername() {
        return currentUsername;
    }
}
