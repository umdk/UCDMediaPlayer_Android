package com.ucloud.uvod.example.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ucloud.uvod.example.R;
import com.ucloud.uvod.example.ui.base.UBaseHelper;
import com.ucloud.uvod.example.ui.base.UBaseHelper.ChangeListener;

import butterknife.Bind;
import butterknife.ButterKnife;

public class UVerticalProgressView extends RelativeLayout implements ChangeListener {
    private static final int MSG_HIDE = 1;
    private static final int DELAY_HIDE = 5000;

    @Bind(R.id.volume_icon)
    ImageView volumeIcon;

    @Bind(R.id.volume_progress)
    UVerticalProgressBar verticalProgressBar;

    private UBaseHelper baseHelper;

    private boolean isUseSystemVolume;

    private int iconNormalResId;

    private UiHandler uiHandler = new UiHandler();

    private class UiHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_HIDE:
                    hide();
                    break;
                default:
                    break;
            }
        }
    }

    public void setIconNormalResId(int resId) {
        iconNormalResId = resId;
    }

    public UVerticalProgressView(Context context, AttributeSet attrs, int i) {
        super(context, attrs, i);
    }

    public UVerticalProgressView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UVerticalProgressView(Context context) {
        this(context, null, 0);
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        verticalProgressBar.setOrientation(false);
        isUseSystemVolume = true;
    }

    public void setHelper(UBaseHelper baseHelper) {
        this.baseHelper = baseHelper;
        this.baseHelper.setOnChangeListener(this);
        verticalProgressBar.setMax(this.baseHelper.getMaxLevel());
        updateProgressBar();
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(MotionEvent event) {
        if (trackTouch(event)) {
            show();
            return true;
        }
        return super.onTouchEvent(event);
    }

    private boolean trackTouch(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                verticalProgressBar.setProgress(computeLevel(event.getY()));
                return true;
            case MotionEvent.ACTION_UP:
                int level = computeLevel(event.getY());
                verticalProgressBar.setProgress(level);
                baseHelper.setVauleTouch(level);
                return true;
            default:
                break;
        }
        return false;
    }

    private int computeLevel(float location) {
        int level = 0;
        if (location <= verticalProgressBar.getTop()) {
            level = verticalProgressBar.getMax();
        }
        else if (location >= verticalProgressBar.getBottom()) {
            level = 0;
        }
        else {
            level = (int) Math
                    .ceil((verticalProgressBar.getHeight() - location + verticalProgressBar
                            .getTop())
                            * verticalProgressBar.getMax()
                            / verticalProgressBar.getHeight());
        }
        return level;
    }

    public void onUpdateUI() {
        updateProgressBar();
    }

    public void updateProgressBar() {
        if (baseHelper != null) {
            baseHelper.updateValue();
        }
        if (verticalProgressBar != null) {
            verticalProgressBar.setProgress((int) baseHelper.getCurrentLevel());
        }
        if (baseHelper.isZero()) {
            volumeIcon.setImageResource(iconNormalResId);
        }
        else {
            volumeIcon.setImageResource(iconNormalResId);
        }
    }

    public void change(boolean isUp, boolean isZero) {
        if (isZero) {
            baseHelper.isZero();
        }
        else {
            if (isUp) {
                baseHelper.increaseValue();
            }
            else {
                baseHelper.decreaseValue();
            }
        }
        show();
    }

    public boolean isUseSystemValue() {
        return isUseSystemVolume;
    }

    public void show() {
        uiHandler.removeMessages(MSG_HIDE);
        setVisibility(VISIBLE);
        uiHandler.sendEmptyMessageDelayed(MSG_HIDE, DELAY_HIDE);
    }

    public void hide() {
        uiHandler.removeMessages(MSG_HIDE);
        setVisibility(GONE);
    }
}
