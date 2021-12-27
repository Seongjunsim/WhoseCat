package com.jmsmart.whosecat.data.commondata;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    public String phone, sex, birth, name, userEmail;
    public int userId;

    //생성할 때 받아온 JSON형식의 data 가져와서 저장
    public User(JSONObject data) {
        try {
            this.userId = ((int) data.get("UserID"));
            this.userEmail = ((String) data.get("UserEmail"));
            this.phone = ((String) data.get("UserPhone"));
            this.sex = ((String) data.get("UserSex"));
            this.birth = ((String) data.get("UserBirth"));
            this.name = ((String) data.get("UserName"));
        } catch (JSONException e) {
            e.printStackTrace();
            this.phone = "";
            this.sex = "";
            this.birth = "";
            this.name = "";
        }
    }
}