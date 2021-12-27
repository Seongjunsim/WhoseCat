package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.adapter.HomeListAdapter;
import com.jmsmart.whosecat.data.commondata.PetData;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.databinding.FragmentHomeBinding;
import com.jmsmart.whosecat.view.base.FragViewModel;
import com.jmsmart.whosecat.view.com.DailyActivity;
import com.jmsmart.whosecat.view.com.MainActivity;
import com.jmsmart.whosecat.view.com.PetRegisterActivty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class FragmentHomeViewModel extends FragViewModel implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private Activity activity;
    private FragmentHomeBinding binding;
    public LinkedList<PetInfo> petList;
    public ArrayList<String> petName;
    private PetData petData = null;


    public FragmentHomeViewModel(Activity activity, FragmentHomeBinding binding){
        this.activity = activity;
        this.binding = binding;

        binding.svList.setOnRefreshListener(this);


    }

    @Override
    public View onCreateView(View view) {
        binding.btnReport.setOnClickListener(this);
        binding.imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, PetRegisterActivty.class);
                intent.putExtra("type", 0);
                intent.putExtra("userId",MainActivity.user.userId);
                intent.putExtra("userName",MainActivity.user.name);
                activity.startActivityForResult(intent,300);
            }
        });
        return super.onCreateView(view);
    }

    public void setPetListEmpty(){
        binding.llCaution.setVisibility(View.VISIBLE);
        binding.svList.setVisibility(View.GONE);
        binding.btnReport.setVisibility(View.GONE);
    }

    @Override
    public void onAttach() {
        super.onAttach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void setBattery(String battery, String sync){
        binding.tvBattery.setText(battery);
        Calendar c = Calendar.getInstance();
        String s = String.format("%04d/%02d/%02d %02d:%02d 기준",c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE) );
        binding.tvTime.setText(s);
    }

    public void setList(LinkedList<PetInfo> list, int index){
        this.petList = list;
        //Toast.makeText(context, list.get(0).device,Toast.LENGTH_SHORT).show();

        petName = new ArrayList<>();
        for(PetInfo pet : petList){
            petName.add(pet.petName);
        }
        Log.i("AddS","adddd");


    }

    public void onClick(View view){
        if(petList == null || petList.size()==0){
            Toast.makeText(activity, activity.getString(R.string.caution), Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(activity, DailyActivity.class);
        intent.putExtra("today", petData);
        activity.startActivity(intent);
    }

    public void setStateText(String temp){
        binding.tvState.setText(temp);
    }

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Toast.makeText(activity, petList.get(i).petName+"선택",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("Connect other");
            intent.putExtra("index",i);
            activity.sendBroadcast(intent);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    };

    public void onSavedInstanceState(Bundle savedInstance){
        savedInstance.putSerializable("PetList", petList);
    }

    public void bindingPetData(PetData petData){
        binding.svList.setVisibility(View.VISIBLE);
        binding.llCaution.setVisibility(View.GONE);
        binding.btnReport.setVisibility(View.VISIBLE);
        HomeListAdapter adapter = new HomeListAdapter(activity, petData, petList.get(MainActivity.listIndex).petID);
        this.petData = petData;
        binding.lvHomeList.setAdapter(adapter);


    }

    public void defaultPetData(){
        //binding.stepVal.setText("-");
        //binding.exerVal.setText("-");
        //binding.lightVal.setText("-");
        //binding.restVal.setText("-");
        //binding.sunVal.setText("-");
        //binding.vitaminD.setText("-");
        //binding.walkVal.setText("-");
        //binding.kalVal.setText("-");
        //binding.waterVal.setText("-");
    }

    @Override
    public void onRefresh() {
        binding.svList.setRefreshing(true);
        Intent intent = new Intent("update");
        activity.sendBroadcast(intent);
    }

    public void stopRefreshing(){
        if(binding.svList.isRefreshing()){
            binding.svList.setRefreshing(false);
        }
    }

    public void setImg(){
        //petList.get(MainActivity.listIndex).petID

    }
}
