package com.jmsmart.whosecat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jmsmart.whosecat.ListViewItem;
import com.jmsmart.whosecat.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private TextView dataTextview;

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    //생성자
    public ListViewAdapter(){

    }

    //Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount(){
        return listViewItemList.size();
    }

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final int pos = position;
        final Context context = parent.getContext();

        //"listview_item" Layout을 inflate하여 convertView참조 획득
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item, parent, false);
        }

            dataTextview = (TextView)convertView.findViewById(R.id.datas);

            ListViewItem listViewItem = listViewItemList.get(position);

        dataTextview.setText(listViewItem.getData());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
    }

    public void addItem(String _data) {
        ListViewItem item = new ListViewItem();

        item.setText(_data);
        listViewItemList.add(item);
    }

    public void setItem(String _text){
        System.out.println(listViewItemList.size());
        listViewItemList.get(listViewItemList.size()-1).setText(_text);
    }
}

