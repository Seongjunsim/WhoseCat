package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class WaterData {
    @SerializedName("petID")
    public int petID;
    @SerializedName("tick")
    public long tick;
    @SerializedName("waterVal")
    public int waterVal;

    public WaterData(int _petID, int _waterVal){
        this.petID = _petID;
        this.tick = new Date().getTime();
        this.waterVal = _waterVal;
    }
}
