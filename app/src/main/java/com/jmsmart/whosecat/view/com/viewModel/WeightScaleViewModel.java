package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertisingSet;
import android.bluetooth.le.AdvertisingSetCallback;
import android.bluetooth.le.AdvertisingSetParameters;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Intent;
import android.os.Build;
import android.os.ParcelUuid;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.serverdata.ModifyData_Weight;
import com.jmsmart.whosecat.databinding.ActivityWeightScaleBinding;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.view.base.CommonModel;
import com.jmsmart.whosecat.view.com.MainActivity;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WeightScaleViewModel implements CommonModel, View.OnClickListener {

    ActivityWeightScaleBinding binding;
    Activity activity;
    float weight = 0;
    final String LOG_TAG = "LOG_TAG";
    AdvertisingSet currentAdvertisingSet;

    final int MESURE = 2;
    final int ATTACH = 130;
    final int MESURE_COMPLETE = 34;

    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;

    private UUID SYNC_SERVICE_UUID = UUID.fromString("0000181D-0000-1000-8000-00805f9b34fb");//Sync Service UUID

    private UUID[] serviceUuids = new UUID[1];
    private BluetoothLeScanner mScanner;

    List<ScanFilter> filters;
    ScanSettings settings;


    public WeightScaleViewModel(ActivityWeightScaleBinding binding, Activity activity) {
        this.binding = binding;
        this.activity = activity;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        BluetoothLeAdvertiser advertiser =
                BluetoothAdapter.getDefaultAdapter().getBluetoothLeAdvertiser();
        binding.submit.setOnClickListener(this);
        binding.restart.setOnClickListener(this);
        AdvertisingSetParameters parameters = (new AdvertisingSetParameters.Builder())
                .setLegacyMode(true) // True by default, but set here as a reminder.
                .setScannable(true)
                .setInterval(AdvertisingSetParameters.INTERVAL_HIGH)
                .setTxPowerLevel(AdvertisingSetParameters.TX_POWER_MEDIUM)
                .build();

        BASE_URL = activity.getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);

        final AdvertiseData data = (new AdvertiseData.Builder()).setIncludeDeviceName(true).build();


        AdvertisingSetCallback callback = new AdvertisingSetCallback() {
            @Override
            public void onAdvertisingSetStarted(AdvertisingSet advertisingSet, int txPower, int status) {
                Log.i(LOG_TAG, "onAdvertisingSetStarted(): txPower:" + txPower + " , status: "
                        + status);
                currentAdvertisingSet = advertisingSet;
            }

            @Override
            public void onAdvertisingDataSet(AdvertisingSet advertisingSet, int status) {
                Log.i(LOG_TAG, "onAdvertisingDataSet() :status:" + status);
                currentAdvertisingSet.setScanResponseData(new
                        AdvertiseData.Builder().addServiceUuid(new ParcelUuid(UUID.randomUUID())).build());
            }

            @Override
            public void onScanResponseDataSet(AdvertisingSet advertisingSet, int status) {
                Log.i(LOG_TAG, "onScanResponseDataSet(): status:" + status);
                currentAdvertisingSet.setAdvertisingData(new AdvertiseData.Builder().
                        setIncludeDeviceName(true).setIncludeTxPowerLevel(true).build());
                byte[] d = data.getServiceData().get(SYNC_SERVICE_UUID);
                Log.i(LOG_TAG,String.valueOf(d));

            }

            @Override
            public void onAdvertisingSetStopped(AdvertisingSet advertisingSet) {
                Log.i(LOG_TAG, "onAdvertisingSetStopped():");
            }
        };

        advertiser.startAdvertisingSet(parameters, data, null, null, null, callback);

        filters = new LinkedList<>();
        mScanner = BluetoothAdapter.getDefaultAdapter().getBluetoothLeScanner();
        ScanFilter filter = new ScanFilter.Builder()
                .setServiceUuid(new ParcelUuid(SYNC_SERVICE_UUID)).build();
        filters.add(filter);

        settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();

        binding.tvExplainTitle.setText(MainActivity.listPetInfo.get(MainActivity.listIndex).petName+activity.getString(R.string.weight_title));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onResume() {
        mScanner.startScan(filters, settings, mScanCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPause() {
        mScanner.stopScan(mScanCallback);
    }

    @Override
    public void onDestroy() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            if(result==null || result.getDevice() == null || TextUtils.isEmpty(result.getDevice().getName())) return;
            StringBuilder builder = new StringBuilder(result.getDevice().getName());
            byte[] bytes = result.getScanRecord().getServiceData(result.getScanRecord().getServiceUuids().get(0));
            for(byte b : bytes){
                builder.append(String.format("%02x", b).toUpperCase());
            }
            int type = Byte.toUnsignedInt(bytes[0]);
            //Log.i(LOG_TAG, builder.toString()+" "+type);
            DecimalFormat decimalFormat = new DecimalFormat("#00.00");
            switch (type){
                case MESURE:
                    float mesureWeight = parseWeight(bytes[1], bytes[2]);
                    if(weight==0 || weight==mesureWeight){
                        weight = parseWeight(bytes[1], bytes[2]);
                        break;
                    }
                    if(weight!=mesureWeight){
                        binding.imgExplain.setImageResource(R.drawable.timer);
                        binding.tvExplain.setText(activity.getString(R.string.weight_second));
                        binding.tvWeightValue.setText(decimalFormat.format(mesureWeight));
                        Log.i(LOG_TAG, String.format("%02.2f", mesureWeight));
                        weight = mesureWeight;
                    }
                    break;
                case ATTACH:
                    weight = 0;
                    binding.imgExplain.setImageResource(R.drawable.weight);
                    binding.tvExplain.setText(activity.getString(R.string.weight_first));
                    binding.tvWeightValue.setText(decimalFormat.format( weight));
                    Log.i(LOG_TAG, String.format("%02.2f", weight));
                    break;
                case MESURE_COMPLETE:
                    //서버 전송 다이얼로그 출력하도록?
                    binding.imgExplain.setImageResource(R.drawable.check);
                    binding.tvExplain.setText(activity.getString(R.string.weight_third));
                    binding.llButton.setVisibility(View.VISIBLE);
                    weight = parseWeight(bytes[1], bytes[2]);
                    binding.tvWeightValue.setText(decimalFormat.format(weight));
                    Log.i(LOG_TAG, String.format("%02.2f send", weight));
                    mScanner.stopScan(mScanCallback);
                    break;
            }

        }


        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e(LOG_TAG,"fail");
            super.onScanFailed(errorCode);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    public float parseWeight(byte first, byte second){
        int value1 = Byte.toUnsignedInt(first);
        int value2 = Byte.toUnsignedInt(second) * 256;
        return (float)(value1+value2)/200;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.submit:
                ModifyData_Weight data = new ModifyData_Weight(MainActivity.listPetInfo.get(MainActivity.listIndex).petID, weight, (float) (weight*2.204));

                service.modifyWeight(data).enqueue(new Callback<ServerResponse>(){
                    @Override
                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                        ServerResponse result = response.body();
                        if(result.getCode() == ServerResponse.PET_MODIFY_SUCCESS){
                            System.out.println(result.getMessage());
                        }
                        else if(result.getCode() == ServerResponse.PET_MODIFY_FAILURE){
                            System.out.println(result.getMessage());
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
                activity.finish();
                break;
            case R.id.restart:
                binding.imgExplain.setImageResource(R.drawable.weight);
                binding.tvExplain.setText(activity.getString(R.string.weight_first));
                weight=0;
                binding.llButton.setVisibility(View.GONE);
                mScanner.startScan(filters,settings,mScanCallback);
                break;
        }
    }
}
