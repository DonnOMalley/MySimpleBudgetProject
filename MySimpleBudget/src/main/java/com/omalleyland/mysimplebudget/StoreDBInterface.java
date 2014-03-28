package com.omalleyland.mysimplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omal310371 on 6/7/13.
 */
public class StoreDBInterface implements IObjectDBInterface {

    private final Context context;
    private final String className;
    private DBHelper dbHelper;

    public StoreDBInterface(Context context) {
        className = getClass().toString();
        Log.v(className, "StoreDB(Context) Constructor");
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    @Override
    public SyncObject cursorToSyncObject(Cursor cursor) {

        Log.v(className, "Writing Cursor to Store Object");

        Store store = new Store();

        store.setID(cursor.getInt(Common.colSTORE_ID_INDEX));
        store.setName(cursor.getString(Common.colSTORE_NAME_INDEX));
        store.setServerID(cursor.getInt(Common.colSTORE_SERVER_ID_INDEX));
        store.setSyncStatus(cursor.getInt(Common.colSTORE_SYNC_STATUS_INDEX));
        store.setActiveStatus(cursor.getInt(Common.colSTORE_ACTIVE_STATUS_INDEX));
        return store;
    }

    @Override
    public long addObject(SyncObject syncObject) {
        SQLiteDatabase db;
        long insertId = -1;
        if(syncObject != null) {
            Log.d(className, "Adding Store To Database :: id = " + Integer.toString(syncObject.getID()) +
                    ", name = " + syncObject.getName() +
                    ", serverID = " + Integer.toString(syncObject.getServerID()) +
                    ", syncStatus = " + Integer.toString(syncObject.getSyncStatus()) +
                    ", activeStatus = " + Integer.toString(syncObject.getActiveStatus()));
            try {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Common.colSTORE_NAME, syncObject.getName());
                values.put(Common.colSTORE_SERVER_ID, syncObject.getServerID());
                values.put(Common.colSTORE_SYNC_STATUS, syncObject.getSyncStatus());
                values.put(Common.colSTORE_ACTIVE_STATUS, syncObject.getActiveStatus());
                Log.v(className, "Inserting into Store Table");
                insertId = db.insert(Common.tblSTORES, null, values);
            }
            catch (Exception e) {
                Log.e(className, "Exception Adding Store :: name = '" + syncObject.getName() + "' :: " + e.getMessage());
            }

            dbHelper.close();
            Log.d(className, "StoreID from Insert = " + Long.toString(insertId));
        }
        else {
            Log.e(className, "Unable to add 'NULL' store to Database");
        }
        return insertId;
    }

    @Override
    public int deleteObject(SyncObject syncObject) {
        int result = -1;
        if(syncObject != null) {
            SQLiteDatabase db;
            Log.d(className, "Deleting Store From Database :: id = " + Integer.toString(syncObject.getID()) +
                                ", name = " + syncObject.getName() +
                                ", serverID = " + Integer.toString(syncObject.getServerID()) +
                                ", syncStatus = " + Integer.toString(syncObject.getSyncStatus()) +
                                ", activeStatus = " + Integer.toString(syncObject.getActiveStatus()));
            try {
                db = dbHelper.getWritableDatabase();
                Log.v(className, "Performing Delete From Store Table");
                result = db.delete(Common.tblSTORES, Common.colSTORE_NAME + "=?", new String[] { syncObject.getName() });
            }
            catch (Exception e) {
                Log.e(className, "Exception Removing Store :: name = '" + syncObject.getName() + "' :: " + e.getMessage());
            }
            dbHelper.close();
        }
        else {
            Log.e(this.className, "Unable to delete 'NULL' store from Database");
        }
        return result;
    }

    @Override
    public SyncObject getObject(int serverID) {
        SQLiteDatabase db;
        Store store = new Store();
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Querying Store by Server ID = " + Integer.toString(serverID));
            Cursor cursor = db.query(Common.tblSTORES, Common.colSTORES_ALL, Common.colSTORE_SERVER_ID + " = ?", new String[]{Integer.toString(serverID)}, null, null, null);
            if(cursor.getCount() == 1) {
                cursor.moveToFirst();
                store = (Store)cursorToSyncObject(cursor);
                Log.d(className, "Store Record Returned :: id = " + Integer.toString(store.getID()) +
                                    ", name = " + store.getName() +
                                    ", serverID = " + Integer.toString(store.getServerID()) +
                                    ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                                    ", activeStatus = " + Integer.toString(store.getActiveStatus()));
            }
            else {
                Log.d(className, "Store Not Found :: Cursor Record Count = " + Integer.toString(cursor.getCount()));
                store = null;
            }
            cursor.close();
        }
        catch (Exception e) {
            Log.d(className, "Exception Getting Store By id :: " + e.getMessage());
        }

        dbHelper.close();

        return store;
    }

    @Override
    public SyncObject getObject(String storeName) {
        SQLiteDatabase db;
        Store store = new Store();
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Querying Store by Name = " + storeName);
            Cursor cursor = db.query(Common.tblSTORES, Common.colSTORES_ALL, Common.colSTORE_NAME + " = ?", new String[]{storeName}, null, null, null);
            if(cursor.getCount() == 1) {
                cursor.moveToFirst();
                store = (Store)cursorToSyncObject(cursor);
                Log.d(className, "Store Record Returned :: id = " + Integer.toString(store.getID()) +
                                    ", name = " + store.getName() +
                                    ", serverID = " + Integer.toString(store.getServerID()) +
                                    ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                                    ", activeStatus = " + Integer.toString(store.getActiveStatus()));
            }
            else {
                Log.d(className, "Store Not Found :: Cursor Record Count = " + Integer.toString(cursor.getCount()));
                store = null;
            }
            cursor.close();
        }
        catch (Exception e) {
            Log.d(className, "Exception Getting Store By Name :: " + e.getMessage());
        }

        dbHelper.close();
        return store;
    }

    @Override
    public List<SyncObject> getActiveDatabaseObjects(){
        SQLiteDatabase db;
        Store store;
        List<SyncObject> storeList = new ArrayList<SyncObject>();
        String[] whereArgs = new String[]{Integer.toString(Common.ACTIVE_STATUS_ACTIVE)};
        String whereClause = Common.colSTORE_ACTIVE_STATUS + " = ?";
        Log.v(className, "Querying List of Active Stores");

        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(Common.tblSTORES, Common.colSTORES_ALL, whereClause, whereArgs, null, null, Common.colSTORE_NAME);
            Log.d(className, "Number of Store Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    store = (Store)cursorToSyncObject(cursor);
                    if(store.getName().equalsIgnoreCase("Other")) {
                        storeList.add(0, store);
                    }
                    else {
                        storeList.add(store);
                    }
                    Log.d(className, "Store Record Returned :: id = " + Integer.toString(store.getID()) +
                            ", name = " + store.getName() +
                            ", serverID = " + Integer.toString(store.getServerID()) +
                            ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(store.getActiveStatus()));
                } while (cursor.moveToNext());
            }
            Log.d(className, "Store List Populated :: Size = " + Integer.toString(storeList.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Store List :: " + e.getMessage());
        }
        dbHelper.close();
        return storeList;
    }

    @Override
    public List<SyncObject> getAllDatabaseObjects() {
        SQLiteDatabase db;
        List<SyncObject> storeList = new ArrayList<SyncObject>();
        Store store;
        Log.v(className, "Querying List of All Stores");
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(Common.tblSTORES, Common.colSTORES_ALL, null, null, null, null, Common.colSTORE_NAME);
            Log.d(className, "Number of Store Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    store = (Store)cursorToSyncObject(cursor);
                    storeList.add(store);
                    Log.d(className, "Store Record Returned :: id = " + Integer.toString(store.getID()) +
                                        ", name = " + store.getName() +
                                        ", serverID = " + Integer.toString(store.getServerID()) +
                                        ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                                        ", activeStatus = " + Integer.toString(store.getActiveStatus()));
                } while (cursor.moveToNext());
            }
            Log.d(className, "Store List Populated :: Size = " + Integer.toString(storeList.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Store List :: " + e.getMessage());
        }
        dbHelper.close();
        return storeList;
    }

    @Override
    public List<SyncObject> getUpdatedDatabaseObjects(List<Integer> objectSyncStatuses) {
        SQLiteDatabase db;
        List<SyncObject> storeList = new ArrayList<SyncObject>();
        String whereClause = null;
        List<String> statusListStrings = new ArrayList<String>();
        String[] whereArgs = null;

        //Populate Where Clause/Arguments if SyncStatuses were provided
        // If no statuses were passed
        Log.d(this.className, "Building SQLite Where Clause");
        if(objectSyncStatuses != null) {
            for(Integer syncStatus : objectSyncStatuses) {
                if(whereClause == null) {
                    whereClause = Common.colSTORE_SYNC_STATUS + " IN (?";
                }
                else {
                    whereClause += ",?";
                }
                statusListStrings.add(Integer.toString(syncStatus));
            }
            if(whereClause != null) {
                whereClause += ")";
            }
            whereArgs = new String[statusListStrings.size()];
            statusListStrings.toArray(whereArgs);
        }

        Store store;
        if(whereArgs != null) {
            Log.v(className, "Querying List of Stores from Local Database based on SyncStatuses :: ".concat(whereArgs.toString()));
        }
        try {
            db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(Common.tblSTORES, Common.colSTORES_ALL, whereClause, whereArgs, null, null, Common.colSTORE_SYNC_STATUS);
            Log.d(className, "Number of Store Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    store = (Store)cursorToSyncObject(cursor);
                    storeList.add(store);
                    Log.d(className, "Store Record Returned :: id = " + Integer.toString(store.getID()) +
                                        ", name = " + store.getName() +
                                        ", serverID = " + Integer.toString(store.getServerID()) +
                                        ", syncStatus = " + Integer.toString(store.getSyncStatus()) +
                                        ", activeStatus = " + Integer.toString(store.getActiveStatus()));
                } while (cursor.moveToNext());
            }
            Log.d(className, "Store List Populated :: Size = " + Integer.toString(storeList.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Store List :: " + e.getMessage());
        }
        dbHelper.close();
        return storeList;
    }

    @Override
    public int updateDatabaseObjects(List<SyncObject> syncObjects) {
        //For each record in the List<Store>, update SQLite
        SQLiteDatabase db;
        int updatedRecords = 0;
        String whereClause = Common.colSTORE_ID + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();

        Log.v(className, "Updating Stores with Values from List");
        try {
            Store existingStore = null;
            for(SyncObject syncObject : syncObjects) {
              if(syncObject.getServerID() != Common.UNKNOWN) {
                existingStore = (Store)getObject(syncObject.getServerID());
              }
              if(existingStore == null) {
                existingStore = (Store)getObject(syncObject.getName());
              }
              db = dbHelper.getWritableDatabase();
              if(existingStore != null) {
                values.clear();
                values.put(Common.colSTORE_ID, existingStore.getID());
                values.put(Common.colSTORE_NAME, syncObject.getName());
                values.put(Common.colSTORE_SERVER_ID, syncObject.getServerID());
                values.put(Common.colSTORE_SYNC_STATUS, syncObject.getSyncStatus());
                values.put(Common.colSTORE_ACTIVE_STATUS, syncObject.getActiveStatus());
                whereArgs = new String[]{Integer.toString(existingStore.getID())};
                Log.d(className, "Updating Store Record :: id = " + Integer.toString(existingStore.getID()) +
                        ", name = " + syncObject.getName() +
                        ", serverID = " + Integer.toString(syncObject.getServerID()) +
                        ", syncStatus = " + Integer.toString(syncObject.getSyncStatus()) +
                        ", activeStatus = " + Integer.toString(syncObject.getActiveStatus()));
                updatedRecords = updatedRecords + db.update(Common.tblSTORES, values, whereClause, whereArgs);
              }
              else {
                if(addObject(syncObject) > -1) {
                  updatedRecords += 1;
                }
              }
              dbHelper.close();
            }
            Log.d(className, "Number of Store Records = " + Integer.toString(updatedRecords));
        }
        catch (Exception e) {
          Log.e(className, "Exception Updating Stores from Server :: " + e.getMessage());
          dbHelper.close();
        }
        return updatedRecords;
    }

    @Override
    public int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects, int syncStatus) {
        //For each record in the List<Category>, update SQLite
        SQLiteDatabase db;
        int updatedRecords = 0;
        String whereClause = Common.colSTORE_ID + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();

        Log.v(className, "Updating Stores with Values from List");
        try {

            Store existingStore = null;
            for(SyncObject syncObject : syncObjects) {
                //Check for category to exist,
                //  if exists, update
                //  else add
                if(syncObject.getServerID() != Common.UNKNOWN) {
                  existingStore = (Store)getObject(syncObject.getServerID());
                }
                if(existingStore == null) {
                  existingStore = (Store)getObject(syncObject.getName());
                }
                db = dbHelper.getWritableDatabase();
                if(existingStore!=null) {
                    values.clear();
                    values.put(Common.colSTORE_ID, existingStore.getID());
                    values.put(Common.colSTORE_NAME, syncObject.getName());
                    values.put(Common.colSTORE_SERVER_ID, syncObject.getServerID());
                    values.put(Common.colSTORE_SYNC_STATUS, syncStatus);
                    values.put(Common.colSTORE_ACTIVE_STATUS, syncObject.getActiveStatus());
                    whereArgs = new String[]{Integer.toString(existingStore.getID())};
                    Log.d(className, "Updating store Record :: id = " + Integer.toString(existingStore.getID()) +
                            ", name = " + syncObject.getName() +
                            ", serverID = " + Integer.toString(syncObject.getServerID()) +
                            ", syncStatus = " + Integer.toString(syncObject.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(syncObject.getActiveStatus()));
                    updatedRecords = updatedRecords + db.update(Common.tblSTORES, values, whereClause, whereArgs);
                }
                else {
                    if(addObject(syncObject) > -1) {
                        updatedRecords += 1;
                    }
                }
                dbHelper.close();
            }
            Log.d(className, "Number of Store Records = " + Integer.toString(updatedRecords));
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Stores from Server :: " + e.getMessage());
            dbHelper.close();
        }
        return updatedRecords;
    }

    @Override
    public JSONObject buildJSON(int httpType, List<Integer> objectSyncStatuses, String userName, String password) {
        List<SyncObject> syncObjects    = new ArrayList<SyncObject>();
        Store store;
        JSONObject  jsonObjectResult    = null;
        JSONObject  storeJSON;
        JSONArray   jsonStoreArray      = new JSONArray();

        if(httpType == Common.HTTP_TYPE_POST) {
            Log.d(this.className, "Getting Data for HTTP Post");
            syncObjects = getUpdatedDatabaseObjects(objectSyncStatuses);
            Log.d(this.className, "Objects to Post retrieved");
            if(syncObjects.size() > 0) {
                try {
                    Log.d(this.className, "Building JSON Object");
                    jsonObjectResult = new JSONObject();
                    jsonObjectResult.put("type", Common.HTTP_POST_JSON_TEXT);
                    jsonObjectResult.put("user", userName);
                    for(SyncObject syncObject : syncObjects) {
                        storeJSON = new JSONObject(((Store)syncObject).getMap());
                        jsonStoreArray.put(storeJSON);
                    }
                    jsonObjectResult.put(Common.STORE_JSON_ARRAY, jsonStoreArray);
                    Log.d(this.className, jsonObjectResult.toString());
                }
                catch (Exception e) {
                    Log.e(this.className, "Exception Building Store JSON For POST :: " + e.getMessage());
                }
            }
        }
        else if(httpType == Common.HTTP_TYPE_GET) {
            try {
                jsonObjectResult = new JSONObject();
                jsonObjectResult.put("type", Common.HTTP_GET_JSON_TEXT);
                jsonObjectResult.put("user", userName);
                jsonObjectResult.put(Common.STORE_JSON_ARRAY, jsonStoreArray);    //Just an Empty Array
                Log.d(this.className, jsonObjectResult.toString());
            }
            catch (Exception e) {
                Log.e(this.className, "Exception Building Stpre JSON For GET :: " + e.getMessage());
            }

        }
        else if(httpType == Common.HTTP_TYPE_VERIFY) {
            try {
                syncObjects = getUpdatedDatabaseObjects(objectSyncStatuses);
                jsonObjectResult = new JSONObject();
                jsonObjectResult.put("type", Common.HTTP_VERIFY_JSON_TEXT);
                jsonObjectResult.put("user", userName);
                if(syncObjects.size() > 0) {
                    for(SyncObject syncObject: syncObjects) {
                        storeJSON = new JSONObject(((Store)syncObject).getMap());
                        jsonStoreArray.put(storeJSON);
                    }
                    jsonObjectResult.put(Common.STORE_JSON_ARRAY, jsonStoreArray);
                }
                Log.d(this.className, jsonObjectResult.toString());
            }
            catch (Exception e) {
                Log.e(this.className, "Exception Building Store JSON For VERIFY :: " + e.getMessage());
            }

        }

        return jsonObjectResult; //Null if no records to verify
    }

    public List<SyncObject> parseJSONList(JSONObject jsonObject) {
        List<SyncObject>    syncObjects = new ArrayList<SyncObject>();
        JSONArray           jsonArray;
        JSONObject          storeJSONObject;
        Store               store;
        try {

            jsonArray = jsonObject.getJSONArray(Common.STORE_JSON_ARRAY);
            for (int i = 0; i < jsonArray.length(); i++) {
                storeJSONObject = jsonArray.getJSONObject(i);
                store = new Store();
                store.JSONToObject(storeJSONObject);
                syncObjects.add(store);
            }
        }
        catch (Exception e) {
            syncObjects.clear(); //Wipe out anything that may have been put in.
            Log.e(this.className, "Exception parsing JSON To Store List :: ".concat(e.getMessage()));
        }

        return syncObjects;
    }
}

