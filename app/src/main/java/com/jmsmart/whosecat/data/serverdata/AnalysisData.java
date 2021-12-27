package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class AnalysisData {
    @SerializedName("petID")
    public int petID;
    //time은 millisecond단위
    @SerializedName("startMilliSec")
    public long frontTime;
    @SerializedName("endMilliSec")
    public long rearTime;


    public AnalysisData(int _petID, long _frontTime, long _rearTime){
        this.petID = _petID;
        this.frontTime = _frontTime;
        this.rearTime = _rearTime;
    }
}
