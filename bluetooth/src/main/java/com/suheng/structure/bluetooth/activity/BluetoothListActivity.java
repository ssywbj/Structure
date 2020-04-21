package com.suheng.structure.bluetooth.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.suheng.structure.bluetooth.R;
import com.suheng.structure.bluetooth.connect.BluetoothCommService;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class BluetoothListActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_bluetooth_list_aty);


        findViewById(R.id.item_module1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage("Hello World!");
            }
        });

        findViewById(R.id.item_module2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the device MAC address
                String address = "E0:DD:C0:7B:B6:F1";
                // Get the BluetoothDevice object
                BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                Log.d(mTag, "address: " + address + ", device: " + device);
                // Attempt to connect to the device
                mCommService.connect(device, false);
            }
        });

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.isSupportBluetooth()) {
            if (mBluetoothAdapter.isEnabled()) {
                this.doDiscovery();
                this.getPairedDevice();
            } else {
                Log.d(mTag, "bluetooth isn't enabled, request action enable!");
                //请求用户开启
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        } else {
            Log.w(mTag, "this device isn't support bluetooth!");
        }

        IntentFilter actionDiscoveryStarted = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);//搜索开始
        IntentFilter actionDiscoveryFinished = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);//搜索结束
        IntentFilter actionFound = new IntentFilter(BluetoothDevice.ACTION_FOUND);//寻找到设备
        IntentFilter actionPairingRequest = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);//配对请求
        IntentFilter actionBondStateChanged = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//配对状态改变
        registerReceiver(mFindBlueToothReceiver, actionDiscoveryStarted);
        registerReceiver(mFindBlueToothReceiver, actionDiscoveryFinished);
        registerReceiver(mFindBlueToothReceiver, actionFound);
        registerReceiver(mFindBlueToothReceiver, actionBondStateChanged);
        registerReceiver(mFindBlueToothReceiver, actionPairingRequest);

        // Initialize the BluetoothCommService to perform bluetooth connections
        mCommService = new BluetoothCommService(this, null);
    }

    private final BroadcastReceiver mFindBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            switch (action) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Log.d(mTag, "discovery start");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Log.d(mTag, "discovery finish");
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    if (bluetoothDevice == null) {
                        Log.d(mTag, "found device, but BluetoothDevice is null");
                    } else {
                        String address = bluetoothDevice.getAddress();
                        Log.d(mTag, "found device, address: " + address + ", name: " + bluetoothDevice.getName()
                                + ", bond state: " + bluetoothDevice.getBondState());
                        /*if ("E0:DD:C0:7B:B6:F1".equals(address)) {
                            //pinTargetDevice(bluetoothDevice);
                            // Get the BluetoothDevice object
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                            // Attempt to connect to the device
                            mCommService.connect(device, false);
                        }*/
                    }
                    break;
                case BluetoothDevice.ACTION_PAIRING_REQUEST:
                    Log.d(mTag, "device pairing request");
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    if (bluetoothDevice == null) {
                        Log.d(mTag, "bond state changed, but BluetoothDevice is null");
                    } else {
                        int bondState = bluetoothDevice.getBondState();
                        String msg = "bond state changed, address: " + bluetoothDevice.getAddress()
                                + ", name: " + bluetoothDevice.getName() + ", bond state: " + bondState
                                + ", thread: " + Thread.currentThread().getName();
                        switch (bluetoothDevice.getBondState()) {
                            case BluetoothDevice.BOND_BONDING:
                                msg += ", bond_bonding......";
                                break;
                            case BluetoothDevice.BOND_BONDED:
                                msg += ", bonded finish";
                                //mBluetoothConnectTask.execute(bluetoothDevice);

                                // Get the BluetoothDevice object
                                /*BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(bluetoothDevice.getAddress());
                                // Attempt to connect to the device
                                mCommService.connect(device, false);*/
                                break;
                            case BluetoothDevice.BOND_NONE:
                                msg += ", bond cancel";
                            default:
                                break;
                        }
                        Log.d(mTag, msg);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //private static final String UUID_NAME = "00001101-0000-1000-8000-00805F9B34FB";
    //private static final String UUID_NAME = "8CE255C0-200A-11E0-AC64-0800200C9A66";
    private static final String UUID_NAME = "00001105-0000-1000-8000-00805f9b34fb";
    private static final int REQUEST_ENABLE_BLUETOOTH = 0;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothConnectTask mBluetoothConnectTask = new BluetoothConnectTask();

    public boolean isSupportBluetooth() {
        return (mBluetoothAdapter != null);
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
        unregisterReceiver(mFindBlueToothReceiver);
        mBluetoothConnectTask.disconnect();
        if (mCommService != null) {
            mCommService.stop();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {//bluetooth is opened
                //可以获取列表操作等
                this.getPairedDevice();
                this.doDiscovery();
            } else {
                Log.d(mTag, "bluetooth isn't enabled after request action enable!");
            }
        }
    }

    /*
     *获取已经配对的设备
     */
    private void getPairedDevice() {
        Set<BluetoothDevice> bondedDevices = mBluetoothAdapter.getBondedDevices();
        if (bondedDevices == null || bondedDevices.size() == 0) {
            Log.d(mTag, "no paired devices");
        } else {
            for (BluetoothDevice bondedDevice : bondedDevices) {
                Log.d(mTag, "paired device address: " + bondedDevice.getAddress() + ", name: "
                        + bondedDevice.getName() + ", bond state: " + bondedDevice.getBondState());
                if ("E0:DD:C0:7B:B6:F1".equals(bondedDevice.getAddress())) {
                    mBluetoothConnectTask.execute(bondedDevice);
                    break;
                }
            }
        }
    }

    private void doDiscovery() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
            }
        }).start();
    }

    /**
     * 配对蓝牙设备
     */
    private void pinTargetDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothAdapter.cancelDiscovery(); //配对之前，停止搜索
        if (bluetoothDevice == null) {
            return;
        }
        if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {//没配对才配对
            Log.d(mTag, "start create bond");
            if (bluetoothDevice.createBond()) {
                Log.d(mTag, "create bond successful");
            } else {
                Log.d(mTag, "create bond fail");
            }
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

    private BluetoothCommService mCommService = null;

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
}
