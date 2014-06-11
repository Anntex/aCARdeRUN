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

import de.hdm.mib.dg041.collision.AxisAlignedBoundingBox;
import de.hdm.mib.dg041.collision.Point;
import de.hdm.mib.dg041.game.aCARdeRunGame.GameState;
import de.hdm.mib.dg041.graphics.Camera;
import de.hdm.mib.dg041.graphics.GraphicDevice;
import de.hdm.mib.dg041.graphics.Renderer;
import de.hdm.mib.dg041.graphics.SpriteFont;
import de.hdm.mib.dg041.graphics.TextBuffer;
import de.hdm.mib.dg041.input.InputEvent;
import de.hdm.mib.dg041.math.Matrix4x4;
import de.hdm.mib.dg041.math.Vector3;
import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

/**
 * This class defines a head up display to display the different ingame information.
 * This information could be text, timer and the crash screen.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class HUD
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private static String TAG = HUD.class.getName();

    private Camera hudCamera;
    private SpriteFont fontTimeHUD, fontStartHUD, fontCrashHUD, fontAccident;
    private TextBuffer[] textTimeHUD, textStartHUD, textCrashHUD, textCrashHUDBest;
    private Matrix4x4[] matrixTimeHUD, matrixStartHUD, matrixCrashHUD, matrixCrashHUDBest;
    private int timeElapsed;
    private int totalTimeElapsed;
    private String timer;
    private AxisAlignedBoundingBox[] aabbHUD;
    private static boolean accidentHappened = false;

    private Context context;
    private GraphicDevice graphicDevice;
    private Renderer renderer;

    /**
     * support vibration sensor of the device
     */
    private Vibrator vibrator = null;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public HUD(Context context, GraphicDevice graphicDevice, Renderer renderer)
    {
        this.context = context;
        this.graphicDevice = graphicDevice;
        this.renderer = renderer;
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * This method initializes the hud and creates all required dependencies and objects.
     */
    public void initialize()
    {
	    // get vibrator instance from android context
        try
        {
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "Error no vibrator supported!");
        }

        Matrix4x4 projection = new Matrix4x4();
        Matrix4x4 view = new Matrix4x4();

        // HUDCAMERA:	define the projection type and create a new instance of a HUDCAMERA
        projection = new Matrix4x4();							                    // create new projection matrix
        projection.setOrthogonalProjection(-100f, 100f, -100f, 100f, 0.0f, 100.0f);	// define projection parameters
        view = new Matrix4x4();								                        // create new view matrix
        view.translate(0, -1, 0);							                        // invert view matrix of camera to y-axis

        hudCamera = new Camera();							                        // create new HUDCAMERA
        hudCamera.setProjection(projection);						                // set projection to camera
        hudCamera.setView(view);							                        // set view to camera

	// TIMER HUD

        // timer matrix
        matrixTimeHUD = new Matrix4x4[] {
            Matrix4x4.createTranslation(27, 79, 0),					                // position of the 1st timer text
            Matrix4x4.createTranslation(48, 79, 0)					                // position of the 2nd timer text
        };
        	
        // START HUD
        	
        // position start HUD elements
        matrixStartHUD = new Matrix4x4[] {
            Matrix4x4.createTranslation(-38, 0, 0),
            Matrix4x4.createTranslation(-22, 0, 0),
            Matrix4x4.createTranslation(-20, 0, 0)
        };
        	
       // CRASH HUD
        	
        // position of the crash hud elements
        matrixCrashHUD = new Matrix4x4[] {
            Matrix4x4.createTranslation(-52, 45, 0),
            Matrix4x4.createTranslation(-80, 10, 0),
            Matrix4x4.createTranslation(20, 10, 0),
            Matrix4x4.createTranslation(-80, -20, 0),
            Matrix4x4.createTranslation(-80, -65, 0),
            Matrix4x4.createTranslation(35, -65, 0),
        };
        	
        // position of the best time element on the crash hud
        matrixCrashHUDBest = new Matrix4x4[] {
            Matrix4x4.createTranslation(20, -20, 0),
        };
        	
        // collision box of the hud
        aabbHUD = new AxisAlignedBoundingBox[] {
            new AxisAlignedBoundingBox(0, 0, 0, 0),
            new AxisAlignedBoundingBox(0, 0, 0, 0),
            new AxisAlignedBoundingBox(0, 0, 0, 0),
            new AxisAlignedBoundingBox(0, 0, 0, 0),
            new AxisAlignedBoundingBox(0, 0, 0, 0),
            new AxisAlignedBoundingBox(-80, -65, 50, 16),
            new AxisAlignedBoundingBox(35, -65, 50, 16),
        };
    }
    
    /**
     * This method loads the required content of all kinds of huds in the game.
     */
    public void loadContent()
    {
	
    // HUD: create the text content of the hud
        fontTimeHUD = graphicDevice.createSpriteFont(null, 14);				// font size of the time texts: 14px;
        fontStartHUD = graphicDevice.createSpriteFont(null, 28);			// font size of the start texts: 24px:
        fontAccident = graphicDevice.createSpriteFont(null, 24);			// font size of the accident texts: 24px;
        fontCrashHUD = graphicDevice.createSpriteFont(null, 18);			// font size of the crash texts: 20px;

	
	// TIMER HUD

        // create text buffer for all time elements
        textTimeHUD = new TextBuffer[] {		        				// create textbuffer object
            graphicDevice.createTextBuffer(fontTimeHUD, 16),			// space for INHALT "00:"
            graphicDevice.createTextBuffer(fontTimeHUD, 10)				// space for INHALT "TIME ELAPSED"
        };

        // define time strings
        textTimeHUD[0].setText("00:");							        // Text: "00:"
        textTimeHUD[1].setText("00:00");						        // Text: time elapsed
	
    // START HUD
                	
        // create text buffer for all start strings
        textStartHUD = new TextBuffer[] {
            graphicDevice.createTextBuffer(fontStartHUD, 8),			// space for INHALT "READY?"
            graphicDevice.createTextBuffer(fontStartHUD, 5),			// space for INHALT "SET!"
            graphicDevice.createTextBuffer(fontStartHUD, 5)				// space for INAHLT "GO!"
        };

        // start strings
        textStartHUD[0].setText("Ready?");
        textStartHUD[1].setText("Set");
        textStartHUD[2].setText("GO!!");
        
    // CRASH HUD
             	
        // text buffer for all crash elements in the hud
        textCrashHUD = new TextBuffer[] {
            graphicDevice.createTextBuffer(fontAccident, 10),			// space for Inhalt "CRASH!"
            graphicDevice.createTextBuffer(fontCrashHUD, 8),			// space for INHALT "Time:"
            graphicDevice.createTextBuffer(fontCrashHUD, 8),			// space for INHALT "<timeElapsed>"
            graphicDevice.createTextBuffer(fontCrashHUD, 12),			// space for INHALT "Best Time:"
            graphicDevice.createTextBuffer(fontCrashHUD, 8),			// space for INHALT "RESTART" - Button
            graphicDevice.createTextBuffer(fontCrashHUD, 5)				// space for INHALT "Menu" - Button
        };

        // extra buffer for best time
        textCrashHUDBest = new TextBuffer[] {
            graphicDevice.createTextBuffer(fontCrashHUD, 8)
        };

        // define crash strings
        textCrashHUD[0].setText("ACCIDENT");
        textCrashHUD[1].setText("Time:");
        textCrashHUD[2].setText("");
        textCrashHUD[3].setText("Best Time:");
        textCrashHUD[4].setText("Restart");
        textCrashHUD[5].setText("Menu");
    }
    
    /**
     * This method will be called in a certain period of time to update the display content.
     *
     * @param deltaseconds delta time to update the display content, in milliseconds
     */
    public void update(float deltaseconds)
    {
         // if the game state == started update the timer time on HUD
        if(InGameScreen.IsGameStarted())
        {
            textTimeHUD[1].setText(timer);
        }

        // if the game state == stopped draw timer time to CRASHHUD
        if (!InGameScreen.IsGameStarted() && accidentHappened)
        {
            textCrashHUD[2].setText(timer);
        }
    }

    /**
     * This method redraws all display content in a certain period of time.
     *
     * @param deltaseconds delta time to redraw the display elements, in milliseconds
     */
    public void draw(float deltaseconds)
    {
	    // draw text on HUD
	    graphicDevice.setCamera(hudCamera);

        // if the game wasn´t started already draw the start text
        if (!InGameScreen.IsGameStarted() && !accidentHappened)
        {
            if (timeElapsed == 0 )
            {
                renderer.drawText(textStartHUD[0], matrixStartHUD[0]);
            }
            if (timeElapsed == 1000)
            {
                renderer.drawText(textStartHUD[1], matrixStartHUD[1]);
            }
            if (timeElapsed > 1000 && timeElapsed < 3000)
            {
                renderer.drawText(textStartHUD[2], matrixStartHUD[2]);
            }
        }

        // if the game state == stopped and a collision has happened, draw the CRASHHUD
        if(!InGameScreen.IsGameStarted() && accidentHappened)
        {
            for (int i = 0; i < textCrashHUD.length; i++)
            {
                renderer.drawText(textCrashHUD[i], matrixCrashHUD[i]);
            }

            // consider if a best time was reached
                Ranking.getInstance().pushRanking(totalTimeElapsed);
            // collect the best time from resource
                int bestTime = Ranking.getInstance().getBestTime();
                textCrashHUDBest[0].setText(Ranking.getInstance().timeFormatter(bestTime));
            // draw the best time on screen
                renderer.drawText(textCrashHUDBest[0], matrixCrashHUDBest[0]);
        }
	
        // if game state == started draw timer content
        if (InGameScreen.IsGameStarted() && !accidentHappened)
        {
            for(int i = 0; i < textTimeHUD.length; i++ )
            {
                renderer.drawText(textTimeHUD[i], matrixTimeHUD[i]);				// draw all HUD elements
            }
        }
    }

    /**
     * This method handles all kinds of input form the device during the game was started.
     * If the game is in MENU mode the supported input type is TOUCH. If the game is in
     * GAME mode the supported input type is ACCELEROMETER.
     *
     * @param inputEvent
     *          type of the input event
     * @param screenWidth
     *          the width of the screen in pixels
     * @param screenHeight
     *          the height of the screen in pixels
     */
    public void handleInputEvent(InputEvent inputEvent, int screenWidth, int screenHeight)
    {
    
        Vector3 screenTouchPosition = new Vector3((inputEvent.getValues()[0] / (screenWidth / 2) -1),
                -(inputEvent.getValues()[1] / (screenHeight / 2) - 1), 0);
	
	    Vector3 worldTouchPosition = hudCamera.unproject(screenTouchPosition, 1);
	    
	    Point touchPoint = new Point(worldTouchPosition.getX(), worldTouchPosition.getY());
	    
        for (int i = 0; i < aabbHUD.length; i++)
        {
            if(touchPoint.intersects(aabbHUD[5]))
            {
                try
                {
                    InGameScreen.setIsGameStarted(false);
                    accidentHappened = false;
                    Ranking.rankingPushed = false;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    Log.e(TAG, "Error - Restart Game failed!");
                }

                // play click sound once
                if(aCARdeRunGame.getSoundpool() != null)
                {
                    aCARdeRunGame.getSoundpool().play(aCARdeRunGame.getClickSound(), 1, 1, 0, 0, 1);
                }

                // vibrate 20ms
                vibrator.vibrate(20);
            }
            else if(touchPoint.intersects(aabbHUD[6]))
            {
                // back to the menu
                if(aCARdeRunGame.getGameState() == GameState.GAME && !InGameScreen.IsGameStarted())
                {
                    try
                    {
                        aCARdeRunGame.setGameState(GameState.MENU);
                        aCARdeRunGame.StartMediaPlayer();			// start media player for the game menu
                        accidentHappened = false;
                    }
                    catch (Exception e)
                    {
                        Log.e(TAG, "Can´t set GAMESTATE to GAME and create new game!");
                    }
                }

                // play click sound once
                if(aCARdeRunGame.getSoundpool() != null)
                {
                    aCARdeRunGame.getSoundpool().play(aCARdeRunGame.getClickSound(), 1, 1, 0, 0, 1);
                }

                // vibrate 20ms
                vibrator.vibrate(20);
            }
        }
    }

    // --------------------------------------------------------
    // GETTER & SETTER
    // --------------------------------------------------------

    /**
     * @param milliSec time to set to the timer HUD element
     */
    public void setTimerTime(int milliSec)
    {
        totalTimeElapsed = milliSec;
        try
        {
            timer = Ranking.getInstance().timeFormatter(milliSec);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e(TAG, "ERROR Integer overflow after 6 Minutes of Gameplay!");
        }
    }

    /**
     * @return method return the elapsed timer time as string
     */
    public String getTimerTime()
    {
	    return timer;
    }

    /**
     * @param milliSec set the start timer time
     */
    public void setStartText(int milliSec)
    {
	    timeElapsed = milliSec;
    }

    /**
     * @return TRUE if an accident happend, otherwise FALSE.
     */
    public static boolean getAccidentHappened()
    {
	    return accidentHappened;
    }

    /**
     * @param accidentHappened sets if an accident was happend.
     */
    public static void setAccidentHappend(boolean accidentHappened)
    {
	    HUD.accidentHappened = accidentHappened;
    }
}
