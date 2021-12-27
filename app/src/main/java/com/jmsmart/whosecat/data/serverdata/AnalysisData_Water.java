package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class AnalysisData_Water {
    @SerializedName("petID")
    public int petID;
    //time은 millisecond단위
    @SerializedName("frontTime")
    public long frontTime;
    @SerializedName("rearTime")
    public long rearTime;


    public AnalysisData_Water(int _petID, long _frontTime, long _rearTime){
        this.petID = _petID;
        this.frontTime = _frontTime;
        this.rearTime = _rearTime;
    }
}
