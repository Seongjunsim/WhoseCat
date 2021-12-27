package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class ModifyData_Pet {
    @SerializedName("ownerId")
    private int ownerId;
    @SerializedName("petId")
    private int petId;
    @SerializedName("petName")
    private String petName;
    @SerializedName("petKG")
    private float petKG;
    @SerializedName("petLB")
    private float petLB;
    @SerializedName("petSex")
    private String petSex;
    @SerializedName("petBirth")
    private String petBirth;
    @SerializedName("device")
    private String device;

    public ModifyData_Pet(int _ownerId, int _petId, String _petName, float _petKG,
                        float _petLB, String _petSex, String _petBirth, String _device){
        this.ownerId = _ownerId;
        this.petId = _petId;
        this.petName = _petName;
        this.petKG = _petKG;
        this.petLB = _petLB;
        this.petSex = _petSex;
        this.petBirth = _petBirth;
        this.device = _device;
    }
}
