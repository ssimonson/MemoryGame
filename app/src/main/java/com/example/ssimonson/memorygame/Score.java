package com.example.ssimonson.memorygame;

import java.sql.Time;

/**
 * Created by rmeyer on 10/9/2014.
 */
public class Score {
    //private variables
    private int _id;
    private String _name;
    private String _size;
    private String _theme;
    private int _time;
    private int _tries;

    // Empty constructor
    public Score() {

    }

    // constructor
    public Score(int id, String name, String size, String theme, int time, int tries) {
        this._id = id;
        this._name = name;
        this._size = size;
        this._theme = theme;
        this._time = time;
        this._tries = tries;
    }

    // constructor
    public Score(String name, String size, String theme, int time, int tries) {
        this._name = name;
        this._size = size;
        this._theme = theme;
        this._time = time;
        this._tries = tries;
    }

    public int getID() {
        return this._id;
    }

    public void setID(int id) {
        this._id = id;
    }

    public String getName() {
        return this._name;
    }

    public void setName(String name) {
        this._name = name;
    }

    public String getSize() {
        return this._size;
    }

    public void set_size(String size) {
        this._size = size;
    }

    public String getTheme() {
        return this._theme;
    }

    public void set_theme(String theme) {
        this._theme = theme;
    }

    public int getTime() {
        return this._time;
    }

    public void set_time(int time) {
        this._time = time;
    }

    public int getTries() {
        return this._tries;
    }

    public void set_tries(int tries) {
        this._tries = tries;
    }
}
