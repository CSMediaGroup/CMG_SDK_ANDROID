package adpter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;



import java.util.ArrayList;
import java.util.List;

import common.model.VideoChannelModel;
import flyco.tablayout.SlidingTabLayout;
import tencent.liteav.demo.superplayer.SuperPlayerView;
import ui.fragment.LiveFragment;
import ui.fragment.VideoDetailFragment;
import ui.fragment.XkshFragment;

public class VideoViewPagerAdapter extends FragmentPagerAdapter {
    public List<Fragment> fragmentList = new ArrayList<>();
    private List<VideoChannelModel> titleList = new ArrayList<>();

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public VideoViewPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position).getColumnBean().getColumnName();
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    public void clearItems() {
        titleList.clear();
        fragmentList.clear();
        notifyDataSetChanged();
    }

    public void addItems(@Nullable List<VideoChannelModel> channelBeanList, SlidingTabLayout videoTab,
                         SuperPlayerView playerView, String contentId, String categoryName) {
        titleList.clear();
        fragmentList.clear();
        titleList.addAll(channelBeanList);
        for (VideoChannelModel videoChannelModel : channelBeanList) {
            if (TextUtils.equals("2", videoChannelModel.getColumnBean().getColumnId())) {
                LiveFragment fragment = new LiveFragment();
                fragmentList.add(fragment.newInstance(fragment, videoChannelModel));
            } else if (TextUtils.equals("1", videoChannelModel.getColumnBean().getColumnId())) {
                VideoDetailFragment fragment = new VideoDetailFragment(videoTab, playerView, contentId,categoryName);
                fragmentList.add(fragment.newInstance(fragment, videoChannelModel));
            } else {
                XkshFragment fragment = new XkshFragment(videoTab, playerView,categoryName);
                fragmentList.add(fragment.newInstance(fragment, videoChannelModel));
            }
        }
        notifyDataSetChanged();
    }

    public void addItems2(@Nullable List<VideoChannelModel> channelBeanList, SlidingTabLayout videoTab,
                         SuperPlayerView playerView, String contentId, String categoryName) {
        titleList.clear();
        fragmentList.clear();
        titleList.addAll(channelBeanList);
        for (VideoChannelModel videoChannelModel : channelBeanList) {
            if (TextUtils.equals("1", videoChannelModel.getColumnBean().getColumnId())) {
                VideoDetailFragment fragment = new VideoDetailFragment(videoTab, playerView, contentId,categoryName);
                fragmentList.add(fragment.newInstance(fragment, videoChannelModel));
            }
        }
        notifyDataSetChanged();
    }


    public void remove(@Nullable VideoChannelModel bean) {
        int pos = titleList.indexOf(bean);
        titleList.remove(pos);
        fragmentList.remove(pos);
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return titleList.get(position).hashCode();
    }


    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
