package com.suheng.structure.bluetooth.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.suheng.structure.bluetooth.R;
import com.suheng.structure.bluetooth.ble.DeviceControlActivity;
import com.suheng.structure.bluetooth.connect.BLEHelper;
import com.suheng.structure.ui.architecture.adapter.RecyclerAdapter;
import com.suheng.structure.ui.architecture.basic.BasicActivity;
import com.suheng.structure.ui.architecture.widget.RecyclerItemDecoration;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

public class BluetoothListActivity extends BasicActivity {
    private static final int REQUEST_ENABLE_BLUETOOTH = 0;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothListAdapter mBluetoothListAdapter;
    private List<BluetoothDevice> mBluetoothList = new ArrayList<>();

    private BLEHelper mBLEHelper = new BLEHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_bluetooth_list_aty);
        this.initRecyclerView();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (this.isSupportBluetooth()) {
            if (mBluetoothAdapter.isEnabled()) {
                this.getPairedDevice();
            } else {
                Log.d(mTag, "bluetooth isn't enabled, request action enable!");
                Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE); //请求用户开启
                startActivityForResult(intent, REQUEST_ENABLE_BLUETOOTH);
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
        } else {
            Log.w(mTag, "this device isn't support bluetooth!");
            finish();
        }
    }

    private BluetoothLECommunicator mBluetoothLECommunicator;

    private void initRecyclerView() {
        mBluetoothListAdapter = new BluetoothListAdapter(mBluetoothList);
        mBluetoothListAdapter.setOnItemClickListener(new RecyclerAdapter.OnItemClickListener<BluetoothDevice>() {
            @Override
            public void onItemClick(View view, BluetoothDevice data, int position) {
                //BluetoothConnectActivity.openPage(BluetoothListActivity.this, data.getAddress());
                //bondTargetDevice(data);
                DeviceControlActivity.openPage(BluetoothListActivity.this, data.getName(), data.getAddress());
                /*if (mBluetoothLECommunicator.isConnected()) {
                    mBluetoothLECommunicator.writeBuffer("WWDSDSDS");
                } else {
                    mBluetoothLECommunicator.connect(data);
                }*/
            }
        });

        RecyclerView recyclerView = findViewById(R.id.bluetooth_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());//设置Item增加、移除动画
        recyclerView.addItemDecoration(new RecyclerItemDecoration(this, true));//设置Item分隔线
        recyclerView.setAdapter(mBluetoothListAdapter);

        mBluetoothLECommunicator = new BluetoothLECommunicator(BluetoothListActivity.this) {
            @Override
            public void onDeviceConnectionStateChanged(BluetoothDevice device, BluetoothBLEConnectionState state) {
                Log.w(BluetoothLECommunicator.TAG, "onDeviceConnectionStateChanged, device: " + device + ", state: " + state);
            }

            @Override
            public void onBleServicesDiscovered(List<BluetoothGattService> services, BluetoothDevice device) {
                Log.w(BluetoothLECommunicator.TAG, "onBleServicesDiscovered: " + device + ", device: " + device);
            }

            @Override
            public void onConnectFailed(BluetoothDevice device) {
                Log.w(BluetoothLECommunicator.TAG, "onConnectFailed: " + device);
            }

            @Override
            public void onDeviceEventMessage(BluetoothDevice device, EventMessage message) {
                Log.w(BluetoothLECommunicator.TAG, "onDeviceEventMessage: " + device + ", message: " + message);
            }

            @Override
            public void onDeviceEventMessage(BluetoothDevice device, String message) {
                Log.w(BluetoothLECommunicator.TAG, "onDeviceEventMessage: " + device + ", message: " + message);
            }

            @Override
            public void onDeviceEchoCommandValid(byte command) {
                Log.w(BluetoothLECommunicator.TAG, "onDeviceEchoCommandValid: " + command);
            }
        };
    }

    public boolean isSupportBluetooth() {
        return (mBluetoothAdapter != null);
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
                        String name = bluetoothDevice.getName();
                        Log.d(mTag, "found device, address: " + address + ", name: " + name
                                + ", bond state: " + bluetoothDevice.getBondState());
                        mBluetoothList.add(bluetoothDevice);
                        mBluetoothListAdapter.notifyDataSetChanged();

                        /*if ("BE:FC:46:00:00:02".equals(address)) {//Android Bluedroid
                            bondTargetDevice(bluetoothDevice);
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
                                //BluetoothConnectActivity.openPage(BluetoothListActivity.this, bluetoothDevice.getAddress());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mFindBlueToothReceiver);
        mBluetoothList.clear();
        mBLEHelper.destroy();

        /*if (mBluetoothLECommunicator != null) {
            mBluetoothLECommunicator.disconnect();
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            if (resultCode == Activity.RESULT_OK) {//bluetooth is opened
                //可以获取列表操作等
                this.getPairedDevice();
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
                mBluetoothList.add(bondedDevice);
            }
        }

        this.doDiscovery();

        //mBLEHelper.scan();
    }

    private void doDiscovery() {
        Executors.newCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothAdapter.startDiscovery();
            }
        });
    }

    /**
     * 配对蓝牙设备
     */
    private void bondTargetDevice(BluetoothDevice bluetoothDevice) {
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

    private BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            String address = gatt.getDevice().getAddress();
            Log.d(mTag, "onConnectionStateChange, status: " + status + ", newState: " + newState
                    + ", address:" + address);
        }
    };

    private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        }
    };

    private static final class BluetoothListAdapter extends RecyclerAdapter<BluetoothDevice> {

        BluetoothListAdapter(List<BluetoothDevice> dataList) {
            super(dataList);
        }

        @Override
        protected void bindView(RecyclerView.ViewHolder viewHolder, int position, BluetoothDevice data) {
            if (viewHolder instanceof ContentHolder) {
                ContentHolder contentHolder = (ContentHolder) viewHolder;
                if (TextUtils.isEmpty(data.getName())) {
                    contentHolder.textName.setText(data.getAddress());
                } else {
                    contentHolder.textName.setText(data.getName());
                }
            }
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ContentHolder(getItemLayout(parent.getContext(), R.layout.bluetooth_bluetooth_list_aty_adt));
        }

        static class ContentHolder extends RecyclerView.ViewHolder {
            TextView textName;

            ContentHolder(View view) {
                super(view);
                textName = view.findViewById(R.id.text_bluetooth_name);
            }
        }
    }

}
