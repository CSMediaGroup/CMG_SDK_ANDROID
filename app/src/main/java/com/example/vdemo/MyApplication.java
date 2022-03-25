package com.example.vdemo;

import android.app.Application;

import common.utils.OkGoUtils;
import common.utils.AppInit;

public class MyApplication extends Application {

    /**
     * isDebug 是否为测试环境
     * appkey 你的appKey
     */
    @Override
    public void onCreate() {
        super.onCreate();
        AppInit.init(this,true, "1832192","your_appKey");
        OkGoUtils.initOkGo(this);

//        /* 初始化开始 */
//        final InitConfig config = new InitConfig("1a333b51a32bf4f75e7f950cb4935236", "huawei"); // appid和渠道，appid如不清楚请联系客户成功经理
//        // 私有化配置，云版可忽略{{REPORT_URL}} 例如 https://yourdomain.com，注意域名后不要加“/”
//        config.setUriConfig(UriConfig.createByDomain("", null));
//        // 开启AB测试
//        config.setAbEnable(true);
//        // 是否在控制台输出日志，可用于观察用户行为日志上报情况，上线之前可去掉
//        config.setLogger(new ILogger() {
//            @Override
//            public void log(String s, Throwable throwable) {
//                Log.d("AppLog------->: ",""+s);
//            }
//        });
//        // 开启圈选埋点
////        config.setPicker(new Picker(this, config));
////        config.setH5CollectEnable (false);//关闭内嵌H5页面的无埋点事件
//        // 加密开关，SDK 5.5.1 及以上版本支持，false 为关闭加密，上线前建议设置为 true
//        AppLog.setEncryptAndCompress(false);
//        //开启圈选预置事件开关，true开启，false关闭
////        config.setAutoTrackEnabled (true);
//        //是否上报
//        config.setAutoStart(false);
//
//        AppLog.init(this, config);
//        /* 初始化结束 */
//
//        //授权后
////        AppLog.start();
    }
}