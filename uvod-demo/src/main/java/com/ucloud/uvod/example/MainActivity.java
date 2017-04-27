package com.ucloud.uvod.example;


import android.Manifest;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by lw.tan on 2016/11/10.
 */
public class MainActivity extends AppCompatActivity implements OnItemClickListener{

	String[] demoDirects;

	String[] demoNames;

	public static final String KEY_MEDIACODEC = "mediacodec";

	public static final String KEY_START_ON_PREPARED = "start-on-prepared";

	public static final String KEY_LIVE_STREMAING = "live-streaming";

	public static final String KEY_ENABLE_BACKGROUND_PLAY = "enable-background-play";

	public static final String KEY_SHOW_DEBUG_INFO = "show-debug-info";

	public static final String KEY_URI = "uri";

	@Bind(R.id.rg_codec)
	RadioGroup videoCodecRg;

	@Bind(R.id.rg_streaming)
    RadioGroup streamingTypeRg;

	@Bind(R.id.rg_prepared_start)
	RadioGroup startOnPreparedRg;

	@Bind(R.id.edtxt_uri)
	EditText addressEdtxt;

	@Bind(R.id.toolbar)
	Toolbar toolbar;

	@Bind(R.id.listview)
	ListView listView;

	@Bind(R.id.txtv_version)
	TextView versionTxtv;

	@Bind(R.id.cb_background_play)
	CheckBox backgroundPlayCb;

	@Bind(R.id.cb_show_debug_info)
	CheckBox showDebugInfoCb;

	private static final int REQUEST_CODE = 200;
	private PermissionsChecker mPermissionsChecker; //for android target version >=23
	String[] permissions = new String[]{
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.demo_main);
		ButterKnife.bind(this);

		addressEdtxt.clearFocus();
		setSupportActionBar(toolbar);
		demoNames = getResources().getStringArray(R.array.demoNames);
		listView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoNames));
		listView.setOnItemClickListener(this);
		demoDirects = getResources().getStringArray(R.array.demoDirects);
		versionTxtv.setText(UBuild.VERSION + " " + getResources().getString(R.string.sdk_address));
		mPermissionsChecker = new PermissionsChecker(this);
		if (mPermissionsChecker.lacksPermissions(permissions)) {
			PermissionsActivity.startActivityForResult(this, REQUEST_CODE, permissions);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(!Utils.isNetworkConnected(this)) {
            Toast.makeText(this, "当前网络不可用.", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (Utils.getConnectedType(this)) {
			case ConnectivityManager.TYPE_MOBILE:
				Toast.makeText(this, "当前网络: mobile", Toast.LENGTH_SHORT).show();
				break;
			case ConnectivityManager.TYPE_ETHERNET:
				Toast.makeText(this, "当前网络: ehternet", Toast.LENGTH_SHORT).show();
				break;
			case ConnectivityManager.TYPE_WIFI:
				Toast.makeText(this, "当前网络: wifi", Toast.LENGTH_SHORT).show();
				break;
		}

		if (demoDirects != null && demoDirects.length > position && !TextUtils.isEmpty(demoDirects[position].trim())) {
			String uri = addressEdtxt.getText().toString();
			if (TextUtils.isEmpty(uri)) {
				Toast.makeText(this, "uri is null", Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra(KEY_LIVE_STREMAING, streamingTypeRg.getCheckedRadioButtonId() == R.id.rb_live_streaming ? 1: 0);
			intent.putExtra(KEY_MEDIACODEC, videoCodecRg.getCheckedRadioButtonId() == R.id.rb_mediacodec ? 1: 0);
			intent.putExtra(KEY_START_ON_PREPARED, startOnPreparedRg.getCheckedRadioButtonId() == R.id.rb_auto ? 1: 0);
			intent.putExtra(KEY_ENABLE_BACKGROUND_PLAY, backgroundPlayCb.isChecked() ? 1: 0);
			intent.putExtra(KEY_SHOW_DEBUG_INFO, showDebugInfoCb.isChecked() ? 1: 0);
			intent.putExtra(KEY_URI, uri);
			intent.setAction(demoDirects[position]);
			startActivity(intent);
		}
	}
}
