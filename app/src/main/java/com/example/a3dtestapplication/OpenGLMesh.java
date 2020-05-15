package com.example.a3dtestapplication;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLMesh {
	private FloatBuffer verticesBuffer=null, normalBuffer=null, textCoordinatesBuffer = null;

	// index for indices
	private ShortBuffer indicesBuffer = null;

	// Bitmap for texture loading
	private Bitmap textureBitmap;

	// num of indices and texture id
	private int numOfIndices = -1 , textureId = -1;

	// Base Material Color
	private float[] ambient_color = new float[]{0.3f, 0.3f, 0.3f, 1.0f};
	private float[] diffuse_color = new float[]{0.8f, 0.8f, 0.8f, 1.0f};
	private float[] specular_color = new float[]{1.0f, 1.0f, 1.0f, 1.0f};
	private float shininess_factor = 50.0f;

	// if texture is not loaded so far
	private boolean shouldTextureLoad = false;

//	OpenGLMesh() {}

	void draw(GL10 gl){
		// counter-clockwise winding (gegen uhrzeigersinn)
		gl.glFrontFace(GL10.GL_CCW);

		gl.glEnable(GL10.GL_CULL_FACE);

		gl.glCullFace(GL10.GL_BACK);

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);



		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, verticesBuffer);

		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);

		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);

		// Set base material color
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, ambient_color, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, diffuse_color, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, specular_color, 0);
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininess_factor);


		if (shouldTextureLoad) {
			// load GL texture
			loadGLTexture(gl);
			shouldTextureLoad = false;
		}

		if(textureId != -1 && textCoordinatesBuffer != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);

			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textCoordinatesBuffer);

			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		}

		// Actual drawing

		gl.glDrawElements(GL10.GL_TRIANGLES, numOfIndices, GL10.GL_UNSIGNED_SHORT, indicesBuffer);

		// Disable texture
		if (textureId != -1 && textCoordinatesBuffer != null) {
			gl.glDisable(GL10.GL_TEXTURE_2D);
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		// Disable normal buffer
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		// Disable vertices buffer
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		// Disable face culling
		gl.glDisable(GL10.GL_CULL_FACE);

	}

	void setVertices(float[] vertices){
		// Because a float is 4 byte, need to multiply with 4!
		Log.d("Mesh", "Vertices wurden gesetzt");
		Log.d("Mesh groesse", "vertices length: "+ vertices.length);
		ByteBuffer verticesByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		verticesByteBuffer.order(ByteOrder.nativeOrder());
		// Init buffer
		verticesBuffer = verticesByteBuffer.asFloatBuffer();
		// Put vertices in Buffer
		verticesBuffer.put(vertices);
		// Always new position at 0
		verticesBuffer.position(0);
		Log.d("mMsh to", verticesBuffer.toString());

	}
	// Same as vertices buffer
	void setNormals(float[] normals){
		ByteBuffer normalsByteBuffer = ByteBuffer.allocateDirect(normals.length * 4);
		normalsByteBuffer.order(ByteOrder.nativeOrder());
		// Init buffer
		normalBuffer = normalsByteBuffer.asFloatBuffer();
		normalBuffer.put(normals);
		normalBuffer.position(0);
	}

	void setIndices(short[] indices){
		// Multiply with 2 because the size of short is 2 bytes
		ByteBuffer indicesByteBuffer = ByteBuffer.allocateDirect(indices.length * 2);
		indicesByteBuffer.order(ByteOrder.nativeOrder());
		// Init buffer
		indicesBuffer = indicesByteBuffer.asShortBuffer();
		indicesBuffer.put(indices);
		indicesBuffer.position(0);
		numOfIndices = indices.length;
	}
	// Dont understand this function..
	void setColor(float[] ambient, float[] diffuse, float[] specular, float shininess) {
		// Setting the flat color.
		ambient_color[0] = ambient[0]; ambient_color[1] = ambient[1]; ambient_color[2] = ambient[2]; ambient_color[3] = ambient[3];
		diffuse_color[0] = diffuse[0]; diffuse_color[1] = diffuse[1]; diffuse_color[2] = diffuse[2]; diffuse_color[3] = diffuse[3];
		specular_color[0] = specular[0]; specular_color[1] = specular[1]; specular_color[2] = specular[2]; specular_color[3] = specular[3];
		shininess_factor = shininess;
	}

	void setCoordinates(float[] coordinates){
		// Multiply with 4 because float is 4 byte
		ByteBuffer coordinatesByteBuffer = ByteBuffer.allocateDirect(coordinates.length * 4);
		coordinatesByteBuffer.order(ByteOrder.nativeOrder());
		textCoordinatesBuffer = coordinatesByteBuffer.asFloatBuffer();
		textCoordinatesBuffer.put(coordinates);
		textCoordinatesBuffer.position(0);
	}

	void loadBitmap(Bitmap bitmap){
		this.textureBitmap = bitmap;
		shouldTextureLoad = true;

	}

	private void loadGLTexture(GL10 gl){
		// Generate one texture pointer...
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		textureId = textures[0];

		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		// Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR);

		// Different possible texture parameters
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from the bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textureBitmap, 0);
	}
}
