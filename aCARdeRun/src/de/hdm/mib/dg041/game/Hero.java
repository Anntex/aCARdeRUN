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
import de.hdm.mib.dg041.game.aCARdeRunGame.GameState;
import de.hdm.mib.dg041.graphics.Camera;
import de.hdm.mib.dg041.graphics.CompareFunction;
import de.hdm.mib.dg041.graphics.GraphicDevice;
import de.hdm.mib.dg041.graphics.Material;
import de.hdm.mib.dg041.graphics.Mesh;
import de.hdm.mib.dg041.graphics.Renderer;
import de.hdm.mib.dg041.graphics.Texture;
import de.hdm.mib.dg041.input.InputEvent;
import de.hdm.mib.dg041.math.Matrix4x4;
import de.hdm.mib.dg041.math.Vector3;

/**
 *
 * This class defines a hero object of the game. This class contains all required data to initialize a hero instance.
 * Also this class contains the whole functionality and the behavior of a hero. This class is defined under the
 * singleton design pattern, to garantee a single instance of this class. The hero class will be initialized in the
 * aCARdeRunGame class.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Hero
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private static String TAG = Hero.class.getName();
    private static Hero heroInstance = null;

    // *** MAIN COMPONENTS OF THE HERO *** //
    private Camera cameraMenu, cameraInGame;
    private Mesh meshHero;
    private Material matHero;
    private Texture texHero;
    private Matrix4x4 worldHeroMenu, worldHeroGame;
    private String texture;
    private boolean isOnRightLane = true;
    private boolean transitionToLeft = false;
    private boolean transitionToRight = false;
    
    // *** COMPONENTS TO DISPLAY THE HERO *** //
    private Context context;
    private GraphicDevice graphicDevice;
    private Renderer renderer;

    // --------------------------------------------------------
    // SINGLETON
    // --------------------------------------------------------

    public static Hero getInstance()
    {
        if(heroInstance == null) {
            heroInstance = new Hero();
            return heroInstance;
        } else
            return heroInstance;
    }

    private Hero()
    {}

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * This class initializes all required data for a hero instance. This includes the parameters of the camera.
     */
    public void initialize(Context context, GraphicDevice graphicDevice, Renderer renderer )
    {

        this.context = context;
        this.graphicDevice = graphicDevice;
        this.renderer = renderer;


        Matrix4x4 projection = new Matrix4x4();
        Matrix4x4 view = new Matrix4x4();
	
	// MENU: camera properties of the hero displayed in the menu
        projection = new Matrix4x4();
        projection.setPerspectiveProjection(-0.1f, 0.1f, -0.1f, 0.1f, 0.1f, 100.0f);
        view = new Matrix4x4();
        view.translate(-2.5f, -2f, -5f);

        cameraMenu = new Camera();
        cameraMenu.setProjection(projection);
        cameraMenu.setView(view);

        worldHeroMenu = new Matrix4x4();
        worldHeroMenu.translate(0, 0, -1);
	
	// INGAME: camera properties of the hero displayed in the game
        projection = new Matrix4x4();
        projection.setPerspectiveProjection(-0.1f, 0.1f, -0.1f, 0.1f, 0.53f, 16.0f);
        view = new Matrix4x4();
        view.translate(0, 0, -5);

        cameraInGame = new Camera();
        cameraInGame.setProjection(projection);
        cameraInGame.setView(view);

        worldHeroGame = new Matrix4x4();
        worldHeroGame.translate(+0.3f, -0.7f, -0.5f);
        worldHeroGame.rotateY(180);
        worldHeroGame.scale(0.27f, 0.37f, 0.3f);

        matHero = new Material();
        matHero.setAlphaTestFunction(CompareFunction.GREATER_OR_EQUAL);
        matHero.setAlphaTestValue(0.9f);

        
        // set the texture of the hero using a random value
        Random random = new Random();
        int digit = random.nextInt(3);

        if (digit == 0)
        texture = "heroblue.png";
        else if (digit == 1)
        texture = "heroorange.png";
        else if (digit == 2)
            texture = "herored.png";
        else if (digit == 3)
            texture = "herowhite.png";
    }

    /**
     * This method load the required content of a hero instance live scene objects, the hero 3D model, textures, etc.
     */
    public void loadContent()
    {
        InputStream stream;

        try
        {
            // scene object: read the hero 3d model from assets folder
            stream = context.getAssets().open("hero.obj");	// open hero.obj
            meshHero = Mesh.loadFromOBJ(stream);		    // define the meshes dependend on the read 3d model data
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load hero object!");
        }
	
	
        try
        {
            // read the material of the scene object
            stream = context.getAssets().open(texture);	    // open hero.png
            texHero = graphicDevice.createTexture(stream);	// create a new texture
            matHero.setTexture(texHero);			        // set texture to the hero object
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR to load HeroMaterial!");
        }
    }

    /**
     * This method realises the movement of the hero in the menu screen. If the game state is MENU the loaded hero
     * object will be rotate in range of 15 degrees.
     */
    public void update(float deltaseconds)
    {
	    if (aCARdeRunGame.getGameState() == GameState.MENU)
		    worldHeroMenu.rotateY(deltaseconds * 15);
    }

    /**
     * This method draws the display in a certain period of time
     */
    public void draw(float deltaseconds)
    {
        // if the game state == MENU, draw the hero using the CAMERAMENU
        if(aCARdeRunGame.getGameState() == GameState.MENU) {
            // set scene camer
                graphicDevice.setCamera(cameraMenu);
            // draw scene objects
                renderer.drawMesh(meshHero, matHero, worldHeroMenu);
        }
	
        // if the game state == GAME, draw the hero using the CAMERAINGAME
        if(aCARdeRunGame.getGameState() == GameState.GAME)
             transition();
    }

    public void handleInputEvent(InputEvent inputEvent)
    {
        /* detect the motion of the device using the gravity sensor
         * this sensor detects motions on three axis. we are using the
         * device in landscape mode, so we are only interested in y-axis values. */
        Vector3 gravityValues = new Vector3(
            inputEvent.getValues()[0],
            inputEvent.getValues()[1],
            inputEvent.getValues()[2]
            );
	
	    // detect right motion
	    if(gravityValues.getY() > 2.5f)
        {
            Log.d(TAG, "RIGHT MOTION");
            if(!isOnRightLane)
                transitionToRight = true;
	    }
		
        // detect left meotion
	    if (gravityValues.getY() < -2.5f)
        {
            Log.d(TAG, "LEFT MOTION");
            if(isOnRightLane)
                transitionToLeft = true;
	    }
    }

    /**
     * The method reposition the hero between the two different street lanes, depending on the current position
     * and the transition input from input device.
     */
    private void transition()
    {
	    // set scene camera
	    graphicDevice.setCamera(cameraInGame);

        // draw scene objects

        // hero is on right lane
        if(isOnRightLane)
        {
            // and want to switch the position to the left lane
            if(transitionToLeft)
            {
               worldHeroGame.setIdentity();				                    // reset matrix
               worldHeroGame.translate(-0.3f, -0.7f, -0.5f);		        // normal matrix if hero is on right lane
               worldHeroGame.rotateY(180);				                    // position the object
               worldHeroGame.scale(0.27f, 0.37f, 0.3f);		                // resize object
               renderer.drawMesh(meshHero, matHero, worldHeroGame);		    // draw hero on the left lane
               transitionToLeft = false;				                    // transition is finished
               isOnRightLane = false;					                    // hero is on left lane
            }
            else                                                            // hero is already on right lane
            {
                worldHeroGame.setIdentity();				                // reset matrix
                worldHeroGame.translate(+0.3f, -0.7f, -0.5f);		        // normal matrix if hero is on right lane
                worldHeroGame.rotateY(180);					                // position the object
                worldHeroGame.scale(0.27f, 0.37f, 0.3f);			        // resize object
                renderer.drawMesh(meshHero, matHero, worldHeroGame);	    // draw hero on the right lane
            }
        }

        // hero is on left lane
        if(!isOnRightLane)
        {
           // and want to switch the position to the right lane
           if(transitionToRight)
           {
                worldHeroGame.setIdentity();				                // reset matrix
                worldHeroGame.translate(+0.3f, -0.7f, -0.5f);		        // normal matrix if hero is on right lane
                worldHeroGame.rotateY(180);					                // position the object
                worldHeroGame.scale(0.27f, 0.37f, 0.3f);			        // resize object
                renderer.drawMesh(meshHero, matHero, worldHeroGame);	    // draw hero on the right lane
                transitionToRight = false;
                isOnRightLane = true;
           }
           else                                                             // hero is already on left lane
           {
                worldHeroGame.setIdentity();				                // reset matrix
                worldHeroGame.translate(-0.3f, -0.7f, -0.5f);		        // normal matrix because hero is on left lane
                worldHeroGame.rotateY(180);					                // position the object
                worldHeroGame.scale(0.27f, 0.37f, 0.3f);			        // resize object
                renderer.drawMesh(meshHero, matHero, worldHeroGame);        // draw hero on the left lane
           }
        }
    }

    /**
     * @return the position of the hero on lane. TRUE if hero is on the right lane,
     * otherwise FALSE.
     */
    public boolean positionHeroIsRight()
    {
	    return isOnRightLane;
    }
}
