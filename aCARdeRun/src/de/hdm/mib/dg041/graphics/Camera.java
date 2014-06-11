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
package de.hdm.mib.dg041.graphics;

import de.hdm.mib.dg041.math.Matrix4x4;
import de.hdm.mib.dg041.math.Vector3;
import de.hdm.mib.dg041.math.Vector4;

/**
 * This class defines a camera object to realize the projection of the different matrix
 * in the worlds.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Camera
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private Matrix4x4 projection;
    private Matrix4x4 view;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public Camera()
    {
        projection = new Matrix4x4();
        view = new Matrix4x4();
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the projection of the camera object
     */
    public Matrix4x4 getProjection()
    {
	    return projection;
    }

    /**
     * @return the view of the camera object
     */
    public Matrix4x4 getView()
    {
	    return view;
    }

    /**
     * @param projection
     *          set the projection for this camera.
     */
    public void setProjection(Matrix4x4 projection)
    {
	    this.projection = projection;
    }

    /**
     * @param view
     *          set the view for this camera.
     */
    public void setView(Matrix4x4 view)
    {
	    this.view = view;
    }

    /**
     * This method creates a new projection depending on a given vector3 instance and
     * the projection value. This happens depending on the diagonal.
     *
     * @param v
     * @param w
     * @return
     */
    public Vector3 project(Vector3 v, float w)
    {
        Matrix4x4 viewProjection = projection.multiply(view);
        Vector4 result = viewProjection.multiply(new Vector4(v, w));
	
	    // create a new result vector
        return new Vector3(
            result.getX() / result.getW(),
            result.getY() / result.getW(),
            result.getZ() / result.getW());
    }

    public Vector3 unproject(Vector3 v, float w)
    {
        Matrix4x4 viewProjection = projection.multiply(view);
        Matrix4x4 inverse = viewProjection.getInverse();
        Vector4 result = inverse.multiply(new Vector4(v, w));
	
	    return new Vector3(
		    result.getX() / result.getW(),
		    result.getY() / result.getW(),
		    result.getZ() / result.getW());
    }  
}