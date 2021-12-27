package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class ModifyData_Img {
    @SerializedName("petID")
    private int petID;
    @SerializedName("petImg")
    private byte[] petImg;

    public ModifyData_Img(int petID, byte[] img){
        this.petID = petID;
        this.petImg = img;
    }
}
