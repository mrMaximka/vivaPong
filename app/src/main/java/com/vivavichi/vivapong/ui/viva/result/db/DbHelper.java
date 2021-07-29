package com.vivavichi.vivapong.ui.viva.result.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "result";
    private static final Integer DB_VERSION = 1;

    public DbHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE result (\n" +
                "    id         INTEGER PRIMARY KEY,\n" +
                "    date       STRING  NOT NULL,\n" +
                "    score      DOUBLE\n" +
                ");");

        db.execSQL("INSERT INTO result (\n" +
                "                     id,\n" +
                "                     date,\n" +
                "                     score\n" +
                "                 )\n" +
                "                 VALUES (\n" +
                "                     '1',\n" +
                "                     'unknown',\n" +
                "                     '0'\n" +
                "                 ),(\n" +
                "                     '2',\n" +
                "                     'unknown',\n" +
                "                     '0'\n" +
                "                 ),(\n" +
                "                    '3',\n" +
                "                    'unknown',\n" +
                "                    '0'\n" +
                "                 ),(\n" +
                "                    '4',\n" +
                "                    'unknown',\n" +
                "                    '0 '\n" +
                "                 ),(\n" +
                "                    '5',\n" +
                "                    'unknown',\n" +
                "                    '0'\n" +
                "                 ),(\n" +
                "                    '6',\n" +
                "                    'unknown',\n" +
                "                    '0'\n" +
                "                 ),(\n" +
                "                    '7',\n" +
                "                    'unknown',\n" +
                "                    '0'\n" +
                "                 ),(\n" +
                "                    '8',\n" +
                "                    'unknown',\n" +
                "                    '0'\n" +
                "                 ),(\n" +
                "                    '9',\n" +
                "                    'unknown',\n" +
                "                    '0'\n" +
                "                 ),(\n" +
                "                    '10',\n" +
                "                    'unknown',\n" +
                "                    '0'\n" +
                "                 );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
