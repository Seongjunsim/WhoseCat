package com.jmsmart.whosecat.view.com;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.User;
import com.jmsmart.whosecat.databinding.ActivityMyPageEditBinding;
import com.jmsmart.whosecat.view.com.viewModel.MyPageEditViewModel;

public class MyPageEditActivity extends AppCompatActivity {

    MyPageEditViewModel model;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMyPageEditBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_my_page_edit);
        model = new MyPageEditViewModel(this , binding, (User) getIntent().getSerializableExtra("user"));
        binding.setMyPageEditModel(model);

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
    public void onBackPressed() {
        super.onBackPressed();
        model.onBackPressed();
    }
}