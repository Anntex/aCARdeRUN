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
package de.hdm.mib.dg041.acarderun;

import android.opengl.GLSurfaceView;
import android.content.Context;
import de.hdm.mib.dg041.game.aCARdeRunGame;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class aCARdeRunView extends GLSurfaceView
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    /**
     * reference variable of the game
     */
    private aCARdeRunGame game;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

    public aCARdeRunView(Context context)
    {
        super(context);

        game = new aCARdeRunGame(this);
        setRenderer(game);

        setRenderMode(RENDERMODE_CONTINUOUSLY);
    }

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    @Override
    public void onPause()
    {
        super.onPause();	// call onPause() of super class to pause the game
        game.pause();
    }

    @Override
    public void onResume()
    {
        game.resume();
        super.onResume();	// call onResume() of the super class to resume the game
    }

    public void onStop()
    {
        game.stop();        // stops the game
    }
}
