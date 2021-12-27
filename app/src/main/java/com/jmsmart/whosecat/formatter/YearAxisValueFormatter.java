package com.jmsmart.whosecat.formatter;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class YearAxisValueFormatter extends ValueFormatter {

    public String[] months = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    @Override
    public String getFormattedValue(float value) {
        int days = (int) value;
        if(value<=12 && value >=1)
            return months[days-1];
        else
            return "";
    }
}
