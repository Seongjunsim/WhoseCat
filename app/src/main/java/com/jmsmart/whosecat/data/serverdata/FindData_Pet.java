package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class FindData_Pet {
    @SerializedName("ownerId")
    int ownerId;

    public FindData_Pet(int _ownerId){
        this.ownerId = _ownerId;
    }
}
