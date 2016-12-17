# UCloud多媒体播放器SDK文档

UCDMediaPlayer SDK 是由 UCloud 提供的支持直播、点播播放器。

![screenshot-1](screenshot/screenshot-1.png)  
![screenshot-2](screenshot/screenshot-2.png)  
![screenshot-3](screenshot/screenshot-3.png)  

- 1 [阅读对象](#1)
- 2 [功能特性](#2)
- 3 [开发准备](#3)
    - 3.1 [开发环境配置](#3.1)
    - 3.2 [设备 & 系统](#3.2)
    - 3.3 [混淆说明](#3.3)
- 4 [快速开始](#4)
    - 4.1 [运行 Demo 源码](#4.1)
    - 4.2 [项目集成 SDK](#4.2)
        - step 1: [导入 SDK 库，并添加依赖](#4.2.1)
        - step 2: [添加权限](#4.2.2)
        - step 3: [添加界面元素](#4.2.3)
        - step 4: [设置参数](#4.2.4)
        - step 5: [启动播放](#4.2.5)
- 5 [功能使用](#5)
    - [状态获取](#5.1)
    - [参数设置](#5.2)
- 6 [反馈和建议](#6)
- 7 [版本历史](#7)

<a name="1"></a>
# 1 阅读对象

本文档面向开发人员、测试人员、合作伙伴及对此感兴趣的其他用户，使用该 SDK 需具备基本的 Android 开发能力。

<a name="2"></a>
# 2 功能特性

- 基于 [ijkplayer][1]
- 支持 RTMP、HLS、HTTP-FLV、RTSP 等协议
- 支持直播、点播播放
- 支持软解、硬解切换
- 支持画幅调整
- 支持全屏、非全屏切换
- 支持屏幕亮度调节
- 支持音量调节
- 支持播放进度拖拉操作
- 支持 arm、armv7a、arm64-v8a、x86 等主流芯片体系架构
- 支持 Android 2.3 以上

<a name="3"></a>
# 3 开发准备

<a name="3.1"></a>
## 3.1 开发环境配置

- Android Studio开发工具。官方[下载地址][2]
- 下载 UCDMediaPlayer SDK。

<a name="3.2"></a>
## 3.2 设备 & 系统

- 设备要求：搭载 Android 系统的设备
- 系统要求：Android 2.3+ (API 9+)

<a name="3.3"></a>
## 3.3 混淆说明

为保证正常使用 SDK ，请在 proguard-rules.pro 文件中添加以下代码：

```
-keep class com.ucloud.** { *; } 
-keep class merge.tv.danmaku.ijk.media.player.** { *; }
```

<a name="4"></a>
# 4 快速开始

先下载 Git 源码，然后您可以选择以下两种开始方式，直接运行Demo源码，或者集成SDK到已有项目。

```
git clone https://github.com/umdk/UCDMediaPlayer_Android.git
```

<a name="4.1"></a>
## 4.1 运行Demo源码

打开 Android Studio 菜单 File -> New -> Import Project，选择并指向 git 目录，然后点击 OK 即可。

<a name="4.2"></a>
## 4.2 项目集成 SDK

<a name="4.2.1"></a>
### step 1: 导入 SDK 库，并添加依赖

在已有项目的根目录下新建libs文件夹，并添加 uvod-android-sdk-1.5.3.aar 包(在 git 目录uvod-demo/libs/路径下)。

在 build.gradle 添加以下代码：

```
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    ...
    compile(name:'uvod-android-sdk-1.5.3', ext:'aar')
    ...
}
```

<a name="4.2.2"></a>
### step 2: 添加权限

使用该 SDK 需在 Androidmainfets.xml 中添加以下权限：

```
<!-- common -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

<a name="4.2.3"></a>
### step 3: 添加界面元素

为了能够展示推流预览界面，您需要使用 UVideoView 进行预览：

```
<com.ucloud.uvod.widget.UVideoView
    android:id="@+id/video_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_gravity="center"/>
```

<a name="4.2.4"></a>
### step 4: 设置参数

```
UMediaProfile profile = new UMediaProfile();

// 是否自动播放：0 - 需调用start开始播放；1 - 自动播放
profile.setInteger(UMediaProfile.KEY_START_ON_PREPARED, 1);

// 播放类型：0 - 点播；1 - 直播
profile.setInteger(UMediaProfile.KEY_LIVE_STREAMING, 0); 

// 解码类型：0 - 软解；1 - 硬解
profile.setInteger(UMediaProfile.KEY_MEDIACODEC, 0); 

// 是否允许后台播放：0 - 不允许；1 - 允许
profile.setInteger(UMediaProfile.KEY_ENABLE_BACKGROUND_PLAY, 0); 

mVideoView.setMediaPorfile(profile);

// 设置视频比例，默认VIDEO_RATIO_FIT_PARENT
mVideoView.applyAspectRatio(UVideoView.VIDEO_RATIO_FIT_PARENT);
```

UMediaProfile 为参数设置类，更多内容请参考[参数设置](#5.2)。

<a name="4.2.5"></a>
### step 5: 启动播放

先设置视频播放源，然后调用 UVideoView.start() 即可启动播放：

```
mVideoView.setVideoPath(mUri);
mVideoView.start();
```

> 【小细节】  
> 1. UVideoView 默认自动播放，即 setVideoPath() 后无需调用 start() 也可开始播放。可通过设置 UMediaProfile.KEY_START_ON_PREPARED 参数改变其播放方式。  
> 2. 如果您想使用全屏、调节亮度、声音等功能，请参照 Demo 中 UVideoMainView 的使用。

<a name="5"></a>
# 5 功能使用

**UVideoView** 是整个SDK的核心类，是播放接口的承载者。

<a name="5.1"></a>
## 状态获取

如果您希望获取 SDK 内部状态，您可以通过向 UVideoView 对象设置 Listener 来获取，目前我们支持以下状态的获取：

```
mVideoView.setOnPlayerStateListener(this);
```

- 播放状态返回

| 状态码                                          | 描述         |
| ---                                             | :---:        |
| UPlayerStateListener.State.PREPARING            | 播放准备中   |
| UPlayerStateListener.State.PREPARED             | 播放准备完毕 |
| UPlayerStateListener.State.START                | 播放开始     |
| UPlayerStateListener.State.STOP                 | 播放停止     |
| UPlayerStateListener.State.PAUSE                | 播放暂停     |
| UPlayerStateListener.State.SEEK\_END            | Seek结束     |
| UPlayerStateListener.State.VIDEO\_SIZE\_CHANGED | 视频大小变化 |
| UPlayerStateListener.State.COMPLETED            | 播放结束     |

- 播放信息返回

| 状态码                                      | 描述     |
| ---                                         | :---:    |
| UPlayerStateListener.Info.BUFFERING\_START  | 缓冲开始 |
| UPlayerStateListener.Info.BUFFERING\_END    | 缓冲结束 |
| UPlayerStateListener.Info.BUFFERING\_UPDATE | 更新缓冲 |

- 播放错误返回

| 状态码                                          | 返回码 | 描述     |
| ---                                             | :---:  | :---:    |
| UPlayerStateListener.Error.IOERROR              | -10000 | IO 错误  |
| UPlayerStateListener.Error.PREPARE\_TIMEOUT     | -10006 | 准备超时 |
| UPlayerStateListener.Error.READ\_FRAME\_TIMEOUT | -10007 | 读帧超时 |
| UPlayerStateListener.Error.UNKNOWN              | -1     | 未知错误 |

<a name="5.2"></a>
## 参数设置

如果您希望自定义参数，我们支持以下参数设置：

| 参数名                                   | 描述                                       | 默认值    |
| ---                                      | ---                                        | :---:     |
| UMediaProfile.KEY_LIVE_STREAMING         | 播放类型，0 - 点播; 1 - 直播               | 0         |
| UMediaProfile.KEY_MEDIACODEC             | 解码类型，0 - 软解; 1 - 硬解               | 0         |
| UMediaProfile.KEY_START_ON_PREPARED      | 是否自动播放，0 - 否; 1 - 是               | 0         |
| UMediaProfile.KEY_ENABLE_BACKGROUND_PLAY | 是否后台播放(Android 4.0+)，0 - 否; 1 - 是 | 0         |
| UMediaProfile.KEY_RENDER_SURFACUE        | 是否渲染surface，0 - 否; 1 - 是            | 0         |
| UMediaProfile.KEY_RENDER_TEXTURE         | 是否渲染texture，0 - 否; 1 - 是            | 0         |
| UMediaProfile.KEY_PREPARE_TIMEOUT        | 准备超时(ms)                               | 10 * 1000 |
| UMediaProfile.KEY_READ_FRAME_TIMEOUT     | 读帧超时(ms)                               | 10 * 1000 |

| 方法名                               | 描述                            | 默认值    |
| ---                                  | ---                             | :---:     |
|UVideoView.applyAspectRatio()|画幅：VIDEO\_RATIO\_FIT\_PARENT(以视频源比例适应父控件)，VIDEO\_RATIO\_WRAP\_CONTENT(以视频源比例自适应大小)，VIDEO\_RATIO\_FILL\_PARENT(以视频源比例填充父控件)，VIDEO\_RATIO\_MATCH\_PARENT(以父控件比例铺满父控件)，VIDEO\_RATIO\_16\_9\_FIT\_PARENT(以16:9比例适应父控件)，VIDEO\_RATIO\_4\_3\_FIT\_PARENT(以4:3比例适应父控件)|VIDEO\_RATIO\_FIT\_PARENT|

<a name="6"></a>
# 6 反馈和建议

UMediaPlayer 目前基于 [ijkplayer][1] , 感谢 [ijkplayer][1].

  - 主 页：<https://www.ucloud.cn/>
  - issue：[查看已有 issues 和提交 Bug 推荐][3]
  - 邮 箱：[sdk_spt@ucloud.cn][4]

### 问题反馈参考模板

|名称|描述|
|---|---|
|设备型号|华为 Mate 8|
|系统版本|Android 5.0|
|SDK 版本|v1.4.1|
|问题描述|描述问题现象|
|操作路径|重现问题的操作步骤|
|附件|播放界面截屏、报错日志截图等|

<a name="7"></a>
# 7 版本历史

* v1.5.3 (2016.12.16)
    - 支持https
    - 修改UPlayerStateListener错误回调接口到UI线程
    - 支持aar包

* v1.5.1 (2016.12.07)
    - 针对直播延时优化
    - 优化ijkplayer包名，避免冲突

* v1.5.0 (2016.12.02)
    - 增加UMediaProfile播放器参数设置
    - 优化UVideoView接口

* v1.4.1 (2016.11.03)
    - 修改接口void onEvent(int,String) -> void onEvent(int,Object)
    - 增加setPrepareTimeout(long)接口
    - 增加setReadframeTimeout(long)接口

* v1.3.5 (2015.12.01)
    - 增加UVideoView接口
    - 默认ui界面实现
    - 移至demo

* v1.3.0 (2015.09.16)
    - 增加多清晰度切换
    - 横竖屏切换
    - 画幅调整

* v1.2.0 (2015.07.15)
    - 优化手势seek
    - 音量调节
    - 屏幕亮度控制

* v1.1.0 (2015.06.20)
    - 基本的播放功能

[1]: https://github.com/Bilibili/ijkplayer
[2]: https://developer.android.com/studio/index.html
[3]: https://github.com/umdk/UCDMediaPlayer_Android/issues
[4]: mailto:sdk_spt@ucloud.cn
