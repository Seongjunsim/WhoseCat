package com.jmsmart.whosecat.formatter;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.Calendar;

public class DayAxisValueFormatter extends ValueFormatter {

    private BarChart barChart;


    public DayAxisValueFormatter(BarChart chart) {
        this.barChart = chart;
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
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));

        if(index!=0)
            return hour+"시";
        else
            return "";

    }

}
