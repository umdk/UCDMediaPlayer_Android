package com.ucloud.uvod.example.ui.base;


import android.content.Context;

import com.ucloud.uvod.example.R;

public final class UMenuItemHelper {

    private static UMenuItem MAIN;

    private static UMenuItemHelper INSTANCE;

    private static Context APP_CONTEXT;

    private UMenuItemHelper(Context context) {
        UMenuItemHelper.APP_CONTEXT = context;
        MAIN = new UMenuItem.Builder().title(UMenuItemHelper.APP_CONTEXT.getResources().getString(R.string.menu_main_title)).builder();
    }

    public static UMenuItemHelper getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (UMenuItemHelper.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UMenuItemHelper(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public UMenuItem buildVideoPlayerMenuItem(int defaultSelect) {
        return buildVideoMenuItem(
               APP_CONTEXT.getResources().getString(R.string.menu_item_title_videocodec),
               R.array.pref_videocodec_names,
               R.array.pref_videocodec_values,
               defaultSelect);
    }

    public UMenuItem buildVideoRatioMenuItem(int defaultSelect) {
        return buildVideoMenuItem(
                APP_CONTEXT.getResources().getString(R.string.menu_item_title_ratio),
                R.array.pref_screen_ratio_names,
                R.array.pref_screen_ratio_values,
                defaultSelect);
    }

    public UMenuItem buildVideoMenuItem(String title, int resNameId, int resValueId, int defaultSelect) {
        UMenuItem menuItem = new UMenuItem.Builder().title(title).index(defaultSelect).builder();
        String[] retNames = APP_CONTEXT.getResources().getStringArray(resNameId);
        String[] types = APP_CONTEXT.getResources().getStringArray(resValueId);
        for (int i = 0; i < retNames.length; i++) {
            menuItem.childs.add(new UMenuItem.Builder().title(retNames[i]).type(types[i] + "").parent(menuItem).builder());
        }
        return menuItem;
    }

    public UMenuItem register(UMenuItem child) {
        return register(child, false);
    }

    public UMenuItem register(UMenuItem child, boolean isDefaultSelected) {
        if (MAIN != null && !MAIN.childs.contains(child)) {
            MAIN.childs.add(child);
            if (isDefaultSelected) {
                MAIN.defaultSelected =  MAIN.childs.size() - 1;
            }
        }
        return MAIN;
    }

    public UMenuItem getMainMenu() {
        return MAIN;
    }

    public void release() {
        if (MAIN != null && MAIN.childs != null) {
            MAIN.childs.clear();
            INSTANCE = null;
            MAIN = null;
        }
    }
}
