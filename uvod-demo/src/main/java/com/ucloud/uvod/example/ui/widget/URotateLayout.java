package com.ucloud.uvod.example.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * @author lw.tan on 2015/10/10.
 */
public class URotateLayout extends FrameLayout {
    public static final String TAG = "URotateLayout";
    public static final int ORIENTATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    public static final int ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    public static final int ORIENTATION_SENSOR_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
    public static final int ORIENTATION_SENSOR_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT;
    public static final int ORIENTATION_SENSOR = ActivityInfo.SCREEN_ORIENTATION_SENSOR;
    public static final int ORIENTATION_LOCKED = ActivityInfo.SCREEN_ORIENTATION_LOCKED;

    private int orientation;
    private int lastOrientation;

    private int defaultVideoContainerWidth;
    private int defaultVideoContainerHeight;

    private int screenWidth;
    private int screenHeight;

    public URotateLayout(Context context) {
        super(context);
    }

    public URotateLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public URotateLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void updateScreenWidthAndHeight() {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
    }

    public boolean isLandscape() {
        updateScreenWidthAndHeight();
        return screenWidth > screenHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        updateScreenWidthAndHeight();
        if (isLandscape()) {
            defaultVideoContainerWidth = screenWidth;
            defaultVideoContainerHeight = screenHeight;
        }
        else {
            defaultVideoContainerWidth = screenWidth;
            defaultVideoContainerHeight = screenWidth * 9 / 16;
        }
        setMeasuredDimension(defaultVideoContainerWidth, defaultVideoContainerHeight);
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        if (getContext() instanceof Activity) {
            Activity mActivity = (Activity) getContext();
            switch (orientation) {
                case ORIENTATION_PORTRAIT:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case ORIENTATION_LANDSCAPE:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case ORIENTATION_SENSOR_LANDSCAPE:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    break;
                case ORIENTATION_SENSOR_PORTRAIT:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                    break;
                case ORIENTATION_SENSOR:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                    break;
                case ORIENTATION_LOCKED:
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
                    break;
                default:
                    break;
            }
            this.orientation = orientation;
            invalidate();
        }
    }

    public void locked() {
        lastOrientation = orientation;
        setOrientation(ORIENTATION_LOCKED);
    }

    public boolean isLocked() {
        return orientation == ORIENTATION_LOCKED ? true : false;
    }

    public void unlocked() {
        setOrientation(lastOrientation);
    }

    public void toggleOrientation() {
        if (getContext() instanceof Activity && orientation != ORIENTATION_LOCKED) {
            Activity mActivity = (Activity) getContext();
            if (isLandscape()) {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
            }
            else {
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
            if (orientation == ORIENTATION_SENSOR) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setOrientation(orientation);
                    }
                }, 2000);
            }
        }
    }
}
