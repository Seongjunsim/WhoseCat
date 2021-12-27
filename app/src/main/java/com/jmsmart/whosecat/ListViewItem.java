package com.jmsmart.whosecat;

public class ListViewItem {
    private String dataStr;

    public void setText(String _data){
        dataStr = _data;
    }

    public String getData(){
        return this.dataStr;
    }
}
