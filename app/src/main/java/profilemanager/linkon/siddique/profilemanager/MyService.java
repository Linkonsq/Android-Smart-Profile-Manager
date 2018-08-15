package profilemanager.linkon.siddique.profilemanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class MyService extends Service implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mLight, mAccelerometer, mProximity;
    private float lightVal, axVal, ayVal, azVal, distance;
    private int currentVolume;
    private int maxVolume;
    private int mediumVolume;
    private AudioManager myAudioManager;
    private boolean silent = false, medium = false, high = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        myAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = myAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);

        Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager.registerListener(this, mLight, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);

        Toast.makeText(this, "Started", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_LIGHT) {
            lightVal = event.values[0];
        }

        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
            axVal = event.values[0];
            ayVal = event.values[1];
            azVal = event.values[2];
        }

        if (event.sensor.getType()==Sensor.TYPE_PROXIMITY) {
            distance = event.values[0];
        }

        //Pocket(standing) condition
        if ((distance == 0 && lightVal == 0 && ayVal > 9) || (distance == 0 && lightVal == 0 && ayVal < -9)) {
            high();
            Toast.makeText(this, "High", Toast.LENGTH_SHORT).show();
        }

        //Pocket(sitting) condition
        if (distance == 0 && lightVal == 0 && azVal > 5) {
            high();
            //Toast.makeText(this, "High", Toast.LENGTH_SHORT).show();
        }

        //Pocket(sitting) condition
        if ((distance == 0 && lightVal == 0 && axVal > 5) || (distance == 0 && lightVal == 0 && axVal < -5)) {
            silent();
            //Toast.makeText(this, "Silent", Toast.LENGTH_SHORT).show();
        }

        //table condition(up) uncovered
        if (distance > 0 && azVal > 9 && ayVal < 3) {
            medium();
            //Toast.makeText(this, "Medium", Toast.LENGTH_SHORT).show();
        }

        //table condition(down)
        if (distance == 0 && lightVal == 0 && azVal < -9) {
            silent();
            //Toast.makeText(this, "Silent", Toast.LENGTH_SHORT).show();
        }

        //while using phone (vertically)
        if (distance > 0 && ayVal > 3) {
            silent();
            //Toast.makeText(this, "Silent", Toast.LENGTH_SHORT).show();
        }

        //while using phone (horizontally)
        if ((distance > 0 && axVal > 3 && azVal < 8) || (distance > 0 && axVal < -3 && azVal < 8)) {
            silent();
            //Toast.makeText(this, "Silent", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        Toast.makeText(this, "Destroyed", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private void silent() {
        currentVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        for (int i = currentVolume; i > 0; i--) {
            //myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_VIBRATE);
            myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        }
    }

    private void medium() {
        currentVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        mediumVolume = (maxVolume / 2) + 1;
        if (currentVolume < mediumVolume) {
            for (int i = currentVolume; i <  mediumVolume; i++) {
                //myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_VIBRATE);
                myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            }
        }

        if (currentVolume > mediumVolume) {
            for (int i = currentVolume; i > mediumVolume; i--) {
                //myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_VIBRATE);
                myAudioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }
        }
    }

    private void high() {
        currentVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_RING);
        for (int i = currentVolume; i < maxVolume; i++) {
            //myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_VIBRATE);
            myAudioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        }
    }
}