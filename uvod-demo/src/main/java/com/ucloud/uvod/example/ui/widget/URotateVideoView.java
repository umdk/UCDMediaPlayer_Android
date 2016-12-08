package com.ucloud.uvod.example.ui.widget;


import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TableLayout;


import com.ucloud.uvod.UMediaProfile;
import com.ucloud.uvod.UMediaPlayer;
import com.ucloud.uvod.UPlayerStateListener;
import com.ucloud.uvod.IMediaController;
import com.ucloud.uvod.widget.UVideoView;

import merge.tv.danmaku.ijk.media.player.misc.ITrackInfo;


/**
 * Created by lw.tan on 2015/10/10.
 */
public class URotateVideoView extends URotateLayout {

    private UVideoView mVideoView;

    public URotateVideoView(Context context) {
        super(context);
        init(context);
    }

    public URotateVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public URotateVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        FrameLayout.LayoutParams mLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        mLayoutParams.gravity = Gravity.CENTER;
        mVideoView = new UVideoView(context);
        mVideoView.setLayoutParams(mLayoutParams);
        addView(mVideoView);
    }

    public void onPause() {
        mVideoView.onPause();
    }

    public void onResume() {
        mVideoView.onResume();
    }

    public void onDestroy() {
        mVideoView.onDestroy();
    }

    public void setVideoPath(String path) {
        mVideoView.setVideoPath(path);
    }

    public void setVideoURI(Uri uri) {
        mVideoView.setVideoURI(uri);
    }

    public void stopPlayback() {
        mVideoView.stopPlayback();
    }

    public void setMediaController(IMediaController controller) {
        mVideoView.setMediaController(controller);
    }

    public void setOnPreparedListener(UMediaPlayer.OnPreparedListener l) {
        mVideoView.setOnPreparedListener(l);
    }

    public void setOnCompletionListener(UMediaPlayer.OnCompletionListener l) {
        mVideoView.setOnCompletionListener(l);
    }

    public void setOnErrorListener(UMediaPlayer.OnErrorListener l) {
        mVideoView.setOnErrorListener(l);
    }

    public void setOnInfoListener(UMediaPlayer.OnInfoListener l) {
        mVideoView.setOnInfoListener(l);
    }

    public void releaseWithoutStop() {
        mVideoView.releaseWithoutStop();
    }

    public void release(boolean cleartargetstate) {
        mVideoView.release(cleartargetstate);
    }

    public void start() {
        mVideoView.start();
    }

    public void pause() {
        mVideoView.pause();
    }

    public int getDuration() {
        return mVideoView.getDuration();
    }

    public int getCurrentPosition() {
        return mVideoView.getCurrentPosition();
    }

    public void seekTo(int msec) {
        mVideoView.seekTo(msec);
    }

    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    public int getBufferPercentage() {
        return mVideoView.getBufferPercentage();
    }

    public boolean isInPlaybackState() {
        return mVideoView.isInPlaybackState();
    }

    public boolean canPause() {
        return mVideoView.canPause();
    }

    public boolean canSeekBackward() {
        return mVideoView.canSeekBackward();
    }

    public boolean canSeekForward() {
        return mVideoView.canSeekForward();
    }

    public int getAudioSessionId() {
        return mVideoView.getAudioSessionId();
    }

    public int toggleAspectRatio() {
        return mVideoView.toggleAspectRatio();
    }

    public int toggleRender() {
        return mVideoView.toggleRender();
    }

    public void setOnPlayerStateListener(UPlayerStateListener listener) {
        mVideoView.setOnPlayerStateListener(listener);
    }

    public void setMediaPorfile(UMediaProfile mediaProfile) {
        mVideoView.setMediaPorfile(mediaProfile);
    }

    public UMediaProfile getMediaProfile() {
        return mVideoView.getMediaProfile();
    }

    public int getSelectedTrack(int trackType) {
        return mVideoView.getSelectedTrack(trackType);
    }

    public void deselectTrack(int stream) {
        mVideoView.deselectTrack(stream);
    }

    public void selectTrack(int stream) {
        mVideoView.selectTrack(stream);
    }

    public ITrackInfo[] getTrackInfo() {
        return mVideoView.getTrackInfo();
    }

    public void showMediaInfo() {
        mVideoView.showMediaInfo();
    }

    public void stopBackgroundPlay() {
        mVideoView.stopBackgroundPlay();
    }

    public void enterBackground() {
        mVideoView.enterBackground();
    }

    public boolean isBackgroundPlayEnabled() {
        return mVideoView.isBackgroundPlayEnabled();
    }

    public int applyAspectRatio(int ratio) {
        return mVideoView.applyAspectRatio(ratio);
    }

    public void setHudView(TableLayout hudView) {
        mVideoView.setHudView(hudView);
    }

    public UVideoView getVideoView() {
        return mVideoView;
    }

    public boolean isLiveStreaming() {
        return mVideoView.isLiveStreaming();
    }
}
