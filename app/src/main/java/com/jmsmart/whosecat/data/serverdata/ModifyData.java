package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;
import com.jmsmart.whosecat.data.commondata.User;

public class ModifyData {
    @SerializedName("userId")
        private int userId;
        @SerializedName("userName")
        private String userName;
        @SerializedName("userPhone")
        private String userPhone;
        @SerializedName("userSex")
        private String userSex;
        @SerializedName("userBirth")
        private String userBirth;

    public ModifyData(User user){
            this.userId = user.userId;
            this.userName = user.name;
            this.userPhone = user.phone;
            this.userSex = user.sex;
            this.userBirth = user.birth;
    }
}
