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
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 Main Activity screen with a recyclerview shows a card with an image and a textView. Can add and delete Cards.
 */
public class MainActivity extends AppCompatActivity {
    static final String TAG = String.valueOf(R.string.main_activity_tag);
    ActivityResultLauncher<Intent> launcher;
    CustomAdapter adapter;
    VideoOpenHelper helper;
    VideoList videoList;

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

        helper = new VideoOpenHelper(this);
        videoList = new VideoList();
        if (helper.getSelectAllVideos().size() == 0) {
            videoList.getVideoList().add(new Exercises(getString(R.string.witch_academia), getString(R.string.series), R.drawable.witch, true));
            videoList.getVideoList().add(new Exercises(getString(R.string.paprika), getString(R.string.movie), R.drawable.paprika, false));
            videoList.getVideoList().add(new Exercises(getString(R.string.penguin_drum), getString(R.string.series), R.drawable.penguin, true));
            for (int i = 0; i < videoList.getVideoList().size(); i++) {
                helper.insertVideo(videoList.getVideoList().get(i));
                adapter.notifyItemChanged(i);
            }
        }

        videoList.setVideoList(helper.getSelectAllVideos());
        for (int i = 0; i < helper.getSelectAllVideos().size(); i++) {
            adapter.notifyItemChanged(i);
        }
        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Log.d(TAG, getString(R.string.on_activity_result_tag));
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        String type = data.getStringExtra(getString(R.string.type));
                        String title = data.getStringExtra(getString(R.string.title));
                        int imageId = data.getIntExtra(getString(R.string.image_id), 0);
                        boolean watched = data.getBooleanExtra(getString(R.string.watched), false);
                        int position = data.getIntExtra(getString(R.string.position), 0);

                        for (int i = 0; i < videoList.getVideoList().size(); i++) {
                            if (position == i) {
                                // the same card saved
                                if (!videoList.getVideoList().get(i).getTitle().equals(title)) {
                                    videoList.getVideoList().get(i).setTitle(title);
                                    helper.updateVideoById(videoList.getVideoList().get(i));
                                    adapter.notifyItemChanged(i);
                                }
                                if (!videoList.getVideoList().get(i).getType().equals(type)) {
                                    helper.updateVideoById(videoList.getVideoList().get(i));
                                    videoList.getVideoList().get(i).setType(type);
                                }
                                if (videoList.getVideoList().get(i).getImageId() != imageId) {
                                    videoList.getVideoList().get(i).setImageId(imageId);
                                    helper.updateVideoById(videoList.getVideoList().get(i));
                                    adapter.notifyItemChanged(i);
                                }
                                if (!videoList.getVideoList().get(i).isWatched() == watched) {
                                    videoList.getVideoList().get(i).setWatched(watched);
                                    adapter.notifyItemChanged(i);
                                }
                            }
                        }
                        if (position > videoList.getVideoList().size()) {
                            Exercises exercises = new Exercises(title, type, imageId, watched);
                            videoList.getVideoList().add(exercises);
                            helper.insertVideo(exercises);
                            adapter.notifyItemChanged(videoList.getVideoList().size());
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
                Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                Exercises exercises1 = new Exercises(getString(R.string.empty),getString(R.string.other), R.drawable.image,false);
                intent.putExtra(getString(R.string.title), exercises1.getTitle());
                intent.putExtra(getString(R.string.type), exercises1.getType());
                intent.putExtra(getString(R.string.image_id), exercises1.getImageId());
                intent.putExtra(getString(R.string.watched), exercises1.isWatched());
                intent.putExtra(getString(R.string.position), videoList.getVideoList().size()+1);
                launcher.launch(intent);
                return true;
            case R.id.deleteMenuItem:
                int initialListSize = videoList.getVideoList().size();
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getString(R.string.delete_item))
                        .setMessage(getString(R.string.like_to_delete))
                        .setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                            helper.deleteAllVideos();
                            for (int j = 0; j < initialListSize; j++) {
                                videoList.getVideoList().remove(0);
                                adapter.notifyItemRemoved(0);
                            }
                            videoList.getVideoList().clear();
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
        VideoList selectedVideos = new VideoList();

        /**
         Provides functionality for delete, add, and view recycler item.
         */
        class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
            TextView myText1;
            ImageView myImage1;
            CardView myCardView1;

            /**
             Constructor for Custom View Holder.
             * @param itemView the view of the recycler.
             */
            public CustomViewHolder (@NonNull View itemView) {
                super(itemView);
                myText1 = itemView.findViewById(R.id.myText1);
                myImage1 = itemView.findViewById(R.id.myImage1);
                myCardView1 = itemView.findViewById(R.id.myCardView1);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            /**
             Displays the card item.
             * @param exercises the selected video to show.
             */
            public void updateView (Exercises exercises) {
                myCardView1.setCardBackgroundColor(getResources().getColor(R.color.white));
                myText1.setText(exercises.getTitle());
                myImage1.setImageResource(exercises.getImageId());
            }

            /**
             Selects a video in the recyclerview.
             * @param exercises the selected video to show.
             */
            public void selectItem (Exercises exercises) {
                if (multiSelect) {
                    if (selectedVideos.getVideoList().contains(exercises)) {
                        selectedVideos.getVideoList().remove(exercises);
                    }
                    else {
                        selectedVideos.getVideoList().add(exercises);
                        myCardView1.setCardBackgroundColor(getResources().getColor(R.color.teal_200));
                    }
                    if (selectedVideos.getVideoList().size() == 1) {
                        actionMode.setTitle(selectedVideos.getVideoList().size() +
                                getString(R.string.item_selected));
                    }
                    actionMode.setTitle(selectedVideos.getVideoList().size() +
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
                selectItem(videoList.getVideoList().get(getAdapterPosition()));
                return true;
            }

            /**
             When the user clicks on the card it will bring them to a new page with the card view.
             * @param view the view of the item being clicked.
             */
            @Override
            public void onClick (View view) {
                if (multiSelect) {
                    selectItem(videoList.getVideoList().get(getAdapterPosition()));
                }
                else {
                    int position = getAdapterPosition();
                    Log.d(TAG, getString(R.string.on_click_tag) + getAdapterPosition());
                    Intent intent = new Intent(MainActivity.this, VideoDetailActivity.class);
                    intent.putExtra(getString(R.string.title), videoList.getVideoList().get(position).getTitle());
                    intent.putExtra(getString(R.string.type), videoList.getVideoList().get(position).getType());
                    intent.putExtra(getString(R.string.image_id), videoList.getVideoList().get(position).getImageId());
                    intent.putExtra(getString(R.string.watched), videoList.getVideoList().get(position).isWatched());
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
                    switch (menuItem.getItemId()) {
                        case R.id.deleteMenuItem:
                            for (int i = 0; i < videoList.getVideoList().size(); i++) {
                                if (selectedVideos.getVideoList().contains(videoList.getVideoList().get(i))) {
                                    videoList.getVideoList().remove(i);
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
                    for (Exercises exercises : selectedVideos.getVideoList()) {
                        helper.deleteSelectVideo(exercises);
                    }
                    selectedVideos.getVideoList().clear();

                    for (int i = 0; i < videoList.getVideoList().size(); i++) {
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
            for (int i = 0; i < helper.getSelectAllVideos().size(); i++) {
                if (i == position) {
                    Exercises exercises = helper.getSelectAllVideos().get(i);// check
                    holder.updateView(exercises);
                }
            }
        }

        /**
         Gets the number of items in the recyclerView.
         * @return the size of the video list in the database.
         */
        @Override
        public int getItemCount () {
            return helper.getSelectAllVideos().size();
        }
    }
}