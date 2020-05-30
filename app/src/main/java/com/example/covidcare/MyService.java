package com.example.covidcare;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import table.Device;


public class MyService extends Service {

    private static final String TAG = "MyService";

    private BluetoothAdapter mBluetoothAdapter;
    private final IBinder mBinder = new MyBinder();
    //    private Handler mHandler;
//    private ArrayAdapter<String> listAdapter;
    String status, bluetoothAdapterStatus;
    private static Repository deviceRepository;
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    //   System.out.println(dtf.format(now));
    MediaPlayer player;

    public void setDeviceRepository(Repository deviceRepo) {
        if (deviceRepository == null)
            deviceRepository = deviceRepo;
    }


    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        mHandler = new Handler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    public void checkBluetoothState() {
        if (mBluetoothAdapter == null) {
            bluetoothAdapterStatus = "not supported";
        } else {
            if (mBluetoothAdapter.isEnabled()) {
                if (mBluetoothAdapter.isDiscovering()) {
                    bluetoothAdapterStatus = "discovering process ...";
                } else {
                    bluetoothAdapterStatus = "enabled";
                }
            } else {
                bluetoothAdapterStatus = "need enable";
            }
        }
//        return bluetoothAdapterStatus;
    }


    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // device : represent the nearest device ...
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // adding the devices into array list of strings
//                listAdapter.add(device.getName()  +"\n" + device.getAddress());
                LocalDateTime now = LocalDateTime.now();

                mBluetoothAdapter.getAddress();
                Device dev = new Device(device.getName(), device.getAddress(), dtf.format(now).toString());
                deviceRepository.deviceInsert(dev);

                Log.d(TAG, mBluetoothAdapter.getAddress() + "\t" + mBluetoothAdapter.getName() + "\t" + device.getName() + "+" + dev.getMacAddress() + dev.getTime() + dev.getTime() + "zzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzinsersion");

                status = "found a device";
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                status = "scanning devices ...";
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                status = "scanning..";
            }
        }
    };


    public boolean getIsBluetoothDiscovering() {
        return mBluetoothAdapter.isDiscovering();
    }

    public boolean getIsBluetoothEnabled() {
        return mBluetoothAdapter.isEnabled();
    }

    public String getStatus() {
        return status;
    }

//    public ArrayAdapter<String> getListAdapter(){
//        return listAdapter;
//    }

    public String getBluetoothAdapterStatus() {
        return bluetoothAdapterStatus;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //getting systems default ringtone
        player = MediaPlayer.create(this,
                Settings.System.DEFAULT_ALARM_ALERT_URI);
        //setting loop play to true
        //this will make the ringtone continuously playing
        player.setLooping(true);

        //staring the player
        player.start();

        //we have some options for service
        //start sticky means service will be explicity started and stopped
        startDiscovering();
        return START_STICKY;

    }

    public void startDiscovering() {
//        listAdapter.clear();

        mBluetoothAdapter.startDiscovery();
    }

    public void onPause() {
        unregisterReceiver(devicesFoundReceiver);
    }


    public void onResume() {
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));


    }


    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.d(TAG, "onTaskRemoved: called.");
        onPause();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: called.");
        onPause();
    }

}
