package com.example.app0.data.Local.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.app0.data.Converters.DifficultyConverter;
import com.example.app0.data.Converters.RepeatConverter;

import java.util.Calendar;

@Entity(tableName = "goalInstances")
public class GoalInstance {
    @ColumnInfo(name = "instanceId")
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "goalId")
    public long goalId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "startDate")
    private Calendar startDate;

    @TypeConverters(RepeatConverter.class)
    @ColumnInfo(name = "repeat")
    private Repeat repeat;

    @ColumnInfo(name = "untilDate")
    private Calendar untilDate;

    @TypeConverters(DifficultyConverter.class)
    @ColumnInfo(name = "difficulty")
    private Difficulty difficulty;

    @ColumnInfo(name = "instanceDate")
    public Calendar instanceDate;

    @ColumnInfo(name = "completed")
    public Boolean completed;

    @Ignore
    public GoalInstance() {

    }

    public GoalInstance(long goalId, String title, Calendar startDate, Repeat repeat, Calendar untilDate, Difficulty difficulty, Calendar instanceDate, Boolean completed) {
        this.goalId = goalId;
        this.title = title;
        this.startDate = startDate;
        this.repeat = repeat;
        this.untilDate = untilDate;
        this.difficulty = difficulty;
        this.instanceDate = instanceDate;
        this.completed = completed;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGoalId() {
        return goalId;
    }

    public void setGoalId(long goalId) {
        this.goalId = goalId;
    }

    public Calendar getInstanceDate() {
        return instanceDate;
    }

    public void setInstanceDate(Calendar instanceDate) {
        this.instanceDate = instanceDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public void setStartDate(Calendar startDate) {
        this.startDate = startDate;
    }

    public Repeat getRepeat() {
        return repeat;
    }

    public void setRepeat(Repeat repeat) {
        this.repeat = repeat;
    }

    public Calendar getUntilDate() {
        return untilDate;
    }

    public void setUntilDate(Calendar untilDate) {
        this.untilDate = untilDate;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    @Override
    public String toString() {
        return title;
    }
}
