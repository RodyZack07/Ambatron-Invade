package com.example.savesthekunti;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app_database.db";
    private static final int DATABASE_VERSION = 1;

    // Nama tabel dan kolom
    private static final String TABLE_AKUN = "Akun";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_USERNAME = "username";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_PROGRESS = "Progress";
    private static final String COLUMN_PROGRESS_ID = "id";
    private static final String COLUMN_AKUN_ID = "id_akun";
    private static final String COLUMN_LEVEL = "level";
    private static final String COLUMN_SCORE = "score";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Membuat tabel Akun
        String createAkunTable = "CREATE TABLE " + TABLE_AKUN + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT, " +
                COLUMN_EMAIL + " TEXT, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(createAkunTable);

        // Membuat tabel Progress
        String createProgressTable = "CREATE TABLE " + TABLE_PROGRESS + " (" +
                COLUMN_PROGRESS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_AKUN_ID + " INTEGER, " +
                COLUMN_LEVEL + " INTEGER, " +
                COLUMN_SCORE + " INTEGER, " +
                COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(" + COLUMN_AKUN_ID + ") REFERENCES " + TABLE_AKUN + "(" + COLUMN_ID + "))";
        db.execSQL(createProgressTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AKUN);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROGRESS);
        onCreate(db);
    }

    public long addAkun(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username);
        values.put(COLUMN_EMAIL, email);
        values.put(COLUMN_PASSWORD, password);

        long id = db.insert(TABLE_AKUN, null, values);
        // Jangan tutup db di sini jika masih digunakan
        // db.close();
        return id;
    }

    public void saveProgress(int idAkun, int level, int score) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AKUN_ID, idAkun);
        values.put(COLUMN_LEVEL, level);
        values.put(COLUMN_SCORE, score);

        db.insert(TABLE_PROGRESS, null, values);
        // db.close();
    }

    public Cursor getProgressByAkunId(int idAkun) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(TABLE_PROGRESS,
                null,
                COLUMN_AKUN_ID + " = ?",
                new String[]{String.valueOf(idAkun)},
                null, null, null);

}
    }