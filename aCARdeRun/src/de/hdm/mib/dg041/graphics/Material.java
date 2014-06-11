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

import java.security.InvalidParameterException;

/**
 * This class defines a material object for loaded mesh object. A material
 * contains a texture, color, blend factor, etc.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Material
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private Texture texture = null;
	
    private TextureFilter    textureFilterMin  = TextureFilter.LINEAR;
    private TextureFilter    textureFilterMag  = TextureFilter.LINEAR;
    private TextureWrapMode  textureWrapModeU  = TextureWrapMode.REPEAT;
    private TextureWrapMode  textureWrapModeV  = TextureWrapMode.REPEAT;

    private TextureBlendMode textureBlendMode                    = TextureBlendMode.MODULATE;
    private float[]          textureBlendColor;
		
    private float[]          colorMaterial;
    private BlendFactor      blendFactorSrc, blendFactorDst;

    private boolean          depthWrite                          = true;
    private Side             cullSide                            = Side.NONE;
    private CompareFunction  depthTestFunction                   = CompareFunction.LESS;
    private CompareFunction  alphaTestFunction                   = CompareFunction.ALWAYS;
    private float            alphaTestValue                       = 0.0f;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public Material()
    {
        textureBlendColor = new float[] {0.0f, 0.0f, 0.0f, 0.0f};
        colorMaterial = new float[] {1.0f, 1.0f, 1.0f, 1.0f};
        blendFactorSrc = BlendFactor.ONE;
        blendFactorDst = BlendFactor.ZERO;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the texture of the material
     */
    public Texture getTexture()
    {
	    return texture;
    }

    /**
     * @param texture
     *          set the texture of the material
     */
    public void setTexture(Texture texture)
    {
	    this.texture = texture;
    }

    /**
     * @return the minimum texture filter
     */
    public TextureFilter getTextureFilterMin()
    {
	    return textureFilterMin;
    }

    /**
     * @return the mag texture filter
     */
    public TextureFilter getTextureFilterMag()
    {
	    return textureFilterMag;
    }

    /**
     * The method sets the given min and mag values to the texture filter.
     *
     * @param min
     * @param mag
     */
    public void setTextureFilter(TextureFilter min, TextureFilter mag)
    {
        if (mag != TextureFilter.NEAREST && mag != TextureFilter.LINEAR)
        {
            throw new InvalidParameterException("Magnification filter must be either NEAREST or LINEAR");
        }
        this.textureFilterMin = min;
        this.textureFilterMag = mag;
    }

    /**
     * @return the texture wrap mode U
     */
    public TextureWrapMode getTextureWrapModeU()
    {
	    return textureWrapModeU;
    }

    /**
     * @return the texture warp mode V
     */
    public TextureWrapMode getTextureWrapModeV()
    {
	    return textureWrapModeV;
    }

    /**
     * This method sets the given U and V mode as texture wrap modes.
     *
     * @param wrapModeU
     * @param wrapModeV
     */
    public void setTextureWrap(TextureWrapMode wrapModeU, TextureWrapMode wrapModeV)
    {
        this.textureWrapModeU = wrapModeU;
        this.textureWrapModeV = wrapModeV;
    }

    /**
     * @return the blend mode of the texture
     */
    public TextureBlendMode getTextureBlendMode()
    {
	    return textureBlendMode;
    }

    /**
     * @param mode set the blend mode of the texture
     */
    public void setTextureBlendMode(TextureBlendMode mode)
    {
	    this.textureBlendMode = mode;
    }

    /**
     * @return the blend color of the texture.
     */
    public float[] getTextureBlendColor()
    {
	    return textureBlendColor;
    }

    /**
     * This method sets the color values for the texture blend color.
     *
     * @param color
     */
    public void setTextureBlendColor(float[] color)
    {
        if (colorMaterial.length != 4)
        {
            throw new InvalidParameterException("Color must be 4 elements (RGBA).");
        }
        this.textureBlendColor = color.clone();
    }

    /**
     * @return the color of the material
     */
    public float[] getMaterialColor()
    {
	    return colorMaterial;
    }

    /**
     * This method sets the color values for the material.
     * @param colorMaterial
     */
    public void setColorMaterial(float[] colorMaterial)
    {
        if (colorMaterial.length != 4)
        {
            throw new InvalidParameterException("Color must be 4 elements (RGBA).");
        }
        this.colorMaterial = colorMaterial;
    }

    /**
     * @return the blend source factor.
     */
    public BlendFactor getBlendSourceFactor()
    {
	    return blendFactorSrc;
    }

    /**
     * @return the blend destination factor.
     */
    public BlendFactor getBlendDestFactor()
    {
	    return blendFactorDst;
    }

    /**
     * This method sets the source and destination values for the blend factors.
     *
     * @param factorSrc
     * @param factorDst
     */
    public void setBlendFactors(BlendFactor factorSrc, BlendFactor factorDst)
    {
        if (factorSrc == BlendFactor.SRC_COLOR || factorSrc == BlendFactor.ONE_MINUS_SRC_COLOR)
        {
            throw new InvalidParameterException("Invalid source factor.");
        }
        if (factorDst == BlendFactor.DST_COLOR || factorDst == BlendFactor.ONE_MINUS_DST_COLOR)
        {
            throw new InvalidParameterException("Invalid destination factor.");
        }

        this.blendFactorSrc = factorSrc;
        this.blendFactorDst = factorDst;
    }

    /**
     * @return the culling side
     */
    public Side getCullSide()
    {
	    return cullSide;
    }

    /**
     * @param side the culling side of the material to set.
     */
    public void setCullSide(Side side)
    {
	    this.cullSide = side;
    }
    

    /**
     * @return the compare function to test the depth
     */
    public CompareFunction getDepthTestFunction()
    {
	    return depthTestFunction;
    }

    /**
     * @param function the compare function for depth testing to set
     */
    public void setDepthTestFunction(CompareFunction function)
    {
	    this.depthTestFunction = function;
    }

    /**
     * @return the compare function to test the alpha value
     */
    public CompareFunction getAlphaTestFunction()
    {
	    return alphaTestFunction;
    }

    /**
     * @param function the compare function for alpha testing to set.
     */
    public void setAlphaTestFunction(CompareFunction function)
    {
	    this.alphaTestFunction = function;
    }

    /**
     * @return consider that depth is enabled/disabled
     */
    public boolean getDepthWrite()
    {
	    return depthWrite;
    }

    /**
     * @param depthWrite whether the depth value should be enabled/disabled
     */
    public void setDepthWrite(boolean depthWrite)
    {
	    this.depthWrite = depthWrite;
    }

    /**
     * @return the current alpha test value
     */
    public float getAlphaTestValue()
    {
	    return alphaTestValue;
    }

    /**
     * @param value set the alpha test value
     */
    public void setAlphaTestValue(float value)
    {
        if (value < 0.0f || value > 1.0f)
        {
            throw new InvalidParameterException("Alpha test value must be between 0 and 1!");
        }

        this.alphaTestValue = value;
    }    
}