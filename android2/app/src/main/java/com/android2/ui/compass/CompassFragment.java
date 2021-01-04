package com.android2.ui.compass;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.android2.R;

public class CompassFragment extends Fragment implements SensorEventListener {

    private ImageView compassImage;
    private TextView azimuthAngleTextBox;

    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];
    private float[] orientation = new float[3];

    private float azimuthAngle = 0f;
    private float currentAzimuth = 0f;

    private SensorManager sensorManager;

    public static CompassFragment newInstance() {
        return new CompassFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_compass, container, false);

        compassImage = root.findViewById(R.id.image_compass);
        azimuthAngleTextBox = root.findViewById(R.id.text_azimuth);

        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);

        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];
        }

        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic[0] = alpha * geomagnetic[0] + (1 - alpha) * event.values[0];
            geomagnetic[1] = alpha * geomagnetic[1] + (1 - alpha) * event.values[1];
            geomagnetic[2] = alpha * geomagnetic[2] + (1 - alpha) * event.values[2];
        }

        float R[] = new float[9];
        float I[] = new float[9];
        boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);

        if (success) {
            SensorManager.getOrientation(R, orientation);
            azimuthAngle = (float)Math.toDegrees(orientation[0]);
            azimuthAngle = (azimuthAngle + 360) % 360;

            Animation animation = new RotateAnimation(-currentAzimuth, -azimuthAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            currentAzimuth = azimuthAngle;

            SetAzimuthTextBoxValue();

            animation.setDuration(500);
            animation.setRepeatCount(0);
            compassImage.startAnimation(animation);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void SetAzimuthTextBoxValue() {
        String direction = "NW";

        if (azimuthAngle >= 350 || azimuthAngle <= 10)
            direction = "N";
        if (azimuthAngle < 350 && azimuthAngle > 280)
            direction = "NW";
        if (azimuthAngle <= 280 && azimuthAngle > 260)
            direction = "W";
        if (azimuthAngle <= 260 && azimuthAngle > 190)
            direction = "SW";
        if (azimuthAngle <= 190 && azimuthAngle > 170)
            direction = "S";
        if (azimuthAngle <= 170 && azimuthAngle > 100)
            direction = "SE";
        if (azimuthAngle <= 100 && azimuthAngle > 80)
            direction = "E";
        if (azimuthAngle <= 80 && azimuthAngle > 10)
            direction = "NE";

        azimuthAngleTextBox.setText(azimuthAngle + "° " + direction);
    }
}
