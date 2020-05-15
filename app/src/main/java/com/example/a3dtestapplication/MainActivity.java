package com.example.a3dtestapplication;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
	private TextView softwareVersion;
	private String versionsNumber = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		try {
			String name = TSSMBTSensor.getInstance().getSoftwareVersion();
			softwareVersion.setText(name);
		} catch (Exception e){
			Log.println(Log.ERROR, "Main", "Cant get software version" + e.toString());
		}
		softwareVersion = findViewById(R.id.SoftwareVersion);
		Button button = findViewById(R.id.VersionShit);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				softwareVersion.setText(versionsNumber);
			}
		});
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> {
			try {

				if (TSSMBTSensor.getInstance().IsConnected()) {
					Snackbar.make(view, "is connected", Snackbar.LENGTH_LONG)
							.setAction("Action", null).show();
					versionsNumber = TSSMBTSensor.getInstance().getSoftwareVersion();
				}
			} catch (Exception e) {
				Log.println(Log.DEBUG, "Main","Cant find device\n" + e.toString());
				Snackbar.make(view, "cant find device", Snackbar.LENGTH_LONG)
						.setAction("Action", null).show();
			}

		});

		Button button1 = findViewById(R.id.toOpenGLActivity);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity();
			}
		});
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


}
