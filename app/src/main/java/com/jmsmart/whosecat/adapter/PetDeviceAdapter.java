package com.jmsmart.whosecat.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.clj.fastble.BleManager;
import com.jmsmart.whosecat.Database.ImageTable;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.util.ImageTask;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetDeviceAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<PetInfo> data;
    private LayoutInflater layoutInflater;

    public PetDeviceAdapter(Activity activity, ArrayList<PetInfo> data) {
        this.activity = activity;
        this.data = data;
        layoutInflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        if(i<data.size() && i>=0)
            return data.get(i);
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            view = layoutInflater.inflate(R.layout.list_pet_device, viewGroup, false);
            viewHolder = new ViewHolder();
            viewHolder.img_pet = (CircleImageView)view.findViewById(R.id.img_blue);
            viewHolder.tv_name = (TextView)view.findViewById(R.id.txt_name);
            viewHolder.tv_mac = (TextView)view.findViewById(R.id.txt_mac);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }

        ImageTable imageTable = new ImageTable();
        imageTable.setPetID(data.get(i).petID);

        ImageTask imageTask = new ImageTask(imageTable, viewHolder.img_pet, activity);
        imageTask.execute(ImageTask.SELECT);

        viewHolder.tv_name.setText(data.get(i).petName+activity.getString(R.string.string_of)+" WhoseCat");
        if(TextUtils.isEmpty(data.get(i).device) || data.get(i).device.equals("NULL")){
            viewHolder.tv_mac.setText(activity.getString(R.string.please_click_and_register));
        }else{
            if(BleManager.getInstance().isConnected(data.get(i).device)){
                viewHolder.tv_mac.setText(data.get(i).device+" "+activity.getString(R.string.connecting));
            }else
                viewHolder.tv_mac.setText(data.get(i).device+" "+activity.getString(R.string.disconnecting));
        }

        return view;
    }

    private class ViewHolder{
        public CircleImageView img_pet;
        public TextView tv_name;
        public TextView tv_mac;
    }
}
