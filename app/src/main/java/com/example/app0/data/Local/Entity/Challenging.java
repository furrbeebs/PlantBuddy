package com.example.app0.data.Local.Entity;

public class Challenging implements Difficulty {
    @Override
    public int incPoints() {
        return 10;
    }

    @Override
    public String getDifficultyAsString() {
        return "Hard";
    }
}
