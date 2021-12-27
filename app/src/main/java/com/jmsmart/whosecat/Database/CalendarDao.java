package com.jmsmart.whosecat.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CalendarDao {
    @Query("SELECT * FROM CalendarTable WHERE UserID IS :userId AND Time IS :Time")
    List<CalendarTable> loadAllByIdAndTime(int userId, long Time);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCalendarData(CalendarTable calendarTable);

    @Query("UPDATE CalendarTable SET UserID = :userID, Category = :category, Comment = :comment, Time = :time WHERE CalendarID = :calendarID")
    int updateCalendarData(int calendarID, int userID, int category, String comment, long time);

    @Query("DELETE FROM CalendarTable WHERE CalendarID = :calendarId")
    void deleteCalendarData(int calendarId);
}