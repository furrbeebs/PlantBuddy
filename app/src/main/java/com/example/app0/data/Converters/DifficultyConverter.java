package com.example.app0.data.Converters;

import androidx.room.TypeConverter;

import com.example.app0.data.Local.Entity.Challenging;
import com.example.app0.data.Local.Entity.Difficulty;
import com.example.app0.data.Local.Entity.Easy;
import com.example.app0.data.Local.Entity.Medium;

public class DifficultyConverter {
    @TypeConverter
    public static Integer fromDifficulty(Difficulty difficulty) {
        if (difficulty == null) return null;
        return difficulty.incPoints();
    }

    @TypeConverter
    public static Difficulty toDifficulty(Integer score) {
        if (score == null) return null;

        switch (score) {
            case 3: return new Easy();
            case 6: return new Medium();
            case 10: return new Challenging();
            default:
                throw new IllegalArgumentException("Unknown difficulty points: " + score);
        }
    }

    public static String fromDifficultyToString(Difficulty difficulty) {
        if (difficulty == null) {
            return null;
        }
        return difficulty.getDifficultyAsString();
    }

    public static Difficulty toDifficultyFromString(String difficultyString) {
        if (difficultyString == null) return null;

        switch (difficultyString) {
            case "Easy":
                return new Easy();
            case "Medium":
                return new Medium();
            case "Challenging":
                return new Challenging();
            default:
                throw new IllegalArgumentException("Unknown difficulty string: " + difficultyString);
        }
    }
}
