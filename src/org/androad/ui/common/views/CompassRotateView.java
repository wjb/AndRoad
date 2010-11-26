// Created by plusminus on 22:55:26 - 06.09.2008
package org.androad.ui.common.views;

import org.androad.R;
import org.androad.util.constants.Constants;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.widget.Toast;


public class CompassRotateView extends RotateView implements SensorEventListener {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected boolean mActive = false;
	private boolean mToastShownOnce = false;

	// ===========================================================
	// Constructors
	// ===========================================================

	public CompassRotateView(final Context context) {
		super(context);
	}

	public CompassRotateView(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public void toggleActive() {
		if(this.mActive) {
			deactivate();
		} else {
			activate();
		}
	}

	public void activate(){
		this.mActive = true;
		invalidate();
	}

	public void deactivate(){
		this.mActive = false;
		super.mHeading = Constants.NOT_SET;
		invalidate();
	}

	public boolean isActive() {
		return this.mActive;
	}

	// ===========================================================
	// Methods from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onAccuracyChanged(final Sensor sensor, final int accuracy) {
		if(!this.mToastShownOnce  && this.mActive && accuracy < SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM){
			this.mToastShownOnce = true;
			Toast.makeText(this.getContext(), R.string.compass_please_callibrate, Toast.LENGTH_SHORT).show();
		}
	}

	public void onSensorChanged(final int sensor, final float[] values) {
		//Log.d(TAG, "x: " + values[0] + "y: " + values[1] + "z: " + values[2]);
		if(this.mActive){
			synchronized (this) {
				super.mHeading = values[0];
				invalidate();
			}
		}
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		
	}

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
