package com.ucloud.uvod.example;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ucloud.ucommon.Utils;
import com.ucloud.uvod.UBuild;
import com.ucloud.uvod.example.permission.PermissionsActivity;
import com.ucloud.uvod.example.permission.PermissionsChecker;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity implements OnItemClickListener {

    public static final String KEY_MEDIACODEC = "mediacodec";

    public static final String KEY_START_ON_PREPARED = "start-on-prepared";

    public static final String KEY_LIVE_STREMAING = "live-streaming";

    public static final String KEY_ENABLE_BACKGROUND_PLAY = "enable-background-play";

    public static final String KEY_SHOW_DEBUG_INFO = "show-debug-info";

    public static final String KEY_URI = "uri";

    @BindView(R.id.rg_codec)
    RadioGroup videoCodecRg;

    @BindView(R.id.rg_streaming)
    RadioGroup streamingTypeRg;

    @BindView(R.id.rg_prepared_start)
    RadioGroup startOnPreparedRg;

    @BindView(R.id.edtxt_uri)
    EditText addressEdtxt;

    @BindView(R.id.listview)
    ListView listView;

    @BindView(R.id.txtv_version)
    TextView versionTxtv;

    @BindView(R.id.cb_background_play)
    CheckBox backgroundPlayCb;

    @BindView(R.id.cb_show_debug_info)
    CheckBox showDebugInfoCb;

    private static final int REQUEST_CODE = 200;

    private PermissionsChecker permissionsChecker; //for android target version >=23

    String[] permissions = new String[] {
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    String[] demoDirects;

    String[] demoNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo_main);
        ButterKnife.bind(this);
        addressEdtxt.clearFocus();
        demoNames = getResources().getStringArray(R.array.demoNames);
        listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoNames));
        listView.setOnItemClickListener(this);
        demoDirects = getResources().getStringArray(R.array.demoDirects);
        versionTxtv.setText(UBuild.NAME + "-" + UBuild.VERSION + " " + getResources().getString(R.string.sdk_address));
        permissionsChecker = new PermissionsChecker(this);
        if (permissionsChecker.lacksPermissions(permissions)) {
            PermissionsActivity.startActivityForResult(this, REQUEST_CODE, permissions);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        if (!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, R.string.info1, Toast.LENGTH_SHORT).show();
            return;
        }
        switch (Utils.getConnectedType(this)) {
            case ConnectivityManager.TYPE_MOBILE:
                Toast.makeText(this, R.string.info2, Toast.LENGTH_SHORT).show();
                break;
            case ConnectivityManager.TYPE_ETHERNET:
                Toast.makeText(this, R.string.info3, Toast.LENGTH_SHORT).show();
                break;
            case ConnectivityManager.TYPE_WIFI:
                Toast.makeText(this, R.string.info4, Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        if (demoDirects != null && demoDirects.length > position && !TextUtils.isEmpty(demoDirects[position].trim())) {
            String uri = addressEdtxt.getText().toString();
            if (TextUtils.isEmpty(uri)) {
                Toast.makeText(this, R.string.info5, Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(KEY_LIVE_STREMAING, streamingTypeRg.getCheckedRadioButtonId() == R.id.rb_live_streaming ? 1 : 0);
            intent.putExtra(KEY_MEDIACODEC, videoCodecRg.getCheckedRadioButtonId() == R.id.rb_mediacodec ? 1 : 0);
            intent.putExtra(KEY_START_ON_PREPARED, startOnPreparedRg.getCheckedRadioButtonId() == R.id.rb_auto ? 1 : 0);
            intent.putExtra(KEY_ENABLE_BACKGROUND_PLAY, backgroundPlayCb.isChecked() ? 1 : 0);
            intent.putExtra(KEY_SHOW_DEBUG_INFO, showDebugInfoCb.isChecked() ? 1 : 0);
            intent.putExtra(KEY_URI, uri);
            intent.setAction(demoDirects[position]);
            startActivity(intent);
        }
    }
}
