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
import java.nio.ByteOrder;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import de.hdm.mib.dg041.graphics.SpriteFont.CharacterInfo;
import de.hdm.mib.dg041.graphics.VertexElement.VertexSemantic;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class TextBuffer
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private SpriteFont spriteFont;
    private Mesh mesh;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    TextBuffer(GraphicDevice graphicsDevice, SpriteFont spriteFont, int capacity)
    {
	    VertexElement[] elements = new VertexElement[] {
           new VertexElement(0, 16, GL10.GL_FLOAT, 2, VertexSemantic.VERTEX_ELEMENT_POSITION),
           new VertexElement(8, 16, GL10.GL_FLOAT, 2, VertexSemantic.VERTEX_ELEMENT_TEXCOORD)
	    };

        // allocate byte buffer
        ByteBuffer data = ByteBuffer.allocateDirect(6 * 16 * capacity);
        data.order(ByteOrder.nativeOrder());

        // create a new buffer and fill up with elements
        VertexBuffer vertexBuffer = new VertexBuffer();
        vertexBuffer.setElements(elements);
        vertexBuffer.setBuffer(data);
        vertexBuffer.setNumVertices(0);

        this.spriteFont = spriteFont;
        this.mesh = new Mesh(vertexBuffer, GL10.GL_TRIANGLES);
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the sprite font of the text buffer
     */
    public SpriteFont getSpriteFont()
    {
	    return spriteFont;
    }

    /*
     * @return the mesh
     */
    public Mesh getMesh()
    {
	    return mesh;
    }

    /**
     * @param text set the text to the text buffer
     */
    public void setText(String text)
    {
        Map<Character, SpriteFont.CharacterInfo> characterInfos = spriteFont.getCharacterInfos();
        Texture texture = spriteFont.getMaterial().getTexture();
        ByteBuffer data = mesh.getVertexBuffer().getBuffer();

        data.position(0);

        float x = 0;
        float y = 0;
        for (int index = 0; index < text.length(); ++index)
        {
            char c = text.charAt(index);

            CharacterInfo info = characterInfos.get(c);

            float posLeft = 	x + info.offset.x;
            float posRight = 	x + info.offset.x + info.area.width();
            float posTop = 		y - info.offset.y;
            float posBottom = 	y - (info.offset.y + info.area.height());
            float texLeft = 	(float) info.area.left / (float) texture.getWidth();
            float texRight = 	(float) info.area.right / (float) texture.getWidth();
            float texTop = 		1.0f - (float) info.area.top 	/ (float) texture.getHeight();
            float texBottom = 	1.0f - (float) info.area.bottom / (float) texture.getHeight();

            // triangle 1
            data.putFloat(posLeft); 	data.putFloat(posTop); 		data.putFloat(texLeft); 	data.putFloat(texTop);
            data.putFloat(posLeft); 	data.putFloat(posBottom); 	data.putFloat(texLeft); 	data.putFloat(texBottom);
            data.putFloat(posRight); 	data.putFloat(posTop); 		data.putFloat(texRight); 	data.putFloat(texTop);

            // triangle 2
            data.putFloat(posRight); 	data.putFloat(posTop); 		data.putFloat(texRight); 	data.putFloat(texTop);
            data.putFloat(posLeft); 	data.putFloat(posBottom); 	data.putFloat(texLeft); 	data.putFloat(texBottom);
            data.putFloat(posRight); 	data.putFloat(posBottom); 	data.putFloat(texRight); 	data.putFloat(texBottom);

            x += info.width;
        }

        data.position(0);
        mesh.getVertexBuffer().setNumVertices(6 * text.length());
    }
}