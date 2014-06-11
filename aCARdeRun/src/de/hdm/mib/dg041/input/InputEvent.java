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
package de.hdm.mib.dg041.input;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class InputEvent
{
    // --------------------------------------------------------
    // INPUT DEVICE / ACTION ENUMS
    // --------------------------------------------------------

    /**
     * This enumeration defines the different supported input devices.
     *
     * @author dennis.grewe [dg041@hdm-stuttgart.de]
     * Created on 23.02.2012.
     */
    public enum InputDevice
    {
        NONE,
        KEYBOARD,
        TOUCHSCREEN,
        ACCELEROMETER,
        GYROSCOPE,
        ROTATION,
        LINEAR_ACCELEROMETER,
    	GRAVITY
    }

    /**
     * This enumeration defines the different supported input actions.
     *
     * @author dennis.grewe [dg041@hdm-stuttgart.de]
     * Created on 23.02.2012.
     */
    public enum InputAction
    {
        NONE,
        DOWN,
        UP,
        MOVE,
        UPDATE
    }

    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------
    
    private InputDevice device;
    private InputAction action;
    private float time;
    private int keyCode;
    private float[] values = new float[4];

    // --------------------------------------------------------
    // METHODS
    // --------------------------------------------------------

    /**
     * @return the type of the input event
     */
    public InputDevice getDevice()
    {
	    return device;
    }

    /**
     * @return the action of the input event
     */
    public InputAction getAction()
    {
	    return action;
    }

    /**
     * @return the time code of the event.
     */
    public float getTime()
    {
	    return time;
    }

    /**
     * @return the key code of the input event
     */
    public int getKeycode()
    {
	    return keyCode;
    }

    /**
     * @return the input value
     */
    public float[] getValues()
    {
	    return values;
    }

    public void set(InputDevice device, InputAction action, float time, int keyCode, float value0, float value1, float value2, float value3)
    {
        this.device = device;
        this.action = action;
        this.time = time;
        this.keyCode = keyCode;
        this.values[0] = value0;
        this.values[1] = value1;
        this.values[2] = value2;
        this.values[3] = value3;
    }
}
