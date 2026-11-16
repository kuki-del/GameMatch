package com.example.gamematch;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * Listens to the device's Accelerometer sensor to detect a physical shake gesture.
 */
public class ShakeDetector implements SensorEventListener {

    // G-force threshold required to trigger a shake (2.3g avoids accidental triggers)
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.3F;
    // Minimum delay in ms between two consecutive shakes
    private static final int SHAKE_SLOP_TIME_MS = 1000;

    public interface OnShakeListener {
        void onShake();
    }

    private final OnShakeListener listener;
    private long lastShakeTimestamp = 0;

    public ShakeDetector(OnShakeListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        // Normalize acceleration values by Earth's gravity
        float gX = event.values[0] / SensorManager.GRAVITY_EARTH;
        float gY = event.values[1] / SensorManager.GRAVITY_EARTH;
        float gZ = event.values[2] / SensorManager.GRAVITY_EARTH;

        // Calculate total G-force vector magnitude
        float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

        if (gForce > SHAKE_THRESHOLD_GRAVITY) {
            long now = System.currentTimeMillis();
            // Ignore shakes that happen too close together
            if (lastShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                return;
            }
            lastShakeTimestamp = now;
            if (listener != null) {
                listener.onShake();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not needed for shake detection
    }
}