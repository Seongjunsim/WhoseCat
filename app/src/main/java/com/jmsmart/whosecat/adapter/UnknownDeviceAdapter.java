package com.jmsmart.whosecat.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.clj.fastble.data.BleDevice;
import com.jmsmart.whosecat.R;

import java.util.LinkedList;

public class UnknownDeviceAdapter extends BaseAdapter {

    private LinkedList<BleDevice> data;
    private Activity activity;
    private LayoutInflater layoutInflater;



    public UnknownDeviceAdapter(Activity activity){
        this.activity = activity;
        this.data = new LinkedList<>();
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public void addData(BleDevice bleDevice){
        data.add(bleDevice);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        final BleDevice bleDevice = data.get(i);
        View itemView = convertView;

        if(itemView == null){
            viewHolder = new ViewHolder();
            itemView = activity.getLayoutInflater().inflate(R.layout.item_device_connect, viewGroup,false);
            viewHolder.tv_name = (TextView)itemView.findViewById(R.id.tv_devicename);
            viewHolder.tv_mac = (TextView)itemView.findViewById(R.id.tv_macAddress);
            viewHolder.btn_connect = (ImageButton)itemView.findViewById(R.id.btn_connect);
            itemView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) itemView.getTag();
        }

        viewHolder.tv_name.setText(bleDevice.getName());
        viewHolder.tv_mac.setText(bleDevice.getMac());
        viewHolder.btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("deviceInfo", bleDevice);
                activity.setResult(activity.RESULT_OK, i);
                activity.finish();

            }
        });
        return itemView;
    }

    public class ViewHolder{
        public TextView tv_name;
        public TextView tv_mac;
        public ImageButton btn_connect;
    }
}
