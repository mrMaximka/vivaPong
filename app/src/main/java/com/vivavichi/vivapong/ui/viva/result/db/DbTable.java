package com.vivavichi.vivapong.ui.viva.result.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.vivavichi.vivapong.model.ResultModel;

import java.util.ArrayList;

public class DbTable {

    public ArrayList<ResultModel> loadAllResults(SQLiteDatabase database){
        Cursor cursor = database.query("result",
                null, null, null, null, null, null);
        ArrayList<ResultModel> list = new ArrayList<>();
        cursor.moveToFirst();
        int id = cursor.getColumnIndex("id");
        int date = cursor.getColumnIndex("date");
        int score = cursor.getColumnIndex("score");

        try {
            do {
                ResultModel model = new ResultModel(
                        cursor.getInt(id),
                        cursor.getString(date),
                        cursor.getLong(score)
                );
                list.add(model);
            }while (cursor.moveToNext());
        }catch (Exception e){
            Log.d("BaseError", e.getMessage());
        }
        cursor.close();

        return list;
    }

    public void updateResult(SQLiteDatabase database, int id, String date, Long score){
        database.execSQL("UPDATE result\n" +
                "   SET date = '" + date + "',\n" +
                "       score = '" + score + "'\n" +
                " WHERE id = "+ id + ";");
    }
}
