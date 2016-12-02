package com.ucloud.uvod.example.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.widget.TableLayout;

import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.example.ui.widget.URotateLayout;
import com.ucloud.uvod.widget.UVideoView;

/**
 *
 * @author lw.tan Created by lw.tan on 2016/11/10.
 *
 */
public interface UEasyPlayer {

	int VIDEO_RATIO_AUTO = UVideoView.VIDEO_RATIO_FIT_PARENT;

	int VIDEO_RATIO_ORIGIN = UVideoView.VIDEO_RATIO_WRAP_CONTENT;

	int VIDEO_RATIO_FULL_SCREEN = UVideoView.VIDEO_RATIO_FILL_PARENT;

	int SCREEN_ORIENTATION_SENSOR = URotateLayout.ORIENTATION_SENSOR;

	void init(Activity context);

	void setVideoPath(String uri);

	void onResume();

	void onPause();

	void onDestroy();

	boolean isFullscreen();

	boolean isInPlaybackState();

	int getDuration();

	void seekTo(int position);

	void showNavigationBar(int delay);

	int getCurrentPosition();

	void toggleScreenOrientation();

	int toggleAspectRatio();

	int toggleRender();

	void setOnSettingMenuItemSelectedListener(USettingMenuView.Callback callback);

	void setScreenOriention(int oriention);

	void setPlayerStateLisnter(UPlayerStateListener callback);

	void setMediaProfile(UMediaProfile profile);

	void applyAspectRatio(int ratio);

	UVideoView getVideoView();

	void setHudView(TableLayout mHudView);
}
