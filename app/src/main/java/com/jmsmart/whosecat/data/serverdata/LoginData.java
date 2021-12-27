package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class LoginData {
    @SerializedName("userEmail")
    String userEmail;
    @SerializedName("userPwd")
    String userPwd;

    public LoginData(String _userEmail, String _userPwd){
        this.userEmail = _userEmail;
        this.userPwd = _userPwd;
    }
}
