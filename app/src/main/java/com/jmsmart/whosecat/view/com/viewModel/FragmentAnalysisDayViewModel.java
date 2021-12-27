package com.jmsmart.whosecat.view.com.viewModel;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.PetData;
import com.jmsmart.whosecat.data.commondata.PetLimitXData;
import com.jmsmart.whosecat.data.serverdata.AnalysisData;
import com.jmsmart.whosecat.databinding.FragmentAnalysisDayBinding;
import com.jmsmart.whosecat.formatter.DayAxisValueFormatter;
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

public class FragmentAnalysisDayViewModel extends FragViewModel {
    private FragmentAnalysisDayBinding binding;
    private Context context;
    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;
    private int index = 0;
    private final String[] VAL_LIST = {"sun", "uv", "vitD", "exercise", "walk", "step", "luxpol", "rest", "kal", "water"};
    private String type;
    private final String TYPE_DAY = "day";
    private final String TYPE_MONTH = "month";
    private final String TYPE_YEAR = "year";
    private LinkedList<PetData> listPetHourData;
    private ArrayList<String> list;
    private List<BarEntry> data = new ArrayList<>();
    private Date selectedDate = new Date();
    private boolean isFirst = true;
    private final String[] VAL_UNIT = {"lux", "점", "iu", "분", "분", "step", "점", "분", "kcal", "ml"};
    public FragmentAnalysisDayViewModel(final Context context, FragmentAnalysisDayBinding binding){
        this.context = context;
        this.binding = binding;

        BASE_URL = context.getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);


        //setTvDate(selectedDate);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(binding.chart);

        XAxis xAxis = binding.chart.getXAxis();
        xAxis.setTextSize(10f);
        xAxis.setAxisMinimum(0);
        xAxis.setLabelCount(6, false);
        xAxis.setGranularity(3f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setDrawGridLines(false);



        YAxis leftAxis = binding.chart.getAxisLeft();

        leftAxis.setLabelCount(3, false);
        leftAxis.setDrawAxisLine(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.enableGridDashedLine(30f,10f,5f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(2000f);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setGranularity(0.1f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setSpaceMax(5);
        leftAxis.setSpaceMin(5);

        YAxis rightAxis = binding.chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawAxisLine(false);
        //rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(3, false);
        rightAxis.setAxisMinimum(0);
        rightAxis.setAxisMaximum(2000);
        rightAxis.setGranularity(0.1f);

        Legend l = binding.chart.getLegend();
        l.setEnabled(false);

        binding.chart.setDrawBarShadow(false);
        binding.chart.setDrawValueAboveBar(true);
        binding.chart.getDescription().setEnabled(false);
        binding.chart.setPinchZoom(false);

        binding.chart.setDrawGridBackground(false);
        binding.chart.setPinchZoom(false);
        binding.chart.setDoubleTapToZoomEnabled(false);
        binding.chart.setAutoScaleMinMaxEnabled(false);
        binding.chart.setFitBars(true);

        binding.chart.setAutoScaleMinMaxEnabled(false);
        binding.chart.setTouchEnabled(true);
        binding.chart.setHighlightPerTapEnabled(false);

    }

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

        @Override

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(year, monthOfYear, dayOfMonth, 23,59,59);
            selectedDate.setTime(c.getTimeInMillis());

            isFirst=true;
            getPetDayData(MainActivity.listIndex, selectedDate);
        }

    };

    public void calendarClick(){
        Log.i("click","click");
        Calendar c = Calendar.getInstance();
        c.setTime(selectedDate);
        DatePickerDialog dialog = new DatePickerDialog(context, listener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE));
        dialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setData() {
        final PetLimitXData petLimitXData = new PetLimitXData();
        int count = Integer.parseInt(petLimitXData.getPetDayDate(VAL_LIST[index]));
        data.clear();
        makeEntries();
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            entries.add(data.get(i));
        }

        BarDataSet set;

        if (binding.chart.getData() != null &&
                binding.chart.getData().getDataSetCount() > 0) {
            binding.chart.getData().clearValues();
            set = new BarDataSet(entries, VAL_LIST[index]);
            set.setDrawValues(true);
            set.setColor(context.getColor(R.color.colorPrimary));

        } else {
            set = new BarDataSet(entries, VAL_LIST[index]);
            set.setDrawValues(true);
            set.setColor(context.getColor(R.color.colorPrimary));
        }

        BarData data = new BarData(set);
        data.setValueTextSize(10f);
        //data.setValueTypeface(tfLight);

        data.setBarWidth(0.8f);
        data.setHighlightEnabled(true);
        data.setDrawValues(false);

        binding.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                //Log.i("Click","Click");
                //Toast.makeText(activity,e.getX()+" "+e.getY(),Toast.LENGTH_SHORT).show();

                int i = (int) e.getX() + Integer.parseInt(petLimitXData.getPetDayStart(VAL_LIST[index]));
                Intent intent = new Intent("DrawDataDetails");
                Calendar calendar = Calendar.getInstance();

                calendar.setTimeInMillis(selectedDate.getTime());
                int j = i==23?0:i+1;
                String c = calendar.get(Calendar.YEAR)+"년 "+(calendar.get(Calendar.MONTH)+1)+"월 "+calendar.get(Calendar.DAY_OF_MONTH)+"일 "+i+"시"+" ~ "+j+"시";
                String value = String.valueOf((int)e.getY());

                intent.putExtra("calendar", c);
                intent.putExtra("value", value);
                intent.putExtra("unit",VAL_UNIT[index]);
                context.sendBroadcast(intent);

            }

            @Override
            public void onNothingSelected() {
                Log.i("NOClick","NOClick");
            }
        });
        binding.chart.setData(data);
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(binding.chart);
        binding.chart.getXAxis().setValueFormatter(xAxisFormatter);
        binding.notifyChange();

        //binding.chart.setVisibleXRangeMaximum(8);
        binding.chart.animateY(1000);

        Intent intent = new Intent("DrawDataDetails");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);
        String c = calendar.get(Calendar.YEAR)+"년 "+(calendar.get(Calendar.MONTH)+1)+"월 "+(calendar.get(Calendar.DAY_OF_MONTH))+"일";
        intent.putExtra("calendar", c);
        intent.putExtra("value", "");
        intent.putExtra("unit","");
        context.sendBroadcast(intent);

    }

    private void makeEntries(){
        PetLimitXData pet = new PetLimitXData();
        for(int i=1; i<=Integer.parseInt(pet.getPetDayDate(VAL_LIST[index])); i++){
            PetData petData = listPetHourData.get(i+Integer.parseInt(pet.getPetDayStart(VAL_LIST[index])));
            data.add(new BarEntry(i,Float.valueOf(petData.getPetDate(VAL_LIST[index])),petData.time));
        }
/*
        for(PetData petData : listPetHourData){
            Date date = new Date(petData.time);
            data.add(new BarEntry(date.getHours(), Float.valueOf(petData.getPetDate(VAL_LIST[index])),date.getTime()));
        }*/

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void observeSpinner(int i){
        index = i;
        switch(VAL_LIST[i]){
            case "sun":
                binding.chart.getAxisLeft().setAxisMaximum(2000);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(2000);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "uv":
                binding.chart.getAxisLeft().setAxisMaximum(2000);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(2000);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "vitD":
                binding.chart.getAxisLeft().setAxisMaximum(2000);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(2000);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "exercise":
                binding.chart.getAxisLeft().setAxisMaximum(70);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(70);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "walk":
                binding.chart.getAxisLeft().setAxisMaximum(70);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(70);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "step":
                binding.chart.getAxisLeft().setAxisMaximum(2000);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(2000);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "luxpol":
                binding.chart.getAxisLeft().setAxisMaximum(20);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(20);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "rest":
                binding.chart.getAxisLeft().setAxisMaximum(70);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(70);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "kal":
                binding.chart.getAxisLeft().setAxisMaximum(300);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(300);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
            case "water":
                binding.chart.getAxisLeft().setAxisMaximum(1000);
                binding.chart.getAxisLeft().setAxisMinimum(0);
                binding.chart.getAxisRight().setAxisMaximum(1000);
                binding.chart.getAxisRight().setAxisMinimum(0);
                binding.chart.getXAxis().setAxisMinimum(0);

                break;
        }
        setData();
    }



    private void getPetDayData(int listIndex, Date requestDate){
        Date startDate = new Date();
        startDate.setTime(requestDate.getTime());
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);


        Date endDate = new Date();
        endDate.setTime(requestDate.getTime());
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);

        Log.i("!!!Tick","frontDate : "+startDate.getTime() + " "+"rearDate : "+endDate.getTime());

        AnalysisData analysisData = new AnalysisData(MainActivity.listPetInfo.get(listIndex).petID,startDate.getTime(), endDate.getTime());
        service.sensorRequestHour(analysisData).enqueue(new Callback<ServerResponse>(){

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();

                if(result.getCode() == ServerResponse.ANALYSIS_SUCCESS){
                    try{
                        listPetHourData = getPetData(result.getMessage());
                        if(isFirst){
                            isFirst=false;
                            observeSpinner(FragmentAnalysisViewModel.dataIndex);
                        }

                        for(PetData p : listPetHourData){
                            Date d = new Date(p.time);
                            Log.i("!!AV",d.toString());
                        }

                        list = new ArrayList<String>(Arrays.asList(VAL_LIST));
                        Intent intent = new Intent("init");
                        context.sendBroadcast(intent);

                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
                else{
                    //Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.i("Day","NoDATA");
                    listPetHourData = null;
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

        //네트워크로 JsonArray받아와서 각각 JsonObject로 변환 후 l에 add하기
        return l;
    }


    @Override
    public View onCreateView(View view) {
        Log.i("!!!CreateDay","dddd");
        isFirst=true;
        getPetDayData(MainActivity.listIndex, selectedDate);
        return super.onCreateView(view);
    }

    public void getPetDayData(){
        isFirst = true;
        this.getPetDayData(MainActivity.listIndex, selectedDate);
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
    // TODO: Implement the ViewModel
}