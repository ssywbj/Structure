package com.suheng.structure.hook;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.IBinder;

public class InstrumentationHook extends Instrumentation {

    @Override
    public Activity newActivity(Class<?> clazz, Context context, IBinder token,
                                Application application, Intent intent, ActivityInfo info,
                                CharSequence title, Activity parent, String id,
                                Object lastNonConfigurationInstance) throws InstantiationException,
            IllegalAccessException {
        return super.newActivity(clazz, context, token, application, intent, info,
                title, parent, id, lastNonConfigurationInstance);
    }

    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent)
            throws InstantiationException, IllegalAccessException,
            ClassNotFoundException {
        Activity activity = createActivity(intent);
        if (activity != null) {
            return activity;
        }
        return super.newActivity(cl, className, intent);
    }

    /*可以在createActivity拦截替换某个activity，下面自是一个简单例子*/
    protected Activity createActivity(Intent intent) {
        if (intent == null || intent.getComponent() == null) {
            return null;
        }

        String className = intent.getComponent().getClassName();
        if ("com.suheng.structure.MainActivity".equals(className)) {
            try {
                Class<?> pluginActivity = (Class<?>) Class.forName("com.suheng.structure.hook.HookActivity");
                return (Activity) pluginActivity.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
