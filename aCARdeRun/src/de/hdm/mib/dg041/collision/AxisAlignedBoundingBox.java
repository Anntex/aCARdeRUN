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
package de.hdm.mib.dg041.collision;

import de.hdm.mib.dg041.math.MathHelper;
import de.hdm.mib.dg041.math.Vector2;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class AxisAlignedBoundingBox implements Shape2D
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------
    
    private Vector2 min;
    private Vector2 max;

    // --------------------------------------------------------
    // CONSTRUCTORS
    // --------------------------------------------------------

    /**
     * The default constructor initializes two new Vector2 instances.
     */
    public AxisAlignedBoundingBox()
    {
        this.min = new Vector2();
        this.max = new Vector2();
    }

    /**
     * The constructor initializes two new Vector2 instances based on the given vectors.
     *
     * @param min minimum Vector2
     * @param max maximum Vector2
     */
    public AxisAlignedBoundingBox(Vector2 min, Vector2 max)
    {
        this.min = new Vector2(Math.min(min.v[0], max.v[0]), Math.min(min.v[1], max.v[1]));
        this.max = new Vector2(Math.max(min.v[0], max.v[0]), Math.max(min.v[1], max.v[1]));
    }

    /**
     * The cosntructor initializes two Vector2 instances based on a given vector and two direction values.
     *
     * @param position The given Vector2 instance
     * @param width first direction value
     * @param height second direction value
     */
    public AxisAlignedBoundingBox(Vector2 position, float width, float height)
    {
        this.min = new Vector2(position.v[0] - 0.5f * width, position.v[1] - 0.5f * height);
        this.max = new Vector2(position.v[0] + 0.5f * width, position.v[1] + 0.5f * height);
    }

    /** Konstruktor der aus vier uebergebenen Parameter zwei Vektoren f�r die AABB erstellt**/
    /**
     * The constructor initializes two Vector2 instances based on four parameters to create a
     * axis aligned bounding box
     *
     * @param x x-axis start value of the box
     * @param y y-axis start value of the box
     * @param width width of the box
     * @param height height of the box
     */
    public AxisAlignedBoundingBox(float x, float y, float width, float height)
    {
        this.min = new Vector2(x, y);
        this.max = new Vector2(x + width, y + height);
    }

    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------
    
    // *** IMPLEMENTATION OF THE DOUBLE DISPATCHER PATTERN ***//
    
    @Override
    public boolean intersects(Shape2D shape)
    {
        return shape.intersects(this);
    }

    @Override
    public boolean intersects(Point point)
    {
        Vector2 position = point.getPosition();
        if (position.getX() < min.getX() || position.getX() > max.getX()) return false;
        if (position.getY() < min.getY() || position.getY() > max.getY()) return false;

        return true;
    }

    @Override
    public boolean intersects(Circle circle)
    {
        Vector2 center = circle.getCenter();

        if (center.getX() >= min.getX() && center.getX() <= max.getX()) return true;
        if (center.getY() >= min.getY() && center.getY() <= max.getY()) return true;

        Vector2 nearestPosition = new Vector2(
                MathHelper.clamp(center.getX(), min.getX(), max.getX()),
                MathHelper.clamp(center.getY(), min.getY(), max.getY()));

        float radius = circle.getRadius();

        return nearestPosition.getLengthSqr() < radius * radius;
    }

    @Override
    public boolean intersects(AxisAlignedBoundingBox box)
    {
        if (this.min.getX() >= box.getMax().getX() || this.max.getX() <= box.getMin().getX()) return false;
        if (this.min.getY() >= box.getMax().getY() || this.max.getY() <= box.getMin().getY()) return false;

        return true;
    }

    @Override
    public Vector2 getPosition()
    {
        return new Vector2(
            0.5f * (this.min.v[0] + this.max.v[0]),
            0.5f * (this.min.v[1] + this.max.v[1]));
    }

    @Override
    public void setPosition(Vector2 position)
    {
        Vector2 size = getSize();
        this.min.v[0] = position.v[0] - 0.5f * size.v[0];
        this.min.v[1] = position.v[1] - 0.5f * size.v[1];
        this.max.v[0] = position.v[0] + 0.5f * size.v[0];
        this.max.v[1] = position.v[1] + 0.5f * size.v[1];
    }


    /**
     * @return the minimum vector of the axis aligned bounding box
     */
    public Vector2 getMin()
    {
	    return min;
    }

    /**
     * Sets the minimum Vector2 of the axis aligned bounding box.
     *
     * @param min Vector2 to set
     */
    public void setMin(Vector2 min)
    {
        this.min.v[0] = Math.min(min.v[0], this.max.v[0]);
        this.min.v[1] = Math.min(min.v[1], this.max.v[1]);
        this.max.v[0] = Math.max(min.v[0], this.max.v[0]);
        this.max.v[1] = Math.max(min.v[1], this.max.v[1]);
    }

    /**
     * @return the maximum vector of the axis aligned bounding box
     */
    public Vector2 getMax()
    {
        return max;
    }

    /**
     * Sets the maximum Vector2 of the axis aligned bounding box.
     *
     * @param max Vector2 to set
     */
    public void setMax(Vector2 max)
    {
        this.max.v[0] = Math.max(max.v[0], this.min.v[0]);
        this.max.v[1] = Math.max(max.v[1], this.min.v[1]);
        this.min.v[0] = Math.min(max.v[0], this.min.v[0]);
        this.min.v[1] = Math.min(max.v[1], this.min.v[1]);
    }

    /**
     * The method returns the size of the bounding box by subtracting the two
     * vectors from each others.
     *
     * @return size of the bounding box.
     */
    public Vector2 getSize()
    {
        return Vector2.subtract(this.max, this.min);
    }

    /**
     * The method sets the size of the bounding box depending on the given vector.
     *
     * @param size
     */
    public void setSize(Vector2 size)
    {
        Vector2 position = getPosition();
        this.min.v[0] = position.v[0] - 0.5f * size.v[0];
        this.min.v[1] = position.v[1] - 0.5f * size.v[1];
        this.max.v[0] = position.v[0] + 0.5f * size.v[0];
        this.max.v[1] = position.v[1] + 0.5f * size.v[1];
    }
}
