package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.ObservableField;
import androidx.annotation.IdRes;

import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.data.serverdata.ExistData;
import com.jmsmart.whosecat.data.serverdata.JoinData;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.view.base.CommonModel;
import com.jmsmart.whosecat.view.com.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpViewModel implements CommonModel {
    private Context context = null;

    private int nowCur = 0;

    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;

    private String TAG = "SignUpViewModel";

    public final ObservableField<Integer> idVisible = new ObservableField<>(View.VISIBLE);
    public final ObservableField<Integer> detailVisible = new ObservableField<>(View.GONE);
    public final ObservableField<Integer> puppyVisible = new ObservableField<>(View.GONE);

    public final ObservableField<Boolean> nextBtnEnabled = new ObservableField<>(true);
    public final ObservableField<Boolean> beforeBtnEnabled = new ObservableField<>(false);

    public final ObservableField<String> errorTxt = new ObservableField<>("");
    public final ObservableField<String> userIdTxt = new ObservableField<>("");
    public final ObservableField<String> userPassTxt = new ObservableField<>("");
    public final ObservableField<String> userPassCheckTxt = new ObservableField<>("");
    public final ObservableField<String> userPhoneTxt = new ObservableField<>("");
    public final ObservableField<String> userNameTxt = new ObservableField<>("");
    public final ObservableField<String> userBirthdayTxt = new ObservableField<>("");
    public String userSex;

    private ToggleButton passwordToggle;
    private EditText editPassword;
    private EditText checkPassword;
    private String userType = "";
    //ArrayList<APIManager.UserGroup> userGroups;

    RadioGroup radioGroupSex;

    private ProgressDialog progressDialog;

    public SignUpViewModel(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate() {
        radioGroupSex = ((Activity) context).findViewById(R.id.radio_user);
        radioGroupSex.setOnCheckedChangeListener(radioGroupButtonChangeListener);
        passwordToggle = ((Activity)context).findViewById(R.id.password_toggle);
        editPassword = ((Activity)context).findViewById(R.id.edit_Password);
        checkPassword = ((Activity)context).findViewById(R.id.edit_Password_check);
        userSex = "male";

        BASE_URL = ((Activity) context).getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);
        //userGroups = APIManager.getInstance(context).getUserGroupList();
        // userTypeSpinner.setAdapter(new ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item, getUserGroupList(userGroups)));

        //Intent intent = new Intent(context, TextActivity.class);
        //intent.putExtra("title", context.getString(R.string.privacy_policy));
        //context.startActivity(intent);
    }

    RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
            if(i == R.id.radio_male){
                userSex = "male";
            } else if(i == R.id.radio_female){
                userSex = "female";
            }
        }
    };


    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void onBeforeClick() {
        switch (nowCur) {
            case 1: {
                detailVisible.set(View.GONE);
                idVisible.set(View.VISIBLE);
                beforeBtnEnabled.set(false);

                nowCur = 0;
                break;
            }
        }
    }

    public void onNextClick() {
        userIdTxt.set(userIdTxt.get().trim());
        switch (nowCur) {
            case 0: {
                if (userIdTxt.get().length() < 1 || !checkEmail(userIdTxt.get())) {
                    Toast.makeText(context, context.getString(R.string.id_input_error), Toast.LENGTH_SHORT).show();
                    break;
                } else if (userPassTxt.get().length() < 1 || !userPassTxt.get().equals(userPassCheckTxt.get())) {
                    Toast.makeText(context, context.getString(R.string.pw_input_error), Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "UserPass Length : " + userPassTxt.get().length());
                    Log.d(TAG, "UserPass TextActivity : " + userPassTxt.get());
                    Log.d(TAG, "UserPassCheck : " + userPassCheckTxt.get());
                    break;
                }
                isExistingEmail(userIdTxt.get());
                break;
            }
            case 1: {
                if (userNameTxt.get().length() < 1 || userBirthdayTxt.get().length() < 8 || userPhoneTxt.get().length() < 10 || !isValidDateStr(userBirthdayTxt.get())) {
                    Toast.makeText(context, context.getString(R.string.info_input_error), Toast.LENGTH_SHORT).show();
                    break;
                }
                progressDialog = new ProgressDialog(context);
                progressDialog.onStart();
                attemptJoin();
                progressDialog.cancel();
                break;
            }
        }
    }

    public static boolean checkEmail(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }

    public static boolean isValidDateStr(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            sdf.setLenient(false);
            sdf.parse(date);
        } catch (ParseException e) {
            return false;
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    //Precondition: _email은 유효한 email형식 임을 전제함.
    //Postcondition: 이메일 중복이면 Toast 문자열 출력, 이메일 중복 아니면 nowCur -> 1
    private void isExistingEmail(String _email){
        String email = _email;
        ExistData data = new ExistData(email);

        progressDialog = new ProgressDialog(context);
        progressDialog.show();

        service.userExist(data).enqueue(new Callback<ServerResponse>(){
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();
                if(result.getCode() == ServerResponse.EMAIL_IS_EXIST){
                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                else if(result.getCode() == ServerResponse.EMAIL_NOT_EXIST){
                    idVisible.set(View.GONE);
                    detailVisible.set(View.VISIBLE);
                    beforeBtnEnabled.set(true);
                    nowCur = 1;
                }
                else{
                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
                progressDialog.cancel();
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t){
                t.printStackTrace();
                progressDialog.cancel();
            }
        });
    }

    //Precondition: JoinData의 매개변수가 모두 유효한 형식임을 가정함.
    //Postcondition: 로그인 화면으로 넘어가야 할 것 같음!
    private void attemptJoin(){
        String email = userIdTxt.get();
        String pwd = userPassTxt.get();
        String name = userNameTxt.get();
        String phone = userPhoneTxt.get();
        String birth = userBirthdayTxt.get();
        String sex = userSex;

        JoinData data = new JoinData(name, email,pwd,phone,sex,birth);
        service.userJoin(data).enqueue(new Callback<ServerResponse>(){
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();
                if(result.getCode() == ServerResponse.JOIN_SUCCESS){
                    Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
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
    }

    public void clickToggle(View view){
        boolean on = ((ToggleButton)view).isChecked();
        if(on){
            editPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            checkPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        }else{

            editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            checkPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
}
