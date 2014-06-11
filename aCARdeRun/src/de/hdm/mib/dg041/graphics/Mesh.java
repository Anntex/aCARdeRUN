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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import de.hdm.mib.dg041.graphics.VertexElement.VertexSemantic;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Mesh
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private int mode;
    private VertexBuffer vertexBuffer;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public Mesh()
    {}

    public Mesh(VertexBuffer vertexBuffer, int mode)
    {
        this.vertexBuffer = vertexBuffer;
        this.mode = mode;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the mode of the mesh
     */
    public int getMode()
    {
	    return mode;
    }

    /**
     * @return the vertex buffer containing the loaded vertices
     */
    public VertexBuffer getVertexBuffer()
    {
		return vertexBuffer;
    }


    /**
     * This methods loads an 3D model from a given OBJ file using an input stream. The method
     * parses the content of the file and write the pared vertices into a buffer object.
     *
     * @param stream
     * @return The loaded mesh
     * @throws IOException if the file does not exists
     */
    public static Mesh loadFromOBJ(InputStream stream) throws IOException
    {
		Vector<float[]> positions = null;
		Vector<float[]> texCoords = null;
		Vector<short[]> indexGroups = null;
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		
		for (String line = reader.readLine(); line != null; line = reader.readLine())
        {
			// skip comments
			if (line.startsWith("#"))
            {
                continue;
            }

			// split lines in tokens
			StringTokenizer tokenizer = new StringTokenizer(line);
			int numTokens = tokenizer.countTokens();
			
			// check if line is empty
			if (numTokens < 1)
            {
                continue;
            }

			String command = tokenizer.nextToken();
			
			// parse position data
			if (command.equals("v"))
            {
				if (numTokens != 4)
                {
                    throw new IOException("Unsupported Line with v command. Only 3 coordinates (x y z) are supported!");
                }

				float[] position = new float[] {
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken())
				};
				
				if (positions == null)
                {
                    positions = new Vector<float[]>();
                }

				positions.add(position);				
			}
			
			// parse texture coordinates
			if (command.equals("vt"))
            {
				if (numTokens != 3)
                {
                    throw new IOException("Unsupported Line with vt command. Only 2 coordinates (u v) are supported!");
                }

				float[] texCoord = new float[] {
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken())
				};
				
				if (texCoords == null)
                {
                    texCoords = new Vector<float[]>();
                }
				
				texCoords.add(texCoord);
			}
			
			// Faces parsen
			if (command.equals("f"))
            {
				if (numTokens != 4)
                {
                    throw new IOException("Unsupported Line with f command. Only triangles are supported!");
                }

				for (int i = 0; i < 3; ++i)
                {
					short[] indexGroup = new short[3];
					
					String[] indices = tokenizer.nextToken().split("/");
					if (indices.length > 0)
                    {
                        indexGroup[0] = Short.parseShort(indices[0]);
                    }

					if (indices.length > 1)
                    {
                        indexGroup[1] = Short.parseShort(indices[1]);
                    }

					if (indices.length > 2)
                    {
                        indexGroup[2] = Short.parseShort(indices[2]);
                    }

					if (indexGroups == null)
                    {
                        indexGroups = new Vector<short[]>();
                    }

					indexGroups.add(indexGroup);
				}
			}
		}
		
		int numElements = 0;
		int vertexSize = 0;
		boolean hasPositionData = (positions != null);
		boolean hasTexCoordData = (texCoords != null);
		
		if (hasPositionData)
        {
			numElements++;
			vertexSize += 12;
		}
		if (hasTexCoordData)
        {
			numElements++;
			vertexSize += 8;
		}
		
		int elementIndex = 0;
		int elementOffset = 0;
		VertexElement[] elements = new VertexElement[numElements];
		if (hasPositionData) {
			elements[elementIndex] = new VertexElement(elementOffset, vertexSize, GL10.GL_FLOAT, 3, VertexSemantic.VERTEX_ELEMENT_POSITION);
			elementOffset += 12;
			elementIndex++;
		}
		if (hasTexCoordData)
        {
			elements[elementIndex] = new VertexElement(elementOffset, vertexSize, GL10.GL_FLOAT, 2, VertexSemantic.VERTEX_ELEMENT_TEXCOORD);
			elementOffset += 8;
			elementIndex++;
		}
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(vertexSize * indexGroups.size());
		buffer.order(ByteOrder.nativeOrder());
		
		for (short[] indexGroup : indexGroups)
        {
		    if (hasPositionData)
            {
		        short vertexIndex = indexGroup[0];
		        if (vertexIndex != 0)
                {
		            for (float f : positions.elementAt(vertexIndex - 1))
                    {
		                buffer.putFloat(f);
		            }
		        }
                else
                {
		            buffer.putFloat(0); buffer.putFloat(0); buffer.putFloat(0);
		        }
		    }
					
		    if (hasTexCoordData)
            {
		        short texCoordIndex = indexGroup[1];
		        if (texCoordIndex != 0)
                {
		            for (float f : texCoords.elementAt(texCoordIndex - 1))
                    {
		                buffer.putFloat(f);
		            }
		        }
                else
                {
		            buffer.putFloat(0); buffer.putFloat(0);
		        }
		    }
		}

		
		buffer.position(0);
		
		VertexBuffer vertexBuffer = new VertexBuffer();
		vertexBuffer.setElements(elements);
		vertexBuffer.setBuffer(buffer);
		vertexBuffer.setNumVertices(indexGroups.size());
		
		
		Mesh mesh = new Mesh();
		mesh.vertexBuffer = vertexBuffer;
		mesh.mode = GL10.GL_TRIANGLES;
		
		return mesh;
	}   
}
