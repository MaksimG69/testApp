package com.example.a3dtestapplication;


import android.graphics.Path;

public class OpenGLHelpVector {
	private float[] elements = {0,0,0};

	public OpenGLHelpVector(){}

	public OpenGLHelpVector(float x, float y, float z){
		this.elements[0] = x;
		this.elements[1] = y;
		this.elements[2] = z;

	}

	float getX(){return elements[0];}
	float getY(){return elements[1];}
	float getZ(){return elements[2];}

	private float squareX(){return this.elements[0] * this.elements[0];}
	private float squareY(){return this.elements[1] * this.elements[1];}
	private float squareZ(){return this.elements[2] * this.elements[2];}

	protected void setX(float x){this.elements[0] = x;}
	protected void setY(float y){this.elements[1] = y;}
	protected void setZ(float z){this.elements[2] = z;}

	float[] getElements(){return this.elements;}

	void setElements(float x, float y, float z) {
		this.elements[0] = x;
		this.elements[1] = y;
		this.elements[2] = z;
	}

	float getVectorLength(){
		return (float) Math.sqrt((this.squareX() + this.squareY() + this.squareZ()));
	}

	void addOtherVector(OpenGLHelpVector other){
		this.elements[0] += other.getX();
		this.elements[1] += other.getY();
		this.elements[2] += other.getZ();
	}
	OpenGLHelpVector addOtherVectorCopy(OpenGLHelpVector other){
		OpenGLHelpVector vector = new OpenGLHelpVector(getX(), getY(), getZ());
		vector.addOtherVector(other);
		return vector;
	}
	void subOtherVector(OpenGLHelpVector other){
		elements[0] -= other.getX();
		elements[1] -= other.getY();
		elements[2] -= other.getZ();
	}
	OpenGLHelpVector subOtherVectorCopy(OpenGLHelpVector other){
		OpenGLHelpVector vector = new OpenGLHelpVector(getX(), getY(), getZ());
		vector.subOtherVector(other);
		return vector;
	}
	void mulVectorWithScalar(float scalar){
		for (int i = 0; i < 3; ++i) {
			this.elements[i] *= scalar;
		}
	}

	OpenGLHelpVector mulVectorWithScalarCopy(float scalar) {
		OpenGLHelpVector result = new OpenGLHelpVector(getX(), getY(), getZ());
		result.mulVectorWithScalar(scalar);
		return result;
	}

	float mulVectorWithOtherVector(OpenGLHelpVector other){
		float scalar = 0;
		for (int i = 0; i < 3; ++i){
			scalar += this.elements[i] * other.getElements()[i];
		}
		return scalar;
	}

	void normalizeVector(){
		float length = getVectorLength();
		if (length > 0.0f){
			elements[0] /= length;
			elements[1] /= length;
			elements[2] /= length;
		}

	}
/*
	public void normalize()
	{
		float length = getVectorLength();
		if (length > 0.0f)
		{
			elements[0] /= length;
			elements[1] /= length;
			elements[2] /= length;
		}
	}
*/
	OpenGLHelpVector normalizeVectorCopy(){
		OpenGLHelpVector vector = new OpenGLHelpVector(getX(), getY(), getZ());
		vector.normalizeVector();
		return vector;
	}

	void crossProduct(OpenGLHelpVector other){
		float x = getY() * other.getZ() - getZ() * other.getY();
		float y = getZ() * other.getX() - getX() * other.getZ();
		float z = getX() * other.getY() - getY() * other.getX();
		setElements(x,y,z);
	}

	OpenGLHelpVector crossProductCopy(OpenGLHelpVector other){
		OpenGLHelpVector vector = new OpenGLHelpVector(getX(), getY(), getZ());
		vector.crossProduct(other);
		return vector;
	}

	public float dot(OpenGLHelpVector other)
	{
		return getX() * other.getX() + getY() * other.getY() + getZ() * other.getZ();
	}
}
