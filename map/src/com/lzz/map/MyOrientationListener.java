package com.lzz.map;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MyOrientationListener implements SensorEventListener {

	private SensorManager mSensorManager;
	private Sensor mSensor;
	private Context context;
	
	private float lastX ;

	public MyOrientationListener(Context context) {
		super();
		this.context = context;
	}

	public void start() {
		mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager != null) {
			mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		}
		if (mSensor != null) {
			mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
		}

	}
	private OnOrientationListener mOrientationListener;
	public void setOrientationListener(OnOrientationListener OrientationListener) {
		this.mOrientationListener = OrientationListener;
	}

	public interface OnOrientationListener{
		void onOrientationChanged(float x);
	}

	public void stop() {
		mSensorManager.unregisterListener(this);
	}
	

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() == Sensor.TYPE_ORIENTATION) {
			float x = event.values[SensorManager.DATA_X];
			if(Math.abs(x-lastX)>1.0){
				if(mOrientationListener!=null){
					mOrientationListener.onOrientationChanged(x);
				}
			}
			lastX = x;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

}
