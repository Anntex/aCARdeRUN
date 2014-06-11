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

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class aCARdeRunActivity extends Activity
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    /**
     * reference to the view of the game
     */
    private aCARdeRunView view;

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // hide status bar to display the game in full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // create a new game view and set it to the content
        view = new aCARdeRunView(this);
        setContentView(view);
    }

    @Override
    protected void onPause()
    {
        super.onPause();	// call onPause() of the super class to pause the activity
        view.onPause();		// call onPause() of the game view to pause the game
    }

    @Override
    protected void onResume()
    {
        super.onResume();	// call onResume() of the super class to resume the activity
        view.onResume();	// call onResume() of the game view to resume the game
    }

    @Override
    protected void onStop()
    {
        view.onStop();      // call onStop() of the game view to stop the game
        super.onStop();     // call onStop() of the super class to stop the activity
    }
}