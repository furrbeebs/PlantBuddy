package com.example.app0.data.Local.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.app0.data.Converters.CalendarConverter;
import com.example.app0.data.Converters.DifficultyConverter;
import com.example.app0.data.Converters.RepeatConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

@Entity(tableName = "goalTable")
public class Goal {

    @ColumnInfo(name = "goalId")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "title")
    private String title;

    @TypeConverters(RepeatConverter.class)
    @ColumnInfo(name = "repeat")
    private Repeat repeat;

    @TypeConverters(DifficultyConverter.class)
    @ColumnInfo(name = "difficulty")
    private Difficulty difficulty;

    @ColumnInfo(name = "startDate")
    private Calendar startDate;

    @ColumnInfo(name = "untilDate")
    private Calendar untilDate;

    @ColumnInfo(name = "excluded_dates")
    private String excludedDates; // Store as comma-separated YYYY-MM-DD dates

    @Ignore
    // Constructor for minimal initialization
    public Goal(String title, String frequency) {
        Calendar startingDate = Calendar.getInstance();
        startingDate.set(Calendar.HOUR_OF_DAY, 0);
        startingDate.set(Calendar.MINUTE, 0);
        startingDate.set(Calendar.SECOND, 0);
        startingDate.set(Calendar.MILLISECOND, 0);

        this.title = title;
        this.repeat = RepeatConverter.toRepeat(frequency);
        this.difficulty = DifficultyConverter.toDifficultyFromString("Easy");
        this.startDate = startingDate;
    }

    @Ignore
    // Constructor with just title
    public Goal(String title) {
        this(title, "Daily");
    }

    // Full constructor
    public Goal(String title, Calendar startDate, Repeat repeat, Calendar untilDate,
                Difficulty difficulty) {
        this.title = title;
        this.startDate = startDate;
        this.repeat = repeat;
        this.untilDate = untilDate;
        this.difficulty = difficulty;
    }

    // Getters and setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat frequency) {
        this.repeat = frequency;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Calendar getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Calendar untilDate) {
        this.untilDate = untilDate;
    }

    public String getExcludedDates() {
        return excludedDates == null ? "" : excludedDates;
    }

    public void setExcludedDates(String excludedDates) {
        this.excludedDates = excludedDates;
    }

    public List<String> getExcludedDatesList() {
        if (excludedDates == null || excludedDates.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(excludedDates.split(","));
    }

    public void addExcludedDate(String dateId) {
        List<String> excludedList = new ArrayList<>(getExcludedDatesList());
        if (!excludedList.contains(dateId)) {
            excludedList.add(dateId);
            this.excludedDates = String.join(",", excludedList);
        }
    }

    @Override
    public String toString() {
        return title;
    }
}