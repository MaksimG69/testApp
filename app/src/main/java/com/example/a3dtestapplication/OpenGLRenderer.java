package com.example.a3dtestapplication;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLRenderer implements GLSurfaceView.Renderer {
	private OpenGLGraphNode root = new OpenGLGraphNode();
//	private float[] rgbColor = {255,0,255, 0.7f};
	private float[] rgbColor = {0, 255, 0, 0.7f};
	private boolean change = false;

	public void setChange(boolean bool){this.change = bool;}
	public float[] getRgbColor() {
		return rgbColor;
	}

	public void setRgbColor(float[] rgbColor) {
		this.rgbColor = rgbColor;
	}

	public OpenGLGraphNode getRoot() {
		return root;
	}

	public void setRoot(OpenGLGraphNode root) {
		this.root = root;
	}

	// Empty constructor
	OpenGLRenderer(){}


	public void onSurfaceCreated(@NotNull GL10 gl, EGLConfig config) {
		// background in rgba
		// pink!
		gl.glClearColor(rgbColor[0],rgbColor[1],rgbColor[2], rgbColor[3]);

		// Enable smooth shading
		gl.glShadeModel(GL10.GL_SMOOTH);
		// Depth buffer setup
		gl.glClearDepthf(1.0f);
		// Enable depth testing
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_COLOR_MATERIAL);
		// TODO COLOR
		gl.glColor4f(1.0f,255,0,1.0f);
		// perspective calculation
		gl.glDepthFunc(GL10.GL_LEQUAL);

		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);

		gl.glEnable(GL10.GL_LIGHTING);


		// Check it out
		float[] lightPosition = {0.5f,0.5f,0.5f,1.0f};
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0);

		float[] dif = {0.5f,0.5f,0.5f,1.0f};
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, dif, 0);

		gl.glEnable(GL10.GL_LIGHT0);
	}

	public void onSurfaceChanged(@NotNull GL10 gl, int width, int height) {
		// Sets the current view port to the new size.
		gl.glViewport(0, 0, width, height);// OpenGL docs.
		// Select the projection matrix
		gl.glMatrixMode(GL10.GL_PROJECTION);// OpenGL docs.
		// Reset the projection matrix
		gl.glLoadIdentity();// OpenGL docs.
		// Calculate the aspect ratio of the window
		GLU.gluPerspective(gl, 45.0f,
				(float) width / (float) height,
				1.0f, 2000.0f);
		// Select the modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);// OpenGL docs.
		// Reset the modelview matrix
		gl.glLoadIdentity();// OpenGL docs.
	}

	public void onDrawFrame(@NotNull GL10 gl) {
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		// Replace the current matrix with the identity matrix
		gl.glLoadIdentity();
		// Save the current matrix.
		gl.glPushMatrix();

		//Draw the root
		if (change) {
			Log.d("Draw", "ich will farbe ändern");
			gl.glClearColor(rgbColor[0],rgbColor[1],rgbColor[2], rgbColor[3]);

			change = false;
		}
		root.draw(gl);

		// Restore to the matrix as it was before.
		gl.glPopMatrix();

	}
}
