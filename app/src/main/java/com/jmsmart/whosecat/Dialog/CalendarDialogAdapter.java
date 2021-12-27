package com.jmsmart.whosecat.Dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jmsmart.whosecat.Database.CalendarTable;
import com.jmsmart.whosecat.Database.RoomTypeConverter;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.DateFormat;
import com.jmsmart.whosecat.data.serverdata.CalendarData;
import com.jmsmart.whosecat.view.com.MainActivity;
import com.jmsmart.whosecat.view.com.viewModel.CalendarDayViewModel;

import java.util.LinkedList;
import java.util.List;

public class CalendarDialogAdapter extends BaseAdapter {
    private LinkedList<CalendarData> mList = new LinkedList<>();
    private Context context;
    private CalendarData deleteData;
    private CalendarDayViewModel model;
    private CalendarShowDialog showDialog;

    public void setContext(Context context){
        this.context = context;
    }

    public void setCalendarDayViewModel(CalendarDayViewModel model){
        this.model = model;
    }

    public void setCalendarShowDialog(CalendarShowDialog showDialog){
        this.showDialog = showDialog;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public CalendarData getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        final Context context = viewGroup.getContext();

        if(view == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_calendar_info, viewGroup, false);
        }

        TextView category_txt = (TextView)view.findViewById(R.id.category_textview);
        TextView comment_txt = (TextView)view.findViewById(R.id.comment_textview);
        TextView date_txt = (TextView)view.findViewById(R.id.date_textview);

        CalendarData myItem = getItem(position);

        switch(myItem.getCategory()){
            case CalendarData.HOSPITAL:
                category_txt.setText(R.string.calendar_category_hospital);
                break;
            case CalendarData.PURCHASE_FEED:
                category_txt.setText(R.string.calendar_category_purchase_feed);
                break;
            case CalendarData.VACCINATION:
                category_txt.setText(R.string.calendar_category_vaccination);
                break;
            case CalendarData.BEAUTY:
                category_txt.setText(R.string.calendar_category_beauty);
                break;
            case CalendarData.BATH:
                category_txt.setText(R.string.calendar_category_bath);
                break;
            case CalendarData.ETC:
                category_txt.setText(R.string.calendar_category_ect);
                break;
        }
        comment_txt.setText(myItem.getComment());
        date_txt.setText(DateFormat.getDate(myItem.getTime(), DateFormat.MONTH_DAY_FORMAT));

        ImageView img_delete = (ImageView)view.findViewById(R.id.img_delete);
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData = getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("일정을 삭제하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){
                        showDialog.dismiss();
                        CalendarDialogAdapter.CalendarDeleteAsyncTask task = new CalendarDialogAdapter.CalendarDeleteAsyncTask();
                        task.execute();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id){

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    public void addItem(CalendarData data){
        CalendarData mItem = new CalendarData();

        mItem.setComment(data.getComment());
        mItem.setCategory(data.getCategory());
        mItem.setUserId(data.getUserId());
        mItem.setTime(data.getTime());

        mList.add(data);
    }

    //일정 데이터 삭제 AsyncTask
    private class CalendarDeleteAsyncTask extends AsyncTask<String, Long, Boolean> {
        @Override
        protected void onPreExecute(){

        }

        @Override
        protected Boolean doInBackground(String... strings) {
            try{
                MainActivity.calendarDao.deleteCalendarData(deleteData.getCalendarId());
                List<CalendarTable> byId = MainActivity.calendarDao.loadAllByIdAndTime(deleteData.getUserId(), deleteData.getTime());

                model.relayList = new LinkedList<CalendarData>();
                for(int i=0;i<byId.size();i++){
                    model.relayList.add(RoomTypeConverter.fromCalendarTable(byId.get(i)));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            model.calendarDataList.setValue(model.relayList);
        }
    }
}
