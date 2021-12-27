package com.jmsmart.whosecat.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.serverdata.CalendarData;
import com.jmsmart.whosecat.view.com.MainActivity;

import java.util.Calendar;

public class CalendarAddDialog extends Dialog {
    Button ok;
    Button cancel;
    EditText comment;
    private AddDialogListener dialogListener;
    private CalendarData data;

    public CalendarAddDialog(Context context, Calendar calendar, AddDialogListener dialogListener){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar_add_dialog);
        data = new CalendarData();
        data.setUserId(MainActivity.user.userId);
        data.setTime(calendar.getTimeInMillis());

        this.dialogListener = dialogListener;

        ButtonClickListener onClickListener = new ButtonClickListener();

        RadioGroup rg = (RadioGroup)findViewById(R.id.radio_group);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.hospital){
                    data.setCategory(CalendarData.HOSPITAL);
                }else if(checkedId == R.id.purchase){
                    data.setCategory(CalendarData.PURCHASE_FEED);
                }else if(checkedId == R.id.vaccination){
                    data.setCategory(CalendarData.VACCINATION);
                }else if(checkedId == R.id.beauty){
                    data.setCategory(CalendarData.BEAUTY);
                }else if(checkedId == R.id.bath){
                    data.setCategory(CalendarData.BATH);
                }else{
                    data.setCategory(CalendarData.ETC);
                }
            }
        });

        comment = (EditText)findViewById(R.id.comment);
        ok = (Button)findViewById(R.id.ok);

        ok.setOnClickListener(onClickListener);
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);
    }

    public interface AddDialogListener{
        void clickOK(CalendarData data);
    }

    class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.ok:
                    data.setComment(comment.getText().toString());
                    dialogListener.clickOK(data);
                    dismiss();
                    break;
                case R.id.cancel:
                    dismiss();
                    break;
            }
        }
    }
}
