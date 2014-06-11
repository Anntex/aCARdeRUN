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

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Vector2
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    public float[] v = new float[2];

    // --------------------------------------------------------
    // CONSTRUCTORS
    // --------------------------------------------------------

    public Vector2()
    {
        v[0] = 0.0f;
        v[1] = 0.0f;
    }

    public Vector2(float x, float y)
    {
        v[0] = x;
        v[1] = y;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * This method defines a dot on the current vector
     *
     * @param v1
     * @param v2
     * @return
     */
    public static float dot(Vector2 v1, Vector2 v2)
    {
	    return v1.v[0] * v2.v[0] + v1.v[1] * v2.v[1];
    }

    /**
     * This method returns the vecrot value on the given index.
     *
     * @param index
     * @return
     */
    public float get(int index)
    {
	    return v[index];
    }

    /**
     * @return the x value of the vector
     */
    public float getX()
    {
	    return v[0];
    }

    /**
     * @return the y value of the vector
     */
    public float getY()
    {
	    return v[1];
    }

    /**
     * @return the length of the vector
     */
    public float getLength()
    {
	    return (float) Math.sqrt(
			v[0] * v[0] + v[1] * v[1]);
    }
	
    public float getLengthSqr()
    {
	    return (v[0] * v[0] + v[1] * v[1]);
    }

    /**
     * The method normalizes the current vector of this class.
     *
     * @return
     */
    public Vector2 normalize()
    {
        float l = getLength();
        for (int i = 0; i < 2; ++i)
        {
            v[i] /= l;
        }
        return this;
    }

    /**
     * This method normalize a given vector
     *
     * @param v
     * @return
     */
    public static Vector2 normalize(Vector2 v)
    {
        Vector2 result = new Vector2();
        float l = v.getLength();
        for (int i = 0; i < 2; ++i)
        {
            result.v[i] /= l;
        }
        return result;
    }
	

    // METHODS TO DETECT COLLISIONS etc.

    /**
     * This method sums two given vectors and returns the result.
     *
     * @param v1
     * @param v2
     * @return
     */
    public static Vector2 add(Vector2 v1, Vector2 v2)
    {
	    return new Vector2(v1.v[0] + v2.v[0], v1.v[1] + v2.v[1]);
    }

    /**
     * This method divides the vector depending on the given value.
     *
     * @param v
     * @param s
     * @return
     */
    public static Vector2 divide(Vector2 v, float s)
    {
	    return new Vector2(v.v[0] / s, v.v[1] / s);
    }

    /**
     * This method multiplies the current vector depending on the given value.
     *
     * @param v
     * @param s
     * @return
     */
    public static Vector2 multiply(Vector2 v, float s)
    {
	    return new Vector2(v.v[0] * s, v.v[1] * s);
    }

    public static Vector2 multiply(float s, Vector2 v)
    {
	    return new Vector2(s * v.v[0], s * v.v[1]);
    }

    public static Vector2 subtract(Vector2 v1, Vector2 v2)
    {
	    return new Vector2(v1.v[0] - v2.v[0], v1.v[1] - v2.v[1]);
    }

    /**
     * This method sets the new value to the position of the vector
     *
     * @param index
     * @param value
     */
    public void set(int index, float value)
    {
    	v[index] = value;
    }

    /**
     * @param x set the x value of the vector
     */
    public void setX(float x)
    {
	    v[0] = x;
    }

    /**
     * @param y set the y value of the vector
     */
    public void setY(float y)
    {
	    v[1] = y;
    }
}