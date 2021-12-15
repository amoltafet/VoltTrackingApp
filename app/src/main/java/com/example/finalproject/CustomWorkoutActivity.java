/**
 * This program handles the detail screen for each video.
 * CPSC 312-02, Fall 2021
 * Programming Assignment #7
 *  <div>Icons made by <a href="https://www.flaticon.com/authors/feen" title="feen">feen</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
 *  https://www.zerochan.net/695419
 *  https://www.imdb.com/title/tt0851578/mediaviewer/rm1498027776/
 *  https://miro.medium.com/max/1400/1*QXEgzNLX-6pTbLZu4VpIVg.jpeg
 *
 * @author Rebekah Hale
 * @version v2.0 11/23/21
 */

package com.example.finalproject;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 Model for the video detail page.
 */
public class CustomWorkoutActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> launcher;
    CustomAdapter adapter;
    TextView totalTimeView;
    List<Exercises> exerciseList;
    EditText titleTextView;
    int position;
    int parentId;
    boolean saved;
    boolean run;


    /**
     Handles the functionality of when the activity is created.
     * @param savedInstanceState saves the sate for when the app is closed.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_workout);
        saved = false;
        exerciseList = new ArrayList<>();
        adapter = new CustomAdapter();

        RecyclerView recyclerView = findViewById(R.id.exerciseListView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit a Workout");

        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra("name");
            String totalTime = intent.getStringExtra("totalTime");
            exerciseList = (List<Exercises>) intent.getSerializableExtra("exerciseList");
            parentId = intent.getIntExtra("parentId", 0);
            run = intent.getBooleanExtra("run", false);
            position = intent.getIntExtra("position", 0);
            titleTextView = findViewById(R.id.name);
            titleTextView.setText(name);

            Switch switch1 = (Switch) findViewById(R.id.switch1);
            switch1.setChecked(run);

            switch1.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged (CompoundButton compoundButton, boolean b) {
                    run = b;
                }
            });

            totalTimeView = findViewById(R.id.totalTime);
            if (Long.parseLong(totalTime) > 60) {
                double minute = TimeUnit.SECONDS.toMinutes(Long.parseLong(totalTime)) - (TimeUnit.SECONDS.toHours(Long.parseLong(totalTime)) * 60);
                double seconds = (Long.parseLong(totalTime) % (60 * minute)) * .01;
                totalTimeView.setText(String.valueOf(minute + seconds));
            }
            totalTimeView.setText(String.valueOf(totalTime));

            Button saveWorkoutButton = findViewById(R.id.saveWorkoutButton);
            saveWorkoutButton.setOnClickListener(new View.OnClickListener() {
                /**
                 On click the video info will save to be displayed in the recycler view.
                 * @param view the view of the item being clicked.
                 */
                @Override
                public void onClick (View view) {
                    if (titleTextView.getText().toString().equals(getString(R.string.empty))) {
                        Toast.makeText(CustomWorkoutActivity.this, "Enter a workout name", Toast.LENGTH_LONG).show();
                    }
                    else {
                        saved = true;
                    }
                }
            });

            Button playWorkoutButton = findViewById(R.id.playButton);

            playWorkoutButton.setOnClickListener(view -> {
                if (saved) {
                    Intent intent1 = new Intent(CustomWorkoutActivity.this, PlayWorkoutActivity.class);
                    intent1.putExtra("name", titleTextView.getText().toString());
                    intent1.putExtra("totalTime", String.valueOf(getTotalTime()));
                    intent1.putExtra("exerciseList", (Serializable) exerciseList);
                    intent1.putExtra("parentId", 0);
                    intent1.putExtra("run", false);
                    intent1.putExtra(getString(R.string.position), position);
                    startActivity(intent1);
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Do you want to save before leaving?")
                            .setPositiveButton("Yes", null)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel(); CustomWorkoutActivity.this.finish();
                                }
                            });

                    builder.show();
                }
            });
        }
    }

    public int getTotalTime () {
        int totalTime = 0;
        for (Exercises exercises: exerciseList) {
            totalTime += exercises.getTime();
        }
        return totalTime;
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
                if (saved) {
                    Intent intent = new Intent();
                    intent.putExtra("name", titleTextView.getText().toString());
                    intent.putExtra("totalTime", getTotalTime());
                    intent.putExtra("exerciseList", (Serializable) exerciseList);
                    intent.putExtra("parentId", 0);
                    intent.putExtra("run", false);
                    intent.putExtra(getString(R.string.position), position);
                    CustomWorkoutActivity.this.setResult(Activity.RESULT_OK, intent);
                    CustomWorkoutActivity.this.finish();
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Do you want to save before leaving?")
                            .setPositiveButton("Yes", null)
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel(); CustomWorkoutActivity.this.finish();
                                }
                            });

                    builder.show();
                }

                return true;
            case R.id.addMenuExerciseItem:
                Exercises exercise = new Exercises(parentId);
                exerciseList.add(exercise);
                adapter.notifyItemChanged(exerciseList.size());
                return true;
            case R.id.menuOptionExerciseItem:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        boolean multiSelect = false;
        ActionMode actionMode;
        ActionMode.Callback callbacks;
        List<Exercises> selectedVideos = new ArrayList<>();


        /**
         Provides functionality for delete, add, and view recycler item.
         */
        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView workoutName;
            TextView workoutTime;
            CardView myCardView1;
            /**
             Constructor for Custom View Holder.
             * @param itemView the view of the recycler.
             */
            public CustomViewHolder (@NonNull View itemView) {
                super(itemView);
                workoutName = itemView.findViewById(R.id.exercise_name);
                workoutTime = itemView.findViewById(R.id.exercise_time);
                myCardView1 = itemView.findViewById(R.id.exercise_card);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            /**
             Displays the card item.
             * @param exercise the selected video to show.
             */
            public void updateView (Exercises exercise) {
                myCardView1.setCardBackgroundColor(getResources().getColor(R.color.white));
                workoutName.setText(exercise.getName());
                workoutName.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        exercise.setName(String.valueOf(workoutName.getText()));
                    }
                });
                workoutName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    /**
                     Called when the focus state of a view has changed.
                     * @param view the keyboard.
                     * @param b if the focus has been changed.
                     */
                    @Override
                    public void onFocusChange (View view, boolean b) {
                        if (!b) {
                            hideKeyboard(view);
                        }
                    }
                });
                workoutTime.setText(String.valueOf(exercise.getTime()));
                workoutTime.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged (Editable editable) {
                        String stringTotalTime = "";
                        if (!String.valueOf(workoutTime.getText()).equals("")) {
                            stringTotalTime = String.valueOf(workoutTime.getText());
                            exercise.setTime(Integer.parseInt((stringTotalTime)));
                        }
                        int totalTime = 0;
                        for (Exercises exercises: exerciseList) {
                            totalTime += exercises.getTime();
                        }
                        if (stringTotalTime.contains(".")) {
                            totalTime = (totalTime * 60) * 100;
                            totalTimeView.setText(String.valueOf(totalTime));
                        }
                        if (totalTime > 60) {
                            double minute = TimeUnit.SECONDS.toMinutes(totalTime) - (TimeUnit.SECONDS.toHours(totalTime) * 60);
                            double seconds = (totalTime % (60 * minute)) * .01;
                            totalTimeView.setText(String.valueOf(minute + seconds));
                        }
                        else {
                            totalTimeView.setText(String.valueOf(totalTime) + "secs");
                        }
                    }
                });
                workoutTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    /**
                     Called when the focus state of a view has changed.
                     * @param view the keyboard.
                     * @param b if the focus has been changed.
                     */
                    @Override
                    public void onFocusChange (View view, boolean b) {
                        if (!b) {
                            hideKeyboard(view);
                        }
                    }
                });
            }

            /**
             Selects a video in the recyclerview.
             * @param exercises the selected video to show.
             */
            public void selectItem (Exercises exercises) {
                if (multiSelect) {
                    if (selectedVideos.contains(exercises)) {
                        selectedVideos.remove(exercises);
                    }
                    else {
                        selectedVideos.add(exercises);
                        myCardView1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                    }
                    if (selectedVideos.size() == 1) {
                        actionMode.setTitle(selectedVideos.size() +
                                getString(R.string.item_selected));
                    }
                    actionMode.setTitle(selectedVideos.size() +
                            getString(R.string.items_selected));
                }
            }

            /**
             On long click the user can opt to delete the item or have it remain on the recyclerview.
             * @param view the view of the item being clicked.
             * @return true if the handler consumed the event.
             */
            @Override
            public boolean onLongClick (View view) {
                CustomWorkoutActivity.this.startActionMode(callbacks);
                selectItem(exerciseList.get(getAdapterPosition()));
                return true;
            }

            /**
             When the user clicks on the card it will bring them to a new page with the card view.
             * @param view the view of the item being clicked.
             */
            @Override
            public void onClick (View view) {
                /*if (multiSelect) {
                    selectItem(exerciseList.get(getAdapterPosition()));
                }
                else {
                    int position = getAdapterPosition();
                    Intent intent = new Intent(VideoDetailActivity.this, VideoDetailActivity.class);
                    intent.putExtra("name", exerciseList.get(position).getName());
                    intent.putExtra("totalTime", exerciseList.get(position).getTotalTime());
                    intent.putExtra("exerciseList", (Parcelable) exerciseList.get(position).getExercisesList());
                    intent.putExtra(getString(R.string.position), position);
                    launcher.launch(intent);
                } */
            }
            public void startOnContextActionMode () {
                CustomWorkoutActivity.this.startActionMode(callbacks);
                selectItem(exerciseList.get(getAdapterPosition()));
            }
        }

        /**
         Constructor for Adapter that allows a menu bar to be created with menu items that can be clicked on.
         */
        public CustomAdapter () {
            super();

            callbacks = new ActionMode.Callback() {
                /**
                 Creates the view for the action mode.
                 * @param menu the top menu view.
                 * @param mode the action mode view.
                 * @return true if the action mode is present in the view.
                 */
                @Override
                public boolean onCreateActionMode (ActionMode mode, Menu menu) {
                    multiSelect = true;
                    actionMode = mode;
                    MenuInflater menuInflater = getMenuInflater();
                    menuInflater.inflate(R.menu.cam_menu, menu);
                    return true;
                }

                /**
                 Is false for on create. The method is returned true when the action mode is started.
                 * @param menu the top menu view.
                 * @param actionMode the action mode view.
                 * @return true if the action mode is present in the view.
                 */
                @Override
                public boolean onPrepareActionMode (ActionMode actionMode, Menu menu) {
                    return false;
                }

                /**
                 Called when a menu item is clicked.
                 * @param actionMode the action mode view.
                 * @param menuItem position of each card in recyclerView.
                 * @return true if a menu item is clicked on.
                 */
                @Override
                public boolean onActionItemClicked (ActionMode actionMode, MenuItem menuItem) {
                    if (menuItem.getItemId() == R.id.menuOptionExerciseItem) {
                        for (int i = 0; i < exerciseList.size(); i++) {
                            if (selectedVideos.contains(exerciseList.get(i))) {
                                exerciseList.remove(i);
                                notifyItemRemoved(i);
                            }
                        }
                        actionMode.finish();
                        return true;
                    }
                    return false;
                }

                /**
                 Deletes all of the Videos from the database and recyclerview.
                 * @param actionMode the action mode view.
                 */
                @Override
                public void onDestroyActionMode (ActionMode actionMode) {
                    multiSelect = false;
                    for (Exercises exercises : selectedVideos) {
                        exerciseList.remove(exercises);
                    }
                    selectedVideos.clear();

                    for (int i = 0; i < exerciseList.size(); i++) {
                        notifyItemChanged(i);
                    }
                }
            };
        }

        /**
         Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
         * @param parent the parent view.
         * @param viewType which view to display.
         * @return the custom view holder to show the recyclerview.
         */
        @NonNull
        @Override
        public CustomViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(CustomWorkoutActivity.this)
                    .inflate(R.layout.exercise_card, parent, false);
            return new CustomViewHolder(view);
        }

        /**
         Called by RecyclerView to display the data at the specified position.
         * @param holder updates the view of the recyclerview.
         * @param position position of each card in recyclerView.
         */
        @Override
        public void onBindViewHolder (@NonNull CustomViewHolder holder, int position) {
            Exercises exercise = exerciseList.get(position);// check
            holder.updateView(exercise); //  check here
        }

        /**
         Gets the number of items in the recyclerView.
         * @return the size of the video list in the database.
         */
        @Override
        public int getItemCount () {
            return exerciseList.size();
        }
    }

    /**
     On click the keyboard will disappear.
     * @param view the view of the item being clicked.
     */
    public void hideKeyboard (View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}