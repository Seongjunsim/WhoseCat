package com.jmsmart.whosecat.data.serverdata;

import com.google.gson.annotations.SerializedName;
import com.jmsmart.whosecat.data.commondata.Sleepdoc_10_min_data_type;

public class SensorData {
    @SerializedName("petID")
    public int petID;
    @SerializedName("petLB")
    public double petLB;
    @SerializedName("s_tick")
    public int s_tick;      // 이 정보의 시작 시간
    @SerializedName("e_tick")
    public int e_tick;      // 이 정보의 마지막 시간
    @SerializedName("steps")
    public short steps;     // 걸음수
    @SerializedName("t_lux")
    public int t_lux;     // 이 기간 동안 누적 조도량
    @SerializedName("avg_lux")
    public short avg_lux;   // 평균 조도량 (누적 조도량/걸음수) : 걸음수는 로깅 정보의 갯수임.
    @SerializedName("avg_k")
    public short avg_k;     // 평균 색온도 in kelvin
    @SerializedName("vector_x")
    public short vector_x;
    @SerializedName("vector_y")
    public short vector_y;
    @SerializedName("vector_z")
    public short vector_z;

    public SensorData(Sleepdoc_10_min_data_type data, int petID, double petLB){
        this.petID = petID;
        this.petLB = petLB;
        this.s_tick = data.s_tick;
        this.e_tick = data.e_tick;
        this.steps = data.steps;
        this.t_lux = data.t_lux;
        this.avg_lux = data.avg_lux;
        this.avg_k = data.avg_k;
        this.vector_x = data.vector_x;
        this.vector_y = data.vector_y;
        this.vector_z = data.vector_z;
    }
}
