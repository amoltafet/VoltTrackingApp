/**
 * This program handles the model activity to pass information.
 * CPSC 312-02, Fall 2021
 * Programming Assignment #7
 * Sources:
 *  <div>Icons made by <a href="" title="wanicon">wanicon</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a></div>
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
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 Main Activity screen with a recyclerview shows a card with an image and a textView. Can add and delete Cards.
 */
public class MainActivity extends AppCompatActivity {
    static final String TAG = String.valueOf(R.string.main_activity_tag);
    ActivityResultLauncher<Intent> launcher;
    CustomAdapter adapter;
    WorkoutListOpenHelper helper;
    List<WorkoutList> workoutList;
    private String workoutName = "";
    int positionChanged = 0;

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

        if (helper.getAllWorkoutLists().size() == 0) {
            addListsToDb("abs", 0);
            addListsToDb("legs", 1);
        }


        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, getString(R.string.on_activity_result_tag));
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        String name = data.getStringExtra("name");
                        int totalTime = data.getIntExtra("totalTime", 0);
                        List<Exercises> exercises = (List<Exercises>) data.getSerializableExtra(("exerciseList"));
                        boolean run = false;//data.getBooleanExtra("run", false);
                        int position = data.getIntExtra(getString(R.string.position), 0);

                        for (int i = 0; i < workoutList.size(); i++) {
                            if (position == i) {
                                if (!workoutList.get(i).getName().equals(name)) {
                                    workoutList.get(i).setName(name);
                                    helper.updateWorkoutById(workoutList.get(i));
                                    adapter.notifyItemChanged(i);
                                }
                                if (!workoutList.get(i).getExercisesList().equals(exercises)) {
                                    workoutList.get(i).setExercisesList(exercises);
                                    helper.deleteExerciseList(workoutList.get(i).getId());
                                    for (int j = 0; j < exercises.size(); j++) {
                                        helper.addExerciseListItem(exercises.get(j));
                                        adapter.notifyItemChanged(j);
                                    }
                                    exercises = helper.getAllExercisesLists();
                                    workoutList.get(i).getExercisesList().clear();

                                    for (int j = 0; j < exercises.size(); j++) {
                                        if (exercises.get(j).getParentId() == helper.getAllWorkoutLists().get(i).getId()) {
                                            workoutList.get(i).getExercisesList().add(exercises.get(j));
                                            adapter.notifyItemChanged(j);
                                        }
                                    }
                                }
                                if (workoutList.get(i).getTotalTime() != totalTime) {
                                    workoutList.get(i).setTotalTime(totalTime);
                                    helper.updateWorkoutById(workoutList.get(i));
                                    adapter.notifyItemChanged(i);
                                }
                                if (workoutList.get(i).getRun() != run) {
                                    workoutList.get(i).setRun(run);
                                    helper.updateWorkoutById(workoutList.get(i));
                                    adapter.notifyItemChanged(i);
                                }
                            }
                        }
                        if (position > workoutList.size()) {
                            WorkoutList workouts = new WorkoutList(position, name, exercises, totalTime, run);
                            workoutList.add(workouts);
                            helper.addWorkoutListItem(workouts);
                            adapter.notifyItemChanged(workoutList.size());
                        }
                    }
                }
        );
        workoutList = helper.getAllWorkoutLists();

        for (int i = 0; i < helper.getAllWorkoutLists().size(); i++) {
            adapter.notifyItemChanged(i);
        }

        List<Exercises> allExercises = new ArrayList<>();
        for (Exercises exercise: workoutList.get(0).getExercisesList()) {
                allExercises.add(exercise);

        }

        for (WorkoutList workout: workoutList) {
            workout.getExercisesList().clear();
        }

        for (int i = 0; i < workoutList.size(); i++) {
            for (int j = 0; j < allExercises.size(); j++) {
                if (allExercises.get(j).getParentId() == workoutList.get(i).getId()) {
                    workoutList.get(i).getExercisesList().add(allExercises.get(j));
                }
            }
        }

        for (int i = 0; i < workoutList.size(); i++) {
            adapter.notifyItemChanged(i);
        }

    }

    public void addListsToDb (String name, int position) {
        List<Exercises> exercises = new ArrayList<>();
        exercises.add(new Exercises("", 0));
        WorkoutList newWorkoutList = new WorkoutList(name, exercises);
        workoutList.add(newWorkoutList);
        int time = workoutList.get(workoutList.size() - 1).getTotalTime();
        workoutList.get(workoutList.size() - 1).setTotalTime(time);
        helper.addWorkoutListItem(workoutList.get(workoutList.size() - 1));
        adapter.notifyItemChanged(workoutList.size() - 1);
        workoutList = helper.getAllWorkoutLists();

        for (int i = 0; i < helper.getAllWorkoutLists().size(); i++) {
            adapter.notifyItemChanged(i);
        }

        for (int i = 0; i < exercises.size(); i++) {
            exercises.get(i).setParentId(workoutList.get(position).getId());
        }

        for (int i = 0; i < exercises.size(); i++) {
            helper.addExerciseListItem(exercises.get(i));
            adapter.notifyItemChanged(i);
        }

        exercises = helper.getAllExercisesLists();
        workoutList.get(position).getExercisesList().clear();


        for (int i = 0; i < exercises.size(); i++) {
            if (exercises.get(i).getParentId() == helper.getAllWorkoutLists().get(position).getId()) {
                workoutList.get(position).getExercisesList().add(exercises.get(i));
                adapter.notifyItemChanged(i);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Create A New Workout List");

                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        workoutName = input.getText().toString();
                        addListsToDb(workoutName, workoutList.size());
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;

            case R.id.menuDeleteItem:
                int initialListSize = workoutList.size();
                builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete All Workouts")
                        .setMessage("Are you sure you want to delete all of the workouts?")
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

    private static class MyDragShadowBuilder extends View.DragShadowBuilder {
        private static Drawable shadow;

        public MyDragShadowBuilder (View v) {
            super(v);
            shadow = new ColorDrawable(Color.LTGRAY);
        }

        @Override
        public void onProvideShadowMetrics (Point outShadowSize, Point outShadowTouchPoint) {
            super.onProvideShadowMetrics(outShadowSize, outShadowTouchPoint);
            int width, height;
            width = getView().getWidth() / 2;
            height = getView().getHeight() / 2;
            shadow.setBounds(0, 0, width, height);
            outShadowSize.set(width, height);
            outShadowTouchPoint.set(width / 2, height / 2);
        }

        @Override
        public void onDrawShadow (Canvas canvas) {
            super.onDrawShadow(canvas);

        }
    }

    private class MyDragEventListener implements View.OnDragListener {

        public boolean onDrag (View v, DragEvent event) {
            final int action = event.getAction();
            float firstYPosition = 0;

            switch (action) {
                case DragEvent.ACTION_DRAG_STARTED:
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                        v.setVisibility(View.INVISIBLE);
                        firstYPosition = event.getY();
                        return true;
                    }
                    return false;
                case DragEvent.ACTION_DROP:
                    float positionY = event.getY();
                    if (positionY > firstYPosition) {
                        positionChanged = 1;
                    }
                    else if (positionY < firstYPosition) {
                        positionChanged = -1;
                    }
                    v.invalidate();
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setVisibility(View.VISIBLE);
                    v.invalidate();
                    return true;
                default:
                    break;
            }
            return false;
        }
    }

    /**
     Associated the data with the viewHolder class.
     */
    class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
        boolean multiSelect = false;
        boolean drag = false;
        ActionMode actionMode;
        ActionMode.Callback callbacks;
        List<WorkoutList> selectedWorkoutList = new ArrayList<>();

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
                workoutName = itemView.findViewById(R.id.workout_name);
                workoutTime = itemView.findViewById(R.id.workout_time);
                myCardView1 = itemView.findViewById(R.id.myCardView1);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            /**
             Displays the card item.
             * @param workouts the selected video to show.
             */
            public void updateView (WorkoutList workouts) {
                List<Exercises> newExerciseList = new ArrayList<>();
                for (int i = 0; i < workouts.getExercisesList().size(); i++) {

                    if (workouts.getExercisesList().get(i).getParentId() == workouts.getId()) {
                        newExerciseList.add(workouts.getExercisesList().get(i));
                    }
                }
                workouts.setExercisesList(newExerciseList);



                if (workouts.getTotalTime() >= 60) {
                    double minute = TimeUnit.SECONDS.toMinutes(workouts.getTotalTime()) - (TimeUnit.SECONDS.toHours(workouts.getTotalTime()) * 60);
                    double seconds = (workouts.getTotalTime() % (60 * minute)) * .01;
                    workoutTime.setText(String.valueOf(minute + seconds));
                }
                else {
                    workoutTime.setText(String.valueOf(workouts.getTotalTime() + " seconds"));
                }
                myCardView1.setCardBackgroundColor(getResources().getColor(R.color.white));
                workoutName.setText(workouts.getName());

                Log.d(TAG, "updateView: " + workouts.getTotalTime());
            }

            /**
             Selects a video in the recyclerview.
             * @param workouts the selected video to show.
             */
            public void selectItem (WorkoutList workouts) {
                if (multiSelect) {
                    if (selectedWorkoutList.contains(workouts)) {
                        selectedWorkoutList.remove(workouts);
                        myCardView1.setCardBackgroundColor(getResources().getColor(R.color.white));
                    }
                    else {
                        selectedWorkoutList.add(workouts);
                        myCardView1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));

                    }
                    if (selectedWorkoutList.size() == 1) {
                        actionMode.setTitle(selectedWorkoutList.size() +
                                getString(R.string.item_selected));
                    }
                    actionMode.setTitle(selectedWorkoutList.size() +
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
                if (multiSelect) {
                    multiSelect = false;
                    drag = true;

                    ClipData.Item item = new ClipData.Item((Intent) view.getTag());
                    ClipData dragData = new ClipData((CharSequence) view.getTag(),
                            new String[] { ClipDescription.MIMETYPE_TEXT_PLAIN },
                            item);

                    View.DragShadowBuilder myShadow = new MyDragShadowBuilder(this.myCardView1);
                    view.startDrag(dragData, myShadow, null, 0);

                    MyDragEventListener dragListen = new MainActivity.MyDragEventListener();
                    view.setOnDragListener(dragListen);

                    myCardView1.setVisibility(View.INVISIBLE);
                    while (drag) {
                        if (positionChanged == -1) {
                            if ((workoutList.size() - 1) != getAdapterPosition()) {
                                for (int i = 0; i < workoutList.size(); i++) {
                                    if (i == getAdapterPosition()) {
                                        WorkoutList temp = new WorkoutList();
                                        temp = workoutList.get(i);
                                        workoutList.get(i).setEqual(workoutList.get(i - 1));
                                        workoutList.get(i - 1).setEqual(temp);
                                        adapter.notifyItemChanged(i);
                                        adapter.notifyItemChanged(i + 1);
                                    }
                                }
                            }
                        } else if (positionChanged == 1) {
                            if (0 != getAdapterPosition()) {
                                for (int i = 0; i < workoutList.size(); i++) {
                                    if (i == getAdapterPosition()) {
                                        WorkoutList temp = new WorkoutList();
                                        temp = workoutList.get(i);
                                        workoutList.get(i).setEqual(workoutList.get(i + 1));
                                        workoutList.get(i - 1).setEqual(temp);
                                        adapter.notifyItemChanged(i);
                                        adapter.notifyItemChanged(i - 1);
                                    }
                                }
                            }
                        }
                    }
                    myCardView1.setVisibility(View.VISIBLE);
                    drag = false;
                    multiSelect = true;

                    for (int i = 0; i < workoutList.size(); i++) {
                        adapter.notifyItemChanged(i);
                    }
                }
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
                    intent.putExtra("parentId", workoutList.get(position).getId());
                    intent.putExtra("run", workoutList.get(position).getRun());
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
                    if (menuItem.getItemId() == R.id.camDeleteItem) {
                        for (int i = 0; i < workoutList.size(); i++) {
                            if (selectedWorkoutList.contains(workoutList.get(i))) {
                                helper.deleteWorkout(workoutList.get(i));
                                helper.deleteExerciseList(workoutList.get(i).getId());
                                workoutList.get(i).getExercisesList().clear();
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
                    selectedWorkoutList.clear();

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