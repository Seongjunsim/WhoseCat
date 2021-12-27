package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.RequiresApi;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetData;
import com.jmsmart.whosecat.data.commondata.PetGoalData;
import com.jmsmart.whosecat.databinding.ActivityDailyBinding;
import com.jmsmart.whosecat.view.base.CommonModel;

public class DailyViewModel implements CommonModel, View.OnClickListener {
    ActivityDailyBinding binding;
    Activity activity;
    final int SUN = 0;
    final int EXER = 1;
    final int VIT = 2;

    int sun = 0;
    int exer = 0;
    int vit = 0;
    PetData today;
    int checkIndex = 0;
    private final String[] VAL_LIST = {"sun", "uv", "vitD", "exercise", "walk", "step", "luxpol", "rest", "kal", "water"};
    private PetGoalData goalData = new PetGoalData();
    public DailyViewModel(ActivityDailyBinding binding, Activity activity, PetData today) {
        this.binding = binding;
        this.activity = activity;
        this.today = today;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        binding.cvSun.setOnClickListener(this);
        binding.cvExer.setOnClickListener(this);
        binding.cvVit.setOnClickListener(this);
        setStateData();
        setClickColor(checkIndex);
        setComments(checkIndex);
        setImageData();

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setImageData(){
        int goalNum = 0;

        for(int i=0; i<VAL_LIST.length; i++){

            int value = (int)Double.parseDouble(today.getPetDate(VAL_LIST[i]));
            int goal = (int)Double.parseDouble(goalData.getPetDate(VAL_LIST[i]));

            if(goal<=value){
                goalNum++;
                ImageView imageView = new ImageView(activity);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(50,50);
                params.leftMargin =5;
                params.rightMargin = 5;
                imageView.setLayoutParams(params);


                switch (VAL_LIST[i]){
                    case "sun":
                        imageView.setImageResource(R.drawable.sun);
                        break;
                    case "luxpol":
                        imageView.setImageResource(R.drawable.luxpol);
                        break;
                    case "vitD":
                        imageView.setImageResource(R.drawable.vitd);
                        break;
                    case "uv":
                        imageView.setImageResource(R.drawable.uv);
                        break;
                    case "exercise":
                        imageView.setImageResource(R.drawable.exercise);
                        break;
                    case "walk":
                        imageView.setImageResource(R.drawable.walk);
                        break;
                    case "step":
                        imageView.setImageResource(R.drawable.step);
                        break;
                    case "rest":
                        imageView.setImageResource(R.drawable.rest);
                        break;
                    case "kal":
                        imageView.setImageResource(R.drawable.kal);
                        break;
                    case "water":
                        imageView.setImageResource(R.drawable.water);
                        imageView.setColorFilter(activity.getColor(R.color.colorPrimary));
                        break;
                }

                binding.llImageTodayGoal.addView(imageView);
            }

        }

        binding.valueTodayGoal.setText(String.valueOf(goalNum));
    }

    public void setComments(int type){
        String one = "";
        String comment = "";
        switch (type){
            case SUN:
                if(sun==1){

                }else if(sun==2){

                }
                else {

                }

                break;

            case EXER:
                if(exer==1){

                }else if(exer==2){
                    binding.tvSun.setText(activity.getString(R.string.middle));
                }
                else {
                    binding.tvSun.setText(activity.getString(R.string.high));
                }
                break;

            case VIT:
                if(vit<=2){

                }else if(vit<=4){

                }
                else {

                }
                break;
        }
        binding.tvOne.setText(one);
        binding.tvComment.setText(comment);
    }

    private void setStateData(){
        sun = getLevel("sun");
        exer = getLevel("exercise");
        vit = getLevel("vitD")+getLevel("uv");


        if(sun==1){
            binding.tvSun.setText(activity.getString(R.string.low));
        }else if(sun==2)binding.tvSun.setText(activity.getString(R.string.middle));
        else binding.tvSun.setText(activity.getString(R.string.high));

        if(exer==1){
            binding.tvExercise.setText(activity.getString(R.string.low));
        }else if(exer==2)binding.tvExercise.setText(activity.getString(R.string.middle));
        else binding.tvExercise.setText(activity.getString(R.string.high));

        if(vit<=2){
            binding.tvVit.setText(activity.getString(R.string.low));
        }else if(vit<=4)binding.tvVit.setText(activity.getString(R.string.middle));
        else binding.tvVit.setText(activity.getString(R.string.high));

        setComments(sun);
    }

    private int getLevel(String type){
        int todayData = (int)Double.parseDouble(today.getPetDate(type));
        int goal = (int)Double.parseDouble(goalData.getPetDate(type));

        int l = (int)todayData*10/goal;

        if(l<4){
            return 1;
        }else if(l<7){
            return 2;
        }else return 3;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setClickColor(int type){
        switch (type){
            case SUN:
                binding.tvSun.setTextColor(Color.WHITE);
                binding.titleSun.setTextColor(Color.WHITE);
                binding.imgSun.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                binding.cvSun.setCardBackgroundColor(activity.getColor(R.color.colorPrimary));
                break;

            case EXER:
                binding.tvExercise.setTextColor(Color.WHITE);
                binding.titleExer.setTextColor(Color.WHITE);
                binding.imgExer.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                binding.cvExer.setCardBackgroundColor(activity.getColor(R.color.colorPrimary));
                break;

            case VIT:
                binding.tvVit.setTextColor(Color.WHITE);
                binding.titleVit.setTextColor(Color.WHITE);
                binding.imgVit.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                binding.cvVit.setCardBackgroundColor(activity.getColor(R.color.colorPrimary));
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void setClickDefalutColor(int type){
        switch (type){
            case SUN:
                binding.tvSun.setTextColor(activity.getColor(R.color.colorPrimary));
                binding.titleSun.setTextColor(activity.getColor(R.color.colorPrimary));
                binding.imgSun.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorPrimary)));
                binding.cvSun.setCardBackgroundColor(Color.WHITE);
                break;

            case EXER:
                binding.tvExercise.setTextColor(activity.getColor(R.color.colorPrimary));
                binding.titleExer.setTextColor(activity.getColor(R.color.colorPrimary));
                binding.imgExer.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorPrimary)));
                binding.cvExer.setCardBackgroundColor(Color.WHITE);
                break;

            case VIT:
                binding.tvVit.setTextColor(activity.getColor(R.color.colorPrimary));
                binding.titleVit.setTextColor(activity.getColor(R.color.colorPrimary));
                binding.imgVit.setBackgroundTintList(ColorStateList.valueOf(activity.getColor(R.color.colorPrimary)));
                binding.cvVit.setCardBackgroundColor(Color.WHITE);
                break;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        int index = 0;
        switch (v.getId()){
            case R.id.cv_sun:
                index = SUN;
                if(index==checkIndex)return;
                setClickDefalutColor(checkIndex);
                checkIndex = SUN;
                setClickColor(checkIndex);
                break;
            case R.id.cv_exer:
                index = EXER;
                if(index==checkIndex)return;
                setClickDefalutColor(checkIndex);
                checkIndex = EXER;
                setClickColor(checkIndex);

                break;
            case R.id.cv_vit:
                index = VIT;
                if(index==checkIndex)return;
                setClickDefalutColor(checkIndex);
                checkIndex = VIT;
                setClickColor(checkIndex);

                break;
        }

        setComments(checkIndex);
    }
}
