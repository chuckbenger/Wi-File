package com.tkblackbelt.sync.db;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.tkblackbelt.sync.core.MyLog.D;

public class ConnectionDataSource {

    private SQLiteDatabase database;
    private ArchivedConnectionDBHelper dbHelper;
    private String[] allColumns = {
            ArchivedConnectionDBHelper.COLUMN_ID,
            ArchivedConnectionDBHelper.COLUMN_NAME,
            ArchivedConnectionDBHelper.COLUMN_ADDRESS,
            ArchivedConnectionDBHelper.COLUMN_LAST_CONNECTED
    };


    public ConnectionDataSource(Context context) {
        dbHelper = new ArchivedConnectionDBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public ArchivedConnection createConnection(String name, String address, int port) {
        ContentValues values = new ContentValues();

        Date now = new Date();
        values.put(ArchivedConnectionDBHelper.COLUMN_NAME, name);
        values.put(ArchivedConnectionDBHelper.COLUMN_ADDRESS, address);
        values.put(ArchivedConnectionDBHelper.COLUMN_LAST_CONNECTED, now.toString());


        long insertID = database.insert(ArchivedConnectionDBHelper.TABLE_CONNECTIONS, null, values);
        Cursor cursor = database.query(ArchivedConnectionDBHelper.TABLE_CONNECTIONS, allColumns, ArchivedConnectionDBHelper.COLUMN_ID + " = " + insertID, null, null, null, null);
        cursor.moveToFirst();

        ArchivedConnection connection = cursorToConnection(cursor);
        cursor.close();
        D("File create with id " + insertID);
        return connection;
    }

    public void deleteConnection(ArchivedConnection file) {
        long id = file.getId();
        D("Deleting file with id: " + id);
        database.delete(ArchivedConnectionDBHelper.TABLE_CONNECTIONS, ArchivedConnectionDBHelper.COLUMN_ID + " = " + id, null);
    }

    /**
     * Finds a connection that has the input ip and name
     *
     * @param ip   the ip address of the connection to find
     * @param name the name of the device
     * @return Returns the related record or null;
     */
    public ArchivedConnection findByIpAndName(String ip, String name) {
        Cursor cursor = database.query(ArchivedConnectionDBHelper.TABLE_CONNECTIONS, allColumns, ArchivedConnectionDBHelper.COLUMN_ADDRESS + " = '" + ip + "' AND " + ArchivedConnectionDBHelper.COLUMN_NAME + " = '" + name + "'", null, null, null, null);
        ArchivedConnection connection;
        if (cursor.moveToFirst())
            connection = cursorToConnection(cursor);
        else
            connection = null;
        cursor.close();
        return connection;
    }

    /**
     * Creates a record if one doesn't exist with the input name and address.
     * Otherwise the last login timestamp is updated
     *
     * @param name    the name of the device
     * @param address the ip address of the connection
     * @param port    the device port
     */
    public void createElseUpdate(String name, String address, int port) {
        ArchivedConnection connection = findByIpAndName(address, name);
        if (connection == null)
            createConnection(name, address, port);
        else
            updateTime(connection);
    }

    /**
     * Updates the last connection timestamp on the connection
     *
     * @param connection the connection record to update
     */
    private void updateTime(ArchivedConnection connection) {
        String strFilter = "_id =" + connection.getId();
        ContentValues args = new ContentValues();
        args.put(ArchivedConnectionDBHelper.COLUMN_LAST_CONNECTED, new Date().toString());
        database.update(ArchivedConnectionDBHelper.TABLE_CONNECTIONS, args, strFilter, null);
    }

    public List<ArchivedConnection> getConnections() {
        List<ArchivedConnection> files = new ArrayList<ArchivedConnection>();

        Cursor cursor = database.query(ArchivedConnectionDBHelper.TABLE_CONNECTIONS,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ArchivedConnection playlist = cursorToConnection(cursor);
            files.add(playlist);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return files;
    }

    private ArchivedConnection cursorToConnection(Cursor cursor) {
        ArchivedConnection media = new ArchivedConnection();
        media.setId(cursor.getLong(0));
        media.setName(cursor.getString(1));
        media.setAddress(cursor.getString(2));
        media.setLastConnected(cursor.getString(3));
        return media;
    }
}
