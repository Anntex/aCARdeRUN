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
package de.hdm.mib.dg041.game;


import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.view.View;

import de.hdm.mib.dg041.graphics.GraphicDevice;
import de.hdm.mib.dg041.input.InputSystem;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public abstract class Game implements Renderer
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    /**
     * indicator if the game was already initialized
     */
    private boolean initialized;
    /**
     * defines the timestamp for the last onDrawFrame call
     */
    private long lastTime;
    
    protected GraphicDevice graphicDevice;
    /**
     * context attribute for the game context
     */
    protected static Context context;
    /**
     * own implemented Renderer to draw the loaded meshes
     */
    protected de.hdm.mib.dg041.graphics.Renderer renderer;
    /**
     * reference to the input system of the device
     */
    protected InputSystem inputSystem;
    protected View view;

    /**
     * display width in pixels
     */
    protected int screenWidth = 800;
    /**
     * display height in pixels
     */
    protected int screenHeight = 480;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public Game(View view)
    {
        this.view = view;
        context = view.getContext();                // get the context using the given view to load objects
        inputSystem = new InputSystem(view);		// create new input system
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------
    
    @Override
    public void onDrawFrame(GL10 gl)
    {
        // get current timestamp and calculate the delta to call update and draw methods periodically
        long currentTime = System.currentTimeMillis();
        float deltaSeconds = (currentTime - lastTime)/1000.0f;

        update(deltaSeconds);
        draw(deltaSeconds);

        lastTime = currentTime;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height)
    {
        // resize the graphic device if the device was rotated.
        graphicDevice.resize(width, height);

        // save the new sizes
        screenHeight = height;
        screenWidth = width;

        // display will be redrawn using the new sizes
        resize(width, height);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config)
    {
        lastTime = System.currentTimeMillis();

        if(!initialized)
        {
            graphicDevice = new GraphicDevice();
            graphicDevice.onSurfaceCreated(gl);

            // renderer to draw the meshes
            renderer = new de.hdm.mib.dg041.graphics.Renderer(graphicDevice);

            initialize();
            initialized = true;
            // load content of the GameActitvity
            loadContent();
        }
        else
        {
            graphicDevice.onSurfaceCreated(gl);
            // load content of the GameActitvity
            loadContent();
        }
    }

    public abstract void initialize();

    public abstract void loadContent();

    public abstract void update(float deltaseconds);

    public abstract void draw(float deltaseconds);

    public abstract void resize(int width, int height);

    // *** MEDIAPLAYER & SOUNDPOOL METHODS ***//

    public abstract void pause();

    public abstract void resume();

    public abstract void stop();
}