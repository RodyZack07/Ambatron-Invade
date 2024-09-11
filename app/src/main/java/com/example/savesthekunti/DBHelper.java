package com.example.savesthekunti;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "savesthekunti.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create table Akun
        String CREATE_TABLE_AKUN = "CREATE TABLE Akun (" +
                "id_akun INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username VARCHAR(50), " +
                "email VARCHAR(100), " +
                "password VARCHAR(255), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE_AKUN);

        // Create table Profile
        String CREATE_TABLE_PROFILE = "CREATE TABLE Profile (" +
                "id_profile INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_akun INTEGER, " +
                "photo_profile VARCHAR(255), " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_akun) REFERENCES Akun(id_akun) ON DELETE CASCADE)";
        db.execSQL(CREATE_TABLE_PROFILE);

        // Create table Achievement
        String CREATE_TABLE_ACHIEVEMENT = "CREATE TABLE Achievement (" +
                "id_achievement INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_akun INTEGER, " +
                "nama_achievement VARCHAR(100), " +
                "deskripsi TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_akun) REFERENCES Akun(id_akun) ON DELETE CASCADE)";
        db.execSQL(CREATE_TABLE_ACHIEVEMENT);

        // Create table Isi_Achievement
        String CREATE_TABLE_ISI_ACHIEVEMENT = "CREATE TABLE Isi_Achievement (" +
                "id_isi_achievement INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_achievement INTEGER, " +
                "progress VARCHAR(100), " +
                "detail_isi TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_achievement) REFERENCES Achievement(id_achievement) ON DELETE CASCADE)";
        db.execSQL(CREATE_TABLE_ISI_ACHIEVEMENT);

        // Create table Skin
        String CREATE_TABLE_SKIN = "CREATE TABLE Skin (" +
                "id_skin INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nama_skin VARCHAR(100), " +
                "deskripsi_skin TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_TABLE_SKIN);

        // Create table Koleksi_Skin
        String CREATE_TABLE_KOLEKSI_SKIN = "CREATE TABLE Koleksi_Skin (" +
                "id_koleksi INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "id_akun INTEGER, " +
                "id_skin INTEGER, " +
                "jumlah_koleksi INTEGER, " +
                "status_terkunci BOOLEAN, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (id_akun) REFERENCES Akun(id_akun) ON DELETE CASCADE, " +
                "FOREIGN KEY (id_skin) REFERENCES Skin(id_skin) ON DELETE CASCADE)";
        db.execSQL(CREATE_TABLE_KOLEKSI_SKIN);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop tables if they exist
        db.execSQL("DROP TABLE IF EXISTS Koleksi_Skin");
        db.execSQL("DROP TABLE IF EXISTS Skin");
        db.execSQL("DROP TABLE IF EXISTS Isi_Achievement");
        db.execSQL("DROP TABLE IF EXISTS Achievement");
        db.execSQL("DROP TABLE IF EXISTS Profile");
        db.execSQL("DROP TABLE IF EXISTS Akun");

        // Recreate tables
        onCreate(db);
    }
}
