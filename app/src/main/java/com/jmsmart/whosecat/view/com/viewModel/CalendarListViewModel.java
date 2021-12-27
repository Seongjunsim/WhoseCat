package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.RadioGroup;

import androidx.lifecycle.ViewModel;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.adapter.CalendarAdapter;
import com.jmsmart.whosecat.data.commondata.DateFormat;
import com.jmsmart.whosecat.data.commondata.Keys;
import com.jmsmart.whosecat.data.commondata.TSLiveData;
import com.jmsmart.whosecat.databinding.CalendarListBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class CalendarListViewModel extends ViewModel {
    private long mCurrentTime;

    public Activity activity;
    public TSLiveData<String> mTitle = new TSLiveData<>();
    public static TSLiveData<ArrayList<Object>> mCalendarList = new TSLiveData<>(new ArrayList<>());

    public CalendarListBinding binding;
    public int mCenterPosition; 

    private Context context;

    //캘린더 초기화 및 날짜 검색을 위한 list
    ArrayList<Object> calendarList;

    public void setBinding(CalendarListBinding binding){
        this.binding = binding;
        setRadioGroupListener();
    }

    private void setRadioGroupListener(){
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                //CalendarAdapter.dayViewModels.get(some_index).relayList.get(some_index).getCategory()
                switch(radioGroup.getCheckedRadioButtonId()){
                    case R.id.all:
                        //라디오 버튼을 눌렀을 때, 해당 카테고리를 가진 요소를 어떻게 찾지?
                        //-> CalendarData를 모든 날짜의 relayList를 검사해야지
                        //-> DayViewModel이 relayList를 가졌지.
                        //-> DayViewModel을 어떻게 접근하지?
                        break;
                    case R.id.hospital:

                        break;
                    case R.id.purchase:

                        break;
                    case R.id.vaccination:

                        break;
                    case R.id.bath:

                        break;
                    case R.id.etc:

                        break;
                }
            }
        });
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public void setContext(Context context){this.context = context;}

    public void setTitle(int position) {
        try {
            Object item = mCalendarList.getValue().get(position);
            if (item instanceof Long) {
                setTitle((Long) item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTitle(long time) {
        mCurrentTime = time;
        mTitle.setValue(DateFormat.getDate(time, DateFormat.CALENDAR_HEADER_FORMAT));
    }


    public void initCalendarList() {
        GregorianCalendar cal = new GregorianCalendar();
        setCalendarList(cal);
    }

    public void setCalendarList(GregorianCalendar cal) {
        setTitle(cal.getTimeInMillis());

        calendarList = new ArrayList<>();
        for (int i = -48; i < 48; i++) {
            try {
                GregorianCalendar calendar = new GregorianCalendar(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + i, 1, 0, 0, 0);

                if (i == 0) {
                    mCenterPosition = calendarList.size();
                }

                calendarList.add(calendar.getTimeInMillis());

                int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
                int max = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                for (int j = 0; j < dayOfWeek; j++) {
                    calendarList.add(Keys.EMPTY);
                }

                for (int j = 1; j <= max; j++) {
                    calendarList.add(new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), j));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mCalendarList.setValue(calendarList);
    }

    public void dateFindClick(){
        GregorianCalendar cal = new GregorianCalendar();
        DatePickerDialog dialog = new DatePickerDialog(context, datePickerListener_scroll, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener_scroll = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            GregorianCalendar nowCalendar = new GregorianCalendar();
            GregorianCalendar tempCalendar = new GregorianCalendar(year, monthOfYear,
                    dayOfMonth);

            int searchIndex=0;

            //찾고자 하는게 과거
            if(nowCalendar.getTimeInMillis() >= tempCalendar.getTimeInMillis()){
                GregorianCalendar searchCalendar = new GregorianCalendar(year, monthOfYear,
                        tempCalendar.getActualMinimum(GregorianCalendar.DAY_OF_MONTH));

                for(int i=0; i<calendarList.size(); i++){
                    if(calendarList.get(i) instanceof Long){
                        if((Long)calendarList.get(i)==searchCalendar.getTimeInMillis()){
                            searchIndex = i;
                            break;
                        }
                    }
                }
            }
            //찾고자 하는게 미래
            else{
                GregorianCalendar searchCalendar = new GregorianCalendar(year, monthOfYear,
                        tempCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));

                for(int i=0; i<calendarList.size(); i++){
                    if (calendarList.get(i) instanceof GregorianCalendar) {
                        GregorianCalendar indexCalendar = (GregorianCalendar)calendarList.get(i);
                        //System.out.println("indexCalendar   "+indexCalendar.getTime().toString());
                        if(indexCalendar.getTimeInMillis() == searchCalendar.getTimeInMillis()) {
                            //System.out.println("searchCalendar   "+searchCalendar.getTimeInMillis());
                            searchIndex = i;
                            break;
                        }
                    }
                }
            }

            binding.listCalendar.scrollToPosition(searchIndex);
        }
    };

    public void plusButtonClick(){
        GregorianCalendar cal = new GregorianCalendar();
        DatePickerDialog dialog = new DatePickerDialog(context, datePickerListener_add, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private DatePickerDialog.OnDateSetListener datePickerListener_add = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
            GregorianCalendar tempCalendar = new GregorianCalendar(year, monthOfYear,
                    dayOfMonth);
            GregorianCalendar searchCalendar = new GregorianCalendar(year, monthOfYear,
                    tempCalendar.getActualMaximum(GregorianCalendar.DAY_OF_MONTH));

            int searchIndex=0;

            for(int i=0; i<calendarList.size(); i++){
                if (calendarList.get(i) instanceof GregorianCalendar) {
                    GregorianCalendar indexCalendar = (GregorianCalendar)calendarList.get(i);
                    if(indexCalendar.getTimeInMillis() == searchCalendar.getTimeInMillis()) {
                        searchIndex = i;
                    }
                }
            }

            binding.listCalendar.scrollToPosition(searchIndex);

            for(int i=0;i<CalendarAdapter.dayViewModels.size();i++){
                Calendar c = CalendarAdapter.dayViewModels.get(i).mCalendar.getValue();
                System.out.println(c.getTime());
                if ((c.get(Calendar.YEAR) == year) && (c.get(Calendar.MONTH) == monthOfYear) && (c.get(Calendar.DAY_OF_MONTH) == dayOfMonth)){
                    //달력을 통해 검색한 날짜에 LongClick 이벤트를 발생시킴
                    CalendarAdapter.dayViewModels.get(i).binding.dayLayout.performClick();
                }
            }
        }
    };
}
