package com.ucloud.uvod.example.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.example.R;
import com.ucloud.uvod.example.ui.base.UBrightnessHelper;
import com.ucloud.uvod.example.ui.base.UMenuItem;
import com.ucloud.uvod.example.ui.base.UMenuItemHelper;
import com.ucloud.uvod.example.ui.base.UVolumeHelper;
import com.ucloud.uvod.example.ui.widget.URotateVideoView;
import com.ucloud.uvod.example.ui.widget.UVerticalProgressView;
import com.ucloud.uvod.common.Utils;
import com.ucloud.uvod.widget.UVideoView;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 *
 * Created by lw.tan on 2015/10/10.
 *
 */
public class UVideoMainView extends FrameLayout implements UEasyPlayer, UTopView.Callback, UBottomView.Callback, USettingMenuView.Callback {
    public static final String TAG = "UVideoMainView";
    private Activity mContext;
    private static final int MSG_SHOW_TOP_AND_BOTTOM_VIEW = 1;
    private static final int MSG_HIDE_TOP_AND_BOTTOM_VIEW = 2;

    private static final int MSG_SHOW_SETTING_MENU_VIEW = 7;
    private static final int MSG_HIDE_SETTING_MENU_VIEW = 8;

    private static final int MSG_SHOW_LOADING_VIEW = 13;
    private static final int MSG_HIDE_LOADING_VIEW = 14;
    private static final int MSG_UPDATE_PROGRSS = 15;

    private static final int UPDATE_PROGRESS_INTERVAL = 1000;

    private int mMenuViewShowOrHideAnimationDuration = 100;

    @Bind(R.id.bottomview)
    UBottomView mBottomView;

    @Bind(R.id.topview)
    UTopView mTopView;

    @Bind(R.id.videoview)
    URotateVideoView mRotateVideoView;

    @Bind(R.id.setting_menu_view_ll)
    USettingMenuView mSettingMenuView;

    @Bind(R.id.volume_view)
    UVerticalProgressView mVolumeView;

    @Bind(R.id.brightness_view)
    UVerticalProgressView mBrightnessView;

    @Bind(R.id.loading)
    View mLoadingView;

    @Bind(R.id.loading_container)
    View mLoadingContainer;

    @Bind(R.id.circle_play_status)
    View mPlayStatusView;

    TableLayout mHudView;

    private int mRatio = UVideoView.VIDEO_RATIO_FIT_PARENT;

    private int mOriention = URotateVideoView.ORIENTATION_SENSOR;

    private GestureDetector mGestureDetector;
    private InnerGestureDetector mInnerGestrueDetectoer;
    private int mScreenWidth;
    private int mScreenHeight;
    private USettingMenuView.Callback mSettingMenuItemSelectedListener;

    private UPlayerStateListener mOnPlayerStateListener;

    private boolean isFastSeekMode;

    protected String mUri;

    private boolean isSuccess = true;

    private boolean isInitSettingMenu = false;

    private boolean isFullscreen;

    private UMediaProfile avProfile;

    private boolean isPausedByManual = false;

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
        mInnerGestrueDetectoer = new InnerGestureDetector();
        mGestureDetector = new GestureDetector(getContext(), mInnerGestrueDetectoer);
        setOnTouchListener(mGestureTouchListener);
        updateScreenWidthAndHeight(context);
    }

    private void updateScreenWidthAndHeight(Context context) {
        Pair<Integer, Integer> resolution = Utils.getResolution(context);
        mScreenWidth = resolution.first;
        mScreenHeight = resolution.second;
        isFullscreen = mScreenWidth >= mScreenHeight;
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
        if (mVolumeView != null) {
            mVolumeView.setIconNormalResId(R.drawable.player_icon_bottomview_volume_button_normal);
            mVolumeView.setHelper(volumeHelper);
        }
    }

    private void initBrightnessHelper() {
        UBrightnessHelper brightnessHelper = new UBrightnessHelper(getContext());
        if (mBrightnessView != null) {
            mBrightnessView.setIconNormalResId(R.drawable.player_icon_bottomview_brightness_button_normal);
            mBrightnessView.setHelper(brightnessHelper);
        }
    }

    private void loadViews() {
        ButterKnife.bind(this);
    }

    private void initListeners() {
        if (mTopView != null) {
            mTopView.registerCallback(this);
        }
        if (mBottomView != null) {
            mBottomView.registerCallback(this);
            mBottomView.setPlayerController(this);
        }
        if (mPlayStatusView != null) {
            mPlayStatusView.setOnClickListener(mPlayStatusViewClickListener);
        }
    }

    OnClickListener mPlayStatusViewClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (mRotateVideoView != null && !mRotateVideoView.isPlaying()) {
                togglePlayerToPlay();
            }
        }
    };

    public void setVideoPath(String uri) {
        mUri = uri;
        mRotateVideoView.setOnPlayerStateListener(mPlayerStateLisnter);
        mRotateVideoView.setMediaPorfile(avProfile);
        mRotateVideoView.setHudView(mHudView);
        mRotateVideoView.setVideoPath(mUri);
        mRotateVideoView.applyAspectRatio(mRatio);
    }

    UPlayerStateListener mPlayerStateLisnter = new UPlayerStateListener() {
        @Override
        public void onPlayerStateChanged(UPlayerStateListener.State state, int extra1, Object extra2) {
            switch (state) {
                case PREPARING:
                    notifyShowLoadingView(0);
                    break;
                case PREPARED:
                    notifyHideLoadingView(0);
                    dealOnPrepared();
                    break;
                case START:
                    mPlayStatusView.setVisibility(View.GONE);
                    mBottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_pause_button_normal);
                    mBottomView.release();
                    break;
                case PAUSE:
                    break;
                case COMPLETED:
                    dealCompletion();
                    break;
                case SEEK_END:
                    notifyHideLoadingView(1000);
                    break;
            }
            if (mOnPlayerStateListener != null) {
                mOnPlayerStateListener.onPlayerStateChanged(state, extra1, extra2);
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
            }
            if (mOnPlayerStateListener != null) {
                mOnPlayerStateListener.onPlayerInfo(info, extra1, extra2);
            }
        }

        @Override
        public void onPlayerError(UPlayerStateListener.Error error, int extra1, Object extra2) {
            if (mOnPlayerStateListener != null) {
                mOnPlayerStateListener.onPlayerError(error, extra1, extra2);
            }
        }
    };

    @Override
    public void onResume() {
        if (!isPausedByManual) {
            mRotateVideoView.onResume();
        }
    }

    public boolean isNavigationBarShown() {
        return mBottomView != null && mBottomView.getVisibility() == View.VISIBLE;
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
        return mSettingMenuView != null && mSettingMenuView.getVisibility() == View.VISIBLE;
    }

    private void doShowSettingMenuView() {
        if (mSettingMenuView != null && mSettingMenuView.getVisibility() != View.VISIBLE && isSuccess) {
            mSettingMenuView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(mMenuViewShowOrHideAnimationDuration);
            mSettingMenuView.startAnimation(ta);
        }
    }

    private void doHideSettingMenuView() {
        if (mSettingMenuView != null && isSuccess) {
            mSettingMenuView.setVisibility(View.GONE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(mMenuViewShowOrHideAnimationDuration);
            mSettingMenuView.startAnimation(ta);
        }
    }

    private void doShowNavigationBar() {
        if (mBottomView != null && mBottomView.getVisibility() != View.VISIBLE) {
            mBottomView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(100);
            mBottomView.startAnimation(ta);
        }
        if (mTopView != null && mTopView.getVisibility() != View.VISIBLE) {
            mTopView.setVisibility(View.VISIBLE);
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f, Animation.RELATIVE_TO_SELF, 0f);
            ta.setDuration(100);
            mTopView.startAnimation(ta);
        }
    }

    private void doHideNavigationBar() {
        if (mBottomView != null && mBottomView.getVisibility() == View.VISIBLE) {
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
            ta.setDuration(100);
            mBottomView.startAnimation(ta);
            mBottomView.setVisibility(View.GONE);
        }
        if (mTopView != null && mTopView.getVisibility() == View.VISIBLE) {
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, -1f);
            ta.setDuration(100);
            mTopView.startAnimation(ta);
            mTopView.setVisibility(View.GONE);
        }
    }

    private void singleTapConfirmed() {
        if (mBottomView != null && mBottomView.isShown()) {
            notifyHideNavigationBar(0);
        } else {
            if (!isNavigationBarShown()) {
                notifyShowNavigationBar(0);
            }
        }
    }

    private void hideVolumeView() {
        if (mVolumeView != null && mVolumeView.getVisibility() == View.VISIBLE) {
            mVolumeView.setVisibility(View.GONE);
        }
        if (mBrightnessView != null && mBrightnessView.getVisibility() == View.VISIBLE) {
            mBrightnessView.setVisibility(View.GONE);
        }
    }

    OnTouchListener mGestureTouchListener = new OnTouchListener() {
        @SuppressLint("ClickableViewAccessibility")
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean result = mGestureDetector.onTouchEvent(event);
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if (mRotateVideoView != null && isFastSeekMode && mBottomView != null && mBottomView.getLastFastSeekPosition() != -1) {
                        if (mRotateVideoView.canSeekForward()) {
                            notifyShowLoadingView(0);
                            mRotateVideoView.seekTo(mBottomView.getLastFastSeekPosition());
                        }
                        isFastSeekMode = false;
                        mBottomView.notifyHideFaskSeekIndexBar(1000);
                        mBottomView.notifyUpdateVideoProgressBar(mBottomView.getLastFastSeekPosition());
                        mBottomView.setLastFastSeekPosition(-1);
                    }
                    if (mInnerGestrueDetectoer != null) {
                        mInnerGestrueDetectoer.init();
                    }
                    break;
                default:
                    break;
            }
            return result;
        }
    };

    class InnerGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private float x1 = -1;
        private float y1 = -1;
        private int MIN_SLIDE_DISTANCE = 40;
        private int mMinVerticalSlideDistance = MIN_SLIDE_DISTANCE;
        private int mMinHorizontalSlideDistance = MIN_SLIDE_DISTANCE;
        private boolean isSeekEnable = false;
        public InnerGestureDetector() {
            init();
        }

        public void init() {
            x1 = -1;
            y1 = -1;
            ViewConfiguration mViewConfiguration = ViewConfiguration.get(getContext());
            MIN_SLIDE_DISTANCE = mViewConfiguration.getScaledTouchSlop();
            mMinHorizontalSlideDistance = mMinVerticalSlideDistance = MIN_SLIDE_DISTANCE;
        }

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {
            x1 = -1;
            y1 = -1;
            if (mBottomView != null) {
                mBottomView.setLastSeekPosition(-1);
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
                if (slideVertical && Math.abs(slideY) > mMinVerticalSlideDistance
                        && x1 > mScreenWidth / 2) {
                    mVolumeView.change(slideY < 0, false);
                    mBrightnessView.setVisibility(View.GONE);
                    mVolumeView.setVisibility(View.VISIBLE);
                    x1 = e2.getX();
                    y1 = e2.getY();
                    mMinHorizontalSlideDistance = mScreenWidth;
                    return true;
                }
                if (slideVertical && Math.abs(slideY) > mMinVerticalSlideDistance
                        && x1 < mScreenWidth / 2) {
                    mBrightnessView.change(slideY < 0, false);
                    mVolumeView.setVisibility(View.GONE);
                    mBrightnessView.setVisibility(View.VISIBLE);
                    x1 = e2.getX();
                    y1 = e2.getY();
                    mMinHorizontalSlideDistance = mScreenWidth;
                    return true;
                }
                if (isSuccess && mRotateVideoView.isInPlaybackState() && isSeekEnable) {
                    if (!slideVertical && Math.abs(slideX) > mMinHorizontalSlideDistance) {
                        isFastSeekMode = true;
                        if (!isNavigationBarShown()) {
                            notifyShowNavigationBar(0);
                        }
                        mBottomView.notifyShowFaskSeekIndexBar(0);
                        mBottomView.fastSeek(slideX > 0);
                        x1 = e2.getX();
                        y1 = e2.getY();
                        mMinVerticalSlideDistance = mScreenHeight;
                        return true;
                    }
                }
            } catch (Exception e) {
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
                    range = mScreenWidth;
                } else {
                    range = 5 * mScreenWidth / 6;
                }
                if (mRotateVideoView != null && !mRotateVideoView.isInPlaybackState()) {
                    return super.onSingleTapConfirmed(e);
                }

                if (!isSettingMenuViewShown() && x2 >= range) {
                    notifyHideNavigationBar(0);
                    if (isFullscreen()) {
                        notifyShowSettingMenuView(0);
                    }
                } else {
                    if (isSettingMenuViewShown()) {
                        notifyHideSettingMenuView(0);
                    }
                    else {
                        singleTapConfirmed();
                    }
                }
            } catch (Exception error) {
                error.printStackTrace();
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public boolean onLeftButtonClick(View view) {
        if(isFullscreen()) toggleScreenOrientation();
        else mContext.finish();
        return false;
    }

    @Override
    public boolean onRightButtonClick(View view) {
        toggleScreenOrientation();
        return false;
    }

    @Override
    public void init(Activity context) {
        mContext = context;
    }

    @Override
    public void toggleScreenOrientation() {
        if (mRotateVideoView != null) {
            mRotateVideoView.toggleOrientation();
        }
    }

    public void setScreenOriention(int oriention) {
        if(mRotateVideoView != null) {
            mOriention = oriention;
            mRotateVideoView.setOrientation(mOriention);
        }
    }

    @Override
    public void setPlayerStateLisnter(UPlayerStateListener l) {
        mOnPlayerStateListener = l;
    }

    @Override
    public void setMediaProfile(UMediaProfile profile) {
        this.avProfile = profile;
    }

    public void onConfigurationChanged(Configuration newConfig) {
        if (!isFullscreen()) {
            if(mSettingMenuView != null) {
                mSettingMenuView.setVisibility(View.GONE);
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
        if (mRotateVideoView != null) {
            if (mRotateVideoView.isPlaying()) {
                isPausedByManual = true;
                togglePlayerToPause();
            } else {
                isPausedByManual = false;
                togglePlayerToPlay();
            }
        }
        return false;
    }

    private void togglePlayerToPause() {
        mRotateVideoView.pause();
        mPlayStatusView.setVisibility(View.VISIBLE);
        mBottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_play_button_normal);
    }

    private void togglePlayerToPlay() {
        mPlayStatusView.setVisibility(View.GONE);
        mRotateVideoView.start();
        mBottomView.togglePlayButtonIcon(R.drawable.player_icon_bottomview_pause_button_normal);
    }

    public void dealOnPrepared() {
        notifyHideLoadingView(1000);
        notifyUpdateProgress();
        if (!isInitSettingMenu) {
            UMenuItemHelper menuItemHelper = UMenuItemHelper.getInstance(getContext());
            menuItemHelper.release();
            menuItemHelper.register(UMenuItemHelper.getInstance(getContext()).buildVideoRatioMenuItem(mRatio), true);
            menuItemHelper.register(UMenuItemHelper.getInstance(getContext()).buildVideoPlayerMenuItem(avProfile.getInteger(UMediaProfile.KEY_MEDIACODEC, 0)));
            mSettingMenuView.init();
            mSettingMenuView.setOnMenuItemSelectedListener(this);
            isInitSettingMenu = true;
        }

        boolean isCanSeek = !mRotateVideoView.isLiveStreaming();
        mInnerGestrueDetectoer.isSeekEnable = isCanSeek;
        mBottomView.setSeekEnable(isCanSeek);
    }

    public void dealCompletion() {
        if (mBottomView != null && mRotateVideoView != null && mPlayStatusView != null) {
            togglePlayerToPause();
        }
    }

    @Override
    public boolean isInPlaybackState() {
        return mRotateVideoView != null && mRotateVideoView.isInPlaybackState();
    }

    @Override
    public int getDuration() {
        if (mRotateVideoView != null) {
            return mRotateVideoView.getDuration();
        }
        return 0;
    }

    @Override
    public void seekTo(int position) {
        if (mRotateVideoView != null) {
            mRotateVideoView.seekTo(position);
        }
    }

    @Override
    public void showNavigationBar(int delay) {
        notifyShowNavigationBar(delay);
    }

    @Override
    public int getCurrentPosition() {
        if (mRotateVideoView != null) {
            return mRotateVideoView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public int toggleAspectRatio() {
        return mRotateVideoView.toggleAspectRatio();
    }

    @Override
    public int toggleRender() {
        return mRotateVideoView.toggleRender();
    }

    @Override
    public void onPause() {
        if (mRotateVideoView != null) {
            mRotateVideoView.onPause();
            isFullscreen = isFullscreen();
        }
    }

    @Override
    public boolean onBrightnessButtonClick(View view) {
        if (mBrightnessView != null && mBrightnessView.isShown()) {
            mBrightnessView.setVisibility(View.GONE);
        } else {
            assert mBrightnessView != null;
            mBrightnessView.setVisibility(View.VISIBLE);
        }
        return false;
    }

    @Override
    public boolean onVolumeButtonClick(View view) {
        if (mVolumeView != null && mVolumeView.getVisibility() == View.VISIBLE) {
            mVolumeView.setVisibility(View.GONE);
        } else {
            assert mVolumeView != null;
            mVolumeView.setVisibility(View.VISIBLE);
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
        mRotateVideoView.onDestroy();
    }

    private void notifyShowLoadingView(int duration) {
        Message msg = Message.obtain();
        msg.what = MSG_SHOW_LOADING_VIEW;
        uiHandler.removeMessages(MSG_SHOW_LOADING_VIEW);
        uiHandler.sendMessageDelayed(msg, duration);
    }

    private void notifyHideLoadingView(int duration) {
        Message msg = Message.obtain();
        msg.what = MSG_HIDE_LOADING_VIEW;
        uiHandler.removeMessages(MSG_HIDE_LOADING_VIEW);
        uiHandler.sendMessageDelayed(msg, duration);
    }

    private void doShowLoadingView() {
        if (mLoadingContainer != null && mLoadingContainer.getVisibility() == View.GONE && mLoadingView != null) {
            mLoadingContainer.setVisibility(View.VISIBLE);
            RotateAnimation rotateAnimation = new RotateAnimation(0f, 360, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(2000);
            rotateAnimation.setRepeatMode(RotateAnimation.RESTART);
            rotateAnimation.setRepeatCount(-1);
            mLoadingView.startAnimation(rotateAnimation);
        }
    }

    private void doHideLoadingView(){
        if (mLoadingContainer != null && mLoadingView != null) {
            mLoadingView.clearAnimation();
            mLoadingContainer.setVisibility(View.GONE);
        }
    }

    private void doUpdateProgress() {
        if (mRotateVideoView != null && mRotateVideoView.isInPlaybackState()) {
            int currnetPosition = mRotateVideoView.getCurrentPosition();
            int duration = mRotateVideoView.getDuration();
            if (mBottomView != null) {
                mBottomView.onPositionChanaged(currnetPosition, duration);
            }
            uiHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    notifyUpdateProgress();
                }
            },UPDATE_PROGRESS_INTERVAL);
        }
    }

    private void notifyUpdateProgress() {
        Message msg = Message.obtain();
        msg.what = MSG_UPDATE_PROGRSS;
        uiHandler.removeMessages(msg.what);
        uiHandler.sendMessage(msg);
    }

    public void initAspectRatio(int ratio) {
        mRatio = ratio;
    }

    @Override
    public UVideoView getVideoView() {
        return mRotateVideoView.getVideoView();
    }

    @Override
    public void setHudView(TableLayout hudView) {
        mHudView = hudView;
    }

    @Override
    public void setOnSettingMenuItemSelectedListener(USettingMenuView.Callback l) {
        mSettingMenuItemSelectedListener = l;
    }

    @Override
    public boolean onSettingMenuSelected(UMenuItem item) {
        boolean flag = false;
        if (mSettingMenuItemSelectedListener != null) {
            flag = mSettingMenuItemSelectedListener.onSettingMenuSelected(item);
        }
        if (!flag) try {
            if (item.parent != null) {
               if(item.parent != null && item.parent.title.equals(mContext.getResources().getString(R.string.menu_item_title_ratio))) {
                    mRotateVideoView.applyAspectRatio(Integer.parseInt(item.type));
                } else if (item.parent != null && item.parent.title.equals(mContext.getResources().getString(R.string.menu_item_title_videocodec))) {
                   notifyShowLoadingView(0);
                   mRotateVideoView.getMediaProfile().setInteger(UMediaProfile.KEY_MEDIACODEC, Integer.parseInt(item.type));
                   mRotateVideoView.setVideoPath(mUri);
               }
                notifyHideSettingMenuView(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
