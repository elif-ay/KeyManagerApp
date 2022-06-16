package com.gtu.keymanager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class KeyDatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "KEY_TABLE";
    public static final String COLUMN_NAME = "NAME";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_IMAGE = "IMAGE";

    public KeyDatabaseHelper(@Nullable Context context) {
        super(context, "keys.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTableStatement = "CREATE TABLE " + TABLE_NAME +
                " ("+ COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_NAME + " TEXT, " + COLUMN_IMAGE + " TEXT )";

        sqLiteDatabase.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    public boolean addRecord(KeyModel keyModel, byte[] img) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COLUMN_NAME, keyModel.getKeyName());
        cv.put(COLUMN_IMAGE, keyModel.getImage());
        long insert = db.insert(TABLE_NAME, null, cv);

        if (insert == -1) {
            return false;
        }
        return true;
    }

    public List<KeyModel> getAllRecords() {
        List<KeyModel> returnList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                int recordId = cursor.getInt(0);
                String recordName = cursor.getString(1);
                String recordBytes = cursor.getString(2);
                KeyModel keyModel = new KeyModel(recordId, recordName, recordBytes);
                returnList.add(keyModel);

            }while (cursor.moveToNext());
        }
        else {
            // failure. do not add anything to list
        }
        cursor.close();
        db.close();
        return  returnList;
    }

    public boolean deleteRecord(KeyModel keyModel) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " = " + keyModel.getId();

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return  true;
        }
        else  {
            return  false;
        }
    }
}
