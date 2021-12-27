package com.jmsmart.whosecat.data.commondata;

import android.content.Context;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.util.SharedPreferencesUtil;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PetInfo implements Serializable {
    public String petName, sex, birth, device;
    public float kg, lb;
    public int ownerID, petID;
    public String sexStr, month, unit, weight;
    public String fileUrl = "null";

    public void copy(PetInfo data){
        this.petName = data.petName;
        this.kg = data.kg;
        this.lb = data.lb;
        this.sex = data.sex;
        this.birth = data.birth;
        this.device = data.device;
        this.ownerID = data.ownerID;
        this.petID = data.petID;
        this.sexStr = data.sexStr;
        this.month = data.month;
        this.unit = data.unit;
        this.weight = data.weight;
    }

    public PetInfo(Context context, int ownerID){

        this.petID = -1;
        this.petName = "NULL";
        this.ownerID = ownerID;
        this.kg = 0f;
        this.lb = 0f;
        this.sex = "NULL";
        this.birth = "NULL";
        this.device = "NULL";
        this.sexStr = "NULL";
        this.month = "NULL";
        this.unit = "NULL";
        this.weight = "NULL ";
    }

    public PetInfo(JSONObject data, Context context) throws ParseException {

        try {
            this.petID = (int)data.get("PetID");
            this.petName = (String)data.get("PetName");
            this.ownerID = (int)data.get("OwnerID");
            this.kg = Float.valueOf(String.valueOf(data.get("PetKG")));
            this.lb = Float.valueOf(String.valueOf(data.get("PetLB")));
            this.sex = (String)data.get("PetSex");
            this.birth = (String)data.get("PetBirth");
            this.device = (String)data.get("Device");
        }catch (Exception e){
            e.printStackTrace();
        }

        sexStr = sex.equals("Male")?context.getResources().getString(R.string.pet_male):context.getResources().getString(R.string.pet_female);
        month = getMonth(birth);
        unit = SharedPreferencesUtil.getDefaultUnit(context);
        weight = (unit.equals("Kg")?kg:lb)+"";
    }


    public String getMonth(String time1) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmdd");
        Date pet = sdf.parse(time1);
        Date cur = new Date();

        int month1 = cur.getYear()*12 + cur.getMonth();
        int month2 = pet.getYear()*12 + pet.getMonth();
        return String.valueOf(month1-month2);
    }

    public void showAll(){
        System.out.println("PetInfo: " + petName + " " + kg + " " +
                lb + " " + sex + " " + birth + " " + device + " " + ownerID
                + " " + petID);
    }
}
