package com.jmsmart.whosecat.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

@Database(entities = {CalendarTable.class, ImageTable.class}, version=1)
@TypeConverters({RoomTypeConverter.class}) // type converter를 사용하려면 포함
public abstract class AppDatabase extends RoomDatabase {
    public abstract CalendarDao calendarDao();
    public abstract ImageDao imageDao();
    private static AppDatabase mAppDatabase;
    public static final String DBName = "AppDatabase";

    // 싱글튼 패턴을 유지해야 데이터의 일치성을 보장할 수 있다
    public static AppDatabase getInstance(Context context) {
        if(mAppDatabase==null){
            mAppDatabase = Room.databaseBuilder(context, AppDatabase.class, DBName)
                    .fallbackToDestructiveMigration().build();
        }
        return mAppDatabase;
    }

    public static void destroyInstance(){
        mAppDatabase = null;
    }
}