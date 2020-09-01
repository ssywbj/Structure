package com.suheng.structure.bluetooth.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.suheng.structure.bluetooth.R;
import com.suheng.structure.bluetooth.connect.BluetoothConnectHelper;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectActivity extends BasicActivity {
    //private static final String UUID_NAME = "00001101-0000-1000-8000-00805F9B34FB";
    //private static final String UUID_NAME = "8CE255C0-200A-11E0-AC64-0800200C9A66";
    private static final String UUID_NAME = "00001105-0000-1000-8000-00805f9b34fb";
    private static final String EXTRA_BLUETOOTH_ADDRESS = "data_bluetooth_address";
    private BluetoothConnectHelper mBluetoothConnectHelper = new BluetoothConnectHelper();
    private BluetoothConnectTask mBluetoothConnectTask = new BluetoothConnectTask();
    //private BLEHelper mBLEHelper = new BLEHelper(this);

    public static void openPage(Context context, String address) {
        Intent intent = new Intent(context, BluetoothConnectActivity.class);
        intent.putExtra(EXTRA_BLUETOOTH_ADDRESS, address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_bluetooth_connect_aty);

        String bluetoothAddress = getIntent().getStringExtra(EXTRA_BLUETOOTH_ADDRESS);
        final BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddress);
        Log.d(mTag, "address: " + bluetoothAddress + ", bluetooth device: " + bluetoothDevice);
        mBluetoothConnectHelper.connect(bluetoothDevice, false);

        /*mBLEHelper.scan();
        boolean connect = mBLEHelper.connect(bluetoothAddress);
        Log.d(mTag, "connect: " + connect);*/

        findViewById(R.id.item_data_int).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(String.valueOf(v.getId()));
            }
        });

        findViewById(R.id.item_data_string).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(((TextView) v).getText().toString());
            }
        });

        findViewById(R.id.item_data_object).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(bluetoothDevice.toString());
            }
        });

        findViewById(R.id.item_data_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendMessage(bluetoothDevice.toString());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBluetoothConnectHelper.getState() == BluetoothConnectHelper.STATE_NONE) {
            mBluetoothConnectHelper.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothConnectTask.disconnect();
        if (mBluetoothConnectHelper != null) {
            mBluetoothConnectHelper.stop();
        }
        //mBLEHelper.destroy();
    }

    private void sendMessage(String message) {
        if (mBluetoothConnectHelper.getState() != BluetoothConnectHelper.STATE_COMMUNICATE) {
            showToast("未连接");
            return;
        }

        if (message.length() > 0) {
            mBluetoothConnectHelper.write(message.getBytes());
        }
    }

    private final class BluetoothConnectTask extends AsyncTask<BluetoothDevice, Integer, BluetoothSocket> {
        private BluetoothSocket mBluetoothSocket;

        @Override
        protected BluetoothSocket doInBackground(BluetoothDevice... bluetoothDevices) {
            try {
                Log.d(mTag, "start connect bluetooth, uuid: " + UUID_NAME);
                //mBluetoothSocket = bluetoothDevices[0].createRfcommSocketToServiceRecord(UUID.fromString(UUID_NAME));
                mBluetoothSocket = bluetoothDevices[0].createInsecureRfcommSocketToServiceRecord(UUID.fromString(UUID_NAME));
            } catch (Exception e) {
                try {
                    mBluetoothSocket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                Log.e(mTag, "connect bluetooth error: " + e.toString());
            }
            return mBluetoothSocket;
        }

        @Override
        protected void onPostExecute(BluetoothSocket bluetoothSocket) {
            super.onPostExecute(bluetoothSocket);
            if (bluetoothSocket == null) {
                return;
            }

            //BluetoothClient.getInstance().init(mBluetoothSocket);
        }

        /**
         * 断开连接
         */
        public void disconnect() {
            if (mBluetoothSocket != null) {
                if (mBluetoothSocket.isConnected()) {
                    try {
                        mBluetoothSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                mBluetoothSocket = null;
            }

        }
    }

}