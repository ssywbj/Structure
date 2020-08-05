package com.suheng.structure.bluetooth.connect;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.List;

public class BLEHelper {
    //private static final String TAG = BluetoothConnectHelper.class.getSimpleName();
    private static final String TAG = BLEHelper.class.getSimpleName();
    private Context mContext;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;

    public BLEHelper(Context context) {
        mContext = context;
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            Log.d(TAG, "onLeScan, device: " + device + ", name: " + device.getName() + ", rssi: " + rssi);
            if ("80:1D:00:FA:D1:E0".equals(device.getAddress())) {
                destroy();
            }
        }
    };

    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            BluetoothDevice bluetoothDevice = result.getDevice();
            Log.d(TAG, "onScanResult, callbackType: " + callbackType
                    + ", bluetooth device: " + bluetoothDevice + ", name: " + bluetoothDevice.getName());
            if ("80:1D:00:FA:D1:E0".equals(bluetoothDevice.getAddress())) {
                destroy();
                bluetoothDevice.connectGatt(mContext, true, mBluetoothGattCallback);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d(TAG, "onBatchScanResults, results: " + results.size());
            for (ScanResult scanResult : results) {
                Log.d(TAG, "onBatchScanResults, scanResult: " + scanResult
                        + ", bluetooth device: " + scanResult.getDevice());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.w(TAG, "onScanFailed, errorCode: " + errorCode);
        }
    };

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            String address = gatt.getDevice().getAddress();
            Log.d(TAG, "onConnectionStateChange, status: " + status + ", newState: " + newState
                    + ", address: " + address);
            if (status == BluetoothGatt.GATT_SUCCESS) {///操作成功的情况下
                if (newState == BluetoothProfile.STATE_CONNECTED) {//连上设备
                    boolean connect = gatt.connect();
                    Log.d(TAG, "gatt, connect: " + connect);
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {//设备断开
                    gatt.close();
                    Log.d(TAG, "gatt, close");
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.d(TAG, "onServicesDiscovered, status: " + status + ", gatt: " + gatt);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //发现新的设备,订阅Characteristic的值是否有改变,如果有改变则做出相应的处理
                //enableNotification(gatt, ServerUUID, 订阅的Characteristic的UUID) 视情况是否要实时监听某一种数据而决定是否订阅
                /*BluetoothGattService gattService = getService(ServerUUID, gatt);
                BluetoothGattCharacteristic readChar = getCharacteristic(CharacteristicUUID, gattService);
                gatt.setCharacteristicNotification(readChar, true);//返回一个boolean表示是否订阅成功*/
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.d(TAG, "onServicesDiscovered, status: " + status + ", gatt: " + gatt + ", characteristic :" + characteristic);
            // 主动读取 Characteristic  里面的数据 ,来到这里则可以解析需要的数据了
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.d(TAG, "onServicesDiscovered, gatt: " + gatt + ", characteristic :" + characteristic);
            //当Characteristic里面的数据发生改变的时候 走这个回调方法 也就是onServicesDiscovered在订阅Characteristic的监听
            // 比如一些需要实时知道的数据,气温,心跳等等
        }
    };

    public void scan() {
        if (mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            BluetoothManager bluetoothManager = (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null) {
                mBluetoothAdapter = bluetoothManager.getAdapter();
                //mBluetoothAdapter.startLeScan(mLeScanCallback);

                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                mBluetoothLeScanner.startScan(mScanCallback);
            }
        } else {
            Log.w(TAG, "this device isn't support bluetooth le!");
        }
    }

    public boolean connect(String address) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        BluetoothGatt gatt = device.connectGatt(mContext, false, mBluetoothGattCallback);
        return (gatt == null);
    }

    public void destroy() {
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        if (mBluetoothLeScanner != null) {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

}
