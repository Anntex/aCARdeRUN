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

import de.hdm.mib.dg041.math.Vector2;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Point implements Shape2D
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private Vector2 position;

    // --------------------------------------------------------
    // CONSTRUCTORS
    // --------------------------------------------------------

    /**
     * The default constructor initializes a new instance of Vector2
     */
    public Point()
    {
	    this.position = new Vector2();
    }

    /**
     * The constructor initializes a new POINT depending on the given two values.
     *
     * @param x x-axis value of the point.
     * @param y y-axis value of the point.
     */
    public Point(float x, float y)
    {
	    this.position = new Vector2(x, y);
    }

    /**
     * The constructor initializes a new POINT depending on the given vector.
     *
     * @param position the Vector2 instance to define a new point
     */
    public Point(Vector2 position)
    {
	    this.position = new Vector2(position.v[0], position.v[1]);
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------
    
    // *** IMPLEMENTATION OF THE DOUBLE DISPATCHER PATTERN *** //
    
    @Override
    public boolean intersects(Shape2D shape)
    {
        return shape.intersects(this);
    }

    @Override
    public boolean intersects(Point point)
    {
	    return 0.0000001 > Vector2.subtract(point.position, this.position).getLengthSqr();
    }
	
    @Override
    public boolean intersects(Circle circle)
    {
        float r = circle.getRadius();
        float distSqr = Vector2.subtract(circle.getCenter(), this.position).getLengthSqr();

        return distSqr <= r * r;
    }

    @Override
    public boolean intersects(AxisAlignedBoundingBox box)
    {
        if (this.position.getX() < box.getMin().getX() || this.position.getX() > box.getMax().getX()) return false;
        if (this.position.getY() < box.getMin().getY() || this.position.getY() > box.getMax().getY()) return false;

        return true;
    }

    @Override
    public Vector2 getPosition()
    {
	    return position;
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.position.v[0] = position.v[0];
        this.position.v[1] = position.v[1];
    }
}