package ui.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import adpter.CommentPopRvAdapter;
import adpter.VideoCollectionAdapter;
import adpter.VideoDetailAdapter;
import common.callback.JsonCallback;
import common.callback.VideoInteractiveParam;
import common.constants.Constants;
import common.http.ApiConstants;
import common.manager.BuriedPointModelManager;
import common.manager.ContentBuriedPointManager;
import common.manager.OnViewPagerListener;
import common.manager.ViewPagerLayoutManager;
import common.model.CollectionLabelModel;
import common.model.CommentLv1Model;
import common.model.ContentStateModel;
import common.model.DataDTO;
import common.model.RecommendModel;
import common.model.ReplyLv2Model;
import common.model.ShareInfo;
import common.model.TokenModel;
import common.model.VideoChannelModel;
import common.model.VideoCollectionModel;
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
import model.bean.ActivityRuleBean;
import tencent.liteav.demo.superplayer.SuperPlayerDef;
import tencent.liteav.demo.superplayer.SuperPlayerModel;
import tencent.liteav.demo.superplayer.SuperPlayerView;
import tencent.liteav.demo.superplayer.contants.Contants;
import tencent.liteav.demo.superplayer.model.SuperPlayerImpl;
import tencent.liteav.demo.superplayer.model.utils.SystemUtils;
import tencent.liteav.demo.superplayer.ui.view.PointSeekBar;
import widget.CollectionClickble;
import widget.CustomLoadMoreView;
import widget.LoadingView;

import static android.widget.RelativeLayout.BELOW;
import static common.constants.Constants.VIDEOTAG;
import static common.constants.Constants.success_code;
import static common.constants.Constants.token_error;
import static common.utils.AppInit.appId;
import static tencent.liteav.demo.superplayer.SuperPlayerView.mTargetPlayerMode;
import static tencent.liteav.demo.superplayer.model.SuperPlayerImpl.mCurrentPlayVideoURL;
import static tencent.liteav.demo.superplayer.ui.player.AbsPlayer.formattedTime;
import static tencent.liteav.demo.superplayer.ui.player.WindowPlayer.mDuration;
import static tencent.liteav.demo.superplayer.ui.player.WindowPlayer.mProgress;
import static ui.activity.VideoHomeActivity.maxPercent;
import static ui.activity.VideoHomeActivity.uploadBuriedPoint;
import static ui.activity.WebActivity.szrmLoginRequest;
import static utils.NetworkUtil.setDataWifiStates;

import common.model.VideoCollectionModel.DataDTO.RecordsDTO;

public class VideoDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView videoDetailRv;
    public VideoCollectionAdapter adapter;
    private CommentPopRvAdapter commentPopRvAdapter;
    //??????????????????
    public List<RecordsDTO> mDatas = new ArrayList<>();
    //??????????????????
    private List<MultiItemEntity> mCommentPopRvData;
    private List<CommentLv1Model.DataDTO.RecordsDTO> mCommentPopDtoData;
    public SuperPlayerView playerView;
    private ImageView videoStaticBg;
    private ImageView startPlay;

    public RelativeLayout videoDetailCommentBtn;
    //??????????????????
    public CustomPopWindow popupWindow;
    private boolean popupWindowIsShow;
    private LinearLayout videoDetailLikes;

    private View contentView;
    private View chooseContentView;
    private RelativeLayout dismissPop;
    private RecyclerView commentPopRv;
    private TextView commentEdtInput;
    private RelativeLayout collectionBtn;
    private RelativeLayout likesBtn;
    private RelativeLayout commentPopRl;
    private RelativeLayout commentShare;
    //???????????????????????????????????????
    public CustomPopWindow inputAndSendPop;
    private View sendPopContentView;
    private View rootView;
    private EditText edtInput;
    private TextView tvSend;
    private RelativeLayout videoDetailWhiteCommentRl;
    //??????????????????
    public CustomPopWindow choosePop;
    private RecyclerView videoDetailChoosePopRv;

    public ViewPagerLayoutManager videoDetailmanager;
    private ImageView choosePopDismiss;
    public SmartRefreshLayout refreshLayout;
    private String transformationToken = "";
    private String panelCode = "";
    private boolean initialize = true;
    private String mVideoSize = "15"; //?????????????????????
    private int mPageIndex = 1; //??????????????????
    private int mPageSize = 10; //???????????????????????????
    public String myContentId = ""; //??????????????????id
    public int currentIndex = 0; //?????????????????????????????????
    private TextView commentTotal;
    private TextView commentPopCommentTotal;

    private ImageView videoDetailLikesImage; //????????????
    private TextView likesNum; //?????????
    private String videoType; //????????????
    private VideoInteractiveParam param;
    public String playUrl;
    private TextView commentEdittext;
    private String recommendTag = "recommend";
    private boolean isLoadComplate = false;
    private BaseQuickAdapter.RequestLoadMoreListener requestLoadMoreListener;
    public View decorView;
    private SoftKeyBoardListener softKeyBoardListener;
    public CustomPopWindow noLoginTipsPop;
    private View noLoginTipsView;
    private TextView noLoginTipsCancel;
    private TextView noLoginTipsOk;
    private LinearLayout share;
    public CustomPopWindow sharePop;
    private View sharePopView;
    private ImageView shareWxBtn;
    private ImageView shareCircleBtn;
    private ImageView shareQqBtn;
    public RecordsDTO mDataDTO;
    private List<RecommendModel.DataDTO.RecordsDTO> recommondList;
    private ViewGroup rlLp;
    private VideoChannelModel videoChannelModel;

    private VideoChannelModel channelModel;
    private Bundle args;
    //    private RelativeLayout.LayoutParams lp;
    private LoadingView loadingProgress;
    private boolean isFollow; //????????????
    public double pointPercent;// ???????????????????????????????????????
    //    private long everyOneDuration; //?????????????????????????????????????????? ????????????????????????
    private long lsDuration = 0; //??????????????????????????????????????????
    private boolean isCheckState; //????????????????????????????????????????????????
    private TextView zxpl;
    private String negativeScreenContentId;
    private DataDTO negativeScreenDto;
    private View footerView;
    public ActivityRuleBean activityRuleBean;
    public ImageView activityRuleImg; //???????????????
    public ImageView activityRuleAbbreviation;
    public ImageView activityToAbbreviation; //?????????????????????
    public boolean isAbbreviation; //????????????????????????
    public long videoOldSystemTime;
    public long videoReportTime;
    private String mCategoryName = "";
    private boolean isReply = false;
    private String replyId;
    private List<CollectionLabelModel.DataDTO> collectionList;
    private List<String> collectionTvList;
    private String classId;
    private RelativeLayout backRl;
    private ImageView back;
    private int videoPageIndex = 1;
    private RelativeLayout.LayoutParams playViewParams;
    private String className;
    private boolean isShow = true;
    private String logoUrl;
    private String appName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        SystemUtils.setNavbarColor(this, R.color.video_black);
        decorView = getWindow().getDecorView();
        setContentView(R.layout.activity_video_detail);
        initView();
        if (!TextUtils.isEmpty(classId)) {
//            getPullDownData(mVideoSize, panelCode, "false", Constants.REFRESH_TYPE);
            getCollectionList(classId, String.valueOf(videoPageIndex), String.valueOf(mVideoSize));
        } else if (!TextUtils.isEmpty(myContentId)) {
            getOneVideo(myContentId);
        }
    }

    private void initView() {
        playerView = new SuperPlayerView(this, getWindow().getDecorView(), true);
        classId = getIntent().getStringExtra("classId");
        panelCode = getIntent().getStringExtra("panelId");
        logoUrl = getIntent().getStringExtra("logoUrl");
        appName = getIntent().getStringExtra("appName");
        if (!TextUtils.isEmpty(getIntent().getStringExtra("contentId"))) {
            myContentId = getIntent().getStringExtra("contentId");
        }
        className = getIntent().getStringExtra("className");
        if (TextUtils.isEmpty(className)) {
            className = "";
        } else {
            className = "  " + className;
        }
        backRl = findViewById(R.id.back_rl);
        back = findViewById(R.id.back);
        back.setOnClickListener(this);
        param = VideoInteractiveParam.getInstance();
        share = findViewById(R.id.share);
        share.setOnClickListener(this);
        mDataDTO = new RecordsDTO();
        recommondList = new ArrayList<>();
        loadingProgress = findViewById(R.id.video_loading_progress);
        loadingProgress.setVisibility(View.VISIBLE);
        commentEdittext = findViewById(R.id.comment_edittext);
        videoDetailRv = findViewById(R.id.video_detail_rv);
        videoDetailRv.setHasFixedSize(true);
        videoDetailLikesImage = findViewById(R.id.video_detail_likes_image);
        likesNum = findViewById(R.id.likes_num);

        videoDetailmanager = new ViewPagerLayoutManager(VideoDetailActivity.this);
        videoDetailRv.setLayoutManager(videoDetailmanager);
        footerView = View.inflate(VideoDetailActivity.this, R.layout.footer_view, null);
        setSoftKeyBoardListener();

        /**
         * ???????????????????????????????????????
         */
        playerView.playModeCallBack = new SuperPlayerView.PlayModeCallBack() {
            @Override
            public void getPlayMode(SuperPlayerDef.PlayerMode playerMode) {
                LinearLayout videoFragmentFullLin = (LinearLayout) adapter.getViewByPosition(currentIndex, R.id.superplayer_iv_fullscreen);
                if (playerMode.equals(SuperPlayerDef.PlayerMode.FULLSCREEN)) {
                    videoDetailmanager.setCanScoll(false);
                    refreshLayout.setEnableRefresh(false);
                    adapter.setEnableLoadMore(false);
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

                    if (null != adapter.getViewByPosition(currentIndex, R.id.introduce_lin)) {
                        adapter.getViewByPosition(currentIndex, R.id.introduce_lin).setVisibility(View.GONE);
                    }

                    if (null != videoFragmentFullLin) {
                        videoFragmentFullLin.setVisibility(View.GONE);
                    }

                    if (null != adapter.getViewByPosition(currentIndex, R.id.horizontal_video_wdcs_logo)) {
                        adapter.getViewByPosition(currentIndex, R.id.horizontal_video_wdcs_logo).setVisibility(View.GONE);
                    }

                    if (null != adapter.getViewByPosition(currentIndex, R.id.cover_picture)) {
                        adapter.getViewByPosition(currentIndex, R.id.cover_picture).setVisibility(View.GONE);
                    }

                    backRl.setVisibility(View.GONE);

                    KeyboardUtils.hideKeyboard(getWindow().getDecorView());
                } else if (playerMode.equals(SuperPlayerDef.PlayerMode.WINDOW)) {
                    videoDetailmanager.setCanScoll(true);
                    if (!TextUtils.isEmpty(classId)) {
                        refreshLayout.setEnableRefresh(true);
                    }
                    adapter.setEnableLoadMore(true);
                    setLikeCollection(playerView.contentStateModel);
                    if (null != videoDetailCommentBtn) {
                        videoDetailCommentBtn.setVisibility(View.VISIBLE);
                    }
                    if (null != videoFragmentFullLin) {
                        videoFragmentFullLin.setVisibility(View.VISIBLE);
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

                    backRl.setVisibility(View.VISIBLE);
                }
            }
        };

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
                videoDetailmanager.setCanScoll(false);
            }

            @Override
            public void onStopTrackingTouch(PointSeekBar seekBar) {
                int curProgress = seekBar.getProgress();
                int maxProgress = seekBar.getMax();
                if (mTargetPlayerMode == SuperPlayerDef.PlayerMode.WINDOW) {
                    videoDetailmanager.setCanScoll(true);
                } else {
                    videoDetailmanager.setCanScoll(false);
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


        //??????????????????
        SuperPlayerImpl.setReadPlayCallBack(new SuperPlayerImpl.ReadPlayCallBack() {
            @Override
            public void ReadPlayCallback() {
                if (null == playerView.buriedPointModel.getXksh_renew() || TextUtils.equals("false", playerView.buriedPointModel.getXksh_renew())) {
//                    //????????????
                    videoOldSystemTime = DateUtils.getTimeCurrent();
                    String event;
                    event = Constants.CMS_VIDEO_PLAY;
                    String contentId = "";
                    if (TextUtils.isEmpty(mDataDTO.getThirdPartyId())) {
                        contentId = String.valueOf(mDataDTO.getId());
                    } else {
                        contentId = mDataDTO.getThirdPartyId();
                    }
                    uploadBuriedPoint(ContentBuriedPointManager.setContentBuriedPoint(VideoDetailActivity.this, contentId, "", "", event, mDataDTO.getVolcCategory(), mDataDTO.getRequestId(), appName), event);
                }
            }
        });

        //????????????/??????????????? ??????????????????
        SuperPlayerImpl.setDetailAutoPlayOverCallBack(new SuperPlayerImpl.DetailAutoPlayOverCallBack() {
            @Override
            public void DetailAutoPlayOverCallBack() {
                if (isShow) {
                    playerView.mSuperPlayer.reStart();
                }
            }
        });


        videoDetailmanager.setOnViewPagerListener(new OnViewPagerListener() {


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

//                playerView.mWindowPlayer.setDataDTO(mDataDTO, mDataDTO);
//                playerView.mWindowPlayer.setViewpager((NoScrollViewPager) VideoDetailActivity.this.findViewById(R.id.video_vp));
                playerView.mWindowPlayer.setIsTurnPages(false);
                playerView.mWindowPlayer.setManager(videoDetailmanager);
//                playerView.mFullScreenPlayer.setDataDTO(mDataDTO);
                myContentId = String.valueOf(mDatas.get(0).getId());
                addPageViews(myContentId);
                OkGo.getInstance().cancelTag("contentState");
                getContentState(myContentId);
//                getThematicCollection(myContentId);
//                setCollection();

                mCurrentPlayVideoURL = mDatas.get(0).getPlayUrl();
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
                getCommentList(String.valueOf(mPageIndex), String.valueOf(mPageSize), true);
                videoType = mDatas.get(0).getType();
                rlLp = (ViewGroup) videoDetailmanager.findViewByPosition(0);
                OkGo.getInstance().cancelTag(recommendTag);
                //??????????????????
                getRecommend(myContentId, 0);
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
                //?????? ?????????
//              ContentBuriedPointManager.setContentBuriedPoint();
                playerView.mWindowPlayer.hide();

                if (mDuration != 0 && mProgress != 0) {
                    //????????????
                    long evePlayTime = Math.abs(mProgress - lsDuration);
                    double currentPercent = (evePlayTime * 1.0 / mDuration);
                    double uploadPercent = 0;
                    if (null == playerView.buriedPointModel.getIs_renew() || TextUtils.equals("false", playerView.buriedPointModel.getIs_renew())) {
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
                    videoReportTime = DateUtils.getTimeCurrent() - videoOldSystemTime;
                    BigDecimal two = new BigDecimal(uploadPercent);
                    double pointPercentTwo = two.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
                    String event;
                    event = Constants.CMS_VIDEO_OVER;
                    String contentId = "";
                    if (TextUtils.isEmpty(mDataDTO.getThirdPartyId())) {
                        contentId = String.valueOf(mDataDTO.getId());
                    } else {
                        contentId = mDataDTO.getThirdPartyId();
                    }
                    uploadBuriedPoint(ContentBuriedPointManager.setContentBuriedPoint(VideoDetailActivity.this, contentId, String.valueOf(videoReportTime), String.valueOf(Math.floor(pointPercentTwo * 100)), event, mDataDTO.getVolcCategory(), mDataDTO.getRequestId(), appName), event);
//                        DebugLogUtils.DebugLog("???????????????" + event + "????????????:" + videoReportTime + "---" + "???????????????:" + pointPercentTwo);
                    Log.e("video_md", "???????????????" + event + "????????????:" + videoReportTime + "---" + "???????????????:" + pointPercentTwo);
                }

                mDataDTO = mDatas.get(position);
                if (null != adapter.getViewByPosition(position, R.id.superplayer_iv_fullscreen)) {
                    if (TextUtils.equals("2", videoIsNormal(Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(position).getWidth())),
                            Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(position).getHeight()))))) {
                        adapter.getViewByPosition(position, R.id.superplayer_iv_fullscreen).setVisibility(View.VISIBLE);
                    } else {
                        adapter.getViewByPosition(position, R.id.superplayer_iv_fullscreen).setVisibility(View.GONE);
                    }
                }

//                DebugLogUtils.DebugLog(mDataDTO.isFullBtnIsShow() + "??????" + "---????????????" + mDataDTO.getWidth() + "?????????:" + mDataDTO.getHeight() + "????????????---" +
//                        videoIsNormal(Integer.parseInt(NumberFormatTool.getNumStr(mDataDTO.getWidth())),
//                                Integer.parseInt(NumberFormatTool.getNumStr(mDataDTO.getHeight()))));


                //????????????????????????????????????
                playerView.mWindowPlayer.setRecordDuration(0);
                lsDuration = 0;
                maxPercent = 0;
                mCurrentPlayVideoURL = mDatas.get(position).getPlayUrl();
                playUrl = mDatas.get(position).getPlayUrl();
//                playerView.mWindowPlayer.setDataDTO(mDataDTO, mDatas.get(currentIndex));
//                playerView.mFullScreenPlayer.setDataDTO(mDataDTO);
                playerView.mWindowPlayer.setIsTurnPages(true);
                currentIndex = position;
//                choosePopDatas.clear();
                reset();
                myContentId = String.valueOf(mDatas.get(position).getId());
                //??????????????????
                if (null != playerView && null != playerView.buriedPointModel) {
                    playerView.buriedPointModel.setIs_renew("false");
                }
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
                getCommentList(String.valueOf(mPageIndex), String.valueOf(mPageSize), true);
                getContentState(myContentId);
//                getThematicCollection(myContentId);
                setCollection();
                rlLp = (ViewGroup) videoDetailmanager.findViewByPosition(position);
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

        contentView = View.inflate(VideoDetailActivity.this, R.layout.fragment_video_comment_pop, null);
        sendPopContentView = View.inflate(VideoDetailActivity.this, R.layout.layout_input_window, null);
        commentPopCommentTotal = contentView.findViewById(R.id.comment_pop_comment_total);
        edtInput = sendPopContentView.findViewById(R.id.edtInput);
        tvSend = sendPopContentView.findViewById(R.id.tvSend);

        noLoginTipsView = View.inflate(VideoDetailActivity.this, R.layout.no_login_tips, null);
        noLoginTipsCancel = noLoginTipsView.findViewById(R.id.no_login_tips_cancel);
        noLoginTipsOk = noLoginTipsView.findViewById(R.id.no_login_tips_ok);
        noLoginTipsCancel.setOnClickListener(this);
        noLoginTipsOk.setOnClickListener(this);

        sharePopView = View.inflate(VideoDetailActivity.this, R.layout.share_pop_view, null);
        shareWxBtn = sharePopView.findViewById(R.id.share_wx_btn);
        shareWxBtn.setOnClickListener(this);
        shareCircleBtn = sharePopView.findViewById(R.id.share_circle_btn);
        shareCircleBtn.setOnClickListener(this);
        shareQqBtn = sharePopView.findViewById(R.id.share_qq_btn);
        shareQqBtn.setOnClickListener(this);

        activityRuleImg = findViewById(R.id.activity_rule_img);
        activityRuleImg.setOnClickListener(this);
        activityToAbbreviation = findViewById(R.id.activity_to_abbreviation);
        activityToAbbreviation.setOnClickListener(this);
        activityRuleAbbreviation = findViewById(R.id.activity_rule_abbreviation);
        activityRuleAbbreviation.setOnClickListener(this);

        /**
         * ????????????
         */
        tvSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtInput.getText())) {
                    Toast.makeText(VideoDetailActivity.this, "???????????????", Toast.LENGTH_SHORT).show();
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
        videoDetailCommentBtn = findViewById(R.id.video_detail_comment_btn);
        videoDetailCommentBtn.setOnClickListener(this);
        videoDetailWhiteCommentRl = findViewById(R.id.video_detail_white_comment_rl);
        videoDetailWhiteCommentRl.setOnClickListener(this);
        initCommentPopRv();
        adapter = new VideoCollectionAdapter(R.layout.video_fragment_item, mDatas, VideoDetailActivity.this,
                playerView, refreshLayout, videoDetailCommentBtn, videoDetailmanager, className, logoUrl);
        adapter.setLoadMoreView(new CustomLoadMoreView());
        adapter.setPreLoadNumber(2);
        adapter.openLoadAnimation();
        adapter.setOnLoadMoreListener(requestLoadMoreListener, videoDetailRv);

        /**
         * ???wifi ??????????????????
         */
        adapter.setToAddPlayerViewClick(new VideoDetailAdapter.ToAddPlayerViewClick() {
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

        videoDetailRv.setAdapter(adapter);
    }

    /**
     * ????????????????????????
     */
    private void getOneVideo(final String contentId) {
        mDatas.clear();
        OkGo.<String>get(ApiConstants.getInstance().getVideoDetailUrl() + contentId)
                .tag(VIDEOTAG)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                if (jsonObject.get("code").toString().equals(success_code)) {
                                    String json = jsonObject.optJSONObject("data").toString();
                                    if (null == json || TextUtils.isEmpty(json)) {
                                        ToastUtils.showShort(R.string.data_err);
                                        return;
                                    }
                                    RecordsDTO dataDTO = JSON.parseObject(json, RecordsDTO.class);
                                    if (null == dataDTO) {
                                        return;
                                    }
                                    initialize = false;
                                    mDatas.add(dataDTO);
                                    setDataWifiStates(mDatas, VideoDetailActivity.this);
                                    adapter.setNewData(mDatas);
                                } else {
                                    ToastUtils.showShort(jsonObject.get("message").toString());
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        try {
                            if (null == response.body()) {
                                ToastUtils.showShort(R.string.net_err);
                            } else {
                                JSONObject jsonObject = new JSONObject(response.body());
                                ToastUtils.showShort(jsonObject.getString("message"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        adapter.loadMoreEnd();
                        refreshLayout.setEnableRefresh(false);
                        getThematicCollection(myContentId);
                    }
                });
    }

    /**
     * ????????????????????????
     */
    private void getCollectionList(String collectionId, String mPageIndex, String mPageSize) {
        if (null != playerView && null != playerView.getParent()) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
        }
        mDatas.clear();
        OkGo.<VideoCollectionModel>get(ApiConstants.getInstance().getSpecList())
                .tag(VIDEOTAG)
                .headers("appId", appId)
                .params("classId", collectionId)
                .params("pageIndex", mPageIndex)
                .params("pageSize", mPageSize)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new JsonCallback<VideoCollectionModel>(VideoCollectionModel.class) {

                    @Override
                    public void onSuccess(Response<VideoCollectionModel> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode().equals(success_code)) {
                            if (null == response.body().getData() && response.body().getData().getRecords().size() == 0) {
                                ToastUtils.showShort(R.string.data_err);
                                return;
                            }

                            mDatas.addAll(response.body().getData().getRecords());
                            for (int i = 0; i < mDatas.size(); i++) {
                                String videoType = videoIsNormal(Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(i).getWidth())),
                                        Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(i).getHeight())));
                                mDatas.get(i).setLogoType(videoType);
                            }

                            setDataWifiStates(mDatas, VideoDetailActivity.this);
                            adapter.setNewData(mDatas);
                            if (mDatas.size() > 0) {
                                initialize = false;
                            }
//                            videoDetailCommentBtn.setVisibility(View.VISIBLE);
                        } else {
//                            videoDetailCommentBtn.setVisibility(View.GONE);
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Response<VideoCollectionModel> response) {
                        super.onError(response);
                        if (null != response.body()) {
//                            ToastUtils.showShort(response.body().getMessage());
                            return;
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loadingProgress.setVisibility(View.GONE);
                        isLoadComplate = false;
                    }
                });
    }

    /**
     * ?????????playview?????????????????????view
     *
     * @param position
     */
    public void addPlayView(final int position) {
        if (null == playerView) {
            return;
        }
        LinearLayout linearLayout = (LinearLayout) adapter.getViewByPosition(currentIndex, R.id.introduce_lin);
        RelativeLayout.LayoutParams mLayoutBottomParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (null != playerView.mWindowPlayer && null != playerView.mWindowPlayer.mLayoutBottom && null != playerView.mWindowPlayer.mLayoutBottom.getParent()) {
            ((ViewGroup) playerView.mWindowPlayer.mLayoutBottom.getParent()).removeView(playerView.mWindowPlayer.mLayoutBottom);
        }

        if (null != playerView && null != playerView.getParent()) {
            ((ViewGroup) playerView.getParent()).removeView(playerView);
        }

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
            playerView.setOrientation(false);
            if (linearLayout != null) {
                linearLayout.addView(playerView.mWindowPlayer.mLayoutBottom, 0);
            }
        } else if (TextUtils.equals("1", videoType)) {
            int height = (int) (ScreenUtils.getPhoneWidth(VideoDetailActivity.this) / Constants.Portrait_Proportion);
            playViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            playViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            if (phoneIsNormal()) {
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
            int height = (int) (ScreenUtils.getPhoneWidth(VideoDetailActivity.this) / Constants.Horizontal_Proportion);
            playViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            playViewParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            playerView.mSuperPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_ADJUST_RESOLUTION);
            playerView.setOrientation(true);
            mLayoutBottomParams.addRule(BELOW, playerView.getId());
            mLayoutBottomParams.setMargins(0, (AppInit.getContext().getResources().getDisplayMetrics().heightPixels / 2) + ButtonSpan.dip2px(135), 0, 0);
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
            uploadBuriedPoint(ContentBuriedPointManager.setContentBuriedPoint(VideoDetailActivity.this, contentId, "", "", Constants.CMS_CLIENT_SHOW, mDataDTO.getVolcCategory(), mDataDTO.getRequestId(), appName), Constants.CMS_CLIENT_SHOW);
            play(mDatas.get(position).getPlayUrl(), mDatas.get(position).getTitle());
        }
    }


    /**
     * ???????????????16???9
     * 0 :  ???????????????16???9
     * 1 ???  ????????????16???9
     * 2 ???  ????????????
     */
    public static String videoIsNormal(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) {
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
        int phoneWidth = ScreenUtils.getPhoneWidth(VideoDetailActivity.this);
        int phoneHeight = ScreenUtils.getPhoneHeight(VideoDetailActivity.this);
        if (phoneHeight * 9 == phoneWidth * 16) {
            return true;
        } else {
            return false;
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
                isLoadComplate = true;
                adapter.setOnLoadMoreListener(requestLoadMoreListener, videoDetailRv);
//                getPullDownData(mVideoSize, panelCode, "false", Constants.REFRESH_TYPE);
                videoPageIndex = 1;
                getCollectionList(classId, String.valueOf(videoPageIndex), String.valueOf(mVideoSize));
                //??????????????????
                if (null != playerView && null != playerView.buriedPointModel) {
                    playerView.buriedPointModel.setIs_renew("false");
                }
            }
        });

        requestLoadMoreListener = new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (!isLoadComplate) {
                    videoDetailRv.post(new Runnable() {
                        @Override
                        public void run() {
                            if (mDatas.isEmpty()) {
                                adapter.loadMoreFail();
                                return;
                            }
                            videoPageIndex++;
                            loadMoreData(classId, String.valueOf(videoPageIndex), mVideoSize);
                        }
                    });
                }
            }
        };
    }


    /**
     * ?????????????????????
     */
    private void initCommentPopRv() {
        mCommentPopRvData = new ArrayList<>();
        mCommentPopDtoData = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VideoDetailActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        commentPopRv.setLayoutManager(linearLayoutManager);
        commentPopRvAdapter = new CommentPopRvAdapter(mCommentPopRvData, VideoDetailActivity.this);
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
            popupWindow = new CustomPopWindow.PopupWindowBuilder(VideoDetailActivity.this)
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
     */
    public void showChoosePop() {
        if (null == choosePop) {
            //???????????????popWindow
            choosePop = new CustomPopWindow.PopupWindowBuilder(VideoDetailActivity.this)
                    .setView(chooseContentView)
                    .setOutsideTouchable(false)
                    .setFocusable(true)
                    .setAnimationStyle(R.style.take_popwindow_anim)
                    .create()
                    .showAtLocation(VideoDetailActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        } else {
            choosePop.showAtLocation(VideoDetailActivity.this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        }

        choosePop.getPopupWindow().setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                SystemUtils.hideSystemUI(decorView);
            }
        });
    }

    /**
     * ????????????
     */
    private void sharePop() {
        if (null == sharePop) {
            sharePop = new CustomPopWindow.PopupWindowBuilder(VideoDetailActivity.this)
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

    public void toShare(RecordsDTO item, String platform) {
        VideoInteractiveParam param = VideoInteractiveParam.getInstance();
        ShareInfo shareInfo = ShareInfo.getInstance(item.getShareUrl(), item.getShareImageUrl(),
                item.getShareBrief(), item.getShareTitle(), platform);
        try {
            param.shared(shareInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????
     *
     * @param playUrl
     */
    public void play(String playUrl, String title) {
        if (null != playerView) {
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
     * ??????????????????
     */
    private void loadMoreData(String collectionId, String mPageIndex, String mPageSize) {
//        if (null != playerView && null != playerView.getParent()) {
//            ((ViewGroup) playerView.getParent()).removeView(playerView);
//        }
        OkGo.<VideoCollectionModel>get(ApiConstants.getInstance().getSpecList())
                .tag(VIDEOTAG)
                .params("classId", collectionId)
                .params("pageIndex", mPageIndex)
                .params("pageSize", mPageSize)
                .cacheMode(CacheMode.NO_CACHE)
                .execute(new JsonCallback<VideoCollectionModel>(VideoCollectionModel.class) {

                    @Override
                    public void onSuccess(Response<VideoCollectionModel> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode().equals(success_code)) {
                            if (null == response.body().getData() && response.body().getData().getRecords().size() == 0) {
                                ToastUtils.showShort(R.string.data_err);
                                return;
                            }

                            if (response.body().getData().getRecords().size() == 0) {
                                adapter.loadMoreEnd();
                                adapter.setOnLoadMoreListener(null, videoDetailRv);
                                if (null != footerView && null != footerView.getParent()) {
                                    ((ViewGroup) footerView.getParent()).removeView(footerView);
                                }
                                adapter.addFooterView(footerView);
//                                adapter.setOnLoadMoreListener(requestLoadMoreListener, videoDetailRv);
                                isLoadComplate = true;
                                adapter.setNewData(mDatas);
                            } else {
                                adapter.setOnLoadMoreListener(requestLoadMoreListener, videoDetailRv);
                                isLoadComplate = false;
                                mDatas.addAll(response.body().getData().getRecords());
                                for (int i = 0; i < mDatas.size(); i++) {
                                    String videoType = videoIsNormal(Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(i).getWidth())),
                                            Integer.parseInt(NumberFormatTool.getNumStr(mDatas.get(i).getHeight())));
                                    mDatas.get(i).setLogoType(videoType);
                                }

                                setDataWifiStates(mDatas, VideoDetailActivity.this);
                                adapter.setNewData(mDatas);
                            }
                        }
                        if (null != refreshLayout) {
                            refreshLayout.finishRefresh();
                        }
                    }

                    @Override
                    public void onError(Response<VideoCollectionModel> response) {
                        super.onError(response);
                        if (null != response.body()) {
//                            ToastUtils.showShort(response.body().getMessage());
                            return;
                        }
                        ToastUtils.showShort(R.string.net_err);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loadingProgress.setVisibility(View.GONE);
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
                                KeyboardUtils.hideKeyboard(VideoDetailActivity.this.getWindow().getDecorView());
                                mPageIndex = 1;
                                getCommentList(String.valueOf(mPageIndex), String.valueOf(mPageSize), true);
                            } else if (code.equals(token_error)) {
                                Log.e("addComment", "???token ????????????");
                                try {
                                    param.toLogin();
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
                                KeyboardUtils.hideKeyboard(VideoDetailActivity.this.getWindow().getDecorView());
                                getCommentList(String.valueOf(mPageIndex), String.valueOf(mPageSize), true);
                            } else if (code.equals(token_error)) {
                                try {
                                    param.toLogin();
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
    public void getContentState(String contentId) {
        OkGo.<ContentStateModel>get(ApiConstants.getInstance().queryStatsData())
                .tag("contentState")
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
                            String collectionStr = "";
                            collectionList.addAll(response.body().getData());
                            for (int i = 0; i < collectionList.size(); i++) {
                                collectionStr = collectionStr + collectionList.get(i).getTitle();
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
                            RecordsDTO item = adapter.getItem(currentIndex);
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
                                    ImageSpan imgSpan = new ImageSpan(VideoDetailActivity.this,
                                            R.drawable.szrm_sdk_collection_image,
                                            ImageSpan.ALIGN_CENTER);
                                    final String str = collectionTvList.get(i);
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
                                            Intent intent = new Intent(VideoDetailActivity.this, VideoDetailActivity.class);
                                            intent.putExtra("classId", classId);
                                            startActivity(intent);
                                        }
                                    }, VideoDetailActivity.this), 0, sp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    if (i == collectionList.size() - 1) {
                                        builder.append(sp);
                                        builder.append("  " + brief);
                                    } else {
                                        builder.append(sp);
                                    }
                                }
                                foldTextView.setMovementMethod(LinkMovementMethod.getInstance());
                                foldTextView.setText(builder);
                                int count = foldTextView.getLineCount();
                                if (foldTextView.getLineCount() > 2) {

                                }
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
    public void setCollection() {
        RecordsDTO item = adapter.getItem(currentIndex);
        String brief;
        TextView foldTextView = (TextView) adapter.getViewByPosition(currentIndex, R.id.fold_text);
        TextView expendTextView = (TextView) adapter.getViewByPosition(currentIndex, R.id.expend_text);
        if (null == item) {
            return;
        }
        if (TextUtils.isEmpty(adapter.getItem(currentIndex).getBrief())) {
            brief = item.getTitle();
        } else {
            brief = item.getBrief();
        }

        if (TextUtils.isEmpty(className)) {
            foldTextView.setText(brief);
            expendTextView.setText(brief);
        } else {
            SpannableString sp = new SpannableString(className);
            ImageSpan imgSpan = new ImageSpan(VideoDetailActivity.this,
                    R.drawable.szrm_sdk_collection_image,
                    ImageSpan.ALIGN_CENTER);
            sp.setSpan(imgSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new ForegroundColorSpan(Color.WHITE), 0, className.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(sp);
            builder.append(" " + brief);
            foldTextView.setText(builder);
            expendTextView.setText(builder);
        }


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
     * ??????/????????????
     */
    private void addOrCancelFavor(String contentId, String type) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("contentId", contentId);
            jsonObject.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        OkGo.<String>post(ApiConstants.getInstance().addOrCancelFavor())
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

                        if (null == playerView) {
                            return;
                        }

                        try {
                            JSONObject json = new JSONObject(response.body());
                            if (json.get("code").toString().equals(success_code)) {
                                if (!mDatas.isEmpty()) {
                                    String jsonString = BuriedPointModelManager.getLikeAndFavorBuriedPointData(myContentId, mDatas.get(currentIndex).getTitle(),
                                            "", "", "", "", mDatas.get(currentIndex).getIssueTimeStamp(),
                                            Constants.CONTENT_TYPE);
                                    Log.e("??????", "???????????????---" + jsonString);
                                }

                                if (null != playerView.contentStateModel) {
                                    playerView.setContentStateModel(myContentId, videoType);
                                }
                            } else if (json.get("code").toString().equals(token_error)) {
                                Log.e("addOrCancelFavor", "???token ????????????");
                                try {
                                    param.toLogin();
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
                                    param.toLogin();
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


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    /**
     * ???????????????code??????token
     */
    public void getUserToken(String token) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        OkGo.<TokenModel>post(ApiConstants.getInstance().mycsToken())
                .tag("userToken")
                .upJson(jsonObject)
                .execute(new JsonCallback<TokenModel>(TokenModel.class) {
                    @Override
                    public void onSuccess(Response<TokenModel> response) {
                        if (null == response.body()) {
                            ToastUtils.showShort(R.string.data_err);
                            return;
                        }

                        if (response.body().getCode() == 200) {
                            if (null == response.body().getData()) {
                                ToastUtils.showShort(R.string.data_err);
                                return;
                            }
                            Log.d("mycs_token", "????????????");
                            try {
                                PersonInfoManager.getInstance().setToken(VideoInteractiveParam.getInstance().getCode());
                                PersonInfoManager.getInstance().setGdyToken(response.body().getData().getGdyToken());
                                PersonInfoManager.getInstance().setUserId(response.body().getData().getLoginSysUserVo().getId());
                                PersonInfoManager.getInstance().setTgtCode(VideoInteractiveParam.getInstance().getCode());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            transformationToken = response.body().getData().getToken();
                            PersonInfoManager.getInstance().setTransformationToken(transformationToken);
                            if (!TextUtils.isEmpty(myContentId)) {
                                getContentState(myContentId);
                            }
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                    }

                    @Override
                    public void onError(Response<TokenModel> response) {
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

    @Override
    public void onResume() {
        super.onResume();
        if (playerView != null && null != mDataDTO.getPlayUrl() && !SPUtils.isVisibleNoWifiView(this)) {
            if (playerView.homeVideoIsLoad) {
                playerView.mSuperPlayer.resume();
            } else {
                Log.e("sss", mCurrentPlayVideoURL);
                playerView.mSuperPlayer.reStart();
            }
        }
        videoOldSystemTime = DateUtils.getTimeCurrent();
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
    public void onPause() {
        super.onPause();
        if (playerView == null) {
            return;
        }

        playerView.mSuperPlayer.pause();
        if (null == mDataDTO) {
            return;
        }
        if (playerView.mWindowPlayer.mCurrentPlayState != SuperPlayerDef.PlayerState.END) {

            if (mProgress != 0 && mDuration != 0) {
                /**
                 * ?????????????????? ??????????????????
                 */
                long evePlayTime = Math.abs(mProgress - lsDuration);
                double currentPercent = (evePlayTime * 1.0 / mDuration);
                double uploadPercent = 0;
                if (null == playerView.buriedPointModel.getIs_renew() || TextUtils.equals("false", playerView.buriedPointModel.getIs_renew())) {
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
                videoReportTime = DateUtils.getTimeCurrent() - videoOldSystemTime;
                BigDecimal two = new BigDecimal(uploadPercent);
                double pointPercentTwo = two.setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
                lsDuration = mProgress;
                String event;
                event = Constants.CMS_VIDEO_OVER;
                //????????????
                String contentId = "";
                if (TextUtils.isEmpty(mDataDTO.getThirdPartyId())) {
                    contentId = String.valueOf(mDataDTO.getId());
                } else {
                    contentId = mDataDTO.getThirdPartyId();
                }
                uploadBuriedPoint(ContentBuriedPointManager.setContentBuriedPoint(VideoDetailActivity.this, contentId, String.valueOf(videoReportTime), String.valueOf(Math.floor(pointPercentTwo * 100)), event, mDataDTO.getVolcCategory(), mDataDTO.getRequestId(), appName), event);
                Log.e("video_md", "???????????????" + event + "????????????:" + videoReportTime + "---" + "???????????????:" + pointPercentTwo);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isShow = false;
        if (playerView != null) {
            playerView.mSuperPlayer.stop();
            playerView.release();
            playerView.mSuperPlayer.destroy();
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.back) {
            finish();
        } else if (id == R.id.video_detail_collection) {//??????
            if (TextUtils.isEmpty(PersonInfoManager.getInstance().getTransformationToken())) {
                noLoginTipsPop();
            } else {
                addOrCancelFavor(myContentId, videoType);
            }

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
                edtInput.setHint("????????????????????????");
                isReply = false;
                KeyboardUtils.toggleSoftInput(VideoDetailActivity.this.getWindow().getDecorView());
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
                KeyboardUtils.toggleSoftInput(VideoDetailActivity.this.getWindow().getDecorView());
                showInputEdittextAndSend();
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
                param.toLogin();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.share || id == R.id.comment_share) {
            if (mDatas.isEmpty()) {
                return;
            }
            String jsonString = BuriedPointModelManager.getShareClick(myContentId, mDatas.get(currentIndex).getTitle(), "",
                    "", "", "", mDatas.get(currentIndex).getIssueTimeStamp(), Constants.CONTENT_TYPE, "");
            Log.e("??????", "?????????????????????---" + jsonString);
            sharePop();
        } else if (id == R.id.share_wx_btn) {
            if (mDatas.isEmpty()) {
                return;
            }
            String jsonString = BuriedPointModelManager.getShareType(myContentId, mDatas.get(currentIndex).getTitle(), "",
                    "", "", "", mDatas.get(currentIndex).getIssueTimeStamp(), Constants.CONTENT_TYPE, Constants.WX_STRING);
            Log.e("??????", "??????????????????????????????---" + jsonString);
            toShare(mDataDTO, Constants.SHARE_WX);
        } else if (id == R.id.share_circle_btn) {
            if (mDatas.isEmpty()) {
                return;
            }
            String jsonString = BuriedPointModelManager.getShareType(myContentId, mDatas.get(currentIndex).getTitle(), "",
                    "", "", "", mDatas.get(currentIndex).getIssueTimeStamp(), Constants.CONTENT_TYPE, Constants.CIRCLE_STRING);
            Log.e("??????", "?????????????????????????????????---" + jsonString);
            toShare(mDataDTO, Constants.SHARE_CIRCLE);
        } else if (id == R.id.share_qq_btn) {
            if (mDatas.isEmpty()) {
                return;
            }
            String jsonString = BuriedPointModelManager.getShareType(myContentId, mDatas.get(currentIndex).getTitle(), "",
                    "", "", "", mDatas.get(currentIndex).getIssueTimeStamp(), Constants.CONTENT_TYPE, Constants.QQ_STRING);
            Log.e("??????", "??????????????????QQ---" + jsonString);
            toShare(mDataDTO, Constants.SHARE_QQ);
        }
    }


    /**
     * ???????????????????????????
     */
    private void showInputEdittextAndSend() {
        //???????????????popWindow
        if (null == inputAndSendPop) {
            inputAndSendPop = new CustomPopWindow.PopupWindowBuilder(VideoDetailActivity.this)
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
     * ????????????????????? ???????????????????????? ????????????????????????
     */
    private void noLoginTipsPop() {
        if (null == noLoginTipsPop) {
            noLoginTipsPop = new CustomPopWindow.PopupWindowBuilder(VideoDetailActivity.this)
                    .setView(noLoginTipsView)
                    .enableBackgroundDark(true)
                    .setOutsideTouchable(true)
                    .setFocusable(true)
                    .setAnimationStyle(R.style.AnimCenter)
                    .size(AppInit.getContext().getResources().getDisplayMetrics().widthPixels, AppInit.getContext().getResources().getDisplayMetrics().heightPixels)
                    .create()
                    .showAtLocation(decorView, Gravity.CENTER, 0, 0);
        } else {
            noLoginTipsPop.showAtLocation(decorView, Gravity.CENTER, 0, 0);
        }
    }

    /**
     * ?????????????????????
     */
    private void setSoftKeyBoardListener() {
        softKeyBoardListener = new SoftKeyBoardListener(VideoDetailActivity.this);
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

                            ViewFlipper viewFlipper = (ViewFlipper) adapter.getViewByPosition(position, R.id.video_flipper);

                            if (null == viewFlipper) {
                                return;
                            }

                            if (adapter.getItem(position).isClosed()) {
                                viewFlipper.setVisibility(View.GONE);
                                return;
                            }

                            if (recommondList.size() > 1) {
                                viewFlipper.setVisibility(View.VISIBLE);
                                viewFlipper.startFlipping();
                                viewFlipper.setAutoStart(true);
                            } else if (recommondList.size() == 1) {
                                viewFlipper.setVisibility(View.VISIBLE);
                                viewFlipper.setAutoStart(false);
                            } else if (recommondList.size() == 0) {
                                viewFlipper.setVisibility(View.GONE);
                            }
                            adapter.getViewFlipperData(recommondList, viewFlipper, mDatas.get(position));
                        } else {
                            ToastUtils.showShort(response.body().getMessage());
                        }
                        if (SPUtils.isVisibleNoWifiView(VideoDetailActivity.this)) {
                            SPUtils.getInstance().put(Constants.AGREE_NETWORK, "0");
                        } else {
                            addPlayView(position);
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
                        if (SPUtils.isVisibleNoWifiView(VideoDetailActivity.this)) {
                            SPUtils.getInstance().put(Constants.AGREE_NETWORK, "0");
                        } else {
                            addPlayView(position);
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        loadingProgress.setVisibility(View.GONE);
                    }
                });
    }


}