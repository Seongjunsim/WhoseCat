package com.jmsmart.whosecat.view.com.viewModel;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.SeekBar;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.data.serverdata.WaterData;
import com.jmsmart.whosecat.databinding.ActivityWaterBinding;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.view.base.CommonModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaterViewModel implements CommonModel {

    private PetInfo petInfo;
    private Activity activity;
    private ActivityWaterBinding binding;
    private int water = 0;

    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;

    public WaterViewModel(Activity activity, PetInfo petInfo, ActivityWaterBinding binding){
        this.activity = activity;
        this.petInfo = petInfo;
        this.binding = binding;
    }

    @Override
    public void onCreate() {

        BASE_URL = activity.getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);

        binding.sbWater.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                water = i;
                binding.tvWater.setText(water+"/1000");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //시간이랑 water 전송
                //water database를 만들껀지, 아니면 기존 petData 에 넣을지
                WaterData data = new WaterData(petInfo.petID, water);
                service.waterSend(data).enqueue(new Callback<ServerResponse>(){
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                        ServerResponse result = response.body();
                        if(result.getCode() == ServerResponse.WATER_SUCCESS){
                            System.out.println(result.getMessage());
                            Intent intent = new Intent();
                            intent.putExtra("water", water);
                            activity.setResult(activity.RESULT_OK, intent);
                            activity.finish();
                        }
                        else{
                            System.out.println(result.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ServerResponse> call, Throwable t){
                        t.printStackTrace();
                    }
                });
            }
        });
        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                water = 0;
                binding.sbWater.setProgress(0);
            }
        });

        binding.petName.setText(petInfo.petName+"의 오늘 수분량을 넣어주세요!");
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }
}