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
public class WorkoutList {
    private int id;
    private String name;
    private int totalTime;
    private List<Exercises> exercisesList;
    private boolean run;

    /**
     Creates an arrayList of Videos.
     */
    public WorkoutList (String name, List<Exercises> exercises) {
        this.id = -1;
        this.name = name;
        this.exercisesList = exercises;
        this.totalTime = getTotalTime();
        this.run = false;
    }

    public WorkoutList (int id, String name, List<Exercises> exercises, int totalTime, boolean run) {
        this.id = id;
        this.name = name;
        this.exercisesList = exercises;
        this.totalTime = totalTime;
        this.run = run;
    }

    public int getId () {
        return id;
    }

    public void setId (int id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public int getTotalTime () {
        if(exercisesList.size() == 0) {
            return 0;
        }
        this.totalTime = 0;
        for (Exercises exercise: this.exercisesList) {
            this.totalTime += exercise.getTime();
        }
        return this.totalTime;
    }

    public void setTotalTime (int time) {
        this.totalTime = time;
    }

    public List<Exercises> getExercisesList () {
        return exercisesList;
    }

    public void setExercisesList(List<Exercises> exercisesList) {
        this.exercisesList = exercisesList;
    }

    public boolean getRun () {
        return run;
    }

    public void setRun (boolean run) {
        this.run = run;
    }
}