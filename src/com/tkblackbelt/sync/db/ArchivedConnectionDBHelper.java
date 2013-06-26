package com.tkblackbelt.sync.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.tkblackbelt.sync.core.MyLog.D;

public class ArchivedConnectionDBHelper extends SQLiteOpenHelper {

    public static final String TABLE_CONNECTIONS = "connections";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_ADDRESS = "address";
    public static final String COLUMN_LAST_CONNECTED = "last_connected";


    private static final String DATABASE_NAME = "files.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_CONNECTIONS + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_NAME + " text not null,"
            + COLUMN_ADDRESS + " text not null,"
            + COLUMN_LAST_CONNECTED + " text not null"
            + ");";


    public ArchivedConnectionDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        D(ArchivedConnectionDBHelper.class.getName() +
                "Upgrading database from version " + oldVersion + " to "
                + newVersion + ", which will destroy all old data");

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONNECTIONS);
        onCreate(db);
    }
}
