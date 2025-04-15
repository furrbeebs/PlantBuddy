package com.example.app0.data.Local.Entity;

public class Medium implements Difficulty {
    @Override
    public int incPoints() {
        return 6;
    }

    @Override
    public String getDifficultyAsString() {
        return "Medium";
    }
}
