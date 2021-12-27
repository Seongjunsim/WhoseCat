package com.jmsmart.whosecat.data.commondata;

public class PetGoalData {

    public int sunGoal = 60000;
    public double uvGoal = 100;
    public double vitDGoal = 400;
    public int exerciseGoal = 84;
    public int walkGoal = 510;
    public int stepGoal = 7908;
    public double luxpolGoal = 100;
    public int restGoal = 846;
    public double kalGoal = 548;
    public int waterGoal = 400;



    public String getPetDate(String type){
        try {
            return String.valueOf(this.getClass().getDeclaredField(type+"Goal").get(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }
}
