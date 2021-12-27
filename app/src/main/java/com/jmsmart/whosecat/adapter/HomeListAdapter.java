package com.jmsmart.whosecat.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.data.commondata.MyRingChartData;
import com.jmsmart.whosecat.data.commondata.PetData;
import com.jmsmart.whosecat.data.commondata.PetGoalData;
import com.jmsmart.whosecat.data.serverdata.WaterData;
import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.util.ServiceApi;
import com.taosif7.android.ringchartlib.RingChart;
import com.taosif7.android.ringchartlib.models.RingChartData;



import java.util.ArrayList;
import java.util.List;

import az.plainpie.PieView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeListAdapter extends BaseAdapter {

    private static final int TOTAL_VIEW_NUM = 5;
    private PetData data;
    private Context context;
    private LayoutInflater layoutInflater;
    private final int SUN = 0; private final int UV = 1;private final int VIT = 2;private final int EXE = 3;private final int WALK = 4;
    private final int STEP = 5;private final int LUX = 6;private final int REST = 7;private final int KAL = 8;private final int WATER = 9;
    private final String[] VAL_LIST = {"sun", "uv", "vitD", "exercise", "walk", "step", "luxpol", "rest", "kal", "water"};
    private final String[] VAL_UNIT = {"lux", "점", "iu", "time", "time", "bark", "점", "time", "kcal", "ml"};
    private final float[] VAL_GOAL = {60000, 100,400,84, 510, 7908, 100, 846,548, 500};
    private int waterInput = 0;
    private int petID = 0;
    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener;
    private View.OnClickListener btnResetListener;
    private View.OnClickListener btnSumitListener;

    public HomeListAdapter(Context context, PetData data, int petID){
        this.context = context;
        this.data = data;
        this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.petID = petID;
        BASE_URL = context.getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);

    }



    @Override
    public int getCount() {
        return TOTAL_VIEW_NUM;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        /*
        if(view == null){
            viewHolder = new ViewHolder();

            view.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) view.getTag();
        }*/

        if(view==null || view != null){
            switch (i){
                case 0:
                    view = layoutInflater.inflate(R.layout.item_home_view_step_and_kcal, viewGroup, false);
                    StepViewHolder vh = new StepViewHolder();
                    vh.tv_step_value = (TextView)view.findViewById(R.id.home_view_item_step_value);
                    vh.tv_step_goal = (TextView)view.findViewById(R.id.home_view_item_step_goal);
                    vh.tv_kcal = (TextView)view.findViewById(R.id.home_view_item_txt_kcal);
                    vh.hbchart = (HorizontalBarChart)view.findViewById(R.id.home_view_horizon_bar_chart);

                    int stepval = Integer.parseInt(data.getPetDate(VAL_LIST[STEP]));
                    int kalval = (int) Math.round(Double.parseDouble(data.getPetDate(VAL_LIST[KAL])));

                    vh.tv_step_value.setText(String.format("%,d", stepval));
                    vh.tv_step_goal.setText("/"+String.format("%,d",Math.round(VAL_GOAL[STEP]))+VAL_UNIT[STEP]);
                    vh.tv_kcal.setText(String.format("%,d", kalval)+VAL_UNIT[KAL]);

                    vh.hbchart.setDrawBarShadow(false);
                    vh.hbchart.getDescription().setText("");
                    vh.hbchart.getLegend().setEnabled(false);
                    vh.hbchart.setPinchZoom(false);
                    vh.hbchart.setDrawValueAboveBar(true);
                    vh.hbchart.setDoubleTapToZoomEnabled(false);
                    vh.hbchart.setClickable(false);

                    XAxis xAxis = vh.hbchart.getXAxis();
                    xAxis.setDrawGridLines(false);
                    xAxis.setDrawAxisLine(false);
                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                    xAxis.setEnabled(false);

                    YAxis yAxis = vh.hbchart.getAxisLeft();
                    yAxis.setAxisMaximum(VAL_GOAL[STEP]);
                    yAxis.setAxisMinimum(0);
                    yAxis.setEnabled(false);
                    xAxis.setLabelCount(1);
                    YAxis yRAxis = vh.hbchart.getAxisRight();
                    yRAxis.setDrawAxisLine(true);
                    yRAxis.setDrawGridLines(false);
                    yRAxis.setEnabled(false);

                    ArrayList<BarEntry> entries = new ArrayList<>();
                    entries.add(new BarEntry(0f,Float.parseFloat(data.getPetDate(VAL_LIST[STEP]))));

                    BarDataSet barDataSet = new BarDataSet(entries, "step");
                    barDataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {
                            int percent = Math.round(value / VAL_GOAL[STEP] * 100);
                            if(percent > 100)
                                percent = 100;
                            return percent+"%";
                        }
                    });
                    barDataSet.setBarBorderColor(context.getColor(R.color.colorHomePieSecond));
                    BarData barData = new BarData(barDataSet);
                    barData.setValueTextSize(15);
                    barData.setValueTextColor(context.getColor(R.color.colorPrimaryDark));
                    vh.hbchart.setData(barData);
                    vh.hbchart.setDrawBarShadow(true);
                    vh.hbchart.invalidate();
                    vh.hbchart.animateY(1000);
                    break;
                case 1:
                    view = layoutInflater.inflate(R.layout.item_home_view_rest_walk_exercise, viewGroup, false);
                    //PieChart pieChart = (PieChart)view.findViewById(R.id.home_view_pie);
                    RingChart ringChart = (RingChart)view.findViewById(R.id.ring_chart);
                    ringChart.setLayoutMode(RingChart.renderMode.MODE_CONCENTRIC);
                    float restVal, walkVal, exerVal;
                    //restVal = 400f; walkVal = 100f; exerVal = 50f;
                    restVal = Float.parseFloat(data.getPetDate(VAL_LIST[REST]));
                    walkVal = Float.parseFloat(data.getPetDate(VAL_LIST[WALK]));
                    exerVal = Float.parseFloat(data.getPetDate(VAL_LIST[EXE]));

                    PetGoalData petGoalData = new PetGoalData();
                    MyRingChartData rest = new MyRingChartData(restVal / Float.parseFloat(petGoalData.getPetDate(VAL_LIST[REST])) ,context.getColor(R.color.colorRest),"휴식량");
                    MyRingChartData walk = new MyRingChartData(walkVal / Float.parseFloat(petGoalData.getPetDate(VAL_LIST[WALK])) ,context.getColor(R.color.colorWalk),"산책량");
                    MyRingChartData exer = new MyRingChartData(exerVal / Float.parseFloat(petGoalData.getPetDate(VAL_LIST[EXE])) ,context.getColor(R.color.colorExercise),"운동량");
                    List<RingChartData> list = new ArrayList<>();
                    list.add(rest); list.add(walk); list.add(exer);
                    ringChart.setData(list);

                    ((TextView)view.findViewById(R.id.home_view_item_rest_value)).setText(timeTextFormatter((int)restVal));
                    ((TextView)view.findViewById(R.id.home_view_item_walk_value)).setText(timeTextFormatter((int)walkVal));
                    ((TextView)view.findViewById(R.id.home_view_item_exer_value)).setText(timeTextFormatter((int)exerVal));

                    /*
                    pieChart.getDescription().setText("");
                    pieChart.getLegend().setEnabled(true);
                    pieChart.setDrawHoleEnabled(false);
                    pieChart.setTransparentCircleRadius(80f);
                    pieChart.setEntryLabelColor(R.color.black);
                    ArrayList<PieEntry> values = new ArrayList<>();
                    values.add(new PieEntry(Float.parseFloat(data.getPetDate(VAL_LIST[REST]))+33f, context.getString(R.string.rest)));
                    values.add(new PieEntry(Float.parseFloat(data.getPetDate(VAL_LIST[EXE]))+33f, context.getString(R.string.exercise)));
                    values.add(new PieEntry(Float.parseFloat(data.getPetDate(VAL_LIST[WALK]))+33f, context.getString(R.string.walk)));
                    pieChart.animateY(1000);

                    PieDataSet dataSet = new PieDataSet(values,"");
                    dataSet.setSelectionShift(3f);
                    dataSet.setSelectionShift(5f);
                    dataSet.setColors(context.getColor(R.color.colorRest), context.getColor(R.color.colorExercise), context.getColor(R.color.colorWalk));
                    dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                    dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
                    dataSet.setValueFormatter(new ValueFormatter() {
                        @Override
                        public String getFormattedValue(float value) {

                            int time = (int)value-33;
                            return String.format("%02d",time/60) + context.getString(R.string.hour) + String.format(" %02d",time%60) + context.getString(R.string.minute);
                        }
                    });
                    dataSet.setValueLinePart1OffsetPercentage(80f);
                    dataSet.setValueLinePart2Length(0.5f);
                    dataSet.setValueLinePart1Length(0.5f);
                    dataSet.setSliceSpace(1f);
                    PieData pieData = new PieData(dataSet);
                    pieData.setValueTextSize(15);
                    pieData.setValueTextColor(context.getColor(R.color.colorPrimaryDark));
                    pieChart.setData(pieData);*/
                    break;

                case 2:
                    view = layoutInflater.inflate(R.layout.item_home_view_sun_vit_lux, viewGroup, false);
                    SunViewHolder sunViewHolder = new SunViewHolder();
                    sunViewHolder.tv_sun_val = (TextView)view.findViewById(R.id.home_view_item_sun_value);
                    sunViewHolder.tv_sun_goal = (TextView)view.findViewById(R.id.home_view_item_sun_goal);
                    sunViewHolder.tv_vit_val = (TextView)view.findViewById(R.id.home_view_item_vit_value);
                    sunViewHolder.tv_vit_goal = (TextView)view.findViewById(R.id.home_view_item_vit_goal);

                    int sunVal = Integer.parseInt(data.getPetDate(VAL_LIST[SUN]));

                    sunViewHolder.tv_sun_val.setText(String.format("%,d", sunVal));
                    sunViewHolder.tv_sun_goal.setText("/"+String.format("%,d",Math.round(VAL_GOAL[SUN]))+VAL_UNIT[SUN]);
                    sunViewHolder.tv_vit_val.setText(makeInteger(data.getPetDate(VAL_LIST[VIT])));
                    sunViewHolder.tv_vit_goal.setText("/"+Math.round(VAL_GOAL[VIT])+VAL_UNIT[VIT]);



                    break;
                case 3:
                    view = layoutInflater.inflate(R.layout.item_home_view_uv_lux, viewGroup, false);
                    TextView tv_uv_val = (TextView)view.findViewById(R.id.home_view_item_uv_value);
                    TextView tv_uv_goal = (TextView)view.findViewById(R.id.home_view_item_uv_goal);
                    TextView tv_lux_val = (TextView)view.findViewById(R.id.home_view_item_lux_value);
                    TextView tv_lux_goal = (TextView)view.findViewById(R.id.home_view_item_lux_goal);

                    tv_lux_val.setText(makeInteger(data.getPetDate(VAL_LIST[LUX])));
                    tv_lux_goal.setText("/"+Math.round(VAL_GOAL[LUX])+VAL_UNIT[LUX]);
                    tv_uv_val.setText(makeInteger(data.getPetDate(VAL_LIST[UV])));
                    tv_uv_goal.setText("/"+Math.round(VAL_GOAL[UV])+VAL_UNIT[UV]);
                    break;

                case 4:
                    view = layoutInflater.inflate(R.layout.item_home_view_item_water, viewGroup,false);
                    TextView tv_water_value = (TextView)view.findViewById(R.id.home_view_item_water_value);
                    final TextView tv_water_input = (TextView)view.findViewById(R.id.home_view_item_water_input_value);
                    SeekBar sb_water = (SeekBar)view.findViewById(R.id.sb_water);
                    Button btn_submit = (Button)view.findViewById(R.id.btn_submit);
                    Button btn_reset = (Button)view.findViewById(R.id.btn_reset);

                    tv_water_value.setText(data.getPetDate(VAL_LIST[WATER]));
                    tv_water_input.setText(String.valueOf(waterInput));
                    sb_water.setProgress(waterInput);

                    if(seekBarChangeListener==null){
                        seekBarChangeListener = new SeekBar.OnSeekBarChangeListener(){

                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                waterInput = progress;
                                tv_water_input.setText(String.valueOf(waterInput));
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        };
                        sb_water.setOnSeekBarChangeListener(seekBarChangeListener);
                    }

                    if(btnResetListener==null){
                        btnResetListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                waterInput=0;
                                tv_water_input.setText(String.valueOf(waterInput));
                            }
                        };
                        btn_reset.setOnClickListener(btnResetListener);
                    }
                    if(btnSumitListener==null){
                        btnSumitListener = new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                WaterData data = new WaterData(petID, waterInput);
                                service.waterSend(data).enqueue(new Callback<ServerResponse>(){
                                    @Override
                                    public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                                        ServerResponse result = response.body();
                                        if(result.getCode() == ServerResponse.WATER_SUCCESS){
                                            Intent intent = new Intent("update");
                                            context.sendBroadcast(intent);
                                        }
                                        else{
                                            System.out.println(result.getMessage());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ServerResponse> call, Throwable t){
                                        t.printStackTrace();
                                    }
                                });
                            }
                        };
                        btn_submit.setOnClickListener(btnSumitListener);
                    }
                    break;
            }

            return view;
        }else return view;





        /*
        if(i%2==0)
            view = layoutInflater.inflate(R.layout.fragment_home_list_item1, viewGroup, false);
        else
            view = layoutInflater.inflate(R.layout.fragment_home_list_item2,viewGroup, false);
        viewHolder.oddOrPair = i%2;
        viewHolder.tv_stadard = view.findViewById(R.id.tv_standart);
        viewHolder.tv_data_explain= view.findViewById(R.id.tv_data_exp);
        viewHolder.tv_data_name = view.findViewById(R.id.tv_data_name);
        viewHolder.pv_data = view.findViewById(R.id.pv_data);

        viewHolder.tv_stadard.setText("/ "+VAL_GOAL[i]);
        //viewHolder.tv_data_explain.setText();
        viewHolder.tv_data_name.setText(VAL_LIST[i]);

        viewHolder.pv_data.setPercentage(Float.valueOf(data.getPetDate(VAL_LIST[i]))*100/VAL_GOAL[i]);
        viewHolder.pv_data.setInnerText(data.getPetDate(VAL_LIST[i])+" "+VAL_UNIT[i]);
        viewHolder.pv_data.setInnerBackgroundColor(Color.WHITE);
        viewHolder.pv_data.setTextColor(Color.BLACK);
        viewHolder.pv_data.setPercentageTextSize(15);
        viewHolder.pv_data.setScrollBarSize(10);

        if(i%2==0){
            viewHolder.pv_data.setPercentageBackgroundColor(context.getColor(R.color.colorHomeMain));
            viewHolder.pv_data.setMainBackgroundColor(context.getColor(R.color.colorHomePieBackFirst));
        }else{
            viewHolder.pv_data.setPercentageBackgroundColor(context.getColor(R.color.colorHomePieSecond));
            viewHolder.pv_data.setMainBackgroundColor(context.getColor(R.color.colorHomePieBackSecond));
        }*/





        //return view;
    }


    private String makeInteger(String floatingNum){
        return String.valueOf((int)Float.parseFloat(floatingNum));
    }
    private class ViewHolder{
        int oddOrPair;
        PieView pv_data;
        TextView tv_data_name;
        TextView tv_data_explain;
        TextView tv_stadard;
    }

    private class StepViewHolder{
        TextView tv_title;
        TextView tv_step_value;
        TextView tv_step_goal;
        TextView tv_kcal;
        HorizontalBarChart hbchart;
    }

    private class RWEViewHolder{
        PieView pieView;
    }

    private class SunViewHolder{
        TextView tv_sun_val;
        TextView tv_sun_goal;
        TextView tv_vit_val;
        TextView tv_vit_goal;


    }

    public String timeTextFormatter(int minute){
        int h = minute / 60;
        int m = minute%60;


       return String.format("%02d:%02d",h,m);
    }
}
