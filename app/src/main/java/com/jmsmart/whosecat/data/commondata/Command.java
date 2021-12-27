package com.jmsmart.whosecat.data.commondata;

import java.io.Serializable;

/**
 * Created by Administrator on 2019-01-21.
 */

public class Command implements Serializable {
    private String mac;
    private int userId;
    private int petId;
    private String petNm;

    public Command(String mac, int userId, int petId, String petNm) {
        this.mac = mac;
        this.userId = userId;
        this.petId = petId;
        this.petNm = petNm;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public String getPetNm() {
        return petNm;
    }

    public void setPetNm(String petNm) {
        this.petNm = petNm;
    }
}