package com.suheng.structure.hook;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class HookManager {

    static Object activityThreadInstance;

    public static void init() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class<?> activityThread = Class.forName("android.app.ActivityThread");
        Method currentActivityThread = activityThread.getDeclaredMethod("currentActivityThread");
        activityThreadInstance = currentActivityThread.invoke(null);
    }

    public static void injectInstrumentation() throws NoSuchFieldException, IllegalAccessException, IllegalArgumentException {
        Field field = activityThreadInstance.getClass().getDeclaredField("mInstrumentation");
        field.setAccessible(true);
        InstrumentationHook instrumentationHook = new InstrumentationHook();
        field.set(activityThreadInstance, instrumentationHook);
    }

}
