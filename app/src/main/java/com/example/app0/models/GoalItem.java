package com.example.app0.models;

// Data team please edit as you see fit

public class GoalItem {
    private String title;
    private int points;
    private String frequency;
    private boolean completed;
    private String startDate; // Added field for edit dialog
    private String difficulty; // Added field for edit dialog

    public GoalItem(String title, int points, String frequency) {
        this(title, points, frequency, "24/02/25", "Easy"); // Default values
    }

    // New constructor with all fields
    public GoalItem(String title, int points, String frequency,
                    String startDate, String difficulty) {
        this.title = title;
        this.points = points;
        this.frequency = frequency;
        this.startDate = startDate;
        this.difficulty = difficulty;
        this.completed = false;
    }

    // Getters
    public String getTitle() { return title; }
    public int getPoints() { return points; }
    public String getFrequency() { return frequency; }
    public boolean isCompleted() { return completed; }
    public String getStartDate() { return startDate; }
    public String getDifficulty() { return difficulty; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public void setFrequency(String string) {

    }
}