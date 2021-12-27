package com.jmsmart.whosecat.response;

import com.google.gson.annotations.SerializedName;

public class ServerResponse {
    public static int JOIN_SUCCESS = 100;
    public static int JOIN_FAILURE = 101;
    public static int LOGIN_SUCCESS = 200;
    public static int LOGIN_WRONG_ID = 203;
    public static int LOGIN_WRONG_PWD = 204;
    public static int EMAIL_IS_EXIST = 300;
    public static int EMAIL_NOT_EXIST = 301;
    public static int USER_EDIT_SUCCESS = 400;
    public static int USER_EDIT_FAILURE = 401;
    public static int PET_FOUND = 500;
    public static int PET_NOT_FOUND = 501;
    public static int SENSOR_SUCCESS = 600;
    public static int SENSOR_FAILURE = 601;
    public static int ANALYSIS_SUCCESS = 700;
    public static int ANALYSIS_FAILURE = 701;
    public static int PET_DELETE_SUCCESS = 800;
    public static int PET_DELETE_FAILURE = 801;
    public static int PET_MODIFY_SUCCESS = 900;
    public static int PET_MODIFY_FAILURE = 901;
    public static int WATER_SUCCESS = 1000;
    public static int WATER_FAILURE = 1001;
    public static int CALENDAR_SUCCESS = 1100;
    public static int CALENDAR_FAILURE = 1101;


    @SerializedName("code")
    private int code;
    @SerializedName("message")
    private String message;

    public int getCode(){
        return code;
    }

    public String getMessage(){
        return message;
    }
}
