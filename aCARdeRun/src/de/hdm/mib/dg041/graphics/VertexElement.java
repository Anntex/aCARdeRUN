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

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class VertexElement
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    /**
     * This enumeration defines the different possible states of a vertex element.
     *
     * @author dennis.grewe [dg041@hdm-stuttgart.de]
     * Created on 23.02.2012.
     */
    public enum VertexSemantic
    {
        VERTEX_ELEMENT_NONE,
        VERTEX_ELEMENT_POSITION,
        VERTEX_ELEMENT_COLOR,
        VERTEX_ELEMENT_TEXCOORD
    };

    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private int offset;
    private int stride;
    private int type;
    private int count;
    
    private VertexSemantic semantic;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public VertexElement(int offset, int stride, int type, int count, VertexSemantic semantic)
    {
        this.offset = offset;
        this.stride = stride;
        this.type = type;
        this.count = count;
        this.semantic = semantic;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the offset of the element
     */
    public int getOffset()
    {
	    return offset;
    }

    /**
     * @return the stride of the element
     */
    public int getStride()
    {
	    return stride;
    }

    /**
     * @return the type of the element
     */
    public int getType()
    {
	    return type;
    }

    /**
     * @return the number of elements
     */
    public int getCount()
    {
	    return count;
    }

    /**
     * @return the state of the element
     */
    public VertexSemantic getSemantic()
    {
	    return semantic;
    }
}