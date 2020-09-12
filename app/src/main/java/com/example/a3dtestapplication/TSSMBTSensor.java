package com.example.a3dtestapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

class TSSMBTSensor {
	private BluetoothSocket btSocket;
	private OutputStream BTOutPutStream;
	private InputStream BTInputStream;
	private ReentrantLock call_lock;
	private boolean isConnected, isStreaming;
	private float[] lastPacket = new float[]{0,0,0,1};
	private static TSSMBTSensor instance = null;
	private Vector<Byte> unparsedStreamData = new Vector<>();

	// TODO design new exception to throw
	TSSMBTSensor() throws Exception {
		Log.println(Log.DEBUG, "Sensor", "Sensor is started name");
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		String server_mac = null;
		UUID myUuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
		Set<BluetoothDevice> pairedDevices = adapter.getBondedDevices();
		isConnected = false;
		Log.d("Sensor", String.valueOf(pairedDevices.size()));
		if (pairedDevices.size() > 0){
			for(BluetoothDevice device : pairedDevices){
				String deviceName = device.getName();
				Log.println(Log.DEBUG, "Sensor", "Device name:"+ deviceName);
				// TODO reformat
				if(deviceName.contains("YostLabsMBT")) {
					server_mac = device.getAddress();
					break;
				}
			}
		}
		if (server_mac != null){
			// Get remote device
			BluetoothDevice remote_device = adapter.getRemoteDevice(server_mac);
			btSocket = remote_device.createRfcommSocketToServiceRecord(myUuid);
			// STOP if it enabled
			adapter.cancelDiscovery();
			// Try to connect
			btSocket.connect();

			BTOutPutStream = btSocket.getOutputStream();
			BTInputStream = btSocket.getInputStream();
			call_lock = new ReentrantLock();
			isConnected = true;

		}else {
			Log.println(Log.ERROR, "Sensor", "server mac == null ");
			throw new Exception();
		}
	}

	@Contract(pure = true)
	private byte createChecksum(@NotNull byte[] data){
		byte checksum = 0;
		for(byte value : data){
			checksum += value % 256;
		}
		return checksum;
	}

	@NotNull
	private byte[] read(int value){
		byte[] response = new byte[value];
		int value_read = 0;
		while(value_read < value){
			try {
				value_read += BTInputStream.read(response, value_read, value - value_read);
			} catch (IOException e){
				Log.println(Log.ERROR, " Sensor", "Exception in read\n" + e.toString());
			}

		}
		return response;
	}

	// Close connection
	void close() {
		try {
			btSocket.close();
		} catch (IOException e) {
			Log.println(Log.ERROR, "Sensor", "Error in close \n" + e.toString());
		}
	}
	// To write data
	private void write(@NotNull byte[] data){
		byte[] messageBuffer = new byte[data.length + 2];
		//messageBuffer = data.clone();
		// Write buffer from 1 to data.length + 1, because of this don't use copy!
		System.arraycopy(data, 0, messageBuffer, 1, data.length);
		messageBuffer[0] = (byte) 0xf7;
		messageBuffer[data.length + 1] = createChecksum(data);
		try {
			BTOutPutStream.write(messageBuffer);
			BTOutPutStream.flush();
		} catch (IOException e) {
			Log.println(Log.ERROR, "Sensor", "Error in write \n" + e.toString());
		}
	}

	private void writeReturnHead(@NotNull byte[] data){
		byte[] messageBuffer = new byte[data.length + 2];
		System.arraycopy(data,0,messageBuffer,1,data.length);
		messageBuffer[0] = (byte) 0xf9;
		messageBuffer[data.length + 1] = createChecksum(data);
		try {
			BTOutPutStream.write(messageBuffer);
			BTOutPutStream.flush();
		} catch (IOException e) {
			Log.println(Log.ERROR, "Sensor", "Error in write header \n" + e.toString());
		}
	}

	void startStreaming(){
		call_lock.lock();

		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(0x48);
		byte[] header = buffer.array();
		// copy from app
		byte[] dataToSend = new byte[]{(byte)0xdd,header[0],header[1],header[2],header[3]};
		write(dataToSend);

		// Send command to sensor, 255 is nothing
		dataToSend = new byte[]{(byte)0x50,(byte)0,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255,(byte)255};
		write(dataToSend);

		buffer.putInt(0,1000);
		byte[] interval = buffer.array();
		buffer.putInt(0, 0xffffffff);
		byte[] duration = buffer.array();
		buffer.putInt(0,0);
		byte[] delay = buffer.array();
		dataToSend = new byte[]{(byte)0x52,interval[0],interval[1],interval[2],interval[3],duration[0],duration[1],duration[2],duration[3],delay[0],delay[1],delay[2],delay[3]};
		write(dataToSend);
		// last
		dataToSend = new byte[]{(byte)0x55};
		writeReturnHead(dataToSend);
		isStreaming = true;
		// Unlock the ReentrantLock
		call_lock.unlock();
	}

	void stopStreaming() {
		call_lock.lock();
		byte[] dataToSend = new byte[]{(byte) 0x56};
		write(dataToSend);

		try {
			// Try stop, if not stop try again!
			while (BTInputStream.available() != 0) {
				BTInputStream.skip(BTInputStream.available());
				Thread.sleep(1000);
			}
		} catch (IOException e) {
			Log.println(Log.ERROR, "Sensor", "Error in stopStreaming" + e.toString());
			return;
		} catch (InterruptedException e) {
			Log.println(Log.ERROR, "Sensor", "Error interrupt thread sleep" + e.toString());
		}
		// Streaming over set the boolean to false
		isStreaming = false;
		call_lock.unlock();
	}

	boolean IsConnected() {return isConnected;}
	boolean isStreaming() {return isStreaming;}

	String getSoftwareVersion(){
		Log.println(Log.DEBUG, "Sensor", "Getting software version");
		if(isStreaming){
			stopStreaming();
			call_lock.lock();
			byte[] sendData = new byte[]{(byte)0xdf};
			write(sendData);
			byte[] version = read(12);
			call_lock.unlock();
			startStreaming();
			return new String(version);
		}else {
			call_lock.lock();
			byte[] sendData = new byte[]{(byte)0xdf};
			write(sendData);
			byte[] version = read(12);
			call_lock.unlock();
			return new String(version);
		}
	}

	static TSSMBTSensor getInstance() throws Exception {
		return (instance == null) ? instance = new TSSMBTSensor() : instance;
	}

	void getTaredMatrix(){
		call_lock.lock();
		byte[] sendData = new byte[]{(byte) 0xf7};
		write(sendData);
		byte[] responseData = read(16*4);
		Log.d("Resposnse Matrix", Arrays.toString(TSSMBTSensorCalculate.binaryToFloatCalc(responseData)));
		call_lock.unlock();
	}
	float[] getFilteredOrientation(){
		// First lock thread for changing
		call_lock.lock();
		// Check if sensor is streaming
		if (isStreaming){
			try {
				// TODO Check and test this function (change magic number  18)
				if (unparsedStreamData.size() + BTInputStream.available() < 18){
					return lastPacket;
				}
				byte[] responseResult = new byte[1];
				responseResult = read(BTInputStream.available());
				// Unlock thread
				call_lock.unlock();

				for (byte response:
				     responseResult) {
					Log.d("Filter", "response: " + response);
					unparsedStreamData.add(response);
				}
				// REFOPRMAT Change magic number 18
				int location = unparsedStreamData.size() - 18;
				Log.d("Filter", "Location -18: " + location);
				while (location > 0){
					byte checksum = (byte) unparsedStreamData.toArray()[location];
					byte dataLength = (byte) unparsedStreamData.toArray()[location + 1];
					Log.d("Filter", "Checksum: " + checksum);
					Log.d("Filter", "dataLength: " + dataLength);

					if ((dataLength & 255) == 16){
						byte result = 0;
						byte[] quat = new byte[16];
						for (int i = 0; i < quat.length; i++) {
							// TODO check if this cast is ok
							quat[i] = (byte) unparsedStreamData.toArray()[location+i+2];
							result += quat[i];
						}
						if ((result & 255) == (checksum & 255)){
							// Calculate bin to float
							float[] tempResult = TSSMBTSensorCalculate.binaryToFloatCalc(quat);
							if (TSSMBTSensorCalculate.quaternionCheck(tempResult)){
								unparsedStreamData.subList(0,location+18).clear();
								lastPacket = tempResult;
								return lastPacket;
							}
						}
					}
					location -= 1;
				}
				return lastPacket;
			}catch (Exception e){
				// nothing to do so far
				return lastPacket;
			}
		}
		// hey 0
		byte[] sendData = new byte[]{(byte)0x00};
		write(sendData);
		// 16 array length
		byte[] responseData = read(16);
		call_lock.unlock();
		return TSSMBTSensorCalculate.binaryToFloatCalc(responseData);
	}


}
