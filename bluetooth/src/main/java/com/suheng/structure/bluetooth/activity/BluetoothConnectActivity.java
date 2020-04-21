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
import android.widget.Toast;

import com.suheng.structure.bluetooth.R;
import com.suheng.structure.bluetooth.connect.BluetoothCommService;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.io.IOException;
import java.util.UUID;

public class BluetoothConnectActivity extends BasicActivity {
    //private static final String UUID_NAME = "00001101-0000-1000-8000-00805F9B34FB";
    //private static final String UUID_NAME = "8CE255C0-200A-11E0-AC64-0800200C9A66";
    private static final String UUID_NAME = "00001105-0000-1000-8000-00805f9b34fb";
    private static final String EXTRA_BLUETOOTH_ADDRESS = "data_bluetooth_address";
    private BluetoothCommService mCommService = null;
    private BluetoothConnectTask mBluetoothConnectTask = new BluetoothConnectTask();

    public static void openPage(Context context, String address) {
        Intent intent = new Intent(context, BluetoothConnectActivity.class);
        intent.putExtra(EXTRA_BLUETOOTH_ADDRESS, address);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_bluetooth_connect_aty);

        mCommService = new BluetoothCommService(this, null);
        String bluetoothAddress = getIntent().getStringExtra(EXTRA_BLUETOOTH_ADDRESS);
        final BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(bluetoothAddress);
        Log.d(mTag, "address: " + bluetoothAddress + ", bluetooth device: " + bluetoothDevice);
        mCommService.connect(bluetoothDevice, false);

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
        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mCommService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mCommService.getState() == BluetoothCommService.STATE_NONE) {
                // Start the Bluetooth comm services
                mCommService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothConnectTask.disconnect();
        if (mCommService != null) {
            mCommService.stop();
        }
    }

    /**
     * Sends a message.
     *
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mCommService.getState() != BluetoothCommService.STATE_CONNECTED) {
            Toast.makeText(this, "not_connected", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothCommService to write
            byte[] send = message.getBytes();
            mCommService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            /*mOutStringBuffer.setLength(0);
            mOutEditText.setText(mOutStringBuffer);*/
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
