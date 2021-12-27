package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.ObservableField;

import android.widget.Toast;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.data.commondata.User;
import com.jmsmart.whosecat.data.serverdata.ModifyData;
import com.jmsmart.whosecat.databinding.ActivityMyPageEditBinding;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.view.base.CommonModel;
import com.jmsmart.whosecat.view.com.MyPageEditActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyPageEditViewModel implements CommonModel {

    private Context context;
    public User user;
    private ActivityMyPageEditBinding binding;
    private String TAG = "mypageViewModel";

    private ServiceApi service;
    private Retrofit retrofit;
    private String BASE_URL;

    public ObservableField<Boolean> isModify = new ObservableField<>(true);
    public ObservableField<String> applyOrModify = new ObservableField<>("");


    public MyPageEditViewModel(MyPageEditActivity myPageEditActivity, ActivityMyPageEditBinding binding, User user) {
        this.user = user;
        this.context = myPageEditActivity;
        this.binding = binding;
        applyOrModify.set(context.getResources().getString(R.string.mypage_apply));
    }

    @Override
    public void onCreate() {
        if(user.sex.equals("male")){
            binding.mypageRadioMale.setChecked(true);
            binding.mypageRadioFemale.setChecked(false);
        }else{
            binding.mypageRadioMale.setChecked(false);
            binding.mypageRadioFemale.setChecked(true);
        }

        BASE_URL = ((Activity) context).getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);
    }

    public void onClick(){
        if(!isValidDateStr(user.birth)) {
            Toast.makeText(context,context.getString(R.string.info_input_error), Toast.LENGTH_SHORT).show();
            return;
        }
        if(binding.mypageRadioMale.isChecked())user.sex = "male";
        else user.sex = "female";

        ModifyData data = new ModifyData(user);

        service.userModify(data).enqueue(new Callback<ServerResponse>(){
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();
                if(result.getCode() == ServerResponse.USER_EDIT_SUCCESS){
                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent();
                    intent.putExtra("userInfo", user);
                    ((Activity) context).setResult(Activity.RESULT_OK,intent);
                    ((Activity) context).finish();
                }
                else{
                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t){
                t.printStackTrace();
            }
        });

        /*
        APIManager.getInstance(context).updateUser(user.userId, user.name, user.phone, user.sex, user.birth, new ResponseCallback() {
            @Override
            public void onError(int errorCode, String errorMsg) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,context.getString(R.string.profile_change_error),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onDataReceived(JSONObject jsonResponse) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context,context.getString(R.string.profile_change_text),Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });

                //applyOrModify.set(context.getResources().getString(R.string.myPage_Modify));
                //isModify.set(false);
            }

            @Override
            public void onReceiveResponse() {

            }
        });*/
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void onBackPressed() {

    }
    public static boolean isValidDateStr(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setLenient(false);
            sdf.parse(date);
        }
        catch (ParseException e) {
            return false;
        }
        catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }
}
