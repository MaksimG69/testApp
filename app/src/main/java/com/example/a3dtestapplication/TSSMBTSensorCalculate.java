package com.example.a3dtestapplication;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static java.lang.Math.*;
import static java.lang.Math.toRadians;

/**
 * Utility class for calculate functions in bluetooth sensor
 * */
final class TSSMBTSensorCalculate {

	/**
	 * Don't let anyone instantiate this class.
	 */
	private TSSMBTSensorCalculate(){}

	static float[] binaryToFloatCalc(byte[] b)	{
		if (b.length % 4 != 0){
			return new float[0];
		}
		float[] return_array = new float[b.length / 4];
		int resultArrayCount = 0;
		for (int i = 0; i < b.length; i += 4, ++resultArrayCount){
			// count bits and calculate float
			// TODO Try to replace '+' with '|' maybe faster
			int binAsInt = (b[i + 3] & 255)
					+ ((b[i + 2] & 255) << 8) 	// * 2⁸
					+ ((b[i + 1] & 255) << 16) 	// * 2¹⁶
					+ ((b[i] & 255) << 24); 	// * 2²⁴

			return_array[resultArrayCount] = Float.intBitsToFloat(binAsInt);
		}
		return return_array;
	}

	@NotNull
	static byte[] floatToBinaryCalc(@NotNull float[] floats){
		ByteBuffer buffer = ByteBuffer.allocate(4*floats.length);
		FloatBuffer floatBuffer = buffer.asFloatBuffer();
		floatBuffer.put(floats);
		return buffer.array();
	}

	private static float square(float f){
		return f*f;
	}

	static boolean quaternionCheck(@NotNull float[] orientation){
		if (orientation.length != 4)
			return false;
		double length = sqrt(square(orientation[0]) + square(orientation[1]) + square(orientation[2]) + square(orientation[3]));

		Log.d("Length", String.valueOf(length));
		return abs(1 - length) < 1f;
	}

	static float[] unit_matrix = new float[]{  1,0,0,0,
			0,1,0,0,
			0,0,1,0,
			0,0,0,1};

	static float[] rot90degZ = new float[]{  0,-1,0,0,
			1,0,0,0,
			0,0,1,0,
			0,0,0,1};

	static float[] rotMatrixY (double angel){
		// init
		float[] rotMatrix = new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.0f};
		double sin = sin(toRadians(angel));
		double cos = cos(toRadians(angel));
		rotMatrix[0] = (float) cos;
		rotMatrix[2] = (float) sin;
		rotMatrix[5] = 1;
		rotMatrix[8] = -(float) sin;
		rotMatrix[10] = (float) cos;

		return rotMatrix;

	}

	static float[] rotMatrixZ(double angel){
		//init
		float[] rotMatrix = new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.0f};
		double cos = cos(toRadians(angel));
		double sin = sin(toRadians(angel));

		rotMatrix[0] = (float) cos;
		rotMatrix[1] = -(float) sin;
		rotMatrix[4] = (float) sin;
		rotMatrix[5] = (float) cos;
		rotMatrix[10] = 1.0f;
		return rotMatrix;
	}
	static float[] rotMatrixX(double angel){
		// init
		float[] rotMatrix = new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.0f};
		double cos = cos(toRadians(angel));
		double sin = sin(toRadians(angel));
		rotMatrix[0] = 1.0f;
		rotMatrix[5] = (float) cos;
		rotMatrix[6] = -(float) sin;
		rotMatrix[9] = (float) sin;
		rotMatrix[10] = (float) cos;
		return rotMatrix;
	}
	static float angel(float[] matrix){
		float result = 0;
		// calculate trace
		for (int i = 0, x = 0; i < matrix.length; i+=4, ++x) {
			result += matrix[i+x];
		}
		// -2 because of the 3x3 size
		result -= 2;
		return (float) toDegrees(acos(result/2));
	}

}
