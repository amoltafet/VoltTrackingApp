/**
 * This program creates a VideoOpenHelper to get access to the database.
 * CPSC 312-02, Fall 2021
 * Programming Assignment #7
 *  No sources to cite.
 *
 * @author Rebekah Hale
 * @version v2.0 11/23/21
 */
package com.example.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 Handles all of the necessary functions for CRUD to the database.
 */
public class VideoOpenHelper extends SQLiteOpenHelper {
    static final String DATABASE_NAME = "videosDatabase.db";
    static final int DATABASE_VERSION = 1;
    static final String VIDEOS_TABLE = "tableVideos";
    static final String ID = "_id";
    static final String TITLE = "title";
    static final String TYPE = "type";
    static final String IMAGE_ID = "imageId";
    static final String WATCHED = "watched";

    /**
     Constructor for the VideoOpener
     * @param context the context view.
     */
    public VideoOpenHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     Creates the database.
     * @param db the database.
     */
    @Override
    public void onCreate (SQLiteDatabase db) {
        String sqlCreate = "CREATE TABLE " + VIDEOS_TABLE + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE + " TEXT, " +
                TYPE + " TEXT, " +
                IMAGE_ID + " INTEGER, " +
                WATCHED + " TEXT)";
        db.execSQL(sqlCreate);
    }

    /**
     Updates the database.
     * @param db the database.
     * @param newVersion the new version of the database.
     * @param oldVersion the previous version of the database.
     */
    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     Inserts a video into the database.
     * @param exercises the video to be inserted into the database.
     */
    public void insertVideo (Exercises exercises) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, exercises.getTitle());
        contentValues.put(TYPE, exercises.getType());
        contentValues.put(IMAGE_ID, exercises.getImageId());
        contentValues.put(WATCHED, String.valueOf(exercises.isWatched()));
        SQLiteDatabase db = getWritableDatabase();
        db.insert(VIDEOS_TABLE, null, contentValues);
        db.close();
    }

    /**
     Points where in the database the grab information from.
     * @return the cursor of the database.
     */
    public Cursor getSelectAllCursor () {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(VIDEOS_TABLE, new String[]{ID,
                        TITLE,
                        TYPE,
                        IMAGE_ID,
                        WATCHED},
                null, null, null,
                null, null);
        return cursor;
    }

    /**
     Grabs all of the videos in the database.
     * @return the list of videos to the controller.
     */
    public List<Exercises> getSelectAllVideos () {
        List<Exercises> exercises = new ArrayList<>();
        Cursor cursor = getSelectAllCursor();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String title = cursor.getString(1);
            String type = cursor.getString(2);
            int imageId = cursor.getInt(3);
            boolean watched = Boolean.parseBoolean(cursor.getString(4));
            Exercises exercise = new Exercises(id,title, type, imageId,watched);
            exercises.add(exercise);
        }
        return exercises;
    }

    /**
     Updates the select video in the database to have the correct information.
     * @param exercises the video to be updated.
     */
    public void updateVideoById (Exercises exercises) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, exercises.getTitle());
        contentValues.put(TYPE, exercises.getType());
        contentValues.put(IMAGE_ID, exercises.getImageId());
        contentValues.put(WATCHED, String.valueOf(exercises.isWatched()));

        SQLiteDatabase db = getWritableDatabase();
        db.update(VIDEOS_TABLE, contentValues, ID + "=?",
                new String[]{"" + exercises.getId()});
        db.close();
    }

    /**
     Deletes all videos in the database.
     */
    public void deleteAllVideos () {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(VIDEOS_TABLE, null, null);
        db.close();
    }

    /**
     Deletes the specified video.
     * @param exercises the video to be deleted.
     */
    public void deleteSelectVideo (Exercises exercises) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(VIDEOS_TABLE, ID + "=?", new String[]{"" + exercises.getId()});
        db.close();
    }
}