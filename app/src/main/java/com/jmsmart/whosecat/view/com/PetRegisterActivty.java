package com.jmsmart.whosecat.view.com;

import android.app.Dialog;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.databinding.ActivityPetRegisterActivtyBinding;
import com.jmsmart.whosecat.view.com.viewModel.PetRegisterViewModel;

import java.io.IOException;

public class PetRegisterActivty extends AppCompatActivity {

    private PetRegisterViewModel model;

    private int currType;
    private String  userName;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityPetRegisterActivtyBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_pet_register_activty);
        currType = getIntent().getIntExtra("type", 0);
        userId = getIntent().getIntExtra("userId",-1);
        userName = getIntent().getStringExtra("userName");
        int index = getIntent().getIntExtra("index",-1);
        model = new PetRegisterViewModel(this, binding, userId, userName,currType, index);
        binding.setPetRegisterViewModelModel(model);

        model.onCreate();
    }
    @Override
    protected void onResume() {
        super.onResume();
        model.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        model.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.onDestroy();
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        return model.onCreateDialog(id);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        try {
            model.onActivityResult(requestCode,resultCode,data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}