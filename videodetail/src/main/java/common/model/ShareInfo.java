package common.model;

import androidx.annotation.Keep;

/**
 * 分享四要素
 */
@Keep
public class ShareInfo {
    /**
     * @param shareH5 分享地址
     * @param shareImageUrl 图片地址
     * @param shareBrief 简介
     * @param shareTitle 分享标题
     * @param platform 分享平台
     */
    public String shareUrl, shareImage, shareBrief, shareTitle, platform;

    public static ShareInfo getInstance(String shareUrl, String shareImage, String shareBrief, String shareTitle, String platform) {
        return new ShareInfo(shareUrl, shareImage, shareBrief, shareTitle, platform);
    }

    public ShareInfo() {

    }

    public ShareInfo(String shareUrl, String shareImage, String shareBrief, String shareTitle, String platform) {
        this.shareUrl = shareUrl;
        this.shareImage = shareImage;
        this.shareBrief = shareBrief;
        this.shareTitle = shareTitle;
        this.platform = platform;
    }

    public ShareInfo(String shareUrl, String shareImage, String shareBrief, String shareTitle) {
        this.shareUrl = shareUrl;
        this.shareImage = shareImage;
        this.shareBrief = shareBrief;
        this.shareTitle = shareTitle;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getShareImage() {
        return shareImage;
    }

    public void setShareImage(String shareImage) {
        this.shareImage = shareImage;
    }

    public String getShareBrief() {
        return shareBrief;
    }

    public void setShareBrief(String shareBrief) {
        this.shareBrief = shareBrief;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}
