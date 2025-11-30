package com.example.demoapp.model;

public class HistoryLog {
    private String eventType;
    private String timestamp;
    private String details;

    // Constructor
    public HistoryLog(String eventType, String timestamp, String details) {
        this.eventType = eventType;
        this.timestamp = timestamp;
        this.details = details;
    }

    // Getters
    public String getEventType() { return eventType; }
    public String getTimestamp() { return timestamp; }
    public String getDetails() { return details; }
}