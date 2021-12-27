package com.jmsmart.whosecat.view.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import android.util.Log;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.databinding.ActivityWaterBinding;
import com.jmsmart.whosecat.view.com.viewModel.WaterViewModel;

public class WaterActivity extends AppCompatActivity {
    ActivityWaterBinding binding;
    WaterViewModel model;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_water);
        model = new WaterViewModel(this,MainActivity.listPetInfo.get(MainActivity.listIndex), binding);
        binding.setWaterViewModel(model);
        Log.i("WaterWater", "until");
        model.onCreate();
    }

    @Override
    protected void onDestroy() {
        model.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        model.onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        model.onResume();
    }
}