package com.jmsmart.whosecat.data.commondata;

import android.util.Log;

import org.json.JSONObject;

import java.io.Serializable;

public class PetData implements Serializable {
    public long time;
    public int sunVal;
    public double uvVal;
    public double vitDVal;
    public int exerciseVal;
    public int walkVal;
    public int stepVal;
    public double luxpolVal;
    public int restVal;
    public double kalVal;
    public int waterVal;

    public PetData(JSONObject data){
        try {
            this.time = Long.parseLong(data.get("Time").toString());
            this.sunVal = (int)data.get("SunVal");
            this.uvVal = Double.parseDouble(data.get("UvVal").toString());
            this.vitDVal = Double.parseDouble(data.get("VitDVal").toString());
            this.exerciseVal = (int)data.get("ExerciseVal");
            this.walkVal = (int)data.get("WalkVal");
            this.stepVal = (int)data.get("StepVal");
            this.luxpolVal = Double.parseDouble(data.get("LuxpolVal").toString());
            this.restVal = (int)data.get("RestVal");
            this.kalVal = Double.valueOf(String.valueOf(data.get("KalVal")));
            this.waterVal = Integer.valueOf(String.valueOf(data.get("WaterVal")));
        }catch (Exception e){
            Log.e("Error","!!!NONONONO");
            e.printStackTrace();
        }
    }



    public String getPetDate(String type){
        try {
            return String.valueOf(this.getClass().getDeclaredField(type+"Val").get(this));
        } catch (IllegalAccessException e) {
            e.printStackTrace();

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return "";
    }
}
