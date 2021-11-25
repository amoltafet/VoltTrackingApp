/**
 * This program creates a video to be shown in the recycler view.
 * CPSC 312-02, Fall 2021
 * Programming Assignment #7
 *  No sources to cite.
 *
 * @author Rebekah Hale
 * @version v2.0 11/23/21
 */

package com.example.finalproject;

import androidx.annotation.NonNull;

/**
 Video class sets the video info, sets the info safely, and gets the indo safely.
 */
public class Exercises {
    private int id;
    private String title;
    private String type;
    private int imageId;
    private boolean watched;

    /**
     Sets the videos properties.
     * @param title the video title.
     * @param type the type of video.
     * @param imageId the video imageId.
     * @param watched if the video has been watched.
     */
    public Exercises (String title, String type, int imageId, boolean watched) {
        this.id = -1;
        this.title = title;
        this.type = type;
        this.imageId = imageId;
        this.watched = watched;
    }

    /**
     Sets the videos properties.
     * @param id the id of where the video is located in the SQL database
     * @param title the video title.
     * @param type the type of video.
     * @param imageId the video imageId.
     * @param watched if the video has been watched.
     */
    public Exercises(int id, String title, String type, int imageId, boolean watched) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.imageId = imageId;
        this.watched = watched;
    }

    /**
     Gets the video title safely.
     * @return the video title.
     */
    public String getTitle () {
        return title;
    }

    /**
     Sets the video title safely.
     * @param title the video title.
     */
    public void setTitle (String title) {
        this.title = title;
    }

    /**
     Gets the type of video safely.
     * @return the type of video.
     */
    public String getType () {
        return type;
    }

    /**
     Sets the type of video safely.
     * @param type the type of video.
     */
    public void setType (String type) {
        this.type = type;
    }

    /**
     Gets the video image Id safely.
     * @return the video imageId.
     */
    public int getImageId () {
        return imageId;
    }

    /**
     Sets the video imageId safely.
     * @param imageId the video imageId.
     */
    public void setImageId (int imageId) {
        this.imageId = imageId;
    }

    /**
     Gets if the video has been watched safely.
     * @return if the video has been watched.
     */
    public boolean isWatched () {
        return watched;
    }

    /**
     Sets if the video has been watched safely.
     * @param watched if the video has been watched.
     */
    public void setWatched (boolean watched) {
        this.watched = watched;
    }

    /**
     Gets the video id safely.
     * @return the id safely.
     */
    public int getId () {
        return this.id;
    }

    /**
     Prints out a nice string of all of the videos.
     * @return the videos.
     */
    @NonNull
    @Override
    public String toString() {
        return id + String.valueOf(R.string.space) + title;
    }
}