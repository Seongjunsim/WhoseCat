package com.jmsmart.whosecat.view.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.databinding.FragmentAnalysisBinding;
import com.jmsmart.whosecat.view.com.viewModel.FragmentAnalysisViewModel;

public class FragmentAnalysis extends Fragment {

    FragmentAnalysisViewModel model;
    FragmentAnalysisBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,@Nullable Bundle savedInstanceState){
        if(binding==null){
            binding = DataBindingUtil.inflate(inflater,R.layout.fragment_analysis, container, false);
            Log.i("Binding","makeNew");
        }

        View root = binding.getRoot();

        if(model==null)
            model = new FragmentAnalysisViewModel(getActivity(), this,binding);
        return model.onCreateView(root);
    }

    public void getPetData(){
        if(model!=null)
            model.getPetData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        model.onDetach();
        super.onDetach();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
