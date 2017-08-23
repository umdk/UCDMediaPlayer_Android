package com.ucloud.uvod.example.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ucloud.uvod.example.R;
import com.ucloud.uvod.example.ui.base.UMenuItem;
import com.ucloud.uvod.example.ui.base.UMenuItemHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class USettingMenuView extends LinearLayout {

    @BindView(R.id.listview)
    ListView settingItemListView;

    @BindView(R.id.listview_content)
    ListView settingContentItemListView;

    @BindView(R.id.menu_description_txtv)
    TextView menuContentTitleTxtv;

    @BindView(R.id.menu_txtv)
    TextView mainMenuTitleTxtv;

    private UMenuItem mainMenuItem;

    private MenuSettingAdapter menuSettingAdapter;

    private MenuSettingContentAdapter menuSettingContentAdapter;

    private Callback callback;

    public interface Callback {
        boolean onSettingMenuSelected(UMenuItem item);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public USettingMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public USettingMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public USettingMenuView(Context context) {
        super(context, null);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void init() {
        mainMenuItem = UMenuItemHelper.getInstance(getContext()).getMainMenu();
        menuSettingAdapter = new MenuSettingAdapter();
        menuSettingContentAdapter = new MenuSettingContentAdapter();
        if (mainMenuTitleTxtv != null) {
            mainMenuTitleTxtv.setText(mainMenuItem.title);
        }
        if (menuContentTitleTxtv != null) {
            menuContentTitleTxtv.setText(mainMenuItem.childs.get(mainMenuItem.defaultSelected).title);
        }
        settingItemListView.setAdapter(menuSettingAdapter);
        settingItemListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UMenuItem item = mainMenuItem.childs.get(position);
                menuSettingAdapter.notifyDataSetChanged();
                menuSettingContentAdapter.notifyDataSetChanged();
                if (mainMenuItem.defaultSelected != position) {
                    mainMenuItem.defaultSelected = position;
                    menuContentTitleTxtv.setText(item.title);
                    if (callback != null) {
                        callback.onSettingMenuSelected(item);
                    }
                }
            }
        });
        settingContentItemListView.setAdapter(menuSettingContentAdapter);
        settingContentItemListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                UMenuItem menuItem = mainMenuItem.childs.get(mainMenuItem.defaultSelected);
                UMenuItem contentMenuItem = menuItem.childs.get(position);
                if (menuItem.defaultSelected != Integer.parseInt(contentMenuItem.type)) {
                    menuItem.defaultSelected = position;
                    menuSettingContentAdapter.notifyDataSetChanged();
                    menuSettingAdapter.notifyDataSetChanged();
                    if (callback != null) {
                        callback.onSettingMenuSelected(contentMenuItem);
                    }
                }
            }
        });
    }

    class MenuSettingAdapter extends BaseAdapter {

        MenuSettingAdapter() {

        }

        @Override
        public int getCount() {
            return mainMenuItem.childs.size();
        }

        @Override
        public Object getItem(int position) {
            return mainMenuItem.childs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(getContext(), R.layout.player_layout_setting_menu_item, null);
            TextView titleTxtv = (TextView) view.findViewById(R.id.title_txtv);
            TextView descriptionTxtv = (TextView) view.findViewById(R.id.description_txtv);
            UMenuItem item = mainMenuItem.childs.get(position);
            if (item != null) {
                titleTxtv.setText(item.title);
                if (item.defaultSelected >= 0 && item.defaultSelected <= item.childs.size() - 1) {
                    descriptionTxtv.setText(item.childs.get(item.defaultSelected).title);
                }
                else {
                    if (item.childs != null && item.childs.size() >= 1) {
                        descriptionTxtv.setText(item.childs.get(0).title);
                    }
                }
            }

            if (position == mainMenuItem.defaultSelected) {
                titleTxtv.setTextColor(getResources().getColor(R.color.color_progress));
                descriptionTxtv.setTextColor(getResources().getColor(R.color.color_progress));
            }
            else {
                titleTxtv.setTextColor(getResources().getColor(R.color.color_white));
                descriptionTxtv.setTextColor(getResources().getColor(R.color.color_white_alpha_alpha40));
            }
            return view;
        }
    }

    class MenuSettingContentAdapter extends BaseAdapter {

        MenuSettingContentAdapter() {

        }

        @Override
        public int getCount() {
            return mainMenuItem.childs != null && mainMenuItem.childs.get(mainMenuItem.defaultSelected).childs != null ? mainMenuItem.childs.get(mainMenuItem.defaultSelected).childs.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return mainMenuItem.childs.get(mainMenuItem.defaultSelected).childs.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UMenuItem item = null;
            if (mainMenuItem.childs.get(mainMenuItem.defaultSelected).childs != null && mainMenuItem.childs.get(mainMenuItem.defaultSelected).childs.size() > 0) {
                item = mainMenuItem.childs.get(mainMenuItem.defaultSelected).childs.get(position);
            }

            View view;
            if (item != null && !TextUtils.isEmpty(item.description)) {
                view = View.inflate(getContext(), R.layout.player_layout_setting_menu_content_item, null);
            }
            else {
                view = View.inflate(getContext(), R.layout.player_layout_setting_menu_content_item2, null);
            }
            TextView titleTxtv = (TextView) view.findViewById(R.id.title_txtv);
            TextView descriptionTxtv = (TextView) view.findViewById(R.id.description_txtv);
            if (item != null) {
                titleTxtv.setText(item.title);
                descriptionTxtv.setText(item.description);
            }

            if ((item.parent != null && position == item.parent.defaultSelected) || item.parent.childs.size() == 1) {
                titleTxtv.setTextColor(getResources().getColor(R.color.color_progress));
                descriptionTxtv.setTextColor(getResources().getColor(R.color.color_progress));
            }
            else {
                view.setBackgroundResource(android.R.color.transparent);
                titleTxtv.setTextColor(getResources().getColor(R.color.color_white));
                descriptionTxtv.setTextColor(getResources().getColor(R.color.color_white_alpha_alpha40));
            }
            return view;
        }
    }

    public void setOnMenuItemSelectedListener(Callback l) {
        callback = l;
    }
}
