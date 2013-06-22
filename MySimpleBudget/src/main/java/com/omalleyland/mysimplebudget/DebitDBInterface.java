package com.omalleyland.mysimplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by omal310371 on 6/20/13.
 */
public class DebitDBInterface implements IObjectDBInterface {

    private final Context context;
    private final String className;
    private DBHelper dbHelper;

    public DebitDBInterface(Context context) {
        className = getClass().toString();
        Log.v(className, "CategoryDBInterface(Context) Constructor");
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    @Override
    public SyncObject cursorToSyncObject(Cursor cursor) {

        Log.v(className, "Writing Cursor to Debit Object");

        Debit debit = new Debit();
        debit.setID(cursor.getInt(Common.colDEBIT_ID_INDEX));
        debit.setUserID(cursor.getInt(Common.colDEBIT_PURCHASER_ID_INDEX));
        debit.setLocalCategoryID(cursor.getInt(Common.colDEBIT_LOCAL_CATEGORY_ID_INDEX));
        debit.setCategoryID(cursor.getInt(Common.colDEBIT_SERVER_CATEGORY_ID_INDEX));
        debit.setLocalStoreID(cursor.getInt(Common.colDEBIT_LOCAL_STORE_ID_INDEX));
        debit.setStoreID(cursor.getInt(Common.colDEBIT_SERVER_STORE_ID_INDEX));
        debit.setDateString(cursor.getString(Common.colDEBIT_DEBIT_DATE_INDEX));
        debit.setAmount(cursor.getFloat(Common.colDEBIT_DEBIT_AMOUNT_INDEX));
        debit.setComment(cursor.getString(Common.colDEBIT_COMMENT_INDEX));
        debit.setEntryOnString(cursor.getString(Common.colDEBIT_ENTRY_ON_INDEX));

        return debit;
    }

    @Override
    public long addObject(SyncObject syncObject) {
        SQLiteDatabase db;
        long insertId = -1;
        Debit debit;
        if(syncObject != null) {
            debit = (Debit)syncObject;
            Log.d(className, "Adding Debit To Database :: id = " + Integer.toString(debit.getID()) +
                    ", purchaser_id = " + Integer.toString(debit.getUserID()) +
                    ", serverID = " + Integer.toString(syncObject.getServerID()) +
                    ", syncStatus = " + Integer.toString(syncObject.getSyncStatus()) +
                    ", activeStatus = " + Integer.toString(syncObject.getActiveStatus()));
            try {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Common.colCATEGORY_NAME, syncObject.getName());
                if(syncObject.getServerID() > -1) {
                    values.put(Common.colCATEGORY_SERVER_ID, syncObject.getServerID());
                }
                values.put(Common.colCATEGORY_SYNC_STATUS, syncObject.getSyncStatus());
                values.put(Common.colCATEGORY_ACTIVE_STATUS, syncObject.getActiveStatus());
                Log.v(className, "Inserting into Category Table");
                insertId = db.insert(Common.tblCATEGORIES, null, values);
            }
            catch (Exception e) {
                Log.e(className, "Exception Adding Category :: name = '" + syncObject.getName() + "' :: " + e.getMessage());
            }

            dbHelper.close();
            Log.d(className, "CategoryID from Insert = " + Long.toString(insertId));
        }
        else {
            Log.e(className, "Unable to add 'NULL' category to Database");
        }
        return insertId;
    }

    @Override
    public int deleteObject(SyncObject syncObject) {
        return 0;
    }

    @Override
    public SyncObject getObject(int id) {
        return null;
    }

    @Override
    public SyncObject getObject(String name) {
        return null;
    }

    @Override
    public List<SyncObject> getAllDatabaseObjects() {
        return null;
    }

    @Override
    public List<SyncObject> getActiveDatabaseObjects() {
        return null;
    }

    @Override
    public List<SyncObject> getUpdatedDatabaseObjects(List<Integer> objectSyncStatuses) {
        return null;
    }

    @Override
    public int updateDatabaseObjects(List<SyncObject> syncObjects) {
        return 0;
    }

    @Override
    public int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects, int syncStatus) {
        return 0;
    }

    @Override
    public JSONObject buildJSON(int httpType, List<Integer> objectSyncStatuses, String userName, String password) {
        return null;
    }

    @Override
    public List<SyncObject> parseJSONList(JSONObject jsonObject) {
        return null;
    }
}
