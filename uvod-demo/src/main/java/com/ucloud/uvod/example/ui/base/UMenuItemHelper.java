package com.ucloud.uvod.example.ui.base;


import android.content.Context;

import com.ucloud.uvod.example.R;

/**
 * Created by lw.tan on 2015/8/11.
 */
public class UMenuItemHelper {
    private static UMenuItem mMainMenuItem;
    private static UMenuItemHelper instance;
    private static Context mContext;
    private UMenuItemHelper(Context context) {
        mContext = context;
        mMainMenuItem = new UMenuItem.Builder().title(mContext.getResources().getString(R.string.menu_main_title))
                .builder();
    }

    public static UMenuItemHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (UMenuItemHelper.class) {
                if (instance == null) {
                    instance = new UMenuItemHelper(context);
                }
            }
        }
        return instance;
    }

    public UMenuItem buildVideoPlayerMenuItem(int defaultSelect) {
       return buildVideoMenuItem(
               mContext.getResources().getString(R.string.menu_item_title_videocodec),
               R.array.pref_videocodec_names,
               R.array.pref_videocodec_values,
               defaultSelect);
    }

    public UMenuItem buildVideoRatioMenuItem(int defaultSelect) {
        return buildVideoMenuItem(
                mContext.getResources().getString(R.string.menu_item_title_ratio),
                R.array.pref_screen_ratio_names,
                R.array.pref_screen_ratio_values,
                defaultSelect);
    }

    public UMenuItem buildVideoMenuItem(String title, int resNameId, int resValueId, int defaultSelect) {
        UMenuItem menuItem = new UMenuItem.Builder().title(title).index(defaultSelect).builder();
        String[] retNames = mContext.getResources().getStringArray(resNameId);
        String[] types = mContext.getResources().getStringArray(resValueId);
        for(int i = 0; i < retNames.length; i++) {
            menuItem.childs.add(new UMenuItem.Builder().title(retNames[i]).type(types[i] + "").parent(menuItem).builder());
        }
        return menuItem;
    }

    public UMenuItem register(UMenuItem child) {
       return register(child, false);
    }

    public UMenuItem register(UMenuItem child, boolean isDefaultSelected) {
        if (mMainMenuItem != null && !mMainMenuItem.childs.contains(child)) {
            mMainMenuItem.childs.add(child);
            if (isDefaultSelected) {
                mMainMenuItem.defaultSelected =  mMainMenuItem.childs.size() - 1;
            }
        }
        return mMainMenuItem;
    }

    public UMenuItem getMainMenu() {
        return mMainMenuItem;
    }

    public void release() {
        if (mMainMenuItem != null && mMainMenuItem.childs != null) {
            mMainMenuItem.childs.clear();
            instance = null;
            mMainMenuItem = null;
        }
    }
}
