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
 * This interface describes some methods of the double dispatcher pattern () to define some methods
 * required to do some operations of the different 2D models of this framework.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public interface Shape2D
{

    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    public boolean intersects(Shape2D shape);
    public boolean intersects(Point point);
    public boolean intersects(Circle circle);
    public boolean intersects(AxisAlignedBoundingBox box);

    /**
     * @return the position of the bounding geometry.
     */
    public Vector2 getPosition();

    /**
     * The method sets the new position of the bounding geometry.
     *
     * @param position new Vector2 position
     */
    public void setPosition(Vector2 position);
}