package com.example.vdemo;


import static ui.activity.WebActivity.LOGIN_REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;

import common.callback.GetGdyTokenCallBack;
import common.callback.SdkInteractiveParam;
import common.callback.SdkParamCallBack;
import common.callback.VideoInteractiveParam;
import common.callback.VideoParamCallBack;
import common.model.BuriedPointModel;
import common.model.SdkUserInfo;
import common.model.ShareInfo;
import common.model.ThirdUserInfo;
import common.utils.PersonInfoManager;
import ui.activity.LoginActivity;
import ui.activity.VideoDetailActivity;
import ui.activity.VideoHomeActivity;
import ui.activity.WebActivity;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private EditText panelCode;
    private EditText contentId;
    private TextView fxsys;
    private TextView setCode;
    private TextView classList;
    private TextView login_activity;
    private TextView others_home_page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        panelCode = findViewById(R.id.panelid);
        panelCode.setText("48662");
        contentId = findViewById(R.id.contentid);
        fxsys = findViewById(R.id.fxsys);
        classList = findViewById(R.id.class_list);
        others_home_page = findViewById(R.id.others_home_page);
        login_activity = findViewById(R.id.login_activity);
        fxsys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                intent.putExtra("contentId", contentId.getText().toString());
                startActivity(intent);
            }
        });

        classList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                intent.putExtra("panelId", "mycs.video.video");
                intent.putExtra("classId", "10299204");
                intent.putExtra("contentId", contentId.getText().toString());
                intent.putExtra("category_name", "123456");
                startActivity(intent);
            }
        });

        setCode = findViewById(R.id.set_code);

        setCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VideoInteractiveParam.getInstance().setCallBack(new VideoParamCallBack() {
                    @Override
                    public void shared(ShareInfo shareInfo) {

                    }

                    @Override
                    public void Login() {

                    }

                    //https://testmycs.csbtv.com/accountapi/getUserInfoByTgt
                    @Override
                    public String setCode() {
                        return "9ae1ae3d-4a2f-425d-ade5-a8d972de8cc8";
                    }

                    @Override
                    public void recommedUrl(@NonNull String url, @Nullable ShareInfo shareInfo) {

                    }

                    @Override
                    public void trackingPoint(BuriedPointModel buriedPointModel) {

                    }

                    @Override
                    public String setDeviceId() {
                        return "998877665544332212";
                    }

                });

                VideoInteractiveParam.getInstance().setGdyTokenCallBack(new GetGdyTokenCallBack() {
                    @Override
                    public void checkLoginStatus(String gdyToken) {
                    }
                });

                SdkInteractiveParam.getInstance().setSdkCallBack(new SdkParamCallBack() {
                    @Override
                    public ThirdUserInfo setThirdUserInfo() {
                        ThirdUserInfo userInfo = new ThirdUserInfo();
                        userInfo.setUserId("123");
                        userInfo.setPhoneNum("123456");
                        userInfo.setNickName("测试人员");
                        userInfo.setHeadImageUrl("https://oss.zhcs.csbtv.com/zhcs-prd/icon/WechatIMG180.png");
                        return userInfo;
                    }

                    @Override
                    public void shared(ShareInfo shareInfo) {

                    }

                    @Override
                    public void toLogin() {

                    }
                });
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, VideoHomeActivity.class);
                intent.putExtra("panelId", panelCode.getText().toString());
                intent.putExtra("contentId", contentId.getText().toString());
                intent.putExtra("category_name", "123456");
                startActivity(intent);
            }
        });

        others_home_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, WebActivity.class);
//                intent.putExtra("newsLink", "https://www.baidu.com/");
//                intent.putExtra("newsLink", "https://uat-h5.zhcs.csbtv.com/sdk/news/#/");
//                intent.putExtra("newsLink", "file:///android_asset/jsbridge/demo.html");
                intent.putExtra("newsLink", "http://192.168.31.233:3000/home");
//                intent.putExtra("newsLink", "http://192.168.31.161:8081/news/index.html");
                startActivity(intent);
            }
        });

        login_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) {
            SdkUserInfo.DataDTO userInfo = (SdkUserInfo.DataDTO) data.getExtras().getSerializable("userInfo");
            PersonInfoManager.getInstance().setTransformationToken(userInfo.getToken());
            String userInfoStr = JSON.toJSONString(userInfo);
            PersonInfoManager.getInstance().setSzrmUserModel(userInfoStr);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}