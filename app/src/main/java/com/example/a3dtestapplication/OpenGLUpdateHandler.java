package com.example.a3dtestapplication;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Arrays;

public class OpenGLUpdateHandler extends Handler {
	private OpenGLTransformerNode orient_node;
	private boolean keepGoing = false;
	private float xx,yy,zz,ww,xy,xz,yz,wx,wy,wz;
	public final int TOGGLE_GOING = 100;
	public final int START_AGAIN = 200;
	public final int START_STREAMING = 1;
	public final int TEST_MATRICES = 2;
	float[] matrix = new float[16];
	public OpenGLTransformerNode getOrient_node() {
		return orient_node;
	}

	public void setOrient_node(OpenGLTransformerNode orient_node) {
		this.orient_node = orient_node;
	}

	public boolean isKeepGoing() {
		return keepGoing;
	}

	public void setKeepGoing(boolean keepGoing) {
		this.keepGoing = keepGoing;
	}

	@Override
	public void handleMessage(@NonNull Message msg) {
//		Log.d("UpdateHandler", "Started");
		Log.d("UpdateHandler msg", String.valueOf(msg.what));
		Log.d("UpdateHandler arg1", String.valueOf(msg.arg1));



		if (msg.what == TOGGLE_GOING){
			keepGoing = false;
			try {
				TSSMBTSensor.getInstance().stopStreaming();
				Log.d("UpdateHandler", "Is connected");
			}catch (Exception e){
				Log.println(Log.ERROR, "UpdateHandler", "error by stop streaming \n" + e.getMessage());
				return;
			}
		}else if (msg.what == START_AGAIN && !keepGoing){
			keepGoing = true;
			Message tmpmsg = new Message();
			tmpmsg.what = START_STREAMING;
			Log.println(Log.ERROR, "UpdateHandler", String.valueOf(keepGoing));
			sendMessageDelayed(tmpmsg, 200);
		}else if(msg.what == START_STREAMING){
			Log.println(Log.ERROR, "UpdateHandler what = 1 ", String.valueOf(keepGoing));
			if (keepGoing){
				try {
					if (!TSSMBTSensor.getInstance().isStreaming()){
						TSSMBTSensor.getInstance().startStreaming();
						Log.d("UpdateHandler", "Is connected");
					}
				}catch(Exception e){
					Log.println(Log.ERROR, "UpdateHandler", "Error by start streaming \n" + e.getMessage());
					return;
				}
				//Update the GL coordinates
				float[] orientation;
				try {
					orientation = TSSMBTSensor.getInstance().getFilteredOrientation();
					TSSMBTSensor.getInstance().getTaredMatrix();
//					Log.d("orientation " , Arrays.toString(orientation));
					// abcd prepair for rotations matrix (3x3)
					xx = orientation[0] * orientation[0];
					yy = orientation[1] * orientation[1];
					zz = orientation[2] * orientation[2];
					ww = orientation[3] * orientation[3];
					xy = orientation[0] * orientation[1] * 2.0f;
					xz = orientation[0] * orientation[2] * 2.0f;
					yz = orientation[1] * orientation[2] * 2.0f;
					wx = orientation[3] * orientation[0] * 2.0f;
					wy = orientation[3] * orientation[1] * 2.0f;
					wz = orientation[3] * orientation[2] * 2.0f;

//					Log.d("orientation " , "XX: "+ xx + " YY: "+ yy + " ZZ: "+ zz + " WW: " + ww + " xy: " + xy + " xz: " + xz + " yz: " + yz + " wx: " + wx + " wy: " + wy + " wz: " + wz);
//					Log.d("orientation matrix for lock", Arrays.toString(orient_node.getUnit_matrix()));
					orient_node.resourceLock.lock();

					orient_node.getUnit_matrix()[0] = ww + xx - yy - zz;
					orient_node.getUnit_matrix()[1] = xy + wz;
					orient_node.getUnit_matrix()[2] = xz - wy;
					orient_node.getUnit_matrix()[4] = xy - wz;
					orient_node.getUnit_matrix()[5] = ww - xx + yy - zz;
					orient_node.getUnit_matrix()[6] = yz + wx;
					orient_node.getUnit_matrix()[8] = xz + wy;
					orient_node.getUnit_matrix()[9] = yz - wx;
					orient_node.getUnit_matrix()[10] = ww - xx - yy + zz;
					orient_node.resourceLock.unlock();
//					Log.d("orientation matrix after lock", Arrays.toString(orient_node.getUnit_matrix()));

					//call yourself again after 10 mils
					sendMessageDelayed(obtainMessage(START_STREAMING, 0,0), 20);
				} catch (Exception e) {
					e.printStackTrace();
				}


			}
		}


	}

	private void matrixChanger(int status){
		Log.d("orientation status", String.valueOf(status));
		switch (status){
			case 1:
				matrix = new float[]{   8.9706E-41f, 0.009196959f, 0.9982066f, -0.005235569f,
						-0.058918744f, -0.14085421f, -1.4929688E-30f, -5.530622E-33f,
						-2.924605E-15f, -1.5432081E34f, 0.009179385f, 0.9981896f,
						-0.0051151756f, -0.059222013f, 5.247228E-10f, 78.124016f};
				return;
			case 2:
				matrix = new float[]{  3.3582236E-8f, 8.1363168E15f, 2.1969847E26f, -1.185925E-8f,
						-3.0706042E-7f, 0.008741276f, 0.99772125f, -0.0018289819f,
						-0.06687716f, 3.2026516E-14f, 1.75863792E18f, 5.704017E25f,
						2.3922502E-33f, 1.1180477E-13f, 0.008747515f, 0.9977072f};
				return;
			case 3:
				matrix = new float[]{1.8202757E38f, 2886191.2f, -3.1742694E-13f, 3.7214645E-29f,
						5.519501E19f, -2.8060103E-6f, -1.7836485E-35f, 1.2126945E-37f,
						-0.1926813f, 1.7069278E38f, 6.0730486E12f, -2.8528475E-12f,
						3.723223E-29f, -4.4586145E-8f, 1.5954816E-19f, -1.9697005E36f};
				return;
			default:
				matrix = new float[] { 3.7389995E-29f, 1.2549737E7f, 4.1887253E-14f, 1.9149437E-26f,
						-4.5335325E16f, -2.4104256E-23f, 7.5964905E37f, -3.839782E-15f,
						-5.397652E-17f, 3.7394385E-29f, -1.0885154E-11f, 2.025752E11f,
						3.1509038E37f, -27016.12f, -5.938536E-20f, 7.310525E37f};
		}
	}
}
