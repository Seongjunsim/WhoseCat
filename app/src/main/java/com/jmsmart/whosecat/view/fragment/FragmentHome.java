package com.jmsmart.whosecat.view.fragment;

import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetData;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.databinding.FragmentHomeBinding;
import com.jmsmart.whosecat.view.com.viewModel.FragmentHomeViewModel;

import java.util.LinkedList;

public class FragmentHome extends Fragment {

    private FragmentHomeBinding binding;
    private FragmentHomeViewModel model;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("FragmentHome","success");
        //model.onCreate();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        if(binding == null)
            binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false);
        View root = binding.getRoot();
        Log.i("FragH","onCreateView");
        if(model==null)
            model = new FragmentHomeViewModel(getActivity(), binding);
        return model.onCreateView(root);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        model.onSavedInstanceState(outState);
        Log.i("FragH","save");
    }

    public void setList(LinkedList<PetInfo> list, int index){
        model.setList(list, index);
    }
    public void bindingPetData(PetData data){
        model.bindingPetData(data);
    }

    public void setStateText(String temp){model.setStateText(temp);}
    public void setBattery(String battery, String sync){
        model.setBattery(battery,sync);
    }
    public void defaultPetData(){model.defaultPetData();}
    public void stopRefreshing(){
        model.stopRefreshing();
    }
    public void setImg(){model.setImg();}
    public void setPetListEmpty(){
        model.setPetListEmpty();
    }
}
