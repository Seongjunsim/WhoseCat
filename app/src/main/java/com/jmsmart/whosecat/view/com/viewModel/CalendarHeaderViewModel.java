package com.jmsmart.whosecat.view.com.viewModel;

import androidx.lifecycle.ViewModel;

import com.jmsmart.whosecat.data.commondata.TSLiveData;

public class CalendarHeaderViewModel extends ViewModel {
    public TSLiveData<Long> mHeaderDate = new TSLiveData<>();

    public void setHeaderDate(long headerDate) {
        this.mHeaderDate.setValue(headerDate);
    }
}
