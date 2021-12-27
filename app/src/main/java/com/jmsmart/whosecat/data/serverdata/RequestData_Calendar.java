package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class RequestData_Calendar {
    @SerializedName("userId")
    int userId;

    public RequestData_Calendar(int _userId){
        this.userId = _userId;
    }
}
