package edu.msu.stanospa.project3;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;


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

    private double topLeftLat = 42.723169;
    private double topLeftLon = -84.483018;
    private double bottomRightLat = 42.721759;
    private double bottomRightLon = -84.480518;

    float hit, wid;


    private LocationManager locationManager = null;
    private ActiveListener activeListener = new ActiveListener();

    private double latitude = 0;
    private double longitude = 0;
    private boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Force the screen to say on and bright
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

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

        // Get the location manager
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        float distanceHit[] = new float[1];
        Location.distanceBetween(topLeftLat, topLeftLon, topLeftLat, bottomRightLon, distanceHit);
        hit = distanceHit[0];

        float distanceWid[] = new float[1];
        Location.distanceBetween(topLeftLat, topLeftLon, bottomRightLat, topLeftLon, distanceWid);
        wid = distanceWid[0];
    }

    /**
     * Called when this application becomes foreground again.
     */
    @Override
    protected void onResume() {
        super.onResume();

        registerListeners();
    }

    /**
     * Called when this application is no longer the foreground application.
     */
    @Override
    protected void onPause() {
        unregisterListeners();
        super.onPause();
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

    private void unregisterListeners() {
        locationManager.removeUpdates(activeListener);
    }

    private void registerListeners() {
        unregisterListeners();

        // Create a Criteria object
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(true);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(false);

        String bestAvailable = locationManager.getBestProvider(criteria, true);

        if(bestAvailable != null) {
            locationManager.requestLocationUpdates(bestAvailable, 500, 1, activeListener);
            Location location = locationManager.getLastKnownLocation(bestAvailable);
            onLocation(location);
        }
    }

    public void executeOnDelay() {

        float distanceHit[] = new float[1];
        Location.distanceBetween(topLeftLat, topLeftLon, latitude, topLeftLon, distanceHit);

        float distanceWid[] = new float[1];
        Location.distanceBetween(topLeftLat, topLeftLon, topLeftLat, longitude, distanceWid);

        float x = distanceWid[0]/wid;
        float y = distanceHit[0]/hit;

        drawView.addPoint(x, y,radius);
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

    private class ActiveListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            onLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            registerListeners();
        }
    };

    private void onLocation(Location location) {
        if(location == null) {
            return;
        }

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        valid = true;

    }
}