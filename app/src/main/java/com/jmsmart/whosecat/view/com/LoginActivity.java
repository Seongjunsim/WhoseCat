package com.jmsmart.whosecat.view.com;

import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.databinding.ActivityLoginBinding;
import com.jmsmart.whosecat.view.com.viewModel.LoginViewModel;

public class LoginActivity extends AppCompatActivity {
    private LoginViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        ActivityLoginBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_login);
        model = new LoginViewModel(this, binding);
        binding.setLoginModel(model);
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
