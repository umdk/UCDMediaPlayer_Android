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
import android.text.TextUtils;
import android.util.Log;
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
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import merge.tv.danmaku.ijk.media.player.misc.ITrackInfo;

public class UVideoViewActivity extends AppCompatActivity implements TracksFragment.ITrackHolder, UPlayerStateListener {

    @BindView(R.id.video_view)
    UVideoView videoView;

    @BindView(R.id.toast_text_view)
    TextView toastTextView;

    @BindView(R.id.hud_view)
    TableLayout debugInfoHudView;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.right_drawer)
    ViewGroup rightDrawer;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    AndroidMediaController mediaController;

    private String uri;

    @Override
    protected void onCreate(Bundle bundles) {
        super.onCreate(bundles);
        setContentView(R.layout.activity_video_demo2);
        ButterKnife.bind(this);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        uri = getIntent().getStringExtra(MainActivity.KEY_URI);

        UMediaProfile profile = new UMediaProfile();
        profile.setInteger(UMediaProfile.KEY_LIVE_STREAMING, getIntent().getIntExtra(MainActivity.KEY_LIVE_STREMAING, 0)); //标识播放的流为直播源，还是点播源(0点播，1直播)
        profile.setInteger(UMediaProfile.KEY_START_ON_PREPARED, getIntent().getIntExtra(MainActivity.KEY_START_ON_PREPARED, 1)); //当prepread成功后自动开始播放，(无须自己监听prepared消息调用start方法) 直播推荐开启(1开启，0不开启)
        profile.setInteger(UMediaProfile.KEY_MEDIACODEC, getIntent().getIntExtra(MainActivity.KEY_MEDIACODEC, 0)); //视频解码方式，推荐软解
        profile.setInteger(UMediaProfile.KEY_RENDER_TEXTURE, 1);
        profile.setInteger(UMediaProfile.KEY_RENDER_NO, 1);
        profile.setInteger(UMediaProfile.KEY_PREPARE_TIMEOUT, 1000 * 15); //设置第一次播放流地址时，prepared超时时间(超过设置的值，sdk内部会做重连动作，单位ms)
        profile.setInteger(UMediaProfile.KEY_READ_FRAME_TIMEOUT, 1000 * 15); //设置播放过程中，网络卡顿出现读取数据超时(超过设置的值，sdk内部会做重连动作，单位ms)
        profile.setInteger(UMediaProfile.KEY_ENABLE_BACKGROUND_PLAY, getIntent().getIntExtra(MainActivity.KEY_ENABLE_BACKGROUND_PLAY, 0)); //设置切换到后台是否继续播放，直播推荐开启，(默认为0不开启)

        //若需要区分4G是否继续播放等与用户确认相关的操作，设置为0，自行根据Android API监听网络状态调用setVideoPath做重连控制操作。
        profile.setInteger(UMediaProfile.KEY_ENABLE_NETWORK_RECOVERY_RECONNECT, 1); //当发生网络切换恢复时SDK内部会做重连（默认为0 不开启 1不开启)
        profile.setInteger(UMediaProfile.KEY_MAX_CACHED_DURATION, 0); // 点播默认不开启延时丢帧策略
        profile.setInteger(UMediaProfile.KEY_IS_MUSIC_PLAYER, 0); //如果播放的是纯音频流，设置为1，默认为0

        //示例 1：若设置不止一个http请求头字段 （Cookie， Cache-Control等）
        //采用如下外部构造可以满足所有HTTP标准头的设置 （value内容自行拼接处理）value格式不清楚的可以参考http标准的写法，以下尽给出了Cookie & Cache-Control字段的写法

        Map<String, String> headersMap = new HashMap<>();

//        headersMap.put("Cookie", "username=www.ucloud; password=cn;");
//        headersMap.put("Cache-Control", "no-cache;");
//        profile.setExtendMap(UMediaProfile.KEY_HEADERS, headersMap);


        //示例 2： 对Cookie value内容进行了拼接优化处理，不需要自己拼接字段，key,value 以map形式传递 （key,value仅限string内容，若不是先自行转换成string）

        Map<String, String> headCookieMap = new HashMap<>();

//        headCookieMap.put("username", "www.ucloud");
//        headCookieMap.put("password", "cn");
//        headCookieMap.put("test", "test");

        //若示例 1 先设置了Cookie 示例2 同样设置了，无论示例2，在示例1前设置还是后设置 Cookie的值以第二个接口为准，顺序无关。 示例1 设置的其它值同样有效

        profile.setExtendMap(UMediaProfile.KEY_HEADER_COOKIE, headCookieMap); // 针对 cookie设置的接口

//        profile.setExtendMap(UMediaProfile.KEY_HEADERS, null);       //清除所有自定义设置的头字段，清除后http不会携带所有自定义头字段 (包括示例2接口方式设置的cookie)
//        profile.setExtendMap(UMediaProfile.KEY_HEADER_COOKIE, null); //清除cookie字段，清除后http不会携带cookie字段，其它字段没有影响 （示例1方式设置的Cookie，也会移除）

        if (uri != null && uri.endsWith("m3u8")) {
            profile.setInteger(UMediaProfile.KEY_MAX_CACHED_DURATION, 0); // m3u8 默认不开启延时丢帧策略
        }

        videoView.setMediaPorfile(profile);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        mediaController = new AndroidMediaController(this, false);
        mediaController.setSupportActionBar(actionBar);

        drawerLayout.setScrimColor(Color.TRANSPARENT);

        videoView.setMediaController(mediaController);
        if (getIntent().getIntExtra(MainActivity.KEY_SHOW_DEBUG_INFO, 1) == 1) {
            videoView.setHudView(debugInfoHudView);
        }
        videoView.setOnPlayerStateListener(this);

//        videoView.setSpeed(1.25f); //0.5 - 2.0f 推荐设置 区间范围外的值仍然生效，但是视听效果已经比较差

        videoView.setVideoPath(uri);

    }

    @Override
    protected void onPause() {
        super.onPause();
        videoView.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        videoView.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        videoView.onDestroy();
    }

    @Override
    public void onBackPressed() {
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
            int aspectRatio = videoView.toggleAspectRatio();
            String aspectRatioText = UVideoView.getAspectRatioText(this, aspectRatio);
            toastTextView.setText(aspectRatioText);
            mediaController.showOnce(toastTextView);
            return true;
        }
        else if (id == R.id.action_toggle_render) {
            int render = videoView.toggleRender();
            String renderText = UVideoView.getRenderText(this, render);
            toastTextView.setText(renderText);
            mediaController.showOnce(toastTextView);
            return true;
        }
        else if (id == R.id.action_show_info) {
            videoView.showMediaInfo();
        }
        else if (id == R.id.action_show_tracks) {
            if (drawerLayout.isDrawerOpen(rightDrawer)) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.right_drawer);
                if (f != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.remove(f);
                    transaction.commit();
                }
                drawerLayout.closeDrawer(rightDrawer);
            }
            else {
                Fragment f = TracksFragment.newInstance();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.right_drawer, f);
                transaction.commit();
                drawerLayout.openDrawer(rightDrawer);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ITrackInfo[] getTrackInfo() {
        if (videoView == null) {
            return null;
        }
        return videoView.getTrackInfo();
    }

    @Override
    public void selectTrack(int stream) {
        videoView.selectTrack(stream);
    }

    @Override
    public void deselectTrack(int stream) {
        videoView.deselectTrack(stream);
    }

    @Override
    public int getSelectedTrack(int trackType) {
        if (videoView == null) {
            return -1;
        }
        return videoView.getSelectedTrack(trackType);
    }

    @Override
    public void onPlayerStateChanged(UPlayerStateListener.State state, int extra1, Object extra2) {
        switch (state) {
            case PREPARING:
                break;
            case PREPARED:
                break;
            case START:
                videoView.applyAspectRatio(UVideoView.VIDEO_RATIO_FIT_PARENT); //set after start or after setVideoPath
                break;
            case VIDEO_SIZE_CHANGED:
                break;
            case COMPLETED:
                break;
            default:
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
            case BUFFERING_PERCENT:
                break;
            default:
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
            default:
                break;
        }
    }
}
