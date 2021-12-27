package com.jmsmart.whosecat.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.serverdata.CalendarData;
import com.jmsmart.whosecat.view.com.MainActivity;

import java.util.Calendar;

public class CalendarModifyDialog extends Dialog {
    TextView header;
    Button modify;
    Button cancel;
    EditText comment;
    private CalendarModifyDialog.ModifyDialogListener dialogListener;
    private CalendarData dialogData;

    public CalendarModifyDialog(Context context, Calendar calendar, CalendarModifyDialog.ModifyDialogListener dialogListener){
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.calendar_add_dialog);
        dialogData = new CalendarData();
        dialogData.setUserId(MainActivity.user.userId);
        dialogData.setTime(calendar.getTimeInMillis());

        this.dialogListener = dialogListener;

        CalendarModifyDialog.ButtonClickListener onClickListener = new CalendarModifyDialog.ButtonClickListener();

        RadioGroup rg = (RadioGroup)findViewById(R.id.radio_group);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if(checkedId == R.id.hospital){
                    dialogData.setCategory(CalendarData.HOSPITAL);
                }else if(checkedId == R.id.purchase){
                    dialogData.setCategory(CalendarData.PURCHASE_FEED);
                }else if(checkedId == R.id.vaccination){
                    dialogData.setCategory(CalendarData.VACCINATION);
                }else if(checkedId == R.id.beauty){
                    dialogData.setCategory(CalendarData.BEAUTY);
                }else if(checkedId == R.id.bath){
                    dialogData.setCategory(CalendarData.BATH);
                }else{
                    dialogData.setCategory(CalendarData.ETC);
                }
            }
        });

        comment = (EditText)findViewById(R.id.comment);
        modify = (Button)findViewById(R.id.ok);

        modify.setOnClickListener(onClickListener);
        cancel = (Button)findViewById(R.id.cancel);
        cancel.setOnClickListener(onClickListener);

        header = (TextView)findViewById(R.id.header_textview);
        header.setText("일정 수정");
        modify.setText("수정");
    }

    public interface ModifyDialogListener{
        void clickModify(CalendarData data);
    }

    class ButtonClickListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            switch(view.getId()){
                case R.id.ok:
                    dialogData.setComment(comment.getText().toString());
                    dialogListener.clickModify(dialogData);
                    dismiss();
                    break;
                case R.id.cancel:
                    dismiss();
                    break;
            }
        }
    }
}
