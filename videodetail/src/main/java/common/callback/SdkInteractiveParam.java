package common.callback;

import android.util.Log;

import common.model.SdkUserInfo;
import common.model.ShareInfo;
import common.model.ThirdUserInfo;
import event.SingleLiveEvent;

public class SdkInteractiveParam {
    private String TAG = "SdkInteractiveParam";
    public SdkParamCallBack callBack;
    public static SdkInteractiveParam sdkParam;
    public SingleLiveEvent<ThirdUserInfo> userInfoEvent = new SingleLiveEvent<>();

    private SdkInteractiveParam() {
    }

    public static SdkInteractiveParam getInstance() {
        if (sdkParam == null) {
            synchronized (SdkInteractiveParam.class) {
                if (sdkParam == null) {
                    sdkParam = new SdkInteractiveParam();
                }
            }
        }
        return sdkParam;
    }

    public void setSdkCallBack(SdkParamCallBack callBack) {
        this.callBack = callBack;
    }

    /**
     * 传递分享对象
     *
     * @param shareInfo 分享要素
     */
    public void shared(ShareInfo shareInfo) {
        if (callBack == null) {
            Log.e(TAG, "获取参数失败，请重试");
        } else {
            callBack.shared(shareInfo);
        }
    }

    /**
     * Sdk去登录
     */
    public void toLogin() {
        if (callBack == null) {
            Log.e(TAG, "获取参数失败，请重试");
        } else {
            callBack.toLogin();
        }
    }

    /**
     * 获取用户信息
     */
    public ThirdUserInfo getUserInfo() {
        if (callBack == null) {
            Log.e(TAG, "获取参数失败，请重试");
            return null;
        } else {
            return callBack.setThirdUserInfo();
        }
    }


}
