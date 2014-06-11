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

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;
import de.hdm.mib.dg041.collision.AxisAlignedBoundingBox;
import de.hdm.mib.dg041.collision.Point;
import de.hdm.mib.dg041.game.aCARdeRunGame.GameState;
import de.hdm.mib.dg041.graphics.Camera;
import de.hdm.mib.dg041.graphics.GraphicDevice;
import de.hdm.mib.dg041.graphics.Renderer;
import de.hdm.mib.dg041.graphics.SpriteFont;
import de.hdm.mib.dg041.graphics.TextBuffer;
import de.hdm.mib.dg041.input.InputEvent;
import de.hdm.mib.dg041.input.InputSystem;
import de.hdm.mib.dg041.math.Matrix4x4;
import de.hdm.mib.dg041.math.Vector3;

/**
 * This class defines a menu screen instance for the game. The menu is the first screen
 * of the game. A user can switch between three different types of screen:
 *  MAINMENU,
 *  CREDITSMENU,
 *  RANKINGMENU
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class MenuScreen extends GameScreen
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private static String TAG = MenuScreen.class.getName();

    /**
     * This enumeration defines the different possible submenus of the game menu
     */
    private enum MenuMode
    {
        MAINMENU,
        CREDITSMENU,
        RANKINGMENU
    }

    /** display components **/
    private Context context;
    private GraphicDevice graphicDevice;
    private Renderer renderer;
    private int screenWidth;
    private int screenHeight;

    /** projection and camera components **/
    private Camera camera, sceneCamera;
    
    /** text components **/
    private SpriteFont fontTitle, fontMenu;
    private TextBuffer textTitle;
    private Matrix4x4 matrixTitle;
    private TextBuffer[] textMainMenu, textCreditsMenu, textHighscoreMenu;
    private Matrix4x4[] matrixMainMenu, matrixCreditsMenu, matrixHighscoreMenu;
    
    /** 3d hero object for the menu **/
    private Hero hero;
    
    /** collision detection components **/
    private AxisAlignedBoundingBox[] aabbMainMenu, aabbCreditsMenu, aabbHighscoreMenu;
   
    /** defualt menu mode **/
    private MenuMode mode = MenuMode.MAINMENU;

    private Vibrator vibrator = null;

    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    public MenuScreen(Context context,
                      GraphicDevice graphicDevice,
                      Renderer renderer,
                      int screenWidth,
                      int screenHeight)
    {
        this.context = context;
        this.graphicDevice = graphicDevice;
        this.renderer = renderer;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize()
    {
        // get reference of the device vibrator to support vibration
        try
        {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "Error no vibrator supported!");
        }
	
        // get hero instance
        hero = Hero.getInstance();
	
        Matrix4x4 projection = new Matrix4x4();
        Matrix4x4 view = new Matrix4x4();
	
        // CAMERA: define projection type, create a new camera and link it to the view
        projection = new Matrix4x4();							                        // create new projection matrix
        projection.setOrthogonalProjection(-250f, 350f, -250f, 250f, 0.0f, 100.0f);	    // define projection parameters
        view = new Matrix4x4();								                            // create new view matrix
        camera = new Camera();								                            // create new camera
        camera.setProjection(projection);						                        // set projection
        camera.setView(view);								                            // set view
	
	
        // scene camera for the hero object in the menu
        projection = new Matrix4x4();							                        // create new projection matrix for the hero
        projection.setPerspectiveProjection(-0.1f, 0.1f, -0.1f, 0.1f, 0.1f, 16.0f);	    // define perspectival projection parameters
        sceneCamera = new Camera();							                            // create new camera for the hero
        sceneCamera.setProjection(projection);						                    // set projection
        sceneCamera.setView(view);							                            // set view

        // collision boxes for the different main menu entries
        aabbMainMenu = new AxisAlignedBoundingBox[] {
            new AxisAlignedBoundingBox(80, -50, 220, 30),				                // collision box for the 1st entry
            new AxisAlignedBoundingBox(105, -120, 140, 30),				                // collision box for the 2nd entry
            new AxisAlignedBoundingBox(105, -190, 140, 30),				                // collision box for the 3rd entry
        };
	
        // collision boxes for the different creadits menu entries
        aabbCreditsMenu = new AxisAlignedBoundingBox[] {
            new AxisAlignedBoundingBox(0, 0, 0, 16),				                    // collision box for the 1st entry
            new AxisAlignedBoundingBox(0, 0, 0, 16),				                    // collision box for the 2nd entry
            new AxisAlignedBoundingBox(0, 0, 0, 16),				                    // collision box for the 3rd entry
            new AxisAlignedBoundingBox(0, 0, 0, 16),				                    // collision box for the 4th entry
            new AxisAlignedBoundingBox(250, -200, 140, 20)				                // collision box for the 5th entry
        };
	
        // collision boxes for the different highscore menu entries
        aabbHighscoreMenu = new AxisAlignedBoundingBox[] {
            new AxisAlignedBoundingBox(0, 0, 0, 16),
            new AxisAlignedBoundingBox(0, 0, 0, 16),
            new AxisAlignedBoundingBox(0, 0, 0, 16),
            new AxisAlignedBoundingBox(0, 0, 0, 16),
            new AxisAlignedBoundingBox(0, 0, 0, 16),
            new AxisAlignedBoundingBox(0, 0, 0, 16),
            new AxisAlignedBoundingBox(250, -200, 140, 20),
        };
	
        // position of the main menu texts
        matrixMainMenu = new Matrix4x4[] {
            Matrix4x4.createTranslation(80, -50, 0),				                    // position of the 1st MAINMENU matrix
            Matrix4x4.createTranslation(110, -120, 0),				                    // position of the 2nd MAINMENU matrix
            Matrix4x4.createTranslation(110, -190, 0),				                    // position of the 3rd MAINMENU matrix
        };
	
        // position of the credits menu texts
        matrixCreditsMenu = new Matrix4x4[] {
            Matrix4x4.createTranslation(170, 160, 0),				                    // position of the 1st CREDITSMENU matrix
            Matrix4x4.createTranslation(-180, 0, 0),				                    // position of the 2nd CREDITSMENU matrix
            Matrix4x4.createTranslation(-140, -60, 0),				                    // position of the 3rd CREDITSMENU matrix
            Matrix4x4.createTranslation(-50, -120, 0),				                    // position of the 4th CREDITSMENU matrix
            Matrix4x4.createTranslation(250, -200, 0)				                    // position of the 5th CREDITSMENU matrix
        };
	
        // position of the highscore menu texts
        matrixHighscoreMenu = new Matrix4x4[] {
            Matrix4x4.createTranslation(170, 160, 0),
            Matrix4x4.createTranslation(-125, 20, 0),				                    // 2nd entry == 1st place
            Matrix4x4.createTranslation(-138, -30, 0),
            Matrix4x4.createTranslation(-132, -80, 0),
            Matrix4x4.createTranslation(-130, -130, 0),
            Matrix4x4.createTranslation(-130, -180, 0),
            Matrix4x4.createTranslation(250, -200, 0)				                    // back button
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadContent()
    {
        // MENU: create text content
        fontTitle = graphicDevice.createSpriteFont(null, 64);				            // title of the game - font size 64px;
        fontMenu = graphicDevice.createSpriteFont(null, 32);				            // create menu of the game - font size 32px;
	
	
        textTitle = graphicDevice.createTextBuffer(fontTitle, 16);			            // buffer for the title - max. 16 chars
        // create a buffer for the menu entries
        textMainMenu = new TextBuffer[] {
            graphicDevice.createTextBuffer(fontMenu, 16),				                // 1st menu entry max. 16 chars
            graphicDevice.createTextBuffer(fontMenu, 16),				                // 2nd menu entry max. 16 chars
            graphicDevice.createTextBuffer(fontMenu, 16),				                // 3rd menu entry max. 16 chars
        };
	
        // create a buffer for the credits menu entries
        textCreditsMenu = new TextBuffer[] {
            graphicDevice.createTextBuffer(fontMenu, 8),				                // 1st menu entry max. 8 chars
            graphicDevice.createTextBuffer(fontMenu, 35),				                // 2nd menu entry max. 35 chars
            graphicDevice.createTextBuffer(fontMenu, 25),				                // 3rd menu entry max. 25 chars
            graphicDevice.createTextBuffer(fontMenu, 16),				                // 4th menu entry max. 16 chars
            graphicDevice.createTextBuffer(fontMenu, 16)				                // 5th menu entry max. 16 chars
        };
	
        // create a buffer for the highscore menu entries
        textHighscoreMenu = new TextBuffer[] {
            graphicDevice.createTextBuffer(fontMenu, 10),				                // 1st menu entry: Highscore
            graphicDevice.createTextBuffer(fontMenu, 5),				                // 2nd menu entry: 1st place
            graphicDevice.createTextBuffer(fontMenu, 5),				                // 3rd menu entry: 2nd place
            graphicDevice.createTextBuffer(fontMenu, 5),				                // 4th menu entry: 3rd place
            graphicDevice.createTextBuffer(fontMenu, 5),				                // 5th menu entry: 4th place
            graphicDevice.createTextBuffer(fontMenu, 5),				                // 6th menu entry: 5th place
            graphicDevice.createTextBuffer(fontMenu, 16) 				                // back button
        };
	
        // set title of the game
        textTitle.setText("aCARde Run");
	
        // texts of the main menu
        textMainMenu[0].setText("Start aCARde Run");
        textMainMenu[1].setText("Ranking");
        textMainMenu[2].setText("Credits");
	
	    // position of the title
        matrixTitle = Matrix4x4.createTranslation(-250, 160, 0);			            // postition of the title matrix
	
        // texts of the credits menu
        textCreditsMenu[0].setText("Credits");						                    // 1st position
        textCreditsMenu[1].setText("Hochschule der Medien Stuttgart");			        // 2nd position
        textCreditsMenu[2].setText("Mobile Game Development");				            // 3rd position
        textCreditsMenu[3].setText("Dennis Grewe");					                    // 4th position
        textCreditsMenu[4].setText("Back");						                        // 5th position

        // text of the highscore menu
        textHighscoreMenu[0].setText("Ranking");
        textHighscoreMenu[1].setText("1st: ");
        textHighscoreMenu[2].setText("2nd: ");
        textHighscoreMenu[3].setText("3rd: ");
        textHighscoreMenu[4].setText("4th: ");
        textHighscoreMenu[5].setText("5th: ");
        textHighscoreMenu[6].setText("Back");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(float deltaSeconds, InputSystem inputSystem)
    {
	
	    InputEvent inputEvent = inputSystem.peekEvent();				// define new peek event from the input system

        /**
         * check for new input events using an infinite while loop
         */
        while(inputEvent != null)
        {

            switch(inputEvent.getDevice())
            {
                case TOUCHSCREEN:
                    switch(inputEvent.getAction()) {
                        case DOWN:
                            Vector3 screenTouchPosition = new Vector3(
                                (inputEvent.getValues()[0] / (screenWidth / 2) -1),
                                -(inputEvent.getValues()[1] / (screenHeight / 2) - 1), 0);

                            Vector3 worldTouchPosition = camera.unproject(screenTouchPosition, 1);

                            Point touchPoint = new Point(worldTouchPosition.getX(), worldTouchPosition.getY());

                            switch (mode)
                            {
                                case MAINMENU:
                                 for (int i = 0; i < aabbMainMenu.length; i++)
                                 {
                                    if(touchPoint.intersects(aabbMainMenu[0]))
                                    {
                                        // create a new game instance and register it to the renderer
                                        if(aCARdeRunGame.getGameState() == GameState.MENU && !InGameScreen.IsGameStarted())
                                        {
                                            try
                                            {
                                                aCARdeRunGame.setGameState(GameState.GAME);
                                                Ranking.rankingPushed = false;
                                            }
                                            catch (Exception e)
                                            {
                                                Log.e(TAG, "Can�t set GAMESTATE to GAME and create new game!");
                                            }
                                        }

                                        // play click sound once
                                        if(aCARdeRunGame.getSoundpool() != null)
                                        {
                                            aCARdeRunGame.getSoundpool().play(aCARdeRunGame.getClickSound(), 1, 1, 0, 0, 1);
                                        }

                                        // vibriate 20 ms
                                        vibrator.vibrate(20);
                                    }
                                    else if(touchPoint.intersects(aabbMainMenu[1]))
                                    {
                                        // set menu mode to HIGHSCOREMENU
                                        mode = MenuMode.RANKINGMENU;

                                        // play click sound once
                                        if(aCARdeRunGame.getSoundpool() != null)
                                        {
                                            aCARdeRunGame.getSoundpool().play(aCARdeRunGame.getClickSound(), 1, 1, 0, 0, 1);
                                        }

                                        // vibrate 20 ms
                                        vibrator.vibrate(20);
                                    }
                                    else if(touchPoint.intersects(aabbMainMenu[2]))
                                    {
                                        // set menu mode to CREDITSMENU
                                        mode = MenuMode.CREDITSMENU;

                                        // play click sound
                                        if(aCARdeRunGame.getSoundpool() != null)
                                        {
                                            aCARdeRunGame.getSoundpool().play(aCARdeRunGame.getClickSound(), 1, 1, 0, 0, 1);
                                        }

                                        // vibrate 20 ms
                                        vibrator.vibrate(20);
                                    }
                                 }
                                break;

                                case CREDITSMENU:
                                    for (int i = 0; i < aabbCreditsMenu.length; i++)
                                    {
                                        if(touchPoint.intersects(aabbCreditsMenu[4]))
                                        {
                                            // menu mode back to MAINMENU
                                            mode = MenuMode.MAINMENU;

                                            // play click sound once
                                            if(aCARdeRunGame.getSoundpool() != null)
                                            {
                                                aCARdeRunGame.getSoundpool().play(aCARdeRunGame.getClickSound(), 1, 1, 0, 0, 1);
                                            }

                                            // vibrate 20 ms
                                            vibrator.vibrate(20);
                                        }
                                    }
                                break;

                                case RANKINGMENU:
                                    for (int i = 0; i < aabbHighscoreMenu.length; i++)
                                    {
                                        if(touchPoint.intersects(aabbHighscoreMenu[6]))
                                        {
                                            // reset menu mode to MAINMENU
                                            mode = MenuMode.MAINMENU;

                                            // play click sound once
                                            if(aCARdeRunGame.getSoundpool() != null)
                                            {
                                                aCARdeRunGame.getSoundpool().play(aCARdeRunGame.getClickSound(), 1, 1, 0, 0, 1);
                                            }

                                            // vibrate 20 ms
                                            vibrator.vibrate(20);
                                        }
                                    }
                                    break;
                            }
                        }
                        break;
            }
	    
            inputSystem.popEvent();
            inputEvent = inputSystem.peekEvent();
	    }
	
        // call update method of the hero
        hero.update(deltaSeconds);
    }

    @Override
    public void draw(float deltaSeconds)
    {
        // clear display
        graphicDevice.clear(0.0f, 0.0f, 0.0f, 0.0f, 1.0f);

	    switch (mode)
        {
	        case MAINMENU:									                // if mode == MAINMENU
	            graphicDevice.setCamera(sceneCamera);
	            hero.draw(deltaSeconds);
	    
	            // draw text on the display
		        graphicDevice.setCamera(camera);						    // set camera
		        renderer.drawText(textTitle, matrixTitle);					// draw title of the game
		        for(int i = 0; i < textMainMenu.length; i++ )               // draw all MAINMENU components
                {
		            renderer.drawText(textMainMenu[i], matrixMainMenu[i]);
		        }
	            break;

	        case CREDITSMENU:                                               // if mode == CREDITSMENU
		        graphicDevice.setCamera(camera);
		        renderer.drawText(textTitle, matrixTitle);
	            for(int i = 0; i < textCreditsMenu.length; i++ )
                {
		            renderer.drawText(textCreditsMenu[i], matrixCreditsMenu[i]);
	            }
	            break;

            case RANKINGMENU:                                                // if mode == HIGHSCOREMENU
		        graphicDevice.setCamera(camera);
		        renderer.drawText(textTitle, matrixTitle);

	            for (int i = 0; i < textHighscoreMenu.length; i++)
                {
		            renderer.drawText(textHighscoreMenu[i], matrixHighscoreMenu[i]);
	            }
	            Ranking.getInstance().draw(deltaSeconds);
	            break;
	    }
    }
}
