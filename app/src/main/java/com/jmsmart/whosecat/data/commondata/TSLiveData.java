package com.jmsmart.whosecat.data.commondata;

import androidx.lifecycle.MutableLiveData;

//LiveData는 Activity, Fragment의 수명주기내에서만 동작하는 요소이다.
public class TSLiveData<T> extends MutableLiveData<T> {
    public TSLiveData() {

    }

    public TSLiveData(T value) {
        setValue(value);
    }
}