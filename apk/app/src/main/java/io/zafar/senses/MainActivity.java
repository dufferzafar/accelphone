package io.zafar.senses;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    final private String lTag = "Senses";

    private SensorManager mSensorManager;
    private Sensor mSensorAccel;

    private TextView mStatus;

    private boolean logging = false;

    private PrintWriter sensorWriter;

    // Used to format the sensor date
    SimpleDateFormat dateFileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.UK);
    SimpleDateFormat logStamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.UK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        // Prevent screen from going down
        getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Add a 3-dot toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("My Title");

        // This will display the sensor readings
        mStatus = findViewById(R.id.status);

        // Build a sensor
        mSensorManager= (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);  // Sensor.TYPE_ALL

        // Add a FAB
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If data is being logged; stop it!
                if (logging) {
                    fab.setImageResource(android.R.drawable.ic_media_play);

                    // Stop recording
                    mSensorManager.unregisterListener(onSensorChange);

                    // Show snackbar
                    Snackbar.make(view, "Recording paused!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();

                    logging = false;
                }

                // Data is not being logged; start!
                else {
                    fab.setImageResource(android.R.drawable.ic_media_pause);

                    // Start recording
                    mSensorManager.registerListener(onSensorChange, mSensorAccel, SensorManager.SENSOR_DELAY_FASTEST);

                    // Show snackbar
                    Snackbar.make(view, "Now recording accelerometer.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();

                    logging = true;
                }
            }
        });

        // Get file writing permission
        checkAndGrantWriteExternalPermission();

        // Folder to store the sensor logs & recordings
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/senses_data";
        File sensorLogFolder = new File(path);

        // Check if something is amiss
        if (!sensorLogFolder.exists() && !sensorLogFolder.mkdirs()) {
            Log.e(lTag, "Can not create a folder (for some reason.)");
        }

        File sensorLogFile = new File(sensorLogFolder, "accelerometer_" + dateFileName.format(new Date()) + ".csv");

        try {
            sensorLogFile.createNewFile();
            sensorWriter = new PrintWriter(sensorLogFile);
            Log.d(lTag, "File Created: " + sensorLogFile);
        } catch (IOException e) {
            Log.e(lTag, "Couldn't create file: " + sensorLogFile + "\n Error: " + e.toString());
        }

        mStatus.setText("Data will be logged to: " + sensorLogFile);
        Log.d(lTag, "Shit setup");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkAndGrantWriteExternalPermission() {
        String permission = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        int res = this.getApplicationContext().checkCallingOrSelfPermission(permission);
        if (res != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{permission}, 1);
        }
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(lTag, "On Resume - Registering Listener");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Stop recording
        mSensorManager.unregisterListener(onSensorChange);

        sensorWriter.flush();
        Log.d(lTag, "On Pause - Un-registering Listener");
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////


    // This listens for the incoming sensor events
    private SensorEventListener onSensorChange = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {

        }

        @Override
        synchronized public void onSensorChanged(SensorEvent event) {

            // https://stackoverflow.com/a/9333605/2043048
            long timeInMillis = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;

            String t = logStamp.format(new Date(timeInMillis)) + ", "
                    + event.values[0] + ", " + event.values[1] + ", " + event.values[2];

            sensorWriter.println(t);

            // Log.d(lTag, t);
        }
    };
}
