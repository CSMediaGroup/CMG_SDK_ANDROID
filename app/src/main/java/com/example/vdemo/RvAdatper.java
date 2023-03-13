package com.example.vdemo;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import brvah.BaseQuickAdapter;
import brvah.BaseViewHolder;
import event.SzrmRecommend;
import model.bean.SZContentModel;

public class RvAdatper extends BaseQuickAdapter<SZContentModel.DataDTO.ContentsDTO, BaseViewHolder> {
    private Context context;

    public RvAdatper(Context context, int layoutResId, @Nullable List<SZContentModel.DataDTO.ContentsDTO> data) {
        super(layoutResId, data);
        this.context = context;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, SZContentModel.DataDTO.ContentsDTO item) {
        TextView rvItemTV = helper.getView(R.id.rv_item_tv);
        rvItemTV.setText(item.getTitle());
        rvItemTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SzrmRecommend.getInstance().routeToDetailPage(context, item);
            }
        });
    }
}
