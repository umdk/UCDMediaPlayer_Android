package com.ucloud.uvod.example.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.Toast;

import com.ucloud.ucommon.Utils;
import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.example.R;
import com.ucloud.uvod.example.ui.base.UBrightnessHelper;
import com.ucloud.uvod.example.ui.base.UMenuItem;
import com.ucloud.uvod.example.ui.base.UMenuItemHelper;
import com.ucloud.uvod.example.ui.base.UVolumeHelper;
import com.ucloud.uvod.example.ui.widget.URotateLayout;
import com.ucloud.uvod.example.ui.widget.UVerticalProgressView;
import com.ucloud.uvod.widget.UVideoView;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 *
 * Created by lw.tan on 2015/10/10.
 *
 */
public class UVideoMainView extends FrameLayout implements UEasyPlayer, UTopView.Callback, UBottomView.Callback, USettingMenuView.Callback, View.OnClickListener {
    public static final String TAG = "UVideoMainView";

    private Activity activity;

    private static final int MSG_SHOW_TOP_AND_BOTTOM_VIEW = 1;
    private static final int MSG_HIDE_TOP_AND_BOTTOM_VIEW = 2;
    private static final int MSG_SHOW_SETTING_MENU_VIEW = 3;
    private static final int MSG_HIDE_SETTING_MENU_VIEW = 4;
    private static final int MSG_SHOW_LOADING_VIEW = 5;
    private static final int MSG_HIDE_LOADING_VIEW = 6;
    private static final int MSG_UPDATE_PROGRSS = 7;
    private static final int MSG_SHOW_BACKGROUND_VIEW = 8;
    private static final int MSG_HIDE_BACKGROUND_VIEW = 9;
    private static final int MSG_HIDE_FRAME_VIEW = 10;

    private static final int UPDATE_PROGRESS_INTERVAL = 20;
    private static final int MENU_VIEW_ANIMATION_DURATION = 100;

    @BindView(R.id.bottomview)
    UBottomView bottomView;

    @BindView(R.id.topview)
    UTopView topView;

    @BindView(R.id.videoview)
    UVideoView videoView;

    @BindView(R.id.setting_menu_view_ll)
    USettingMenuView settingMenuView;

    @BindView(R.id.volume_view)
    UVerticalProgressView volumeView;

    @BindView(R.id.brightness_view)
    UVerticalProgressView brightnessView;

    @BindView(R.id.loading)
    View loadingView;

    @BindView(R.id.loading_container)
    View loadingContainer;

    @BindView(R.id.circle_play_status)
    View playerStatusView;

    TableLayout debugInfoHudView;

    @BindView(R.id.rl_player_bg)
    View playerBackgroundView;

    @BindView(R.id.rotate_layout)
    URotateLayout rotateLayout;

    @BindView(R.id.img_view_frame)
    ImageView imgViewFrame;

    @BindView(R.id.img_btn_frame_camera)
    ImageView imgBtnFrameCamera;

    private int ratio = UVideoView.VIDEO_RATIO_FIT_PARENT;
    private float speed = 1.0f;

    private GestureDetector gestureDetector;

    private InnerGestureDetector innerGestrueDetectoer;

    private int screenWidth;

    private int screenHeight;

    private USettingMenuView.Callback settingMenuItemSelectedListener;

    private UPlayerStateListener outterPlayerStateListener;

    private UPlayerStateListener playerStateListener;

    private boolean isFastSeekMode;

    protected String uri;

    private boolean isSuccess = true;

    private boolean isInitSettingMenu = false;

    private boolean isFullscreen;

    private UMediaProfile avProfile;

    private boolean isPausedByManual = false;

    private int seekWhenPrepared = 0;

    private boolean isCompleted;

    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_TOP_AND_BOTTOM_VIEW:
                    doShowNavigationBar();
                    break;
                case MSG_HIDE_TOP_AND_BOTTOM_VIEW:
                    doHideNavigationBar();
                    break;
                case MSG_SHOW_SETTING_MENU_VIEW:
                    doShowSettingMenuView();
                    break;
                case MSG_HIDE_SETTING_MENU_VIEW:
                    doHideSettingMenuView();
                    break;
                case MSG_SHOW_LOADING_VIEW:
                    doShowLoadingView();
                    break;
                case MSG_HIDE_LOADING_VIEW:
                    doHideLoadingView();
                    break;
                case MSG_UPDATE_PROGRSS:
                    doUpdateProgress();
                    break;
                case MSG_SHOW_BACKGROUND_VIEW:
                    doShowBackgroundView();
                    break;
                case MSG_HIDE_BACKGROUND_VIEW:
                    doHideBackgroundView();
                    break;
                case MSG_HIDE_FRAME_VIEW:
                    doHideFrameView();
                    break;
                default:
                    break;
            }
        }
    };

    public UVideoMainView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public UVideoMainView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init(context);
    }

    public UVideoMainView(Context context) {
        this(context, null);
        init(context);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(Context context) {
        avProfile = new UMediaProfile();
        innerGestrueDetectoer = new InnerGestureDetector();
        gestureDetector = new GestureDetector(getContext(), innerGestrueDetectoer);
        setOnTouchListener(gestureTouchListener);
        updateScreenWidthAndHeight(context);
    }

    private void updateScreenWidthAndHeight(Context context) {
        Pair<Integer, Integer> resolution = Utils.getResolution(context);
        screenWidth = resolution.first;
        screenHeight = resolution.second;
        isFullscreen = screenWidth >= screenHeight;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        loadViews();
        initVolumeHelper();
        initBrightnessHelper();
        initListeners();
    }

    private void initVolumeHelper() {
        UVolumeHelper volumeHelper = new UVolumeHelper(getContext());
        if (volumeView != null) {
            volumeView.setIconNormalResId(R.drawable.player_icon_bottomview_volume_button_normal);
            volumeView.setHelper(volumeHelper);
        }
    }

    private void initBrightnessHelper() {
        UBrightnessHelper brightnessHelper = new UBrightnessHelper(getContext());
        if (brightnessView != null) {
            brightnessView.setIconNormalResId(R.drawable.player_icon_bottomview_brightness_button_normal);
            brightnessView.setHelper(brightnessHelper);
        }
    }

    private void loadViews() {
        ButterKnife.bind(this);
    }

    private void initListeners() {
        if (topView != null) {
            topView.setCallback(this);
        }
        if (bottomView != null) {
            bottomView.setCallback(this);
            bottomView.setPlayerController(this);
        }
        if (playerStatusView != null) {
            playerStatusView.setOnClickListener(playerStatusViewClickListener);
        }

        imgBtnFrameCamera.setOnClickListener(this);
    }

    OnClickListener playerStatusViewClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (videoView != null && !videoView.isPlaying()) {
                togglePlayerToPlay();
            }
        }
    };

    public void setVideoPath(String uri) {
        seekWhenPrepared = 0;
        isCompleted = false;
        this.uri = uri;
        videoView.setOnPlayerStateListener(playerStateLisnter);
        videoView.setMediaPorfile(avProfile);
        videoView.setHudView(debugInfoHudView);
//        videoView.setVideoPath(this.uri);
        videoView.setVideoPath(uri, 0);
        videoView.applyAspectRatio(ratio);
    }

    UPlayerStateListener playerStateLisnter = new UPlayerStateListener() {
        @Override
        public void onPlayerStateChanged(UPlayerStateListener.State state, int extra1, Object extra2) {
            Log.e(TAG, "onPlayerStateChanged ->" + state.name());
            switch (state) {
                case PREPARING:
                    notifyShowLoadingView(0);
                    break;
                case PREPARED:
                    dealOnPrepared();
                    if (videoView != null && seekWhenPrepared >= 0) {
                        videoView.seekTo(seekWhenPrepared);
                    }
                    break;
                case START:
                    isCompleted = false;
                    notifyUpdateProgress(0);
                    playerStatusView.setVisibility(View.GONE);
                    bottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_pause_button_normal);
                    bottomView.release();
                    break;
                case PAUSE:
                    break;
                case COMPLETED:
                    seekWhenPrepared = 0;
                    isCompleted = true;
                    dealCompletion();
                    break;
                case SEEK_END:
                    isCompleted = false;
                    notifyUpdateProgress(0);
                    break;
                default:
                    break;
            }
            if (outterPlayerStateListener != null) {
                outterPlayerStateListener.onPlayerStateChanged(state, extra1, extra2);
            }
        }

        @Override
        public void onPlayerInfo(Info info, int extra1, Object extra2) {
            switch (info) {
                case BUFFERING_START:
                    notifyShowLoadingView(0);
                    break;
                case BUFFERING_END:
                    notifyHideLoadingView(0);
                    break;
                case BUFFERING_PERCENT:
//                    Log.e(TAG, "percent = " + extra2);
                    // 缓冲百分比回调
                    break;
                case VIDEO_RENDERING_START:
                    notifyHideLoadingView(0);
                    notifyHideBackgroundView(0);
                    break;
                case AUDIO_RENDERING_START:
                    notifyHideLoadingView(0);
                    break;
                default:
                    break;
            }
            if (outterPlayerStateListener != null) {
                outterPlayerStateListener.onPlayerInfo(info, extra1, extra2);
            }
        }

        @Override
        public void onPlayerError(UPlayerStateListener.Error error, int extra1, Object extra2) {
            if (outterPlayerStateListener != null) {
                outterPlayerStateListener.onPlayerError(error, extra1, extra2);
            }
        }
    };

    @Override
    public void onResume() {
        if (!isPausedByManual) {
            videoView.onResume();
        }
    }

    public boolean isNavigationBarShown() {
        return bottomView != null && bottomView.getVisibility() == View.VISIBLE;
    }

    public void notifyShowNavigationBar(int delay) {
        uiHandler.removeMessages(MSG_SHOW_TOP_AND_BOTTOM_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_TOP_AND_BOTTOM_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void notifyHideNavigationBar(int delay) {
        uiHandler.removeMessages(MSG_HIDE_TOP_AND_BOTTOM_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_TOP_AND_BOTTOM_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void notifyShowSettingMenuView(int delay) {
        uiHandler.removeMessages(MSG_SHOW_SETTING_MENU_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_SETTING_MENU_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void notifyHideSettingMenuView(int delay) {
        uiHandler.removeMessages(MSG_HIDE_SETTING_MENU_VIEW);
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_SETTING_MENU_VIEW;
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public boolean isSettingMenuViewShown() {
        return settingMenuView != null && settingMenuView.getVisibility() == View.VISIBLE;
    }

    private void doShowSettingMenuView() {
        if (settingMenuView != null && settingMenuView.getVisibility() != View.VISIBLE && isSuccess) {
            settingMenuView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(MENU_VIEW_ANIMATION_DURATION);
            settingMenuView.startAnimation(ta);
        }
    }

    private void doHideSettingMenuView() {
        if (settingMenuView != null && isSuccess) {
            settingMenuView.setVisibility(View.GONE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(MENU_VIEW_ANIMATION_DURATION);
            settingMenuView.startAnimation(ta);
        }
    }

    private void doShowNavigationBar() {
        if (bottomView != null && bottomView.getVisibility() != View.VISIBLE) {
            bottomView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(100);
            bottomView.startAnimation(ta);
        }
        if (topView != null && topView.getVisibility() != View.VISIBLE) {
            topView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(100);
            topView.startAnimation(ta);
        }
    }

    private void doHideNavigationBar() {
        if (bottomView != null && bottomView.getVisibility() == View.VISIBLE) {
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
            ta.setDuration(100);
            bottomView.startAnimation(ta);
            bottomView.setVisibility(View.GONE);
        }
        if (topView != null && topView.getVisibility() == View.VISIBLE) {
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
            ta.setDuration(100);
            topView.startAnimation(ta);
            topView.setVisibility(View.GONE);
        }
    }

    private void singleTapConfirmed() {
        if (bottomView != null && bottomView.isShown()) {
            notifyHideNavigationBar(0);
        }
        else {
            if (!isNavigationBarShown()) {
                notifyShowNavigationBar(0);
            }
        }
    }

    private void hideVolumeView() {
        if (volumeView != null && volumeView.getVisibility() == View.VISIBLE) {
            volumeView.setVisibility(View.GONE);
        }
        if (brightnessView != null && brightnessView.getVisibility() == View.VISIBLE) {
            brightnessView.setVisibility(View.GONE);
        }
    }

    OnTouchListener gestureTouchListener = new OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean result = gestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (videoView != null && isFastSeekMode && bottomView != null && bottomView.getLastFastSeekPosition() != -1) {
                        if (videoView.canSeekForward()) {
                            notifyShowLoadingView(1);
                            videoView.seekTo(bottomView.getLastFastSeekPosition());
                            bottomView.notifyHideFaskSeekIndexBar(1000);
                            bottomView.notifyUpdateVideoProgressBar(bottomView.getLastFastSeekPosition());
                            bottomView.setLastFastSeekPosition(-1);
                        }
                        isFastSeekMode = false;
                    }
                    if (innerGestrueDetectoer != null) {
                        innerGestrueDetectoer.init();
                    }
                    break;
                default:
                    break;
            }
            return result;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_btn_frame_camera:
                if (videoView == null) {
                    return;
                }

                Bitmap frame = videoView.getCurrentVideoBitmap();

                if (frame != null && imgViewFrame != null) {
                    imgViewFrame.invalidate();
                    imgViewFrame.setImageBitmap(frame);
                    imgViewFrame.setVisibility(View.VISIBLE);
                    notifyHideFrameView(2000);
                } else {
                    Toast.makeText(getContext(), R.string.info6, Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    class InnerGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private float x1 = -1;
        private float y1 = -1;
        private int minSlideDistance = 40;
        private int minVerticalSlideDistance = minSlideDistance;
        private int minHorizontalSlideDistance = minSlideDistance;
        private boolean isSeekEnable = false;
        InnerGestureDetector() {
            init();
        }

        void init() {
            x1 = -1;
            y1 = -1;
            ViewConfiguration mViewConfiguration = ViewConfiguration.get(getContext());
            minSlideDistance = mViewConfiguration.getScaledTouchSlop();
            minHorizontalSlideDistance = minVerticalSlideDistance = minSlideDistance;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            x1 = -1;
            y1 = -1;
            if (bottomView != null) {
                bottomView.setLastSeekPosition(-1);
            }
            return false;
        }

        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float velocityX, float velocityY) {
            try {
                if (x1 == -1 || y1 == -1) {
                    x1 = e1.getX();
                    y1 = e1.getY();
                }
                int slideX = (int) (e2.getX() - x1);
                int slideY = (int) (e2.getY() - y1);
                boolean slideVertical = Math.abs(slideY) >= Math.abs(slideX);
                if (slideVertical && Math.abs(slideY) > minVerticalSlideDistance
                        && x1 > screenWidth / 2) {
                    volumeView.change(slideY < 0, false);
                    brightnessView.setVisibility(View.GONE);
                    volumeView.setVisibility(View.VISIBLE);
                    x1 = e2.getX();
                    y1 = e2.getY();
                    minHorizontalSlideDistance = screenWidth;
                    return true;
                }
                if (slideVertical && Math.abs(slideY) > minVerticalSlideDistance
                        && x1 < screenWidth / 2) {
                    brightnessView.change(slideY < 0, false);
                    volumeView.setVisibility(View.GONE);
                    brightnessView.setVisibility(View.VISIBLE);
                    x1 = e2.getX();
                    y1 = e2.getY();
                    minHorizontalSlideDistance = screenWidth;
                    return true;
                }
                if (isSuccess && videoView.isInPlaybackState() && isSeekEnable) {
                    if (!slideVertical && Math.abs(slideX) > minHorizontalSlideDistance) {
                        isFastSeekMode = true;
                        if (!isNavigationBarShown()) {
                            notifyShowNavigationBar(0);
                        }
                        bottomView.notifyShowFaskSeekIndexBar(0);
                        bottomView.fastSeek(slideX > 0);
                        x1 = e2.getX();
                        y1 = e2.getY();
                        minVerticalSlideDistance = screenHeight;
                        return true;
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

        public boolean onSingleTapConfirmed(MotionEvent e) {
            try {
                hideVolumeView();
                float x2 = e.getX();
                float range;
                if (!isFullscreen()) {
                    range = screenWidth;
                }
                else {
                    range = 5 * screenWidth / 6;
                }
                if (videoView != null && !videoView.isInPlaybackState()) {
                    return super.onSingleTapConfirmed(e);
                }

                if (!isSettingMenuViewShown() && x2 >= range) {
                    notifyHideNavigationBar(0);
                    if (isFullscreen()) {
                        notifyShowSettingMenuView(0);
                    }
                }
                else {
                    if (isSettingMenuViewShown()) {
                        notifyHideSettingMenuView(10);
                    }
                    else {
                        singleTapConfirmed();
                    }
                }
            }
            catch (Exception error) {
                error.printStackTrace();
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public boolean onLeftButtonClick(View view) {
        if (isFullscreen()) {
            toggleScreenOrientation();
        }
        else {
            activity.finish();
        }
        return false;
    }

    @Override
    public boolean onRightButtonClick(View view) {
        toggleScreenOrientation();
        return false;
    }

    @Override
    public void init(Activity context) {
        activity = context;
    }

    @Override
    public void toggleScreenOrientation() {
        if (rotateLayout != null) {
            rotateLayout.toggleOrientation();
        }
    }

    public void setScreenOriention(int oriention) {
        if (videoView != null) {
            rotateLayout.setOrientation(oriention);
        }
    }

    @Override
    public void setPlayerStateLisnter(UPlayerStateListener l) {
        outterPlayerStateListener = l;
    }

    @Override
    public void setMediaProfile(UMediaProfile profile) {
        this.avProfile = profile;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (!isFullscreen()) {
            if (settingMenuView != null) {
                settingMenuView.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean isFullscreen() {
        updateScreenWidthAndHeight(getContext());
        return isFullscreen;
    }

    @Override
    public boolean onPlayButtonClick(View view) {
        if (videoView != null) {
            if (videoView.isPlaying()) {
                isPausedByManual = true;
                togglePlayerToPause();
            }
            else {
                isPausedByManual = false;
                togglePlayerToPlay();
            }
        }
        return false;
    }

    private void togglePlayerToPause() {
        videoView.pause();
        playerStatusView.setVisibility(View.VISIBLE);
        bottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_play_button_normal);
    }

    private void togglePlayerToPlay() {
        playerStatusView.setVisibility(View.GONE);
        if (videoView.isInPlaybackState()) {
            videoView.start();
        } else {
            videoView.setVideoPath(uri);
        }

        bottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_pause_button_normal);
    }

    public void dealOnPrepared() {
        notifyUpdateProgress(0);
        if (!isInitSettingMenu) {
            UMenuItemHelper menuItemHelper = UMenuItemHelper.getInstance(getContext());
            menuItemHelper.release();
            menuItemHelper.register(UMenuItemHelper.getInstance(getContext()).buildVideoRatioMenuItem(ratio), true);
            menuItemHelper.register(UMenuItemHelper.getInstance(getContext()).buildVideoPlayerMenuItem(avProfile.getInteger(UMediaProfile.KEY_MEDIACODEC, 0)));
            menuItemHelper.register(UMenuItemHelper.getInstance(getContext()).buildSpeedModeMenuItem(1), true);
            settingMenuView.init();
            settingMenuView.setOnMenuItemSelectedListener(this);
            isInitSettingMenu = true;
        }
        boolean isCanSeek = !videoView.isLiveStreaming();
        bottomView.setVisibility(View.VISIBLE);
        innerGestrueDetectoer.isSeekEnable = isCanSeek;
        bottomView.setSeekEnable(isCanSeek);
    }

    public void dealCompletion() {
        if (bottomView != null) {
            uiHandler.removeMessages(MSG_UPDATE_PROGRSS);
            int duration = getDuration();
            bottomView.onPositionChanaged(duration, duration);
        }
        if (bottomView != null && videoView != null && playerStatusView != null) {
            togglePlayerToPause();
//            videoView.reset(); //清除最后一帧画面
        }
    }

    @Override
    public boolean isInPlaybackState() {
        return videoView != null && videoView.isInPlaybackState();
    }

    @Override
    public int getDuration() {
        if (videoView != null) {
            return videoView.getDuration();
        }
        return 0;
    }

    @Override
    public void seekTo(int position) {
        if (videoView != null) {
            videoView.seekTo(position);
        }
    }

    @Override
    public void showNavigationBar(int delay) {
        notifyShowNavigationBar(delay);
    }

    @Override
    public int getCurrentPosition() {
        if (videoView != null) {
            return videoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int toggleAspectRatio() {
        return videoView.toggleAspectRatio();
    }

    @Override
    public int toggleRender() {
        return videoView.toggleRender();
    }

    @Override
    public void onPause() {
        if (videoView != null) {
            videoView.onPause();
            isFullscreen = isFullscreen();
        }
    }

    @Override
    public boolean onBrightnessButtonClick(View view) {
        if (brightnessView != null && brightnessView.isShown()) {
            brightnessView.setVisibility(View.GONE);
        }
        else {
            assert brightnessView != null;
            brightnessView.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public boolean onVolumeButtonClick(View view) {
        if (volumeView != null && volumeView.getVisibility() == View.VISIBLE) {
            volumeView.setVisibility(View.GONE);
        }
        else {
            assert volumeView != null;
            volumeView.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        return super.onSaveInstanceState();
    }

    @Override
    public void onDestroy() {
        UMenuItemHelper.getInstance(getContext()).release();
        videoView.onDestroy();
        seekWhenPrepared = 0;
        activity = null;
        debugInfoHudView = null;
    }

    private void notifyShowLoadingView(int duration) {
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_LOADING_VIEW;
        uiHandler.removeMessages(MSG_SHOW_LOADING_VIEW);
        uiHandler.sendMessageDelayed(msg, duration);
    }

    private void notifyShowBackgroundView(int duration) {
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_BACKGROUND_VIEW;
        uiHandler.removeMessages(MSG_SHOW_BACKGROUND_VIEW);
        uiHandler.sendMessageDelayed(msg, duration);
    }

    private void notifyHideBackgroundView(int duration) {
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_BACKGROUND_VIEW;
        uiHandler.removeMessages(MSG_HIDE_BACKGROUND_VIEW);
        uiHandler.sendMessageDelayed(msg, duration);
    }

    private void notifyHideLoadingView(int duration) {
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_LOADING_VIEW;
        uiHandler.removeMessages(MSG_HIDE_LOADING_VIEW);
        uiHandler.sendMessageDelayed(msg, duration);
    }

    private void doShowBackgroundView() {
        if (playerBackgroundView != null) {
            playerBackgroundView.setVisibility(View.VISIBLE);
        }
    }

    private void doHideBackgroundView() {
        if (playerBackgroundView != null) {
            playerBackgroundView.setVisibility(View.GONE);
        }
    }

    private void doShowLoadingView() {
        if (loadingContainer != null && loadingContainer.getVisibility() == View.GONE && loadingView != null) {
            loadingContainer.setVisibility(View.VISIBLE);
        }
    }

    private void doHideLoadingView() {
        if (loadingContainer != null && loadingView != null) {
            loadingContainer.setVisibility(View.GONE);
        }
    }

    private void doUpdateProgress() {
        if (videoView != null && videoView.isInPlaybackState() && !isCompleted) {
            int currnetPosition = videoView.getCurrentPosition();
            int duration = videoView.getDuration();
            if (bottomView != null) {
                bottomView.onPositionChanaged(currnetPosition, duration);
            }
            notifyUpdateProgress(UPDATE_PROGRESS_INTERVAL);
        }
    }

    private void doHideFrameView() {
        imgViewFrame.setVisibility(View.GONE);
    }

    private void notifyUpdateProgress(int delay) {
        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_PROGRSS;
        uiHandler.removeMessages(msg.what);
        uiHandler.sendMessageDelayed(msg, delay);
    }

    private void notifyHideFrameView(int delay) {
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_FRAME_VIEW;
        uiHandler.removeMessages(msg.what);
        uiHandler.sendMessageDelayed(msg, delay);
    }

    public void initAspectRatio(int ratio) {
        this.ratio = ratio;
    }

    @Override
    public UVideoView getVideoView() {
        return videoView;
    }

    @Override
    public void setHudView(TableLayout hudView) {
        debugInfoHudView = hudView;
    }

    @Override
    public void setMenuItemSelectedListener(USettingMenuView.Callback l) {
        settingMenuItemSelectedListener = l;
    }

    @Override
    public boolean onSettingMenuSelected(UMenuItem item) {
        boolean flag = false;
        if (settingMenuItemSelectedListener != null) {
            flag = settingMenuItemSelectedListener.onSettingMenuSelected(item);
        }
        if (!flag) {
            try {
                if (item.parent != null) {
                    if (item.parent.title.equals(activity.getResources().getString(R.string.menu_item_title_ratio))) {
                        videoView.applyAspectRatio(Integer.parseInt(item.type));
                    }
                    else if (item.parent.title.equals(activity.getResources().getString(R.string.menu_item_title_videocodec))) {
                      //根据需要自行决定切换时是否需要显示封面图片，自定义封面在布局文件指定或代码实现
                        //notifyShowBackgroundView(0);
                        notifyShowLoadingView(0);
                        videoView.pause();
                        seekWhenPrepared = videoView.getCurrentPosition();
                        //具备清除上一次播放的画面效果， 当uri发生变化时，内部默认会自动清除
//                         videoView.reset();
                        videoView.getMediaProfile().setInteger(UMediaProfile.KEY_MEDIACODEC, Integer.parseInt(item.type));
                        videoView.setVideoPath(uri, seekWhenPrepared);
                    }
                    else if (item.parent.title.equals(activity.getResources().getString(R.string.menu_item_title_speed_mode))) {
                        videoView.setSpeed(Float.parseFloat(item.type));
                    }
                    notifyHideSettingMenuView(0);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
