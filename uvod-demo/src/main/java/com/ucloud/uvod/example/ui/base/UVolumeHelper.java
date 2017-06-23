package com.ucloud.uvod.example.ui.base;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class UVolumeHelper extends UBaseHelper {

    private static final String TAG = "UVolumeHelper";

    private static int DEFAULT_VOLUME_LEVEL = 1;

    private AudioManager audioManager;

    public UVolumeHelper(Context context) {
        super(context);
    }

    @Override
    public void init(Context context) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        setMaxLevel(maxVolume);
        currentLevel = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentLevel, 0);
        setLevel(DEFAULT_VOLUME_LEVEL);
    }

    @Override
    public void setValue(int level, boolean isTouch) {
        Log.i(TAG, "CurrentLevel: " + currentLevel + ", Operation level:" + level + ", Max level:" + maxLevel);
        if (isZero() && isTouch) {
            level = historyLevel;
        }
        if (level < 0) {
            level = 0;
        }
        else if (level > maxLevel) {
            level = maxLevel;
        }
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, level, 0);
        updateValue();
        if (!isZero()) {
            historyLevel = currentLevel;
        }
        if (changeListener != null) {
            changeListener.onUpdateUI();
        }
    }

    @Override
    public int getSystemValueLevel() {
        int level;
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (levelStep == 0) {
            setLevel(DEFAULT_VOLUME_LEVEL);
        }
        level = (int) (currentVolume / levelStep);
        if (currentVolume % levelStep > 0) {
            level++;
        }
        return level;
    }
}
