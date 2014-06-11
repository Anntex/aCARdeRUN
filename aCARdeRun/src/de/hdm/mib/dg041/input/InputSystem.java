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

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import de.hdm.mib.dg041.input.InputEvent.InputAction;
import de.hdm.mib.dg041.input.InputEvent.InputDevice;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

/**
 * @author dennis.grewe [dg041@hdm-stuttgart.de]
 * Created on 23.02.2012.
 */
public class InputSystem implements OnKeyListener, OnTouchListener, SensorEventListener
{
    // --------------------------------------------------------
    // PROPERTIES
    // --------------------------------------------------------

    private Queue<InputEvent> inputQueue;
	private Queue<InputEvent> inputPool;

    // --------------------------------------------------------
    // CONSTRUCTOR
    // --------------------------------------------------------

	public InputSystem(View view)
    {
		int maxInputEvents = 128;
		inputQueue = new ArrayBlockingQueue<InputEvent>(maxInputEvents);
		inputPool = new ArrayBlockingQueue<InputEvent>(maxInputEvents);

        for (int i = 0; i < maxInputEvents; ++i)
		{
			inputPool.add(new InputEvent());
		}
		
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.setOnKeyListener(this);
		view.setOnTouchListener(this);
		
		Context context = view.getContext();
		SensorManager sensorManager = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
		
		Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (accelerometer != null)
        {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

		Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		if (gyroscope != null)
        {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        }

		Sensor rotation = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);			
		if (rotation != null)
        {
            sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_GAME);
        }

		Sensor linearAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
		if(linearAccelerometer != null)
        {
            sensorManager.registerListener(this, linearAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        }

		Sensor gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
			sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1)
    {
		// net needed
	}

	@Override
	public void onSensorChanged(SensorEvent event)
    {
		InputDevice device = InputDevice.NONE;
		InputAction action = InputAction.UPDATE;
		float time = event.timestamp / 1000.0f;
		float v0 = 0, v1 = 0, v2 = 0, v3 = 0;
		
		switch (event.sensor.getType())
        {
            case Sensor.TYPE_ACCELEROMETER:
                device = InputDevice.ACCELEROMETER;
                v0 = event.values[0];
                v1 = event.values[1];
                v2 = event.values[2];
                break;

            case Sensor.TYPE_GYROSCOPE:
                device = InputDevice.GYROSCOPE;
                v0 = event.values[0];
                v1 = event.values[1];
                v2 = event.values[2];
                break;

            case Sensor.TYPE_ROTATION_VECTOR:		// TYPE_ROTATION_VECTOR
                device = InputDevice.ROTATION;
                v0 = event.values[0];
                v1 = event.values[1];
                v2 = event.values[2];

                if (event.values.length > 3)
                {
                    v3 = event.values[3];
                }
                else
                {
                    // @see android SensorEvent documentation:
                    // http://developer.android.com/reference/android/hardware/SensorEvent.html
                    v3 = (float) Math.cos(Math.asin(Math.sqrt(v0 * v0 + v1 * v1 + v2 * v2)));
                }
                break;

            case Sensor.TYPE_LINEAR_ACCELERATION:
                    device = InputDevice.LINEAR_ACCELEROMETER;
                    v0 = event.values[0];
                    v1 = event.values[1];
                    v2 = event.values[2];
                    break;

            case Sensor.TYPE_GRAVITY:
                    device = InputDevice.GRAVITY;
                    v0 = event.values[0];
                    v1 = event.values[1];
                    v2 = event.values[2];
                    break;

            default:
                return;
		}
		
		InputEvent inputEvent = inputPool.poll();
		if (inputEvent == null)
        {
            return;
        }

		inputEvent.set(device, action, time, 0, v0, v1, v2, v3);
		inputQueue.add(inputEvent);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event)
    {
		InputDevice device = InputDevice.TOUCHSCREEN;
		InputAction action = InputAction.NONE;
		float time = event.getEventTime() / 1000.0f;
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                action = InputAction.DOWN;
                break;

            case MotionEvent.ACTION_UP:
                action = InputAction.UP;
                break;

            case MotionEvent.ACTION_MOVE:
                action = InputAction.MOVE;
                break;

            default:
                return false;
		}
		
		InputEvent inputEvent = inputPool.poll();
		if (inputEvent == null)
        {
            return false;
        }

		inputEvent.set(device, action, time, 0, x, y, 0, 0);
		inputQueue.add(inputEvent);
		
		return true;
	}

	@Override
	public boolean onKey(View view, int keycode, KeyEvent event)
    {
		InputDevice device = InputDevice.KEYBOARD;
		InputAction action = InputAction.NONE;
		float time = event.getEventTime() / 1000.0f;
		
		switch (event.getAction())
        {
            case KeyEvent.ACTION_DOWN:
                action = InputAction.DOWN;
                break;

            case KeyEvent.ACTION_UP:
                action = InputAction.UP;
                break;

            default:
                return false;
		}
		
		InputEvent inputEvent = inputPool.poll();
		if (inputEvent == null)
        {
            return false;
        }

		inputEvent.set(device, action, time, keycode, 0, 0, 0, 0);
		inputQueue.add(inputEvent);
		
		return true;
	}

	public InputEvent peekEvent()
    {
		return inputQueue.peek();
	}
	
	public void popEvent()
    {
		InputEvent inputEvent = inputQueue.poll();
		if (inputEvent == null)
        {
            return;
        }

		inputPool.add(inputEvent);
	}
}