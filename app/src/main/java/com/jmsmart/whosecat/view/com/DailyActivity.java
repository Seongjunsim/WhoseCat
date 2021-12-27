package com.jmsmart.whosecat.view.com;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetData;
import com.jmsmart.whosecat.databinding.ActivityDailyBinding;
import com.jmsmart.whosecat.databinding.ActivityLoginBinding;
import com.jmsmart.whosecat.view.com.viewModel.DailyViewModel;

public class DailyActivity extends AppCompatActivity {

    private ActivityDailyBinding binding;
    private DailyViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState){
        PetData data = null;
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_daily);
        data = (PetData)getIntent().getSerializableExtra("today");
        model = new DailyViewModel( binding,this, data);
        binding.setDaily(model);
        model.onCreate();
    }
    @Override
    protected void onResume(){
        super.onResume();
        model.onResume();
    }
    @Override
    protected void onPause(){
        super.onPause();
        model.onPause();
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        model.onDestroy();
    }
}