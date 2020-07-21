package com.rukuni.efa;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "efa";

    private static final String TABLE_USER = "users";
    private static final String TABLE_PROCESS = "process";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_METER_NUMBER = "meterNumber";

    private static final String KEY_PID = "process_id";
    private static final String KEY_METER = "meter";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_PROBLEM = "problem";

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER +
                "(" +
                    KEY_ID + " INTEGER PRIMARY KEY," +
                    KEY_NAME + " TEXT," +
                    KEY_EMAIL + " TEXT UNIQUE," +
                    KEY_UID + " TEXT," +
                    KEY_CREATED_AT + " TEXT," +
                    KEY_METER_NUMBER + " TEXT" +
                ")";

        String CREATE_PROCESS_TABLE = "CREATE TABLE " + TABLE_PROCESS +
                "(" +
                    KEY_PID + " INTEGER PRIMARY KEY," +
                    KEY_METER + " TEXT," +
                    KEY_CONTACT + " TEXT UNIQUE," +
                    KEY_PROBLEM + " TEXT" +
                ")";

        db.execSQL(CREATE_LOGIN_TABLE);
        db.execSQL(CREATE_PROCESS_TABLE);
        Log.d(TAG, "Database tables created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROCESS);
        onCreate(db);
    }

    public void addUser(String meterNumber, String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values1 = new ContentValues();
        values1.put(KEY_NAME, name);
        values1.put(KEY_EMAIL, email);
        values1.put(KEY_UID, uid);
        values1.put(KEY_CREATED_AT, created_at);
        values1.put(KEY_METER_NUMBER, meterNumber);

        long id = db.insert(TABLE_USER, null, values1);
        db.close();
        Log.d(TAG, "New user inserted into MySQL: " + id);
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(2));
            user.put("email", cursor.getString(3));
            user.put("uid", cursor.getString(1));
            user.put("created_at", cursor.getString(6));
        }
        cursor.close();
        db.close();
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }
}