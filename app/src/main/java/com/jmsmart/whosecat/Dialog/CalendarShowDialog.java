package com.jmsmart.whosecat.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.jmsmart.whosecat.Database.CalendarTable;
import com.jmsmart.whosecat.Database.RoomTypeConverter;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.serverdata.CalendarData;
import com.jmsmart.whosecat.view.com.MainActivity;
import com.jmsmart.whosecat.view.com.viewModel.CalendarDayViewModel;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

public class CalendarShowDialog extends Dialog {
    private ListView mListView;
    private Button btn_exit;
    private LinkedList<CalendarData> relayList;
    private Context context;
    private ImageView img_add;
    private ImageView img_delete;
    private Calendar calendar;
    private CalendarData dialogData;
    CalendarDialogAdapter mAdapter;

    CalendarData clickedItem;
    Calendar clickedCalendar;
    int clickedItemPosition;

    CalendarData modifiedItem;

    DisplayMetrics dm;
    private int dialogW;
    private int dialogH;

    CalendarDayViewModel calendarDayViewModel;

    public CalendarShowDialog(@NonNull final Context context, CalendarDayViewModel calendarDayViewModel) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar_show_dialog);
        this.calendarDayViewModel = calendarDayViewModel;
        this.relayList = calendarDayViewModel.relayList;
        this.context = context;
        this.calendar = calendarDayViewModel.mCalendar.getValue();

        dm = context.getApplicationContext().getResources().getDisplayMetrics();
        dialogW = dm.widthPixels * 95 / 100;
        dialogH = dm.heightPixels / 2;

        CalendarShowDialog.ButtonClickListener onClickListener = new CalendarShowDialog.ButtonClickListener();

        mListView = (ListView)findViewById(R.id.calendar_listview);
        dataSetting();

        btn_exit = (Button)findViewById(R.id.btn_exit);
        btn_exit.setOnClickListener(onClickListener);

        img_add = (ImageView)findViewById(R.id.img_add);
        img_add.setOnClickListener(onClickListener);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                clickedItem = mAdapter.getItem(position);
                clickedCalendar = Calendar.getInstance();
                clickedCalendar.setTimeInMillis(clickedItem.getTime());
                clickedItemPosition = position;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("일정을 수정하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        CalendarModifyDialog cd = new CalendarModifyDialog(context, clickedCalendar, new CalendarModifyDialog.ModifyDialogListener() {
                            @Override
                            public void clickModify(final CalendarData data) {
                                modifiedItem = data;
                                modifiedItem.setCalendarId(clickedItem.getCalendarId());
                                CalendarModiftAsyncTask task = new CalendarModiftAsyncTask();
                                task.execute();
                            }
                        });
                        WindowManager.LayoutParams wm = cd.getWindow().getAttributes();
                        wm.copyFrom(cd.getWindow().getAttributes());
                        wm.width = dialogW;
                        wm.height = dialogH;
                        cd.show();
                        dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return false;
            }
        });
    }

    private void dataSetting(){
        mAdapter = new CalendarDialogAdapter();
        mAdapter.setContext(context);
        mAdapter.setCalendarDayViewModel(calendarDayViewModel);
        mAdapter.setCalendarShowDialog(this);

        for(int i=0;i<relayList.size();i++){
            mAdapter.addItem(relayList.get(i));
        }
        mListView.setAdapter(mAdapter);
    }
    class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.btn_exit:
                    dismiss();
                    break;
                case R.id.img_add:
                    CalendarAddDialog cd = new CalendarAddDialog(context, calendar, new CalendarAddDialog.AddDialogListener() {
                        @Override
                        public void clickOK(final CalendarData data) {
                            dialogData = data;
                            CalendarAddAsyncTask task = new CalendarAddAsyncTask();
                            task.execute();
                        }
                    });
                    WindowManager.LayoutParams wm = cd.getWindow().getAttributes();
                    wm.copyFrom(cd.getWindow().getAttributes());
                    wm.width = dialogW;
                    wm.height = dialogH;
                    cd.show();

                    dismiss();
                    break;
            }
        }
    }

    //일정 데이터를 추가하는 asynctask
    private class CalendarAddAsyncTask extends AsyncTask<String, Long, Boolean> {
        @Override
        protected void onPreExecute(){ }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                MainActivity.calendarDao.insertCalendarData(RoomTypeConverter.fromCalendarData(dialogData));
                List<CalendarTable> byId= MainActivity.calendarDao
                        .loadAllByIdAndTime(MainActivity.user.userId, calendar.getTimeInMillis());
                relayList.clear();
                for(int i=0;i<byId.size();i++) {
                    relayList.add(RoomTypeConverter.fromCalendarTable(byId.get(i)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            calendarDayViewModel.calendarDataList.setValue(relayList);
        }
    }

    //일정 데이터 수정 AsyncTask
    private class CalendarModiftAsyncTask extends AsyncTask<String, Long, Boolean> {
        @Override
        protected void onPreExecute(){ }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                MainActivity.calendarDao.updateCalendarData(modifiedItem.getCalendarId(), modifiedItem.getUserId(),
                        modifiedItem.getCategory(), modifiedItem.getComment(), modifiedItem.getTime());
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            calendarDayViewModel.updateRelayListItem(modifiedItem, clickedItemPosition);
        }
    }
}
