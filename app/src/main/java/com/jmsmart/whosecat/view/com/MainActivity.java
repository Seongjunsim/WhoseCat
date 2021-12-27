package com.jmsmart.whosecat.view.com;

import android.Manifest;
import android.app.NativeActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.constraintlayout.widget.Group;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.google.android.material.navigation.NavigationView;
import com.jmsmart.whosecat.Database.AppDatabase;
import com.jmsmart.whosecat.Database.CalendarDao;
import com.jmsmart.whosecat.Database.ImageDao;
import com.jmsmart.whosecat.R;

import com.jmsmart.whosecat.response.ServerResponse;
import com.jmsmart.whosecat.data.commondata.Command;
import com.jmsmart.whosecat.data.commondata.PetData;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.data.commondata.User;
import com.jmsmart.whosecat.data.serverdata.AnalysisData;

import com.jmsmart.whosecat.data.serverdata.FindData_Pet;
import com.jmsmart.whosecat.service.CommandService;
import com.jmsmart.whosecat.util.ServiceApi;
import com.jmsmart.whosecat.util.SharedPreferencesUtil;
import com.jmsmart.whosecat.view.com.viewModel.FragmentAnalysisViewModel;
import com.jmsmart.whosecat.view.com.viewModel.PetDeviceManagementViewModel;
import com.jmsmart.whosecat.view.fragment.FragmentAnalysis;
import com.jmsmart.whosecat.view.fragment.FragmentCalendar;
import com.jmsmart.whosecat.view.fragment.FragmentHome;
import com.jmsmart.whosecat.view.fragment.FragmentSetting;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.UUID;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity{
    private final static int REQUEST_ENABLE_BT = 1; // 블루투스 실행 확인 값
    private final static int REQUEST_FINE_LOCATION = 2; //위치 정보 제공 확인 값

    BleGattCallback first;

    public static String USER_EMAIL;
    public static User user;
    public static LinkedList<PetInfo> listPetInfo;
    public Context context = this;
    public static int listIndex=0;

    public int pick=0;

    private final String TAG = "MainActivity";
    private String BASE_URL;
    private ServiceApi service;
    private Retrofit retrofit;

    private View headerView;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentHome fragmentHome = new FragmentHome();
    private FragmentCalendar fragmentCalendar = new FragmentCalendar();
    private FragmentAnalysis fragmentAnalysis = new FragmentAnalysis();
    private FragmentSetting fragmentSetting = new FragmentSetting();

    private BleManager bManager;

    private BroadcastReceiver brFromFragHome;
    private BroadcastReceiver receiver;
    private BroadcastReceiver brUpdate;

    private ProgressDialog progressDialog;

    public static AppDatabase appDatabase;
    public static CalendarDao calendarDao;
    public static ImageDao imageDao;

    private static String ACTION_SET_CMD = "bingo_action_set_cmd";
    private static String ACTION_VIEW_PROGRESS = "bingo_action_view_progress";

    private UUID BATTERY_SERVICE_UUID = UUID.fromString("0000180f-0000-1000-8000-00805f9b34fb");
    private UUID  BATTERY_CHAR_UUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb");
    private static final UUID DEVICE_INFORMATION_SERVICE_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    private static final UUID SW_REVISION_CHAR_UUID = UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb");

    private static final UUID GENERAL_SERVICE_UUID = UUID.fromString("0000fffe-0000-1000-8000-00805f9b34fb");
    private static final UUID SYSCMD_CHAR_UUID = UUID.fromString("0000ffff-0000-1000-8000-00805f9b34fb");

    private static final byte SYSCMD_SET_RTC = (byte)0x06;

    private static final byte SYSCMD_GET_UUID = (byte)0x0B;

    private boolean isSync = false;

    private DrawerLayout mDrawerLayout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(SharedPreferencesUtil.getDefaultUnit(this)==null)
            SharedPreferencesUtil.setDefaultUnit(this,"Kg");
        bManager = BleManager.getInstance();
        bManager.init(getApplication());
        requestLocationPermission();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.connecting));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(true);
        progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(keyCode== KeyEvent.KEYCODE_BACK && !event.isCanceled()) {
                    if(progressDialog.isShowing()) {
                        //your logic here for back button pressed event
                    }
                    return true;
                }
                return false;
            }
        });

        setTitle("Kitty Doc");

        //Init FragmentTransaction
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_parent, fragmentHome).commitAllowingStateLoss();

        //Init BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new ItemSelectedListener());

        //Init Toolbar
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.menu_icon);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigationView);
        headerView = navigationView.getHeaderView(0);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == R.id.weight_check){
                    //몸무게 액티비티
                    if(listPetInfo == null || listPetInfo.size()==0){
                        Toast.makeText(MainActivity.this, getString(R.string.caution), Toast.LENGTH_SHORT).show();
                        return true;
                    }

                    Intent intent = new Intent(MainActivity.this, WeightScaleActivity.class);
                    startActivity(intent);
                }else if(id == R.id.water_check){
                    //물 액티비티
                }else if(id==R.id.food_check){
                    //밥 액티비티
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });

        //ListView listView = (ListView) navigationView.getMenu().getItem(R.id.battery_list).getActionView();
        //ArrayList<String> l = new ArrayList<>();
        //l.add("123");
        //l.add("456");
        ///listView.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, l));

        Intent intent = getIntent();
        if(intent.getAction().equals("action.login")){
            try{
                JSONObject userInfo = new JSONObject(intent.getStringExtra("userInfo"));
                USER_EMAIL = userInfo.get("UserEmail").toString();
                user = new User(userInfo);

                TextView tv_header = (TextView)headerView.findViewById(R.id.tv_user_name);
                TextView tv_email = (TextView)headerView.findViewById(R.id.tv_user_email);
                tv_header.setText(user.name);
                tv_email.setText(user.userEmail);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }else if(intent.getAction().equals("action.edit")){
            TextView tv_header = (TextView)headerView.findViewById(R.id.tv_user_name);
            TextView tv_email = (TextView)headerView.findViewById(R.id.tv_user_email);
            tv_header.setText(user.name);
            tv_email.setText(user.userEmail);
        }

        BASE_URL = this.getString(R.string.baseUrl);
        retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(BASE_URL).build();
        service = retrofit.create(ServiceApi.class);

        //FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(context);
        //SQLiteDatabase db = dbHelper.getWritableDatabase();
        //dbHelper.onUpgrade(db,1,1);

        createDB();

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Connect other");

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("Get_Pet_Data");

        brFromFragHome = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                listIndex = intent.getIntExtra("index",0);
                connectDevice(intent.getIntExtra("index",0));
            }
        };

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isRunning = intent.getBooleanExtra("isRunning",false);
                //Log.d(TAG, "isRunning : " + isRunning);
                if(isRunning){
                    String syncPetSrn = intent.getStringExtra("petSrn");
                    int intProgress = intent.getIntExtra("progress",0);
                    intProgress--;
                    if(intProgress==-1)intProgress=0;

                    String nm = listPetInfo.get(listIndex).petName;
                    fragmentHome.setStateText(nm+" "+getString(R.string.synchronizing)+ " " + intProgress + "%");

                }
                else{
                    fragmentHome.setStateText(getString(R.string.dataSyncSuccess));
                    getPetData(listIndex);
                }

                // isRunning = 현재 데이터를 읽는 중인지 , true면 읽는중 false면 완료
                // petSrn = 현재 동기화중인 혹은 동기화를 끝낸 반려견
                // progress = 동기화 퍼센트
            }
        };

        brUpdate = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                attemptFind();
            }
        };

        IntentFilter filter = new IntentFilter(ACTION_VIEW_PROGRESS);
        IntentFilter updatefilter = new IntentFilter("update");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver,filter);
        LocalBroadcastManager.getInstance(this).registerReceiver(brUpdate, updatefilter);
        registerReceiver(brFromFragHome,intentFilter);
        registerReceiver(brUpdate,updatefilter);
        attemptFind();
    }

    private void createDB(){
        appDatabase = AppDatabase.getInstance(getApplicationContext());
        calendarDao = appDatabase.calendarDao();
        imageDao = appDatabase.imageDao();
    }

    private void closeDb() throws IOException {
        //appDatabase.close();
    }

    private void getPetData(final int listIndex){
        Date endDate = new Date();
        endDate.setHours(23);
        endDate.setMinutes(59);
        endDate.setSeconds(59);
        Date startDate = new Date();
        startDate.setHours(0);
        startDate.setMinutes(0);
        startDate.setSeconds(0);
        AnalysisData analysisData = new AnalysisData(listPetInfo.get(listIndex).petID, startDate.getTime(), endDate.getTime());
        service.sensorRequestDay(analysisData).enqueue(new Callback<ServerResponse>(){
            LinkedList<PetData> listPetData;
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();

                if(result.getCode() == ServerResponse.ANALYSIS_SUCCESS){
                    try{
                        listPetData = getPetData(result.getMessage());
                    }catch(Exception e){
                        Log.e("NoDATA","!!!NONONO");
                        e.printStackTrace();
                    }
                    if(listPetData.size() !=0){
                        fragmentHome.bindingPetData(listPetData.get(0));
                        fragmentHome.stopRefreshing();
                    }
                    else{
                        fragmentHome.setStateText(listPetInfo.get(listIndex).petName+" 의 데이터가 없습니다.");
                        fragmentHome.stopRefreshing();
                        fragmentHome.defaultPetData();
                    }

                }
                else{
                    //Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t){
                t.printStackTrace();
            }
        });

    }


    private void connectDevice(int index){

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(this, getString(R.string.please_open_blue), Toast.LENGTH_LONG).show();
            //없애야햄
            getPetData(listIndex);
        }
        fragmentHome.defaultPetData();
        if(listPetInfo.isEmpty()){
            Toast.makeText(this, "팻 등록 필요", Toast.LENGTH_SHORT).show();
            return;
        }
        final PetInfo cur = listPetInfo.get(index);

        if(cur.device.equals("NULL")){
            fragmentHome.setStateText(getString(R.string.none_device));
            getPetData(index);
            fragmentHome.defaultPetData();
        }else{
            if(!TextUtils.isEmpty(cur.device) && BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                if (!bManager.isConnected(cur.device)) {



                    first = new BleGattCallback() {
                        @Override
                        public void onStartConnect() {
                            progressDialog.show();
                            fragmentHome.setStateText(getString(R.string.connecting));
                        }
                        @Override
                        public void onConnectFail(BleDevice bleDevice, BleException exception) {
                            progressDialog.dismiss();
                            //연결 실패시 재 시도
                            fragmentHome.setStateText(getString(R.string.conect_fail_msg));

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(R.string.conect_fail_msg);
                            builder.setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            //연결 시도
                                            bManager.connect(cur.device, first);
                                        }
                                    });
                            builder.setNegativeButton(android.R.string.no,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                            builder.setCancelable(true);
                            builder.show();
                            getPetData(listIndex);

                        }
                        @Override
                        public void onConnectSuccess(BleDevice bleDeviceResult, BluetoothGatt gatt, int status) {
                            progressDialog.dismiss();

                            //연결 성공시 ble데이터 가져오기
                            loadDeviceData(cur);
                        }
                        @Override
                        public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                            progressDialog.dismiss();
                            fragmentHome.setStateText(getString(R.string.conect_fail_msg));
                            getPetData(listIndex);
                        }
                    };
                    //연결 시도
                    bManager.connect(cur.device, first);
                } else {//연결되어있다
                    //ble데이터 가져오기
                    loadDeviceData(cur);
                }
            }
        }


    }

    public void loadDeviceData(final PetInfo petInfo){
        fragmentHome.setStateText(getString(R.string.connectSuccess));
        fragmentHome.defaultPetData();
        Log.d(TAG, "loadDeviceData");
        new Thread() {
            @Override
            public void run() {
                super.run();
                final String mac = petInfo.device;
                if (mac == null || mac == "NULL") {
                    Log.i(TAG,"no mac");
                }
                else {
                    BluetoothDevice bluetoothDevice = bManager.getBluetoothAdapter().getRemoteDevice(mac);
                    final BleDevice bleDevice = new BleDevice(bluetoothDevice, 0, null, 0);
                    Calendar c = Calendar.getInstance();
                    //c.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                    TimeZone tz = c.getTimeZone();
                    int time = (int) (c.getTimeInMillis() / 1000);
                    int gmtOffset = (int) (tz.getRawOffset() / 1000);
                    Log.i(TAG, "time: " + time + ", gmt : " + gmtOffset);

                    byte[] op = new byte[9];
                    op[0] = SYSCMD_SET_RTC;

                    ByteBuffer bb1 = ByteBuffer.wrap(new byte[4]);
                    bb1.order(ByteOrder.LITTLE_ENDIAN);
                    bb1.putInt(time);
                    ByteBuffer bb2 = ByteBuffer.wrap(new byte[4]);
                    bb2.order(ByteOrder.LITTLE_ENDIAN);
                    bb2.putInt(gmtOffset);

                    System.arraycopy(bb1.array(), 0, op, 1, 4);
                    System.arraycopy(bb2.array(), 0, op, 5, 4);
                    bManager.write(bleDevice, GENERAL_SERVICE_UUID.toString(), SYSCMD_CHAR_UUID.toString(), op,
                            new BleWriteCallback() {
                                @Override
                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                    Log.i(TAG, "timeSet: 성공");
                                    //버전 가져오기
                                    bManager.read(bleDevice, DEVICE_INFORMATION_SERVICE_UUID.toString(), SW_REVISION_CHAR_UUID.toString(), new BleReadCallback() {
                                        @Override
                                        public void onReadSuccess(byte[] data) {

                                            //bleDevice.setDeviceVersion();
                                            //배터리 정보 가져오기
                                            bManager.read(bleDevice, BATTERY_SERVICE_UUID.toString(), BATTERY_CHAR_UUID.toString(), new BleReadCallback() {
                                                @Override
                                                public void onReadSuccess(byte[] data) {
                                                    Calendar c = Calendar.getInstance();
                                                    String sync = c.toString();
                                                    Log.i("Battery", String.valueOf(data[0]));
                                                    fragmentHome.setBattery(String.valueOf(data[0]), sync+" 기준");
                                                    //binding.tvBattery.setText(data[0] + "%");
                                                    //Log.i(TAG, "battery: " + data[0] + "%, version: " + bleDevice.getDeviceVersion());
                                                    //uuid확인
                                                    bManager.write(bleDevice, GENERAL_SERVICE_UUID.toString(), SYSCMD_CHAR_UUID.toString(), new byte[]{SYSCMD_GET_UUID},
                                                            new BleWriteCallback() {
                                                                @Override
                                                                public void onWriteSuccess(int current, int total, byte[] justWrite) {
                                                                    Log.i(TAG, "getUUID : " + "current : " + current + " total : " + total + " justWrite :" + HexUtil.formatHexString(justWrite));
                                                                    bManager.read(bleDevice, GENERAL_SERVICE_UUID.toString(), SYSCMD_CHAR_UUID.toString(), new BleReadCallback() {
                                                                        @Override
                                                                        public void onReadSuccess(byte[] data) {
                                                                            String uData = HexUtil.formatHexString(data);
                                                                            Log.i(TAG, "getUUID : " + "data : " + uData);
                                                                            //setUUID
                                                                            if(uData.contains("00000000000000000")){
                                                                                Log.i(TAG, "NO DATA");
                                                                                PetDeviceManagementViewModel.setUUID(bleDevice, context);
                                                                            }
                                                                            else{ //서비스 시작
                                                                                fragmentHome.setStateText(getString(R.string.synchronizing));
                                                                                isSync = true;
                                                                                Intent cmdService = new Intent(context, CommandService.class);
                                                                                cmdService.setAction(ACTION_SET_CMD);
                                                                                cmdService.putExtra("Command", new Command(bleDevice.getMac(), petInfo.ownerID, petInfo.petID, petInfo.petName));
                                                                                Log.d(TAG, "Start Command Service with userId = " + petInfo.ownerID + "  and petSrn" + petInfo.petID);
                                                                                context.startService(cmdService);
                                                                            }
                                                                        }
                                                                        @Override
                                                                        public void onReadFailure(BleException exception) {
                                                                            //getDataProgress.dismiss();
                                                                        }
                                                                    });
                                                                }

                                                                @Override
                                                                public void onWriteFailure(BleException exception) {
                                                                    //getDataProgress.dismiss();
                                                                }
                                                            });
                                                }
                                                @Override
                                                public void onReadFailure(BleException exception) {
                                                    Log.i(TAG, "battery read Fail");
                                                    //getDataProgress.dismiss();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onReadFailure(BleException exception) {
                                            Log.d(TAG, "version read fail");
                                            //getDataProgress.dismiss();
                                        }
                                    });
                                }

                                @Override
                                public void onWriteFailure(BleException exception) {
                                    Log.i(TAG, "timeSet: 실패");
                                    //getDataProgress.dismiss();
                                }
                            });
                }
            }
        }.start();
    }


    class ItemSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem){
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            switch(menuItem.getItemId()){
                case R.id.homeItem:
                    Log.i("FragmentChange", "success");
                    transaction.replace(R.id.fragment_parent, fragmentHome).commitNowAllowingStateLoss();
                    //fragmentHome.setList(listPetInfo, listIndex);
                    break;
                case R.id.calendarItem:
                    transaction.replace(R.id.fragment_parent, fragmentCalendar).commitNowAllowingStateLoss();
                    break;
                case R.id.analysisItem:
                    transaction.replace(R.id.fragment_parent, fragmentAnalysis).commit();
                    break;
                case R.id.settingItem:
                    transaction.replace(R.id.fragment_parent, fragmentSetting).commitNowAllowingStateLoss();
                    break;
            }
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);


        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) item.getActionView();

        if(listPetInfo!=null){
            ArrayList<String> petName = new ArrayList<>();
            for(PetInfo pet : listPetInfo){
                petName.add(pet.petName);
            }
            Log.i("AddS","adddd");
            spinner.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item, petName));
            spinner.setOnItemSelectedListener(spinnerListener);
            spinner.setSelection(listIndex);

        }else{
            ArrayList<String> petName = new ArrayList<>();
            spinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,petName));
        }

        return true;
    }

    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener(){

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            //Toast.makeText(this, listPetInfo.get(i).petName+"선택",Toast.LENGTH_SHORT).show();
            listIndex = i;
            connectDevice(i);
            if(fragmentAnalysis!=null)
                fragmentAnalysis.getPetData();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    };

    //Toolbar의 뒤로가기 버튼이 눌리는 경우
    @Override
    public void onBackPressed(){
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        fragmentHome.setImg();

        if(requestCode == 100) {
            if(resultCode==1){
                Log.i("Good","send is good");
                try {
                    JSONObject object = new JSONObject(data.getStringExtra("json"));
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("수신데이터");
                    builder.setMessage(
                            "s_tick: "+object.get("s_tick").toString()+"\n"
                                    +"e_tick: "+object.get("e_tick").toString()+"\n"
                                    +"steps: "+object.get("steps").toString()+"\n"
                                    +"t_lux: "+object.get("t_lux").toString()+"\n"
                                    +"avg_lux: "+object.get("avg_lux").toString()+"\n"
                                    +"avg_k: "+object.get("avg_k").toString()+"\n"
                                    +"vector_x: "+object.get("vector_x").toString()+"\n"
                                    +"vector_y: "+object.get("vector_y").toString()+"\n"
                                    +"vector_z: "+object.get("vector_z").toString()+"\n"
                    );
                    builder.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestCode==200){
            if(resultCode==RESULT_OK){
                user = (User) data.getSerializableExtra("userInfo");
                USER_EMAIL = user.userEmail;
                Toast.makeText(this,user.name+"님 환영합니다",Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode==300){
            if(resultCode == 10){
                Toast.makeText(this, "팻 등록을 성공하였습니다.",Toast.LENGTH_SHORT).show();

            }else if(resultCode==15){
                Toast.makeText(this, "팻 수정을 성공하였습니다.",Toast.LENGTH_SHORT).show();

            }else if(resultCode==20){
                Toast.makeText(this, "팻 삭제를 성공하였습니다.",Toast.LENGTH_SHORT).show();
            }
            attemptFind();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            }
            case R.id.action_bluetooth:{

            }
          }
          return super.onOptionsItemSelected(item);
    }

    private void requestEnableBLE(){ //블루투스 연결하게 설정
        Intent ble_enable_intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(ble_enable_intent, REQUEST_ENABLE_BT);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestLocationPermission(){//위치 정보 제공하라고 설정
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
    }



    // 서버에서 PetInfo 배열의 메시지를 받으면 파싱하는 것 (주의! : PetInfo 생성자에서 "name"들이 잘 맞는지 확인 필요!!!)
    private LinkedList<PetInfo> getPetInfo(String message) throws JSONException, ParseException {
        LinkedList<PetInfo> l = new LinkedList<PetInfo>();
        JSONArray ja = new JSONArray(message);
        for(int i=0; i<ja.length(); i++){
            JSONObject object = (JSONObject) ja.get(i);
            l.add(new PetInfo(object,this));
        }

        //네트워크로 JsonArray받아와서 각각 JsonObject로 변환 후 l에 add하기
        return l;
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

    private void attemptFind(){
        Log.i("~~~~",String.valueOf(user.userId));
        FindData_Pet data = new FindData_Pet(user.userId);
        service.petFind(data).enqueue(new Callback<ServerResponse>(){
            @Override
            public void onResponse(Call<ServerResponse> call, Response<ServerResponse> response){
                ServerResponse result = response.body();

                if(result.getCode() == ServerResponse.PET_FOUND){
                    try{
                        listPetInfo = getPetInfo(result.getMessage());
                        for(int i=0;i<listPetInfo.size();i++){
                            listPetInfo.get(i).showAll();
                        }
                        fragmentHome.setList(listPetInfo,listIndex);
                        fragmentHome.setImg();
                    }catch(Exception e){
                        Log.i("~~~Error","not found");
                        e.printStackTrace();
                    }

                    if(listPetInfo==null){
                        Log.i("~~~Error","not found2");
                        fragmentHome.setStateText(getString(R.string.request_pet));
                        fragmentHome.setPetListEmpty();
                        return;
                    }

                    if(listIndex >= listPetInfo.size())
                        listIndex= listPetInfo.size()-1;
                    invalidateOptionsMenu();

                    if(listPetInfo.size()!=0){

                    }else{
                        //팻 등록 레이아웃 요청!
                        Log.i("~~~Error","not found3");
                        fragmentHome.setStateText(getString(R.string.request_pet));
                        fragmentHome.setPetListEmpty();
                    }
                }
                else{
                    listPetInfo = null;
                    listIndex = 0;
                    invalidateOptionsMenu();
                    Log.i("~~~Error","not found4");
                    fragmentHome.setStateText(getString(R.string.request_pet));
                    fragmentHome.setPetListEmpty();
                    //Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServerResponse> call, Throwable t){
                t.printStackTrace();
            }
        });
    }

    @Override
    protected void onDestroy() {
        FragmentAnalysisViewModel.timeIndex = 0;
        FragmentAnalysisViewModel.dataIndex = 0;
        USER_EMAIL = null;
        user = null;
        listPetInfo = null;
        Log.i("!!!DESTROY", "DES");
        super.onDestroy();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(listPetInfo != null)
            fragmentHome.setList(listPetInfo,listIndex);
    }
}