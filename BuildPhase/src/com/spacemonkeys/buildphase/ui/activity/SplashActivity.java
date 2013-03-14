package com.spacemonkeys.buildphase.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;

import com.spacemonkeys.buildphase.R;
import com.spacemonkeys.buildphase.WebActivity;

public class SplashActivity extends Activity implements OnClickListener {
	private static String TAG = "TAG";
	private ImageButton mReport, mMap;
	private Button mWeb;

    @Override
    public void onClick(final View arg0) {
    	Log.e(TAG, "view clicked");

    		if (arg0.equals(mReport)) {
    			final Intent i = new Intent(this, ReportActivity.class);
    			startActivity(i);
    		}

    		if (arg0.equals(mMap)) {
    			final Intent i = new Intent(this, MapActivity.class);
    			startActivity(i);
    		}

    		if (arg0.equals(mWeb)) {
    			final Intent i = new Intent(this, WebActivity.class);
    			startActivity(i);
    		}

    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        mReport = (ImageButton)findViewById(R.id.report);
        mReport.setOnClickListener(this);
        mMap = (ImageButton)findViewById(R.id.map);
        mMap.setOnClickListener(this);
        mWeb = (Button)findViewById(R.id.web);
        mWeb.setOnClickListener(this);
        Log.w(TAG, "onCreate Finished");
    }


}
