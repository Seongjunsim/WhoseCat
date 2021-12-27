package com.jmsmart.whosecat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jmsmart.whosecat.Database.ImageTable;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.util.ImageTask;

import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ExistDeviceAdapter extends BaseAdapter {

    private LinkedList<PetInfo> data;
    private Context context;
    private LayoutInflater layoutInflater;

    public ExistDeviceAdapter(Context context){
        this.context = context;
        this.data = new LinkedList<>();
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        if(i>=data.size() && i<0)
            return null;
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        PetInfo info = data.get(i);
        if(view == null){
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.list_exist_device, viewGroup, false);
            viewHolder.img_pet = view.findViewById(R.id.img_pet);
            viewHolder.tv_exist_pet_name = view.findViewById(R.id.tv_exist_pet_name);
            viewHolder.tv_device = view.findViewById(R.id.tv_device);
            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }
        ImageTable imageTable = new ImageTable();
        imageTable.setPetID(info.petID);
        ImageTask imageTask = new ImageTask(imageTable, viewHolder.img_pet, context);
        imageTask.execute(ImageTask.SELECT);
        viewHolder.tv_exist_pet_name.setText(info.petName);
        viewHolder.tv_device.setText(info.device);

        return view;
    }

    private class ViewHolder{
        CircleImageView img_pet;
        TextView tv_exist_pet_name;
        TextView tv_device;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void addData(PetInfo petInfo){
        data.add(petInfo);
        notifyDataSetChanged();
    }
}
