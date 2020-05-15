package com.example.a3dtestapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.io.InputStream;

public class OpenGL3DTestActivity extends AppCompatActivity {
	private boolean is_polling = false;
	static int changer = 1;
	float[] myColor = new float[]{255,0,255, 0.7f};
	float[] myColor1 = new float[]{0, 255, 0, 0.7f};
	OpenGLRenderer renderer;
	private OpenGLUpdateHandler handler = new OpenGLUpdateHandler();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // (NEW) maybe no need
		setContentView(R.layout.activity_open_g_l3_d_test);
		Button change = findViewById(R.id.change);
		String bname = "Change";
		change.setText(bname);
		renderer = new OpenGLRenderer();
		change.setOnClickListener(v -> {
			if(changer < 3)
				changer += 1;
			else changer = 1;
//			changer = 2;
			Log.d("Changer", String.valueOf(changer));
//			Message message = new Message();
//			message.what = handler.TEST_MATRICES;
//			message.arg1 = changer;
//			handler.sendMessage(message);
			renderer.setRgbColor(new float[]{255,0,255, 0.7f});
			renderer.setChange(true);
		});

		GLSurfaceView view = findViewById(R.id.myGLSurfaceView);

		view.setRenderer(renderer);

		OpenGLNodeTranslator cameraNode = new OpenGLNodeTranslator();
		// Change Depth, 0.0 to big!
		cameraNode.setTransformZ(-12.0f);
		cameraNode.setTransformX(0.0f);
		cameraNode.setTransformY(-2.5f);

		renderer.getRoot().addChild(cameraNode);
		OpenGLTransformerNode sensorOrientation = new OpenGLTransformerNode();
		cameraNode.addChild(sensorOrientation);

		InputStream objFile = getResources().openRawResource(R.raw.new_head);
//		Log.d("MyActiv", "Bis hier1");
		OpenGLObject tssNoLED = new OpenGLObject(objFile, this);
//		Log.d("MyActiv", "Bis hier2");
		sensorOrientation.addChild(tssNoLED);
//		Log.d("MyActiv", "Bis hier3");
//		Log.d("MyActiv", "Bis hier4");
		handler.setOrient_node(sensorOrientation);
		Message startAgain = new Message();
		startAgain.what = handler.START_AGAIN;
		startAgain.arg1 = changer;
		handler.sendMessage(startAgain);

	}

	private OpenGLTransformerNode update(){
		return handler.getOrient_node();
	}
}
