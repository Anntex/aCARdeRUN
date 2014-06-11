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

import de.hdm.mib.dg041.android.R;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.view.View;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class aCARdeRunGame extends Game
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    /**
     * Possible game states
     */
    public enum GameState
    {
	    MENU,
	    GAME,
    }

    /**
     * defines the current state of the game
     */
    private static GameState currentGameState;
    
    /** Game components **/
    private MenuScreen menu;
    private InGameScreen game;
    private Hero hero;
    private Ranking ranking;
    
    /** sound components **/
    private static MediaPlayer mediaPlayer;
    private static boolean isPlaying = false;
    private static SoundPool soundPool;
    private static int clickSound;
    private static int crashSound;


    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public aCARdeRunGame(View view)
    {
	    super(view);
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    @Override
    public void initialize()
    {
        // create new game components
        menu = new MenuScreen(context, graphicDevice, renderer, screenWidth, screenHeight);
        game = new InGameScreen(context, graphicDevice, renderer, screenWidth, screenHeight);
        hero = Hero.getInstance();
        ranking = Ranking.getInstance();

        menu.initialize();
        game.initialize();
        hero.initialize(context, graphicDevice, renderer);
        ranking.initialize(context, graphicDevice, renderer);

        // default game state == MENU
        currentGameState = GameState.MENU;

        // create a new soundpool, number of tracks, quality of sound, etc.
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }

    @Override
    public void loadContent() {
	
	// call load content of the game components
		menu.loadContent();
		
		game.loadContent();
		
		hero.loadContent();
		
		ranking.loadContent();
	
	// SOUNDPOOL & MediaPlayer
		
		StartMediaPlayer();
		
        // load clicksound
        clickSound = soundPool.load(context, R.raw.menuclick, 1); // load sound and define priority
        crashSound = soundPool.load(context, R.raw.crash, 1);
	
    }

    /**
     * This method will be called in regular periods to update the state of the game.
     *
     * @param deltaseconds
     *          time intervall in milliseconds
     */
    @Override
    public void update(float deltaseconds)
    {
        switch (currentGameState)
        {
        case MENU:
            menu.update(deltaseconds, inputSystem);
            break;
        case GAME:
            game.update(deltaseconds, inputSystem);
            break;
        }
    }

    /**
     * The method draws the game content on the display in regular period.
     *
     * @param deltaseconds
     *          time intervall in milliseconds
     */
    @Override
    public void draw(float deltaseconds)
    {
        switch (currentGameState) {
        case MENU:
            menu.draw(deltaseconds);
            break;
        case GAME:
            game.draw(deltaseconds);
            break;
        }
    }

    @Override
    public void resize(int width, int height)
    {
        // this method is not needed because the game will be displayed in landscape mode only
    }

    @Override
    public void pause()
    {
        if(mediaPlayer != null && isPlaying)
            mediaPlayer.pause();
    }

    @Override
    public void resume()
    {
        if(mediaPlayer != null && !isPlaying)
            mediaPlayer.start();
    }
    
    @Override
    public void stop()
    {
	    ranking.onStop();
    }

    /**
     * @return GameState
     *          the current game state of the game {GAME, MENU}
     */
    public static GameState getGameState()
    {
	    return currentGameState;
    }

    /**
     * @param state GameState
     *                  sets the new GameState of the game.
     */
    public static void setGameState(GameState state)
    {
	    currentGameState = state;
    }

    // *** SOUNDPOOL & MEDIAPLAYER METHODS *** //

    /**
     * This method instantiates a new MediaPlayer depending on the current
     * game state of the game.
     */
    public static void StartMediaPlayer()
    {
        // check if media player is already used
        if(mediaPlayer != null && isPlaying)
        {
            mediaPlayer.stop();
            mediaPlayer = null;
            isPlaying = false;
        }
	
        if(currentGameState == GameState.MENU && !isPlaying)
        {
            while (mediaPlayer == null)
            {
                mediaPlayer = MediaPlayer.create(context, R.raw.ambientloop);		// create new media player instance
                mediaPlayer.setLooping(true);						                // track will be played in loop mode
                mediaPlayer.setVolume(0.2f, 0.2f);					                // set the volume
            }

            mediaPlayer.start();                                                    // start the media player
            isPlaying = true;									                    // set isPlaying flag

        }
        else if(currentGameState == GameState.GAME && !isPlaying)
        {
            while(mediaPlayer == null)
            {
                mediaPlayer = MediaPlayer.create(context, R.raw.ingameloop);		// create new media player instance
                mediaPlayer.setLooping(true);						                // track will be played in loop mode
                mediaPlayer.setVolume(0.2f, 0.2f);					                // set the volume
            }

            mediaPlayer.start();
            isPlaying = true;
        }
    }

    /**
     * @return SoundPool
     *          The method returns the soundpool object to administrate game sounds.
     */
    public static SoundPool getSoundpool()
    {
	    return soundPool;
    }

    /**
     * @return int
     *          The method returns the click sound used in the game.
     */
    public static int getClickSound ()
    {
	    return clickSound;
    }

    /**
     * @return int
     *          The method returns the crash sound used in the game.
     */
    public static int getCrashSound()
    {
	    return crashSound;
    }
}