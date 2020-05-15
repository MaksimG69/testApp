package com.example.a3dtestapplication;

import org.junit.Test;

import static org.junit.Assert.*;
public class OpenGLHelpVectorTest {
	private int VECTOR_LENGTH = 3;
	@Test
	public void initTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(1,1,1);
		OpenGLHelpVector vector1 = new OpenGLHelpVector();
		for (int i = 0; i < VECTOR_LENGTH; ++i){
			assertEquals(1,vector.getElements()[i],0);
			assertEquals(0,vector1.getElements()[i],0);
		}
	}


	@Test
	public void testMul(){
		OpenGLHelpVector second = new OpenGLHelpVector(7,1,3);
		OpenGLHelpVector first = new OpenGLHelpVector(1,1,2);
		assertEquals(14, first.mulVectorWithOtherVector(second), 0.001);

	}

	@Test
	public void testScalarMul(){
		OpenGLHelpVector vector = new OpenGLHelpVector(3,4,1);
		float scalar = 2;
		float[] expected = {6,8,2};
		vector.mulVectorWithScalar(scalar);
		for (int i = 0; i < VECTOR_LENGTH; ++i){
			assertEquals(expected[i], vector.getElements()[i], 0.001);
		}
	}

	@Test
	public void getVectorLengthTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(3,4,1);
		assertEquals(Math.sqrt(26), vector.getVectorLength(), 0.001);


	}

	@Test
	public void getNewVectorAfterMulWithScalarTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(3,4,1);
		float scalar = 2;
		float[] expected = {6,8,2};
		OpenGLHelpVector vector1 = vector.mulVectorWithScalarCopy(scalar);
		for (int i = 0; i < VECTOR_LENGTH; ++i){
			assertEquals(expected[i], vector1.getElements()[i], 0);
			assertNotEquals(vector.getElements()[i], vector1.getElements()[i]);
		}
		assertNotEquals(vector, vector1);
	}

	@Test
	public void getOtherVectorAddTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(3,4,1);
		OpenGLHelpVector vector1 = new OpenGLHelpVector(5,7,4);
		float[] expected = {8,11,5};
		vector.addOtherVector(vector1);
		for (int i = 0; i < VECTOR_LENGTH; ++i) {
			assertEquals(expected[i], vector.getElements()[i], 0);
			assertNotEquals(vector.getElements()[i], vector1.getElements()[i]);
		}
	}

	@Test
	public void getOtherVectorCopyVectorTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(3,4,1);
		OpenGLHelpVector vector1 = new OpenGLHelpVector(5,7,4);
//		float[] expected = {8,11,5};
		OpenGLHelpVector vector2 = vector.addOtherVectorCopy(vector1);
		vector.addOtherVector(vector1);
		assertNotEquals(vector, vector2);
		assertNotEquals(vector, vector1);
		assertNotEquals(vector1, vector2);
		for (int i = 0; i < VECTOR_LENGTH; ++i) {
			assertEquals(vector.getElements()[i], vector2.getElements()[i], 0);
			assertNotEquals(vector.getElements()[i], vector1.getElements()[i]);
		}
	}

	@Test
	public void setElementsTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(1,2,3);
		float[] notExpect = {1,2,3};
		float[] expect = {2,3,4};

		vector.setElements(2,3,4);

		for (int i = 0; i < VECTOR_LENGTH; ++i) {
			assertEquals(expect[i], vector.getElements()[i], 0);
			assertNotEquals(notExpect[i], vector.getElements()[i]);
		}
	}

	@Test
	public void normalizeVectorTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(3,4,5);
		vector.normalizeVector();
		assertEquals(1, vector.getVectorLength(),0.00001f);

		float f = 0.0f;
		float[] expect = {0.424f, 0.566f, 0.707f};
		for (int i = 0; i < VECTOR_LENGTH; ++i){
			assertEquals(expect[i], vector.getElements()[i], 0.001f);
		}

	}


	@Test
	public void normalizeGetCopyVectorTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(3,4,5);
		OpenGLHelpVector vector1 = vector.normalizeVectorCopy();
		assertNotEquals(vector, vector1);
		assertEquals(1, vector1.getVectorLength(), 0);

	}

	@Test
	public void crossProductTest(){
		OpenGLHelpVector vector = new OpenGLHelpVector(1,3,2);
		OpenGLHelpVector vector1 = new OpenGLHelpVector(13,5,7);
		float[] expect = {11,19,-34};
		OpenGLHelpVector vector2 = vector.crossProductCopy(vector1);

		for (int i = 0; i < VECTOR_LENGTH; ++i) {
			assertEquals(expect[i], vector2.getElements()[i], 0);
			assertNotEquals(vector.getElements()[i],vector1.getElements()[i],0);
			assertNotEquals(vector.getElements()[i],vector2.getElements()[i],0);
			assertNotEquals(vector1.getElements()[i],vector2.getElements()[i],0);
		}

		assertNotEquals(vector, vector1);
		assertNotEquals(vector, vector2);
		assertNotEquals(vector1, vector2);
	}
}
