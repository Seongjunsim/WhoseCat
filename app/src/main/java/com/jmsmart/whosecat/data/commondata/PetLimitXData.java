package com.jmsmart.whosecat.data.commondata;

public class PetLimitXData {

    public int sunDayStart = 7;
    public int uvDayStart = 10;
    public int vitDDayStart = 7;
    public int exerciseDayStart = -1;
    public int walkDayStart = -1;
    public int stepDayStart = -1;
    public int luxpolDayStart = -1;
    public int restDayStart = -1;
    public int kalDayStart = -1;
    public int waterDayStart = -1;

    public int sunDayLimit = 11;
    public int uvDayLimit = 7;
    public int vitDDayLimit = 11;
    public int exerciseDayLimit = 24;
    public int walkDayLimit = 24;
    public int stepDayLimit = 24;
    public int luxpolDayLimit = 7;
    public int restDayLimit = 24;
    public int kalDayLimit = 24;
    public int waterDayLimit = 24;




    public String getPetDayStart(String type){
        try {
            return String.valueOf(this.getClass().getDeclaredField(type+"DayStart").get(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getPetDayDate(String type){
        try {
            return String.valueOf(this.getClass().getDeclaredField(type+"DayLimit").get(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }

}
