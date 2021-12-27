package com.jmsmart.whosecat.data.commondata;

import com.taosif7.android.ringchartlib.models.RingChartData;

public class MyRingChartData extends RingChartData {
    /**
     * Doughnut ring data item
     *
     * @param value absolute Integer value
     * @param color resolved int colour !!!NOT RESOURCE ID
     * @param label unique Label for this item
     */


    public MyRingChartData(float value, int color, String label) {
        super(value, color, label);
    }



    @Override
    public int compareTo(RingChartData item) {
        return 0;
    }


}
