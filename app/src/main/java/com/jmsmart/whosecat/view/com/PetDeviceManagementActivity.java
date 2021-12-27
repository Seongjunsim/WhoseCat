package com.jmsmart.whosecat.view.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.databinding.ActivityMyPageEditBinding;
import com.jmsmart.whosecat.databinding.ActivityPetDeviceManagementBinding;
import com.jmsmart.whosecat.view.com.viewModel.PetDeviceManagementViewModel;

import java.util.ArrayList;

public class PetDeviceManagementActivity extends AppCompatActivity {

    PetDeviceManagementViewModel model;
    ActivityPetDeviceManagementBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        ArrayList<PetInfo> dataList = (ArrayList<PetInfo>) intent.getSerializableExtra("PetInfo");

        binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_device_management);
        model = new PetDeviceManagementViewModel(this, binding, dataList);
        binding.setPetDeviceManagement(model);

        model.onCreate();
    }


}