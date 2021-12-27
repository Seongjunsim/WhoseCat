package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.adapter.PickerAdapter;
import com.jmsmart.whosecat.databinding.FragmentAnalysisBinding;
import com.jmsmart.whosecat.view.base.FragViewModel;
import com.jmsmart.whosecat.view.com.MainActivity;
import com.jmsmart.whosecat.view.fragment.FragmentAnalysisDay;
import com.jmsmart.whosecat.view.fragment.FragmentAnalysisMonth;
import com.jmsmart.whosecat.view.fragment.FragmentAnalysisYear;

import java.util.ArrayList;
import java.util.List;

import travel.ithaka.android.horizontalpickerlib.PickerLayoutManager;


public class FragmentAnalysisViewModel extends FragViewModel implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {
    boolean animate = false;
    Activity activity;
    FragmentAnalysisBinding binding;
    Fragment parent;
    FragmentAnalysisDay fragmentAnalysisDay;
    FragmentAnalysisMonth fragmentAnalysisMonth;
    FragmentAnalysisYear fragmentAnalysisYear;
    PickerLayoutManager pickerLayoutManager;
    FragmentManager fragmentManager;
    private final String[] VAL_LIST = {"sun", "uv", "vitD", "exercise", "walk", "step", "luxpol", "rest", "kal", "water"};
    public static int dataIndex=0;
    public static int timeIndex=0;
    private final List<String> dataList = new ArrayList<String>();
    public FragmentAnalysisViewModel(final Activity activity, Fragment parent, FragmentAnalysisBinding binding) {
        this.activity = activity;
        this.binding = binding;
        this.parent = parent;
        if(MainActivity.listPetInfo == null || MainActivity.listPetInfo.size()==0){
            binding.llCaution.setVisibility(View.VISIBLE);
            binding.llAnalysis.setVisibility(View.GONE);
            return;
        }

        fragmentAnalysisDay = new FragmentAnalysisDay();
        fragmentAnalysisMonth = new FragmentAnalysisMonth();
        fragmentAnalysisYear = new FragmentAnalysisYear();
        fragmentManager = parent.getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_parent, fragmentAnalysisDay).commitAllowingStateLoss();

        //binding.day.setOnClickListener(this);
        //binding.month.setOnClickListener(this);
        //binding.year.setOnClickListener(this);

        dataList.add(activity.getString(R.string.sunVal));
        dataList.add(activity.getString(R.string.uv));
        dataList.add(activity.getString(R.string.vitamin_d));
        dataList.add(activity.getString(R.string.exercise));
        dataList.add(activity.getString(R.string.walk));
        dataList.add(activity.getString(R.string.step));
        dataList.add(activity.getString(R.string.light));
        dataList.add(activity.getString(R.string.rest));
        dataList.add(activity.getString(R.string.kal));
        dataList.add(activity.getString(R.string.water));

        pickerLayoutManager = new PickerLayoutManager(activity,PickerLayoutManager.HORIZONTAL,false);
        pickerLayoutManager.setChangeAlpha(true);
        pickerLayoutManager.setScaleDownBy(0.99f);
        pickerLayoutManager.setScaleDownDistance(0.8f);


        PickerAdapter adapter = new PickerAdapter(activity, dataList, binding.dataSpinner);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.dataSpinner);
        binding.dataSpinner.setLayoutManager(pickerLayoutManager);
        binding.dataSpinner.setAdapter(adapter);


        binding.sgDate.setOnCheckedChangeListener(this);

        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setDataDetails(intent);
            }
        };

        activity.registerReceiver(br,new IntentFilter("DrawDataDetails"));

        binding.arrowLeft.setOnClickListener(this);
        binding.arrowRight.setOnClickListener(this);

        binding.imgCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (timeIndex){
                    case 0:
                        fragmentAnalysisDay.calenderClick();
                        break;
                    case 1:
                        fragmentAnalysisMonth.calendarClick();
                        break;
                    case 2:
                        fragmentAnalysisYear.calenderClick();
                        break;

                }
            }
        });
    }

    @Override
    public View onCreateView(View view) {

        Log.i("!!!Ana","onCreateView");
        if(MainActivity.listPetInfo == null || MainActivity.listPetInfo.size()==0){
            binding.llCaution.setVisibility(View.VISIBLE);
            binding.llAnalysis.setVisibility(View.GONE);

        }else{
            binding.llCaution.setVisibility(View.GONE);
            binding.llAnalysis.setVisibility(View.VISIBLE);
            binding.dataSpinner.smoothScrollToPosition(dataIndex);
        }


        return super.onCreateView(view);
    }

    private void setDataDetails(Intent intent){


        pickerLayoutManager.setOnScrollStopListener(new PickerLayoutManager.onScrollStopListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void selectedView(View view) {
                if(animate)
                    return;
                Log.i("!!!CALL", "whywbya");
                animate=true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        animate=false;
                        Log.i("!!!CALL", "falsefalse");
                    }
                }, 700);
                switch (timeIndex){
                    case 0:
                        Log.i("!!!CALL", "day");
                        dataIndex = dataList.indexOf(((TextView)view).getText().toString());
                        fragmentAnalysisDay.observerSpinner(dataIndex);
                        break;
                    case 1:
                        Log.i("!!!CALL", "month");
                        dataIndex = dataList.indexOf(((TextView)view).getText().toString());
                        fragmentAnalysisMonth.observerSpinner(dataIndex);
                        break;
                    case 2:
                        Log.i("!!!CALL", "year");
                        dataIndex = dataList.indexOf(((TextView)view).getText().toString());
                        fragmentAnalysisYear.observerSpinner(dataIndex);
                        break;

                }
            }
        });

        binding.tvCalendar.setText(intent.getStringExtra("calendar"));
        binding.tvValue.setText(intent.getStringExtra("value"));
        binding.tvUnit.setText(intent.getStringExtra("unit"));
    }


    public void onClick(View view){
        switch (view.getId()){
            case R.id.arrow_left:
                if(dataIndex==0){
                    return;
                }else{
                    binding.dataSpinner.smoothScrollToPosition(--dataIndex);

                }
                break;

            case R.id.arrow_right:
                if(dataIndex == dataList.size()-1)
                    return;
                else
                    binding.dataSpinner.smoothScrollToPosition(++dataIndex);
        }
    }

    public void getPetData(){
        switch (timeIndex){
            case 0:
                fragmentAnalysisDay.getPetDayData();
                break;
            case 1:
                fragmentAnalysisMonth.getPetMonthData();
                break;
            case 2:
                fragmentAnalysisYear.getPetYearData();
                break;
        }
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

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        fragmentManager = parent.getChildFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        binding.fragmentParent.removeAllViews();
        int i=0;
        if(checkedId==R.id.day) i=0; else if(checkedId==R.id.month) i=1; else i=2;
        timeIndex = i;

        switch (timeIndex){
            case 0:
                transaction.replace(R.id.fragment_parent, fragmentAnalysisDay).commitAllowingStateLoss();
                break;
            case 1:
                transaction.replace(R.id.fragment_parent, fragmentAnalysisMonth).commitAllowingStateLoss();
                break;
            case 2:
                transaction.replace(R.id.fragment_parent, fragmentAnalysisYear).commitAllowingStateLoss();
                break;
        }
    }
}
