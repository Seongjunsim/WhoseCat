package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.adapter.ExistDeviceAdapter;
import com.jmsmart.whosecat.adapter.UnknownDeviceAdapter;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.databinding.ActivityScanDeviceBinding;
import com.jmsmart.whosecat.view.base.CommonModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ScanDeviceViewModel implements CommonModel {
    private ArrayList<PetInfo> listPetInfo;
    private Activity activity;
    private ActivityScanDeviceBinding binding;

    private LinkedList<PetInfo> existedData;
    private LinkedList<BleDevice> unknownData;

    private final static String TAG = ".Central"; // Log 태그
    private BluetoothAdapter ble_adapter_; // 블루트스 어뎁터
    private BluetoothManager ble_manager;
    private BleManager newBleM;

    private UUID SYNC_SERVICE_UUID = UUID.fromString("0000fffa-0000-1000-8000-00805f9b34fb");//Sync Service UUID

    private UUID[] serviceUuids = new UUID[1];
    private List<BleDevice> myDevices;
    private int totalSyncBytes;


    public ScanDeviceViewModel(ArrayList<PetInfo> listPetInfo, Activity activity, ActivityScanDeviceBinding binding){
        this.listPetInfo = listPetInfo;
        this.activity = activity;
        this.binding = binding;

        existedData = new LinkedList<>();
        unknownData = new LinkedList<>();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        totalSyncBytes = 0;
        serviceUuids[0] = SYNC_SERVICE_UUID; //퍼피닥 기기만 찾기 위해 생성
        activity.setTitle(activity.getString(R.string.scan));

        //블루투스 매니저 생성
        BleManager.getInstance().init(activity.getApplication());
        newBleM = BleManager.getInstance();


        ble_manager = (BluetoothManager)activity.getSystemService(Context.BLUETOOTH_SERVICE); // 해당 기기의 블루투스 서비스를 받아와서 저장
        ble_adapter_ = ble_manager.getAdapter(); // 해당 기기의 블루투스 어뎁터 가져옴

        // btn_scan_.setOnClickListener((v)->{startScan(v);}); //스캔 버튼 클릭시 실행
        //btn_stop_.setOnClickListener((v)->{stopScan();}); // 스캔 종료버튼 클릭 시 실행

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setServiceUuids(serviceUuids)
                .setAutoConnect(true)
                .setScanTimeOut(5000)
                .build();
        newBleM.initScanRule(scanRuleConfig);
        ScanSettings settings = new ScanSettings.Builder()//블루투스 스캔 방법 설정
                .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build();

        startScan();


    }

    private void startScan(){
        newBleM.scan(new BleScanCallback() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onScanFinished(List<BleDevice> scanResultList) { //스캔 완료시
                Log.d(TAG, "scan F");
                myDevices = scanResultList;
                binding.pbExistedDevice.setVisibility(View.GONE);
                binding.pbUnknownDevice.setVisibility(View.GONE);
                if(existedData.size()==0){
                    binding.tvNoExistedDevice.setVisibility(View.VISIBLE);
                }
                if(unknownData.size()==0){
                    binding.tvNoUnknownDevice.setVisibility(View.VISIBLE);
                }

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onScanStarted(boolean success) {
                Log.d(TAG, "scan S");
                binding.listExistedDevice.setAdapter(new ExistDeviceAdapter(activity));
                binding.listUnknown.setAdapter(new UnknownDeviceAdapter(activity));

            }

            @Override
            public void onScanning(BleDevice bleDevice) { //찾는 중 발견한 디바이스 정보 출력
                Log.d(TAG, "scanning");
                Log.d(TAG, bleDevice.getName() + bleDevice.getMac());
                boolean unknown = true;
                if(listPetInfo != null){
                    for(PetInfo info : listPetInfo){
                        if(info.device.equals(bleDevice.getMac())){
                            existedData.add(info);
                            unknown = false;
                            ExistDeviceAdapter adapter = (ExistDeviceAdapter) binding.listExistedDevice.getAdapter();
                            adapter.addData(info);
                            binding.listExistedDevice.setAdapter(adapter);
                        }
                    }
                }
                if(unknown){
                    if(bleDevice.getName()=="WhoseCat"){
                        unknownData.add(bleDevice);
                        UnknownDeviceAdapter adapter = (UnknownDeviceAdapter) binding.listUnknown.getAdapter();
                        adapter.addData(bleDevice);
                        binding.listUnknown.setAdapter(adapter);
                    }
                }
            }
        });

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
