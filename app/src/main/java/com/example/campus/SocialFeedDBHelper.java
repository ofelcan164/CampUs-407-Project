package com.example.campus;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class SocialFeedDBHelper {

    SQLiteDatabase sqLiteDatabase;

    public SocialFeedDBHelper(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteDatabase = sqLiteDatabase;
    }

    public void createTable() {
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS socialFeed " +
                "(id INTEGER PRIMARY KEY, username TEXT, postContent TEXT, dateTime TEXT)");
    }

    public ArrayList<SocialFeedDB> readSocialPosts() {
        createTable();
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * from socialFeed"), null);
        return null;
    }
}
