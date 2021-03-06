package com.jmsmart.whosecat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.Gson;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.serverdata.CalendarData;
import com.jmsmart.whosecat.databinding.CalendarHeaderBinding;
import com.jmsmart.whosecat.databinding.CalendarListBinding;
import com.jmsmart.whosecat.databinding.DayItemBinding;
import com.jmsmart.whosecat.databinding.EmptyDayBinding;
import com.jmsmart.whosecat.view.com.viewModel.CalendarHeaderViewModel;
import com.jmsmart.whosecat.view.com.viewModel.CalendarDayViewModel;
import com.jmsmart.whosecat.view.com.viewModel.CalendarEmptyViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

public class CalendarAdapter extends ListAdapter<Object, RecyclerView.ViewHolder> {
    private final int HEADER_TYPE = 0;
    private final int EMPTY_TYPE = 1;
    private final int DAY_TYPE = 2;
    private Context context;
    private LifecycleOwner lifecycleOwner;
    public static ArrayList<CalendarDayViewModel> dayViewModels = new ArrayList<>();

    public CalendarAdapter() {
        super(new DiffUtil.ItemCallback<Object>() {
            @Override
            public boolean areItemsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                return oldItem == newItem;
            }

            @Override
            public boolean areContentsTheSame(@NonNull Object oldItem, @NonNull Object newItem) {
                Gson gson = new Gson();
                return gson.toJson(oldItem).equals(gson.toJson(newItem));
            }
        });
    }

    public void setLifecycleOwner(LifecycleOwner l){
        this.lifecycleOwner = l;
    }

    @Override
    public int getItemViewType(int position) { //????????? ?????????
        Object item = getItem(position);
        if (item instanceof Long) {
            return HEADER_TYPE; //?????? ??????
        } else if (item instanceof String) {
            return EMPTY_TYPE; // ???????????? ?????? ??????
        }else {
            return DAY_TYPE; // ?????? ??????
        }
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == HEADER_TYPE) { // ?????? ??????
            CalendarHeaderBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_calendar_header, parent, false);
            StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) binding.getRoot().getLayoutParams();
            params.setFullSpan(true); //Span??? ????????? ??????
            binding.getRoot().setLayoutParams(params);
            return new HeaderViewHolder(binding);
        } else if (viewType == EMPTY_TYPE) { //???????????? ?????? ??????
            EmptyDayBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_day_empty, parent, false);
            return new EmptyViewHolder(binding);
        } else{
            context = parent.getContext();
            DayItemBinding binding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_day, parent, false);// ?????? ??????
            return new DayViewHolder(binding);
        }
    }

    //position??? ???????????? ViewHolder??? ????????????, Data??? ViewHolder??? ??????????????? ???.
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int viewType = getItemViewType(position);
        if (viewType == HEADER_TYPE) { //?????? ?????? ?????????
            HeaderViewHolder holder = (HeaderViewHolder) viewHolder;
            Object item = getItem(position);
            CalendarHeaderViewModel model = new CalendarHeaderViewModel();
            if (item instanceof Long) {
                model.setHeaderDate((Long) item);
            }
            holder.setViewModel(model);
        } else if (viewType == EMPTY_TYPE) { //???????????? ?????? ?????? ?????????
            EmptyViewHolder holder = (EmptyViewHolder) viewHolder;
            CalendarEmptyViewModel model = new CalendarEmptyViewModel();
            holder.setViewModel(model);
        } else if (viewType == DAY_TYPE) { // ?????? ?????? ?????????
            final DayViewHolder holder = (DayViewHolder) viewHolder;
            Object item = getItem(position);
            final CalendarDayViewModel model = new CalendarDayViewModel(position,context);
            if (item instanceof Calendar) {
                model.setCalendar((Calendar) item);
            }
            holder.setViewModel(model);
            setCalendarDataListObserver(model,holder);
        }
    }

    //MutableList??? ?????? observer??????
    private void setCalendarDataListObserver(final CalendarDayViewModel model,final DayViewHolder holder){
        model.calendarDataList.observe(lifecycleOwner, new Observer<LinkedList<CalendarData>>() {
            @Override
            public void onChanged(LinkedList<CalendarData> calendarData) {
                if(model.relayList != null){
                    if(model.relayList.size() == 0){
                        holder.binding.imgCalendarCircle1.setVisibility(View.INVISIBLE);
                        holder.binding.imgCalendarCircle2.setVisibility(View.INVISIBLE);
                        holder.binding.imgCalendarCircle3.setVisibility(View.INVISIBLE);
                    }else if(model.relayList.size() == 1){
                        holder.binding.imgCalendarCircle1.setVisibility(View.VISIBLE);
                        holder.binding.imgCalendarCircle2.setVisibility(View.INVISIBLE);
                        holder.binding.imgCalendarCircle3.setVisibility(View.INVISIBLE);
                    }else if(model.relayList.size() == 2){
                        holder.binding.imgCalendarCircle1.setVisibility(View.VISIBLE);
                        holder.binding.imgCalendarCircle2.setVisibility(View.VISIBLE);
                        holder.binding.imgCalendarCircle3.setVisibility(View.INVISIBLE);
                    }else if(model.relayList.size() >= 3){
                        holder.binding.imgCalendarCircle1.setVisibility(View.VISIBLE);
                        holder.binding.imgCalendarCircle2.setVisibility(View.VISIBLE);
                        holder.binding.imgCalendarCircle3.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }


    private class HeaderViewHolder extends RecyclerView.ViewHolder { //?????? ?????? ViewHolder
        private CalendarHeaderBinding binding;

        private HeaderViewHolder(@NonNull CalendarHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarHeaderViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }
    }


    private class EmptyViewHolder extends RecyclerView.ViewHolder { // ???????????? ?????? ?????? ViewHolder
        private EmptyDayBinding binding;

        private EmptyViewHolder(@NonNull EmptyDayBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarEmptyViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }

    }

    private class DayViewHolder extends RecyclerView.ViewHolder {// ?????? ?????? ViewHolder
        private DayItemBinding binding;

        private DayViewHolder(@NonNull DayItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void setViewModel(CalendarDayViewModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
            binding.getModel().setBinding(this.binding);
            dayViewModels.add(model);
        }
    }
}