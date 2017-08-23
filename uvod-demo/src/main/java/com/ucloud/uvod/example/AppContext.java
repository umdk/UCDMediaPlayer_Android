package com.ucloud.uvod.example;

import android.app.Application;

import com.umeng.analytics.MobclickAgent;


/**
 * Created by lw.tan on 2017/8/17.
 */

public class AppContext extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MobclickAgent.setScenarioType(getApplicationContext(), MobclickAgent.EScenarioType.E_UM_NORMAL);
    }
}
