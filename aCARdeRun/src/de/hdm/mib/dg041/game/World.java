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

import java.io.IOException;
import java.io.InputStream;
import de.hdm.mib.dg041.graphics.Camera;
import de.hdm.mib.dg041.graphics.CompareFunction;
import de.hdm.mib.dg041.graphics.GraphicDevice;
import de.hdm.mib.dg041.graphics.Material;
import de.hdm.mib.dg041.graphics.Mesh;
import de.hdm.mib.dg041.graphics.Renderer;
import de.hdm.mib.dg041.graphics.Texture;
import de.hdm.mib.dg041.math.Matrix4x4;
import android.content.Context;
import android.util.Log;

/**
 * This class realizes the game world and contains all sourrunding objects
 * of the game, exept the hero and the enemy.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class World
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private static String TAG = World.class.getName();
    
    /** graphical objects, textures, materials, meshes required for the world **/
    private Mesh meshTreeSmall, meshTreeBig, meshRoad, meshGroundLeft, 
    			meshGroundRight, meshSky, meshRaceClock, meshCrashHUD;
    private Texture texTreeSmall, texTreeBig, texRoad, texGroundLeft, 
    			texGroundRight, texSky, texRaceClock, texCrashHUD;
    private Material matTreeSmall, matTreeBig, matRoad, matGroundLeft, 
    			matGroundRight, matSky, matRaceClock, matCrashHUD;
    private Matrix4x4 worldRoad, worldGroundLeft, worldGroundRight, worldSky, 
    			worldRaceClock, worldCrashHUD;
    private Matrix4x4[] worldTreesSmall, worldTreesBig;
    
    /** values to perform the movement of trees **/
    private float speed = 0.05f;
    private float position = 0f;

    private Context context;
    private GraphicDevice graphicDevice;
    private Renderer renderer;
    private Camera sceneCamera;

    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    public World(Context context, GraphicDevice graphicDevice, Renderer renderer )
    {
        this.context = context;
        this.graphicDevice = graphicDevice;
        this.renderer = renderer;
    }

    /**
     * The method initializes all required objects and define the parameters of a aCARdeRun world.
     * The method will be called for the first time of the game.
     */
    public void initialize()
    {
	
        Matrix4x4 projection = new Matrix4x4();
        Matrix4x4 view = new Matrix4x4();
	
        // define the projection type for the scene camera
        projection = new Matrix4x4();							                    // create new projection matrix
        projection.setPerspectiveProjection(-0.1f, 0.1f, -0.1f, 0.1f, 0.1f, 16.0f);	// set projection parameters
        view = new Matrix4x4();								                        // create new view matrix
        view.translate(0, -1, 0);							                        // invert view matrix of the scene camera to y-axis
        sceneCamera = new Camera();							                        // create new scene camera
        sceneCamera.setProjection(projection);						                // set projection matrix to scene camera
        sceneCamera.setView(view);							                        // set view to scene camera

        // set material of scene objects
        matTreeBig = new Material();						                        // new material for scene object: big tree
        matTreeBig.setAlphaTestFunction(CompareFunction.GREATER_OR_EQUAL);	        // set alpha test function for scene object: big tree
        matTreeBig.setAlphaTestValue(0.9f);					                        // set alpha test value for scene object: big tree

        matTreeSmall = new Material();						                        // new material for scene object: small tree
        matTreeSmall.setAlphaTestFunction(CompareFunction.GREATER_OR_EQUAL);	    // set alpha test function for scene object: small tree
        matTreeSmall.setAlphaTestValue(0.9f);					                    // set alpha test value for scene object: small tree

        matRoad = new Material();						                            // new material for scene object: road
        matGroundLeft = new Material();						                        // new material for scene object: left ground
        matGroundRight = new Material();					                        // new material for scene object: right ground
        matSky = new Material();						                            // new material for scene object: sky
        matCrashHUD = new Material();						                        // new material for scene object: crash HUD

        matRaceClock = new Material();						                        // new material for scene object: race clock
        matRaceClock.setAlphaTestFunction(CompareFunction.GREATER_OR_EQUAL);	    // set alpha test function for scene object: race clock
        matRaceClock.setAlphaTestValue(0.9f);					                    // set alpha test value for scene object: race clock
	
	
	    // load world components
        // World: SKY
        worldSky = new Matrix4x4();							                        // new matrix for scene object: sky
        worldSky.translate(-30, 0, -15);

        // World: ROAD
        worldRoad = new Matrix4x4();							                    // new matrix for scene object: road
        worldRoad.translate(0, 0, -1);							                    // translate matrix to z-axis

        // World: GROUNDLEFT
        worldGroundLeft = new Matrix4x4();						                    // new matrix for scene object: left ground
        worldGroundLeft.translate(-23, 0, -1);						                // translate matrix to the left of the road matrix

        // World: GROUNDRIGHT
        worldGroundRight = new Matrix4x4();						                    // new matrix for scene object: right ground
        worldGroundRight.translate(2, 0, -1);						                // translate matrix to the right of the road matrix

        // World: RACECLOCK
        worldRaceClock = new Matrix4x4();						                    // new matrix for scene object: race clock
        worldRaceClock.translate(0.35f, 2.1f, -1.5f);					            // translate matrix to the right top of the screen

        // World: CRASHHUD
        worldCrashHUD = new Matrix4x4();						                    // new matrix for scene object: crash HUD
        worldCrashHUD.translate(-0.45f, 0.6f, -0.5f);					            // translate matrix to the center of the screen
	
        	
        // create matrix for all tree objects and translate their position to the left and right of the road
        worldTreesSmall = new Matrix4x4[] {
        	Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-4), Matrix4x4.createRotationY(45)),	// 2nd tree left
        	Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-10), Matrix4x4.createRotationY(45)),	// 4th tree left
        	Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-16), Matrix4x4.createRotationY(45)),	// 6th tree left
        	Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-1), Matrix4x4.createRotationY(35)),	// 1st tree right
        	Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-7), Matrix4x4.createRotationY(35)),	// 3rd tree right
        	Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-13), Matrix4x4.createRotationY(35)),	// 5th tree right
        };
        
        worldTreesBig = new Matrix4x4[] {
        	Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-1), Matrix4x4.createRotationY(120)),	// 1st tree left
        	Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-7), Matrix4x4.createRotationY(120)),	// 3rd tree left
        	Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-13), Matrix4x4.createRotationY(120)),	// 5th tree left
        	Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-4), Matrix4x4.createRotationY(120)),	// 2nd tree right
        	Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-10), Matrix4x4.createRotationY(120)),	// 4th tree right
        	Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-16), Matrix4x4.createRotationY(120)),	// 6th tree right
        };
	
    }


    /**
     * This method loads the content for a aCARdeRun world.
     */
    public void loadContent()
    {
        InputStream stream;
	
	    // load scene objects and their textures from file system
        try
        {
            // read scene object sky.obj
            stream = context.getAssets().open("sky.obj");
            meshSky = Mesh.loadFromOBJ(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load SKY.OBJ as MESH!");
        }

	    // load the texture for scene object: sky
        try
        {
            // read scene object sky.png
            stream = context.getAssets().open("sky.png");
            texSky = graphicDevice.createTexture(stream);
            matSky.setTexture(texSky);
        }
        catch (IOException e)
        {
           e.printStackTrace();
           Log.e(TAG, "ERROR to load SKY.PNG as Texture!");
        }

	    // load the mesh for the scene object: treesmall
        try
        {
            // read mesh treesmall.obj
            stream = context.getAssets().open("treesmall.obj");
            meshTreeSmall = Mesh.loadFromOBJ(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load TREESMALL.OBJ as MESH!");
        }

	    // load the texture for scene object: treesmall
        try
        {
            // read texture from treesmall.png
            stream = context.getAssets().open("treesmall.png");
            texTreeSmall = graphicDevice.createTexture(stream);
            matTreeSmall.setTexture(texTreeSmall);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load TREESMALL.PNG as Texture!");
        }

        // load the mesh for the scene object: treebig
        try
        {
            // read mesh treebig.obj
            stream = context.getAssets().open("treebig.obj");
            meshTreeBig = Mesh.loadFromOBJ(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load TREESMALL.OBJ as MESH!");
        }

	    // load the texture for scene object: treebig
        try
        {
            // read texture from treebig.png
            stream = context.getAssets().open("treebig.png");
            texTreeBig = graphicDevice.createTexture(stream);
            matTreeBig.setTexture(texTreeBig);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load TREESMALL.PNG as Texture!");
        }

	    // load the mesh for the scene object: road
        try
        {
            // read mesh road.obj
            stream = context.getAssets().open("road.obj");
            meshRoad = Mesh.loadFromOBJ(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load ROAD.OBJ as MESH!");
        }

	    // load the texture for scene object: road
        try
        {
            // read texture from road.png
            stream = context.getAssets().open("road.png");
            texRoad = graphicDevice.createTexture(stream);
            matRoad.setTexture(texRoad);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load ROAD.PNG as Texture!");
        }

	    // load the mesh for scene object: ground left
        try
        {
            // read mesh groundleft.obj
            stream = context.getAssets().open("groundleft.obj");
            meshGroundLeft = Mesh.loadFromOBJ(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load GROUNDLEFT.OBJ as MESH!");
        }

	    // load the mesh for scene object: ground right
        try
        {
            stream = context.getAssets().open("groundright.obj");
            meshGroundRight = Mesh.loadFromOBJ(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load GROUNDRIGHT.OBJ as MESH!");
        }

	    // load the texture for scene object: ground
        try
        {
            // read the texture ground.png and set it to the scene object ground left and right
            stream = context.getAssets().open("grass.png");
            texGroundLeft = graphicDevice.createTexture(stream);
            texGroundRight = graphicDevice.createTexture(stream);
            matGroundLeft.setTexture(texGroundLeft);
            matGroundRight.setTexture(texGroundRight);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load GRASS2.PNG as Texture!");
        }

	
	    // load the mesh for the scene object: race clock
        try
        {
            // read raceclock.obj
            stream = context.getAssets().open("raceclock.obj");
            meshRaceClock = Mesh.loadFromOBJ(stream);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load RACECLOCK.OBJ as MESH!");
        }

	    // load the texture for the scene object: race clock
        try
        {
            // Textur des Sceneobjekt: Raceclock lesen
            stream = context.getAssets().open("raceclock.png");
            texRaceClock = graphicDevice.createTexture(stream);
            matRaceClock.setTexture(texRaceClock);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load RACECLOCK.PNG as Texture!");
        }

        // load the mesh for the scene object: crash HUD
        try
        {
            // read the mesh crashhud.obj
            stream = context.getAssets().open("crashhud.obj");
            meshCrashHUD = Mesh.loadFromOBJ(stream);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load CRASHHUD.OBJ as MESH!");
        }

	    // load the texture for the scene object: race clock
		try
        {
		    // read texture raceclock.png
            stream = context.getAssets().open("raceclock.png");
            texCrashHUD = graphicDevice.createTexture(stream);
            matCrashHUD.setTexture(texCrashHUD);
		}
        catch (Exception e)
        {
		    e.printStackTrace();
		    Log.e(TAG, "ERROR to load CRASHHUD.PNG as Texture!");
		}
    }

    /**
     * This method is called in a certain period of times to realize the impression of a fluid motion.
     * @param deltaseconds
     */
    public void update(float deltaseconds)
    {
        if(InGameScreen.IsGameStarted() && !HUD.getAccidentHappened())
        {
            // realize the movement of the trees
            if (position < 6.0f)
            {
                position += speed;
            }
            else
            {
                position = 0f;
            }

            // update the tree positions of the world
            this.updateWorldTrees();
        }
    }

    /**
     * This method draws all components on the screen
     * @param deltaseconds
     */
    public void draw(float deltaseconds)
    {
        // clear screen
        graphicDevice.clear(0.0f, 0.2f, 1.0f, 1.0f, 1.0f);
	
	    graphicDevice.setCamera(sceneCamera);
	
        // draw the sky mesh before all other objects
        renderer.drawMesh(meshSky, matSky, worldSky);
	
        // draw the road
        renderer.drawMesh(meshRoad, matRoad, worldRoad);
	
        // draw left and right ground
        renderer.drawMesh(meshGroundLeft, matGroundLeft, worldGroundLeft);
        renderer.drawMesh(meshGroundRight, matGroundRight, worldGroundRight);
	
        if (InGameScreen.IsGameStarted())
        {
            // draw race clock
            renderer.drawMesh(meshRaceClock, matRaceClock, worldRaceClock);		// the race click should be drawn if the game was started
        }
	
        if(!InGameScreen.IsGameStarted() && HUD.getAccidentHappened())
        {
            // draw chrash HUD
            renderer.drawMesh(meshCrashHUD, matCrashHUD, worldCrashHUD);		// the race clock should be drawn if the game was stopped
        }
	
        // draw all small trees in the world
        for(Matrix4x4 worldTree : worldTreesSmall)
        {
            renderer.drawMesh(meshTreeSmall, matTreeSmall, worldTree);
        }
	
        // draw all big trees in the world
        for(Matrix4x4 worldTree : worldTreesBig)
        {
            renderer.drawMesh(meshTreeBig, matTreeBig, worldTree);
        }
    }

    /**
     * This method updates all world trees depending on the given world speed.
     * This gives the impression of a fluid motion.
     */
    private void updateWorldTrees()
    {
        // update the positions of the small tress int the world matrix depending on the world speed
        for(int i = 0; i < worldTreesSmall.length; i++ )
        {
            worldTreesSmall[i].setIdentity();
            if (i == 0)
            worldTreesSmall[0] = Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-4), Matrix4x4.createRotationY(45));
            if (i == 1)
            worldTreesSmall[1] = Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-10), Matrix4x4.createRotationY(45));
            if (i == 2)
            worldTreesSmall[2] = Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position-16), Matrix4x4.createRotationY(45));
            if (i == 3)
            worldTreesSmall[3] = Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-1), Matrix4x4.createRotationY(45));
            if (i == 4)
            worldTreesSmall[4] = Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-7), Matrix4x4.createRotationY(45));
            if (i == 5)
            worldTreesSmall[5] = Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position-13), Matrix4x4.createRotationY(45));
        }

        // update the positions of the big trees in the world matrix depending on the world speed
        for(int i = 0; i < worldTreesBig.length; i++)
        {
            worldTreesBig[i].setIdentity();
            if (i == 0)
            {
                worldTreesBig[0] = Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position - 1), Matrix4x4.createRotationY(120));
            }
            if (i == 1)
            {
                worldTreesBig[1] = Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position - 7), Matrix4x4.createRotationY(120));
            }
            if (i == 2)
            {
                worldTreesBig[2] = Matrix4x4.multiply(Matrix4x4.createTranslation(-1.2f, 0, position - 13), Matrix4x4.createRotationY(120));
            }
            if (i == 3)
            {
                worldTreesBig[3] = Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position - 4), Matrix4x4.createRotationY(120));
            }
            if (i == 4)
            {
                worldTreesBig[4] = Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position - 10), Matrix4x4.createRotationY(120));
            }
            if (i == 5)
            {
                worldTreesBig[5] = Matrix4x4.multiply(Matrix4x4.createTranslation(+1.2f, 0, position - 16), Matrix4x4.createRotationY(120));
            }
        }
    }

    /**
     * This method increases the speed of the world to increase the level of difficulty.
     * The default value is +0.05f.
     *
     * @param value
     *          The value to speed up the movement of the world.
     */
    public void setWorldSpeed(float value)
    {
	    this.speed = value;
    }
}
