package adpter;


import android.support.annotation.Keep;
import android.support.annotation.Nullable;

//import com.chad.library.adapter.base.BaseQuickAdapter;
//import com.chad.library.adapter.base.BaseViewHolder;
import com.szrm.videodetail.demo.R;

import java.util.List;

import brvah.BaseQuickAdapter;
import brvah.BaseViewHolder;

@Keep
public class VideoDetailKeyWordAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public VideoDetailKeyWordAdapter(int layoutResId, @Nullable List<String> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.keyword_tv, item);
    }
}
