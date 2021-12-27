package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class CalendarData {
    public static final int HOSPITAL = 0;
    public static final int PURCHASE_FEED = 1;
    public static final int VACCINATION = 2;
    public static final int BEAUTY = 3;
    public static final int BATH = 4;
    public static final int ETC = 5;

//    @SerializedName("calendar")
//    Calendar calendar;
    @SerializedName("category")
    int category;
    @SerializedName("comment")
    String comment;
    @SerializedName("userId")
    int userId;
    @SerializedName("time")
    long time;

    int calendarId;

    public void setCategory(int category){this.category=category;}
    public void setComment(String comment){this.comment=comment;}
    public void setUserId(int userId){this.userId=userId;}
    public void setTime(long time){
        this.time = time;
    }
    public void setCalendarId(int id){this.calendarId=id;}


    public int getCategory(){return category;}
    public String getComment(){return comment;}
    public int getUserId(){return userId;}
    public long getTime(){return time;}
    public int getCalendarId(){return calendarId;}
}
