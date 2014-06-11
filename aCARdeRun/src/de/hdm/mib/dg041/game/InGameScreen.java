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

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.util.Log;
import de.hdm.mib.dg041.graphics.GraphicDevice;
import de.hdm.mib.dg041.graphics.Renderer;
import de.hdm.mib.dg041.input.InputEvent;
import de.hdm.mib.dg041.input.InputSystem;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class InGameScreen extends GameScreen
{
    // --------------------------------------------------------
    // INNERCLASS TIMER THREAD
    // --------------------------------------------------------

    /**
     * This class defines a timer thread. This timer instance will be used to collect time information
     * during the game like elapsed milliseconds, etc.
     *
     * @author dennis.grewe [dg041@hdm-stuttgart.de]
     * Created on 23.02.2012.
     */
    private final class TimerThread extends TimerTask
    {
        public void run()
        {
            synchronized (monitorObject)
            {
                if (milliSec < 4000)
                {
                    // draw start text on HUD in the first four seconds
                    hud.setStartText(milliSec);
                    milliSec += 1000 ;
                }

                if (milliSec >= 4000)
                {
                    // start the game fter the first four seconds
                    gameStarted = true;
                    try
                    {
                        // increase elapsed second counter after each second
                        milliSec += 1000;
                        hud.setTimerTime(milliSec-4000);	// set elapsed time to the hud
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        Log.e(TAG, "Cannot write Time to HUD, because HUD == NULL!");
                    }
                }
            }
        }
    }

    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private static String TAG = InGameScreen.class.getName();

    /**
     * This flag indicates if the game can be started. Other components like the world
     * or the HUD need this information as well.
     */
    private static boolean gameStarted = false;

    private boolean isMediaPlayerStarted = false;
    
    /** timer thread components **/
    private final Object monitorObject = new Object();
    private TimerThread timerThread;
    private Timer timer;
    private int milliSec = 0;
    
    /** references to game objects **/
    private World world;
    private HUD hud;
    private Hero hero;
    private Obstacle obstacle;
    
    /** components to display game elements **/
    private Context context;
    private GraphicDevice graphicDevice;
    private Renderer renderer;
    private int screenWidth;
    private int screenHeight;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    /**
     * @param context
     *      the context of the android application
     * @param graphicDevice
     *      the graphic device reference to handle device inputs
     * @param renderer
     *      the reference to the renderer object to render elements on the screen
     * @param screenWidth
     *      the screen width resolution in pixels
     * @param screenHeight
     *      the screen height resolution in pixels
     */
    public InGameScreen(Context context,
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
        this.timer = new Timer();			// init new timer object to schedule timer thread
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
        // init all game componenst and pass the graphic device
        world = new World(context, graphicDevice, renderer);
        hud = new HUD(context, graphicDevice, renderer);
        hero = Hero.getInstance();
        obstacle = new Obstacle(context, graphicDevice, renderer);

        // call init method of each game component to init their required components
        world.initialize();
        hud.initialize();
        obstacle.initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadContent()
    {
	    // call load content method of each game component to load the content for their required components
		world.loadContent();
		hud.loadContent();
		obstacle.loadContent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(float deltaSeconds, InputSystem inputSystem)
    {
	    // process input events and delegate the game information to the correct component
	    InputEvent inputEvent = inputSystem.peekEvent();
	
        while (inputEvent != null)
        {
            switch (inputEvent.getDevice())
            {
                // check for touch event
                case TOUCHSCREEN:
                    switch(inputEvent.getAction())
                    {
                        case DOWN:
                        // call handle input method of hud class
                        hud.handleInputEvent(inputEvent, screenWidth, screenHeight);
                        break;
                    }
                    break;

                // check for gravity event
                case GRAVITY:
                    // call handle input method of hero class
                    hero.handleInputEvent(inputEvent);
                    break;
            }

            inputSystem.popEvent();                 // clear event queue of the input system for processed event
            inputEvent = inputSystem.peekEvent();
        }

        // check if game state == started --> start timer task and pass time to hud
        if (!gameStarted && !HUD.getAccidentHappened())
        {
            if(!isMediaPlayerStarted)
            {
                // start media player
                aCARdeRunGame.StartMediaPlayer();
                isMediaPlayerStarted = true;
            }

            try
            {
                if (timer == null)
                {
                    timer = new Timer();
                }
                if(timerThread == null)
                {
                    // start time thread and schedule it each 1000 ms
                    synchronized (monitorObject) {
                    timerThread = new TimerThread();
                    timer.schedule(timerThread, 1000, 1000);
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG, "Problem to start Timer AND/OR TimerThread!");
            }
        }
	
        // check if collision happened
        if (gameStarted && checkCollision())
        {
            try
            {
                stopTimerTask();				    // stop timer thread
                gameStarted = false;			    // set INGAMESTATE == false
                isMediaPlayerStarted = false;
                HUD.setAccidentHappend(true);		// call hud that collision was happened
                milliSec = 0;				        // set timer milliSec == 0
                world.setWorldSpeed(0.05f);			// reset world speed to default value
                obstacle.setObstacleSpeed(0.05f);	// reset obstacle speed to default value
                Log.d(TAG, "Collision detected!");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Log.e(TAG, "Problem to stop TimerTask!");
            }
        }
	
	
	// check level of difficulty
		levelOfDifficulty();
		
	
	// call update methods of the game components
		world.update(deltaSeconds);
		hud.update(deltaSeconds);
		hero.update(deltaSeconds);
		obstacle.update(deltaSeconds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void draw(float deltaSeconds)
    {
	    // call draw method of the game components
		world.draw(deltaSeconds);
		hud.draw(deltaSeconds);
		obstacle.draw(deltaSeconds);
		hero.draw(deltaSeconds);
    }
    
    /** Methode ueberprueft ob eine Kollision zwischen dem Fahrzeug 
      * und dem Enemy aufgetreten ist. Die Methode wird regelmaessig
      * von der Update-Methode aufgerufen **/
    private boolean checkCollision()
    {
        /* collision happens if the hero is on the same lane like the enemy and the bounding boxes of
         * the components are overlapped */
        if (hero.positionHeroIsRight() == obstacle.positionObstacleIsRight() &&
            obstacle.getZValue() == 18.0f)
        {
            // collision detected - play crash sound - reset the hero position to pole position
            obstacle.resetZValue();
            if(aCARdeRunGame.getSoundpool() != null)
            {
                aCARdeRunGame.getSoundpool().play(aCARdeRunGame.getCrashSound(), 1, 1, 0, 0, 1);
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    

    /**
     * This method increases the difficulty level depending on the elapsed time.
     * This is evidenced by the faster moving of game objects in the game. The level
     * of increasing is kind of log(x)
     */
    private void levelOfDifficulty() {
	
        switch (milliSec)
        {
            // 00:30 m have passed
            case 35000:
                world.setWorldSpeed(0.1f);		    // double speed
                obstacle.setObstacleSpeed(0.1f);	// double speed
                break;
             // 01:00 m have passed
            case 65000:
                world.setWorldSpeed(0.20f);		    // double speed again
                obstacle.setObstacleSpeed(0.20f);	// double speed again
                break;
            // 01:30  m have passed
            case 95000:
                world.setWorldSpeed(0.25f);		    // increase speed of 0.05f
                obstacle.setObstacleSpeed(0.25f);	// increase speed of 0.05f
                break;
             // 02:00 m have passed
            case 125000:
                world.setWorldSpeed(0.35f);		    // increase speed of 0.1f
                obstacle.setObstacleSpeed(0.35f);	// increase speed of 0.1f
                break;
             // 02:30 m have passed
            case 155000:
                world.setWorldSpeed(0.40f);		    // increase speed of 0.05f
                obstacle.setObstacleSpeed(0.40f);	// increase speed of 0.05f
                break;
             // 03:00 m have passed
            case 185000:
                world.setWorldSpeed(0.55f);		    // increase speed of 0.15f
                obstacle.setObstacleSpeed(0.55f);	// increase speed of 0.15f
                break;
             // 03:30 m have passed
            case 215000:
                world.setWorldSpeed(0.65f);		    // increase speed of 0.1f
                obstacle.setObstacleSpeed(0.65f);	// increase speed of 0.1f
                break;
             // 04:00 m have passed
            case 245000:
                world.setWorldSpeed(0.75f);		    // increase speed of 0.1f
                obstacle.setObstacleSpeed(0.75f);	// increase speed of 0.1f
                break;
            // 04:30 m have passed
            case 275000:
                world.setWorldSpeed(0.90f);		    // increase speed of 0.1f
                obstacle.setObstacleSpeed(0.90f);	// increase speed of 0.1f
                break;
            default:
                break;
        }
    }

    /**
     * This method stops the timer task if it was started and reset the references back to null values.
     */
    private synchronized void stopTimerTask()
    {
        if (timerThread != null)
        {
            timerThread.cancel();
            timerThread = null;
        }
        if (timer != null)
        {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * @return TRUE if the game was already started, otherwise FALSE and we are in the ready state of the game.
     */
    public static boolean IsGameStarted()
    {
	    return gameStarted;
    }

    /**
     * @param gameStarted set the game started falg - TRUE == game started, otherwise FALSE
     */
    public static void setIsGameStarted(boolean gameStarted)
    {
	    InGameScreen.gameStarted = gameStarted;
    }
}