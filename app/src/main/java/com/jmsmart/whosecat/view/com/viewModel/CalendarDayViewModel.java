package com.jmsmart.whosecat.view.com.viewModel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jmsmart.whosecat.Database.CalendarTable;
import com.jmsmart.whosecat.Database.RoomTypeConverter;
import com.jmsmart.whosecat.Dialog.CalendarShowDialog;
import com.jmsmart.whosecat.data.serverdata.CalendarData;
import com.jmsmart.whosecat.data.commondata.TSLiveData;
import com.jmsmart.whosecat.databinding.DayItemBinding;
import com.jmsmart.whosecat.view.com.MainActivity;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class CalendarDayViewModel extends ViewModel {
    public TSLiveData<Calendar> mCalendar = new TSLiveData<>();
    private Context context;
    public int position;

    public MutableLiveData<LinkedList<CalendarData>> calendarDataList = new MutableLiveData<LinkedList<CalendarData>>();
    public LinkedList<CalendarData> relayList;

    public DayItemBinding binding;

    DisplayMetrics dm;
    private int dialogW;
    private int dialogH;

    private CalendarData dialogData;

    public CalendarDayViewModel(int position, Context context){
        this.position = position;
        this.context = context;
        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        dialogW = dm.widthPixels * 95 / 100;
        dialogH = dm.heightPixels / 2;
    }

    public void setCalendar(Calendar calendar) {
        this.mCalendar.setValue(calendar);
        DaySettingAsyncTask task = new DaySettingAsyncTask();
        task.execute();
    }

    public void setPosition(int position){
        this.position = position;
    }

    public void setBinding(DayItemBinding binding){
        this.binding = binding;
    }

    //짧은 클릭
    public void click(){
        CalendarShowDialog cd = new CalendarShowDialog(context,this);
        WindowManager.LayoutParams wm = cd.getWindow().getAttributes();
        wm.copyFrom(cd.getWindow().getAttributes());
        wm.width = dialogW;
        wm.height = dialogH;
        cd.show();
    }

    //일자타입 초기화 시 일정의 개수를 구해줄 AsyncTask
    private class DaySettingAsyncTask extends AsyncTask<String, Long, Boolean> {
        @Override
        protected void onPreExecute(){ }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                List<CalendarTable> byId = MainActivity.calendarDao
                        .loadAllByIdAndTime(MainActivity.user.userId, mCalendar.getValue().getTimeInMillis());
                relayList = new LinkedList<CalendarData>();
                for(int i=0;i<byId.size();i++){
                    relayList.add(RoomTypeConverter.fromCalendarTable(byId.get(i)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            calendarDataList.setValue(relayList);
        }
    }

    public void updateRelayListItem(CalendarData data, int position){
        relayList.set(position,data);
    }
}
