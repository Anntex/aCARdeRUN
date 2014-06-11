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

/**
 * This class defines an own implemented renderer inspired on a classic openGL ES or  android renderer.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Renderer
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private GraphicDevice graphicsDevice;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public Renderer(GraphicDevice graphicsDevice)
    {
	    this.graphicsDevice = graphicsDevice;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the graphics devices
     */
    public GraphicDevice getGraphicsDevice()
    {
	    return graphicsDevice;
    }

    /**
     * This method draws a loaded mesh and its corresponding material into a matrix (for example the world).
     *
     * @param mesh
     * @param material
     * @param world
     */
    public void drawMesh(Mesh mesh, Material material, Matrix4x4 world)
    {
        graphicsDevice.setWorldMatrix(world);
        setupMaterial(material);

        VertexBuffer vertexBuffer = mesh.getVertexBuffer();
        graphicsDevice.bindVertexBuffer(vertexBuffer);
        graphicsDevice.draw(mesh.getMode(), 0, vertexBuffer.getNumVertices());
        graphicsDevice.unbindVertexBuffer(vertexBuffer);
    }

    /**
     * This method draws a loaded mesh without its material in a matrix (for example the world)
     *
     * @param mesh
     * @param world
     */
    public void drawMesh(Mesh mesh, Matrix4x4 world)
    {
        graphicsDevice.setWorldMatrix(world);

        VertexBuffer vertexBuffer = mesh.getVertexBuffer();
        graphicsDevice.bindVertexBuffer(vertexBuffer);
        graphicsDevice.draw(mesh.getMode(), 0, vertexBuffer.getNumVertices());
        graphicsDevice.unbindVertexBuffer(vertexBuffer);
    }

    /**
     * This method draws a certain text into a matrix.
     *
     * @param textBuffer
     * @param world
     */
    public void drawText(TextBuffer textBuffer, Matrix4x4 world)
    {
        drawMesh(textBuffer.getMesh(), textBuffer.getSpriteFont().getMaterial(), world);
    }


    /**
     * This method sets up the properties of a material for a mesh.
     *
     * @param material
     */
    private void setupMaterial(Material material)
    {
        // setup text
        graphicsDevice.bindTexture(material.getTexture());
        graphicsDevice.setTextureFilters(material.getTextureFilterMin(), material.getTextureFilterMag());
        graphicsDevice.setTextureWrapMode(material.getTextureWrapModeU(), material.getTextureWrapModeV());
        graphicsDevice.setTextureBlendMode(material.getTextureBlendMode());
        graphicsDevice.setTextureBlendColor(material.getTextureBlendColor());

        // setup material properties
        graphicsDevice.setMaterialColor(material.getMaterialColor());
        graphicsDevice.setBlendFactors(material.getBlendSourceFactor(), material.getBlendDestFactor());

        // setup display properties like culling, depth, alpha, ...
        graphicsDevice.setCullSide(material.getCullSide());
        graphicsDevice.setDepthTest(material.getDepthTestFunction());
        graphicsDevice.setDepthWrite(material.getDepthWrite());
        graphicsDevice.setAlphaTest(material.getAlphaTestFunction(), material.getAlphaTestValue());
    }
}