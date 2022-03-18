package common.callback;

import common.model.SdkUserInfo;
import common.model.ShareInfo;
import common.model.ThirdUserInfo;

public interface SdkParamCallBack {

    /**
     * 获取用户信息
     */
    ThirdUserInfo setThirdUserInfo();

    /**
     * 分享事件
     */
    void shared(ShareInfo shareInfo);

    /**
     * 去登录
     */
    void toLogin();

}
