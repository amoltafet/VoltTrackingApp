package com.example.finalproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.InputType;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PlayWorkoutActivity extends AppCompatActivity implements SensorEventListener {
    ActivityResultLauncher<Intent> launcher;
    List<Exercises> exerciseList;
    PlayWorkoutActivity.CustomAdapter adapter;
    private SensorManager sensorManager = null;
    private boolean running = false;
    private float totalSteps = 0f;
    private float previousTotalSteps = 0f;
    private TextView stepsTakenTextView;
    private int ACTIVITY_REQUEST_CODE = 1;
    private long steps = 0;
    private int i = 0;
    private int j;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_workout);

        exerciseList = new ArrayList<>();
        adapter = new CustomAdapter();

        RecyclerView recyclerView = findViewById(R.id.exerciseListView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Play Workout");

        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String time = intent.getStringExtra("totalTime");
            exerciseList = (List<Exercises>) intent.getSerializableExtra("exerciseList");
            int parentId = intent.getIntExtra("parentId", 0);
            int position = intent.getIntExtra("position", 0);
            boolean run = intent.getBooleanExtra("run", false);
            TextView timeView = findViewById(R.id.timeLeft);
            if (Long.parseLong(time) > 60) {
                double minute = TimeUnit.SECONDS.toMinutes(Long.parseLong(time)) - (TimeUnit.SECONDS.toHours(Long.parseLong(time)) * 60);
                double seconds = (Long.parseLong(time) % (60 * minute)) * .01;
                timeView.setText(String.valueOf(minute + seconds));
            } else {
                timeView.setText(String.valueOf(time));
            }
            getSupportActionBar().setTitle(name);
            int totalTime = 0;
            for (int i = 0; i < exerciseList.size(); i++) {
                totalTime += exerciseList.get(i).getTime();
            }

            TextView nameView = findViewById(R.id.name);
            TextView timeLeft = findViewById(R.id.timeLeft);
            ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
            mProgressBar.setIndeterminate(true);
            List<CountDownTimer> timers = new ArrayList<>();
            for (int i = 0; i < exerciseList.size(); i++) {
                timers.add(new CountDownTimer(exerciseList.get(i).getTime() * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeft.setText(String.valueOf(millisUntilFinished / 1000));
                    }

                    @Override
                    public void onFinish() {
                        timeLeft.setText("done!");
                    }
                });
            }
            CountDownTimer mCountDownTimer;
            if (run) {
                nameView.setText("Cardio");
                i = 1;
                mProgressBar.setProgress(i);
                mCountDownTimer = new CountDownTimer(100 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        i++;
                        mProgressBar.setProgress((int) i);
                        timeLeft.setText(String.valueOf((int) i));


                    }

                    @Override
                    public void onFinish() {
                        mProgressBar.setProgress(100);
                        mProgressBar.setVisibility(View.GONE);

                    }
                };
                mCountDownTimer.start();
            } else {
                nameView.setText(exerciseList.get(0).getName());
                System.out.println(totalTime);
                CountDownTimer mCount = new CountDownTimer(totalTime * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        timeLeft.setText(String.valueOf(millisUntilFinished / 1000));
                    }
                    @Override
                    public void onFinish() {
                        timeLeft.setText("done!");
                    }
                };
                mCount.start();
            }

            stepsTakenTextView = findViewById(R.id.stepsTextView);
            sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                //ask for permission
                requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, 0);
            }
        }
    }
    /**
     Creates and displays the options menu.
     * @param menu the view of the item being clicked.
     * @return true if the handler consumed the event.
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.custom_workout_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     Handles events when a menu item is clicked on.
     * @param item the view of the item being clicked.
     * @return true if the handler consumed the event.
     */
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return false;
    }

    @Override
    protected void onResume () {
        super.onResume();
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Step counter sensor not available", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onSensorChanged (SensorEvent event){
        stepsTakenTextView.setText(String.valueOf(event.values[0]));
    }

    @Override
    public void onAccuracyChanged (Sensor sensor,int accuracy){
    }

    private void enableActivityMonitor () {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult ( int requestCode, @NonNull String[] permissions,
                                             @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableActivityMonitor();
            }
        }
    }


    class CustomAdapter extends RecyclerView.Adapter<PlayWorkoutActivity.CustomAdapter.CustomViewHolder> {

        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            CardView myCardView1;
            TextView myText1;
            TextView myTime;
            public CustomViewHolder(@NonNull View itemView) {
                super(itemView);
                myCardView1 = itemView.findViewById(R.id.exercise_card);
                myText1 = itemView.findViewById(R.id.exercise_name);
                myTime = itemView.findViewById(R.id.exercise_time);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void updateView(Exercises b) {
                myCardView1.setCardBackgroundColor(getResources().getColor(R.color.white));
                myText1.setText(b.getName());
                myTime.setText(String.valueOf(b.getTime()));
                myText1.setInputType(InputType.TYPE_NULL);
                myTime.setInputType(InputType.TYPE_NULL);
            }

            @Override
            public void onClick(View v) {
            }

            @Override
            public boolean onLongClick(View v) {
                return true; // false means this event handler did not handle or "consume" the event
            }
        }


        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(PlayWorkoutActivity.this)
                    .inflate(R.layout.exercise_card, parent, false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
            Exercises b = exerciseList.get(position);
            holder.updateView(b);
        }

        @Override
        public int getItemCount() {
            return exerciseList.size();
        }
    }
}



