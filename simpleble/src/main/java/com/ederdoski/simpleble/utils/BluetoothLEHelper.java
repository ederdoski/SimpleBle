package com.ederdoski.simpleble.utils;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.ederdoski.simpleble.interfaces.BleCallback;
import com.ederdoski.simpleble.models.BluetoothLE;

import java.util.ArrayList;
import java.util.UUID;

public class BluetoothLEHelper {

    private Activity act;

    private ArrayList<BluetoothLE> aDevices     = new ArrayList<>();

    private BleCallback bleCallback;
    private BluetoothGatt    mBluetoothGatt;
    private BluetoothAdapter mBluetoothAdapter;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED    = 1;
    private int              mConnectionState   = STATE_DISCONNECTED;

    private static long SCAN_PERIOD             = 10000;
    private static boolean mScanning            = false;
    private static String FILTER_SERVICE        = "";

    public BluetoothLEHelper(Activity _act){
        if(Functions.isBleSupported(_act)) {
            act = _act;
            BluetoothManager bluetoothManager = (BluetoothManager) act.getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
            mBluetoothAdapter.enable();
        }
    }

    public void scanLeDevice(boolean enable) {
        Handler mHandler = new Handler();

        if (enable) {
            mScanning = true;

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            if(!FILTER_SERVICE.equals("")) {
                UUID[] filter  = new UUID[1];
                filter [0]     = UUID.fromString(FILTER_SERVICE);
                mBluetoothAdapter.startLeScan(filter, mLeScanCallback);
            }else{
                mBluetoothAdapter.startLeScan(mLeScanCallback);
            }

        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            act.runOnUiThread(new Runnable() {
                public void run() {
                    if(aDevices.size() > 0) {

                        boolean isNewItem = true;

                        for (int i = 0; i < aDevices.size(); i++) {
                            if (aDevices.get(i).getMacAddress().equals(device.getAddress())) {
                                isNewItem = false;
                            }
                        }

                        if(isNewItem) {
                            aDevices.add(new BluetoothLE(device.getName(), device.getAddress(), rssi, device));
                        }

                    }else{
                        aDevices.add(new BluetoothLE(device.getName(), device.getAddress(), rssi, device));
                    }
                }
            });
        }
    };

    public ArrayList<BluetoothLE> getListDevices(){
        return aDevices;
    }

    public void connect(BluetoothDevice device, BleCallback _bleCallback){
        if (mBluetoothGatt == null && !isConnected()) {
            bleCallback = _bleCallback;
            mBluetoothGatt = device.connectGatt(act, false, mGattCallback);
        }
    }

    public void disconnect(){
        if (mBluetoothGatt != null && isConnected()) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
    }

    public boolean isReadyForScan(){

        return Permissions.checkPermisionStatus(act, Manifest.permission.BLUETOOTH)
                && Permissions.checkPermisionStatus(act, Manifest.permission.BLUETOOTH_ADMIN)
                && Permissions.checkPermisionStatus(act, Manifest.permission.ACCESS_COARSE_LOCATION) && Functions.getStatusGps(act);
    }

    public void write(String service, String characteristic, byte[] aBytes){

        BluetoothGattCharacteristic mBluetoothGattCharacteristic;

        mBluetoothGattCharacteristic = mBluetoothGatt.getService(UUID.fromString(service)).getCharacteristic(UUID.fromString(characteristic));
        mBluetoothGattCharacteristic.setValue(aBytes);

        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
    }

    public void write(String service, String characteristic, String aData){

        BluetoothGattCharacteristic mBluetoothGattCharacteristic;

        mBluetoothGattCharacteristic = mBluetoothGatt.getService(UUID.fromString(service)).getCharacteristic(UUID.fromString(characteristic));
        mBluetoothGattCharacteristic.setValue(aData);

        mBluetoothGatt.writeCharacteristic(mBluetoothGattCharacteristic);
    }

    public void read(String service, String characteristic){
        mBluetoothGatt.readCharacteristic(mBluetoothGatt.getService(UUID.fromString(service)).getCharacteristic(UUID.fromString(characteristic)));

    }

    private final BluetoothGattCallback mGattCallback;
    {
        mGattCallback = new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    Log.i("BluetoothLEHelper", "Attempting to start service discovery: " + mBluetoothGatt.discoverServices());
                    mConnectionState = STATE_CONNECTED;
                }

                if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    mConnectionState = STATE_DISCONNECTED;
                }

                bleCallback.onBleConnectionStateChange(gatt, status, newState);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                bleCallback.onBleServiceDiscovered(gatt, status);
            }

            @Override
            public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                super.onCharacteristicWrite(gatt, characteristic, status);
                bleCallback.onBleWrite(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                bleCallback.onBleRead(gatt, characteristic, status);
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                bleCallback.onBleCharacteristicChange(gatt, characteristic);
            }

        };
    }

    public boolean isConnected(){
        return mConnectionState == STATE_CONNECTED;
    }

    public boolean isScanning(){
        return mScanning;
    }

    public void setScanPeriod(int scanPeriod){
        SCAN_PERIOD = scanPeriod;
    }

    public long getScanPeriod(){
        return SCAN_PERIOD;
    }

    public void setFilterService(String filterService){
        FILTER_SERVICE = filterService;
    }

    public BluetoothGattCallback getGatt(){
        return mGattCallback;
    }

}