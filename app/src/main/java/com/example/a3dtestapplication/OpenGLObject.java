package com.example.a3dtestapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public class OpenGLObject extends OpenGLGraphNode  {
	private Vector<OpenGLMesh> meshes = new Vector<>();
	private HashMap<String, OpenGLMaterial> materials = new HashMap<>();
	// init string here!
	private String currentMaterial = null;

	public void draw(@NotNull GL10 gl10){
		OpenGLMesh currentMesh;
//		OpenGLGraphNode[] children = null;
		for (OpenGLMesh mesh:
				meshes){
			currentMesh = mesh;
			currentMesh.draw(gl10);
		}
		OpenGLGraphNode currentNode;
		for (OpenGLGraphNode node:
		     children) {
			currentNode = node;
			currentNode.draw(gl10);
		}
	}

	private String readLine(InputStream inputStream){
		Vector<Byte> byteBuffer = new Vector<>();
		int currentByte = 0;
		while (currentByte != 10){ // NewLine charachter
			try {
				currentByte = inputStream.read();
			} catch (IOException e) {
				Log.println(Log.DEBUG, "OpenGLObject", "Fail read input stream");
			}

			if (currentByte == -1)
				break;
			byteBuffer.add((byte) currentByte);

		}
		if (byteBuffer.size() == 0){
			return null;
		}
		else {
			byte[] bytesForString = new byte[byteBuffer.size()];
			for (int i = 0; i < byteBuffer.size(); ++i){
				bytesForString[i] = byteBuffer.elementAt(i);
			}
			return new String(bytesForString);
		}
	}

	private void scan(@NonNull Context context) throws IOException {
//		Scanner scanner = new Scanner(context.getAssets().open("da_real_torus.obj"));
		Scanner scanner = new Scanner(context.getAssets().open("torus.obj"));
		while(scanner.hasNext()) {
			String line = scanner.nextLine();
//			if (line.startsWith("v "))
				//add vertex line to verticesList
//				this.verticesList.add(line);
//			else if (line.startsWith("f "))
				// add face line to facedList

//				this.facesList.add(line);
		}
		scanner.close();
	}

	private void loadMaterial(InputStream inputStream, Activity myApp){
		String line = null;
		while((line = readLine(inputStream)) != null){
			switch (line.charAt(0)){
				case '#':
					// skip comment
					continue;
				case 'n':
					//String
					if (line.substring(0,6).equals("newmtl")){
						String material_name = line.substring(7,line.length() -1 );
						materials.put(material_name, new OpenGLMaterial());
						currentMaterial = material_name;
					}
				case 'N':
					if (line.substring(0,2).equals("Ns")){
						float newSpecWeight = Float.parseFloat(line.substring(3,line.length()-1));
						materials.get(currentMaterial).shininess = newSpecWeight;
					} else if (line.substring(0,2).equals("Ni")){
						continue;
					}
				case 'K':
					if (line.substring(0,2).equals("Ka")){
						String[] lineParts = line.split("\\s+");
						float[] new_ambient = new float[]{new Float(lineParts[1]),new Float(lineParts[2]), new Float(lineParts[3]), 1.0f};
						materials.get(currentMaterial).ambient_color = new_ambient;

					} else if(line.substring(0,2).equals("Kd")) {
						//Diffuse Reflectivity
						String [] line_parts = line.split("\\s+");
						float[] new_diffuse = new float[]{new Float(line_parts[1]), new Float(line_parts[2]),new Float(line_parts[3]), 1.0f};
						materials.get(currentMaterial).diffuse_color = new_diffuse;
					} else if(line.substring(0,2).equals("Ks")) {
						//Specular Reflectivity
						String [] line_parts = line.split("\\s+");
						float[] new_specular = new float[]{new Float(line_parts[1]), new Float(line_parts[2]),new Float(line_parts[3]), 1.0f};
						materials.get(currentMaterial).specular_color = new_specular;
					}
				case 'm':
					if (line.substring(0,4).equals("map_"))
					{
						//This is a color map of some sort. Regardless of its intended reflectivity
						//of application, we will treat it as a single texture to be applied to the
						//mesh
						int slash_idx = line.lastIndexOf("\\\\");
						String texture_file_name = line.substring(slash_idx + 2, line.length() - 5);
						Log.d("File : ", texture_file_name);
						int tex_file_id = myApp.getResources().getIdentifier(texture_file_name, "drawable", "yei.tssBtTestApp");

						Log.d("File : ", " Here id : " + tex_file_id);
						Bitmap tmp_bitmap = BitmapFactory.decodeResource(myApp.getResources(), tex_file_id);
						materials.get(currentMaterial).textureBitmap = tmp_bitmap;
					}
			}
		}
	}
	/*
		public OpenGLObject(InputStream obj_in_stream, Activity myApp) {

		//Lets set up some storage constructs for parsing the obj
		Vector<OpenGLHelpVector> raw_verts = new Vector<>();
		Vector<OpenGLHelpVector> raw_normals = new Vector<>();
		Vector<OpenGLTextCoord> raw_tex_coord = new Vector<>();

		Vector<Float> final_verts = new Vector<Float>();
		Vector<Float> final_norms = new Vector<Float>();
		Vector<Float> final_tex_coords = new Vector<Float>();
		Vector<Short> final_indices = new Vector<Short>();

		String line = null;
		try {
			while ((line = readLine(obj_in_stream)) != null){
				switch (line.charAt(0)) {
					case 35: //#
						//The line is a comment
						continue;
					case 111: //o
						//The line is a new object
						Log.d("In object.........", "Meshsize: " + meshes.size());
						if(meshes.size() > 0)
						{
							//First lets append the current vert/normal/uv data to
							//the current GLMesh (if there is one)
							OpenGLMesh cur_mesh = meshes.lastElement();
							//Vertices
							float[] vert_array = new float[final_verts.size()];
							for (int i = 0; i < final_verts.size(); i++)
							{
								vert_array[i] = final_verts.elementAt(i);
							}
							cur_mesh.setVertices(vert_array);
							//Normals
							float[] norm_array = new float[final_norms.size()];
							for (int i = 0; i < final_norms.size(); i++)
							{
								norm_array[i] = final_norms.elementAt(i);
							}
							cur_mesh.setNormals(norm_array);
							//Texture Coordinates
							float[] uv_array = new float[final_tex_coords.size()];
							for (int i = 0; i < final_tex_coords.size(); i++)
							{
								uv_array[i] = final_tex_coords.elementAt(i);
							}
							cur_mesh.setCoordinates(uv_array);
							//Indicies
							short[] indice_array = new short[final_indices.size()];
							for (int i = 0; i < final_indices.size(); i++)
							{
								indice_array[i] = final_indices.elementAt(i);
							}
							cur_mesh.setIndices(indice_array);
						}
						//First clear our storage vectors for the new object
						//raw_verts.clear();
						//raw_normals.clear();
						//raw_tex_coord.clear();
						final_verts.clear();
						final_indices.clear();
						final_norms.clear();
						final_tex_coords.clear();
						//Then add a new GLMesh to our list for building
						meshes.add(new OpenGLMesh());
						break;
					case 118: //v
						if (line.charAt(1) == 't'){
							//The line is a texture coordinate
							String [] line_parts = line.split("\\s+");
							float tmp_u = new Float(line_parts[1]);
							float tmp_v = new Float(line_parts[2]);
							raw_tex_coord.add(new OpenGLTextCoord(tmp_u, tmp_v));
						}
						else if (line.charAt(1) == 'n'){
							//The line is a normal
							String [] line_parts = line.split("\\s+");
							float tmp_x = new Float(line_parts[1]);
							float tmp_y = new Float(line_parts[2]);
							float tmp_z = new Float(line_parts[3]);
							OpenGLHelpVector tmp_vec = new OpenGLHelpVector(tmp_x, tmp_y, tmp_z);
							tmp_vec.normalizeVector();
							raw_normals.add(tmp_vec);
						}
						else{
							//The line is a new vertex
							String [] line_parts = line.split("\\s+");
							float tmp_x = new Float(line_parts[1]);
							float tmp_y = new Float(line_parts[2]);
							float tmp_z = new Float(line_parts[3]);
							raw_verts.add(new OpenGLHelpVector(tmp_x, tmp_y, tmp_z));
						}
						break;

					case 115: //s
						//The line toggles smooth shading
						break;

					case 102: //f
						//The line declares a face (where the magic happens :) )
						String [] line_parts = line.split("\\s+");
						//Declare and initialize our temporary arrays
						int[] vert_indicies = new int[line_parts.length - 1];
						int[] tex_indicies = new int[line_parts.length - 1];
						int[] norm_indicies = new int[line_parts.length - 1];
						for (int i = 0; i < vert_indicies.length; i++)
						{
							vert_indicies[i] = 0;
							tex_indicies[i] = 0;
							norm_indicies[i] = 0;
						}

						//Begin parsing the face line
						for (int i = 1; i < line_parts.length; i++)
						{
							if (line_parts[i].contains("//"))
							{
								//We are dealing with only verts and normals
								String [] sub_parts = line_parts[i].split("//");
								int tmp_vert_ind = new Integer(sub_parts[0]);
								int tmp_norm_ind = new Integer(sub_parts[1]);
								vert_indicies[i - 1] = tmp_vert_ind;
								norm_indicies[i - 1] = tmp_norm_ind;
							}
							else
							{
								//We have either verts, verts and texCoords, or verts and texCoords and norms
								String [] sub_parts = line_parts[i].split("/");
								if (sub_parts.length == 1)
								{
									//We are dealing with just verts
									int tmp_vert_ind = new Integer(sub_parts[0]);
									vert_indicies[i - 1] = tmp_vert_ind;
								}
								else if (sub_parts.length == 2)
								{
									//We are dealing with verts and texCoords
									int tmp_vert_ind = new Integer(sub_parts[0]);
									int tmp_texCoord_ind = new Integer(sub_parts[1]);
									vert_indicies[i - 1] = tmp_vert_ind;
									tex_indicies[i - 1] = tmp_texCoord_ind;
								}
								else
								{
									//We have all three parts
									int tmp_vert_ind = new Integer(sub_parts[0]);
									int tmp_texCoord_ind = new Integer(sub_parts[1]);
									int tmp_norm_ind = new Integer(sub_parts[2]);
									vert_indicies[i - 1] = tmp_vert_ind;
									tex_indicies[i - 1] = tmp_texCoord_ind;
									norm_indicies[i - 1] = tmp_norm_ind;
								}
							}
						}
						//Now that we have our indicies, we will add the faces to
						//the most recient GLMesh
						//Because a face listing may have more than 3 verts declared (not triangle shape)
						//we will attempt to automatically triangulate the face
						int second_vert = 1;
						while (vert_indicies.length - second_vert >= 2)
						{
							//Get our actual vertices
							OpenGLHelpVector vert1 = null;
							OpenGLHelpVector vert2 = null;
							OpenGLHelpVector vert3 = null;

							vert1 = raw_verts.elementAt(vert_indicies[0] - 1);
							vert2 = raw_verts.elementAt(vert_indicies[second_vert] - 1);
							vert3 = raw_verts.elementAt(vert_indicies[second_vert + 1] - 1);

							//Get our actual normals
							OpenGLHelpVector norm1 = null;
							OpenGLHelpVector norm2 = null;
							OpenGLHelpVector norm3 = null;
							if (norm_indicies[0] == 0)
							{
								//We have no normals from the file. We should
								//make our own
								norm1 = vert2.subOtherVectorCopy(vert1).crossProductCopy(vert3.subOtherVectorCopy(vert2));
								norm2 = vert2.subOtherVectorCopy(vert1).crossProductCopy(vert3.subOtherVectorCopy(vert2));
								norm3 = vert2.subOtherVectorCopy(vert1).crossProductCopy(vert3.subOtherVectorCopy(vert2));
							}
							else
							{
								norm1 = raw_normals.elementAt(norm_indicies[0] - 1);
								norm2 = raw_normals.elementAt(norm_indicies[second_vert] - 1);
								norm3 = raw_normals.elementAt(norm_indicies[second_vert + 1] - 1);
							}
							norm1.normalizeVector();
							norm2.normalizeVector();
							norm3.normalizeVector();

							//Get our actual texture coordinates
							OpenGLTextCoord uv1 = null;
							OpenGLTextCoord uv2 = null;
							OpenGLTextCoord uv3 = null;
							if (tex_indicies[0] == 0)
							{
								//Well, we have no texture coordinates, so lets just make them up!
								uv1 = new OpenGLTextCoord(0,0);
								uv2 = new OpenGLTextCoord(1,0);
								uv3 = new OpenGLTextCoord(1,1);
							}
							else
							{
								uv1 = raw_tex_coord.elementAt(tex_indicies[0] - 1);
								uv2 = raw_tex_coord.elementAt(tex_indicies[second_vert] - 1);
								uv3 = raw_tex_coord.elementAt(tex_indicies[second_vert + 1] - 1);
							}

							//Finally, we will add our verts, normals, and texture coordinates to the
							//running array to be used for the GLMesh
							//Vert1
							final_verts.add(vert1.getX());
							final_verts.add(vert1.getY());
							final_verts.add(vert1.getZ());
							final_norms.add(norm1.getX());
							final_norms.add(norm1.getY());
							final_norms.add(norm1.getZ());
							final_tex_coords.add(uv1.getU());
							final_tex_coords.add(uv1.getV());
							final_indices.add((short)((final_verts.size() / 3) - 1));
							//Vert2
							final_verts.add(vert2.getX());
							final_verts.add(vert2.getY());
							final_verts.add(vert2.getZ());
							final_norms.add(norm2.getX());
							final_norms.add(norm2.getY());
							final_norms.add(norm2.getZ());
							final_tex_coords.add(uv2.getU());
							final_tex_coords.add(uv2.getV());
							final_indices.add((short)((final_verts.size() / 3) - 1));
							//Vert3
							final_verts.add(vert3.getX());
							final_verts.add(vert3.getY());
							final_verts.add(vert3.getZ());
							final_norms.add(norm3.getX());
							final_norms.add(norm3.getY());
							final_norms.add(norm3.getZ());
							final_tex_coords.add(uv3.getU());
							final_tex_coords.add(uv3.getV());
							final_indices.add((short)((final_verts.size() / 3) - 1));
							second_vert += 1;
						}

						break;

					case 117: //u
						if(line.substring(0,6).equals("usemtl")){
							//The line is declaring what material the object uses
							OpenGLMesh cur_mesh = meshes.lastElement();
							OpenGLMaterial mesh_material = materials.get(line.substring(7,line.length()-1));
							cur_mesh.setColor(mesh_material.ambient_color, mesh_material.diffuse_color, mesh_material.specular_color, mesh_material.shininess);
							if (mesh_material.textureBitmap != null)
							{
								cur_mesh.loadBitmap(mesh_material.textureBitmap);
							}
						}
						break;

					case 109: //m
						String tmp_string = line.substring(0,6);
						if(tmp_string.equals("mtllib")){
							//The line is declaring a material file to load
							String mat_file_name = line.substring(7, line.length() - 5);
							int mat_file_id = myApp.getResources().getIdentifier(mat_file_name, "raw", "com.example.a3dtestapplication");
							InputStream mtl_in_stream = myApp.getResources().openRawResource(mat_file_id);
							loadMaterial(mtl_in_stream, myApp);
						}
						break;
				}

			}
			obj_in_stream.close();
			//First lets append the current vert/normal/uv data to
			//the current GLMesh (if there is one)
			OpenGLMesh cur_mesh = meshes.lastElement();
			//Vertices
			float[] vert_array = new float[final_verts.size()];
			for (int i = 0; i < final_verts.size(); i++)
			{
				vert_array[i] = final_verts.elementAt(i);
			}
			cur_mesh.setVertices(vert_array);
			//Normals
			float[] norm_array = new float[final_norms.size()];
			for (int i = 0; i < final_norms.size(); i++)
			{
				norm_array[i] = final_norms.elementAt(i);
			}
			cur_mesh.setNormals(norm_array);
			//Texture Coordinates
			float[] uv_array = new float[final_tex_coords.size()];
			for (int i = 0; i < final_tex_coords.size(); i++)
			{
				uv_array[i] = final_tex_coords.elementAt(i);
			}
			cur_mesh.setCoordinates(uv_array);
			//Indicies
			short[] indice_array = new short[final_indices.size()];
			for (int i = 0; i < final_indices.size(); i++)
			{
				indice_array[i] = final_indices.elementAt(i);
			}
			cur_mesh.setIndices(indice_array);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	 */



	public OpenGLObject(InputStream stream, Activity app){
		// Storage for parsing Object
		Log.d("Mesh", "Starting objekt");
		Vector<OpenGLHelpVector> rawVertices = new Vector<>();
		Vector<OpenGLHelpVector> rawNormals = new Vector<>();
		Vector<OpenGLTextCoord> rawTextCoordinates = new Vector<>();

		Vector<Float> finalVertices = new Vector<>();
		Vector<Float> finalNormals = new Vector<>();
		Vector<Float> finalTexCoordinates = new Vector<>();
		Vector<Short> finalIndices = new Vector<>();

		String line = null;
		try{
			while ((line = readLine(stream)) != null){
				switch (line.charAt(0)){
					case '#':
						Log.d(" Open Object", "Habe # gelesen");
						// Skipping comment line
						continue;
					case 'o':
						Log.d(" Open Object", "Habe o gelesen");
						// New object
						Log.d("In object..................", "Meshsize: " + meshes.size());
						if (meshes.size() > 0){
							OpenGLMesh currentMesh = meshes.lastElement();
							//vertices
							float[] verticesArray = new float[finalVertices.size()];
							for (int i = 0; i < finalVertices.size(); ++i)
								verticesArray[i] = finalVertices.elementAt(i);

							currentMesh.setVertices(verticesArray);
							// Normals
							float[] normalsArray = new float[finalNormals.size()];
							for (int i = 0; i < finalNormals.size(); ++i)
								normalsArray[i] = finalNormals.elementAt(i);

							currentMesh.setNormals(normalsArray);

							// Coordinates
							float[] textCoordinatesArray = new float[finalTexCoordinates.size()];
							for (int i = 0; i < finalTexCoordinates.size(); ++i)
								textCoordinatesArray[i] = finalTexCoordinates.elementAt(i);

							currentMesh.setCoordinates(textCoordinatesArray);

							short[] indicesArray = new short[finalIndices.size()];
							for (int  i = 0; i < finalIndices.size(); ++i)
								indicesArray[i] = finalIndices.elementAt(i);

							currentMesh.setIndices(indicesArray);
						}

						// Clear storage
						finalVertices.clear();
						finalIndices.clear();
						finalNormals.clear();
						finalTexCoordinates.clear();


						meshes.add(new OpenGLMesh());
						Log.d(" Open Object", "Habe new mesh hinzugefügt");
						break;
					case 'v':
						Log.d(" Open Object", "Habe v gelesen");
						if (line.charAt(1) == 't'){
							// Texture field
							String[] lineParts = line.split("\\s+");
							float tmpU = Float.parseFloat(lineParts[1]);
							float tmpV = Float.parseFloat(lineParts[2]);
							rawTextCoordinates.add(new OpenGLTextCoord(tmpU, tmpV));

						}else if (line.charAt(1) == 'n') {
							// Normals
							String[] lineParts = line.split("\\s+");
							float tmpX = Float.parseFloat(lineParts[1]);
							float tmpY = Float.parseFloat(lineParts[2]);
							float tmpZ = Float.parseFloat(lineParts[3]);
							OpenGLHelpVector vector = new OpenGLHelpVector(tmpX, tmpY, tmpZ);
							// Create direction vector
							vector.normalizeVector();
							rawNormals.add(vector);
						}else{
							// new vertex
							String[] lineParts = line.split("\\s+");
							float tmpX = Float.parseFloat(lineParts[1]);
							float tmpY = Float.parseFloat(lineParts[2]);
							float tmpZ = Float.parseFloat(lineParts[3]);
							rawVertices.add(new OpenGLHelpVector(tmpX, tmpY, tmpZ));
						}
						break;
					case 's':
						// smooth Shading
						Log.d(" Open Object", "Habe s gelesen");
						break;
					case 'f':
						// Face
						Log.d(" Open Object", "Habe f gelesen");
						String[] lineParts = line.split("\\s+");
						int[] vertexIndices = new int[lineParts.length-1];
						int[] textureIndices = new int[lineParts.length-1];
						int[] normalsIndices = new int[lineParts.length-1];
						// init array with 0
						for (int i = 0; i < vertexIndices.length-1; ++i){
							vertexIndices[i] = 0;
							textureIndices[i] = 0;
							normalsIndices[i] = 0;
						}
						for (int i = 1; i < lineParts.length; ++i){
							if (lineParts[i].contains("//")){
								String[] subParts = lineParts[i].split("//");
								int tempVertIndices = Integer.parseInt(subParts[0]);
								int tempNormIndices = Integer.parseInt(subParts[1]);
								vertexIndices[i-1] = tempVertIndices;
								normalsIndices[i-1] = tempNormIndices;
							}else{
								String[] subParts = lineParts[i].split("/");
								if (subParts.length == 1){
									// Just vertices
									// create new Integer.class because of the java reference
									int tempVertex = Integer.parseInt(subParts[0]);
									vertexIndices[i-1] = tempVertex;
								}
								else if(subParts.length == 2) {
									// text coordinate and vertices
									int tempVertex = Integer.parseInt(subParts[0]);
									int tempTexCoordinate = Integer.parseInt(subParts[1]);
									vertexIndices[i-1] = tempVertex;
									textureIndices[i-1] = tempTexCoordinate;
								}
								else{
									// normals, vertices and texture coordinates
									int tempVertex = Integer.parseInt(subParts[0]);
									int tempTexCoordinate = Integer.parseInt(subParts[1]);
									int tempNormals = Integer.parseInt(subParts[2]);
									vertexIndices[i-1] = tempVertex;
									textureIndices[i-1] = tempTexCoordinate;
									normalsIndices[i-1] = tempNormals;
								}
							}
						}


						int secondVertex = 1;
						while (vertexIndices.length - secondVertex >= 2){
							// get actual vertices
							OpenGLHelpVector vert1=null, vert2=null, vert3 = null;
							vert1 = rawVertices.elementAt(vertexIndices[0] - 1);
							vert2 = rawVertices.elementAt(vertexIndices[secondVertex] -1);
							vert3 = rawVertices.elementAt(vertexIndices[secondVertex + 1] - 1); // hier ändern!

							OpenGLHelpVector norm1=null, norm2=null, norm3 = null;
							// get actual normals
							if (normalsIndices[0] == 0){
								// no normals
								//create some
								norm1 = vert2.subOtherVectorCopy(vert1).crossProductCopy(vert3.subOtherVectorCopy(vert2));
								norm2 = vert2.subOtherVectorCopy(vert1).crossProductCopy(vert3.subOtherVectorCopy(vert2));
								norm3 = vert2.subOtherVectorCopy(vert1).crossProductCopy(vert3.subOtherVectorCopy(vert2));
							} else {
								norm1 = rawNormals.elementAt(normalsIndices[0] - 1);
								norm2 = rawNormals.elementAt(normalsIndices[secondVertex - 1] - 1);
								norm3 = rawNormals.elementAt(normalsIndices[secondVertex + 1] - 1);
							}
							//create direction vector
							norm1.normalizeVector();
							norm2.normalizeVector();
							norm3.normalizeVector();

							// get actual texture coordinates
							OpenGLTextCoord uv1, uv2, uv3 = null;
							if (textureIndices[0] == 0){
								// no indices
								// create some
								uv1 = new OpenGLTextCoord(0,0);
								uv2 = new OpenGLTextCoord(1,0);
								uv3 = new OpenGLTextCoord(1,1);
							}else {
								uv1 = rawTextCoordinates.elementAt(textureIndices[0] - 1);
								uv2 = rawTextCoordinates.elementAt(textureIndices[secondVertex - 1] - 1);
								uv3 = rawTextCoordinates.elementAt(textureIndices[secondVertex + 1] - 1);
							}
							// vertex 1
							finalVertices.add(vert1.getX());
							finalVertices.add(vert1.getY());
							finalVertices.add(vert1.getZ());
							finalNormals.add(norm1.getX());
							finalNormals.add(norm1.getY());
							finalNormals.add(norm1.getZ());
							finalTexCoordinates.add(uv1.getU());
							finalTexCoordinates.add(uv1.getV());
							finalIndices.add((short)((finalVertices.size() / 3) - 1));

							// Vertex 2
							finalVertices.add(vert2.getX());
							finalVertices.add(vert2.getY());
							finalVertices.add(vert2.getZ());
							finalNormals.add(norm2.getX());
							finalNormals.add(norm2.getY());
							finalNormals.add(norm2.getZ());
							finalTexCoordinates.add(uv2.getU());
							finalTexCoordinates.add(uv2.getV());
							finalIndices.add((short)(finalVertices.size()/3 -1));

							// Vertex 3
							finalVertices.add(vert3.getX());
							finalVertices.add(vert3.getY());
							finalVertices.add(vert3.getZ());
							finalNormals.add(norm3.getX());
							finalNormals.add(norm3.getY());
							finalNormals.add(norm3.getZ());
							finalTexCoordinates.add(uv3.getU());
							finalTexCoordinates.add(uv3.getV());
							finalIndices.add((short)(finalVertices.size()/3 -1));
							secondVertex += 1;
						}
						break;
					case 'm':
						String temp = line.substring(0,6);
						if(temp.equals("mtllib")){
							String matFileName = line.substring(7, line.length() - 5);
							int matFileId = app.getResources().getIdentifier(matFileName,"raw", "com.example.a3dtestapplication");
							InputStream matlInStream = app.getResources().openRawResource(matFileId);
							loadMaterial(matlInStream,app);

						}
						break;
				}

			}
			stream.close();
			// Now append data to current OpenGLMesh
			Log.d(" Open Object", "bin beim current angekommen");
			OpenGLMesh currentMesh = meshes.lastElement();
			// Vertices
			float[] verticesArray = new float[finalVertices.size()];
			for (int i = 0; i < finalVertices.size(); ++i){
				verticesArray[i] = finalVertices.elementAt(i);
			}
			Log.d("Mesh in objct", "Call: " + 2);
			currentMesh.setVertices(verticesArray);

			float[] normalsArray = new float[finalNormals.size()];
			for (int i = 0; i < finalNormals.size(); ++i) {
				normalsArray[i] = finalNormals.elementAt(i);
			}
			currentMesh.setNormals(normalsArray);

			float[] textureCoordinates = new float[finalTexCoordinates.size()];
			for (int i = 0; i < finalTexCoordinates.size(); i++) {
				textureCoordinates[i] = finalTexCoordinates.elementAt(i);
			}
			currentMesh.setCoordinates(textureCoordinates);

			short[] indices = new short[finalIndices.size()];
			for (int i = 0; i < finalIndices.size(); i++) {
				indices[i] = finalIndices.elementAt(i);
			}
			currentMesh.setIndices(indices);

		}catch (Exception e){
			e.printStackTrace();
			Log.println(Log.ERROR, "OpenGLObject", "In init fail!");
		}


	}
}
