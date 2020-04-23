package com.suheng.structure.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.suheng.structure.bluetooth.ble.SampleGattAttributes;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public abstract class BluetoothLECommunicator implements BleCallback {


    public static final String TAG = "LJB/BLEComm";

    /*public static final UUID UUID_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_NOTIFY = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    //public static final UUID UUID_READ = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_WRITE = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public static final UUID UUID_NOTIFY_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");*/

    public static final UUID UUID_SERVICE = UUID.fromString(SampleGattAttributes.key);
    public static final UUID UUID_NOTIFY = UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    public static final UUID UUID_WRITE = UUID.fromString(SampleGattAttributes.key2);
    public static final UUID UUID_NOTIFY_DESCRIPTOR = UUID.fromString(SampleGattAttributes.key1);


    public static final byte HEATING_TEMP_LEVEL_INVALID = 0x7F;
    public static final byte HEATING_PRESET_HOURS_INVALID = 0x7F;
    public static final short HEATING_MINUTES_LEFT_INVALID = 0x7F7F;


    public static final byte BATTERY_POWER_LEVEL_MIN = 1;
    public static final byte BATTERY_POWER_LEVEL_MAX = 5;
    public static final byte HEATING_TEMP_LEVEL_MIN = 1;
    public static final byte HEATING_TEMP_LEVEL_MAX = 10;
    public static final byte HEATING_TIMING_LEVEL_MIN = 1;
    public static final byte HEATING_TIMING_LEVEL_MAX = 6;
    public static final short HEATING_MINUTES_LEFT_MIN = 0;
    public static final short HEATING_MINUTES_LEFT_MAX = 360;


    public static final byte COMMAND_QUERY = (byte) 0x01;
    public static final byte COMMAND_HEAT = (byte) 0x02;
    public static final byte COMMAND_TERMINATE_HEATING = (byte) 0x03;
    public static final byte EVENT_QUERY_RESULT = (byte) 0x81;
    public static final byte EVENT_HEAT_ECHO = (byte) 0x82;
    public static final byte EVENT_PERIODIC_REPORT = (byte) 0x83;
    public static final byte EVENT_TERMINATE_HEATING_ECHO = (byte) 0x93;
    public static final byte EVENT_SENDING_COMMAND_TIMEOUT = (byte) 0x10;
    private BluetoothGattServer mBleGattServer;
    private BluetoothDevice mCurDevice;
    private WriteBufferRunnable mWriteRunnable = null;

    private boolean mShouldStopSendingCommand = true;
    private int mSendingCommandCount = 0;


    public enum BluetoothBLEConnectionState {
        STATE_DISCONNECTED,
        STATE_CONNECTED,
        STATE_SERVICE_DISCOVERED,

    }

    private Map<String, BluetoothGattService> mGattServicesMap = new ConcurrentHashMap<>();
    // Member fields
    private ScheduledExecutorService mConnectExecutor = Executors.newScheduledThreadPool(3);
    private ScheduledExecutorService mFailExecutor = Executors.newScheduledThreadPool(1);
    private final BluetoothAdapter mAdapter;
    private final BluetoothManager mBleManager;
    private BluetoothBLEConnectionState mState;

    private ConnectRunnable mConnectRunnable;
    private BluetoothGatt mBluetoothGatt;
    private Context mContext;


    public BluetoothLECommunicator(Context context) {
        mContext = context;
        mState = BluetoothBLEConnectionState.STATE_DISCONNECTED;
        mBleManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        mBleGattServer = mBleManager.openGattServer(context, new ServiceServerController());
        mAdapter = mBleManager.getAdapter();

    }


    public boolean isConnected() {
        return mState == BluetoothBLEConnectionState.STATE_CONNECTED;
    }


    public synchronized void connect(BluetoothDevice device) {
        Log.w(TAG, "connect to: " + device + ", mState: " + mState);

        // Start the thread to connect with the given device
        if (mState != BluetoothBLEConnectionState.STATE_CONNECTED) {
            mConnectRunnable = new ConnectRunnable(device);
            mConnectExecutor.schedule(mConnectRunnable, 200, TimeUnit.MILLISECONDS);
        }


    }


    private class ConnectRunnable implements Runnable {


        public ConnectRunnable(BluetoothDevice device) {
            Log.w(TAG, "Enter connect thread");
            mCurDevice = device;
        }

        @Override
        public void run() {

            if (mBluetoothGatt != null) {
                mBluetoothGatt.close();
                mBluetoothGatt = null;
            }
            // Always cancel discovery because it will slow down a connection
            if (mAdapter != null) {
                mAdapter.stopLeScan(new BluetoothAdapter.LeScanCallback() {
                    @Override
                    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {

                    }
                });
            }

            //If the returned value gatt is null, that means remote device occurs exception,
            //but if the gatt is not null, that does not mean a successful connection, which depends on the param callback.

            mBluetoothGatt = mCurDevice.connectGatt(mContext, false, new BLECommCallback());


            if (mBluetoothGatt != null) {
                BluetoothDevice device = mBluetoothGatt.getDevice();
                if (mCurDevice.getAddress().equalsIgnoreCase(device.getAddress())) {
                    mBluetoothGatt.connect();
                    Log.w(TAG, "mBluetoothGatt.connect(): ");
                }
            }
        }
    }


    /**
     * disconnect the connection
     */
    public synchronized void disconnect() {
        Log.w(TAG, "disconnect");

        if (mBleGattServer != null) {
            mBleGattServer.cancelConnection(mCurDevice);
        }
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt = null;
        }
    }

    /**
     *trigger onCharacteristicRead callback
     */

//    private void readBuffer() {
//        if (mState == STATE_CONNECTED) {
//            BluetoothGattCharacteristic readCharacteristic = getCharacteristicByUuid(UUID_SERVICE, UUID_READ);
//            if (readCharacteristic == null) {
//                Log.e(TAG, "Error, no suitable readCharacteristic found.");
//                return;
//            }
//            mBluetoothGatt.readCharacteristic(readCharacteristic);
//        }
//    }


    /**
     * Assembly flag codes, command byte, command content and checksum to a whole command.
     * command format: --start codes[2]--command byte[1]--command content[variable length]--checksum[1]--end codes[2]---
     * command format: 0xFF,0xAA,comByte, bytes, checkSum, 0xAA,0xFF.
     *
     * @param comByte
     * @param bytes
     * @param length
     * @return
     */

    byte[] assemblyCommand(byte comByte, byte[] bytes, int length) {
        int total = length + 6;
        byte[] command = new byte[total];
        command[0] = (byte) 0xFF;
        command[1] = (byte) 0xAA;
        command[2] = comByte;

        byte checkSum = comByte;
        for (int j = 0, i = 3; j < length; i++, j++) {
            command[i] = bytes[j];
            checkSum += bytes[j];
        }

        command[total - 3] = checkSum;
        command[total - 2] = (byte) 0xAA;
        command[total - 1] = (byte) 0xFF;
        Log.e(TAG, "assemblyCommand, checkSum = " + checkSum);

        return command;
    }

    public void prepareWriteCommand(byte comByte, String string) {
        byte[] commandBytes = null;
        if (comByte == COMMAND_QUERY) {
            Log.e(TAG, "cmdByte COMMAND_QUERY");
            commandBytes = assemblyCommand(COMMAND_QUERY, new byte[0], 0);

        } else if (comByte == COMMAND_HEAT) {
            if (TextUtils.isEmpty(string)) {
                Log.e(TAG, "empty command error");
                return;
            }
            commandBytes = assemblyCommand(COMMAND_HEAT, string.getBytes(), string.getBytes().length);
        } else if (comByte == COMMAND_TERMINATE_HEATING) {
            Log.e(TAG, "cmdByte COMMAND_TERMINATE_HEATING");
            commandBytes = assemblyCommand(COMMAND_TERMINATE_HEATING, new byte[0], 0);
        }

        mShouldStopSendingCommand = false;
        mSendingCommandCount = 0;
        if (mWriteRunnable == null) {
            mWriteRunnable = new WriteBufferRunnable(commandBytes);
            mConnectExecutor.schedule(mWriteRunnable, 200, TimeUnit.MILLISECONDS);
        }

    }

    String bytes2HexString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            byte c = bytes[i];
            builder.append(String.format("0x%x ", c));

        }

        return builder.toString();
    }

    /**
     * write thread
     */
    private class WriteBufferRunnable implements Runnable {

        byte[] command = null;

        public WriteBufferRunnable(byte[] commandValue) {
            command = commandValue;
        }

        @Override
        public void run() {
            while (!mShouldStopSendingCommand && mSendingCommandCount < 5) {
                mSendingCommandCount++;
                Log.w(TAG, "mSendingCommandCount = " + mSendingCommandCount);
                writeBuffer(command);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (mSendingCommandCount >= 5) {
                onDeviceEchoCommandValid(EVENT_SENDING_COMMAND_TIMEOUT);
//                reportEventMessageToUpper(mCurDevice, "Count of Sending Command more than 5");
                Log.e(TAG, "sending command timed out.");

            }

            mWriteRunnable = null;
        }
    }

    /**
     * write bytes to ble channel
     *
     * @param valueBytes
     */
    private void writeBuffer(byte[] valueBytes) {
        if (mState == BluetoothBLEConnectionState.STATE_CONNECTED) {
            BluetoothGattCharacteristic writeCharacteristic = getCharacteristicByUuid(UUID_SERVICE, UUID_WRITE);
            if (writeCharacteristic == null) {
                Log.e(TAG, "Error, no suitable writeCharacteristic found.");
                return;
            }


            for (int i = 0; i < valueBytes.length; i++) {
                byte c = valueBytes[i];
                Log.w(TAG, "c = " + String.format("0x%x ", c));
            }

            writeCharacteristic.setValue(valueBytes);
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);

        } else {
            Log.e(TAG, "when writeBuffer(byte[]) mState != STATE_CONNECTED");
        }
    }

    /**
     * write a string to ble channel
     *
     * @param stringValue
     */
    public void writeBuffer(String stringValue) {

        if (mState == BluetoothBLEConnectionState.STATE_CONNECTED) {
            BluetoothGattCharacteristic writeCharacteristic = getCharacteristicByUuid(UUID_SERVICE, UUID_WRITE);
            if (writeCharacteristic == null) {
                Log.e(TAG, "Error, no suitable writeCharacteristic found.");
                return;
            }

            byte[] bytes = stringValue.getBytes();
            for (int i = 0; i < bytes.length; i++) {
                byte c = bytes[i];
                Log.w(TAG, "c = " + String.format("0x%x", c));
            }
            Log.w(TAG, "value to write: " + stringValue);
            writeCharacteristic.setValue(stringValue);
            mBluetoothGatt.writeCharacteristic(writeCharacteristic);

        } else {
            Log.e(TAG, "when writeBuffer(String) mState == STATE_DISCONNECTED ");
        }
    }

    /**
     * report remote message to upper
     *
     * @param device
     * @param bytes
     */
    void reportEventMessage(BluetoothDevice device, byte[] bytes) {
        int length = bytes.length;

        if (length < 6) {
            Log.w(TAG, "error, too short length, length = " + length);
            return;
        }

        if (bytes[0] == bytes[length - 1] && bytes[0] == (byte) 0xFF
                && bytes[1] == bytes[length - 2] && bytes[1] == (byte) 0xAA) {

            byte[] command = new byte[length - 4];
            for (int j = 0, i = 2; i < length - 2; i++, j++) {
                command[j] = bytes[i];
            }

            byte c = command[0];
            switch (c) {
                case EVENT_QUERY_RESULT:
                    mShouldStopSendingCommand = true;
                    if (checkQueryResultValid(command)) {
                        byte powerLevel = command[1];
                        byte tempLevel = command[2];
                        byte presetHourLevel = command[3];
                        int left = command[5] & 0xFF;
                        short minutesLeft = (short) ((command[4] << 8) & 0xFF00 | command[5] & 0xFF);

                        EventMessage message = new EventMessage(device, powerLevel, tempLevel, presetHourLevel, minutesLeft);
                        reportEventMessageToUpper(device, message);
                    }

                    break;
                case EVENT_HEAT_ECHO:
                    mShouldStopSendingCommand = true;
                    if (checkHeatEchoValid(command)) {
                        Log.w(TAG, "checkHeatEchoValid ok");
                        onDeviceEchoCommandValid(EVENT_HEAT_ECHO);
                    } else {
                        Log.w(TAG, "checkHeatEchoValid error");
                    }

                    break;
                case EVENT_PERIODIC_REPORT:
                    if (checkPeriodicReportValid(command)) {
                        byte powerLevel = command[1];
                        byte heatingTempLevel = HEATING_TEMP_LEVEL_INVALID;
                        byte presetHourLevel = HEATING_PRESET_HOURS_INVALID;
                        short minutesLeft = (short) ((command[2] << 8) & 0xFF00 | command[3] & 0xFF);

                        EventMessage message = new EventMessage(device, powerLevel, heatingTempLevel, presetHourLevel, minutesLeft);
                        reportEventMessageToUpper(device, message);
                    }
                    break;
                case EVENT_TERMINATE_HEATING_ECHO:
                    mShouldStopSendingCommand = true;
                    if (checkTerminateHeatingValid(command)) {
                        onDeviceEchoCommandValid(EVENT_TERMINATE_HEATING_ECHO);
                    }
                    break;
            }

        } else {
            StringBuilder builder = new StringBuilder();
            builder.append("bytes[0]:").append(String.format("0x%x", bytes[0])).append("\n")
                    .append("bytes[1]:").append(String.format("0x%x", bytes[1])).append("\n")
                    .append("bytes[length-2]:").append(String.format("0x%x", bytes[length - 2])).append("\n")
                    .append("bytes[length-1]:").append(String.format("0x%x", bytes[length - 1])).append("\n");
            Log.w(TAG, "Starting codes don't match end codes:\n " + builder.toString());
        }
    }


    boolean checkQueryResultValid(byte[] command) {
        if (command.length != 7) {
            Log.e(TAG, "query result length error");
            return false;
        }
        byte com = command[0];
        byte powerLevel = command[1];
        byte heatingTempLevel = command[2];
        byte presetHourLevel = command[3];
        byte timeLeftHigh = command[4];
        byte timeLeftLow = command[5];
        byte checkSum = command[6];

        byte sum = (byte) (com + powerLevel + heatingTempLevel + presetHourLevel + timeLeftHigh + timeLeftLow);
        Log.e(TAG, "sum = " + sum);
        if (sum != checkSum) {
            Log.e(TAG, "checkQueryResultValid(), check sum failed");
            Log.w(TAG, "com: " + com
                    + ", powerLevel: " + powerLevel
                    + ", heatingTempLevel: " + heatingTempLevel
                    + ", presetHourLevel: " + presetHourLevel
                    + ", timeLeftHigh: " + timeLeftHigh
                    + ", timeLeftLow: " + timeLeftLow
                    + ", checkSum: " + checkSum);
            return false;
        }

        return true;
    }

    boolean checkHeatEchoValid(byte[] command) {
        if (command.length != 5) {
            Log.e(TAG, "check heat echo length error");
            return false;
        }
        byte com = command[0];
        byte powerLevel = command[1];
        byte heatingTempLevel = command[2];
        byte presetHourLevel = command[3];
        byte checkSum = command[4];
        byte sum = (byte) (com + powerLevel + heatingTempLevel + presetHourLevel);
        Log.e(TAG, "sum = " + sum);
        if (sum != checkSum) {
            Log.e(TAG, "checkHeatEchoValid(), check sum failed");
            Log.w(TAG, "com: " + com + ", heatingTempLevel: " + heatingTempLevel
                    + ", presetHourLevel: " + presetHourLevel + ", checkSum: " + checkSum);
            return false;
        }

        return true;
    }


    boolean checkPeriodicReportValid(byte[] command) {
        if (command.length != 5) {
            Log.e(TAG, "periodic report length error");
            return false;
        }
        byte com = command[0];
        byte powerLevel = command[1];
        byte timeLeftHigh = command[2];
        byte timeLeftLow = command[3];
        byte checkSum = command[4];
        byte sum = (byte) (com + powerLevel + timeLeftHigh + timeLeftLow);
        Log.e(TAG, "sum = " + sum);
        if (sum != checkSum) {
            Log.e(TAG, "checkPeriodicReportValid(), check sum failed");
            Log.w(TAG, "com: " + com + ", powerLevel: " + powerLevel
                    + ", timeLeftHigh: " + timeLeftHigh + ", timeLeftLow: " + timeLeftLow + ", checkSum: " + checkSum);
            return false;
        }

        return true;
    }

    boolean checkTerminateHeatingValid(byte[] command) {
        if (command.length != 2) {
            Log.e(TAG, "TerminateHeating echo length error");
            return false;
        }
        byte com = command[0];
        byte checkSum = command[1];
        byte sum = (byte) com;
        Log.e(TAG, "sum = " + sum);
        if (sum != checkSum) {
            Log.e(TAG, "checkTerminateHeatingValid(), check sum failed");
            Log.w(TAG, "com: " + com + ", checkSum: " + checkSum);
            return false;
        }

        return true;
    }


    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed(BluetoothDevice device) {

        mState = BluetoothBLEConnectionState.STATE_DISCONNECTED;
        onConnectFailed(device);

    }


    void reportEventMessageToUpper(final BluetoothDevice device, final EventMessage message) {
        mConnectExecutor.execute(new Runnable() {
            @Override
            public void run() {
                onDeviceEventMessage(device, message);
            }
        });
    }

    void reportEventMessageToUpper(final BluetoothDevice device, final String message) {
        mConnectExecutor.execute(new Runnable() {
            @Override
            public void run() {
                onDeviceEventMessage(device, message);
            }
        });
    }

    private String getStringFromBytes(byte[] srcBytes, int length) {
        byte[] destBytes = new byte[length];
        System.arraycopy(srcBytes, 0, destBytes, 0, length);
        return new String(destBytes);
    }


    private BluetoothGattService getServiceByUuid(UUID serviceUuid) {

        BluetoothGattService gattService = null;
        if (mGattServicesMap.containsKey(serviceUuid.toString())) {
            gattService = mGattServicesMap.get(serviceUuid.toString());
            Log.w(TAG, "serviceUuid:" + serviceUuid + " found.");
        } else {
            Log.w(TAG, "serviceUuid: " + serviceUuid + " not found");
        }
        return gattService;
    }

    private BluetoothGattCharacteristic getCharacteristicByUuid(UUID serviceUuid, UUID characteristicUuid) {

        BluetoothGattCharacteristic gattCharacteristic = null;
        Log.w(TAG, "serviceUuid: " + serviceUuid + ", characteristicUuid: " + characteristicUuid);

        if (mGattServicesMap.containsKey(serviceUuid.toString())) {

            BluetoothGattService service = mGattServicesMap.get(serviceUuid.toString());
            gattCharacteristic = service.getCharacteristic(characteristicUuid);
            if (gattCharacteristic != null) {
                Log.w(TAG, "gattCharacteristic:" + characteristicUuid + " found.");
            } else {
                Log.w(TAG, "gattCharacteristic: " + characteristicUuid + " not found");
            }
        } else {
            Log.w(TAG, "serviceUuid: " + serviceUuid + " not found");
        }

        return gattCharacteristic;
    }


    void reset() {
        mGattServicesMap.clear();
    }

    private class BLECommCallback extends BluetoothGattCallback {


        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.w(TAG, "onConnectionStateChange, status = " + status + ", newState = " + newState);
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                //report event for connected
                mState = BluetoothBLEConnectionState.STATE_CONNECTED;
                Log.w(TAG, "mState: " + mState);
                onDeviceConnectionStateChanged(gatt.getDevice(), BluetoothBLEConnectionState.STATE_CONNECTED);
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                //report event for disconnected
                mState = BluetoothBLEConnectionState.STATE_DISCONNECTED;
                Log.w(TAG, "mState: " + mState);
                BluetoothDevice device = gatt.getDevice();
                onDeviceConnectionStateChanged(gatt.getDevice(), BluetoothBLEConnectionState.STATE_DISCONNECTED);
                reset();
//                gatt.close();
                if (status != BluetoothGatt.GATT_SUCCESS) {
                    disconnect();
                    if (status == 22) {
                        return;
                    }
                    Log.w(TAG, "reconnect due to status: " + status);
                    connect(device);
                }

            }
        }


        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.w(TAG, "onCharacteristicChanged");
            Log.w(TAG, "characteristic.getValue().length = " + characteristic.getValue().length);


            byte[] report = characteristic.getValue();
            for (int j = 0; j < report.length; j++) {
                Log.w(TAG, "report[" + j + "]=" + String.format("0x%2x ", report[j]));
            }
            byte[] bytes = trimBytes(characteristic.getValue(), characteristic.getValue().length);
            StringBuilder builder = new StringBuilder();
            builder.append("read reported data successfully from remote: ");
            for (int i = 0; i < bytes.length; i++) {
                byte c = bytes[i];
                builder.append(String.format("0x%x ", c));

            }
//            readBuffer();
            reportEventMessage(gatt.getDevice(), bytes);
            Log.w(TAG, builder.toString());
            reportEventMessageToUpper(gatt.getDevice(), builder.toString());

        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            Log.w(TAG, "onCharacteristicRead, status = " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {

                Log.w(TAG, "characteristic.getValue().length = " + characteristic.getValue().length);
                byte[] bytes = trimBytes(characteristic.getValue(), characteristic.getValue().length);
                StringBuilder builder = new StringBuilder();
                builder.append("read successfully data: ");
                for (int i = 0; i < bytes.length; i++) {
                    byte c = bytes[i];
                    builder.append(String.format("0x%x ", c));
                }

                reportEventMessage(gatt.getDevice(), bytes);

                Log.w(TAG, builder.toString());
                reportEventMessageToUpper(gatt.getDevice(), builder.toString());
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.w(TAG, "onCharacteristicWrite, status = " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.w(TAG, "characteristic.getValue().length = " + characteristic.getValue().length);
                byte[] bytes = trimBytes(characteristic.getValue(), characteristic.getValue().length);

                StringBuilder builder = new StringBuilder();
                builder.append("written successfully data: ");
                for (int i = 0; i < bytes.length; i++) {
                    byte c = bytes[i];
                    builder.append(String.format("0x%x ", c));

                }
                Log.w(TAG, builder.toString());
                reportEventMessageToUpper(gatt.getDevice(), builder.toString());

//                simulateReport(gatt.getDevice());

            }

        }


        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.w(TAG, "onReadRemoteRssi, status = " + status + ", rssi = " + rssi);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //get rssi
            }
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.w(TAG, "onDescriptorWrite status = " + status);
//            simulateReport(gatt.getDevice());
        }


        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.w(TAG, "onServicesDiscovered, status = " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> gattServices = gatt.getServices();
                for (BluetoothGattService service : gattServices) {
                    UUID uuid = service.getUuid();
                    Log.w(TAG, "serviceUuid:" + uuid + ", device: " + gatt.getDevice().getName());
                    mGattServicesMap.put(uuid.toString(), service);

                    List<BluetoothGattCharacteristic> gattCharacteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : gattCharacteristics) {
                        UUID characterUuid = characteristic.getUuid();
                        Log.w(TAG, "characterUuid:" + characterUuid + "\n" + ", belongs to service: " + characteristic.getService().getUuid());
                    }
                }

                BluetoothGattCharacteristic notifyCharacteristic = getCharacteristicByUuid(UUID_SERVICE, UUID_NOTIFY);
                setCharacteristicNotification(notifyCharacteristic);

                onBleServicesDiscovered(gattServices, gatt.getDevice());
            }

        }


        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.w(TAG, "onServicesDiscovered, status = " + status);
        }
    }

    /**
     * set notification characteristic to receive buffer from remote automatically
     *
     * @param characteristic
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic) {
        if (characteristic == null) {
            Log.e(TAG, "Error, no suitable notifyCharacteristic found.");
            return;
        }

        boolean notificationReqSuccess = mBluetoothGatt.setCharacteristicNotification(characteristic, true);
        Log.w(TAG, "notificationReqSuccess = " + notificationReqSuccess);
        if (notificationReqSuccess) {
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                Log.w(TAG, "descriptor.getUuid = " + descriptor.getUuid());
            }
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID_NOTIFY_DESCRIPTOR);
            Log.w(TAG, "descriptor = " + descriptor);
            if (descriptor != null) {
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                mBluetoothGatt.writeDescriptor(descriptor);
            }
        }
    }

    void simulateReport(BluetoothDevice device) {

        byte[] simulatedResult = new byte[15];
        simulatedResult[0] = (byte) 0xAA;
        simulatedResult[1] = (byte) 0xFF;
        simulatedResult[2] = (byte) 0xFF;
        simulatedResult[3] = (byte) 0xAA;
        simulatedResult[4] = EVENT_QUERY_RESULT;
        simulatedResult[5] = (byte) 2;
        simulatedResult[6] = (byte) 5;
        simulatedResult[7] = (byte) 5;
        short minutesLeft = 10;
        simulatedResult[8] = (byte) (minutesLeft >> 8);
        simulatedResult[9] = (byte) minutesLeft;
        Log.w(TAG, "simulatedResult[9]:" + String.format("0x%x ", simulatedResult[9]));
        byte checksum = (byte) (simulatedResult[4] + simulatedResult[5] + simulatedResult[6] + simulatedResult[7] + simulatedResult[8] + simulatedResult[9]);
        simulatedResult[10] = checksum;
        simulatedResult[11] = (byte) 0xAA;
        simulatedResult[12] = (byte) 0xFF;
        simulatedResult[13] = 0;
        simulatedResult[14] = 0;


//            byte[] simulatedResult = new byte[9];
//            simulatedResult[0] = (byte) 0xFF;
//            simulatedResult[1] = (byte) 0xAA;
//            simulatedResult[2] = EVENT_PERIODIC_REPORT;
//            simulatedResult[3] = (byte)5;
//            short minutesLeft = (short)0x126;
//            simulatedResult[4] = (byte)(minutesLeft >> 8);
//            simulatedResult[5] = (byte)minutesLeft;
//            byte checksum = (byte) (simulatedResult[2] + simulatedResult[3] + simulatedResult[4] + simulatedResult[5]);
//            simulatedResult[6] = checksum;
//            simulatedResult[7] = (byte) 0xAA;
//            simulatedResult[8] = (byte) 0xFF;

        byte[] resBytes = trimBytes(simulatedResult, simulatedResult.length);

        StringBuilder builder = new StringBuilder();
        builder.append("simulated report: \n");
        for (int i = 0; i < resBytes.length; i++) {
            byte c = resBytes[i];
            builder.append(String.format("0x%x ", c));

        }
        reportEventMessage(device, resBytes);
        reportEventMessageToUpper(device, builder.toString());
    }

    synchronized byte[] trimBytes(byte[] bytes, int length) {
        int startIndex = -1;
        int endIndex = -1;

        byte startFF = 0;
        byte startAA = 0;
        byte endAA = 0;
        byte endFF = 0;

        for (int i = 0; i < length; i++) {

            if (endIndex != -1) {
                endIndex++;
            }

            byte c = bytes[i];

            if (startFF == (byte) 0xFF && startAA == (byte) 0xAA && endAA == (byte) 0xAA && c == (byte) 0xFF) {
                endFF = c;
                break;
            }

            if (startFF == (byte) 0xFF && startAA == (byte) 0xAA && c == (byte) 0xAA && endFF == 0) {
                endAA = c;

            }

            if (startFF == (byte) 0xFF && c == (byte) 0xAA && endAA == 0 && endFF == 0) {
                startAA = c;
                startIndex = i - 1;
                endIndex = i;
            }

            if (c == (byte) 0xFF && startAA == 0 && endAA == 0 && endFF == 0) {
                startFF = c;
            }
        }

        Log.w(TAG, "startIndex = " + startIndex + ", endIndex = " + endIndex);

        if (startFF == (byte) 0xFF && startAA == (byte) 0xAA && endAA == (byte) 0xAA && endFF == (byte) 0xFF) {

            int len = endIndex - startIndex + 1;
            byte[] resBytes = new byte[len];
            for (int i = 0; i < len; i++) {
                resBytes[i] = bytes[startIndex++];
            }
            return resBytes;
        } else {
            Log.e(TAG, "0xFF 0xAA,...,0xAA 0xFF lost matches");
        }

        return new byte[0];
    }

    public class EventMessage {
        public BluetoothDevice mDevice;
        public byte mPowerLevel;
        public byte mHeatingTempLevel;
        public byte mHeatingHourLevel;
        public short mHeatingMinuteLeft;

        public EventMessage(BluetoothDevice device,
                            byte powerLevel,
                            byte heatingTempLevel,
                            byte hourLevel,
                            short minuteLeft) {
            mDevice = device;
            mPowerLevel = powerLevel;
            mHeatingTempLevel = heatingTempLevel;
            mHeatingHourLevel = hourLevel;
            mHeatingMinuteLeft = minuteLeft;
        }
    }


    private static class ServiceServerController extends BluetoothGattServerCallback {
    }

}

interface BleCallback {
    void onDeviceConnectionStateChanged(BluetoothDevice device, BluetoothLECommunicator.BluetoothBLEConnectionState state);

    void onBleServicesDiscovered(List<BluetoothGattService> services, BluetoothDevice device);

    void onConnectFailed(BluetoothDevice device);

    void onDeviceEventMessage(BluetoothDevice device, BluetoothLECommunicator.EventMessage message);

    void onDeviceEventMessage(BluetoothDevice device, String message);

    void onDeviceEchoCommandValid(byte command);
}
