package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class ModifyData_Weight {
    @SerializedName("petId")
    private int petId;
    @SerializedName("petKG")
    private float petKG;
    @SerializedName("petLB")
    private float petLB;

    public ModifyData_Weight(int _petId, float _petKG, float _petLB){
        this.petId = _petId;
        this.petKG = _petKG;
        this.petLB = _petLB;
    }
}
