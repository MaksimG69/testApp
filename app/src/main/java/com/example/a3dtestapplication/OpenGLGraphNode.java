package com.example.a3dtestapplication;

import androidx.annotation.NonNull;

import java.util.Vector;
import java.util.concurrent.locks.ReentrantLock;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLGraphNode {
	OpenGLGraphNode parent = null;
	Vector<OpenGLGraphNode> children = new Vector<>();
	ReentrantLock resourceLock;

	OpenGLGraphNode() {
		resourceLock = new ReentrantLock();
	}

	public void addChild(@NonNull OpenGLGraphNode child){
		child.parent = this;
		this.children.add(child);
	}

	public void draw(@NonNull GL10 gl10){
		OpenGLGraphNode currentNode;

		for (OpenGLGraphNode child : children){
			currentNode = child;
			currentNode.draw(gl10);
		}
	}
}
