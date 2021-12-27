package com.jmsmart.whosecat.view.com.viewModel;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jmsmart.whosecat.Dialog.YearPickerDialog;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetData;
import com.jmsmart.whosecat.data.commondata.PetGoalData;
import com.jmsmart.whosecat.data.commondata.PetLimitYData;
import com.jmsmart.whosecat.data.serverdata.AnalysisData_Year;
import com.jmsmart.whosecat.databinding.FragmentAnalysisMonthBinding;
import com.jmsmart.whosecat.databinding.FragmentAnalysisYearBinding;
import com.jmsmart.whosecat.formatter.YearAxisValueFormatter;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.view.base.FragViewModel;
import com.jmsmart.whosecat.view.com.MainActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FragmentAnalysisYearViewModel extends FragViewModel {
    private FragmentAnalysisYearBinding binding;
    private AppCompatActivity activity;
    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;
    private int index = 0;
    private final String[] VAL_LIST = {"sun", "uv", "vitD", "exercise", "walk", "step", "luxpol", "rest", "kal", "water"};
    private final String[] VAL_UNIT = {"lux", "점", "iu", "분", "분", "bark", "점", "분", "kcal", "ml"};
    private String type;
    private LinkedList<PetData> listPetDayData;
    private ArrayList<String> list;
    private List<BarEntry> data = new ArrayList<>();
    private Date selectedDate = new Date();
    private boolean isFirst = true;
    private PetGoalData petGoalData = new PetGoalData();
    public FragmentAnalysisYearViewModel(final AppCompatActivity activity, FragmentAnalysisYearBinding binding){
        this.activity = activity;
        this.binding = binding;

        BASE_URL = activity.getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);


        //setTvDate(selectedDate);

        XAxis xAxis = binding.chart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setAxisMinimum(0);
        xAxis.setLabelCount(4, false);
        //xAxis.setGranularity(4f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);


        YAxis leftAxis = binding.chart.getAxisLeft();
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(30f,10f,5f);
        leftAxis.setLabelCount(3, false);

        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(1000f);
        //leftAxis.setGranularityEnabled(true);
        //leftAxis.setGranularity(0.1f);

        YAxis rightAxis = binding.chart.getAxisRight();
        leftAxis.setDrawAxisLine(false);
        rightAxis.setDrawGridLines(false);
        //rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(3, false);
        rightAxis.setAxisMinimum(0);
        rightAxis.setAxisMaximum(1000);
        rightAxis.setGranularity(0.1f);
        rightAxis.setDrawAxisLine(false);
        Legend l = binding.chart.getLegend();
        l.setEnabled(false);

        binding.chart.setDrawBarShadow(false);
        binding.chart.setDrawValueAboveBar(true);
        binding.chart.getDescription().setEnabled(false);
        binding.chart.setTouchEnabled(true);

        binding.chart.setDrawGridBackground(false);
        binding.chart.setPinchZoom(false);
        binding.chart.setDoubleTapToZoomEnabled(false);
        //binding.chart.setVisibleXRangeMaximum(6f);
        //binding.chart.setVisibleXRangeMaximum(6f);
        binding.chart.setAutoScaleMinMaxEnabled(false);
        binding.chart.setScaleEnabled(false);
        binding.chart.setHighlightPerTapEnabled(false);



    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

        @Override

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth, 23,49,49);
            selectedDate.setTime(c.getTimeInMillis());
            //setTvDate(selectedDate);
            isFirst = true;
            getPetYearData(MainActivity.listIndex, selectedDate);
        }

    };



    public void calendarClick(){
        Calendar c = Calendar.getInstance();
        YearPickerDialog yearPickerDialog = new YearPickerDialog();
        yearPickerDialog.setListener(listener);
        yearPickerDialog.show( activity.getSupportFragmentManager(), "YearAnd");
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setData() {
        int count = listPetDayData.size();
        data.clear();
        makeEntries();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        PetGoalData pgd = new PetGoalData();
        for (int i = 0; i < count; i++) {
            entries.add(data.get(i));
            if(data.get(i).getY() >= (int)Float.parseFloat(pgd.getPetDate(VAL_LIST[index]))){
                colors.add(activity.getColor(R.color.colorPrimary));
            }else{
                colors.add(Color.GRAY);
            }

        }
        BarEntry barEntry;

        BarDataSet set;

        if (binding.chart.getData() != null &&
                binding.chart.getData().getDataSetCount() > 0) {
            binding.chart.getData().clearValues();
            set = new BarDataSet(entries, VAL_LIST[index]);
            set.setDrawValues(true);
            set.setColors(colors);



        } else {
            set = new BarDataSet(entries, VAL_LIST[index]);
            set.setDrawValues(true);
            set.setColors(colors);

        }

        BarData data = new BarData(set);
        data.setValueTextSize(10f);
        //data.setValueTypeface(tfLight);
        data.setDrawValues(true);
        data.setBarWidth(0.8f);
        data.setHighlightEnabled(true);
        data.setDrawValues(false);

        binding.chart.setData(data);
        ValueFormatter xAxisFormatter = new YearAxisValueFormatter();
        binding.chart.getXAxis().setValueFormatter(xAxisFormatter);
        binding.chart.setClickable(true);
        binding.chart.setTouchEnabled(true);
        binding.chart.setHighlightPerTapEnabled(false);

        binding.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Log.i("Click","Click");
                //Toast.makeText(activity,e.getX()+" "+e.getY(),Toast.LENGTH_SHORT).show();
                int i = (int) e.getX();
                Intent intent = new Intent("DrawDataDetails");
                Calendar calendar = Calendar.getInstance();

                long date = 0;
                if(i==0)
                    return;
                else
                    date = (Long)binding.chart.getData().getDataSetByIndex(0).getEntryForIndex(i-1).getData();
                calendar.setTimeInMillis(date);

                String c = calendar.get(Calendar.YEAR)+"년 "+(calendar.get(Calendar.MONTH)+1)+"월 ";
                String value = String.valueOf((int)e.getY());

                intent.putExtra("calendar", c);
                intent.putExtra("value", value);
                intent.putExtra("unit",VAL_UNIT[index]+"/일");
                activity.sendBroadcast(intent);

            }

            @Override
            public void onNothingSelected() {
                Log.i("NOClick","NOClick");
            }
        });

        binding.notifyChange();
        //binding.chart.setVisibleXRangeMaximum(8);
        binding.chart.animateY(1000);

        Intent intent = new Intent("DrawDataDetails");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        String c = calendar.get(Calendar.YEAR)+"년 ";
        intent.putExtra("calendar", c);
        intent.putExtra("value", "");
        intent.putExtra("unit","");
        activity.sendBroadcast(intent);

    }

    private void makeEntries(){
        for(PetData petData : listPetDayData){
            Calendar c = Calendar.getInstance();

            c.setTimeInMillis(petData.time);
            Log.i("getgetget",String.valueOf(c.get(Calendar.MONTH)));
            data.add(new BarEntry(c.get(Calendar.MONTH)+1, Float.valueOf(petData.getPetDate(VAL_LIST[index])),c.getTimeInMillis()));
        }


    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void observeSpinner(int i){
        index = i;
        Log.i("!!!Month", "observe");
        PetLimitYData p = new PetLimitYData();
        binding.chart.getAxisLeft().setAxisMaximum((int)Float.parseFloat(p.getPetMonthDate(VAL_LIST[i])));
        binding.chart.getAxisLeft().setAxisMinimum(0);
        binding.chart.getAxisRight().setAxisMaximum((int)Float.parseFloat(p.getPetMonthDate(VAL_LIST[i])));
        binding.chart.getAxisRight().setAxisMinimum(0);

        float goal = Float.valueOf(petGoalData.getPetDate(VAL_LIST[i]));
        LimitLine li = new LimitLine(goal,"goal");

        li.enableDashedLine(30,10,5);
        li.setLineWidth(2f);
        li.setLineColor(activity.getColor(R.color.colorPrimaryDark));
        li.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);

        binding.chart.getAxisLeft().removeAllLimitLines();
        binding.chart.getAxisLeft().addLimitLine(li);
        binding.chart.getAxisLeft().setDrawTopYLabelEntry(true);

        setData();
    }





    private void getPetYearData(int listIndex, Date requestDate){
        Date frontDate = new Date();
        frontDate.setTime(requestDate.getTime());

        Calendar c = Calendar.getInstance();
        c.setTime(frontDate);
        System.out.println(c.toString());

        Date rearDate = new Date();
        rearDate.setTime(requestDate.getTime());
        rearDate.setMonth(0);
        rearDate.setDate(1);
        rearDate.setHours(0);
        rearDate.setMinutes(0);
        rearDate.setSeconds(0);

        Log.i("!!!Tick",rearDate.toString() +" "+frontDate.toString());

        AnalysisData_Year analysisData = new AnalysisData_Year(MainActivity.listPetInfo.get(listIndex).petID,c.get(Calendar.YEAR));
        service.sensorRequestYear(analysisData).enqueue(new Callback<ServerResponse>(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();

                if(result.getCode() == ServerResponse.ANALYSIS_SUCCESS){
                    try{
                        listPetDayData = getPetData(result.getMessage());

                        if(isFirst){
                            isFirst=false;
                            observeSpinner(FragmentAnalysisViewModel.dataIndex);
                        }


                        Log.i("getget", String.valueOf(listPetDayData.size()));
                        list = new ArrayList<String>(Arrays.asList(VAL_LIST));
                        //binding.spDataType.setAdapter(new ArrayAdapter<>(context,android.R.layout.simple_spinner_dropdown_item, list));
                        //binding.spDataType.setOnItemSelectedListener(spinnerListener);

                        Intent intent = new Intent("init");
                        activity.sendBroadcast(intent);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    listPetDayData = null;
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t){
                t.printStackTrace();
            }
        });

    }

    private LinkedList<PetData> getPetData(String message) throws JSONException, ParseException {
        LinkedList<PetData> l = new LinkedList<PetData>();
        JSONArray ja = new JSONArray(message);
        for(int i=0; i<ja.length(); i++){
            JSONObject object = (JSONObject) ja.get(i);
            l.add(new PetData(object));
        }
        return l;
    }


    @Override
    public View onCreateView(View view) {
        Log.i("!!!Year", "onCreateView");
        isFirst = true;
        getPetYearData(MainActivity.listIndex, selectedDate);
        return super.onCreateView(view);
    }

    public void getPetYearData(){
        isFirst = true;
        getPetYearData(MainActivity.listIndex, selectedDate);
    }


    @Override
    public void onAttach() {
        super.onAttach();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
