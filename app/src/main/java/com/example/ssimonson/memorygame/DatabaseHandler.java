package com.example.ssimonson.memorygame;

/**
 * Created by rmeyer on 10/9/2014.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "memoryGame";

    //region Scores Table
    private static final String TABLE_SCORES = "scores";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_SIZE = "size";
    private static final String KEY_TIME = "time";
    private static final String KEY_TRIES = "tries";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SCORES + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_SIZE + " TEXT," + KEY_TIME + " INT," + KEY_TRIES + " INT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORES);

        // Create tables again
        onCreate(db);
    }

    // region CRUD(Create, Read, Update, Delete) Operations
    void addScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, score.getName());
        values.put(KEY_SIZE, score.getSize());
        values.put(KEY_TIME, score.getTime());
        values.put(KEY_TRIES, score.getTries());

        // Inserting Row
        db.insert(TABLE_SCORES, null, values);
        db.close(); // Closing database connection
    }

    public Score getScore(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_SCORES, new String[] { KEY_ID,
                        KEY_NAME, KEY_SIZE, KEY_TIME, KEY_TRIES }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Score score = new Score(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getInt(3), cursor.getInt(4));
        return score;
    }

    public List<Score> getAllScores() {
        return getAllScores("size, tries");
    }
    public List<Score> getAllScores(String orderBy) {
        List<Score> scoreList = new ArrayList<Score>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_SCORES + " ORDER BY " + orderBy;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Score score = new Score();
                score.setID(Integer.parseInt(cursor.getString(0)));
                score.setName(cursor.getString(1));
                score.set_size(cursor.getString(2));
                score.set_time(cursor.getInt(3));
                score.set_tries(cursor.getInt(4));
                // Adding contact to list
                scoreList.add(score);
            } while (cursor.moveToNext());
        }

        // return score list
        return scoreList;
    }

    public int updateScore(Score contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_SIZE, contact.getSize());
        values.put(KEY_TIME, contact.getTime());
        values.put(KEY_TRIES, contact.getTries());

        // updating row
        return db.update(TABLE_SCORES, values, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
    }

    public void deleteScore(Score score) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCORES, KEY_ID + " = ?",
                new String[] { String.valueOf(score.getID()) });
        db.close();
    }

    public void deleteAllScores() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_SCORES, "", null);
        db.close();
    }

    public int getScoresCount() {
        String countQuery = "SELECT * FROM " + TABLE_SCORES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }
    //endregion
    //endregion

}
