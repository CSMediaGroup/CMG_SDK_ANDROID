package ui.activity;

import static common.utils.AppInit.appId;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.szrm.videodetail.demo.R;

import org.json.JSONException;
import org.json.JSONObject;

import common.callback.JsonCallback;
import common.callback.SdkInteractiveParam;
import common.callback.SdkParamCallBack;
import common.http.ApiConstants;
import common.model.SdkUserInfo;
import common.model.ShareInfo;
import common.model.TokenModel;
import common.utils.PersonInfoManager;
import common.utils.SPUtils;
import common.utils.ToastUtils;

public class LoginActivity extends AppCompatActivity {
    private TextView login;
    private EditText userIdEtv;
    private EditText tel;
    private EditText adress;
    private EditText nickNameEtv;
    private SdkUserInfo sdkUserInfo;
    private String userId;
    private String mobile;
    private String headProfile;
    private String nickName;
    private TextView signOutLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userIdEtv = findViewById(R.id.userId);
        tel = findViewById(R.id.tel);
        adress = findViewById(R.id.headProfile);
        nickNameEtv = findViewById(R.id.nickName);

        //登录
        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("appId", appId);
                    jsonObject.put("userId", userIdEtv.getText());
                    jsonObject.put("mobile", tel.getText());
                    jsonObject.put("headProfile", adress.getText());
                    jsonObject.put("nickName", nickNameEtv.getText());
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
                                    ToastUtils.showShort(com.szrm.videodetail.demo.R.string.data_err);
                                    return;
                                }
                                if (response.body().getCode().equals("200")) {
                                    String token = response.body().getData().getToken();
                                    PersonInfoManager.getInstance().setTransformationToken(token);
                                    Intent intent = new Intent();
                                    intent.putExtra("userInfo", response.body().getData());
                                    setResult(RESULT_OK, intent);
                                    ToastUtils.showShort("登录成功");
                                    finish();
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
                                ToastUtils.showShort(com.szrm.videodetail.demo.R.string.net_err);
                            }
                        });
            }
        });

        findViewById(R.id.signOutLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInfoManager.getInstance().clearThirdUserToken();
            }
        });
    }
}