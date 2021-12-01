/**
 * This program handles the model activity to pass information.
 * CPSC 312-02, Fall 2021
 * Programming Assignment #7
 * Sources:
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
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 Main Activity screen with a recyclerview shows a card with an image and a textView. Can add and delete Cards.
 */
public class MainActivity extends AppCompatActivity {
    static final String TAG = String.valueOf(R.string.main_activity_tag);
    ActivityResultLauncher<Intent> launcher;
    CustomAdapter adapter;
    WorkoutListOpenHelper helper;
    List<WorkoutList> workoutList;

    /**
     Handles the functionality of when the activity is created.
     * @param savedInstanceState saves the sate for when the app is closed.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        adapter = new CustomAdapter();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        helper = new WorkoutListOpenHelper(this);
        workoutList = new ArrayList<>();
        List<Exercises> list = new ArrayList<>();

        if (helper.getAllWorkoutLists().size() == 0) {

            list.add(new Exercises("crunch", 45));
            list.add(new Exercises("rest", 20));
            list.add(new Exercises("plank", 60));
            workoutList.add(new WorkoutList("Abs", list));
            int time = workoutList.get(0).getTotalTime();
            workoutList.get(0).setTotalTime(time);
            for (int i = 0; i < workoutList.size(); i++) {
                helper.addWorkoutListItem(workoutList.get(i));
                int parentId = helper.getAllWorkoutLists().get(i).getId();
                for (Exercises exercise: list) {
                    exercise.setParentId(parentId);
                    helper.addExerciseListItem(exercise);
                    adapter.notifyItemChanged(i);
                }
                adapter.notifyItemChanged(i);
            }
        }

        workoutList = helper.getAllWorkoutLists();
        for (int i = 0; i < helper.getAllWorkoutLists().size(); i++) {
            adapter.notifyItemChanged(i);
        }
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, getString(R.string.on_activity_result_tag));
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        String name = data.getStringExtra("name");
                        int totalTime = data.getIntExtra("totalTime", 0);
                        List<Exercises> exercises = data.getParcelableExtra("exerciseList");
                        int position = data.getIntExtra(getString(R.string.position), 0);

                        for (int i = 0; i < workoutList.size(); i++) {
                            if (position == i) {
                                // the same card saved
                                if (!workoutList.get(i).getName().equals(name)) {
                                    workoutList.get(i).setName(name);
                                    helper.updateWorkoutById(workoutList.get(i));
                                    adapter.notifyItemChanged(i);
                                }
                                if (!workoutList.get(i).getExercisesList().equals(exercises)) {
                                    helper.updateWorkoutById(workoutList.get(i));
                                    workoutList.get(i).setExercisesList(exercises);
                                }
                                if (workoutList.get(i).getTotalTime() != totalTime) {
                                    workoutList.get(i).setTotalTime(totalTime);
                                    helper.updateWorkoutById(workoutList.get(i));
                                    adapter.notifyItemChanged(i);
                                }
                            }
                        }
                        if (position > workoutList.size()) {
                            WorkoutList workouts = new WorkoutList(name, exercises);
                            workoutList.add(workouts);
                            helper.addWorkoutListItem(workouts);
                            adapter.notifyItemChanged(workoutList.size());
                        }
                    }
                });
    }

    /**
     Creates and displays the options menu.
     * @param menu the view of the item being clicked.
     * @return true if the handler consumed the event.
     */
    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     Handles the events when a menu item is clicked on.
     * @param item the view of the item being clicked.
     * @return if a menu item is clicked on.
     */
    @Override
    public boolean onOptionsItemSelected (@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.addMenuItem:
                Intent intent = new Intent(MainActivity.this, PlayWorkoutActivity.class);
                WorkoutList workouts = new WorkoutList("", new ArrayList<>());
                intent.putExtra("name", workouts.getName());
                intent.putExtra("totalTime", String.valueOf(workouts.getTotalTime()));
                intent.putExtra("exerciseList", (Serializable) workouts.getExercisesList());
                intent.putExtra(getString(R.string.position), workoutList.size()+1);
                launcher.launch(intent);
                return true;
            case R.id.deleteMenuItem:
                int initialListSize = workoutList.size();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.delete_item))
                        .setMessage(getString(R.string.like_to_delete))
                        .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                            helper.deleteAllWorkouts();
                            helper.deleteAllExercises();
                            for (int j = 0; j < initialListSize; j++) {
                                workoutList.remove(0);
                                adapter.notifyItemRemoved(0);
                            }
                            workoutList.clear();
                        })
                        .setNegativeButton(getString(R.string.no), null).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     Associated the data with the viewHolder class.
     */
    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        boolean multiSelect = false;
        ActionMode actionMode;
        ActionMode.Callback callbacks;
        List<WorkoutList> selectedVideos = new ArrayList<>();

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
                myCardView1 = itemView.findViewById(R.id.myCardView1);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            /**
             Displays the card item.
             * @param workouts the selected video to show.
             */
            public void updateView (WorkoutList workouts) {
                myCardView1.setCardBackgroundColor(getResources().getColor(R.color.white));
                workoutName.setText(workouts.getName());
                workoutTime.setText(String.valueOf(workouts.getTotalTime()));
                Log.d(TAG, "updateView: " + workouts.getTotalTime());
            }

            /**
             Selects a video in the recyclerview.
             * @param workouts the selected video to show.
             */
            public void selectItem (WorkoutList workouts) {
                if (multiSelect) {
                    if (selectedVideos.contains(workouts)) {
                        selectedVideos.remove(workouts);
                    }
                    else {
                        selectedVideos.add(workouts);
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
                Log.d(TAG, getString(R.string.on_long_click));
                MainActivity.this.startActionMode(callbacks);
                selectItem(workoutList.get(getAdapterPosition()));
                return true;
            }

            /**
             When the user clicks on the card it will bring them to a new page with the card view.
             * @param view the view of the item being clicked.
             */
            @Override
            public void onClick (View view) {
                if (multiSelect) {
                    selectItem(workoutList.get(getAdapterPosition()));
                }
                else {
                    int position = getAdapterPosition();
                    Log.d(TAG, getString(R.string.on_click_tag) + getAdapterPosition());
                    Intent intent = new Intent(MainActivity.this, CustomWorkoutActivity.class);
                    intent.putExtra("name", workoutList.get(position).getName());
                    intent.putExtra("totalTime", String.valueOf(workoutList.get(position).getTotalTime()));
                    intent.putExtra("exerciseList", (Serializable) workoutList.get(position).getExercisesList());
                    intent.putExtra(getString(R.string.position), position);
                    launcher.launch(intent);
                }
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
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
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
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
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
                    if (menuItem.getItemId() == R.id.deleteMenuItem) {
                        for (int i = 0; i < workoutList.size(); i++) {
                            if (selectedVideos.contains(workoutList.get(i))) {
                                workoutList.remove(i);
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
                    for (WorkoutList workouts : selectedVideos) {
                        helper.deleteWorkout(workouts);
                    }
                    selectedVideos.clear();

                    for (int i = 0; i < workoutList.size(); i++) {
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
            View view = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.card_list_view_item, parent, false);
            return new CustomViewHolder(view);
        }

        /**
         Called by RecyclerView to display the data at the specified position.
         * @param holder updates the view of the recyclerview.
         * @param position position of each card in recyclerView.
         */
        @Override
        public void onBindViewHolder (@NonNull CustomViewHolder holder, int position) {
            for (int i = 0; i < helper.getAllWorkoutLists().size(); i++) {
                if (i == position) {
                    WorkoutList workout = helper.getAllWorkoutLists().get(i);
                    holder.updateView(workout);
                }
            }
        }

        /**
         Gets the number of items in the recyclerView.
         * @return the size of the video list in the database.
         */
        @Override
        public int getItemCount () {
            return helper.getAllWorkoutLists().size();
        }
    }
}