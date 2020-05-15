package com.example.a3dtestapplication;
import android.graphics.Bitmap;

public class OpenGLMaterial {
	public float[] ambient_color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	public float[] diffuse_color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	public float[] specular_color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	public float shininess = 0.0f;

	public Bitmap textureBitmap = null;
}
