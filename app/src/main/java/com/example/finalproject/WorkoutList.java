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

    /**
     Creates an arrayList of Videos.
     */
    public WorkoutList () {
        this.id = -1;
        this.name = "";
        this.totalTime = 0;
        this.exercisesList = new ArrayList<>();
    }

    public WorkoutList (String name, List<Exercises> exercises) {
        this.id = -1;
        this.name = name;
        this.exercisesList = exercises;
        this.totalTime = getTotalTime();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Exercises> getExercisesList() {
        return exercisesList;
    }

    public void setExercisesList(List<Exercises> exercisesList) {
        this.exercisesList = exercisesList;
    }

    public int getTotalTime () {
        for (Exercises exercise: this.exercisesList) {
            this.totalTime += exercise.getTime();
        }
        return this.totalTime;
    }

    public void setTotalTime (int time) {
        this.totalTime = time;
    }
}