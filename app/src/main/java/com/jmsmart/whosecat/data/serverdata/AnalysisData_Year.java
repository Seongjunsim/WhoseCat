package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class AnalysisData_Year {
    @SerializedName("petID")
    public int petID;
    @SerializedName("year")
    public int year;
    @SerializedName("offset")
    public int offset;


    public AnalysisData_Year(int _petID, int _year){
        this.petID = _petID;
        this.year = _year;

        Date date = new Date();
        this.offset = date.getTimezoneOffset();
    }
}
