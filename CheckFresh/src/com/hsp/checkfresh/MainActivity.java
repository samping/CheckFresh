package com.hsp.checkfresh;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hsp.checkfresh.runnable.GetNet;
import com.hsp.checkfresh.runnable.LogMessage;
import com.hsp.checkfresh.runnable.MsgTag;

public class MainActivity extends Activity implements OnItemSelectedListener,
		OnClickListener {

	private final static String checkfreshUrl = "http://www.checkfresh.com/";

	ExecutorService fixedThreadPool;
	GetNet getNet;

	Spinner spinner;
	protected ArrayAdapter<String> mAdapter;

	TextView productionData;
	EditText data;
	Button check;

	private int select_item;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		fixedThreadPool = Executors.newFixedThreadPool(3);
		getNet = new GetNet(msgHandler);
		fixedThreadPool.execute(getNet.GetSelectRunnable);

		productionData = (TextView) findViewById(R.id.production_data);
		
		data = (EditText) findViewById(R.id.editText1);
		data.setText("1223");
		check = (Button) findViewById(R.id.check);

		check.setOnClickListener(this);

		spinner = (Spinner) findViewById(R.id.select);
		spinner.setOnItemSelectedListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private Handler msgHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// Toast.makeText(MainActivity.this, (String) msg.obj, 3000).show();
			switch (msg.what) {
			case MsgTag.IO_ERROR:
				Toast.makeText(MainActivity.this, "IO_ERROR", 3000).show();
				break;
			case MsgTag.TIME_OUT:
				Toast.makeText(MainActivity.this, "TIME_OUT", 3000).show();
				break;
			case MsgTag.GET_BANDS_SCS:
				LogMessage.printMsg("GetNet.bands : " + GetNet.bands.length);
				// MainActivity.this.mAdapter = new ArrayAdapter<String>(
				// MainActivity.this,
				// android.R.layout.simple_spinner_dropdown_item,
				// GetNet.bands);
				// spinner.setAdapter(MainActivity.this.mAdapter);

				mAdapter = new ArrayAdapter<String>(MainActivity.this,
						android.R.layout.simple_spinner_item, GetNet.bands);
				LogMessage.printMsg("sucess");
				// 设置下拉列表的风格
				mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				// 将adapter 添加到spinner中
				spinner.setAdapter(mAdapter);
				break;
			case MsgTag.PRODUCT_DATA:
				productionData.setText(msg.getData().getString("data"));
				productionData.invalidate();
			}
		};
	};

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		LogMessage.printMsg(GetNet.bands[arg2]);
		select_item = arg2;
		String uri = (String) GetNet.bandsMap.get(GetNet.bands[arg2]);
		uri = GetNet.brandMsgUri + uri;
		LogMessage.printMsg(uri);
		fixedThreadPool.execute(getNet.GetBrandMsgRunnable);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.check:
			LogMessage.printMsg(data.getText().toString());
			String uri = (String) GetNet.bandsMap
					.get(GetNet.bands[select_item]);
			GetNet.brandDataUri = GetNet.brandDataUri + uri + "&batch="
					+ data.getText().toString();
			LogMessage.printMsg(uri);
			fixedThreadPool.execute(getNet.GetBrandDataRunnable);
			break;
		}
	}
}
