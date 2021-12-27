package com.jmsmart.whosecat.view.com;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.databinding.ActivityScanDeviceBinding;
import com.jmsmart.whosecat.view.com.viewModel.ScanDeviceViewModel;

import java.util.ArrayList;

public class ScanDeviceActivity extends AppCompatActivity {

    ActivityScanDeviceBinding binding;
    ScanDeviceViewModel model;
    ArrayList<PetInfo> list;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        list = (ArrayList<PetInfo>) intent.getSerializableExtra("ListPetInfo");
        
        binding = DataBindingUtil.setContentView(this,R.layout.activity_scan_device);
        model = new ScanDeviceViewModel(list, this, binding);
        binding.setScanDevice(model);

        model.onCreate();
    }
}