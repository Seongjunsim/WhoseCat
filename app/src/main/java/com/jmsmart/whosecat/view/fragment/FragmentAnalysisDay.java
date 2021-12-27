package com.jmsmart.whosecat.view.fragment;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.databinding.FragmentAnalysisDayBinding;
import com.jmsmart.whosecat.view.com.viewModel.FragmentAnalysisDayViewModel;


public class FragmentAnalysisDay extends Fragment {

    private FragmentAnalysisDayViewModel model;
    private FragmentAnalysisDayBinding binding;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if(binding==null){
            binding = DataBindingUtil.inflate(inflater,R.layout.fragment_analysis_day, container, false);
            Log.i("Binding","makeNew");
        }

        View root = binding.getRoot();

        if(model==null)
            model = new FragmentAnalysisDayViewModel(container.getContext(), binding);
        return model.onCreateView(root);
    }

    public void getPetDayData(){
        model.getPetDayData();
    }

    public void observerSpinner(int i){
        model.observeSpinner(i);
    }

    public void calenderClick(){
        model.calendarClick();
    }


}