package com.ucloud.uvod.example.impl;

import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.example.MainActivity;
import com.ucloud.uvod.example.R;
import com.ucloud.uvod.example.ui.AndroidMediaController;
import com.ucloud.uvod.example.ui.TracksFragment;
import com.ucloud.uvod.widget.UVideoView;

import butterknife.Bind;
import butterknife.ButterKnife;
import merge.tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * Created by lw.tan on 2015/10/10.
 */
public class UVideoViewActivity extends AppCompatActivity implements TracksFragment.ITrackHolder, UPlayerStateListener {

	@Bind(R.id.video_view)
	UVideoView mVideoView;

	@Bind(R.id.toast_text_view)
	TextView mToastTextView;

	@Bind(R.id.hud_view)
	TableLayout mHudView;

	@Bind(R.id.drawer_layout)
	DrawerLayout mDrawerLayout;

	@Bind(R.id.right_drawer)
	ViewGroup mRightDrawer;

	AndroidMediaController mMediaController;

	private boolean mBackPressed;

	private String mUri;

	@Bind(R.id.toolbar)
	Toolbar toolbar;

	@Override
	protected void onCreate(Bundle bundles) {
		super.onCreate(bundles);
		setContentView(R.layout.activity_video_demo2);
		ButterKnife.bind(this);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mUri = getIntent().getStringExtra(MainActivity.KEY_URI);

		UMediaProfile profile = new UMediaProfile();
		profile.setInteger(UMediaProfile.KEY_START_ON_PREPARED, getIntent().getIntExtra(MainActivity.KEY_START_ON_PREPARED, 1));
		profile.setInteger(UMediaProfile.KEY_LIVE_STREAMING, getIntent().getIntExtra(MainActivity.KEY_LIVE_STREMAING, 0));
		if (mUri != null && mUri.endsWith("m3u8")) {
			profile.setInteger(UMediaProfile.KEY_MAX_CACHED_DURATION, 0);// m3u8 默认不开启延时丢帧策略
		}
		profile.setInteger(UMediaProfile.KEY_MEDIACODEC, getIntent().getIntExtra(MainActivity.KEY_MEDIACODEC, 0));
		profile.setInteger(UMediaProfile.KEY_ENABLE_BACKGROUND_PLAY, getIntent().getIntExtra(MainActivity.KEY_ENABLE_BACKGROUND_PLAY, 0));

		mVideoView.setMediaPorfile(profile);

		setSupportActionBar(toolbar);

		ActionBar actionBar = getSupportActionBar();
		mMediaController = new AndroidMediaController(this, false);
		mMediaController.setSupportActionBar(actionBar);

		mDrawerLayout.setScrimColor(Color.TRANSPARENT);

		mVideoView.setMediaController(mMediaController);
		if(getIntent().getIntExtra(MainActivity.KEY_SHOW_DEBUG_INFO, 1) == 1) {
			mVideoView.setHudView(mHudView);
		}
		mVideoView.setOnPlayerStateListener(this);
		mVideoView.setVideoPath(mUri);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mVideoView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mVideoView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mVideoView.onDestroy();
	}

	@Override
	public void onBackPressed() {
		mBackPressed = true;
		super.onBackPressed();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_player, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_toggle_ratio) {
			int aspectRatio = mVideoView.toggleAspectRatio();
			String aspectRatioText = UVideoView.getAspectRatioText(this, aspectRatio);
			mToastTextView.setText(aspectRatioText);
			mMediaController.showOnce(mToastTextView);
			return true;
		} else if (id == R.id.action_toggle_render) {
			int render = mVideoView.toggleRender();
			String renderText = UVideoView.getRenderText(this, render);
			mToastTextView.setText(renderText);
			mMediaController.showOnce(mToastTextView);
			return true;
		} else if (id == R.id.action_show_info) {
			mVideoView.showMediaInfo();
		} else if (id == R.id.action_show_tracks) {
			if (mDrawerLayout.isDrawerOpen(mRightDrawer)) {
				Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
				if (f != null) {
					FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
					transaction.remove(f);
					transaction.commit();
				}
				mDrawerLayout.closeDrawer(mRightDrawer);
			} else {
				Fragment f = TracksFragment.newInstance();
				FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
				transaction.replace(R.id.right_drawer, f);
				transaction.commit();
				mDrawerLayout.openDrawer(mRightDrawer);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public ITrackInfo[] getTrackInfo() {
		if (mVideoView == null)
			return null;

		return mVideoView.getTrackInfo();
	}

	@Override
	public void selectTrack(int stream) {
		mVideoView.selectTrack(stream);
	}

	@Override
	public void deselectTrack(int stream) {
		mVideoView.deselectTrack(stream);
	}

	@Override
	public int getSelectedTrack(int trackType) {
		if (mVideoView == null)
			return -1;

		return mVideoView.getSelectedTrack(trackType);
	}

	@Override
	public void onPlayerStateChanged(UPlayerStateListener.State state, int extra1, Object extra2) {
		switch (state) {
			case PREPARING:
				break;
			case PREPARED:
				break;
			case START:
				mVideoView.applyAspectRatio(UVideoView.VIDEO_RATIO_FIT_PARENT);//set after start or after setVideoPath
				break;
			case VIDEO_SIZE_CHANGED:
				break;
			case COMPLETED:
				break;
		}
	}

	@Override
	public void onPlayerInfo(UPlayerStateListener.Info info, int extra1, Object extra2) {
		switch (info) {
			case BUFFERING_START:
				break;
			case BUFFERING_END:
				break;
			case BUFFERING_UPDATE:
				break;
		}
	}

	@Override
	public void onPlayerError(UPlayerStateListener.Error error, int extra1, Object extra2) {
		switch (error) {
			case IOERROR:
				Toast.makeText(this, "Error: " + extra1, Toast.LENGTH_SHORT).show();
				break;
			case PREPARE_TIMEOUT:
				break;
			case READ_FRAME_TIMEOUT:
				break;
			case UNKNOWN:
				Toast.makeText(this, "Error: " + extra1, Toast.LENGTH_SHORT).show();
				break;
		}
	}
}
