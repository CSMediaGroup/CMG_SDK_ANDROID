package com.example.vdemo;


import static ui.activity.WebActivity.LOGIN_REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;
import java.util.List;

import common.callback.GetGdyTokenCallBack;
import common.callback.SdkInteractiveParam;
import common.callback.SdkParamCallBack;
import common.callback.VideoInteractiveParam;
import common.callback.VideoParamCallBack;
import common.http.ApiConstants;
import common.model.BuriedPointModel;
import common.model.SdkUserInfo;
import common.model.ShareInfo;
import common.model.ThirdUserInfo;
import common.utils.PersonInfoManager;
import common.utils.ToastUtils;
import event.SzrmRecommend;
import model.bean.SZContentModel;
import ui.activity.LoginActivity;
import ui.activity.TgtCodeActivity;
import ui.activity.VideoDetailActivity;
import ui.activity.VideoHomeActivity;
import ui.activity.WebActivity;
import utils.UUIDUtils;

public class MainActivity extends AppCompatActivity {
    private TextView tv;
    private EditText panelCode;
    private EditText contentId;
    private TextView fxsys;
    private TextView setCode;
    private TextView classList;
    private TextView others_home_page;
    private TextView clear_user_info;
    private Switch isRealease;
    private TextView uuid;
    private TextView test;
    private TextView toPageDetail;
    private TextView loadMoreData;
    private TextView changeUserInfo;
    private List<SZContentModel.DataDTO.ContentsDTO> contents = new ArrayList<>();
    private List<SZContentModel.DataDTO.ContentsDTO> loadMoreContents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = findViewById(R.id.tv);
        test = findViewById(R.id.test);
        panelCode = findViewById(R.id.panelid);
        panelCode.setText("48662");
        contentId = findViewById(R.id.contentid);
        fxsys = findViewById(R.id.fxsys);
        classList = findViewById(R.id.class_list);
        others_home_page = findViewById(R.id.others_home_page);
        isRealease = findViewById(R.id.isRealease);
        uuid = findViewById(R.id.uuid);
        uuid.setText("当前设备的uuid：" + UUIDUtils.deviceUUID());
        toPageDetail = findViewById(R.id.to_page_detail);
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

                /**
                 * 获取推荐列表
                 */
                SzrmRecommend.getInstance().requestContentList("10");
                SzrmRecommend.getInstance().contentsEvent.observe(MainActivity.this, new Observer<List<SZContentModel.DataDTO.ContentsDTO>>() {
                    @Override
                    public void onChanged(List<SZContentModel.DataDTO.ContentsDTO> contentsDTOS) {
                        contents = contentsDTOS;
                    }
                });

                SzrmRecommend.getInstance().loadMoreContentEvent.observe(MainActivity.this, new Observer<List<SZContentModel.DataDTO.ContentsDTO>>() {
                    @Override
                    public void onChanged(List<SZContentModel.DataDTO.ContentsDTO> contentsDTOS) {
                        loadMoreContents = contentsDTOS;
                    }
                });

            }
        });

        /**
         * 跳转新闻详情页
         */
        toPageDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (contents.isEmpty()) {
                    ToastUtils.showShort("先点击获取参数值");
                    return;
                }
                SzrmRecommend.getInstance().routeToDetailPage(MainActivity.this, contents.get(3));
            }
        });

        /**
         * 获取更多推荐列表内容
         */
        loadMoreData = findViewById(R.id.load_more_data);
        loadMoreData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SZContentModel.DataDTO.ContentsDTO contentsDTO = contents.get(contents.size() - 1);
                SzrmRecommend.getInstance().requestMoreContentList(contentsDTO, "10");
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
//                intent.putExtra("newsLink", "file:///android_asset/jsbridge/test.html");
                intent.putExtra("newsLink", "http://192.168.31.161:8081/news/index.html");
                startActivity(intent);
            }
        });


        findViewById(R.id.clear_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonInfoManager.getInstance().clearThirdUserToken();
                ToastUtils.showShort("清除用户信息");
            }
        });

        isRealease.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    ApiConstants.getInstance().setBaseUrl("https://fuse-api-gw.zhcs.csbtv.com/");
//                    appId = Constants.LIUYANG_JGH;
                } else {
                    ApiConstants.getInstance().setBaseUrl("https://uat-fuse-api-gw.zhcs.csbtv.com/");
//                    appId = Constants.YUEYANG_JGH;
                }
            }
        });

        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TgtCodeActivity.class);
                intent.putExtra("newsLink", "http://192.168.31.161:8081/news/index.html");
                startActivity(intent);
            }
        });

        changeUserInfo = findViewById(R.id.change_user_info);
        changeUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });


        SdkInteractiveParam.getInstance().setSdkCallBack(new SdkParamCallBack() {
            @Override
            public ThirdUserInfo setThirdUserInfo() {
                ThirdUserInfo thirdUserInfo = new ThirdUserInfo();
                thirdUserInfo.setUserId(PersonInfoManager.getInstance().getRequestUserId());
                thirdUserInfo.setNickName(PersonInfoManager.getInstance().getRequestUserNickName());
                thirdUserInfo.setPhoneNum(PersonInfoManager.getInstance().getRequestUserPhone());
                thirdUserInfo.setHeadImageUrl(PersonInfoManager.getInstance().getRequestUserHead());
                return thirdUserInfo;
            }

            @Override
            public void shared(ShareInfo shareInfo) {
                Log.e("share", JSON.toJSONString(shareInfo));
                if (null != shareInfo) {
                    ToastUtils.showShort("分享成功");
                }
            }

            @Override
            public void toLogin() {
                Log.e("toLogin", "toLogin");
                //这里是你跳转你的登录页面 去登录
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

//        /**
//         * 获取用户信息
//         */
//        SdkInteractiveParam.getInstance().userInfoEvent.observe(this, new Observer<ThirdUserInfo>() {
//            @Override
//            public void onChanged(ThirdUserInfo thirdUserInfo) {
//                PersonInfoManager.getInstance().setThirdUserId(thirdUserInfo.getUserId());
//                PersonInfoManager.getInstance().setThirdUserHead(thirdUserInfo.getHeadImageUrl());
//                PersonInfoManager.getInstance().setThirdUserNickName(thirdUserInfo.getNickName());
//                PersonInfoManager.getInstance().setThirdUserPhone(thirdUserInfo.getPhoneNum());
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}