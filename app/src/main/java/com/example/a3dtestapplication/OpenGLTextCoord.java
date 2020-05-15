package com.example.a3dtestapplication;

public class OpenGLTextCoord {
	private float[] elements = {0.0f,0.0f};

	public OpenGLTextCoord(float u, float v)
	{
		elements[0] = u;
		elements[1] = v;
	}

	public float getU()
	{
		return elements[0];
	}

	public float getV()
	{
		return elements[1];
	}

}
