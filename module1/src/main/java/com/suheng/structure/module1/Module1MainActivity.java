package com.suheng.structure.module1;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.ui.architecture.basic.BasicActivity;

import java.util.List;

@Route(path = RouteTable.MODULE1_ATY_MODULE1_MAIN)
public class Module1MainActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.module1_aty_module1_main);

        SocketClient.getInstance().connect(RouteTable.SOCKET_HOST, RouteTable.SOCKET_PORT);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sensorManager == null) {
            Log.w(mTag, "SensorManager is null !");
        } else {
            List<Sensor> list = sensorManager.getSensorList(Sensor.TYPE_ALL);
            for (Sensor sensor : list) {
                Log.d(mTag, "sensor: " + sensor + ", name: " + sensor.getName());
            }

            Sensor stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (stepCounterSensor == null) {
                Log.w(mTag, "StepCounterSensor is null !");
            } else {
                sensorManager.registerListener(null, stepCounterSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        }
    }

    public void sendString(View view) {
        SocketClient.getInstance().sendRequest("ni hao");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SocketClient.getInstance().disconnect();
    }
}
