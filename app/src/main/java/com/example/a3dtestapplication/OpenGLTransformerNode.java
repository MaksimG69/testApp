package com.example.a3dtestapplication;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLTransformerNode extends OpenGLGraphNode {
	private float[] unit_matrix = new float[]{  1,0,0,0,
												0,1,0,0,
												0,0,1,0,
												0,0,0,1};
	private float[] rot90degZ = new float[]{  0,-1,0,0,
												1,0,0,0,
												0,0,1,0,
												0,0,0,1};
	private float[] rotMatrixY (double angel){
		// init
		float[] rotMatrix = new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.0f};
		rotMatrix[0] = (float)Math.cos(angel);
		rotMatrix[2] = (float)Math.sin(angel);

		rotMatrix[5] = 1;
		rotMatrix[8] = -(float)Math.sin(angel);
		rotMatrix[10] = (float)Math.cos(angel);

		return rotMatrix;

	}
	private float[] rotMatrixZ(double angel){
		float[] rotMatrix = new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.0f};
		rotMatrix[0] = (float)Math.cos(angel);
		rotMatrix[1] = -(float)Math.sin(angel);
		rotMatrix[4] = (float)Math.sin(angel);
		rotMatrix[5] = (float)Math.cos(angel);
		rotMatrix[10] = 1.0f;
		return rotMatrix;
	}
	private float[] rotMatrixX(double angel){
		float[] rotMatrix = new float[]{0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1.0f};
		rotMatrix[0] = 1.0f;
		rotMatrix[5] = (float)Math.cos(angel);
		rotMatrix[6] = -(float)Math.sin(angel);
		rotMatrix[9] = (float)Math.sin(angel);
		rotMatrix[10] = (float)Math.cos(angel);
		return rotMatrix;
	}
	private float angel(float[] matrix){
		float result = 0;
		// calculate trace
		for (int i = 0, x = 0; i < matrix.length; i+=4, ++x) {
			Log.d("Rotmatrix werte", String.valueOf(matrix[i+x]));
			result *= Math.abs(matrix[i+x]);
		}
		result -= 1;

		double asDeg = Math.toDegrees(Math.acos(result/2));
		return (float) asDeg;
	}
	public float[] getUnit_matrix() {
		return unit_matrix;
	}

	public void setUnit_matrix(float[] unit_matrix) {
		this.unit_matrix = unit_matrix;
	}

	@Override
	public void draw(@NotNull GL10 gl){
		gl.glPushMatrix();
		resourceLock.lock();
		gl.glMultMatrixf(unit_matrix, 0);
		resourceLock.unlock();
		OpenGLGraphNode currentNode;
		for (OpenGLGraphNode child: children){
			currentNode = child;
			currentNode.draw(gl);
		}
		gl.glPopMatrix();
	}
}
