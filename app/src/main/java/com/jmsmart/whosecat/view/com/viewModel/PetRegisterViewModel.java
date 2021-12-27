package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.clj.fastble.data.BleDevice;
import com.jmsmart.whosecat.Database.ImageTable;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.data.serverdata.JoinData_Pet;
import com.jmsmart.whosecat.data.serverdata.ModifyData_Pet;
import com.jmsmart.whosecat.databinding.ActivityPetRegisterActivtyBinding;
import com.jmsmart.whosecat.util.ImageTask;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.util.SharedPreferencesUtil;
import com.jmsmart.whosecat.view.base.CommonModel;
import com.jmsmart.whosecat.view.com.MainActivity;
import com.jmsmart.whosecat.view.com.ScanDeviceActivity;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PetRegisterViewModel implements CommonModel {
    private final String TAG = "PetViewModel";
    private Activity activity;
    private ActivityPetRegisterActivtyBinding binding;
    private int userId;
    private int type;
    private String userName;

    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;

    private String btnText;

    private Date selectDate;
    static final int DATE_DIALOG_ID = 999;

    public static final int TYPE_ADD = 0;
    public static final int TYPE_EDIT = 1;
    private static final String ACTION_REFRESH_PET_LIST = "refreshPetListAction";

    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE = 2;

    //로컬 db에 사용할 변수
    ImageTable imageTable;

    private String petName , petType, weightKg ,weightLb ,petBirth;
    private String sex ;

    private int petSrn=-1;
    private String fileCode=null;

    private Bitmap bitmap;
    private Uri mImageCaptureUri;

    public PetInfo petInfo;
    public int index;

    ArrayList<String> petTypeNames;

    public PetRegisterViewModel(Activity activity, ActivityPetRegisterActivtyBinding binding, int userId, String userName, int type, int index){
        this.activity = activity;
        this.binding = binding;
        this.userId = userId;
        this.type = type;
        this.userName=userName;
        this.index = index;

        petInfo = new PetInfo(activity, userId);
        Resources r =  activity.getResources();

        btnText = type == TYPE_ADD ?r.getString(R.string.add):r.getString(R.string.edit);
        ((AppCompatActivity)activity).setTitle(
                type == TYPE_ADD ?
                        r.getString(R.string.add_pet)
                        :r.getString(R.string.edit_pet));
    }

    public Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                Date date = new Date();
                return new DatePickerDialog(activity, datePickerListener, date.getYear(), date.getMonth(), date.getDay());
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {



        // when dialog box is closed, below method will be called.
        public void onDateSet(DatePicker view, int year, int month, int day) {
            selectDate = new Date(year, month, day);
            String s = DateFormat.getDateInstance(DateFormat.SHORT).format(selectDate);
            binding.editPetDate.setText(s);
        }
    };



    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v == binding.btnNext){
                //웹서버에 정보 추가
                petInfo.petName = binding.editPetName.getText().toString();
                if(binding.radioPet.getCheckedRadioButtonId()==R.id.radio_pet_male)petInfo.sex="Male";
                else if(binding.radioPet.getCheckedRadioButtonId()==R.id.radio_pet_female)petInfo.sex="FeMale";
                else petInfo.sex="None";
                petInfo.birth = binding.editPetDate.getText().toString();
                petInfo.device = binding.editDevice.getText().toString().equals("")?"NULL":binding.editDevice.getText().toString();
                if(petInfo.petName.length() < 1 || petInfo.birth.length() < 1 || binding.editPetWeight.getText().toString().length() < 1 || !isValidDateStr(petInfo.birth)){
                    Toast.makeText(activity,activity.getString(R.string.pet_info_input_error),Toast.LENGTH_SHORT).show();
                }
                if(binding.spinnerWeight.getSelectedItemPosition()==0){
                    petInfo.kg = Float.valueOf(binding.editPetWeight.getText().toString());
                    SharedPreferencesUtil.setDefaultUnit(activity, "Kg");
                    petInfo.lb = Float.valueOf(String.valueOf(petInfo.kg*2.204));
                }
                else {
                    petInfo.lb = Float.valueOf(binding.editPetWeight.getText().toString());
                    SharedPreferencesUtil.setDefaultUnit(activity, "Lb");
                    petInfo.kg = Float.valueOf(String.valueOf(petInfo.lb*0.453));
                }

                Log.d("addPet", "PetViewModel: petBirth =" + petInfo.birth);
                Log.d("addPet", "PetViewModel: petWeight =" + binding.editPetWeight.getText().toString());
                Log.d("addPet", "PetViewModel: weightKg =" + petInfo.kg);
                Log.d("addPet", "PetViewModel: weightLb =" + petInfo.lb);

                //로그인 성공 시 동작을 attemptJoinPet함수 내부로 넣어 두었음!
                if(type==TYPE_ADD)
                    attemptJoinPet();
                else
                    attemptModifyPet();
            }
        }
    };

    private void attemptModifyPet(){
        ModifyData_Pet data = new ModifyData_Pet(petInfo.ownerID,
                petInfo.petID,
                petInfo.petName,
                petInfo.kg,
                petInfo.lb,
                petInfo.sex,
                petInfo.birth,
                petInfo.device);

        service.petModify(data).enqueue(new Callback<ServerResponse>(){
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();
                if(result.getCode() == ServerResponse.PET_MODIFY_SUCCESS){
                    Toast.makeText(activity, result.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("~~~PETID", String.valueOf(Integer.parseInt(result.getMessage())));
                    uploadImageFile(petInfo.petID);

                }
                else{
                    Log.i("Error","server error");
                    Toast.makeText(activity, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t){
                t.printStackTrace();
            }
        });
    }

    private void attemptJoinPet(){
        JoinData_Pet data = new JoinData_Pet(petInfo.petName,petInfo.ownerID,petInfo.kg, petInfo.lb, petInfo.sex,
                petInfo.birth, petInfo.device);
        service.petJoin(data).enqueue(new Callback<ServerResponse>(){
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();
                if(result.getCode() == ServerResponse.JOIN_SUCCESS){
                    Toast.makeText(activity, result.getMessage(), Toast.LENGTH_SHORT).show();
                    uploadImageFile(Integer.parseInt(result.getMessage()));
                    if(type==0)
                        activity.setResult(10);
                    else
                        activity.setResult(15);
                    activity.finish();
                }
                else{
                    Toast.makeText(activity, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t){
                t.printStackTrace();
            }
        });
    }

    private void initPetInfo(){
        if(type == TYPE_EDIT){
            PetInfo tempPetInfo = MainActivity.listPetInfo.get(index);
            binding.editPetName.setText(tempPetInfo.petName);
            binding.editPetDate.setText(tempPetInfo.birth);
            if(tempPetInfo.device!="NULL")binding.editDevice.setText(tempPetInfo.device);
            else binding.editDevice.setText("");
            ImageTable imageTable = new ImageTable();
            imageTable.setPetID(tempPetInfo.petID);
            ImageTask imageTask = new ImageTask(imageTable, binding.imgPet, activity);
            imageTask.execute(ImageTask.SELECT);
            petInfo.petID = tempPetInfo.petID;
            if(tempPetInfo.unit.equals("Kg"))
                binding.editPetWeight.setText(String.valueOf(tempPetInfo.kg));
            else
                binding.editPetWeight.setText(String.valueOf(tempPetInfo.lb));
            if(tempPetInfo.sex.equals("Male"))
                binding.radioPetMale.setChecked(true);
            else if(tempPetInfo.sex.equals("FeMale"))
                binding.radioPetFemale.setChecked(true);
            else
                binding.radioPetNone.setChecked(true);
        }
    }


    @Override
    public void onCreate() {
        if(type == TYPE_EDIT){

        }

        binding.editPetDate.setOnClickListener(listener);

        binding.btnNext.setText(btnText);
        binding.btnNext.setOnClickListener(listener);
        binding.imgPet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                activity.startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });
        BASE_URL = ((Activity) activity).getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);

        //강아지 정보 세팅하기
        initPetInfo();
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

    public void uploadImageFile(int petId){
        bitmap = ((BitmapDrawable)binding.imgPet.getDrawable()).getBitmap();
        imageTable = new ImageTable();
        imageTable.setPetID(petId);
        imageTable.setPetImageByBitmap(bitmap);
        ImageTask task = new ImageTask(imageTable, binding.imgPet, activity);
        task.execute(ImageTask.INSERT);

        // 비트맵으로 보내줬으면~~
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) throws IOException {

        if(resultCode!=activity.RESULT_OK)
            return;
        switch(requestCode) {
            case 100:
                BleDevice device = (BleDevice) data.getParcelableExtra("deviceInfo");
                binding.editDevice.setText(device.getMac());
                Toast.makeText(activity, "가져오기 성공",Toast.LENGTH_SHORT).show();
                break;
            case PICK_FROM_ALBUM:
                Log.i("!!WHY","NOT");
                mImageCaptureUri = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), data.getData());

                int height = bitmap.getHeight();
                int width = bitmap.getWidth();

                Bitmap resize = Bitmap.createScaledBitmap(bitmap,width/5,height/5, true);




                binding.imgPet.setImageBitmap(resize);

            case PICK_FROM_CAMERA: {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정합니다.
                // 이후에 이미지 크롭 어플리케이션을 호출하게 됩니다.
                /*
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");
                // CROP할 이미지를 200*200 크기로 저장
                intent.putExtra("outputX", 480); // CROP한 이미지의 x축 크기
                intent.putExtra("outputY", 400); // CROP한 이미지의 y축 크기
                intent.putExtra("aspectX", 6); // CROP 박스의 X축 비율
                intent.putExtra("aspectY", 5); // CROP 박스의 Y축 비율
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                activity.startActivityForResult(intent, CROP_FROM_IMAGE); // CROP_FROM_CAMERA case문 이동*/

                break;
            }
            case CROP_FROM_IMAGE: {
                if(resultCode != Activity.RESULT_OK) {
                    Log.i("!!EEE","");
                    return;
                }
                final Bundle extras = data.getExtras();
                Log.i("!!AAA","");
                if(extras != null) {
                    Bitmap photo = extras.getParcelable("data"); // CROP된 BITMAP
                    binding.imgPet.setImageBitmap(photo); // 레이아웃의 이미지칸에 CROP된 BITMAP을 보여줌
                    bitmap = photo;
                    break;
                }

            }

        }

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

    public void scan(){
        Intent intent = new Intent(activity, ScanDeviceActivity.class);
        intent.putExtra("ListPetInfo", MainActivity.listPetInfo);
        activity.startActivityForResult(intent, 100);
    }
}
