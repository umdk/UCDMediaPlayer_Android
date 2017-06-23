package com.ucloud.uvod.example.ui.base;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager.LayoutParams;

/**
 * Created by lw.tan on 2015/10/10.
 */
public class UBrightnessHelper extends UBaseHelper {

    public static int DEFAULT_MAX_BRIGHTNESS_VALUE = 100;
    public static int DEFAULT_MIN_CHANAGE_LEVEL = 10;
    public static int DEFAULT_BRIGHTNESS_VALUE = 50;

    public UBrightnessHelper(Context context) {
        super(context);
    }

    @Override
    public void init(Context context) {
        setLevel(DEFAULT_MIN_CHANAGE_LEVEL);
        setMaxLevel(DEFAULT_MAX_BRIGHTNESS_VALUE);
    }

    @Override
    public void setValue(int level, boolean isTouch) {
        if (isZero() && isTouch) {
            level = historyLevel;
        }
        if (level < 0) {
            level = 0;
        }
        else if (level > maxLevel) {
            level = maxLevel;
        }
        float tempValue = level;
        if (context != null && context instanceof Activity) {
            LayoutParams lp = ((Activity) (context)).getWindow().getAttributes();
            lp.screenBrightness = tempValue / maxLevel;
            ((Activity) (context)).getWindow().setAttributes(lp);
            updateValue();
            if (!isZero()) {
                historyLevel = currentLevel;
            }
            if (changeListener != null) {
                changeListener.onUpdateUI();
            }
        }
    }

    @Override
    public int getSystemValueLevel() {
        if (context != null && context instanceof Activity) {
            LayoutParams lp = ((Activity) (context)).getWindow().getAttributes();
            return lp.screenBrightness == -1 ? DEFAULT_MAX_BRIGHTNESS_VALUE : (int) (lp.screenBrightness * DEFAULT_MAX_BRIGHTNESS_VALUE);
        }
        return DEFAULT_MAX_BRIGHTNESS_VALUE;
    }
}
