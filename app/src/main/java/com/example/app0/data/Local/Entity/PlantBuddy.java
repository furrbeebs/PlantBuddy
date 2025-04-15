package com.example.app0.data.Local.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PlantBuddy {

    @PrimaryKey
    private int userid = 1;

    @ColumnInfo(name = "username")
    public String username;

    @ColumnInfo(name = "plantname")
    public String plantname;

    @ColumnInfo(name = "level")
    public int level;

    @ColumnInfo(name = "xp")
    public double xp;

    @ColumnInfo(name = "image")
    public int image;

    public PlantBuddy() {}

    public PlantBuddy(String username, String plantname, int level, double xp, int image) {
        this.username = username;
        this.plantname = plantname;
        this.level = level;
        this.xp = xp;
        this.image = image;
    }

    // Getters
    public int getUserid() { return userid; }
    public String getUsername() { return username; }
    public String getPlantname() { return plantname; }
    public int getLevel() { return level; }
    public double getXp() { return xp; }
    public int getImage() { return image; }

    // Setters
    public void setUserid(int userid) { this.userid = userid; }
    public void setUsername(String username) { this.username = username; }
    public void setPlantname(String plantname) { this.plantname = plantname; }
    public void setLevel(int level) { this.level = level; }
    public void setXp(double xp) { this.xp = xp; }
    public void setImage(int image) { this.image = image; }

}
