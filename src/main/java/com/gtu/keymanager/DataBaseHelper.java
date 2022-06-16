package com.gtu.keymanager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "keyManager.db";
    public static final String TABLE_NAME = "KEY_TABLE";


    public DataBaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME + " (name text, image blob);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME);
    }

    public boolean insert(String name, byte[] img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("name", name);
        cv.put("image", img);
        long insert = db.insert(TABLE_NAME, null, cv);

        if (insert == -1) {
            return false;
        }
        return true;
    }

    public String getName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where name = ?", new String[]{name});
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public Bitmap getImage(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where name = ?", new String[]{name});
        cursor.moveToFirst();
        byte[] bitmap = cursor.getBlob(1);
        Bitmap img = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
        return img;
    }
/*
    public List<FirstModel> getAllRecords() {
        List<FirstModel> returnList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int recordId = cursor.getInt(0);
                String recordName = cursor.getString(1);

                FirstModel firstModel = new FirstModel(recordId, recordName);
                returnList.add(firstModel);

            }while (cursor.moveToNext());
        }
        else {
            // failure. do not add anything to list
        }
        cursor.close();
        db.close();
        return  returnList;
    }

    public boolean deleteRecord(FirstModel firstModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + firstModel.getId();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return  true;
        }
        else  {
            return  false;
        }
    }
    */

}
