package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.ObservableField;
import com.google.android.material.snackbar.Snackbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.databinding.ActivityLoginBinding;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.util.SharedPreferencesUtil;
import com.jmsmart.whosecat.view.base.CommonModel;
import com.jmsmart.whosecat.view.com.MainActivity;
import com.jmsmart.whosecat.view.com.SignUpActivity;
import com.jmsmart.whosecat.data.serverdata.LoginData;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginViewModel implements CommonModel {
    private  ActivityLoginBinding binding;
    private Context context;
    private ProgressDialog progressDialog;

    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;

    private boolean isAutoOn = false;
    private String TAG = "LoginViewModel";

    public final ObservableField<String> inputEmailHint = new ObservableField<>("");
    public final ObservableField<String> inputPasswordHint = new ObservableField<>("");
    public final ObservableField<String> btnLoginText = new ObservableField<>("");

    public final ObservableField<String> inputEmail = new ObservableField<>("");
    public final ObservableField<String> inputPassword = new ObservableField<>("");

    public final ObservableField<String> btnSignInText = new ObservableField<>("");

    public LoginViewModel(Context context, ActivityLoginBinding binding){
        this.context = context;
        this.binding = binding;

    }

    @Override
    public void onCreate() {
        inputEmailHint.set(context.getResources().getString(R.string.prompt_email));
        inputPasswordHint.set(context.getResources().getString(R.string.password));
        btnLoginText.set(context.getResources().getString(R.string.sign_in));
        btnSignInText.set(context.getResources().getString(R.string.sign_up));

        BASE_URL = ((Activity) context).getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);

        binding.edPw.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                click();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        autoLogin();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void click(){
        inputEmail.set(inputEmail.get().trim());
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.edPw.getWindowToken(), 0);

        ((Activity)context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if(inputEmail.get().isEmpty() &&  inputPassword.get().isEmpty()) showSnack(R.string.empty_mail_pw);
        else if(!checkEmail(inputEmail.get())) showSnack(R.string.check_mail);
        else {
            if (binding.cbAuto.isChecked())
                SharedPreferencesUtil.setLoginInfo(context, inputEmail.get(), inputPassword.get());

            Log.e(TAG, "click sucess");

            //서버 통신 구현 callback으로 실패할 경우 에러 메시지, 성공할 경우 intent로 들어가기
            attemptLogIn();
        }
    }

    public void onSignInClick() {
        Intent intent = new Intent(context, SignUpActivity.class);
        context.startActivity(intent);
    }

    public void onFindPwClick() {
        //Intent intent = new Intent(context, FindPasswordActivity.class);
        //context.startActivity(intent);
    }

    public static boolean checkEmail(String email) {
        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();
        return isNormal;
    }

    public void showSnack(int stringId) {
        Snackbar.make(((Activity) context).getWindow().getDecorView().getRootView(), stringId, Snackbar.LENGTH_SHORT).show();
    }

    public void showSnack(String message){
        Snackbar.make(((Activity) context).getWindow().getDecorView().getRootView(), message, Snackbar.LENGTH_SHORT).show();
    }

    public void autoLogin(){
        String[] info = SharedPreferencesUtil.getLoginInfo(context);
        if(info!=null){
            inputEmail.set(info[0]);
            inputPassword.set(info[1]);
            click();
        }
    }

    //Precondition: email, pwd 모두 유효한 값을 입력받았음을 전제함.
    //Postcondition: showScanck을 통해 JSON의 message를 출력 성공 시 MainActivity로 인텐트
    private void attemptLogIn(){
        final String email = inputEmail.get();
        final String password = inputPassword.get();
        LoginData data = new LoginData(email, password);
        progressDialog = new ProgressDialog(context);
        progressDialog.show();

        service.userLogin(data).enqueue(new Callback<ServerResponse>(){
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();

                if(result.getCode() == ServerResponse.LOGIN_SUCCESS){
                    try{
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("userInfo", result.getMessage());
                        intent.setAction("action.login");
                        context.startActivity(intent);
                        ((Activity) context).finish();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else if(result.getCode() == ServerResponse.LOGIN_WRONG_ID){
                    showSnack(result.getMessage());
                }
                else if(result.getCode() == ServerResponse.LOGIN_WRONG_PWD){
                    showSnack(result.getMessage());
                }
                else{
                    showSnack(result.getMessage());
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
}
