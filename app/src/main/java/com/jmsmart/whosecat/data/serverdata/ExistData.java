package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class ExistData {
    @SerializedName("userEmail")
    String userEmail;

    public ExistData(String _userEmail){
        this.userEmail = _userEmail;
    }
}
