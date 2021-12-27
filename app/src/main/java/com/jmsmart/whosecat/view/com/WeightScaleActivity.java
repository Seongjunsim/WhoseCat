package com.jmsmart.whosecat.view.com;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.databinding.ActivityLoginBinding;
import com.jmsmart.whosecat.databinding.ActivityWeightScaleBinding;
import com.jmsmart.whosecat.view.com.viewModel.WeightScaleViewModel;

public class WeightScaleActivity extends AppCompatActivity {

    WeightScaleViewModel model;

    TextView tv_value;
    Button btn;
    ListView lv;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityWeightScaleBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_weight_scale);
        model = new WeightScaleViewModel(binding, this);
        binding.setWeightScale(model);
        model.onCreate();

        // After onAdvertisingSetStarted callback is called, you can modify the
        // advertising data and scan response data:

        // Wait for onAdvertisingDataSet callback...

        // Wait for onScanResponseDataSet callback...

        // When done with the advertising:
        //advertiser.stopAdvertisingSet(callback);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onResume() {
        model.onResume();
        super.onResume();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPause() {
        model.onPause();
        super.onPause();
    }
}