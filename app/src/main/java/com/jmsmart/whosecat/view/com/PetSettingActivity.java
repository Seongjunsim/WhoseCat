package com.jmsmart.whosecat.view.com;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.data.commondata.User;
import com.jmsmart.whosecat.databinding.ActivityLoginBinding;
import com.jmsmart.whosecat.databinding.ActivityPetSettingBinding;
import com.jmsmart.whosecat.view.com.viewModel.PetSettingViewModel;

import java.util.LinkedList;

public class PetSettingActivity extends AppCompatActivity {

    PetSettingViewModel model;
    ActivityPetSettingBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if(intent!=null){
            LinkedList<PetInfo> data = MainActivity.listPetInfo;
            User user = (User) intent.getSerializableExtra("user");
            binding = DataBindingUtil.setContentView(this,R.layout.activity_pet_setting);
            model = new PetSettingViewModel(this,binding, data, user);
            binding.setPetSettingViewModle(model);
            model.onCreate();
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        model.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        model.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        model.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        model.onActivityResult(requestCode, resultCode, data);
    }
}