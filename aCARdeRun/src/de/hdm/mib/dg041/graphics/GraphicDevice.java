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

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.InvalidParameterException;

import javax.microedition.khronos.opengles.GL10;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.opengl.GLUtils;
import android.util.Log;

import de.hdm.mib.dg041.math.Matrix4x4;

/**
 * This class defines a graphical device depending on the openGL ES 1.0 framework.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class GraphicDevice
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private GL10 gl;
    private static String TAG = GraphicDevice.class.getName();

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * This method creates a new GraphicDevice based on the openGL ES 1.0 framework.
     * @param gl
     */
    public void onSurfaceCreated(GL10 gl)
    {
        this.gl = gl;
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
    }


    /**
     * This method draws a color depending on the given RGB values and clears the
     * screen.
     *
     * @param red
     * @param green
     * @param blue
     */
    public void clear(float red, float green, float blue)
    {
        gl.glClearColor(red, green, blue, 1.0f);
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }

    /**
     * This method draws a color depending on the given RGB values and an alpha value and
     * clears the screen.
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void clear(float red, float green, float blue, float alpha)
    {
        gl.glClearColor(red, green, blue, alpha);
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
    }
    
    /** Methode zum Zeichnen von RGB + Alpha + Tiefe**/
    /**
     * This method draws a color depending on the given RGB values, an alpha value, a value of depgth
     * and clears the screen.
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha
     * @param depth
     */
    public void clear(float red, float green, float blue, float alpha, float depth)
    {
	    gl.glClearColor(red, green, blue, alpha);
	    gl.glClearDepthf(depth);
	    gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    public void draw(int mode, int first, int count)
    {
	    gl.glDrawArrays(mode, first, count);
    }

    /**
     * This method draws a viewport on the screen using the given width and height values.
     * @param width
     * @param height
     */
    public void resize(int width, int height)
    {
	    gl.glViewport(0, 0, width, height);
    }

    /**
     * This method sets a camera object to the view port and defines the type of the projection.
     * @param camera
     */
    public void setCamera(Camera camera)
    {
        Matrix4x4 viewCamera = Matrix4x4.multiply(camera.getProjection(), camera.getView());

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadMatrixf(viewCamera.m, 0);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    /**
     * This method supports the definition of a world as a 4x4 matrix.
     *
     * @param world
     */
    public void setWorldMatrix(Matrix4x4 world)
    {
	    gl.glLoadMatrixf(world.m, 0);
    }

    // extended methods to use the vertex buffer to display vertices

    /**
     * This method collects the vertex declaraion and iterates over all collected vertex elements.
     *
     * @param vertexBuffer
     */
    public void bindVertexBuffer(VertexBuffer vertexBuffer)
    {
	    ByteBuffer buffer = vertexBuffer.getBuffer();
	
        for (VertexElement element : vertexBuffer.getElements())
        {
            int offset = element.getOffset();
            int stride = element.getStride();
            int type = element.getType();
            int count = element.getCount();

            buffer.position(offset);

            switch (element.getSemantic())
            {
                case VERTEX_ELEMENT_POSITION:
                    gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
                    gl.glVertexPointer(count, type, stride, buffer);
                    break;

                case VERTEX_ELEMENT_COLOR:
                    gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
                    gl.glColorPointer(count, type, stride, buffer);
                    break;

                case VERTEX_ELEMENT_TEXCOORD:
                    gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                    gl.glTexCoordPointer(count, type, stride, buffer);
                    break;
            }
        }
    }

    public void unbindVertexBuffer(VertexBuffer vertexBuffer)
    {
        for (VertexElement element : vertexBuffer.getElements())
        {
            switch (element.getSemantic())
            {
                case VERTEX_ELEMENT_POSITION:
                    gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
                    break;

                case VERTEX_ELEMENT_COLOR:
                    gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
                    break;

                case VERTEX_ELEMENT_TEXCOORD:
                    gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
                    break;
            }
        }
    }

    // extended methods to support textures

    /**
     * This method loads a texture from a bitmap (png / jpeg) via input stream.
     *
     * @param stream
     * @return
     */
    public Texture createTexture(InputStream stream)
    {
        Bitmap bitMap = BitmapFactory.decodeStream(stream);
        if (bitMap == null)
        {
            return null;
        }
	
    	return createTexture(bitMap);
    }

    /**
     * This method creates a new texture buffer
     *
     * @param spriteFont
     * @param capacity
     * @return
     */
    public TextBuffer createTextBuffer(SpriteFont spriteFont, int capacity)
    {
	    return new TextBuffer(this, spriteFont, capacity);
    } 

    /**
     * This method creates a texture directly from a given bitmap object.
     *
     * @param bitmap
     * @return
     */
    public Texture createTexture(Bitmap bitmap)
    {
        int level = 0;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] handles = new int[1];
        gl.glGenTextures(1, handles, 0);

        // bind texture
        int handle = handles[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, handle);

        Texture texture = new Texture(handle, width, height);

        // mirror bitmap on y-axis
        Matrix matrix = new Matrix();
        matrix.setScale(1, -1);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

        // if nacessary create mipmaps from the texture and load it
        while (width >= 1 && height >= 1)
        {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);

            if(height == 1 || width == 1)
            {
                break;
            }

            level++;
            height /= 2;
            width /= 2;

            bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
        }

        return texture;
    }

    /**
     * This method binds a texture to an object in the draw methods.
     * @param texture
     */
    public void bindTexture(Texture texture)
    {
        try
        {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getHandle());
            gl.glEnable(GL10.GL_TEXTURE_2D);
        }
        catch (Exception e)
        {
            Log.e(TAG, "ERROR - Binding a texture failed!");
        }
    }

    /**
     * This method unbinds a texture from an object. This must be performed in the draw-method.
     */
    public void unbindTexture()
    {
        try
        {
            gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
            gl.glDisable(GL10.GL_TEXTURE_2D);
        }
        catch (Exception e)
        {
            Log.e(TAG, "ERROR - Unbinding a texture failed!");
        }
	
    }

    // extended methods to use meterials

    public void setAlphaTest(CompareFunction function, float value)
    {
        if (function == CompareFunction.ALWAYS)
        {
            gl.glDisable(GL10.GL_ALPHA_TEST);
        }
        else
        {
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glAlphaFunc(getGLConstant(function), value);
        }
    }

    /**
     * Method to set the blend factors
     * @param srcFactor
     * @param dstFactor
     */
    public void setBlendFactors(BlendFactor srcFactor, BlendFactor dstFactor)
    {
        if (srcFactor == BlendFactor.ONE && dstFactor == BlendFactor.ZERO)
        {
            gl.glDisable(GL10.GL_BLEND);
        }
        else
        {
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(getGLConstant(srcFactor), getGLConstant(dstFactor));
        }
    }

    /**
     * This method sets the side of the material which will be not displayed.
     * @param side
     */
    public void setCullSide(Side side)
    {
        if (side == Side.NONE)
        {
            gl.glDisable(GL10.GL_CULL_FACE);
        }
        else
        {
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glCullFace(getGLConstant(side));
        }
    }

    public void setDepthTest(CompareFunction function)
    {
        if (function == CompareFunction.ALWAYS)
        {
            gl.glDisable(GL10.GL_DEPTH_TEST);
        }
        else
        {
            gl.glEnable(GL10.GL_DEPTH_TEST);
            gl.glDepthFunc(getGLConstant(function));
        }
    }

    /**
     * This method enables/disables the depth value.
     *
     * @param enabled
     */
    public void setDepthWrite(boolean enabled)
    {
	    gl.glDepthMask(enabled);
    }

    public void setMaterialColor(float[] color)
    {
	    setMaterialColor(color[0], color[1], color[2], color[3]);
    }

    /**
     * This method sets the color to a certain material.
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void setMaterialColor(float red, float green, float blue, float alpha)
    {
	    gl.glColor4f(red, green, blue, alpha);
    }

    /**
     * This method sets a certain alpha value to a texture.
     *
     * @param color
     */
    public void setTextureBlendColor(float[] color)
    {
	    gl.glTexEnvfv(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_COLOR, color, 0);
    }

    /**
     * This method sets a certain texture an alpha value.
     *
     * @param red
     * @param green
     * @param blue
     * @param alpha
     */
    public void setTextureBlendColor(float red, float green, float blue, float alpha)
    {
	    setTextureBlendColor(new float[] {red, green, blue, alpha});
    }

    /**
     * This metod sets the type of the blend mode
     *
     * @param blendMode
     */
    public void setTextureBlendMode(TextureBlendMode blendMode)
    {
	    gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, getGLConstant(blendMode));
    }

    /**
     * This method sets the filter for the texture.
     *
     * @param filterMin
     * @param filterMag
     */
    public void setTextureFilters(TextureFilter filterMin, TextureFilter filterMag)
    {
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, getGLConstant(filterMin));
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, getGLConstant(filterMag));
    }

    /**
     * This method sets the wrap mode of the texture
     *
     * @param wrapU
     * @param wrapV
     */
    public void setTextureWrapMode(TextureWrapMode wrapU, TextureWrapMode wrapV)
    {
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, getGLConstant(wrapU));
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, getGLConstant(wrapV));
    }

        // extended methods to display text on screen

    /**
     * This method creates a new sprite font which can be display on screen.
     *
     * @param typeface
     * @param size
     * @return
     */
    public SpriteFont createSpriteFont(Typeface typeface, float size)
    {
	    return new SpriteFont(this, typeface, size);
    }
    
    private static int getGLConstant(BlendFactor factor)
    {
        switch (factor)
        {
            case ZERO: 				    return GL10.GL_ZERO;
            case ONE:				    return GL10.GL_ONE;
            case SRC_COLOR:				return GL10.GL_SRC_COLOR;
            case ONE_MINUS_SRC_COLOR:	return GL10.GL_ONE_MINUS_SRC_COLOR;
            case DST_COLOR:				return GL10.GL_DST_COLOR;
            case ONE_MINUS_DST_COLOR:	return GL10.GL_ONE_MINUS_DST_COLOR;
            case SRC_ALPHA:				return GL10.GL_SRC_ALPHA;
            case ONE_MINUS_SRC_ALPHA:	return GL10.GL_ONE_MINUS_SRC_ALPHA;
            case DST_ALPHA:				return GL10.GL_DST_ALPHA;
            case ONE_MINUS_DST_ALPHA:	return GL10.GL_ONE_MINUS_DST_ALPHA;
            default:				    throw new InvalidParameterException("Illegal BlendFactor value!");
        }
    }
	
    private static int getGLConstant(CompareFunction function)
    {
        switch (function)
        {
            case NEVER:				return GL10.GL_NEVER;
            case ALWAYS:			return GL10.GL_ALWAYS;
            case LESS:				return GL10.GL_LESS;
            case LESS_OR_EQUAL:		return GL10.GL_LEQUAL;
            case EQUAL:				return GL10.GL_EQUAL;
            case GREATER_OR_EQUAL:  return GL10.GL_GEQUAL;
            case GREATER:			return GL10.GL_GREATER;
            case NOT_EQUAL:			return GL10.GL_NOTEQUAL;
            default:				throw new InvalidParameterException("Illegal CompareFunction value!");
        }
    }
	
    private static int getGLConstant(Side side)
    {
        switch (side)
        {
            case NONE:				return 0;
            case FRONT:				return GL10.GL_FRONT;
            case BACK:				return GL10.GL_BACK;
            case FRONT_AND_BACK:	return GL10.GL_FRONT_AND_BACK;
            default:				throw new InvalidParameterException("Illegal Side value!");
        }
    }
	
    private static int getGLConstant(TextureBlendMode mode)
    {
        switch (mode)
        {
            case REPLACE:			return GL10.GL_REPLACE;
            case MODULATE:			return GL10.GL_MODULATE;
            case DECAL:				return GL10.GL_DECAL;
            case BLEND:				return GL10.GL_BLEND;
            case ADD:				return GL10.GL_ADD;
            default:				throw new InvalidParameterException("Illegal TextureBlendMode value!");
        }
    }
	
    private static int getGLConstant(TextureFilter filter)
    {
        switch (filter)
        {
            case NEAREST:				    return GL10.GL_NEAREST;
            case NEAREST_MIPMAP_NEAREST:	return GL10.GL_NEAREST_MIPMAP_NEAREST;
            case NEAREST_MIPMAP_LINEAR:		return GL10.GL_NEAREST_MIPMAP_LINEAR;
            case LINEAR:    				return GL10.GL_LINEAR;
            case LINEAR_MIPMAP_NEAREST:		return GL10.GL_LINEAR_MIPMAP_NEAREST;
            case LINEAR_MIPMAP_LINEAR:		return GL10.GL_LINEAR_MIPMAP_LINEAR;
            default:		        		throw new InvalidParameterException("Illegal TextureFilter value!");
        }
    }
	
    
    private static int getGLConstant(TextureWrapMode mode)
    {
        switch (mode)
        {
            case CLAMP:				return GL10.GL_CLAMP_TO_EDGE;
            case REPEAT:			return GL10.GL_REPEAT;
            default:				throw new InvalidParameterException("Illegal TextureWrapMode value!");
        }
    }  
}
