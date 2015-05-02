package edu.msu.stanospa.project3;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    private Sensor accelSensor = null;
    private AccelListener accelListener = null;
    private float radius;
    private float y;

    private static float A = 0.95f;
    private static float MAX_Y = 9.5f;
    private static float MIN_Y = 0.1f;
    private static int DELAY = 50;
    private DrawView drawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawView = (DrawView)this.findViewById(R.id.drawView);

        SensorManager sensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);
        accelSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if(accelSensor != null) {
            accelListener = new AccelListener();
            sensorManager.registerListener(accelListener,
                    accelSensor,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        executeOnDelay();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    private class AccelListener implements SensorEventListener {
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
        @Override
        public void onSensorChanged(SensorEvent event) {
            y = (1 - A) * event.values[1] + A * y;
            if(y > MAX_Y)
                y = MAX_Y;
            if(y < MIN_Y)
                y = 0f;
            radius = 1 - y / MAX_Y;
        }
    }

    public void executeOnDelay() {
        drawView.addPoint(0.5f,0.5f,radius);
        delay();
    }

    public void delay() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                executeOnDelay();
            }
        }, DELAY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.color_black:
                drawView.setPaintColor(DrawView.PaintColor.Black);
                return true;
            case R.id.color_white:
                drawView.setPaintColor(DrawView.PaintColor.White);
                return true;
            case R.id.color_red:
                drawView.setPaintColor(DrawView.PaintColor.Red);
                return true;
            case R.id.color_green:
                drawView.setPaintColor(DrawView.PaintColor.Green);
                return true;
            case R.id.color_blue:
                drawView.setPaintColor(DrawView.PaintColor.Blue);
                return true;
            case R.id.send_picture:
                ViewSender sender = new ViewSender();
                sender.sendView(this,  drawView, "Caravaggio");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
