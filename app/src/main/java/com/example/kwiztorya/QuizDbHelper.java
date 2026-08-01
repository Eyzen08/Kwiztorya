package com.example.kwiztorya;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class QuizDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Kwiztorya.db";
    private static final int DATABASE_VERSION = 1;

    // Era progress table
    private static final String TABLE_ERA_PROGRESS = "era_progress";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ERA_ID = "era_id";
    private static final String COLUMN_IS_COMPLETED = "is_completed";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_COMPLETED_AT = "completed_at";

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createEraProgressTable = "CREATE TABLE " + TABLE_ERA_PROGRESS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ERA_ID + " INTEGER UNIQUE, " +
                COLUMN_IS_COMPLETED + " INTEGER DEFAULT 0, " +
                COLUMN_SCORE + " INTEGER DEFAULT 0, " +
                COLUMN_COMPLETED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(createEraProgressTable);

        // Insert first era as unlocked by default
        ContentValues values = new ContentValues();
        values.put(COLUMN_ERA_ID, 0);
        values.put(COLUMN_IS_COMPLETED, 0);
        values.put(COLUMN_SCORE, 0);
        db.insert(TABLE_ERA_PROGRESS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ERA_PROGRESS);
        onCreate(db);
    }

    public void saveEraProgress(int eraId, boolean passed, int score) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ERA_ID, eraId);
        values.put(COLUMN_IS_COMPLETED, passed ? 1 : 0);
        values.put(COLUMN_SCORE, score);

        db.insertWithOnConflict(TABLE_ERA_PROGRESS, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        // If passed, unlock next era
        if (passed && eraId < 4) { // 4 is the last era index
            ContentValues nextEraValues = new ContentValues();
            nextEraValues.put(COLUMN_ERA_ID, eraId + 1);
            nextEraValues.put(COLUMN_IS_COMPLETED, 0);
            nextEraValues.put(COLUMN_SCORE, 0);
            db.insertWithOnConflict(TABLE_ERA_PROGRESS, null, nextEraValues, SQLiteDatabase.CONFLICT_IGNORE);
        }
    }

    public boolean isEraUnlocked(int eraId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ERA_PROGRESS,
                new String[]{COLUMN_IS_COMPLETED},
                COLUMN_ERA_ID + " = ?",
                new String[]{String.valueOf(eraId)},
                null, null, null);

        boolean isUnlocked = false;
        if (cursor.moveToFirst()) {
            isUnlocked = true; // Era exists in progress table
        }
        cursor.close();
        return isUnlocked;
    }
}