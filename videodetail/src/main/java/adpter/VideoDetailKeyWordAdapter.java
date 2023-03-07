package adpter;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

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
