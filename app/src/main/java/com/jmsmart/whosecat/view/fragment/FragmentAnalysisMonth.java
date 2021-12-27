package com.jmsmart.whosecat.view.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.databinding.FragmentAnalysisDayBinding;
import com.jmsmart.whosecat.databinding.FragmentAnalysisMonthBinding;
import com.jmsmart.whosecat.view.com.viewModel.FragmentAnalysisMonthViewModel;


public class FragmentAnalysisMonth extends Fragment {

    private FragmentAnalysisMonthViewModel model;
    private FragmentAnalysisMonthBinding binding;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(binding==null){
            binding = DataBindingUtil.inflate(inflater,R.layout.fragment_analysis_month, container, false);
            Log.i("Binding","makeNew");
        }

        View root = binding.getRoot();

        if(model==null)
            model = new FragmentAnalysisMonthViewModel((AppCompatActivity) container.getContext(), binding);
        return model.onCreateView(root);
    }

    public void getPetMonthData(){
        model.getPetMonthData();
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void observerSpinner(int i){
        model.observeSpinner(i);
    }

    public void calendarClick(){
        model.calendarClick();
    }
}