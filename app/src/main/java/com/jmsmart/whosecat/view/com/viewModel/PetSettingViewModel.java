package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.jmsmart.whosecat.adapter.PetInfoAdapter;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.data.commondata.User;
import com.jmsmart.whosecat.databinding.ActivityPetSettingBinding;
import com.jmsmart.whosecat.view.base.CommonModel;
import com.jmsmart.whosecat.view.com.PetRegisterActivty;

import java.util.LinkedList;

public class PetSettingViewModel implements CommonModel {
    User user;
    LinkedList<PetInfo> data;
    Activity activity;
    ActivityPetSettingBinding binding;

    public PetSettingViewModel(Activity activity, ActivityPetSettingBinding binding, LinkedList<PetInfo> data, User user){
        this.activity = activity;
        this.binding = binding;
        this.data = data;
        this.user = user;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onResume() {
        if(data!=null){
            binding.listPetInfo.setAdapter(new PetInfoAdapter(activity,data, user));

        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void click(View view){
        Intent intent = new Intent(activity, PetRegisterActivty.class);
        intent.putExtra("type", 0);
        intent.putExtra("userId",user.userId);
        intent.putExtra("userName",user.name);
        activity.startActivityForResult(intent,100);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==100){//추가
            if(resultCode == 10){
                activity.setResult(10);
                activity.finish();
            }
        }else if(requestCode==200){//수정
            if(resultCode == 15){
                activity.setResult(15);
                activity.finish();
            }
        }

    }
}
