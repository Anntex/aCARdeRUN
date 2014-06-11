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
public class Circle implements Shape2D
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private Vector2 center;
    private float radius;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    /**
     * Default constructor initializes a new Vector2 instance and a default radius value of 0.0f.
     */
    public Circle()
    {
        this.center = new Vector2();
        this.radius = 0.0f;
    }

    public Circle(Vector2 center, float radius)
    {
        this.center = new Vector2(center.v[0], center.v[1]);
        this.radius = radius;
    }

    public Circle(float x, float y, float radius)
    {
        this.center = new Vector2(x, y);
        this.radius = radius;
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
        float distSqr = Vector2.subtract(point.getPosition(), this.getCenter()).getLengthSqr();
        return distSqr <= radius * radius;
    }

    @Override
    public boolean intersects(Circle circle)
    {
        float distSqr = Vector2.subtract(circle.center, this.center).getLengthSqr();
        return distSqr <= (this.radius + circle.radius) * (this.radius + circle.radius);
    }

    @Override
    public boolean intersects(AxisAlignedBoundingBox box)
    {
        Vector2 min = box.getMin();
        Vector2 max = box.getMax();

        if (center.getX() >= min.getX() && center.getX() <= max.getX()) return true;
        if (center.getY() >= min.getY() && center.getY() <= max.getY()) return true;

        Vector2 nearestPosition = new Vector2(
                MathHelper.clamp(center.getX(), min.getX(), max.getX()),
                MathHelper.clamp(center.getY(), min.getY(), max.getY()));

        return nearestPosition.getLengthSqr() < radius * radius;
    }
	
    @Override
    public Vector2 getPosition()
    {
        return center;
    }

    @Override
    public void setPosition(Vector2 position)
    {
        this.center.v[0] = position.v[0];
        this.center.v[1] = position.v[1];
    }

    /**
     * @return a Vector2 instance which describes the center of the circle.
     */
    public Vector2 getCenter()
    {
        return center;
    }

    /**
     * The method sets the center of the circle.
     *
     * @param center new center Vector2
     */
    public void setCenter(Vector2 center)
    {
        this.center = center;
    }

    /**
     * @return the radius value of the circle
     */
    public float getRadius()
    {
        return radius;
    }

    /**
     * The method sets the radius of the circle.
     *
     * @param radius new radius value.
     */
    public void setRadius(float radius)
    {
        this.radius = radius;
    }
}