package ui.activity;

import static common.constants.Constants.success_code;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.zhouwei.library.CustomPopWindow;
import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.WebViewClient;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.wdcs.videodetail.demo.R;

import org.json.JSONException;

import common.callback.JsonCallback;
import common.callback.SdkInteractiveParam;
import common.constants.Constants;
import common.http.ApiConstants;
import common.model.AppSystemModel;
import common.model.DeviceIdModel;
import common.model.JumpToNativePageModel;
import common.model.MechanismModel;
import common.model.SdkUserInfo;
import common.model.ShareInfo;
import common.utils.AppInit;
import common.utils.ButtonSpan;
import common.utils.ImageUtils;
import common.utils.PersonInfoManager;
import common.utils.SavePhoto;
import common.utils.ScreenUtils;
import common.utils.SystemUtil;
import common.utils.ToastUtils;
import io.reactivex.functions.Consumer;

public class WebActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = WebActivity.class.getSimpleName();
    private LinearLayout imgBack;
    private TextView webTitle;
    private LinearLayout iconShare;
    private ImageView imgClose;
    public static final int LOGIN_REQUEST_CODE = 315;
    private AgentWeb mAgentWeb;
    private LinearLayout container;
    private WebViewClient mWebViewClient;
    private BridgeWebView mBridgeWebView;
    private SdkUserInfo.DataDTO userInfo;
    private Handler handler;
    private CustomPopWindow sharePop;
    private View sharePopView;
    private ImageView shareWxBtn;
    private ImageView shareCircleBtn;
    private ImageView shareQqBtn;
    private JSONObject dataObject;
    private JumpToNativePageModel param;
    private String logoUrl;
    private String webUrl;
    private String intentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        param = (JumpToNativePageModel) getIntent().getSerializableExtra("param");
        setContentView(R.layout.activity_web);
        initView();
    }

    private void initView() {
        container = findViewById(R.id.container);
        imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
        imgClose = findViewById(R.id.imgClose);
        webTitle = findViewById(R.id.webTitle);
        iconShare = findViewById(R.id.iconShare);
        iconShare.setOnClickListener(this);
        ScreenUtils.fullScreen(this, true);
        ScreenUtils.setStatusBarColor(this, R.color.white);
        param = (JumpToNativePageModel) getIntent().getSerializableExtra("param");
        intentUrl = getIntent().getStringExtra("newsLink");
        if (null != param && TextUtils.isEmpty(param.getNewsLink())) {
            iconShare.setVisibility(View.GONE);
        } else {
            iconShare.setVisibility(View.VISIBLE);
        }

        sharePopView = View.inflate(this, R.layout.share_pop_view, null);
        shareWxBtn = sharePopView.findViewById(R.id.share_wx_btn);
        shareWxBtn.setOnClickListener(this);
        shareCircleBtn = sharePopView.findViewById(R.id.share_circle_btn);
        shareCircleBtn.setOnClickListener(this);
        shareQqBtn = sharePopView.findViewById(R.id.share_qq_btn);
        shareQqBtn.setOnClickListener(this);
        getCfg();
        mBridgeWebView = new BridgeWebView(this);

        //登录
        findViewById(R.id.toLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WebActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        });
    }

    /**
     * 获取机构
     */
    private void getCfg() {
        OkGo.<MechanismModel>get(ApiConstants.getInstance().getCfg())
                .tag("cfg")
                .params("appId", "1818423")
                .execute(new JsonCallback<MechanismModel>(MechanismModel.class) {
                    @Override
                    public void onSuccess(Response<MechanismModel> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode().equals(success_code)) {
                            MechanismModel.DataDTO model = response.body().getData();
                            logoUrl = model.getLogo();
                            webUrl = model.getConfig().getListUrl();
                            initBridge();
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }

                    }

                    @Override
                    public void onError(Response<MechanismModel> response) {
                        super.onError(response);
                        if (null != response.body()) {
                            ToastUtils.showShort(response.body().getMessage());
                            return;
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }
                });
    }

//    @NonNull
//    @Override
//    protected ViewGroup getAgentWebParent() {
//        return (ViewGroup) this.findViewById(R.id.container);
//    }

    private WebViewClient getWebViewClient() {
        return new WebViewClient() {
            BridgeWebViewClient mBridgeWebViewClient = new BridgeWebViewClient(mBridgeWebView);

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (mBridgeWebViewClient.shouldOverrideUrlLoading(view, url)) {
                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (mBridgeWebViewClient.shouldOverrideUrlLoading(view, request.getUrl().toString())) {
                        return true;
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                String str1 = "javascript: window.userInfo = '";
                String str2 = "javascript: window.deviceId = '";
                String str3 = "javascript: window.appVersion = '";
                String str4 = "'";
                mAgentWeb.getJsAccessEntrace().callJs(str1 + PersonInfoManager.getInstance().getSzrmUserModel() + str4);
                mAgentWeb.getJsAccessEntrace().callJs(str2 + SystemUtil.getANDROID_ID() + str4);
                mAgentWeb.getJsAccessEntrace().callJs(str3 + getAppInfo() + str4);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mBridgeWebViewClient.onPageFinished(view, url);

            }

        };
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb != null && mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            userInfo = (SdkUserInfo.DataDTO) data.getExtras().getSerializable("userInfo");
            PersonInfoManager.getInstance().setUserId(userInfo.getLoginSysUserVo().getId());
            String userInfoStr = JSON.toJSONString(userInfo);
            PersonInfoManager.getInstance().setSzrmUserModel(userInfoStr);
            String str1 = "onAppLogin('";
            String str2 = "')";
            mAgentWeb.getJsAccessEntrace().callJs(str1 + userInfoStr + str2);
        }
    }


    private void initBridge() {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(container, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .useDefaultIndicator(-1, 2)
                .setWebViewClient(getWebViewClient())
                .setWebView(mBridgeWebView)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
//               .setDownloadListener(mDownloadListener) 4.0.0 删除该API
                .createAgentWeb()
                .ready()
                .go(intentUrl);

        if (null != mBridgeWebView) {
            setBridge();
//            /**
//             * 设置标题
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_SETTITLE, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    Log.e("WebActivity", "页面标题为：" + data);
//                    webTitle.setText(data);
//                    function.onCallBack("标题");
//                }
//            });
//
//            /**
//             * 返回
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_MONITORLIFECYCLE, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    finish();
//                }
//            });
//
//            /**
//             * 获取设备id
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_GETDEVICEID, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    function.onCallBack(SystemUtil.getANDROID_ID());
//                }
//            });
//
//            /**
//             * 获取用户信息
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_GETUSERINFO, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    if (null != userInfo) {
//                        String str = JSON.toJSONString(userInfo);
//                        function.onCallBack(str);
//                    }
//                }
//            });
//
//            /**
//             * 打开新的webview
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_JUMPTONATIVEPAGE, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    Intent intent = new Intent(WebActivity.this, EasyWebActivity.class);
//                    intent.putExtra("param", data);
//                    startActivity(intent);
//                }
//            });
//
//            /**
//             * 分享
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_SHARE, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    ShareInfo shareInfo = JSON.parseObject(data, ShareInfo.class);
//                    SdkInteractiveParam.getInstance().shared(shareInfo);
//                }
//            });
//
//            /**
//             * 保存图片
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_SAVEPHOTO, new BridgeHandler() {
//                @Override
//                public void handler(final String data, final CallBackFunction function) {
//                    String[] writePerMissionGrop = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
//                    new RxPermissions(WebActivity.this).request(writePerMissionGrop).subscribe(new Consumer<Boolean>() {
//                        @Override
//                        public void accept(Boolean aBoolean) throws Exception {
//                            if (aBoolean) {
//                                JSONObject jsonObject = JSON.parseObject(data);
//                                final String url = jsonObject.getString("url");
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        ImageUtils.saveBitmap2file(SavePhoto.getBitmap(url), WebActivity.this
//                                                , handler, function);
//                                    }
//                                }).start();
//                            } else {
//                                function.onCallBack("0");
//                                ToastUtils.showShort("请在设置中手动开启写入SD卡权限");
//                            }
//                        }
//                    });
//                }
//            });
//
//            /**
//             * 打开登录页
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_GOLOGING, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    SdkInteractiveParam.getInstance().toLogin();
//                }
//            });
//
//
//            /**
//             * 打开视频
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_OPENVIDEO, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    Intent intent = new Intent(WebActivity.this, VideoHomeActivity.class);
//                    JSONObject jsonObject = JSON.parseObject(data);
//                    intent.putExtra("contentId", jsonObject.getString("contentId"));
//                    startActivity(intent);
//                }
//            });
//
//            /**
//             * 获取手机信息
//             */
//            mBridgeWebView.registerHandler(Constants.SDK_JS_GETAPPVERSION, new BridgeHandler() {
//                @Override
//                public void handler(String data, CallBackFunction function) {
//                    AppSystemModel model = new AppSystemModel();
//                    model.setPlatform("Android");
//                    model.setDeviceBrand(SystemUtil.getDeviceBrand());
//                    model.setVersionCode(String.valueOf(SystemUtil.getVersionCode(WebActivity.this)));
//                    model.setVersionName(SystemUtil.getVersionName(WebActivity.this));
//                    String str = JSON.toJSONString(model);
//                    function.onCallBack(str);
//                }
//            });
        }

        handler = new Handler() {
            @Override
            public void dispatchMessage(@NonNull Message msg) {
                super.dispatchMessage(msg);
                int id = msg.what;
                if (id == 1) {
                    CallBackFunction callBackFunction = (CallBackFunction) msg.obj;
                    callBackFunction.onCallBack("1");
                } else {
                    CallBackFunction callBackFunction = (CallBackFunction) msg.obj;
                    callBackFunction.onCallBack("0");
                }
            }
        };
    }

    /**
     * 设置jsBridge
     */
    private void setBridge() {
        mBridgeWebView.registerHandler("MJBrigeHandler", new BridgeHandler() {
            @Override
            public void handler(final String data, final CallBackFunction function) {
                final JSONObject jsonObject = JSON.parseObject(data);
                String methodName = jsonObject.getString("methodName");
                dataObject = jsonObject.getJSONObject("data");
                if (TextUtils.equals(methodName, Constants.SDK_JS_SETTITLE)) { //设置标题
                    String title = dataObject.getString("title");
                    webTitle.setText(title);
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_MONITORLIFECYCLE)) { //返回
                    finish();
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_GETDEVICEID)) { //获取设备id
                    DeviceIdModel model = new DeviceIdModel();
                    model.setDeviceId(SystemUtil.getANDROID_ID());
                    String deviceIdStr = JSON.toJSONString(model);
                    function.onCallBack(deviceIdStr);
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_GETUSERINFO)) { //获取用户信息
                    String userInfoStr = PersonInfoManager.getInstance().getSzrmUserModel();
                    if (!TextUtils.isEmpty(userInfoStr)) {
                        function.onCallBack(userInfoStr);
                    }
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_JUMPTONATIVEPAGE)) { //跳转新webView
                    Intent intent = new Intent(WebActivity.this, WebActivity.class);
                    JumpToNativePageModel model = JSON.parseObject(JSON.toJSONString(dataObject), JumpToNativePageModel.class);
                    intent.putExtra("param", model);
                    startActivity(intent);
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_SHARE)) { //分享
                    sharePop();
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_SAVEPHOTO)) { //保存图片

                    String[] writePerMissionGrop = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    new RxPermissions(WebActivity.this).request(writePerMissionGrop).subscribe(new Consumer<Boolean>() {
                        @Override
                        public void accept(Boolean aBoolean) throws Exception {
                            if (aBoolean) {
                                final String url = dataObject.getString("url");
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ImageUtils.saveBitmap2file(SavePhoto.getBitmap(url), WebActivity.this
                                                , handler, function);
                                    }
                                }).start();
                            } else {
                                function.onCallBack("0");
                                ToastUtils.showShort("请在设置中手动开启写入SD卡权限");
                            }
                        }
                    });
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_GOLOGING)) { //跳转登录
//                    SdkInteractiveParam.getInstance().toLogin();
                    Intent intent = new Intent(WebActivity.this, LoginActivity.class);
                    startActivityForResult(intent, LOGIN_REQUEST_CODE);
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_OPENVIDEO)) { //打开视频
                    Intent intent = new Intent(WebActivity.this, VideoHomeActivity.class);
                    intent.putExtra("contentId", dataObject.getString("contentId"));
                    intent.putExtra("logoUrl", logoUrl);
                    startActivity(intent);
                } else if (TextUtils.equals(methodName, Constants.SDK_JS_GETAPPVERSION)) { //获取设备版本号等信息
                    function.onCallBack(getAppInfo());
                }
            }
        });
    }

    /**
     * 获取系统的一些信息
     *
     * @return
     */
    private String getAppInfo() {
        AppSystemModel appSystemModel = new AppSystemModel();
        appSystemModel.setOsName("Android");
        appSystemModel.setBrand(SystemUtil.getDeviceBrand());
        appSystemModel.setOsVersion(String.valueOf(SystemUtil.getVersionCode(WebActivity.this)));
        appSystemModel.setAppVersion(SystemUtil.getVersionName(WebActivity.this));
        String appInfo = JSON.toJSONString(appSystemModel);
        return appInfo;
    }

    /**
     * 分享弹窗
     */
    private void sharePop() {
        if (null == sharePop) {
            sharePop = new CustomPopWindow.PopupWindowBuilder(this)
                    .setView(sharePopView)
                    .setOutsideTouchable(true)
                    .setFocusable(true)
                    .size(AppInit.getContext().getResources().getDisplayMetrics().widthPixels, ButtonSpan.dip2px(150))
                    .setAnimationStyle(R.style.take_popwindow_anim)
                    .create()
                    .showAtLocation(container, Gravity.BOTTOM, 0, 0);
        } else {
            sharePop.showAtLocation(container, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgBack) {
            finish();
        } else if (id == R.id.share_wx_btn) {
            toShare();
        } else if (id == R.id.share_circle_btn) {
            toShare();
        } else if (id == R.id.share_qq_btn) {
            toShare();
        } else if (id == R.id.iconShare) {
            sharePop();
        }
    }

    /**
     * 分享
     */
    private void toShare() {
        String shareStr = JSON.toJSONString(dataObject);
        ShareInfo shareInfo = JSON.parseObject(shareStr, ShareInfo.class);
        SdkInteractiveParam.getInstance().shared(shareInfo);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if (null != mAgentWeb) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
        if (PersonInfoManager.getInstance().isRequestSzrmLogin()) {
            //需要去请求数智融媒的登录
            szrmLoginRequest();
        } else {
            //不需要请求数智融媒的登录
        }
        super.onResume();
    }

    private void szrmLoginRequest() {
        org.json.JSONObject jsonObject = new org.json.JSONObject();
        try {
            jsonObject.put("appId", "1");
            jsonObject.put("userId", "12345");
            jsonObject.put("mobile", "18684711216");
            jsonObject.put("headProfile", "https://oss.zhcs.csbtv.com/zhcs-prd/icon/WechatIMG180.png");
            jsonObject.put("nickName", "JSON");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<SdkUserInfo>post(ApiConstants.getInstance().getLoginParty())
                .tag("sdkLogin")
                .upJson(jsonObject)
                .execute(new JsonCallback<SdkUserInfo>() {
                    @Override
                    public void onSuccess(Response<SdkUserInfo> response) {
                        if (null == response.body().getData()) {
                            ToastUtils.showShort(com.wdcs.videodetail.demo.R.string.data_err);
                            return;
                        }
                        if (response.body().getCode().equals("200")) {
                            String token = response.body().getData().getToken();
                            SdkUserInfo.DataDTO.LoginSysUserVoDTO loginUserInfo = new SdkUserInfo.DataDTO.LoginSysUserVoDTO();
                            PersonInfoManager.getInstance().setTransformationToken(token);
                            PersonInfoManager.getInstance().setAppId(response.body().getData().getAppId());
                            PersonInfoManager.getInstance().setUserId(loginUserInfo.getId());
                            PersonInfoManager.getInstance().setPhoneNum(loginUserInfo.getPhone());
                            PersonInfoManager.getInstance().setNickName(loginUserInfo.getNickname());
                            PersonInfoManager.getInstance().setUserImageUrl(loginUserInfo.getHead());
                            String userModelStr = JSON.toJSONString(response.body().getData());
                            PersonInfoManager.getInstance().setSzrmUserModel(userModelStr);
                            Log.e(TAG, "数智融媒 登录成功");
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<SdkUserInfo> response) {
                        super.onError(response);
                        if (null != response.body()) {
                            ToastUtils.showShort(response.message());
                            return;
                        }
                        ToastUtils.showShort(com.wdcs.videodetail.demo.R.string.net_err);
                    }
                });
    }

}