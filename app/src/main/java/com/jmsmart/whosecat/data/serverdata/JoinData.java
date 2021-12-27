package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;

public class JoinData {
    @SerializedName("userName")
    private String userName;
    @SerializedName("userEmail")
    private String userEmail;
    @SerializedName("userPwd")
    private String userPwd;
    @SerializedName("userPhone")
    private String userPhone;
    @SerializedName("userSex")
    private String userSex;
    @SerializedName("userBirth")
    private String userBirth;

    public JoinData(String _userName, String _userEmail, String _userPwd,
                    String _userPhone, String _userSex, String _userBirth){
        this.userName = _userName;
        this.userEmail = _userEmail;
        this.userPwd = _userPwd;
        this.userPhone = _userPhone;
        this.userSex = _userSex;
        this.userBirth = _userBirth;
    }
}
