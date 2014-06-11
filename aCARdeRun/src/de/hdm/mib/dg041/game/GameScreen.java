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

import de.hdm.mib.dg041.input.InputSystem;

/**
 * The abstract class GameScreen defines methods which must be implemented by all other screen
 * implementation of the game.
 *
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public abstract class GameScreen
{
    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * The method initializes all important components of the screen. This includes
     * the world, HUD, hero, enemy, etc.
     */
    public abstract void initialize();

    /**
     * The method loads all necessary component information for the screen.
     */
    public abstract void loadContent();

    /**
     * The method updates the information, position, etc. of the screen components
     * periodically.
     *
     * @param deltaSeconds the milliseconds for updating the screen periodically.
     * @param inputSystem InpustSystem
     *                      The kind of input system used by the game.
     */
    public abstract void update(float deltaSeconds, InputSystem inputSystem);

    /**
     * The method redraw the screen periodically depending on the given milliseconds
     * interval.
     *
     * @param deltaSeconds the milliseconds for redrawn the screen periodically.
     */
    public abstract void draw(float deltaSeconds);
}