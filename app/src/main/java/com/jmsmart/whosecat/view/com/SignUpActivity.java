package com.jmsmart.whosecat.view.com;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.databinding.ActivitySignUpBinding;
import com.jmsmart.whosecat.view.com.viewModel.SignUpViewModel;

public class SignUpActivity extends AppCompatActivity {

    private SignUpViewModel model = new SignUpViewModel(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySignUpBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
        binding.setSignUpModel(model);

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
}