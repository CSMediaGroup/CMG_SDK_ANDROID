package event;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import common.callback.JsonCallback;
import common.callback.SdkInteractiveParam;
import common.constants.Constants;
import common.http.ApiConstants;
import common.utils.PersonInfoManager;
import model.bean.SZContentModel;
import ui.activity.VideoHomeActivity;
import ui.activity.WebActivity;

public class SzrmRecommend {
    public static SzrmRecommend szrmRecommend;
    public SingleLiveEvent<List<SZContentModel.DataDTO.ContentsDTO>> contentsEvent = new SingleLiveEvent<>();
    public List<SZContentModel.DataDTO.ContentsDTO> contentsDTOS = new ArrayList<>();

    private SzrmRecommend() {
    }

    public static SzrmRecommend getInstance() {
        if (szrmRecommend == null) {
            synchronized (SdkInteractiveParam.class) {
                if (szrmRecommend == null) {
                    szrmRecommend = new SzrmRecommend();
                }
            }
        }
        return szrmRecommend;
    }

    /**
     * 在星沙调用推荐列表数据
     */
    public void requestContentList(String refreshType) {
        OkGo.<SZContentModel>get(ApiConstants.getInstance().getCategoryCompositeData())
                .tag("zxs_categoryCompositeData")
                .params("categoryCode", "zxs.tuijian")
                .params("personalRec", "1")
                .params("refreshType", refreshType)
                .params("pageSize", "20")
                .params("ssid", PersonInfoManager.getInstance().getANDROID_ID())
                .execute(new JsonCallback<SZContentModel>() {
                    @Override
                    public void onSuccess(Response<SZContentModel> response) {
                        contentsEvent.setValue(response.body().getData().get(0).getContents());
                    }

                    @Override
                    public void onError(Response<SZContentModel> response) {
                        super.onError(response);
                        Log.e("zxs_list", response.body().getMessage());
                        contentsEvent.setValue(contentsDTOS);
                    }
                });
    }

    /**
     * 进入新闻详情页
     */
    public void routeToDetailPage(Context context, SZContentModel.DataDTO.ContentsDTO contentsDTO) {
        if (TextUtils.equals(contentsDTO.getType(), Constants.NEWS_VIDEO) || TextUtils.equals(contentsDTO.getType(), Constants.SHORT_VIDEO)) {
            Intent intent = new Intent(context, VideoHomeActivity.class);
            context.startActivity(intent);
        } else {
            Intent intent = new Intent(context, WebActivity.class);
            context.startActivity(intent);
        }
    }

}
