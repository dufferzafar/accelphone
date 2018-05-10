package io.zafar.senses;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
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
    private Sensor sensorAcc;
    private Sensor sensorGyr;

    private TextView mStatus;

    // Whether the data is being logged right now
    private boolean logging = false;

    // Directory where data will be stored
    private String sensorLogFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                                         + "/senses_data";
    private File sensorLogFolder;

    private File accLogFile;
    private PrintWriter accWriter;

    private File gyrLogFile;
    private PrintWriter gyrWriter;

    private String audioFilePath;
    private MediaRecorder audioRecorder;

    // Used to format the sensor date
    SimpleDateFormat dateFileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.UK);
    SimpleDateFormat logLineStamp = new SimpleDateFormat("HH:mm:ss.SSS", Locale.UK);

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
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorAcc = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorGyr = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        mStatus.setText("");

        if (sensorAcc != null) {
            mStatus.append(
                    "Accelerometer Found: " + "\n\n"
                            + "Name: " + sensorAcc.getName() + "\n"
                            + "Vendor: " + sensorAcc.getVendor() + "\n"
                            + "---------------------------------\n\n"
            );
        } else {
            mStatus.append("NO Accelerometer Found. \n\n");
        }

        if (sensorGyr != null) {
            mStatus.append(
                    "Gyroscope Found: " + "\n\n"
                            + "Name: " + sensorGyr.getName() + "\n"
                            + "Vendor: " + sensorGyr.getVendor() + "\n"
                            + "---------------------------------\n\n"
            );
        } else {
            mStatus.append("NO Gyroscope Found. \n\n");
        }

        // Add a FAB
        final FloatingActionButton fab = findViewById(R.id.fab);

        if (sensorAcc == null || sensorGyr == null) {
            fab.setEnabled(false);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If data is being logged; stop it!
                if (logging) {
                    fab.setImageResource(android.R.drawable.ic_media_play);

                    // Stop recording
                    mSensorManager.unregisterListener(onAccChange);

                    // Show snackbar
                    Snackbar.make(view, "Recording stopped!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();

                    logging = false;
                }

                // Data is not being logged; start!
                else {
                    fab.setImageResource(android.R.drawable.ic_media_pause);

                    // Setup files where the data will be written to
                    setupOutputFiles();

                    // Display paths in the main view
                    mStatus.append("Accelerometer data logged to: \n\n" + accLogFile);
                    mStatus.append("\n\nGyroscope data logged to: \n\n" + gyrLogFile);
                    mStatus.append("\n\nAudio logged to: \n\n" + audioFilePath);

                    // Start recording Accelerometer
                    mSensorManager.registerListener(onAccChange, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);

                    // and the Gyroscope
                    // TODO: Change this to Gyro!
                    // mSensorManager.registerListener(onGyrChange, sensorAcc, SensorManager.SENSOR_DELAY_FASTEST);
                    mSensorManager.registerListener(onGyrChange, sensorGyr, SensorManager.SENSOR_DELAY_FASTEST);

                    // Start recording Microphone
                    audioRecorder.start();

                    // Show snackbar
                    Snackbar.make(view, "Now recording accelerometer & microphone", Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();

                    logging = true;
                }
            }
        });

        // Get file writing & audio recording permission
        checkRequestPermissions();

        // Folder to store the sensor logs & recordings
        sensorLogFolder = new File(sensorLogFolderPath);

        // Check if something is amiss
        if (!sensorLogFolder.exists() && !sensorLogFolder.mkdirs()) {
            Log.e(lTag, "Can not create a folder (for some reason.)");
        }

        Log.d(lTag, "Activity Loaded");
    }

    private void setupOutputFiles() {

        try {
            accLogFile = new File(sensorLogFolder,
                    "accelerometer_" + dateFileName.format(new Date()) + ".csv");
            accLogFile.createNewFile();

            accWriter = new PrintWriter(accLogFile);

            gyrLogFile = new File(sensorLogFolder,
                    "gyroscope_" + dateFileName.format(new Date()) + ".csv");
            gyrLogFile.createNewFile();

            gyrWriter = new PrintWriter(gyrLogFile);

            Log.d(lTag, "File Created: " + accLogFile);
            Log.d(lTag, "File Created: " + gyrLogFile);
        } catch (IOException e) {
            Log.e(lTag, "Couldn't create file: " + accLogFile + "\n Error: " + e.toString());
        }

        // Audio Recorder
        audioRecorder = new MediaRecorder();
        audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            audioFilePath = sensorLogFolderPath + "/audio_" + dateFileName.format(new Date()) + ".m4a";
            audioRecorder.setOutputFile(audioFilePath);
            audioRecorder.prepare();
            Log.d(lTag, "File Created: " + audioFilePath);
        } catch (Exception e) {
            Log.e(lTag, "Couldn't setup audio recording." + "\n Error: " + e.toString());
        }

        Log.d(lTag, "Files setup");
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

    private void checkRequestPermissions() {
        String perm_write = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        String perm_mic = Manifest.permission.RECORD_AUDIO;
        int r1 = this.getApplicationContext().checkCallingOrSelfPermission(perm_write);
        int r2 = this.getApplicationContext().checkCallingOrSelfPermission(perm_mic);
        if (r1 + r2 != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{perm_write, perm_mic}, 1);
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
        mSensorManager.unregisterListener(onAccChange);
        mSensorManager.unregisterListener(onGyrChange);

        // Cleanup!
        try {
            audioRecorder.stop();
            audioRecorder.release();

            accWriter.flush();
            gyrWriter.flush();
        } catch (Exception e) {
            Log.d(lTag, "Bad cleanup");
        }

        Log.d(lTag, "On Pause - Un-registering Listener");
    }

    /////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////


    // This listens for accelerometer events
    private SensorEventListener onAccChange = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        synchronized public void onSensorChanged(SensorEvent event) {

            // https://stackoverflow.com/a/9333605/2043048
            long timeInMillis = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;

            // String name = event.sensor.getName();
            // name + ","

            String t = logLineStamp.format(new Date(timeInMillis)) + ", "
                    + event.values[0] + ", " + event.values[1] + ", " + event.values[2];

            accWriter.println(t);

            // Log.d(lTag, t);
        }
    };

    // This listens for gyroscope events
    private SensorEventListener onGyrChange = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        synchronized public void onSensorChanged(SensorEvent event) {

            // https://stackoverflow.com/a/9333605/2043048
            long timeInMillis = (new Date()).getTime() + (event.timestamp - System.nanoTime()) / 1000000L;

            String t = logLineStamp.format(new Date(timeInMillis)) + ", "
                    + event.values[0] + ", " + event.values[1] + ", " + event.values[2];

            gyrWriter.println(t);

            // Log.d(lTag, t);
        }
    };

}
