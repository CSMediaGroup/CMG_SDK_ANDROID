package smartrefresh.layout.listener;

import android.support.annotation.NonNull;

import smartrefresh.layout.api.RefreshLayout;

//import com.scwang.smart.refresh.layout.api.RefreshLayout;

/**
 * 加载更多监听器
 * Created by scwang on 2017/5/26.
 */
public interface OnLoadMoreListener {
    void onLoadMore(@NonNull RefreshLayout refreshLayout);
}
