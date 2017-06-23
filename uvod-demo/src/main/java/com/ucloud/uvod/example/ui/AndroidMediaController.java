package com.ucloud.uvod.example.ui;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MediaController;

import com.ucloud.uvod.IMediaController;

import java.util.ArrayList;

public class AndroidMediaController extends MediaController implements IMediaController {
    private ActionBar actionBar;

    private ArrayList<View> showOnceArray = new ArrayList<>();

    public AndroidMediaController(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AndroidMediaController(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public AndroidMediaController(Context context) {
        super(context);
    }

    public void setSupportActionBar(ActionBar actionBar) {
        this.actionBar = actionBar;
        if (isShowing()) {
            actionBar.show();
        }
        else {
            actionBar.hide();
        }
    }

    @Override
    public void show() {
        super.show();
        if (actionBar != null) {
            actionBar.show();
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (actionBar != null) {
            actionBar.hide();
        }
        for (View view : showOnceArray) {
            view.setVisibility(View.GONE);
        }
        showOnceArray.clear();
    }

    public void showOnce(View view) {
        showOnceArray.add(view);
        view.setVisibility(View.VISIBLE);
        show();
    }
}
