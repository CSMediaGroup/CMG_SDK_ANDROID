package common.callback;


import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import common.model.ShareModel;

public class ShareAdapter extends BaseQuickAdapter<ShareModel, BaseViewHolder> {
    public ShareAdapter(int layoutResId, @Nullable List<ShareModel> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ShareModel item) {

    }
}
