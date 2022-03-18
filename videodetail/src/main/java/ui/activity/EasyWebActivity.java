package ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wdcs.videodetail.demo.R;

import common.model.JumpToNativePageModel;
import common.utils.ScreenUtils;

public class EasyWebActivity extends BaseAgentWebActivity {
    private LinearLayout container;
    private ImageView imgBack;
    private TextView webTitle;
    private ImageView iconShare;
    private JumpToNativePageModel param;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        param = (JumpToNativePageModel) getIntent().getSerializableExtra("param");
        setContentView(R.layout.activity_easy_web);
        initView();
    }

    private void initView() {
        ScreenUtils.fullScreen(this, true);
        ScreenUtils.setStatusBarColor(this, R.color.white);
        imgBack = findViewById(R.id.imgBack);
        webTitle = findViewById(R.id.webTitle);
        iconShare = findViewById(R.id.iconShare);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webTitle.setText(param.getTitle());
        if (TextUtils.isEmpty(param.getLink())) {
            iconShare.setVisibility(View.GONE);
        } else {
            iconShare.setVisibility(View.VISIBLE);
        }
    }

    @NonNull
    @Override
    protected ViewGroup getAgentWebParent() {
        return (ViewGroup) this.findViewById(R.id.container);
    }

    @Nullable
    @Override
    protected String getUrl() {
        return param.getNewsLink();
    }
}