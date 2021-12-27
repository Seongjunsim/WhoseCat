package com.jmsmart.whosecat.Database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "CalendarTable")
public class CalendarTable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "CalendarID")
    private int CalendarID;
    @ColumnInfo(name = "UserID")
    private int UserID;
    @ColumnInfo(name = "Category")
    private int Category;
    @ColumnInfo(name = "Comment")
    private String Comment;
    @ColumnInfo(name = "Time")
    private long Time;

    public void setCalendarID(int CalendarID){this.CalendarID=CalendarID;}
    public void setUserID(int UserID){this.UserID=UserID;}
    public void setCategory(int Category){this.Category=Category;}
    public void setComment(String Comment){this.Comment=Comment;}
    public void setTime(long Time){this.Time=Time;}

    public int getCalendarID(){return CalendarID;}
    public int getUserID(){return UserID;}
    public int getCategory(){return Category;}
    public String getComment(){return Comment;}
    public long getTime(){return Time;}
}
