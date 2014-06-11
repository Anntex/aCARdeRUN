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

import java.nio.ByteBuffer;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class VertexBuffer
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private int numberOfVertices;
    private VertexElement[] elements;
    private ByteBuffer buffer;

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the number of vertices in the buffer
     */
    public int getNumVertices()
    {
	    return numberOfVertices;
    }

    /**
     * @return the elements of the vertex buffer
     */
    public VertexElement[] getElements()
    {
	    return elements;
    }

    /**
     * @return the allocated byte buffer
     */
    public ByteBuffer getBuffer()
    {
	    return buffer;
    }

    /**
     * @param number set the maximum number of vertices for this buffer
     */
    public void setNumVertices(int number)
    {
	    this.numberOfVertices = number;
    }

    /**
     * @param elements set an array of elements to the vertex buffer object
     */
    public void setElements(VertexElement[] elements)
    {
	    this.elements = elements;
    }

    /**
     * @param buffer set the byte buffer
     */
    public void setBuffer(ByteBuffer buffer)
    {
	    this.buffer = buffer;
    } 
}