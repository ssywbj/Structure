package com.suheng.structure.common.data.net;

import android.content.Context;
import android.os.Handler;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.suheng.structure.common.arouter.RouteTable;
import com.suheng.structure.common.data.net.callback.LoginListener;
import com.suheng.structure.common.event.ExitLoginEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.Random;

@Route(path = RouteTable.COMMON_PROVIDER_REQUEST_MANAGER)
public class RequestManager implements IProvider {

    @Override
    public void init(Context context) {
    }

    public void doLoginRequest(String name, String pwd, final LoginListener loginListener) {
        //Log.d(mTAG, "login request-->" + request);
        //模拟网络请求
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (loginListener == null) {
                    return;
                }
                int random = new Random().nextInt(5);
                if (random == 1) {
                    loginListener.onLoginFail("密码不正确，请重新输入！", random);
                } else if (random == 2) {
                    loginListener.onLoginFail("账号异常，请重新输入！", random);
                } else {
                    loginListener.onLoginSuccess();
                }
            }
        }, 1000);
    }

    public void doExitRequest() {
        //模拟网络请求
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int random = new Random().nextInt(5);
                ExitLoginEvent event = new ExitLoginEvent();
                if (random == 1 || random == 2) {
                    event.setCode(random);
                }
                EventBus.getDefault().post(event);
            }
        }, 2000);
    }
}
