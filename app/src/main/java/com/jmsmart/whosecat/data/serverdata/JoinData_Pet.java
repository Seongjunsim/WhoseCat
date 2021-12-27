package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class JoinData_Pet {
    @SerializedName("petName")
    private String petName;
    @SerializedName("ownerId")
    private int ownerId;
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

    public JoinData_Pet(String _petName, int _ownerId, float _petKG,
                    float _petLB, String _petSex, String _petBirth, String _device){
        this.petName = _petName;
        this.ownerId = _ownerId;
        this.petKG = _petKG;
        this.petLB = _petLB;
        this.petSex = _petSex;
        this.petBirth = _petBirth;
        this.device = _device;
    }
}
