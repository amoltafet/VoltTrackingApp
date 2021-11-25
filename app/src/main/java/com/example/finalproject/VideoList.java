/**
 * This program creates a videoList to be shown in the recycler view.
 * CPSC 312-02, Fall 2021
 * Programming Assignment #7
 *  No sources to cite.
 *
 * @author Rebekah Hale
 * @version v2.0 11/23/21
 */
package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

/**
 Creates and gets a videoList.
 */
public class VideoList {
    private List<Exercises> exercisesList;

    /**
     Creates an arrayList of Videos.
     */
    public VideoList () {
        this.exercisesList = new ArrayList<>();
    }

    /**
     Gets the videoList safely.
     * @return the videoList.
     */
    public List<Exercises> getVideoList() {
        return exercisesList;
    }

    /**
     Sets the database video list to the model video list.
     * @param exercises the list of videos.
     */
    public void setVideoList (List<Exercises> exercises) {
        this.exercisesList.clear();
        this.exercisesList = exercises;
    }
}