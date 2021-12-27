package com.jmsmart.whosecat.view.com.viewModel;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleReadCallback;
import com.clj.fastble.callback.BleWriteCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;
import com.jmsmart.whosecat.R;
import com.jmsmart.whosecat.adapter.PetDeviceAdapter;
import com.jmsmart.whosecat.data.commondata.PetInfo;
import com.jmsmart.whosecat.databinding.ActivityPetDeviceManagementBinding;
import com.jmsmart.whosecat.view.base.CommonModel;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;

public class PetDeviceManagementViewModel implements CommonModel {
    private Activity activity;
    private ActivityPetDeviceManagementBinding binding;
    private ArrayList<PetInfo> dataList;
    private static BleManager bleManager = BleManager.getInstance();
    private ProgressDialog progressDialog;

    private static final UUID GENERAL_SERVICE_UUID = UUID.fromString("0000fffe-0000-1000-8000-00805f9b34fb");
    private static final UUID SYSCMD_CHAR_UUID = UUID.fromString("0000ffff-0000-1000-8000-00805f9b34fb");

    private static final byte SYSCMD_SET_RTC = (byte)0x06;
    private static final byte SYSCMD_FACTORY_RESET = (byte)0xAA;
    private static final byte SYSCMD_GET_UUID = (byte)0x0B;
    private static final byte SYSCMD_SET_UUID = (byte)0x0A;

    private static final String TAG = "DeviceManagement";

    public PetDeviceManagementViewModel(Activity activity, ActivityPetDeviceManagementBinding binding, ArrayList<PetInfo> dataList) {
        this.activity = activity;
        this.binding = binding;
        this.dataList = dataList;

        progressDialog = new ProgressDialog(activity);
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

    }

    @Override
    public void onCreate() {

        final PetDeviceAdapter adapter = new PetDeviceAdapter(activity, dataList);
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "초기화" item
                SwipeMenuItem resetItem = new SwipeMenuItem(activity.getApplicationContext());
                // set item background
                resetItem.setBackground(new ColorDrawable(Color.rgb(0x5e, 0xd4, 0x9d)));
                // set item width
                resetItem.setWidth(((int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        60,
                        activity.getResources().getDisplayMetrics()
                )));
                // set a icon
                resetItem.setIcon(R.drawable.ok_clicked);
                resetItem.setTitle(R.string.factory_reset);
                resetItem.setTitleSize((int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 4, activity.getResources().getDisplayMetrics()));
                resetItem.setTitleColor(Color.parseColor("#ffffff"));
                //resetItem.setIcon(R.drawable.ic_sync_white);
                // add to menu
                menu.addMenuItem(resetItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(activity.getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xfd,0x71, 0x5e)));
                // set item width
                deleteItem.setWidth(((int)TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        60,
                        activity.getResources().getDisplayMetrics()
                )));
                // set a icon
                deleteItem.setIcon(R.drawable.ok_clicked);
                deleteItem.setTitle(R.string.change_device);
                deleteItem.setTitleSize((int) TypedValue.applyDimension( TypedValue.COMPLEX_UNIT_DIP, 4, activity.getResources().getDisplayMetrics()));
                deleteItem.setTitleColor(Color.parseColor("#ffffff"));
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        binding.deviceInfo.setAdapter(adapter);
        binding.deviceInfo.setMenuCreator(creator);
        binding.deviceInfo.setSwipeDirection(SwipeMenuListView.DIRECTION_RIGHT);
        binding.deviceInfo.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT);
        binding.deviceInfo.setDividerHeight(0);

        binding.deviceInfo.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index){
                    //sync
                    case 0:

                        bleManager.init(activity.getApplication());
                        BluetoothDevice bluetoothDevice = BleManager.getInstance().getBluetoothAdapter().getRemoteDevice(dataList.get(index).device);
                        if(bleManager.isConnected(dataList.get(index).device))
                            factoryReset(new BleDevice(bluetoothDevice, 0, null, 0), position);
                        else{
                            Toast.makeText(activity, R.string.must_connect,Toast.LENGTH_LONG).show();
                        }

                        break;
                    //delete
                    case 1:
                        if(TextUtils.isEmpty(dataList.get(index).device)){
                            //Toast.makeText(activity, "등록된 기기가 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            //showDeleteDialog(position);
                        }
                        /*clAdapter.removeItem(position);
                        clAdapter.notifyDataSetChanged();*/
                        break;
                }
                return true;
            }
        });
        binding.deviceInfo.setAdapter(adapter);
    }

    @Override
    public void onResume() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    public void factoryReset(final BleDevice bleDevice, final int position){
        progressDialog.setMessage(activity.getString(R.string.factory_reset));
        progressDialog.show();

        bleManager.write(bleDevice, GENERAL_SERVICE_UUID.toString(), SYSCMD_CHAR_UUID.toString(),  new byte[] {SYSCMD_FACTORY_RESET},
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.i(TAG, "factoryReset: 성공");
                        new android.os.Handler().postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                BluetoothDevice bluetoothDevice = BleManager.getInstance().getBluetoothAdapter().getRemoteDevice(dataList.get(position).device);
                                bleManager.disconnect(new BleDevice(bluetoothDevice, 0, null, 0));
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        //clAdapter.getItem(position).setConnected(false);
                                        //clAdapter.notifyDataSetChanged();
                                    }
                                });
                                connectDevice(dataList.get(position).device, position, true);
                            }
                        }, 3000);// 3초 정도 딜레이를 준 후 시작
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        progressDialog.dismiss();
                        Log.i(TAG, "factoryReset: 실패");
                        Toast.makeText(activity, R.string.fail, Toast.LENGTH_SHORT).show();
                    }
                });



    }
    public void setTimeAndZone(final BleDevice bleDevice, final boolean isFactory) {
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
        bleManager.write(bleDevice, GENERAL_SERVICE_UUID.toString(), SYSCMD_CHAR_UUID.toString(), op,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.i(TAG, "timeSet: 성공");
                        progressDialog.dismiss();
                        if(isFactory)
                            setUUID(bleDevice, activity);
                        else
                            getUUID(bleDevice, activity);
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {
                        Log.i(TAG, "timeSet: 실패");
                        progressDialog.dismiss();
                    }
                });

    }
    public void connectDevice(final String mac, final int position, final boolean isFactory) {
        progressDialog.setMessage(activity.getString(R.string.connecting));
        bleManager.connect(mac, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                progressDialog.dismiss();
                Toast.makeText(activity, activity.getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
                Log.i(TAG, exception.toString());
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                progressDialog.dismiss();
                Toast.makeText(activity, activity.getString(R.string.connect), Toast.LENGTH_LONG).show();
                //clAdapter.getItem(position).setConnected(true);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //clAdapter.notifyDataSetChanged();
                    }
                });
                setTimeAndZone(bleDevice, isFactory);
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice device, BluetoothGatt gatt, int status) {

            }
        });
    }

    public static void getUUID(final BleDevice bleDevice, final Context c) {
        bleManager.write(bleDevice, GENERAL_SERVICE_UUID.toString(), SYSCMD_CHAR_UUID.toString(), new byte[]{SYSCMD_GET_UUID},
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.i(TAG, "getUUID : " + "current : " + current + " total : " + total + " justWrite :" + HexUtil.formatHexString(justWrite));
                        bleManager.read(bleDevice, GENERAL_SERVICE_UUID.toString(), SYSCMD_CHAR_UUID.toString(), new BleReadCallback() {
                            @Override
                            public void onReadSuccess(byte[] data) {
                                String uData = HexUtil.formatHexString(data);
                                Log.i(TAG, "getUUID : " + "data : " + uData);
                                if(uData.contains("00000000000000000")){
                                    setUUID(bleDevice, c);
                                }
                            }
                            @Override
                            public void onReadFailure(BleException exception) {

                            }
                        });
                    }

                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
    }
    public static void setUUID(final BleDevice bleDevice, Context c) {
        String androidId = Settings.Secure.getString(c.getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID uuid = new UUID(androidId.hashCode(), androidId.hashCode());
        Log.i(TAG, "setUUID "+ uuid.toString());
        byte[] op = new byte[17];
        // uuid to bytes
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        // must be little-endian
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        op[0] = SYSCMD_SET_UUID;
        System.arraycopy(bb.array(), 0, op, 1, 16);
        bleManager.write(bleDevice, GENERAL_SERVICE_UUID.toString(), SYSCMD_CHAR_UUID.toString(), op,
                new BleWriteCallback() {
                    @Override
                    public void onWriteSuccess(int current, int total, byte[] justWrite) {
                        Log.i(TAG, "setUUID : " + "current : " + current + " total : " + total + " justWrite :" + HexUtil.formatHexString(justWrite));
                    }
                    @Override
                    public void onWriteFailure(BleException exception) {

                    }
                });
    }


}
