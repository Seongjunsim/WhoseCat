package com.jmsmart.whosecat.Database;

import androidx.room.TypeConverter;

import com.jmsmart.whosecat.data.serverdata.CalendarData;

public class RoomTypeConverter {
    @TypeConverter
    public static CalendarTable fromCalendarData(CalendarData data) {
        CalendarTable calendarDto = new CalendarTable();
        calendarDto.setUserID(data.getUserId());
        calendarDto.setCategory(data.getCategory());
        calendarDto.setComment(data.getComment());
        calendarDto.setTime(data.getTime());
        calendarDto.setCalendarID(data.getCalendarId());
        return calendarDto;
    }

    @TypeConverter
    public static CalendarData fromCalendarTable(CalendarTable table){
        CalendarData data = new CalendarData();
        data.setUserId(table.getUserID());
        data.setTime(table.getTime());
        data.setCategory(table.getCategory());
        data.setComment(table.getComment());
        data.setCalendarId(table.getCalendarID());
        return data;
    }
}
