package com.example.vdemo;

import static common.constants.Constants.success_code;
import static common.utils.AppInit.appId;

import android.app.Application;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import common.callback.JsonCallback;
import common.constants.Constants;
import common.http.ApiConstants;
import common.model.MechanismModel;
import common.utils.OkGoUtils;
import common.utils.AppInit;
import common.utils.ToastUtils;

public class MyApplication extends Application {

    /**
     * isDebug 是否为测试环境
     * appid 机构号id
     */
    @Override
    public void onCreate() {
        super.onCreate();
        AppInit.init(this, false, "6254248");


    }


}