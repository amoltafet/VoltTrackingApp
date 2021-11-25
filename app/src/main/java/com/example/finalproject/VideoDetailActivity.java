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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 Model for the video detail page.
 */
public class VideoDetailActivity extends AppCompatActivity {

    /**
     Handles the functionality of when the activity is created.
     * @param savedInstanceState saves the sate for when the app is closed.
     */
    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_detail_activity);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.add_watch);

        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(getString(R.string.title));
            String type = intent.getStringExtra(getString(R.string.type));
            int imageId = intent.getIntExtra(getString(R.string.image_id), 0);
            boolean watched = intent.getBooleanExtra(getString(R.string.watched), false);
            int position = intent.getIntExtra(getString(R.string.position), 0);

            EditText titleTextView = findViewById(R.id.titleEditableTextView);
            titleTextView.setText(title);
            titleTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {

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

            Spinner spinner = (Spinner) findViewById(R.id.typesSpinner);
            if (type.equals(getString(R.string.series))) {
                spinner.setSelection(0);
            }
            else if (type.equals(getString(R.string.movie))) {
                spinner.setSelection(1);
            }
            else if (type.equals(getString(R.string.other))) {
                spinner.setSelection(2);
            }

            CheckBox checkBox = findViewById(R.id.checkBox);
            checkBox.setChecked(watched);

            ImageView image = findViewById(R.id.imageView);
            image.setImageResource(imageId);

            Button saveButton = findViewById(R.id.saveButton);
            saveButton.setOnClickListener(new View.OnClickListener() {
                /**
                 On click the video info will save to be displayed in the recycler view.
                 * @param view the view of the item being clicked.
                 */
                @Override
                public void onClick (View view) {
                    if (titleTextView.getText().toString().equals(getString(R.string.empty))) {
                        Toast.makeText(VideoDetailActivity.this, getString(R.string.save_info), Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent intent = new Intent();
                        intent.putExtra(getString(R.string.title), titleTextView.getText().toString());
                        intent.putExtra(getString(R.string.type), spinner.getSelectedItem().toString());
                        intent.putExtra(getString(R.string.image_id), imageId);
                        intent.putExtra(getString(R.string.watched), checkBox.isChecked());
                        intent.putExtra(getString(R.string.position), position);
                        VideoDetailActivity.this.setResult(Activity.RESULT_OK, intent);
                        VideoDetailActivity.this.finish();
                    }
                }
            });
        }
    }

    /**
     Handles events when a menu item is clicked on.
     * @param item the view of the item being clicked.
     * @return true if the handler consumed the event.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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