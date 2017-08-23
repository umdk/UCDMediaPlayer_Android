package com.ucloud.uvod.example.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ucloud.uvod.example.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lw.tan on 2015/10/10.
 */
public class UTopView extends RelativeLayout {
    private Callback callback;

    @BindView(R.id.topview_title_txtv)
    TextView titleTxtv;

    @BindView(R.id.topview_left_button)
    ImageButton leftImgBtn;

    @BindView(R.id.topview_right_button)
    ImageButton rightImgBtn;

    public interface Callback {
        boolean onLeftButtonClick(View view);
        boolean onRightButtonClick(View view);
    }

    public UTopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public UTopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UTopView(Context context) {
        this(context, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        leftImgBtn.setOnClickListener(leftButtonClickListener);
        rightImgBtn.setOnClickListener(rightButtonClickListener);
    }

    OnClickListener leftButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (callback != null) {
                callback.onLeftButtonClick(v);
            }
        }
    };

    OnClickListener rightButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (callback != null) {
                callback.onRightButtonClick(v);
            }
        }
    };

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public void setTitle(int resid) {
        if (titleTxtv != null) {
            titleTxtv.setText(resid);
        }
    }
}
