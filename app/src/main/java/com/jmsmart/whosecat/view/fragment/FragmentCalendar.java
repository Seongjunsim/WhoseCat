package com.jmsmart.whosecat.view.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jmsmart.whosecat.BR;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.adapter.CalendarAdapter;
import com.jmsmart.whosecat.databinding.CalendarListBinding;
import com.jmsmart.whosecat.view.com.viewModel.CalendarListViewModel;

import java.util.ArrayList;

public class FragmentCalendar extends Fragment {
    private CalendarListBinding binding;
    private CalendarAdapter calendarAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false);
        binding.setVariable(BR.model, new ViewModelProvider(this).get(CalendarListViewModel.class));
        binding.setLifecycleOwner(this);
        binding.getModel().setActivity(getActivity());
        binding.getModel().initCalendarList();
        binding.getModel().setBinding(binding);
        binding.getModel().setContext(getContext());

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(7, StaggeredGridLayoutManager.VERTICAL);
        calendarAdapter = new CalendarAdapter();
        calendarAdapter.setLifecycleOwner(getViewLifecycleOwner());
        binding.listCalendar.setLayoutManager(manager);
        binding.listCalendar.setAdapter(calendarAdapter);
        observe();
        return binding.getRoot();
    }

    //CalendarListViewModel의 mCalendarList가 변화할 때마다 onChanged함수 호출
    private void observe() {
        binding.getModel().mCalendarList.observe(getViewLifecycleOwner(), new Observer<ArrayList<Object>>() {
            @Override
            public void onChanged(ArrayList<Object> objects) {
                calendarAdapter.submitList(objects);
                if (binding.getModel().mCenterPosition > 0) {
                    binding.listCalendar.scrollToPosition(binding.getModel().mCenterPosition);
                }
            }
        });
    }
}
