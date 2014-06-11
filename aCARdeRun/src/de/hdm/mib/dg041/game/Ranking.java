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
import android.content.SharedPreferences;
import de.hdm.mib.dg041.graphics.GraphicDevice;
import de.hdm.mib.dg041.graphics.Renderer;
import de.hdm.mib.dg041.graphics.SpriteFont;
import de.hdm.mib.dg041.graphics.TextBuffer;
import de.hdm.mib.dg041.math.Matrix4x4;

/**
 * This class implements the logic of a ranking screen. The Ranking object
 * contains an array which contains all entries displayed in the ranking
 * menu and the crash hud. Also, the ranking object persists the rankung
 * values to the device.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class Ranking
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    /** components to persist the ranking values  **/
    public static final String PREF_NAME = "Rankinglist";
    public static boolean rankingPushed = false;
    public static Ranking rankingInstance = null;

    private SharedPreferences settings;
    private SharedPreferences.Editor editor = null;

    /** components to display the ranking values **/
    private int[] ranking = new int[5];
    private SpriteFont fontHighscore;
    private TextBuffer[] textRanking;
    private Matrix4x4[] matHighscore;

    private Context context;
    private GraphicDevice graphicDevice;
    private Renderer renderer;

    // --------------------------------------------------------
    // SINGLETON
    // --------------------------------------------------------

    public static Ranking getInstance()
    {
        if(rankingInstance == null)
        {
            rankingInstance = new Ranking();
            return rankingInstance;
        }
        else
        {
            return rankingInstance;
        }
    }

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    private Ranking()
    {}

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    public void initialize(Context context, GraphicDevice graphicDevice, Renderer renderer)
    {
        this.context = context;
        this.graphicDevice = graphicDevice;
        this.renderer = renderer;

        settings = context.getSharedPreferences(PREF_NAME, 0);
        editor = settings.edit();

        // position of the RANKING strings
        matHighscore = new Matrix4x4[] {
            Matrix4x4.createTranslation(-30, 20, 0),				// 1st. place
            Matrix4x4.createTranslation(-30, -30, 0),				// 2nd. place
            Matrix4x4.createTranslation(-30, -80, 0),				// 3rd. place
            Matrix4x4.createTranslation(-30, -130, 0),				// 4th. place
            Matrix4x4.createTranslation(-30, -180, 0),				// 5th. place
        };
    }

    /**
     * The method loads the required content of a ranking object.
     * This includes a read operation to get the persisted ranking values from
     * device.
     */
    public void loadContent()
    {
        // font size of the "RANKINGS" string in the menu
        fontHighscore = graphicDevice.createSpriteFont(null, 32);

        // text buffer for the ranking string
        textRanking = new TextBuffer[] {
            graphicDevice.createTextBuffer(fontHighscore, 8),			// 1st. place
            graphicDevice.createTextBuffer(fontHighscore, 8),			// 2nd. place
            graphicDevice.createTextBuffer(fontHighscore, 8),			// 3rd. place
            graphicDevice.createTextBuffer(fontHighscore, 8),			// 4th. place
            graphicDevice.createTextBuffer(fontHighscore, 8),			// 5th. place
        };

        // load the content of the ranking values from shared preferences
        loadPreferences();
    }

    public void draw(float deltaSeconds)
    {
        // call content of the ranking again
        for (int i = 0; i < textRanking.length; i++)
        {
            textRanking[i].setText(timeFormatter(ranking[i]));
        }
	
        // draw the ranking content on screen
        for (int i = 0; i < textRanking.length; i++)
        {
            renderer.drawText(textRanking[i], matHighscore[i]);
        }
    }

    // --------------------------------------------------------
    // SHARED PREFERENCES METHODS
    // --------------------------------------------------------

    /**
     * This method loads the persisted ranking values from the android
     * shared preferences. If the method was called the first time
     * default values will be persists to device.
     */
    private void loadPreferences()
    {
	
        ranking[0] = settings.getInt("1st", 0);	// default value -> 357539 = 00:00
        ranking[1] = settings.getInt("2nd", 0);	// ...
        ranking[2] = settings.getInt("3rd", 0);	// ...
        ranking[3] = settings.getInt("4th", 0);	// ...
        ranking[4] = settings.getInt("5th", 0);	// ...

        // set initial string
        for (int i = 0; i < textRanking.length; i++)
        {
            textRanking[i].setText(timeFormatter(ranking[i]));
        }
    }

    /**
     * The method persists the ranking values to the device using android shared preferences.
     */
    private void savePreferences()
    {
        editor.putInt("1st", ranking[0]);
        editor.putInt("2nd", ranking[1]);
        editor.putInt("3rd", ranking[2]);
        editor.putInt("4th", ranking[3]);
        editor.putInt("5th", ranking[4]);
        editor.commit();
    }

    /**
     * This method sorts a certain time value into the ranking array
     * if the value is one of the best fife entries.
     *
     * @param time The time value to add to the ranking array.
     */
    public void pushRanking(int time)
    {
        if(!rankingPushed) {
            if(time > ranking[0])
            {
                ranking[4] = ranking[3];
                ranking[3] = ranking[2];
                ranking[2] = ranking[1];
                ranking[1] = ranking[0];
                ranking[0] = time;
                rankingPushed = true;
            }
            else if(time > ranking[1])
            {
                ranking[4] = ranking[3];
                ranking[3] = ranking[2];
                ranking[2] = ranking[1];
                ranking[1] = time;
                rankingPushed = true;
            }
            else if(time > ranking[2])
            {
                ranking[4] = ranking[3];
                ranking[3] = ranking[2];
                ranking[2] = time;
                rankingPushed = true;
            }
            else if(time > ranking[3])
            {
                ranking[4] = ranking[3];
                ranking[3] = time;
                rankingPushed = true;
            }
            else if(time > ranking[4])
            {
                ranking[4] = time;
                rankingPushed = true;
            }
        }
    }

    /**
     * @return the best time value
     */
    public int getBestTime()
    {
	    return ranking[0];
    }

    /**
     * This method formats the given milliseconds into the MM:SS format and returns the
     * result as String.
     *
     * @param milliSec
     *          The milliseconds value to format into the pattern MM:SS
     * @return A string of the formatted milliseconds
     */
    public String timeFormatter(int milliSec)
    {
        String time = "";

        if (milliSec > 357539)
        {
            throw new RuntimeException("Time value exceeds allowed format!");
        }
        else if(milliSec == 0)
        {
            return "00:00";
        }
        else
        {

            int constant = milliSec;
            constant = constant / 1000;
            int min = (constant / 60);
            int sec = constant - min * 60;

            if (min < 10 && sec < 10)
            {
                time = "0" + min + ":" + "0" + sec;
            }
            if (min > 10 && sec < 10)
            {
                time = "" + min + ":" + "0" + sec;
            }
            if (min < 10 && sec >= 10)
            {
                time = "0" + min + ":" + sec;
            }
            if (min > 10 && sec > 10)
            {
                time = "" + min + ":" + sec;
            }
        }

	    return time;
    }

    /**
     * This method will be called if the application will be stopped by
     * the android os or the user. In this case the method stores the current changes
     * to the ranking values.
     */
    public void onStop()
    {
	    this.savePreferences();
    }
}