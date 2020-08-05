/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.structure.watch.middleground;

import java.util.HashMap;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class SampleGattAttributes {
    private static HashMap<String, String> attributes = new HashMap<>();
    public static final String HEART_RATE_MEASUREMENT = "00002a37-0000-1000-8000-00805f9b34fb";
    public static final String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";

    public static final String SERVICE_HEART_RATE = "0000180d-0000-1000-8000-00805f9b34fb";
    public static final String SERVICE_DEVICE_INFO = "0000180a-0000-1000-8000-00805f9b34fb";
    public static final String CHAR_MANUFACTURER_NAME = "00002a29-0000-1000-8000-00805f9b34fb";

    public final static String UUID_SERVER = "0000ffe0-0000-1000-8000-00805f9b34fb";
    public static final String UUID_WRITE = "0000ff02-0000-1000-8000-00805f9b34fb";//写入特征UUID
    public final static String UUID_NOTIFY = "0000ffe1-0000-1000-8000-00805f9b34fb";

    static {
        // Sample Services.
        attributes.put(SERVICE_HEART_RATE, "Heart Rate Service");
        attributes.put(SERVICE_DEVICE_INFO, "Device Information Service");
        // Sample Characteristics.
        attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
        attributes.put(CHAR_MANUFACTURER_NAME, "Manufacturer Name String");

        attributes.put(UUID_SERVER, "UUID SERVER");
        attributes.put(UUID_WRITE, "UUID WRITE");
        attributes.put(UUID_NOTIFY, "UUID NOTIFY");
    }

    public static String lookup(String uuid, String defaultName) {
        String name = attributes.get(uuid);
        return name == null ? defaultName : name;
    }
}
