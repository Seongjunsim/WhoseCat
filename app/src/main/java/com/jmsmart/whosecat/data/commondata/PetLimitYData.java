package com.jmsmart.whosecat.data.commondata;

public class PetLimitYData {

    public int sunMonthLimit = 90000;
    public double uvMonthLimit = 120;
    public double vitDMonthLimit = 600;
    public int exerciseMonthLimit = 200;
    public int walkMonthLimit = 710;
    public int stepMonthLimit = 12000;
    public double luxpolMonthLimit = 120;
    public int restMonthLimit = 1500;
    public double kalMonthLimit = 900;
    public int waterMonthLimit = 600;

    public int sunDayLimit = 80000;
    public double uvDayLimit = 110;
    public double vitDDayLimit = 600;
    public int exerciseDayLimit = 200;
    public int walkDayLimit = 710;
    public int stepDayLimit = 10000;
    public double luxpolDayLimit = 110;
    public int restDayLimit = 1440;
    public double kalDayLimit = 800;
    public int waterDayLimit = 400;

    public String getPetMonthDate(String type){
        try {
            return String.valueOf(this.getClass().getDeclaredField(type+"MonthLimit").get(this));
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
