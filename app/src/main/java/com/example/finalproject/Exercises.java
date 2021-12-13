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

import java.io.Serializable;

/**
 Video class sets the video info, sets the info safely, and gets the indo safely.
 */
public class Exercises implements Serializable {
    private int id;
    private int parentId;
    private String name;
    private int time;


    public Exercises (String name, int time) {
        this.id = -1;
        this.name = name;
        this.time = time;
    }

    public Exercises (int id, int parentId, String name, int time) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.time = time;
    }


    public Exercises (int parentId) {
        this.id = -1;
        this.parentId = parentId;
        this.name = "";
        this.time = 0;

    }

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public int getParentId () {
        return parentId;
    }

    public void setParentId (int parentId) {
        this.parentId = parentId;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getTime () {
        return time;
    }

    public void setTime (int time) {
        this.time = time;
    }

}