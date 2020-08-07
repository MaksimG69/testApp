package com.example.a3dtestapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	private TextView softwareVersionTextView;
	private String versionsNumber = "";
	private Handler handler;
	private Boolean hasChanged = false;
	final private String VERSION_STRING = "Version";
	final private int VERSIONS_CHANGE = 101;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		handler = new Handler(callback);
//		try {
//			String name = TSSMBTSensor.getInstance().getSoftwareVersion();
//			softwareVersionTextView.setText(name);
//		} catch (Exception e){
//			Log.println(Log.ERROR, "Main", "Cant get software version" + e.toString());
//
//		}
		softwareVersionTextView = findViewById(R.id.SoftwareVersion);
		softwareVersionTextView.setText(String.format("%s...", VERSION_STRING));
		Button SWVersionButton = findViewById(R.id.VersionShit);
		String nameForVersionButton = VERSION_STRING;
		SWVersionButton.setText(nameForVersionButton);
		SWVersionButton.setOnClickListener(v -> {
			try {
				hasChanged = true;
				versionsNumber = TSSMBTSensor.getInstance().getSoftwareVersion();
				handler.sendEmptyMessage(VERSIONS_CHANGE);
//				System.out.println("versionsNumber");
				Log.println(Log.DEBUG, "Main", "Versions number is : " + versionsNumber);
			} catch (Exception e){
				Log.println(Log.ERROR, "Main", "Cant get software version" + e.toString());
				Snackbar.make(v, "Cant set number, maybe not connected", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}
		});
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> {
			try {

				if (TSSMBTSensor.getInstance().IsConnected()) {
					Snackbar.make(view, "is connected", Snackbar.LENGTH_LONG)
							.setAction("Action", null).show();
					versionsNumber = TSSMBTSensor.getInstance().getSoftwareVersion();
					Log.println(Log.DEBUG, "Main second Version", "Version is: " + versionsNumber);
				}
			} catch (Exception e) {
				Log.println(Log.DEBUG, "Main","Cant find device\n" + e.toString());
				Snackbar.make(view, "cant find device", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}

		});

		Button startOpenGLButton = findViewById(R.id.toOpenGLActivity);
		String startOpenGLActivity = "Load Open GL";
		startOpenGLButton.setText(startOpenGLActivity);
		startOpenGLButton.setOnClickListener(v -> openActivity());

	}
	private void openActivity(){
		Intent intent = new Intent(this, OpenGL3DTestActivity.class);
		startActivity(intent);
//		System.out.println(name);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void updateTextView(final String text){
		MainActivity.this.runOnUiThread(() -> {
			softwareVersionTextView = findViewById(R.id.SoftwareVersion);
			this.softwareVersionTextView.setText("text");
			this.softwareVersionTextView.invalidate();
		});
	}

	Handler.Callback callback = new Handler.Callback() {
		@Override
		public boolean handleMessage(@NonNull Message msg) {
			if(msg.what == VERSIONS_CHANGE)
				softwareVersionTextView.setText(String.format("%s: %s", VERSION_STRING, versionsNumber));
			return true;
		}
	};
}
