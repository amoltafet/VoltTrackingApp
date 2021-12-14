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
public class WorkoutListOpenHelper extends SQLiteOpenHelper {

    static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "finalProject.db";

    final String WORKOUT_LIST_TABLE = "tableWorkoutList";
    final String WORKOUT_LIST_ID = "_id";
    final String WORKOUT_LIST_NAME = "name";
    final String WORKOUT_TOTAL_TIME = "totalTime";
    final String WORKOUT_RUN = "run";

    final String EXERCISE_LIST_TABLE = "child_list";
    final String EXERCISE_LIST_ID = "_id";
    final String EXERCISE_WORKOUT_LIST_ID = "parent_id";
    final String EXERCISE_LIST_NAME = "name";
    final String EXERCISE_LIST_TIME = "time";

    public WorkoutListOpenHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        String CREATE_MAIN_LIST_TABLE = "CREATE TABLE " + WORKOUT_LIST_TABLE + "("
                + WORKOUT_LIST_ID + " INTEGER PRIMARY KEY,"
                + WORKOUT_LIST_NAME + " TEXT,"
                + WORKOUT_TOTAL_TIME + " INTEGER,"
                + WORKOUT_RUN + " STRING)";

        String CREATE_TABLE_CHILD_LIST = "CREATE TABLE " + EXERCISE_LIST_TABLE + "("
                + EXERCISE_LIST_ID + " INTEGER PRIMARY KEY,"
                + EXERCISE_WORKOUT_LIST_ID + " INTEGER,"
                + EXERCISE_LIST_NAME + " TEXT,"
                + EXERCISE_LIST_TIME + " INTEGER)";

        db.execSQL(CREATE_MAIN_LIST_TABLE);
        db.execSQL(CREATE_TABLE_CHILD_LIST);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WORKOUT_LIST_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EXERCISE_LIST_TABLE);
        onCreate(db);
    }

    public void addWorkoutListItem (WorkoutList workouts) { SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(WORKOUT_LIST_NAME, workouts.getName());
        values.put(WORKOUT_TOTAL_TIME, workouts.getTotalTime());
        values.put(WORKOUT_RUN, String.valueOf(workouts.getRun()));
        db.insert(WORKOUT_LIST_TABLE, null, values);
        db.close();
    }

    public void addExerciseListItem (Exercises exercises) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EXERCISE_LIST_NAME, exercises.getName());
        values.put(EXERCISE_LIST_TIME, exercises.getTime());
        values.put(EXERCISE_WORKOUT_LIST_ID, exercises.getParentId());
        db.insert(EXERCISE_LIST_TABLE, null, values);
        db.close();
    }

    public void deleteWorkout (WorkoutList workouts) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(WORKOUT_LIST_TABLE, WORKOUT_LIST_ID + " = ?", new String[] { String.valueOf(workouts.getId()) });
        db.close();
    }

    public void deleteExercise (Exercises exercises) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(EXERCISE_LIST_TABLE, EXERCISE_LIST_ID + " = ?", new String[] { String.valueOf(exercises.getId()) });
        db.close();
    }

    public void deleteAllWorkouts () {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(WORKOUT_LIST_TABLE, null, null);
        db.close();
    }

    public void deleteAllExercises () {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(EXERCISE_LIST_TABLE, null, null);
        db.close();
    }

    public List<WorkoutList> getAllWorkoutLists () {
        List<WorkoutList> workouts = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = getSelectAllWorkoutsCursor();

        while (cursor.moveToNext()) {

            int id = cursor.getInt(0);
            String name = cursor.getString(1);
            int totalTime = cursor.getInt(2);
            String run = cursor.getString(3);
            List <Exercises> exercisesList = getAllExercisesLists();
            WorkoutList workout = new WorkoutList(id, name, exercisesList, totalTime, Boolean.getBoolean(run));
            workouts.add(workout);
        }
        cursor.close();
        db.close();
        return workouts;
    }


    public List<Exercises> getAllExercisesLists () {
        List<Exercises> exercises = new ArrayList<>();

        SQLiteDatabase exercise_db = this.getReadableDatabase();
        Cursor exercise_cursor = getSelectAllExercisesCursor();

        while (exercise_cursor.moveToNext()) {

            int exerciseId = exercise_cursor.getInt(0);
            int parentId = exercise_cursor.getInt(1);
            String exerciseName = exercise_cursor.getString(2);
            int time = exercise_cursor.getInt(3);
            Exercises exercise = new Exercises(exerciseId, parentId, exerciseName, time);
            exercises.add(exercise);
        }
        exercise_cursor.close();
        exercise_db.close();
        return exercises;
    }

    public Cursor getSelectAllWorkoutsCursor () {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(WORKOUT_LIST_TABLE, new String[]{
                        WORKOUT_LIST_ID,
                        WORKOUT_LIST_NAME,
                        WORKOUT_TOTAL_TIME,
                        WORKOUT_RUN},
                null, null, null,
                null, null);
        return cursor;
    }

    public Cursor getSelectAllExercisesCursor () {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(EXERCISE_LIST_TABLE, new String[]{
                        EXERCISE_LIST_ID,
                        EXERCISE_WORKOUT_LIST_ID,
                        EXERCISE_LIST_NAME,
                        EXERCISE_LIST_TIME},
                null, null, null,
                null, null);
        return cursor;
    }

    public void updateWorkoutById (WorkoutList workout) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WORKOUT_LIST_NAME, workout.getName());
        contentValues.put(WORKOUT_TOTAL_TIME, workout.getTotalTime());
        contentValues.put(WORKOUT_RUN, workout.getRun());

        SQLiteDatabase db = getWritableDatabase();
        db.update(WORKOUT_LIST_TABLE, contentValues, WORKOUT_LIST_ID + "=?",
                new String[]{"" + workout.getId()});
        db.close();
    }

    public void updateExerciseById (Exercises exercises) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(EXERCISE_LIST_NAME, exercises.getName());
        contentValues.put(EXERCISE_LIST_TIME, exercises.getTime());

        SQLiteDatabase db = getWritableDatabase();
        db.update(WORKOUT_LIST_TABLE, contentValues, WORKOUT_LIST_ID + "=?",
                new String[]{"" + exercises.getId()});
        db.close();
    }

}