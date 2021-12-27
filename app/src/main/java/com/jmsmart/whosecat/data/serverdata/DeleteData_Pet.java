package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class DeleteData_Pet {
    @SerializedName("ownerID")
    int ownerID;
    @SerializedName("petID")
    int petID;

    public DeleteData_Pet(int _ownerID, int _petID){
        this.ownerID = _ownerID;
        this.petID = _petID;
    }
}
