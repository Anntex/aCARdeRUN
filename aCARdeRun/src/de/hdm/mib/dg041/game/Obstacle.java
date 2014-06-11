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

import java.io.InputStream;
import java.util.Random;

import android.content.Context;
import android.util.Log;
import de.hdm.mib.dg041.graphics.Camera;
import de.hdm.mib.dg041.graphics.GraphicDevice;
import de.hdm.mib.dg041.graphics.Material;
import de.hdm.mib.dg041.graphics.Mesh;
import de.hdm.mib.dg041.graphics.Renderer;
import de.hdm.mib.dg041.graphics.Texture;
import de.hdm.mib.dg041.math.Matrix4x4;

/**
 * This class realizes the oncoming traffic of the hero. The behavior of the
 * obstacle class is implemented as static behavior. The loaded obstacles lay
 * down on the lane and the hero has to avoid a collision.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Obstacle
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private static final String TAG = Obstacle.class.getName();

    private Camera camera;					    // Camera of the obstacle
    private Mesh meshBaleofStone;				// mesh of the obstacle
    private Texture texBaleofStraw; 		    // texture of the obstacle
    private Material matBaleofStraw;			// material of the obstacle
    private Matrix4x4 worldBaleofStraw;			// world of the obstacle
    private Random random;					    // random generator for the position of the obstacle
    private int randomPosition;					// random position of the obstacle in the world
    private float zValue = 0f;					// current position of the obstacle in the world
    private float speed = 0.05f;				// speed of the obstacle
    private float position = 0f;
    private boolean isOnRightLane;				// position of the obstacle on which lane

    private Context context;
    private GraphicDevice graphicDevice;
    private Renderer renderer;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public Obstacle(Context context, GraphicDevice graphicDevice, Renderer renderer)
    {
        this.context = context;
        this.graphicDevice = graphicDevice;
        this.renderer = renderer;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    public void initialize()
    {
        Matrix4x4 projection;
        Matrix4x4 view;
	
	    // camera parameters
		projection = new Matrix4x4();
		projection.setPerspectiveProjection(-0.1f, 0.1f, -0.1f, 0.1f, 0.1f, 16.0f);
		view = new Matrix4x4();
		view.translate(0, -1, 0);
		
		camera = new Camera();
		camera.setProjection(projection);
		camera.setView(view);
		
	    // meterial of the obstacle
		matBaleofStraw = new Material();
			
	    // create the world for the obstacle
		worldBaleofStraw = new Matrix4x4();
		loadWorld();
    }

    /**
     * This method loads the required content of an obstacle.
     */
    public void loadContent()
    {
        // create new input stream
        InputStream stream;
	
	    // stream methes
        try
        {
            stream = context.getAssets().open("box.obj");	// open BOX.OBJ
            meshBaleofStone = Mesh.loadFromOBJ(stream);		// create MESH
            stream = null;
        }
        catch (Exception e)
        {
        	    e.printStackTrace();
        	    Log.e(TAG, "ERRROR to load BOX.OBJ as MESH!");
        }
	
        // stream texture
        try
        {
		    stream = context.getAssets().open("straw.png");	// open STONEWALL.PNG
		    texBaleofStraw = graphicDevice.createTexture(stream);
		    matBaleofStraw.setTexture(texBaleofStraw);		// create material
		    stream = null;
		}
        catch (Exception e)
        {
		    e.printStackTrace();
		    Log.e(TAG, "ERROR to load STRAW.PNG as TEXTURE");
		}
    }

    /**
     * Update the position of the obstacle in the world in a certain period of time.
     *
     * @param deltaSeconds
     */
    public void update(float deltaSeconds)
    {
        if(InGameScreen.IsGameStarted() && !HUD.getAccidentHappened())
        {
            // position of the obstacle
            if (zValue == 0)                        // if speed == 0 --> obstacle is not visible
            {
                random = new Random();			    // create new random value
                randomPosition = random.nextInt(2);	// get next value between 0 and 1

                if (randomPosition == 0)            // if value == 0 --> display obstacle on right lane
                {
                    position = +0.4f;
                    isOnRightLane = true;
                }
                else if (randomPosition == 1)       // if value == 1 --> display obstacle on left lane
                {
                    position = -0.4f;
                    isOnRightLane = false;
                }
            }

                // realize the movement of the obstacle
                if(zValue <= 20.0f)
                    zValue += speed;
                else
                    zValue = 0f;

                // reload world matrix
                loadWorld();
        }
    }

    /**
     * This method redraws an obstacle in a certain period of time.
     * @param deltaSeconds
     */
    public void draw(float deltaSeconds)
    {
	    // set scene camera
		graphicDevice.setCamera(camera);
	    // draw scene objects
		renderer.drawMesh(meshBaleofStone, matBaleofStraw, worldBaleofStraw);
    }

    /**
     * @return TRUE if the obstacle is positioned on the right lane, otherwise FALSE
     */
    public boolean positionObstacleIsRight()
    {
	    return isOnRightLane;
    }

    public int getZValue()
    {
	    return (int)zValue;
    }  

    public void resetZValue()
    {
	    this.zValue = 0.0f;
    }

    /**
     * @param value sets the speed of the obstacle. defualt value: +0.1f
     */
    public void setObstacleSpeed(float value)
    {
	    this.speed = value;
    }

    /**
     * The method loads all the required content for an obstacle in a world.
     */
    private void loadWorld()
    {
        worldBaleofStraw.setIdentity();
        worldBaleofStraw.translate(position, 0, zValue-20);
        worldBaleofStraw.scale(0.20f, 0.20f, 0.20f);
        worldBaleofStraw.rotateY(45);
    }
}