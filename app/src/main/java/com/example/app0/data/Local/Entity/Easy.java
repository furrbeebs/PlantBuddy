package com.example.app0.data.Local.Entity;

public class Easy implements Difficulty {
    @Override
    public int incPoints() {
        return 3;
    }

    @Override
    public String getDifficultyAsString() {
        return "Easy";
    }
}
