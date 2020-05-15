package com.example.a3dtestapplication;

import org.jetbrains.annotations.NotNull;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLNodeTranslator extends OpenGLGraphNode {
	private float transformX=0.0f, transformY=0.0f, transformZ = 0.0f;

	public float getTransformX() {
		return transformX;
	}

	public float getTransformY() {
		return transformY;
	}

	public float getTransformZ() {
		return transformZ;
	}

	public void setTransformX(float transformX) {
		this.transformX = transformX;
	}

	public void setTransformY(float transformY) {
		this.transformY = transformY;
	}

	void setTransformZ(float transformZ) {
		this.transformZ = transformZ;
	}

	@Override
	public void draw(@NotNull GL10 gl){
		gl.glPushMatrix();
		gl.glTranslatef(transformX,transformY,transformZ);
		OpenGLGraphNode currentNode;
		for (OpenGLGraphNode child:
		     children) {
			currentNode = child;
			currentNode.draw(gl);
		}
		gl.glPopMatrix();
	}
}
