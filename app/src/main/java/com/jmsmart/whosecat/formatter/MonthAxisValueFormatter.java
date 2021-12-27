package com.jmsmart.whosecat.formatter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Calendar;

public class MonthAxisValueFormatter extends ValueFormatter {

    private BarChart barChart;

    public MonthAxisValueFormatter(BarChart barChart){
        this.barChart = barChart;
    }

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        Long date=0l;
        if(index==0)
            date = (Long)barChart.getData().getDataSetByIndex(0).getEntryForIndex(index).getData();
        else{
            if(barChart.getData().getDataSetByIndex(0).getEntryCount() > index-1)
                date = (Long)barChart.getData().getDataSetByIndex(0).getEntryForIndex(index-1).getData();
        }

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        String month = String.valueOf(c.get(Calendar.MONTH)+1);
        String day = String.valueOf(c.get(Calendar.DATE));

        if(index!=0)
            return month + "/" +day;
        else
            return "";
    }

}
