
package com.ucloud.uvod.example.ui.widget;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.ucloud.uvod.example.R;

public class UVerticalProgressBar extends ProgressBar {

    private int barWidth;

    private int barHeight;

    private boolean horizontal = false;

    public UVerticalProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public UVerticalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UVerticalProgressBar(Context context) {
        this(context, null);
    }

    private void init() {
        barWidth = getResources().getDimensionPixelSize(R.dimen.volume_vertical_progress_width);
        barHeight = getResources().getDimensionPixelSize(R.dimen.vs_progressbar_height);
    }

    public void setOrientation(boolean horizontal) {
        this.horizontal = horizontal;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        try {
            Rect rec = getProgressDrawable().getBounds();
            if (!horizontal) {
                if (getWidth() > barWidth) {
                    rec.left = (getWidth() - barWidth) / 2;
                    getProgressDrawable().setBounds(rec.left, rec.top,
                            rec.left + barWidth, rec.bottom);
                }
            }
            else {
                if (getHeight() > barHeight) {
                    rec.top = (getHeight() - barWidth) / 2;
                    getProgressDrawable().setBounds(rec.left, rec.top,
                            rec.right, rec.top + barHeight);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        super.onDraw(canvas);
    }
}
