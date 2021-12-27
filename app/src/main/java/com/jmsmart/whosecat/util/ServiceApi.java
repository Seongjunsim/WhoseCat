package com.jmsmart.whosecat.util;

import com.jmsmart.whosecat.data.serverdata.AnalysisData_Year;
import com.jmsmart.whosecat.data.serverdata.CalendarData;
import com.jmsmart.whosecat.data.serverdata.ModifyData_Weight;
import com.jmsmart.whosecat.data.serverdata.ModifyData_Img;
import com.jmsmart.whosecat.data.serverdata.RequestData_Calendar;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.data.serverdata.AnalysisData;
import com.jmsmart.whosecat.data.serverdata.AnalysisData_Water;
import com.jmsmart.whosecat.data.serverdata.DeleteData_Pet;
import com.jmsmart.whosecat.data.serverdata.ModifyData;
import com.jmsmart.whosecat.data.serverdata.ExistData;
import com.jmsmart.whosecat.data.serverdata.FindData_Pet;
import com.jmsmart.whosecat.data.serverdata.JoinData;
import com.jmsmart.whosecat.data.serverdata.JoinData_Pet;
import com.jmsmart.whosecat.data.serverdata.LoginData;
import com.jmsmart.whosecat.data.serverdata.ModifyData_Pet;
import com.jmsmart.whosecat.data.serverdata.SensorData;
import com.jmsmart.whosecat.data.serverdata.WaterData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceApi {

    @POST("/user/login")
    Call<ServerResponse> userLogin(@Body LoginData data);

    @POST("/user/join")
    Call<ServerResponse> userJoin(@Body JoinData data);

    @POST("/user/existEmail")
    Call<ServerResponse> userExist(@Body ExistData data);

    @POST("/user/edit")
    Call<ServerResponse> userModify(@Body ModifyData data);

    @POST("/pet/join")
    Call<ServerResponse> petJoin(@Body JoinData_Pet data);

    @POST("/pet/find")
    Call<ServerResponse> petFind(@Body FindData_Pet data);

    @POST("/pet/modify")
    Call<ServerResponse> petModify(@Body ModifyData_Pet data);

    @POST("/pet/delete")
    Call<ServerResponse> petDelete(@Body DeleteData_Pet data);

    @POST("/pet/img/modify")
    Call<ServerResponse> petImgModify(@Body ModifyData_Img data);

    @POST("/pet/analysis/year")
    Call<ServerResponse> sensorRequestYear(@Body AnalysisData_Year data);

    @POST("/pet/analysis/day")
    Call<ServerResponse> sensorRequestDay(@Body AnalysisData data);

    @POST("/pet/analysis/hour")
    Call<ServerResponse> sensorRequestHour(@Body AnalysisData data);

    @POST("/sensor/send")
    Call<ServerResponse> sensorSend(@Body SensorData data);

    @POST("/water/send")
    Call<ServerResponse> waterSend(@Body WaterData data);

    @POST("/pet/modify/weight")
    Call<ServerResponse> modifyWeight(@Body ModifyData_Weight data);

    @POST("/water/analysis/day")
    Call<ServerResponse> waterRequestDay(@Body AnalysisData_Water data);

    @POST("/water/analysis/hour")
    Call<ServerResponse> waterRequestHour(@Body AnalysisData_Water data);
}
