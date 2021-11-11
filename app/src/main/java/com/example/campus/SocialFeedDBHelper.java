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
        // Will change this to be only in a certain location range? TODO
        Cursor c = sqLiteDatabase.rawQuery(String.format("SELECT * from socialFeed"), null);

        int dateTimeIndex = c.getColumnIndex("dateTime");
        int usernameIndex = c.getColumnIndex("username");
        int postContentIndex = c.getColumnIndex("postContent");

        c.moveToFirst();

        ArrayList<SocialFeedDB> socialFeedList = new ArrayList<>();

        while(!c.isAfterLast()) {
            String dateTime = c.getString(dateTimeIndex);
            String username = c.getString(usernameIndex);
            String postContent = c.getString(postContentIndex);

            SocialFeedDB socialFeedPost = new SocialFeedDB(username, postContent, dateTime);
            socialFeedList.add(socialFeedPost);
            c.moveToNext();
        }
        c.close();
        sqLiteDatabase.close();

        return socialFeedList;
    }

    public void saveSocialPosts(String username, String postContent, String dateTime) {
        createTable();
        sqLiteDatabase.execSQL(String.format("INSERT INTO socialFeedDB (username, postContent, dateTime) VALUES ('%s', '%s', '%s', '%s')",
                username, postContent, dateTime));
    }

    public void updateSocialPost(String username, String postContent, String dateTime) {
        createTable();
        sqLiteDatabase.execSQL(String.format("UPDATE socialFeedDB set username = '%s', postContent = '%s', dateTime = '%s'",
                username, postContent, dateTime));
    }
}
