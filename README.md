# CMG_SDK_ANDROID
融媒云SDK Android
#融媒云SDK说明文档

##一、总体说明

```
本SDK包含了资讯列表页，资讯详情页、视频详情页等UI页面，配合管理后台可以快速实现新闻上稿、排版和内容推荐。

1.如果您无需自定义资讯列表的UI样式，可以使用SDK自带的UI页面，包含资讯列表页、资讯详情页，短视频页等
2.如果您需要自定义资讯列表的UI样式，可以使用requestContentList等方法，直接获取资讯瀑布流的原始数据，自行实现其UI渲染代码



由于本SDK包含分享、点赞、评论等涉及用户操作的一些功能，需要您实现以下回调方法：
1.分享功能
2.登录功能
3.用户基本信息	(用户ID，手机号，昵称，头像)

```
##二、Android端集成步骤

1.使用Gradle集成SDK

```
<1>.在模块的build.gradle中添加dependencies {
 implementation 'com.github.CSMediaGroup:CMG_SDK_ANDROID:latest.release'
}
如果使用的support包版本，请依赖 implementation 'com.github.CSMediaGroup:CMG_SDK_ANDROID:support_1.0.6' 对应的support包版本号


<2>.在app级别的 build.gradle 中 
buildscript{
repositories {
 		...
 		maven {url 'https://jitpack.io'}
 		}
}

allprojects{
	//...同上
}

```

2.在自己的Application中初始化中加入

```
   /**
     * isDebug 是否为测试环境
     * appId 你的appId
     */
    @Override
    public void onCreate() {
        super.onCreate();
        ...
 AppInit.init(this, false, "your_appId");
 }

```

3.使用单例类去实现SdkParamCallBack接口 实现其中的方法

```
setSdkUserInfo 设置用户信息
shared  可以拿到分享要素
toLogin 实现跳转登录页面

SdkInteractiveParam.getInstance().setSdkCallBack(new SdkParamCallBack() {
    @Override
    public ThirdUserInfo setSdkUserInfo(SdkUserInfo sdkUserInfo) {
        //传递登录信息
        ThirdUserInfo thirdUserInfo = new ThirdUserInfo();
        thirdUserInfo.setUserId("你那边登录的用户id")
        ...
        return thirdUserInfo;
    }

    @Override
    public void shared(ShareInfo shareInfo) {
        //这里是我传递给你的分享要素
    }

    @Override
    public void toLogin() {
        //这里是你跳转你的登录页面逻辑 去登录
    }
});
```

##四、SDK的使用

###1.使用SDK的UI （使用SDK的列表页样式）

Android：

```
//跳转到资讯首页  
Intent intent = new Intent(MainActivity.this, WebActivity);
startActivity(intent);

###2.自定义UI （获取列表数据，自定义列表样式，使用路由方法进入详情页）

Android：

<1>获取列表数据

//获取列表数据 调用
SzrmRecommend.getInstance().requestContentList("open");

//通过LiveData拿到接口数据
SzrmRecommend.getInstance().contentsEvent.observe(MainActivity.this, new Observer<List<SZContentModel.DataDTO.ContentsDTO>>() {
                    @Override
                    public void onChanged(List<SZContentModel.DataDTO.ContentsDTO> contentsDTOS) {
                    //数据
                    }
                });
                


<2>调用SDK提供的路由方法，进入详情页

SzrmRecommend.getInstance().routeToDetailPage(SZContentModel);
```

##五、其他说明

###1.字段说明

【列表样式】 listStyle

null     左文右图
0        无图
1        左文右图
2        左图右文（可以不实现该样式，目前不会出现）
3        多图
4        大图
5        自定义尺寸的大图  （可以同大图样式处理）



【封面图】thumbnailUrl    (多图新闻格式需要单独转化，如<br> 
"https://cdn-oss.zhcs.csbtv.com/zhcs-prd/images/63bb71e484115300016b9600.jpeg,<br> 
https://cdn-oss.zhcs.csbtv.com/zhcs-prd/images/63bb720684115300016b9602.jpeg,<br> 
https://cdn-oss.zhcs.csbtv.com/zhcs-prd/images/63bb721284115300016b9604.jpeg”）

【标题】title

【时间】createTime    （为国际时间UTC，使用时需要转化为当前时区的时间）

【作者】source

【阅读量】目前没有该数据
```
