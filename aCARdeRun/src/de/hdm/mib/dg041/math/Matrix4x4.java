/* Copyright 2012 Dennis Grewe
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. */
package de.hdm.mib.dg041.math;

import android.opengl.Matrix;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Matrix4x4
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    public float[] m = new float [16];

    // --------------------------------------------------------
    // CONSTRUCTORS
    // --------------------------------------------------------

    public Matrix4x4()
    {
	    setIdentity();
    }

    public Matrix4x4(float[] m) throws ArrayIndexOutOfBoundsException
    {
	    // check if array size is less than 16 -> throw exception
		if (m.length < 16)
        {
		    throw new ArrayIndexOutOfBoundsException();
		}

	    // pass array values to the class variable
		for (int i = 0; i<16; i++)
        {
		    this.m[i] = m[i];
		}
    }

    public Matrix4x4(Matrix4x4 m)
    {
        // pass the matrix values to the array class variable
        for(int i = 0; i < 16; i++) {
            this.m[i] = m.m[i];
        }
    }

    public Matrix4x4(
		float m00, float m10, float m20, float m30,
		float m01, float m11, float m21, float m31,
		float m02, float m12, float m22, float m32,
		float m03, float m13, float m23, float m33)
    {
        m[0] = m00; m[4] = m10; m[ 8] = m20; m[12] = m30;
        m[1] = m01; m[5] = m11; m[ 9] = m21; m[13] = m31;
        m[2] = m02; m[6] = m12; m[10] = m22; m[14] = m32;
        m[3] = m03; m[7] = m13; m[11] = m23; m[15] = m33;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------
    
	// ****************** MATRIX ROTATION ****************** //

    /**
     * This method calculates a matrix rotation depending on the given angle value.
     *
     * @param angle
     *          the angle value to rotate the matrix.
     * @return A Matrix4x4 containing the calculated values of the rotation.
     */
    public static Matrix4x4 createRotationX(float angle)
    {
        Matrix4x4 mat = new Matrix4x4();
        return mat.rotateX(angle);
    }

    /**
     * This method calculates the rotation on the y-axis of the matrix
     *
     * @param angle
     *          the angle value to rotate the matrix.
     * @return A new calculated Matrix4x4
     */
    public static Matrix4x4 createRotationY(float angle)
    {
        Matrix4x4 mat = new Matrix4x4();
        return mat.rotateY(angle);
    }

    /**
     * This method calculates the rotation on the z-axis of the matrix
     *
     * @param angle
     *          the angle value to rotate the matrix.
     * @return A new calculated Matrix4x4
     */
    public static Matrix4x4 createRotationZ(float angle)
    {
        Matrix4x4 mat = new Matrix4x4();
        return mat.rotateZ(angle);
    }

    // ****************** MATRIX SCALING ****************** //

    /**
     * This method scales the matrix depending on the given scaling value.
     *
     * @param s
     *          the scaling factor
     * @return A new calculated Matrix4x4
     */
    public static Matrix4x4 createScale(float s)
    {
        Matrix4x4 mat = new Matrix4x4();
        return mat.scale(s);
    }

    /**
     * This method scales an object depending on the given axis.
     *
     * @param x
     *          x-axis value
     * @param y
     *          y-axis value
     * @param z
     *          z-axis value
     * @return A new calculated Matrix4x4
     */
    public static Matrix4x4 createScale(float x, float y, float z)
    {
        Matrix4x4 mat = new Matrix4x4();
        return mat.scale(x, y, z);
    }

    /**
     * This method translate the matrix depending on the given axis values.
     *
     * @param x
     *          x-axis value
     * @param y
     *          y-axis value
     * @param z
     *          z-axis value
     * @return A new calculated Matrix4x4
     */
    public static Matrix4x4 createTranslation(float x, float y, float z )
    {
        Matrix4x4 mat = new Matrix4x4();
        return mat.translate(x, y, z);
    }

    // ****************** MATRIX MANIPULATION ****************** //

    /**
     * The method multiplies two given matrices
     *
     * @param m1
     * @param m2
     * @return the calulated result as Matrix4x4
     */
    public static Matrix4x4 multiply(Matrix4x4 m1, Matrix4x4 m2)
    {
        Matrix4x4 result = new Matrix4x4();
        Matrix.multiplyMM(result.m, 0, m1.m, 0, m2.m, 0);
        return result;
    }

    /**
     * This method multiplies a given matrix and a vector.
     *
     * @param m the matrix
     * @param v the vector
     * @return the calculated result as Matrix4x4
     */
    public static Vector4 multiply(Matrix4x4 m, Vector4 v)
    {
        Vector4 result = new Vector4();
        Matrix.multiplyMV(result.v, 0, m.m, 0, v.v, 0);
        return result;
    }

    public Matrix4x4 getTranspose()
    {
        Matrix4x4 result = new Matrix4x4();
        Matrix.transposeM(result.m, 0, m, 0);
        return result;
    }

    /**
     * @return the inverse matrix
     */
    public Matrix4x4 getInverse()
    {
        Matrix4x4 result = new Matrix4x4();
        Matrix.invertM(result.m, 0, m, 0);
        return result;
    }

    /**
     * This method multiplies the current matrix depending on the given matrix
     *
     * @param matrix
     * @return A new calculated Matrix4x4
     */
    public Matrix4x4 multiply(Matrix4x4 matrix)
    {
        Matrix4x4 result = new Matrix4x4();
        Matrix.multiplyMM(result.m, 0, m, 0, matrix.m, 0);
        return result;
    }

    /**
     * This method multiplies the current matrix and the given vector
     *
     * @param vector
     * @return A new calculated Matrix4x4
     */
    public Vector4 multiply(Vector4 vector)
    {
        Vector4 result = new Vector4();
        Matrix.multiplyMV(result.v, 0, m, 0, vector.v, 0);
        return result;
    }

    // ****************** CALCULATE MATRIX ROTATION ****************** //

    /**
     * This method calculates a rotation of the current matrix depending on the given angle value
     * and the exis.
     *
     * @param angle
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Matrix4x4 rotate(float angle, float x, float y, float z)
    {
        Matrix.rotateM(m, 0, angle, x, y, z);
        return this;
    }

    /**
     * This method calculates a rotation of the current matrix and the given x-axis value.
     *
     * @param angle
     * @return
     */
    public Matrix4x4 rotateX(float angle)
    {
        Matrix.rotateM(m, 0, angle, 1, 0, 0);
        return this;
    }

    /**
     * This method calculates a rotation of the current matrix and the given y-axis value.
     *
     * @param angle
     * @return
     */
    public Matrix4x4 rotateY(float angle)
    {
        Matrix.rotateM(m, 0, angle, 0, 1, 0);
        return this;
    }

    /**
     * This method calculates a rotation of the current matrix and the given z-axis value.
     *
     * @param angle
     * @return
     */
    public Matrix4x4 rotateZ(float angle)
    {
        Matrix.rotateM(m, 0, angle, 0, 0, 1);
        return this;
    }

    /**
     * This method calculates a scalability of the current matrix and the given scale value.
     *
     * @param s
     * @return
     */
    public Matrix4x4 scale(float s)
    {
        Matrix.scaleM(m, 0, s, s, s);
        return this;
    }

    /**
     * This method calculates a scalability of the current matrix and the given axis value.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Matrix4x4 scale(float x, float y, float z)
    {
        Matrix.scaleM(m, 0, x, y, z);
        return this;
    }

    /**
     * This method translates the current matrix and the given axis values.
     *
     * @param x
     * @param y
     * @param z
     * @return
     */
    public Matrix4x4 translate(float x, float y, float z)
    {
        Matrix.translateM(m, 0, x, y, z);
        return this;
    }

    // ****************** OTHER MATRIX METHODS ****************** //

    /**
     * @return the manipulated Matrix4x4 depending on the default values of the Matrix4x4 class.
     */
    public Matrix4x4 setIdentity()
    {
        Matrix.setIdentityM(m, 0);
        return this;
    }

    /**
     * This method calculates the orthogonal projection of the current matrix depending on the given parameters.
     *
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     * @return
     */
    public Matrix4x4 setOrthogonalProjection(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix.orthoM(m, 0, left, right, bottom, top, near, far);
        return this;
    }

    /**
     * This method calculates the perspective projection of the current matrix depending on the given parameters
     *
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near
     * @param far
     * @return
     */
    public Matrix4x4 setPerspectiveProjection(float left, float right, float bottom, float top, float near, float far)
    {
        Matrix.frustumM(m, 0, left, right, bottom, top, near, far);
        return this;
    }   
}