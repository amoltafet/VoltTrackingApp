package com.example.finalproject;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PlayWorkoutActivity extends AppCompatActivity /*implements SensorEventListener*/ {
    private SensorManager sensorManager = null;
    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private TextView stepsTakenTextView;
    private int ACTIVITY_REQUEST_CODE = 1;
    private long steps = 0;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_workout);
        
        Intent intent = getIntent();
        if (intent != null) {
            name = intent.getStringExtra("name");
            time = intent.getStringExtra("totalTime");
            exercises = (List<Exercises>) intent.getSerializableExtra("exerciseList");
            parentId = intent.getIntExtra("parentId", 0);
            position = intent.getIntExtra("position", 0);

            TextView timeView = findViewById(R.id.timeLeft);
            if (Long.parseLong(time) > 60) {
                double minute = TimeUnit.SECONDS.toMinutes(Long.parseLong(time)) - (TimeUnit.SECONDS.toHours(Long.parseLong(time)) * 60);
                double seconds = (Long.parseLong(time) % (60 * minute)) * .01;
                timeView.setText(String.valueOf(minute + seconds));
            } else {
                timeView.setText(String.valueOf(time));
            }
            
            TextView nameView = findViewById(R.id.name);
            nameView.setText(name);
//        stepsTakenTextView = findViewById(R.id.stepsTakenCount);
//        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//
//
//        Button startRun = findViewById(R.id.startRunButton);
//        startRun.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
//       if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
//            //ask for permission
//            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
//        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//        if (countSensor != null) {
//            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
//        } else {
//            Toast.makeText(this, "Step counter sensor not available", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        stepsTakenTextView.setText(String.valueOf(event.values[0]));
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//
//    private void enableActivityMonitor () {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {
//
//        }
//        else {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACTIVITY_REQUEST_CODE);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult (int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//
//        if (requestCode == ACTIVITY_REQUEST_CODE) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                enableActivityMonitor();
//            }
//        }
//    }


}
