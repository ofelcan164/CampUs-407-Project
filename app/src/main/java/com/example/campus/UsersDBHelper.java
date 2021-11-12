package com.example.campus;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class UsersDBHelper {

    SQLiteDatabase sqLiteDatabase;

    public UsersDBHelper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS users " +
                "(id INTEGER PRIMARY KEY, username TEXT, password TEXT, savedLat DOUBLE, savedLng DOUBLE, useCurLocation BOOLEAN)");
    }

    /**
     * Returns a user given a username and password
     * null if there is no such user
     */
    public User getValidUser(String username, String password) {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * from users WHERE username = '%s' AND password = '%s'", username, password), null);

        if (c.getCount() == 0) {
            // Returned data empty, return null -> no valid user with this username and password
            return null;
        }

        // There is a valid user
        int usernameIndex = c.getColumnIndex("username");
        int passwordIndex = c.getColumnIndex("password");
        int latIndex = c.getColumnIndex("savedLat");
        int lngIndex = c.getColumnIndex("savedLng");
        int curLocationIndex = c.getColumnIndex("useCurLocation");

        c.moveToFirst();

        return new User(c.getString(usernameIndex), c.getString(passwordIndex), c.getDouble(latIndex), c.getDouble(lngIndex), (c.getInt(curLocationIndex) != 0));
    }

    /**
     * Inserts a user into the users database
     */
    public void insertUser(User user) {
        createTable();
        ContentValues vals = new ContentValues();
        vals.put("username", user.getUsername());
        vals.put("password", user.getPassword());
        vals.put("savedLat", user.getSavedLocationLat());
        vals.put("savedLng", user.getSaveLocationLng());
        vals.put("useCurLocation",user.getUseCurLocation());
        sqLiteDatabase.insert("users", null, vals);
    }
}
