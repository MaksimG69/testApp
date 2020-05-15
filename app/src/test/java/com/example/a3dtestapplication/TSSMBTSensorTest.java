package com.example.a3dtestapplication;

import org.junit.Test;

import java.util.AbstractMap;
import java.util.Vector;

import static com.example.a3dtestapplication.TSSMBTSensorCalculate.unit_matrix;
import static org.junit.Assert.*;
public class TSSMBTSensorTest {



	private Vector<AbstractMap.SimpleEntry<byte[], float[]>> arguments(){
		Vector<AbstractMap.SimpleEntry<byte[], float[]>> result = new Vector<>();
		result.add(new AbstractMap.SimpleEntry<>(new byte[]{-67, 71, -27, -40, -67, 27, -26, -94, -65, 48, -52, -56, 63, 56, 118, -94},
												new float[]{-0.04880318f, -0.038061745f, -0.6906247f, 0.7205602f}));
		result.add(new AbstractMap.SimpleEntry<>(new byte[]{-67, 71, -32, 15, -67, 27, -17, 123, -65, 48, -51, 37, 63, 56, 118, 73},
												new float[]{-0.048797663f, -0.038070183f, -0.69063026f, 0.7205549f}));
		result.add(new AbstractMap.SimpleEntry<>(new byte[]{-67, 71, -14, 93, -67, 27, -27, -112, -65, 48, -52, 28, 63, 56, 119, 59},
												new float[]{-0.04881512f, -0.038060725f, -0.69061446f, 0.7205693f}));
		result.add(new AbstractMap.SimpleEntry<>(new byte[]{-67, 71, -21, 14, -67, 27, -24, -52, -65, 48, -52, 81, 63, 56, 119, 15},
												new float[]{-0.04880815f, -0.03806381f, -0.6906176f, 0.7205667f}));
		result.add(new AbstractMap.SimpleEntry<>(new byte[]{-67, 72, 32, 119, -67, 27, -20, -86, -65, 48, -54, -62, 63, 56, 120, 78},
												new float[]{-0.048859086f, -0.038067497f, -0.69059384f, 0.7205857f}));

		return result;
	}

	@Test
	public void binToFloat(){
		for (AbstractMap.SimpleEntry map:
		     arguments()) {
			assertArrayEquals((float[]) map.getValue(), TSSMBTSensorCalculate.binaryToFloatCalc((byte[]) map.getKey()), 0.0f);
		}
	}

	@Test
	public void floatToBin(){
		for (AbstractMap.SimpleEntry map:
		     arguments()) {
			assertArrayEquals((byte[])map.getKey(), TSSMBTSensorCalculate.floatToBinaryCalc((float[]) map.getValue()));
		}
	}

	@Test
	public void something(){
		int bla = 25;
		byte b = (byte)0x00;
		for (int i = 0; i < 25; i++) {
			byte bule = Byte.parseByte(Integer.toHexString(i), 16);
			System.out.println("Bla : " + i + " bule : " + (byte)bule + " Integer " + Integer.toHexString(i));

			double f = 1.000000059604643;
			double g = 0.9999999701976772;
			System.out.println("f: " + Math.abs(1-f));
			System.out.println("g: " + Math.abs(1-g));
			System.out.println("g: " + (1-g));
		}
	}

	@Test
	public void mathShit(){


		// First test unit matrix
		assertEquals(0, TSSMBTSensorCalculate.angel(unit_matrix), 0.001f);
		for (float i = 0; i < 180; ++i) {
			float[] matrixX = TSSMBTSensorCalculate.rotMatrixX(i);
			float[] matrixY = TSSMBTSensorCalculate.rotMatrixY(i);
			float[] matrixZ = TSSMBTSensorCalculate.rotMatrixZ(i);
//			System.out.println("Matrix Z: " + Arrays.toString(matrix) + " Angel " + String.valueOf(TSSMBTSensorCalculate.angel(matrix)));
			assertEquals(i, TSSMBTSensorCalculate.angel(matrixX), 0.001f);
			assertEquals(i, TSSMBTSensorCalculate.angel(matrixY), 0.001f);
			assertEquals(i, TSSMBTSensorCalculate.angel(matrixZ), 0.001f);
		}

	}
}
