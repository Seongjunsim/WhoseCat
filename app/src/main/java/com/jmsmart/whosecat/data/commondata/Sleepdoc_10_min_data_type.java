package com.jmsmart.whosecat.data.commondata;

import android.util.Log;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

//import com.wapplecloud.libwapple.Log;

/**
 * Created by parkboo on 2015. 10. 6..
 */
public class Sleepdoc_10_min_data_type {
    public int s_tick;      // 이 정보의 시작 시간
    public int e_tick;      // 이 정보의 마지막 시간
    public short steps;     // 걸음수
    public int t_lux;     // 이 기간 동안 누적 조도량
    public short avg_lux;   // 평균 조도량 (누적 조도량/걸음수) : 걸음수는 로깅 정보의 갯수임.
    public short avg_k;     // 평균 색온도 in kelvin
    public short vector_x;
    public short vector_y;
    public short vector_z;

    public Sleepdoc_10_min_data_type() {
    }

    public Sleepdoc_10_min_data_type(int s_tick, int e_tick, short steps, int t_lux, short avg_lux, short avg_k, short vector_x, short vector_y, short vector_z) {
        this.s_tick = s_tick; //4
        this.e_tick = e_tick;  //4
        this.steps = steps;  //2
        this.t_lux = t_lux;  //4
        this.avg_lux = avg_lux;  //2
        this.avg_k = avg_k; //2
        this.vector_x = vector_x; //2
        this.vector_y = vector_y; //2
        this.vector_z = vector_z; //2
    }

    public Sleepdoc_10_min_data_type(byte[] data) {
        Log.i("data", "length : " + data.length);
        int offset = 0;
        byte[] s_tick = new byte[4];
        System.arraycopy(data, offset, s_tick, 0, s_tick.length);
        setS_tick(s_tick);
        offset += s_tick.length;
        Date now = new Date( this.s_tick);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Log.i("data", "s_tick : " + this.s_tick);
        Log.i("data", "s_tick_date : " + sdf.format(now));

        byte[] e_tick = new byte[4];
        System.arraycopy(data, offset, e_tick, 0, e_tick.length);
        setE_tick(e_tick);
        offset += e_tick.length;
        Log.i("data", "e_tick : " + this.e_tick);

        byte[] steps = new byte[2];
        System.arraycopy(data, offset, steps, 0, steps.length);
        setSteps(steps);
        offset += steps.length;
        Log.i("data", "steps : " + this.steps);

        byte[] t_lux = new byte[4];
        System.arraycopy(data, offset, t_lux, 0, t_lux.length);
        setT_lux(t_lux);
        offset += t_lux.length;
        Log.i("data", "t_lux : " + this.t_lux);

        byte[] avg_lux = new byte[2];
        System.arraycopy(data, offset, avg_lux, 0, avg_lux.length);
        setAvg_lux(avg_lux);
        offset += avg_lux.length;
        Log.i("data", "avg_lux : " + this.avg_lux);

        byte[] avg_k = new byte[2];
        System.arraycopy(data, offset, avg_k, 0, avg_k.length);
        setAvg_k(avg_k);
        offset += avg_k.length;
        Log.i("data", "avg_k : " + this.avg_k);

        byte[] vector_x = new byte[2];
        System.arraycopy(data, offset, vector_x, 0, vector_x.length);
        setVector_x(vector_x);
        offset += vector_x.length;
        Log.i("data", "vector_x : " + this.vector_x);


        byte[] vector_y = new byte[2];
        System.arraycopy(data, offset, vector_y, 0, vector_y.length);
        setVector_y(vector_y);
        offset += vector_y.length;
        Log.i("data", "vector_y : " + this.vector_y);

        byte[] vector_z = new byte[2];
        System.arraycopy(data, offset, vector_z, 0, vector_z.length);
        setVector_z(vector_z);
        Log.i("data", "vector_z : " + this.vector_z);
    }

    public void setS_tick(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.s_tick = wrapped.getInt();
    }

    public void setE_tick(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.e_tick = wrapped.getInt();
    }

    public void setSteps(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.steps = wrapped.getShort();
    }

    public void setT_lux(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.t_lux = wrapped.getInt();
    }

    public void setAvg_lux(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.avg_lux = wrapped.getShort();
    }

    public void setAvg_k(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.avg_k = wrapped.getShort();
    }

    public void setVector_x(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.vector_x = wrapped.getShort();
    }

    public void setVector_y(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.vector_y = wrapped.getShort();
    }

    public void setVector_z(byte[] bytes) {
        ByteBuffer wrapped = ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.LITTLE_ENDIAN);
        this.vector_z = wrapped.getShort();
    }

    public static int size() {
        return 24;
    }

    @Override
    public String toString() {
        return "Sleepdoc_10_min_data_type{" +
                "s_tick=" + s_tick +
                ", e_tick=" + e_tick +
                ", steps=" + steps +
                ", t_lux=" + t_lux +
                ", avg_lux=" + avg_lux +
                ", avg_k=" + avg_k +
                '}';
    }
}