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

import java.util.List;

import common.callback.JsonCallback;
import common.callback.SdkInteractiveParam;
import common.callback.SdkParamCallBack;
import common.http.ApiConstants;
import common.model.SdkUserInfo;
import common.model.ShareInfo;
import common.model.ThirdUserInfo;
import common.model.TokenModel;
import common.utils.PersonInfoManager;
import common.utils.SPUtils;
import common.utils.ToastUtils;
import event.SingleLiveEvent;

public class LoginActivity extends AppCompatActivity {
    private TextView login;
    private EditText userIdEtv;
    private EditText tel;
    private EditText adress;
    private EditText nickNameEtv;
    private TextView changeUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userIdEtv = findViewById(R.id.userId);
        tel = findViewById(R.id.tel);
        adress = findViewById(R.id.headProfile);
        nickNameEtv = findViewById(R.id.nickName);

        //登录
        findViewById(R.id.loginA).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PersonInfoManager.getInstance().setRequestUserId("123879419");
                PersonInfoManager.getInstance().setRequestUserHead("测试");
                PersonInfoManager.getInstance().setRequestUserNickName("loginA");
                PersonInfoManager.getInstance().setRequestUserPhone("123131231");
                ToastUtils.showShort("登录成功");
                finish();
            }
        });

        findViewById(R.id.loginB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PersonInfoManager.getInstance().setRequestUserId("123879419");
                PersonInfoManager.getInstance().setRequestUserHead("测试");
                PersonInfoManager.getInstance().setRequestUserNickName("loginB");
                PersonInfoManager.getInstance().setRequestUserPhone("123131231");
                ToastUtils.showShort("登录成功");
                finish();
            }
        });

    }
}