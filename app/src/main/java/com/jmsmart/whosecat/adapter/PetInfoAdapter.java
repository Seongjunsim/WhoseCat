package com.jmsmart.whosecat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.jmsmart.whosecat.Database.ImageTable;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.data.commondata.User;
import com.jmsmart.whosecat.data.serverdata.DeleteData_Pet;
import com.jmsmart.whosecat.util.ImageTask;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.view.com.PetRegisterActivty;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PetInfoAdapter extends BaseAdapter {

    Activity activity;
    LinkedList<PetInfo> data;
    LayoutInflater layoutInflater;
    User user;

    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;

    public PetInfoAdapter(Activity activity, LinkedList<PetInfo> data, User user){
        this.user = user;
        this.activity = activity;
        this.data = data;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        BASE_URL = activity.getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        PetInfo info = data.get(i);
        if(view == null){
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.list_pet_info, viewGroup, false);
            viewHolder.img_pet = view.findViewById(R.id.img_pet);
            viewHolder.tv_pet_age = view.findViewById(R.id.tv_pet_age);
            viewHolder.tv_pet_name= view.findViewById(R.id.tv_pet_name);
            viewHolder.tv_pet_sex = view.findViewById(R.id.tv_sex);
            viewHolder.btn_modify = view.findViewById(R.id.btn_modify);
            viewHolder.btn_delete = view.findViewById(R.id.btn_delete);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        ImageTable imageTable = new ImageTable();
        imageTable.setPetID(info.petID);
        ImageTask imageTask = new ImageTask(imageTable, viewHolder.img_pet, activity);
        imageTask.execute(ImageTask.SELECT);
        viewHolder.tv_pet_name.setText(info.petName);
        viewHolder.tv_pet_age.setText(info.month);
        viewHolder.tv_pet_sex.setText(info.sexStr);

        viewHolder.btn_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, PetRegisterActivty.class);
                intent.putExtra("type", 1);
                intent.putExtra("userId",user.userId);
                intent.putExtra("userName",user.name);
                intent.putExtra("index",i);
                activity.startActivityForResult(intent, 200);
            }
        });

        viewHolder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("정말로 "+data.get(i).petName+"(을)를 삭제 하시겠습니까?");
                builder.setPositiveButton(activity.getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //연결 시도
                                DeleteData_Pet delete_data = new DeleteData_Pet(data.get(i).ownerID, data.get(i).petID);
                                service.petDelete(delete_data).enqueue(new Callback<ServerResponse>(){
                                    @Override
                                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                                        ServerResponse result = response.body();

                                        if(result.getCode() == ServerResponse.PET_DELETE_SUCCESS){
                                            Toast.makeText(activity, result.getMessage(), Toast.LENGTH_SHORT).show();
                                            activity.setResult(25);
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
                        });
                builder.setNegativeButton(android.R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                builder.setCancelable(true);
                builder.show();

            }
        });

        return view;
    }

    class ViewHolder{
        public CircleImageView img_pet;
        public TextView tv_pet_name;
        public TextView tv_pet_age;
        public TextView tv_pet_sex;
        public ImageButton btn_modify;
        public ImageButton btn_delete;
    }
}
