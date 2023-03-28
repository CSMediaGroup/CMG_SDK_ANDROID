package com.example.vdemo;
import android.app.Application;

import common.utils.AppInit;

public class MyApplication extends Application {

    /**
     * isDebug 是否为测试环境
     * appid 机构号id
     */
    @Override
    public void onCreate() {
        super.onCreate();
        AppInit.init(this, false, "9111184"); //1960205 uat   9111184 生产
    }


}