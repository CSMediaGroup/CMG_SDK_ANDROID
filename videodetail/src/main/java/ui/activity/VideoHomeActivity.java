package ui.activity;

import static android.widget.RelativeLayout.BELOW;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.example.zhouwei.library.CustomPopWindow;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;
import com.szrm.videodetail.demo.R;
import com.tencent.rtmp.TXLiveConstants;
import com.szrm.videodetail.demo.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import adpter.CommentPopRvAdapter;
import adpter.VideoViewPagerAdapter;
import adpter.XkshVideoAdapter;
import common.callback.JsonCallback;
import common.callback.SdkInteractiveParam;
import common.constants.Constants;
import common.http.ApiConstants;
import common.manager.BuriedPointModelManager;
import common.manager.ContentBuriedPointManager;
import common.manager.OnViewPagerListener;
import common.manager.ViewPagerLayoutManager;
import common.model.CategoryModel;
import common.model.CollectionLabelModel;
import common.model.ColumnModel;
import common.model.CommentLv1Model;
import common.model.ContentStateModel;
import common.model.DataDTO;
import common.model.RecommendModel;
import common.model.ReplyLv2Model;
import common.model.SdkUserInfo;
import common.model.ThirdUserInfo;
import common.model.TokenModel;
import common.model.TrackingUploadModel;
import common.model.VideoChannelModel;
import common.model.VideoDetailModel;
import common.model.VideoOneModel;
import common.utils.ButtonSpan;
import common.utils.DateUtils;
import common.utils.KeyboardUtils;
import common.utils.NoScrollViewPager;
import common.utils.NumberFormatTool;
import common.utils.PersonInfoManager;
import common.utils.SPUtils;
import common.utils.ScreenUtils;
import common.utils.SoftKeyBoardListener;
import common.utils.ToastUtils;
import common.utils.AppInit;
import flyco.tablayout.SlidingTabLayout;
import flyco.tablayout.listener.OnTabSelectListener;
import model.bean.ActivityRuleBean;
import tencent.liteav.demo.superplayer.SuperPlayerDef;
import tencent.liteav.demo.superplayer.SuperPlayerModel;
import tencent.liteav.demo.superplayer.SuperPlayerView;
import tencent.liteav.demo.superplayer.contants.Contants;
import tencent.liteav.demo.superplayer.model.SuperPlayerImpl;
import tencent.liteav.demo.superplayer.model.utils.SystemUtils;
import tencent.liteav.demo.superplayer.ui.view.PointSeekBar;
import ui.fragment.VideoDetailFragment;
import ui.fragment.XkshFragment;
import utils.NetworkUtil;
import widget.CollectionClickble;
import widget.CustomLoadMoreView;
import widget.LoadingView;
import widget.NetBroadcastReceiver;


import org.json.JSONException;
import org.json.JSONObject;

import static common.constants.Constants.TRACKINGUPLOAD;
import static common.constants.Constants.VIDEOTAG;
import static common.constants.Constants.success_code;
import static common.constants.Constants.token_error;
import static common.utils.AppInit.appId;
import static common.utils.SPUtils.isVisibleNoWifiView;
import static common.utils.ShareUtils.toShare;
import static tencent.liteav.demo.superplayer.SuperPlayerView.instance;
import static tencent.liteav.demo.superplayer.SuperPlayerView.mTargetPlayerMode;
import static tencent.liteav.demo.superplayer.ui.player.AbsPlayer.formattedTime;
import static tencent.liteav.demo.superplayer.ui.player.WindowPlayer.mDuration;
import static tencent.liteav.demo.superplayer.ui.player.WindowPlayer.mProgress;
import static ui.activity.WebActivity.szrmLoginRequest;
import static utils.NetworkUtil.setDataWifiState;

public class VideoHomeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView videoBack;
    private RelativeLayout videoTitleView;
    private SuperPlayerView playerView;

    private TranslateAnimation translateAniLeftShow, translateAniLeftHide;
    public CustomPopWindow noLoginTipsPop;
    private View noLoginTipsView;
    private TextView noLoginTipsCancel;
    private TextView noLoginTipsOk;
    public String contentId;
    public static double maxPercent = 0; //?????????????????????
    public static long lsDuration = 0; //??????????????????????????????????????????
    private NetBroadcastReceiver netWorkStateReceiver;
    private String categoryName;
    public static boolean isPause;
    private List<CategoryModel.DataDTO> categoryModelList = new ArrayList<>();
    private RecyclerView videoDetailRv;
    public XkshVideoAdapter adapter;
    private CommentPopRvAdapter commentPopRvAdapter;
    //??????????????????
    public List<DataDTO> mDatas = new ArrayList<>();
    //??????????????????
    private List<MultiItemEntity> mCommentPopRvData;
    private List<CommentLv1Model.DataDTO.RecordsDTO> mCommentPopDtoData;
    private ImageView videoStaticBg;
    private ImageView startPlay;

    public RelativeLayout videoDetailCommentBtn;
    //??????????????????
    public CustomPopWindow popupWindow;
    private LinearLayout videoDetailLikes;

    private View contentView;
    private View chooseContentView;
    private RelativeLayout dismissPop;
    private RecyclerView commentPopRv;
    private TextView commentEdtInput;
    private RelativeLayout commentPopRl;
    //???????????????????????????????????????
    public CustomPopWindow inputAndSendPop;
    private View sendPopContentView;
    private View rootView;
    private LinearLayout edtParent;
    private EditText edtInput;
    private TextView tvSend;
    private RelativeLayout videoDetailWhiteCommentRl;
    //??????????????????
    public CustomPopWindow choosePop;

    public ViewPagerLayoutManager xkshManager;
    private ImageView choosePopDismiss;
    public SmartRefreshLayout refreshLayout;
    private String transformationToken = "";
    private String panelCode = "";
    private String recordContentId;//???????????????id
    private boolean initialize = true;
    private int mVideoSize = 15; //?????????????????????
    private int mPageIndex = 1; //??????????????????
    private int mPageSize = 10; //???????????????????????????
    public String myContentId = ""; //??????????????????id
    public int currentIndex = 0; //?????????????????????????????????
    private TextView commentTotal;
    private TextView commentPopCommentTotal;

    private ImageView videoDetailLikesImage; //????????????
    private TextView likesNum; //?????????
    private String videoType; //????????????
    public String playUrl;
    private TextView commentEdittext;
    private String videoTag = "videoTag";
    private String recommendTag = "recommend";
    private boolean isLoadComplate = false;
    private BaseQuickAdapter.RequestLoadMoreListener requestLoadMoreListener;
    public View decorView;
    private SoftKeyBoardListener softKeyBoardListener;
    private LinearLayout share;
    public CustomPopWindow sharePop;
    private View sharePopView;
    private ImageView shareWxBtn;
    private ImageView shareCircleBtn;
    private ImageView shareQqBtn;
    public DataDTO mDataDTO;
    private List<RecommendModel.DataDTO.RecordsDTO> recommondList;
    public ViewGroup rlLp;
    private VideoChannelModel videoChannelModel;

    private VideoChannelModel channelModel;
    private Bundle args;
    private SlidingTabLayout mVideoTab;
    private LoadingView loadingProgress;
    private RelativeLayout.LayoutParams lp;
    public static boolean isFollow; //????????????
    public LinearLayout fullLin;
    public double pointPercent;// ???????????????????????????????????????
    //    private long everyOneDuration; //?????????????????????????????????????????? ????????????????????????
    public ActivityRuleBean activityRuleBean;
    private RelativeLayout.LayoutParams playViewParams;
    private View footView;
    public long xkshOldSystemTime;
    public long xkshReportTime;
    private boolean isReply = false;
    private String replyId;
    private List<CollectionLabelModel.DataDTO> collectionList;
    private List<String> collectionTvList;
    private List<String> collectionStrList;
    private View footerView;
    private String logoUrl;
    private String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        SystemUtils.setNavbarColor(this, R.color.video_black);
        setContentView(R.layout.activity_video_main);
        initView();
        getOneVideo();
    }

    private void initView() {
        contentId = getIntent().getStringExtra("contentId");
        categoryName = getIntent().getStringExtra("category_name");
        logoUrl = getIntent().getStringExtra("logoUrl");
        appName = getIntent().getStringExtra("appName");
        videoBack = findViewById(R.id.video_back);
        videoBack.setOnClickListener(this);
        videoTitleView = findViewById(R.id.video_title_view);
        noLoginTipsView = View.inflate(this, R.layout.no_login_tips, null);
        noLoginTipsCancel = noLoginTipsView.findViewById(R.id.no_login_tips_cancel);
        noLoginTipsOk = noLoginTipsView.findViewById(R.id.no_login_tips_ok);
        refreshLayout = findViewById(R.id.refreshLayout);

        noLoginTipsCancel.setOnClickListener(this);
        noLoginTipsOk.setOnClickListener(this);
        playerView = SuperPlayerView.getInstance(this, getWindow().getDecorView(), true);
        footerView = View.inflate(this, R.layout.footer_view, null);
        if (NetworkUtil.isWifi(this)) {
            SPUtils.getInstance().put("net_state", "0");
        } else {
            SPUtils.getInstance().put("net_state", "1");
        }

        if (netWorkStateReceiver == null) {
            netWorkStateReceiver = new NetBroadcastReceiver();
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkStateReceiver, filter);


        /**
         * ??????????????????
         */
        SdkInteractiveParam.getInstance().userInfoEvent.observe(this, new Observer<ThirdUserInfo>() {
            @Override
            public void onChanged(ThirdUserInfo thirdUserInfo) {
                PersonInfoManager.getInstance().setThirdUserId(thirdUserInfo.getUserId());
                PersonInfoManager.getInstance().setThirdUserHead(thirdUserInfo.getHeadImageUrl());
                PersonInfoManager.getInstance().setThirdUserNickName(thirdUserInfo.getNickName());
                PersonInfoManager.getInstance().setThirdUserPhone(thirdUserInfo.getPhoneNum());
            }
        });

        //?????????????????????
        if (null != playerView && null != playerView.mFullScreenPlayer) {
            playerView.mFullScreenPlayer.mSeekBarProgress.setOnSeekBarChangeListener(new PointSeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(PointSeekBar seekBar, int progress, boolean fromUser) {

                    if (playerView.mFullScreenPlayer.mGestureVideoProgressLayout != null && fromUser) {
                        playerView.mFullScreenPlayer.mGestureVideoProgressLayout.show();
                        float percentage = ((float) progress) / seekBar.getMax();
                        float currentTime = (mDuration * percentage);
                        playerView.mFullScreenPlayer.mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime) + " / " + formattedTime((long) mDuration));
                        playerView.mFullScreenPlayer.mGestureVideoProgressLayout.setProgress(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(PointSeekBar seekBar) {
                    if (null == playerView) {
                        return;
                    }
                    if (null == playerView.mFullScreenPlayer) {
                        return;
                    }
                    playerView.mFullScreenPlayer.removeCallbacks(playerView.mFullScreenPlayer.mHideViewRunnable);
                }

                @Override
                public void onStopTrackingTouch(PointSeekBar seekBar) {
                    int curProgress = seekBar.getProgress();
                    int maxProgress = seekBar.getMax();

                    switch (playerView.mFullScreenPlayer.mPlayType) {
                        case VOD:
                            if (curProgress >= 0 && curProgress <= maxProgress) {
                                // ??????????????????
                                playerView.mFullScreenPlayer.toggleView(playerView.mFullScreenPlayer.mLayoutReplay, false);
                                float percentage = ((float) curProgress) / maxProgress;
                                long duration = (long) (percentage * mDuration);
                                lsDuration = duration;
                                if (percentage > maxPercent) {
                                    maxPercent = percentage;
                                }

                                int position = (int) (mDuration * percentage);

                                if (playerView.mFullScreenPlayer.mControllerCallback != null) {
                                    playerView.mFullScreenPlayer.mControllerCallback.onSeekTo(position);
                                    playerView.mFullScreenPlayer.mControllerCallback.onResume();
                                }
                            }
                            break;
                    }
                    playerView.mFullScreenPlayer.postDelayed(playerView.mFullScreenPlayer.mHideViewRunnable, Contants.delayMillis);
                }
            });
        }

        if (null != playerView && null != playerView.mWindowPlayer) {
            //???????????????
            playerView.mWindowPlayer.mSeekBarProgress.setOnSeekBarChangeListener(new PointSeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(PointSeekBar seekBar, int progress, boolean fromUser) {
                    if (null == playerView) {
                        return;
                    }
                    if (null == playerView.mWindowPlayer) {
                        return;
                    }
                    if (playerView.mWindowPlayer.mGestureVideoProgressLayout != null && fromUser) {
                        playerView.mWindowPlayer.mGestureVideoProgressLayout.show();
                        float percentage = ((float) progress) / seekBar.getMax();
                        float currentTime = (mDuration * percentage);
                        playerView.mWindowPlayer.mGestureVideoProgressLayout.setTimeText(formattedTime((long) currentTime) + " / " + formattedTime((long) mDuration));
                        playerView.mWindowPlayer.mGestureVideoProgressLayout.setProgress(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(PointSeekBar seekBar) {
                    if (null == playerView) {
                        return;
                    }
                    if (null == playerView.mWindowPlayer) {
                        return;
                    }
                    playerView.mWindowPlayer.removeCallbacks(playerView.mWindowPlayer.mHideViewRunnable);
                    xkshManager.setCanScoll(false);
                }

                @Override
                public void onStopTrackingTouch(PointSeekBar seekBar) {
                    int curProgress = seekBar.getProgress();
                    int maxProgress = seekBar.getMax();
                    if (mTargetPlayerMode == SuperPlayerDef.PlayerMode.WINDOW) {
                        xkshManager.setCanScoll(true);
                    } else {
                        xkshManager.setCanScoll(false);
                    }

                    switch (playerView.mWindowPlayer.mPlayType) {
                        case VOD:
                            if (curProgress >= 0 && curProgress <= maxProgress) {
                                // ??????????????????
                                playerView.mWindowPlayer.toggleView(playerView.mWindowPlayer.mLayoutReplay, false);
                                float percentage = ((float) curProgress) / maxProgress;
                                long duration = (long) (percentage * mDuration);
                                lsDuration = duration;
                                if (percentage > maxPercent) {
                                    maxPercent = percentage;
                                }
                                int position = (int) (mDuration * percentage);

                                if (playerView.mWindowPlayer.mControllerCallback != null) {
                                    playerView.mWindowPlayer.mControllerCallback.onSeekTo(position);
                                    playerView.mWindowPlayer.mControllerCallback.onResume();
                                }
                            }
                            break;
                    }
                    playerView.mWindowPlayer.postDelayed(playerView.mWindowPlayer.mHideViewRunnable, Contants.delayMillis);
                }
            });
        }

        /**
         * ???????????????????????????????????????
         */
        playerView.playModeCallBack = new SuperPlayerView.PlayModeCallBack() {
            @Override
            public void getPlayMode(SuperPlayerDef.PlayerMode playerMode) {
                LinearLayout fullLin = (LinearLayout) adapter.getViewByPosition(currentIndex, R.id.superplayer_iv_fullscreen);
                if (playerMode.equals(SuperPlayerDef.PlayerMode.FULLSCREEN)) {
                    xkshManager.setCanScoll(false);
                    refreshLayout.setEnableRefresh(false);
                    adapter.setEnableLoadMore(false);

                    videoBack.setVisibility(View.GONE);
                    if (null != popupWindow) {
                        popupWindow.dissmiss();
                    }


                    if (null != inputAndSendPop) {
                        inputAndSendPop.dissmiss();
                    }

                    if (null != choosePop) {
                        choosePop.dissmiss();
                    }

                    if (null != noLoginTipsPop) {
                        noLoginTipsPop.dissmiss();
                    }

                    if (null != sharePop) {
                        sharePop.dissmiss();
                    }

                    if (null != videoDetailCommentBtn) {
                        videoDetailCommentBtn.setVisibility(View.GONE);
                    }

                    if (null != videoTitleView) {
                        videoTitleView.setVisibility(View.GONE);
                    }
                    if (null != adapter.getViewByPosition(currentIndex, R.id.introduce_lin)) {
                        adapter.getViewByPosition(currentIndex, R.id.introduce_lin).setVisibility(View.GONE);
                    }

                    if (null != fullLin) {
                        fullLin.setVisibility(View.GONE);
                    }


                    if (null != adapter.getViewByPosition(currentIndex, R.id.horizontal_video_wdcs_logo)) {
                        adapter.getViewByPosition(currentIndex, R.id.horizontal_video_wdcs_logo).setVisibility(View.GONE);
                    }

                    if (null != adapter.getViewByPosition(currentIndex, R.id.cover_picture)) {
                        adapter.getViewByPosition(currentIndex, R.id.cover_picture).setVisibility(View.GONE);
                    }

                    KeyboardUtils.hideKeyboard(getWindow().getDecorView());
                } else if (playerMode.equals(SuperPlayerDef.PlayerMode.WINDOW)) {
                    xkshManager.setCanScoll(true);
                    refreshLayout.setEnableRefresh(true);
                    adapter.setEnableLoadMore(true);
                    videoBack.setVisibility(View.VISIBLE);
                    setLikeCollection(playerView.contentStateModel);
                    if (null != videoDetailCommentBtn) {
                        videoDetailCommentBtn.setVisibility(View.VISIBLE);
                    }

                    if (null != fullLin) {
                        fullLin.setVisibility(View.VISIBLE);
                    }

                    if (null != adapter.getViewByPosition(currentIndex, R.id.introduce_lin)) {
                        adapter.getViewByPosition(currentIndex, R.id.introduce_lin).setVisibility(View.VISIBLE);
                    }


                    if (null != adapter.getViewByPosition(currentIndex, R.id.horizontal_video_wdcs_logo)) {
                        adapter.getViewByPosition(currentIndex, R.id.horizontal_video_wdcs_logo).setVisibility(View.VISIBLE);
                    }

                    if (null != adapter.getViewByPosition(currentIndex, R.id.cover_picture)) {
                        adapter.getViewByPosition(currentIndex, R.id.cover_picture).setVisibility(View.VISIBLE);
                    }

                }
                if (null != videoTitleView) {
                    videoTitleView.setVisibility(View.VISIBLE);
                }
            }
        };

        //??????????????????
        SuperPlayerImpl.setReadPlayCallBack(new SuperPlayerImpl.ReadPlayCallBack() {
            @Override
            public void ReadPlayCallback() {
                if (null == playerView.buriedPointModel.getXksh_renew() || TextUtils.equals("false", playerView.buriedPointModel.getXksh_renew())) {
//                    //????????????
                    xkshOldSystemTime = DateUtils.getTimeCurrent();
                    String event;
                    if (TextUtils.equals(mDataDTO.getIsAutoReportEvent(), "1")) {
                        event = Constants.CMS_VIDEO_PLAY;
                    } else {
                        event = Constants.CMS_VIDEO_PLAY_AUTO;
                    }
                    String contentId = "";
                    if (TextUtils.isEmpty(mDataDTO.getThirdPartyId())) {
                        contentId = String.valueOf(mDataDTO.getId());
                    } else {
                        contentId = mDataDTO.getThirdPartyId();
                    }
                    uploadBuriedPoint(ContentBuriedPointManager.setContentBuriedPoint(VideoHomeActivity.this, contentId, "", "", event, mDataDTO.getVolcCategory(), mDataDTO.getRequestId(), appName), event);
                }

            }
        });

        //????????????/??????????????? ??????????????????
        SuperPlayerImpl.setAutoPlayOverCallBack(new SuperPlayerImpl.AutoPlayOverCallBack() {
            @Override
            public void AutoPlayOverCallBack() {
                if (!isPause) {
                    Log.e("yqh_yqh", "???????????????" + SuperPlayerImpl.mCurrentPlayVideoURL);
                    playerView.mSuperPlayer.reStart();
                }
            }
        });
        translateAnimation();

        share = findViewById(R.id.share);
        share.setOnClickListener(this);
        mDataDTO = new DataDTO();
        recommondList = new ArrayList<>();
        loadingProgress = findViewById(R.id.xksh_loading_progress);
        loadingProgress.setVisibility(View.VISIBLE);
        commentEdittext = findViewById(R.id.comment_edittext);
        videoDetailRv = findViewById(R.id.video_detail_rv);
        videoDetailRv.setHasFixedSize(true);
        videoDetailLikesImage = findViewById(R.id.video_detail_likes_image);
        likesNum = findViewById(R.id.likes_num);

        xkshManager = new ViewPagerLayoutManager(this);
        videoDetailRv.setLayoutManager(xkshManager);
        footView = View.inflate(this, R.layout.footer_view, null);
        setSoftKeyBoardListener();
        xkshManager.setOnViewPagerListener(new OnViewPagerListener() {


            @Override
            public void onInitComplete() {
                if (initialize) {
                    return;
                }
                initialize = true;

                if (mDatas.isEmpty()) {
                    return;
                }
                mDataDTO = mDatas.get(0);

                if (null != adapter.getViewByPosition(0, R.id.superplayer_iv_fullscreen)) {
                    if (TextUtils.equals("2", videoIsNormal(Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(0).getWidth())),
                            Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(0).getHeight()))))) {
                        adapter.getViewByPosition(0, R.id.superplayer_iv_fullscreen).setVisibility(View.VISIBLE);
                    } else {
                        adapter.getViewByPosition(0, R.id.superplayer_iv_fullscreen).setVisibility(View.GONE);
                    }
                }

//                playerView = SuperPlayerView.getInstance(getActivity(), decorView);
                playerView.mWindowPlayer.setDataDTO(mDataDTO, mDataDTO);
//                playerView.mWindowPlayer.setViewpager((NoScrollViewPager) getActivity().findViewById(R.id.video_vp));
                playerView.mWindowPlayer.setIsTurnPages(false);
                playerView.mWindowPlayer.setManager(xkshManager);
                playerView.mFullScreenPlayer.setDataDTO(mDataDTO);
                myContentId = String.valueOf(mDatas.get(0).getId());
                addPageViews(myContentId);
                OkGo.getInstance().cancelTag("contentState");
                getContentState(myContentId);
                String localUserId = PersonInfoManager.getInstance().getUserId();
                String userId = mDataDTO.getCreateBy();
                if (TextUtils.isEmpty(mDataDTO.getIssuerId()) || TextUtils.equals(localUserId, userId)) {
                    adapter.getViewByPosition(0, R.id.follow).setVisibility(View.GONE);
                } else {
                    adapter.getViewByPosition(0, R.id.follow).setVisibility(View.VISIBLE);
                }

                SuperPlayerImpl.mCurrentPlayVideoURL = mDatas.get(0).getPlayUrl();
                if (isVisibleNoWifiView(VideoHomeActivity.this)) {
                    playerView.setOrientation(false);
                } else {
                    playerView.setOrientation(true);
                }
                currentIndex = 0;
                mPageIndex = 1;
                if (mDatas.get(0).getDisableComment()) {
                    videoDetailWhiteCommentRl.setEnabled(false);
                    commentPopRl.setEnabled(false);
                    commentEdittext.setText("????????????");
                    commentEdtInput.setHint("????????????");
                } else {
                    videoDetailWhiteCommentRl.setEnabled(true);
                    commentPopRl.setEnabled(true);
                    commentEdittext.setText("?????????...");
                    commentEdtInput.setHint("?????????...");
                }
                getCommentList("1", String.valueOf(mPageSize), true);
                videoType = mDatas.get(0).getType();
                rlLp = (ViewGroup) xkshManager.findViewByPosition(0);
                OkGo.getInstance().cancelTag(recommendTag);
                //??????????????????
                getRecommend(myContentId, 0);
//                initChoosePop();
            }

            @Override
            public void onPageRelease(boolean isNext, int position) {
            }

            @Override
            public void onPageSelected(final int position, boolean isBottom) {
                if (null == playerView) {
                    return;
                }

                if (null != playerView.getTag() && position == (int) playerView.getTag()) {
                    return;
                }
                //????????????
                if (mDatas.isEmpty()) {
                    return;
                }
                if (null == mDatas.get(position)) {
                    return;
                }

                playerView.mWindowPlayer.hide();

                if (mDuration != 0 && mProgress != 0) {
                    //????????????
                    long evePlayTime = Math.abs(mProgress - lsDuration);
                    double currentPercent = (evePlayTime * 1.0 / mDuration);
                    double uploadPercent = 0;
                    if (null == playerView.buriedPointModel.getXksh_renew() || TextUtils.equals("false", playerView.buriedPointModel.getXksh_renew())) {
                        //????????????
                        if (currentPercent > maxPercent) {
                            uploadPercent = currentPercent;
                            maxPercent = currentPercent;
                        } else {
                            uploadPercent = maxPercent;
                        }
                    } else {
                        uploadPercent = 1;
                    }
                    xkshReportTime = DateUtils.getTimeCurrent() - xkshOldSystemTime;
                    BigDecimal two = new BigDecimal(uploadPercent);
                    double pointPercentTwo = two.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
                    String contentId = "";
                    if (TextUtils.isEmpty(mDataDTO.getThirdPartyId())) {
                        contentId = String.valueOf(mDataDTO.getId());
                    } else {
                        contentId = mDataDTO.getThirdPartyId();
                    }
                    uploadBuriedPoint(ContentBuriedPointManager.setContentBuriedPoint(VideoHomeActivity.this, contentId, String.valueOf(xkshReportTime), String.valueOf(Math.floor(pointPercentTwo * 100)), Constants.CMS_VIDEO_OVER_AUTO, mDataDTO.getVolcCategory(), mDataDTO.getRequestId(), appName), Constants.CMS_VIDEO_OVER_AUTO);
                    Log.e("xksh_md", "???????????????" + Constants.CMS_VIDEO_OVER_AUTO + "????????????:" + xkshReportTime + "---" + "???????????????:" + pointPercentTwo);
                }

                mDataDTO = mDatas.get(position);

                if (isVisibleNoWifiView(VideoHomeActivity.this)) {
                    playerView.setOrientation(false);
                } else {
                    playerView.setOrientation(true);
                }

                if (null != adapter.getViewByPosition(position, R.id.superplayer_iv_fullscreen)) {
                    if (TextUtils.equals("2", videoIsNormal(Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(position).getWidth())),
                            Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(position).getHeight()))))) {
                        adapter.getViewByPosition(position, R.id.superplayer_iv_fullscreen).setVisibility(View.VISIBLE);
                    } else {
                        adapter.getViewByPosition(position, R.id.superplayer_iv_fullscreen).setVisibility(View.GONE);
                    }
                }


                //??????????????????
                if (null != playerView && null != playerView.buriedPointModel) {
                    playerView.buriedPointModel.setXksh_renew("false");
                }
                //????????????????????????????????????
                playerView.mWindowPlayer.setRecordDuration(0);
                lsDuration = 0;
                maxPercent = 0;
                SuperPlayerImpl.mCurrentPlayVideoURL = mDatas.get(position).getPlayUrl();
                playUrl = mDatas.get(position).getPlayUrl();
                playerView.mWindowPlayer.setDataDTO(mDataDTO, mDatas.get(currentIndex));
                playerView.mFullScreenPlayer.setDataDTO(mDataDTO);
                playerView.mWindowPlayer.setIsTurnPages(true);
                playerView.mFullScreenPlayer.setIsTurnPages(true);
                currentIndex = position;
                reset();
                myContentId = String.valueOf(mDatas.get(position).getId());

                addPageViews(myContentId);
                videoType = mDatas.get(position).getType();
                mPageIndex = 1;
                if (mDatas.get(position).getDisableComment()) {
                    videoDetailWhiteCommentRl.setEnabled(false);
                    commentPopRl.setEnabled(false);
                    commentEdittext.setText("????????????");
                    commentEdtInput.setHint("????????????");
                } else {
                    videoDetailWhiteCommentRl.setEnabled(true);
                    commentPopRl.setEnabled(true);
                    commentEdittext.setText("?????????...");
                    commentEdtInput.setHint("?????????...");
                }
                getCommentList("1", String.valueOf(mPageSize), true);
                getContentState(myContentId);

                String localUserId = PersonInfoManager.getInstance().getUserId();
                String userId = mDataDTO.getCreateBy();
                if (TextUtils.isEmpty(mDataDTO.getIssuerId()) || TextUtils.equals(localUserId, userId)) {
                    adapter.getViewByPosition(position, R.id.follow).setVisibility(View.GONE);
                } else {
                    adapter.getViewByPosition(position, R.id.follow).setVisibility(View.VISIBLE);
                }

                rlLp = (ViewGroup) xkshManager.findViewByPosition(position);
                OkGo.getInstance().cancelTag(recommendTag);
                getRecommend(myContentId, position);

                if (!"1".equals(playerView.mFullScreenPlayer.strSpeed)) {
                    playerView.mFullScreenPlayer.mVodMoreView.mCallback.onSpeedChange(1.0f);
                    playerView.mFullScreenPlayer.superplayerSpeed.setText("??????");
                    playerView.mFullScreenPlayer.mRbSpeed1.setChecked(true);
                }
            }
        });

        initSmartRefresh();
        commentTotal = findViewById(R.id.comment_total);
        videoDetailLikes = findViewById(R.id.video_detail_likes);
        videoDetailLikes.setOnClickListener(this);

        contentView = View.inflate(this, R.layout.fragment_video_comment_pop, null);
        sendPopContentView = View.inflate(this, R.layout.layout_input_window, null);
        commentPopCommentTotal = contentView.findViewById(R.id.comment_pop_comment_total);
        edtParent = sendPopContentView.findViewById(R.id.edt_parent);
        edtInput = sendPopContentView.findViewById(R.id.edtInput);
        tvSend = sendPopContentView.findViewById(R.id.tvSend);

        noLoginTipsView = View.inflate(this, R.layout.no_login_tips, null);
        noLoginTipsCancel = noLoginTipsView.findViewById(R.id.no_login_tips_cancel);
        noLoginTipsOk = noLoginTipsView.findViewById(R.id.no_login_tips_ok);
        noLoginTipsCancel.setOnClickListener(this);
        noLoginTipsOk.setOnClickListener(this);

        sharePopView = View.inflate(this, R.layout.share_pop_view, null);
        shareWxBtn = sharePopView.findViewById(R.id.share_wx_btn);
        shareWxBtn.setOnClickListener(this);
        shareCircleBtn = sharePopView.findViewById(R.id.share_circle_btn);
        shareCircleBtn.setOnClickListener(this);
        shareQqBtn = sharePopView.findViewById(R.id.share_qq_btn);
        shareQqBtn.setOnClickListener(this);

        /**
         * ????????????
         */
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtInput.getText())) {
                    ToastUtils.showShort("???????????????");
                } else {
                    if (isReply) {
                        toReply(replyId);
                    } else {
                        toComment(edtInput.getText().toString(), myContentId);
                    }
                    edtInput.setText("");
                }
            }
        });

        rootView = findViewById(R.id.root);

        dismissPop = contentView.findViewById(R.id.dismiss_pop);
        dismissPop.setOnClickListener(this);
        commentPopRv = contentView.findViewById(R.id.comment_pop_rv);
        commentEdtInput = contentView.findViewById(R.id.comment_edtInput);
        commentPopRl = contentView.findViewById(R.id.comment_pop_rl);
        commentPopRl.setOnClickListener(this);
        initCommentPopRv();
        videoDetailCommentBtn = findViewById(R.id.video_detail_comment_btn);
        videoDetailCommentBtn.setOnClickListener(this);
        videoDetailWhiteCommentRl = findViewById(R.id.video_detail_white_comment_rl);
        videoDetailWhiteCommentRl.setOnClickListener(this);
        adapter = new XkshVideoAdapter(R.layout.xksh_video_item_layout, mDatas, this,
                playerView, refreshLayout, videoDetailCommentBtn, xkshManager, logoUrl);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.setPreLoadNumber(2);
        adapter.openLoadAnimation();
        adapter.setOnLoadMoreListener(requestLoadMoreListener, videoDetailRv);
        /**
         * ???wifi ??????????????????
         */
        adapter.setToAddPlayerViewClick(new XkshVideoAdapter.ToAddPlayerViewClick() {
            @Override
            public void clickNoWifi(int position) {
                SPUtils.getInstance().put(Constants.AGREE_NETWORK, "1");
                for (int i = 0; i < mDatas.size(); i++) {
                    if (null != mDatas.get(i)) {
                        mDatas.get(i).setWifi(true);
                    }
                }

                for (int i = 0; i < mDatas.size(); i++) {
                    if (null != mDatas.get(i)) {
                        mDatas.get(i).setWifi(true);
                    }
                }
                addPlayView(position);
                adapter.notifyDataSetChanged();
            }
        });

        /**
         * ????????????
         */
        adapter.setFollowViewClick(new XkshVideoAdapter.FollowViewClick() {
            @Override
            public void followClick(int position) {
                if (TextUtils.isEmpty(PersonInfoManager.getInstance().getTransformationToken())) {
                    noLoginTipsPop();
                } else {
                    if (isFollow) {
                        //????????????????????????
                        cancelFollow(mDatas.get(position).getCreateBy());
                    } else {
                        //??????????????????
                        toFollow(mDatas.get(position).getCreateBy());
                    }
                }
            }
        });

        videoDetailRv.setAdapter(adapter);

    }

    //????????????
    private void translateAnimation() {
        //????????????????????????
        translateAniLeftShow = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF??????????????????
                0,//fromXValue???????????????X?????????
                Animation.RELATIVE_TO_SELF,
                1,//fromXValue???????????????X?????????
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue???????????????Y?????????
                Animation.RELATIVE_TO_SELF,
                0);//fromXValue???????????????Y?????????
        translateAniLeftShow.setRepeatMode(Animation.REVERSE);
        translateAniLeftShow.setDuration(500);

        //????????????????????????
        translateAniLeftHide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF??????????????????
                1,//fromXValue???????????????X?????????
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue???????????????X?????????
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue???????????????Y?????????
                Animation.RELATIVE_TO_SELF,
                0);//fromXValue???????????????Y?????????
        translateAniLeftHide.setRepeatMode(Animation.REVERSE);
        translateAniLeftHide.setDuration(500);
    }

    /**
     * ?????????playview?????????????????????view
     *
     * @param position
     */
    public void addPlayView(final int position) {
        if (isVisibleNoWifiView(VideoHomeActivity.this)) {
            return;
        }

        if (null != playerView.mWindowPlayer && null != playerView.mWindowPlayer.mLayoutBottom && null != playerView.mWindowPlayer.mLayoutBottom.getParent()) {
            ((ViewGroup) playerView.mWindowPlayer.mLayoutBottom.getParent()).removeView(playerView.mWindowPlayer.mLayoutBottom);
        }

        if (null != playerView && null != playerView.getParent()) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
        }
        LinearLayout linearLayout = (LinearLayout) adapter.getViewByPosition(currentIndex, R.id.introduce_lin);
        RelativeLayout.LayoutParams mLayoutBottomParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        RelativeLayout itemRelativelayout = (RelativeLayout) adapter.getViewByPosition(position, R.id.item_relativelayout);

        String videoType = videoIsNormal(Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(position).getWidth())),
                Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(position).getHeight())));
        if (TextUtils.equals("0", videoType)) {
            double percent = Double.parseDouble(mDatas.get(position).getWidth()) / Double.parseDouble(mDatas.get(position).getHeight());
            double mHeight;
            mHeight = getResources().getDisplayMetrics().widthPixels / percent;
            playViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) mHeight);
            playViewParams.removeRule(RelativeLayout.ALIGN_PARENT_TOP);
            playViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            playViewParams.setMargins(0, 0, 0, 0);
            playerView.mSuperPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);

//            int height = (int) (Integer.parseInt(mDatas.get(position).getWidth()) / Constants.Portrait_Proportion);


            playerView.setOrientation(false);
            if (linearLayout != null) {
                linearLayout.addView(playerView.mWindowPlayer.mLayoutBottom, 0);
            }
        } else if (TextUtils.equals("1", videoType)) {
            int height = (int) (ScreenUtils.getPhoneWidth(this) / Constants.Portrait_Proportion);
            playViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            if (phoneIsNormal()) {
                playViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                playerView.mSuperPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
                playerView.setOrientation(false);
            } else {
                playViewParams.addRule(RelativeLayout.ABOVE, videoDetailCommentBtn.getId());
                playerView.mSuperPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
                playerView.setOrientation(false);
            }
            if (linearLayout != null) {
                linearLayout.addView(playerView.mWindowPlayer.mLayoutBottom, 0);
            }
        } else {
            int height = (int) (ScreenUtils.getPhoneWidth(this) / Constants.Horizontal_Proportion);
            playViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            playViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            playerView.mSuperPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            playerView.setOrientation(true);
            mLayoutBottomParams.addRule(BELOW, playerView.getId());
            mLayoutBottomParams.setMargins(0, (AppInit.getContext().getResources().getDisplayMetrics().heightPixels / 2) + ButtonSpan.dip2px(120), 0, 0);
            playerView.mWindowPlayer.mLayoutBottom.setLayoutParams(mLayoutBottomParams);
            if (null != itemRelativelayout) {
                itemRelativelayout.addView(playerView.mWindowPlayer.mLayoutBottom);
            }
        }
        playerView.setLayoutParams(playViewParams);
        playerView.setTag(position);

        if (rlLp != null) {
            rlLp.addView(playerView, 1);
            //???????????????
            String contentId = "";
            if (TextUtils.isEmpty(mDataDTO.getThirdPartyId())) {
                contentId = String.valueOf(mDataDTO.getId());
            } else {
                contentId = mDataDTO.getThirdPartyId();
            }
            uploadBuriedPoint(ContentBuriedPointManager.setContentBuriedPoint(this, contentId, "", "", Constants.CMS_CLIENT_SHOW, mDataDTO.getVolcCategory(), mDataDTO.getRequestId(), appName), Constants.CMS_CLIENT_SHOW);
            play(mDatas.get(position).getPlayUrl(), mDatas.get(position).getTitle());
        }
    }

    private void initSmartRefresh() {
        refreshLayout = findViewById(R.id.refreshLayout);
        refreshLayout.setEnableScrollContentWhenLoaded(true);
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (playerView.mSuperPlayer.getPlayerState() == SuperPlayerDef.PlayerState.PLAYING) {
                    playerView.mSuperPlayer.pause();
                }
                isLoadComplate = false;
                adapter.setOnLoadMoreListener(requestLoadMoreListener, videoDetailRv);
                getOneVideo();
                //??????????????????
                if (null != playerView && null != playerView.buriedPointModel) {
                    playerView.buriedPointModel.setXksh_renew("false");
                }
            }
        });
        requestLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
//                if (!isLoadComplate) {
//                    videoDetailRv.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (mDatas.isEmpty()) {
//                                adapter.loadMoreFail();
//                                return;
//                            }
//                            getRandomVideoList();
//                        }
//                    });
//                }
            }
        };
    }

    /**
     * ?????????????????????
     */
    private void initCommentPopRv() {
        mCommentPopRvData = new ArrayList<>();
        mCommentPopDtoData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentPopRv.setLayoutManager(linearLayoutManager);
        commentPopRvAdapter = new CommentPopRvAdapter(mCommentPopRvData, this);
        commentPopRvAdapter.bindToRecyclerView(commentPopRv);
        commentPopRvAdapter.setEmptyView(R.layout.comment_list_empty);
        commentPopRvAdapter.expandAll();
        commentPopRv.setAdapter(commentPopRvAdapter);
        commentPopRvAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                commentPopRv.post(new Runnable() {
                    @Override
                    public void run() {
                        mPageIndex++;
                        getCommentList(String.valueOf(mPageIndex), String.valueOf(mPageSize), false);
                    }
                });
            }
        }, commentPopRv);

    }


    /**
     * ?????????????????????
     */
    private void showCommentPopWindow() {
        if (null == popupWindow) {
            //???????????????popWindow
            popupWindow = new CustomPopWindow.PopupWindowBuilder(this)
                    .setView(contentView)
                    .setOutsideTouchable(false)
                    .setFocusable(true)
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
                    .size(AppInit.getContext().getResources().getDisplayMetrics().widthPixels, AppInit.getContext().getResources().getDisplayMetrics().heightPixels - ButtonSpan.dip2px(200))
                    .setAnimationStyle(R.style.take_popwindow_anim)
                    .create()
                    .showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        } else {
            popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
//        SystemUtils.hideBottomUIMenuForPopupWindow(popupWindow);
        popupWindow.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                SystemUtils.hideSystemUI(decorView);
            }
        });

    }

    /**
     * ????????????
     *
     * @param playUrl
     */
    public void play(String playUrl, String title) {
//        if (null == playUrl || TextUtils.isEmpty(playUrl)) {
//            ToastUtils.showShort("??????????????????(" + playUrl + "),?????????????????????");
//            return;
//        }
        if (null != playerView) {
//            videoStaticBg.setVisibility(View.GONE);
            SuperPlayerModel model = new SuperPlayerModel();
            model.url = playUrl;
            model.title = title;
            model.contentId = myContentId;
            playerView.playWithModel(model);
        }
    }

    public void stop() {
        if (null != playerView && null != playerView.getParent()) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
        }
        playerView.stopPlay();
    }

    public void reset() {
        if (null != playerView && null != playerView.getParent()) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
        }

        if (null != playerView) {
            playerView.resetPlayer();
        }
    }

    /**
     * ?????????????????????
     */
    private void setSoftKeyBoardListener() {
        softKeyBoardListener = new SoftKeyBoardListener(this);
        //?????????????????????
        softKeyBoardListener.setListener(new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                //?????????????????????????????????
                Log.e("yqh", "?????????????????????,?????????");
            }

            @Override
            public void keyBoardHide(int height) {
                //?????????????????????,?????????
//                SystemUtils.hideSystemUI(decorView);
                if (null != inputAndSendPop) {
                    inputAndSendPop.getPopupWindow().dismiss();
                }
                Log.e("yqh", "?????????????????????,?????????");
            }
        });
    }

    public void setLikeCollection(ContentStateModel.DataDTO contentStateModel) {
        if (contentStateModel.getWhetherLike().equals("true")) {
            videoDetailLikesImage.setImageResource(R.drawable.szrm_sdk_favourite_select);
        } else {
            videoDetailLikesImage.setImageResource(R.drawable.szrm_sdk_favourite);
        }

        if (contentStateModel.getLikeCountShow().equals("0")) {
            likesNum.setText("???");
        } else {
            likesNum.setText(NumberFormatTool.formatNum(Long.parseLong(NumberFormatTool.getNumStr(contentStateModel.getLikeCountShow())), false));
        }

        TextView followView = (TextView) adapter.getViewByPosition(currentIndex, R.id.follow);
        if (null != followView) {
            if (contentStateModel.getWhetherFollow().equals("true")) {
                followView.setBackgroundResource(R.drawable.followed_bg);
                followView.setText("?????????");
                isFollow = true;
            } else {
                adapter.getViewByPosition(currentIndex, R.id.follow).setBackgroundResource(R.drawable.follow_bg);
                followView.setText("??????");
                isFollow = false;
            }
        }
    }

    /**
     * ???????????????16???9
     * 0 :  ???????????????16???9
     * 1 ???  ????????????16???9
     * 2 ???  ????????????
     */
    private String videoIsNormal(int videoWidth, int videoHeight) {
        if (videoWidth == 0 && videoHeight == 0) {
            return "2";
        }
        if (videoWidth > videoHeight) {
            //??????
            if (videoWidth * 9 == videoHeight * 16) {
                return "2";
            } else {
                return "0";
            }
        } else {
            //??????
            if (videoHeight * 9 == videoWidth * 16) {
                return "1";
            } else {
                return "0";
            }
        }
    }

    /**
     * ???????????????16???9
     *
     * @return
     */
    private boolean phoneIsNormal() {
        int phoneWidth = ScreenUtils.getPhoneWidth(this);
        int phoneHeight = ScreenUtils.getPhoneHeight(this);
        if (phoneHeight * 9 == phoneWidth * 16) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (playerView != null && null != mDataDTO && !SPUtils.isVisibleNoWifiView(this)) {
            SuperPlayerImpl.mCurrentPlayVideoURL = mDataDTO.getPlayUrl();
            if (playerView.homeVideoIsLoad) {
                playerView.mSuperPlayer.resume();
            } else {
                playerView.mSuperPlayer.reStart();
            }
        }
        isPause = false;
        xkshOldSystemTime = DateUtils.getTimeCurrent();
        if (!TextUtils.isEmpty(myContentId)) {
            getContentState(myContentId);
        }

        if (PersonInfoManager.getInstance().isRequestSzrmLogin()) {
            try {
                szrmLoginRequest(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (null != playerView && playerView.mSuperPlayer.getPlayerMode() == SuperPlayerDef.PlayerMode.FULLSCREEN) {
            SystemUtils.hideSystemUI(decorView);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (playerView == null) {
            return;
        }
        playerView.mSuperPlayer.pause();
        isPause = true;
        if (null == mDataDTO) {
            return;
        }
        if (playerView.mWindowPlayer.mCurrentPlayState != SuperPlayerDef.PlayerState.END) {
            if (mDuration != 0 && mProgress != 0) {
                /**
                 * ?????????????????? ??????????????????
                 */
                String event = Constants.CMS_VIDEO_OVER_AUTO;
                long evePlayTime = Math.abs(mProgress - lsDuration);
                double currentPercent = evePlayTime * 1.0 / mDuration;
                double uploadPercent = 0;
                if (null == playerView.buriedPointModel.getXksh_renew() || TextUtils.equals("false", playerView.buriedPointModel.getXksh_renew())) {
//                      //????????????
                    if (currentPercent > maxPercent) {
                        uploadPercent = currentPercent;
                        maxPercent = currentPercent;
                    } else {
                        uploadPercent = maxPercent;
                    }
                } else {
                    uploadPercent = 1;
                }

                xkshReportTime = DateUtils.getTimeCurrent() - xkshOldSystemTime;
                BigDecimal two = new BigDecimal(uploadPercent);
                double pointPercentTwo = two.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
                lsDuration = mProgress;
                //????????????
                String contentId = "";
                if (TextUtils.isEmpty(mDataDTO.getThirdPartyId())) {
                    contentId = String.valueOf(mDataDTO.getId());
                } else {
                    contentId = mDataDTO.getThirdPartyId();
                }
                uploadBuriedPoint(ContentBuriedPointManager.setContentBuriedPoint(this, contentId, String.valueOf(xkshReportTime), String.valueOf(Math.floor(pointPercentTwo * 100)), event, mDataDTO.getVolcCategory(), mDataDTO.getRequestId(), appName), event);
                Log.e("xksh_md", "???????????????" + event + "????????????:" + xkshReportTime + "---" + "???????????????:" + pointPercentTwo);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (instance != null) {
            instance.release();
            instance.mSuperPlayer.destroy();
            instance = null;
        }
        OkGo.getInstance().cancelAll();
        maxPercent = 0;
        lsDuration = 0;
        unregisterReceiver(netWorkStateReceiver);
//        OkGo.getInstance().cancelTag(VIDEOTAG);
    }

    /**
     * ????????????
     */
    private void getOneVideo() {
        if (null != playerView && null != playerView.getParent()) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
        }
        mDatas.clear();
        OkGo.<VideoOneModel>get(ApiConstants.getInstance().getVideoDetailUrl() + contentId)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new JsonCallback<VideoOneModel>(VideoOneModel.class) {
                    @Override
                    public void onSuccess(Response<VideoOneModel> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode().equals(success_code)) {
                            if (null == response.body().getData()) {
                                ToastUtils.showShort(R.string.data_err);
                                return;
                            }

                            mDatas.add(response.body().getData());
                            for (int i = 0; i < mDatas.size(); i++) {
                                String videoType = videoIsNormal(Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(i).getWidth())),
                                        Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(i).getHeight())));
                                mDatas.get(i).setLogoType(videoType);
                            }
                            setDataWifiState(mDatas, VideoHomeActivity.this);
                            adapter.setNewData(mDatas);
                            if (mDatas.size() > 0) {
                                initialize = false;
                                recordContentId = String.valueOf(mDatas.get(mDatas.size() - 1).getId());
                            }
                            videoDetailCommentBtn.setVisibility(View.VISIBLE);
                        } else {
                            videoDetailCommentBtn.setVisibility(View.GONE);
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Response<VideoOneModel> response) {
                        if (null != response.body()) {
                            ToastUtils.showShort(response.body().getMessage());
                            return;
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    /**
     * ??????????????????
     */
    private void getRandomVideoList() {
        OkGo.<VideoDetailModel>get(ApiConstants.getInstance().queryRandomVideoList())
                .cacheMode(CacheMode.NO_CACHE)
                .params("pageSize", "15")
                .params("org_id", appId)
                .execute(new JsonCallback<VideoDetailModel>(VideoDetailModel.class) {
                    @Override
                    public void onSuccess(Response<VideoDetailModel> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode().equals(success_code)) {
                            if (response.body().getCode().equals(success_code)) {
                                if (null == response.body().getData()) {
                                    isLoadComplate = true;
                                    ToastUtils.showShort(R.string.data_err);
                                    return;
                                }

                                if (response.body().getData().size() == 0) {
                                    adapter.loadMoreEnd(false);
//                                    adapter.setOnLoadMoreListener(null, videoDetailRv);
//                                    if (null != footerView && null != footerView.getParent()) {
//                                        ((ViewGroup) footerView.getParent()).removeView(footerView);
//                                    }
//                                    adapter.addFooterView(footerView);

                                    isLoadComplate = true;
                                } else {
                                    adapter.setOnLoadMoreListener(requestLoadMoreListener, videoDetailRv);
                                    isLoadComplate = false;
                                    mDatas.addAll(response.body().getData());
                                    setDataWifiState(mDatas, VideoHomeActivity.this);
                                    adapter.loadMoreComplete();
                                }
//                            adapter.setNewData(mDatas);
                                adapter.loadMoreComplete();

                            } else {
                                adapter.loadMoreFail();
                            }
                        } else {

                        }
                    }

                    @Override
                    public void onError(Response<VideoDetailModel> response) {
                        super.onError(response);
                        if (null != response.body()) {
                            ToastUtils.showShort(response.body().getMessage());
                            return;
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }
                });
    }


//    /**
//     * ??????????????????
//     */
//    private void loadMoreData(String url, String contentId, String panelCode, String removeFirst, String refreshType) {
//        String deviceId = "";
//        try {
//            deviceId = param.getDeviceId();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        OkGo.<VideoOneModel>get(url)
//                .tag(VIDEOTAG)
//                .params("contentId", contentId)
//                .params("pageSize", 10)
//                .params("panelCode", panelCode)
//                .params("removeFirst", removeFirst)
//                .params("ssid", deviceId)
//                .params("refreshType", refreshType)
//                .params("type", "")
//                .cacheMode(CacheMode.NO_CACHE)
//                .execute(new JsonCallback<VideoOneModel>(VideoOneModel.class) {
//                    @Override
//                    public void onSuccess(Response<VideoOneModel> response) {
//                        if (null == response.body()) {
//                            isLoadComplate = true;
//                            ToastUtils.showShort(R.string.data_err);
//                            return;
//                        }
//
//                        if (response.body().getCode().equals(success_code)) {
//                            if (null == response.body().getData()) {
//                                isLoadComplate = true;
//                                ToastUtils.showShort(R.string.data_err);
//                                return;
//                            }
//
////                            if (response.body().getData() == 0) {
////                                Log.e("loadMoreData", "?????????????????????");
////                                adapter.loadMoreComplete();
////                                adapter.setOnLoadMoreListener(null, videoDetailRv);
////                                if (null != footView && null != footView.getParent()) {
////                                    ((ViewGroup) footView.getParent()).removeView(footView);
////                                }
////                                adapter.addFooterView(footView);
////                                isLoadComplate = true;
////                                return;
////                            } else {
////                                adapter.setOnLoadMoreListener(requestLoadMoreListener, videoDetailRv);
////                                isLoadComplate = false;
////                            }
//                            mDatas.add(response.body().getData());
//                            setDataWifiState(mDatas, VideoHomeActivity.this);
////                            adapter.setNewData(mDatas);
//                            recordContentId = String.valueOf(mDatas.get(mDatas.size() - 1).getId());
//                            Log.e("loadMoreData", "loadMoreData========" + mDatas.size());
//                            adapter.loadMoreComplete();
//                        } else {
//                            adapter.loadMoreFail();
//                        }
//                    }
//
//                    @Override
//                    public void onError(Response<VideoOneModel> response) {
//                        if (null != response.body()) {
//                            ToastUtils.showShort(response.body().getMessage());
//                            return;
//                        }
//                        ToastUtils.showShort(R.string.net_err);
//                    }
//
//                    @Override
//                    public void onFinish() {
//                        super.onFinish();
//                        refreshLayout.setEnableRefresh(true);
//                    }
//                });
//    }

    /**
     * ?????????+1
     */
    private void addPageViews(String contentId) {
        OkGo.<String>post(ApiConstants.getInstance().addViews() + contentId)
                .tag(VIDEOTAG)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            Log.e("yqh", jsonObject.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            ToastUtils.showShort(jsonObject.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    /**
     * ????????????????????????
     */
    public void getContentState(String contentId) {
        OkGo.<ContentStateModel>get(ApiConstants.getInstance().queryStatsData())
                .tag(VIDEOTAG)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .params("contentId", contentId)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new JsonCallback<ContentStateModel>(ContentStateModel.class) {
                    @Override
                    public void onSuccess(Response<ContentStateModel> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode().equals(success_code)) {
                            if (null == response.body().getData()) {
                                ToastUtils.showShort(R.string.data_err);
                                return;
                            }

                            playerView.contentStateModel = response.body().getData();
                            if (null != playerView.contentStateModel) {
                                setLikeCollection(playerView.contentStateModel);
                                playerView.setContentStateModel(myContentId, videoType);
                            }
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<ContentStateModel> response) {
                        if (null != response.body()) {
                            ToastUtils.showShort(response.body().getMessage());
                            return;
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }
                });
    }

    /**
     * ????????????????????????
     */
    public void getThematicCollection(String contentId) {
        OkGo.<CollectionLabelModel>get(ApiConstants.getInstance().getCollectToVideo() + contentId)
                .tag(VIDEOTAG)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new JsonCallback<CollectionLabelModel>(CollectionLabelModel.class) {
                    @Override
                    public void onSuccess(Response<CollectionLabelModel> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode().equals(success_code)) {
                            if (null == response.body().getData()) {
                                return;
                            }
                            collectionList = new ArrayList<>();
                            collectionTvList = new ArrayList<>();
                            collectionStrList = new ArrayList<>();
                            String collectionStr = "";
                            collectionList.addAll(response.body().getData());
                            for (int i = 0; i < collectionList.size(); i++) {
                                collectionStr = collectionStr + collectionList.get(i).getTitle();
                                collectionStrList.add(collectionList.get(i).getTitle());
                                if (i == collectionList.size() - 1) {
                                    collectionTvList.add("  " + collectionList.get(i).getTitle());
                                } else {
                                    if (i == 0) {
                                        collectionTvList.add("  " + collectionList.get(i).getTitle() + "???");
                                    } else {
                                        collectionTvList.add(collectionList.get(i).getTitle() + "???");
                                    }

                                }
                            }

                            TextView foldTextView = (TextView) adapter.getViewByPosition(currentIndex, R.id.fold_text);
                            TextView expendTextView = (TextView) adapter.getViewByPosition(currentIndex, R.id.expend_text);
                            String brief = "";
                            String spaceStr = "";
                            DataDTO item = adapter.getItem(currentIndex);
                            if (null == item) {
                                return;
                            }
                            if (TextUtils.isEmpty(adapter.getItem(currentIndex).getBrief())) {
                                brief = item.getTitle();
                            } else {
                                brief = item.getBrief();
                            }
                            SpannableStringBuilder builder = new SpannableStringBuilder();
                            if (collectionList.isEmpty()) {
                                return;
                            } else {
                                for (int i = 0; i < collectionList.size(); i++) {
                                    ImageSpan imgSpan = new ImageSpan(VideoHomeActivity.this,
                                            R.drawable.szrm_sdk_collection_image,
                                            ImageSpan.ALIGN_CENTER);
                                    final String str = collectionTvList.get(i);
                                    final String strChun = collectionStrList.get(i);
                                    SpannableString sp = new SpannableString(str);
                                    if (i == 0) {
                                        sp.setSpan(imgSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    }
                                    final String classId = String.valueOf(collectionList.get(i).getId());
                                    /**
                                     * ?????????????????????????????????
                                     */
                                    sp.setSpan(new CollectionClickble(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //????????????????????????
                                            Intent intent = new Intent(VideoHomeActivity.this, VideoDetailActivity.class);
                                            intent.putExtra("classId", classId);
                                            intent.putExtra("className", strChun.trim());
                                            intent.putExtra("logoUrl", logoUrl);
                                            intent.putExtra("appName", appName);
                                            startActivity(intent);
                                        }
                                    }, VideoHomeActivity.this), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    if (i == collectionList.size() - 1) {
                                        builder.append(sp);
                                        builder.append("  " + brief);
                                    } else {
                                        builder.append(sp);
                                    }
                                }
                                foldTextView.setMovementMethod(LinkMovementMethod.getInstance());
                                foldTextView.setText(builder);
                                expendTextView.setMovementMethod(LinkMovementMethod.getInstance());
                                expendTextView.setText(builder);
                            }
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<CollectionLabelModel> response) {
                        super.onError(response);
                        if (null != response.body()) {
                            ToastUtils.showShort(response.body().getMessage());
                            return;
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    /**
     * ??????????????????
     */
    public void getCommentList(String pageIndex, String pageSize, final boolean isRefresh) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contentId", myContentId);
            jsonObject.put("pageIndex", pageIndex);
            jsonObject.put("pageSize", pageSize);
            jsonObject.put("pcommentId", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<CommentLv1Model>post(ApiConstants.getInstance().getCommentWithReply())
                .tag(VIDEOTAG)
                .upJson(jsonObject)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new JsonCallback<CommentLv1Model>(CommentLv1Model.class) {
                    @Override
                    public void onSuccess(Response<CommentLv1Model> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode().equals("200")) {
                            if (null == response.body().getData()) {
                                ToastUtils.showShort(R.string.data_err);
                                return;
                            }

                            if (isRefresh) {
                                mCommentPopRvData.clear();
                                mCommentPopDtoData.clear();
                            }

                            //????????????
                            List<CommentLv1Model.DataDTO.RecordsDTO> lv1List = response.body().getData().getRecords();
                            for (int i = 0; i < lv1List.size(); i++) {
                                CommentLv1Model.DataDTO.RecordsDTO lv1Model = lv1List.get(i);
                                lv1Model.setPosition(i);
                                lv1Model.setShow(true);
                                List<ReplyLv2Model.ReplyListDTO> lv2List = lv1Model.getReply().getReplyList();
                                for (int j = 0; j < lv2List.size(); j++) {
                                    ReplyLv2Model.ReplyListDTO lv2Model = lv2List.get(j);
                                    lv2Model.setPosition(j);
                                    lv2Model.setParentPosition(i);
                                    lv1Model.addSubItem(lv2Model);
                                }
                                mCommentPopRvData.add(lv1Model);
                            }

                            mCommentPopDtoData.addAll(lv1List);
                            commentPopRvAdapter.setContentId(myContentId);
                            commentPopRvAdapter.setSrc(mCommentPopRvData);
                            commentPopRvAdapter.setNewData(mCommentPopRvData);

                            //?????????????????????
                            commentPopRvAdapter.setLv1CommentClick(new CommentPopRvAdapter.Lv1CommentClick() {
                                @Override
                                public void Lv1Comment(String id, String replyName) {
                                    toSetHint(id, replyName);
                                }
                            });

                            //????????????????????????????????????
                            commentPopRvAdapter.setLv1No1Click(new CommentPopRvAdapter.Lv1No1Click() {
                                @Override
                                public void lv1No1Click(String id, String replyName) {
                                    toSetHint(id, replyName);
                                }
                            });

                            //????????????????????????????????????
                            commentPopRvAdapter.setLv1No2Click(new CommentPopRvAdapter.Lv1No2Click() {
                                @Override
                                public void lv1No2Click(String id, String replyName) {
                                    toSetHint(id, replyName);
                                }
                            });

                            //?????????????????????
                            commentPopRvAdapter.setLv2ReplyClick(new CommentPopRvAdapter.Lv2ReplyClick() {
                                @Override
                                public void Lv2ReplyClick(String id, String replyName) {
                                    toSetHint(id, replyName);
                                }
                            });


                            if (mCommentPopDtoData.isEmpty()) {
                                commentTotal.setText("(0)");
                                commentPopCommentTotal.setText("(0)");
                            } else {
                                commentTotal.setText("(" + response.body().getData().getTotal() + ")");
                                commentPopCommentTotal.setText("(" + response.body().getData().getTotal() + ")");
                            }

                            if (response.body().getData().getRecords().size() == 0) {
                                commentPopRvAdapter.loadMoreEnd();
                            } else {
                                commentPopRvAdapter.loadMoreComplete();
                            }

                        } else {
                            commentPopRvAdapter.loadMoreFail();
                        }

                    }

                    @Override
                    public void onError(Response<CommentLv1Model> response) {
                        commentPopRvAdapter.loadMoreFail();
                    }
                });
    }

    private void toSetHint(String id, String replyName) {
        if (TextUtils.isEmpty(PersonInfoManager.getInstance().getTransformationToken())) {
            try {
                noLoginTipsPop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            KeyboardUtils.toggleSoftInput(getWindow().getDecorView());
            showInputEdittextAndSend();
            edtInput.setHint("??????@" + replyName);
            isReply = true;
            replyId = id;
        }
    }

    /**
     * ???????????????????????????
     */
    private void showInputEdittextAndSend() {
        //???????????????popWindow
        if (null == inputAndSendPop) {
            inputAndSendPop = new CustomPopWindow.PopupWindowBuilder(this)
                    .setView(sendPopContentView)
                    .setOutsideTouchable(false)
                    .setFocusable(true)
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
                    .setAnimationStyle(R.style.take_popwindow_anim)
                    .size(AppInit.getContext().getResources().getDisplayMetrics().widthPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .create()
                    .showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        } else {
            inputAndSendPop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
        edtInput.setFocusable(true);
        edtInput.setFocusableInTouchMode(true);
        edtInput.requestFocus();
    }

    /**
     * ??????
     */
    private void toComment(String content, String contentId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contentId", contentId);
            jsonObject.put("content", content);
            jsonObject.put("title", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(ApiConstants.getInstance().addComment())
                .tag(VIDEOTAG)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .upJson(jsonObject)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }
                        try {
                            JSONObject mJsonObject = new JSONObject(response.body());
                            String code = mJsonObject.get("code").toString();

                            if (code.equals(success_code)) {
                                if (!mDatas.isEmpty()) {
                                    String jsonString = BuriedPointModelManager.getVideoComment(myContentId, mDatas.get(currentIndex).getTitle(), "", "",
                                            "", "", mDatas.get(currentIndex).getIssueTimeStamp(), Constants.CONTENT_TYPE);
                                    Log.e("??????", "???????????????---" + jsonString);
                                }
                                ToastUtils.showShort("??????????????????????????????????????????");
                                if (null != inputAndSendPop) {
                                    inputAndSendPop.dissmiss();
                                }
                                KeyboardUtils.hideKeyboard(getWindow().getDecorView());
                                mPageIndex = 1;
                                getCommentList("1", String.valueOf(mPageSize), true);
                            } else if (code.equals(token_error)) {
                                Log.e("addComment", "???token ????????????");
                                try {
                                    SdkInteractiveParam.getInstance().toLogin();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (null != mJsonObject.getString("message")) {
                                    ToastUtils.showShort(mJsonObject.getString("message"));
                                } else {
                                    ToastUtils.showShort("????????????");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showShort("????????????");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtils.showShort("????????????");
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        edtInput.setText("");
                    }
                });
    }

    /**
     * ??????
     */
    private void toReply(String id) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id", id);
            jsonObject.put("reply", edtInput.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(ApiConstants.getInstance().addUserReply())
                .tag(VIDEOTAG)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .upJson(jsonObject)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }
                        try {
                            JSONObject mJsonObject = new JSONObject(response.body());
                            String code = mJsonObject.get("code").toString();

                            if (code.equals(success_code)) {
                                ToastUtils.showShort("??????????????????????????????????????????");
                                if (null != inputAndSendPop) {
                                    inputAndSendPop.dissmiss();
                                }
                                mPageIndex = 1;
                                KeyboardUtils.hideKeyboard(getWindow().getDecorView());
                                getCommentList("1", String.valueOf(mPageSize), true);
                            } else if (code.equals(token_error)) {
                                try {
                                    SdkInteractiveParam.getInstance().toLogin();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (null != mJsonObject.getString("message")) {
                                    ToastUtils.showShort(mJsonObject.getString("message"));
                                } else {
                                    ToastUtils.showShort("????????????");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showShort("????????????");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    /**
     * ????????????????????????
     */
    private void getRecommend(String contentId, final int position) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("current", "1");
            jsonObject.put("pageSize", "999");
            jsonObject.put("contentId", contentId + "");
            jsonObject.put("pageIndex", "1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<RecommendModel>post(ApiConstants.getInstance().recommendList())
                .tag(recommendTag)
                .headers("appId", appId)
                .upJson(jsonObject)
                .execute(new JsonCallback<RecommendModel>() {
                    @Override
                    public void onSuccess(Response<RecommendModel> response) {
                        if (null == response.body().getData()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }
                        if (response.body().getCode().equals("200")) {
                            recommondList.clear();
                            recommondList.addAll(response.body().getData().getRecords());
                            if (recommondList.size() > 1) {
                                adapter.setRecommendList(recommondList, true);
                                mDatas.get(position).setRecommendVisible(true);
                            } else if (recommondList.size() == 1) {
                                adapter.setRecommendList(recommondList, false);
                                mDatas.get(position).setRecommendVisible(true);
                            } else if (recommondList.size() == 0) {
                                adapter.setRecommendList(recommondList, false);
                                mDatas.get(position).setRecommendVisible(false);
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }

                    }

                    @Override
                    public void onError(Response<RecommendModel> response) {
                        super.onError(response);
                        if (null != response.body()) {
                            ToastUtils.showShort(response.body().getMessage());
                            return;
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (isVisibleNoWifiView(VideoHomeActivity.this)) {
                            SPUtils.getInstance().put(Constants.AGREE_NETWORK, "0");
                        }
                        loadingProgress.setVisibility(View.GONE);
                        addPlayView(position);
                        getThematicCollection(myContentId);
                    }
                });
    }

    /**
     * ??????
     *
     * @param targetUserId
     */
    private void toFollow(String targetUserId) {
        OkGo.<TrackingUploadModel>post(ApiConstants.getInstance().toFollow() + targetUserId)
                .tag(VIDEOTAG)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .execute(new JsonCallback<TrackingUploadModel>() {
                    @Override
                    public void onSuccess(Response<TrackingUploadModel> response) {
                        if (200 == response.body().getCode()) {
                            setFollowView("true");
                            //???????????? ???????????? ???????????????id mDataDTO.getCreateBy()
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<TrackingUploadModel> response) {
                        super.onError(response);
                        if (null != response.body()) {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    /**
     * ????????????
     */
    private void setFollowView(String whetherFollow) {
        /**
         * ????????????
         */
        TextView followText = (TextView) adapter.getViewByPosition(currentIndex, R.id.follow);
        String localUserId = PersonInfoManager.getInstance().getUserId();
        String userId = mDataDTO.getCreateBy();

        if (null == followText) {
            return;
        }
        if (TextUtils.equals(whetherFollow, "true")) {
            //?????????
            isFollow = true;
            followText.setText("?????????");
            followText.setBackgroundResource(R.drawable.followed_bg);
        } else {
            //?????????
            isFollow = false;
            followText.setText("??????");
            followText.setBackgroundResource(R.drawable.follow_bg);
        }
    }

    /**
     * ????????????
     *
     * @param targetUserId
     */
    private void cancelFollow(String targetUserId) {
        OkGo.<TrackingUploadModel>post(ApiConstants.getInstance().cancelFollow() + targetUserId)
                .tag(VIDEOTAG)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .execute(new JsonCallback<TrackingUploadModel>() {
                    @Override
                    public void onSuccess(Response<TrackingUploadModel> response) {
                        if (200 == response.body().getCode()) {
                            setFollowView("false");
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<TrackingUploadModel> response) {
                        super.onError(response);
                        if (null != response.body()) {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    /**
     * ????????????
     *
     * @param jsonObject
     */
    public static void uploadBuriedPoint(JSONObject jsonObject, final String trackingType) {
        if (null == jsonObject) {
            return;
        }
        OkGo.<TrackingUploadModel>post(ApiConstants.getInstance().trackingUpload())
                .tag(TRACKINGUPLOAD)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .upJson(jsonObject)
                .execute(new JsonCallback<TrackingUploadModel>() {
                    @Override
                    public void onSuccess(Response<TrackingUploadModel> response) {
                        Log.e("????????????", "????????????" + trackingType);
                    }

                    @Override
                    public void onError(Response<TrackingUploadModel> response) {
                        super.onError(response);
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }
                        ToastUtils.showShort(response.body().getMessage());
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                    }
                });
    }

    /**
     * ????????????????????? ???????????????????????? ????????????????????????
     */
    private void noLoginTipsPop() {
        if (null == noLoginTipsPop) {
            noLoginTipsPop = new CustomPopWindow.PopupWindowBuilder(this)
                    .setView(noLoginTipsView)
                    .enableBackgroundDark(true)
                    .setOutsideTouchable(true)
                    .setFocusable(true)
                    .setAnimationStyle(R.style.AnimCenter)
                    .size(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels)
                    .create()
                    .showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        } else {
            noLoginTipsPop.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }
    }

    /**
     * ??????/????????????
     */
    private void addOrCancelLike(String targetId, String type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("targetId", targetId);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(ApiConstants.getInstance().addOrCancelLike())
                .tag(VIDEOTAG)
                .headers("token", PersonInfoManager.getInstance().getTransformationToken())
                .upJson(jsonObject)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (null == playerView) {
                            return;
                        }

                        try {
                            JSONObject json = new JSONObject(response.body());
                            if (null != json && json.get("code").toString().equals("200")) {
                                if (!mDatas.isEmpty()) {
                                    String jsonString = BuriedPointModelManager.getLikeAndFavorBuriedPointData(myContentId, mDatas.get(currentIndex).getTitle(),
                                            "", "", "", "", mDatas.get(currentIndex).getIssueTimeStamp(),
                                            Constants.CONTENT_TYPE);
                                    Log.e("??????", "???????????????---" + jsonString);
                                }

                                if (json.get("data").toString().equals("1")) {
                                    int num;
                                    videoDetailLikesImage.setImageResource(R.drawable.szrm_sdk_favourite_select);
                                    num = Integer.parseInt(NumberFormatTool.getNumStr(likesNum.getText().toString()));
                                    num++;
                                    likesNum.setText(NumberFormatTool.formatNum(num, false));
                                    playerView.contentStateModel.setWhetherLike("true");
                                    playerView.contentStateModel.setLikeCountShow(NumberFormatTool.formatNum(num, false).toString());
                                } else {
                                    int num;
                                    videoDetailLikesImage.setImageResource(R.drawable.szrm_sdk_favourite);
                                    num = Integer.parseInt(NumberFormatTool.getNumStr(likesNum.getText().toString()));
                                    if (num > 0) {
                                        num--;
                                    }
                                    if (num == 0) {
                                        likesNum.setText("???");
                                    } else {
                                        likesNum.setText(NumberFormatTool.formatNum(num, false));
                                    }
                                    playerView.contentStateModel.setWhetherLike("false");
                                    playerView.contentStateModel.setLikeCountShow(NumberFormatTool.formatNum(num, false).toString());
                                }
                                if (null != playerView.contentStateModel) {
                                    playerView.setContentStateModel(myContentId, videoType);
                                }
                            } else if (json.get("code").toString().equals(token_error)) {
                                Log.e("addOrCancelLike", "???token,????????????");
                                try {
                                    SdkInteractiveParam.getInstance().toLogin();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if (null != json.get("message").toString()) {
                                    ToastUtils.showShort(json.get("message").toString());
                                } else {
                                    ToastUtils.showShort("????????????");
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ToastUtils.showShort("????????????");
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        ToastUtils.showShort("????????????");
                    }
                });
    }

    /**
     * ????????????
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
                    .showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        } else {
            sharePop.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.video_back) {
            finish();
        } else if (id == R.id.video_detail_likes) {//??????
            if (TextUtils.isEmpty(PersonInfoManager.getInstance().getTransformationToken())) {
                noLoginTipsPop();
            } else {
                addOrCancelLike(myContentId, videoType);
            }
        } else if (id == R.id.dismiss_pop) {
            if (popupWindow != null) {
                popupWindow.dissmiss();
            }
        } else if (id == R.id.video_detail_comment_btn) {
            showCommentPopWindow();
        } else if (id == R.id.comment_pop_rl) {
            if (TextUtils.isEmpty(PersonInfoManager.getInstance().getTransformationToken())) {
                try {
                    noLoginTipsPop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                KeyboardUtils.toggleSoftInput(getWindow().getDecorView());
                edtInput.setHint("????????????????????????");
                isReply = false;
                showInputEdittextAndSend();
            }
        } else if (id == R.id.video_detail_white_comment_rl) {
            if (TextUtils.isEmpty(PersonInfoManager.getInstance().getTransformationToken())) {
                try {
                    noLoginTipsPop();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                edtInput.setHint("????????????????????????");
                isReply = false;
                showCommentPopWindow();
            }
        } else if (id == R.id.no_login_tips_cancel) {
            if (null != noLoginTipsPop) {
                noLoginTipsPop.dissmiss();
            }
        } else if (id == R.id.no_login_tips_ok) {
            if (null != noLoginTipsPop) {
                noLoginTipsPop.dissmiss();
            }
            try {
                SdkInteractiveParam.getInstance().toLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.share || id == R.id.comment_share) {
            if (mDatas.isEmpty()) {
                return;
            }
            sharePop();
        } else if (id == R.id.share_wx_btn) {
            if (mDatas.isEmpty()) {
                return;
            }
            toShare(mDataDTO, Constants.SHARE_WX);
        } else if (id == R.id.share_circle_btn) {
            if (mDatas.isEmpty()) {
                return;
            }
            toShare(mDataDTO, Constants.SHARE_CIRCLE);
        } else if (id == R.id.share_qq_btn) {
            if (mDatas.isEmpty()) {
                return;
            }
            toShare(mDataDTO, Constants.SHARE_QQ);
        }
    }
}