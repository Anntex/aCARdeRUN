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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.os.Environment;

/**
 * The sprite font class defines an object to display sprite font on the graphical device.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class SpriteFont
{
    // --------------------------------------------------------
    // INNER CLASS CHARACTER INFO
    // --------------------------------------------------------

    /**
     * This class defines an object to store meta data for a sprite font character.
     *
     * @author dennis.grewe [dg041@hdm-stuttgart.de]
     * Created on 23.02.2012.
     */
    public class CharacterInfo
    {
        public int width;		// width of the character
        public Rect area;		// area of the texture to display the charactor
        public Point offset;    // offset to the position to display the character
    }

    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private Material material;
    private Map<Character, CharacterInfo> characterInfos;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    /**
     * This constructor initializes a new SpriteFont object depending on the given
     * graphic device, the font type and the size of the font.
     *
     * @param graphicsDevice
     * @param typeface
     * @param size
     */
    SpriteFont(GraphicDevice graphicsDevice, Typeface typeface, float size)
    {
        material = new Material();						                        // create a new material
        characterInfos = new HashMap<Character, SpriteFont.CharacterInfo>();	// create a new map -> key = character, value = meta data

        Paint paint = new Paint();				                                // create new drawing object
        paint.setTypeface(typeface);				                            // set the type face
        paint.setTextSize(size);				                                // define the text size
        paint.setARGB(255, 255, 255, 255);			                            // set RGB color and alpha -> here: white
        paint.setFlags(Paint.ANTI_ALIAS_FLAG);			                        // setting: --> here ANTIALIASING

        int spacing = (int)Math.ceil(paint.getFontSpacing());	                // get recomendet space

        char[] c = new char[]{' '};
        int x = 0;
        int y = 0;
        Rect charBounds = new Rect();
        int bitmapSize = 1;
        boolean doesFit = false;
	
        // check if characters fit into texture
        while (!doesFit)
        {
            while (c[0] < 256)
            {
                paint.getTextBounds(c, 0, 1, charBounds);

                // check if width of area is larger than bitmap -> increase space
                if (x + charBounds.width() > bitmapSize)
                {
                    x = 0;
                    y += spacing;
                }
                x += charBounds.width() + 1;
                c[0]++;
                if (c[0] == 128)
                {
                    c[0] = 160;
                }
            }

            // check if we are in range of the bitmap
            if (y + spacing < bitmapSize)
            {
                doesFit = true;
            }
            else
            {
                // double bitmap and start again
                bitmapSize *= 2;
                x = 0;
                y = 0;
                c[0] = ' ';
            }
        }
	
        // create a new bitmap object and define a canvas object to draw on bitmap
        Bitmap bitmap = Bitmap.createBitmap(bitmapSize, bitmapSize, Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        x = 0;
        y = 0;
        c[0] = ' ';
        Point charOffset = new Point();

        // draw the characters on bitmap and set meta data
        while (c[0] < 256)
        {
            paint.getTextBounds(c, 0, 1, charBounds);
            charOffset.set(charBounds.left, charBounds.top);

            // check if width of the area is larger than the bitmap -> increase space
            if (x + charBounds.width() > bitmap.getWidth())
            {
                x = 0;
                y += spacing;
            }

            int drawPosX = x - charOffset.x;
            int drawPosY = y - charOffset.y;
            canvas.drawText(c, 0, 1, drawPosX, drawPosY, paint);

            CharacterInfo info = new CharacterInfo();
            info.width = (int) Math.ceil(paint.measureText(c, 0, 1));
            info.area = new Rect(charBounds);
            info.area.offset(drawPosX, drawPosY);
            info.offset = new Point(charOffset);
            characterInfos.put(c[0], info);

            x += charBounds.width() + 1;

            c[0]++;

            if (c[0] == 128)
            {
                c[0] = 160;
            }
        }
	
        Texture texture = graphicsDevice.createTexture(bitmap);
        material.setTexture(texture);
        material.setTextureFilter(TextureFilter.LINEAR_MIPMAP_LINEAR, TextureFilter.LINEAR);
        material.setTextureWrap(TextureWrapMode.CLAMP, TextureWrapMode.CLAMP);
        material.setBlendFactors(BlendFactor.SRC_ALPHA, BlendFactor.ONE_MINUS_SRC_ALPHA);

        try
        {
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path, "font.png");
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, stream);
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the material of the sprite font
     */
    public Material getMaterial()
    {
	    return material;
    }

    /**
     * @return a list of all meta data (chraracter info) of the sprite font
     */
    public Map<Character, CharacterInfo> getCharacterInfos()
    {
	    return characterInfos;
    }  
}