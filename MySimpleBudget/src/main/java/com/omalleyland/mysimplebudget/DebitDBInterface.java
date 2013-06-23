package com.omalleyland.mysimplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
        Log.v(className, "DebitDBInterface(Context) Constructor");
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
        debit.setAmount(Float.parseFloat(cursor.getString(Common.colDEBIT_DEBIT_AMOUNT_INDEX)));
        debit.setComment(cursor.getString(Common.colDEBIT_COMMENT_INDEX));
        debit.setEntryOnString(cursor.getString(Common.colDEBIT_ENTRY_ON_INDEX));

        return debit;
    }

    @Override
    public long addObject(SyncObject syncObject) {
        SQLiteDatabase db;
        NumberFormat numberFormat = DecimalFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(1);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.DOWN);
        long insertId = -1;
        Debit debit;

        if(syncObject != null) {
            debit = (Debit)syncObject;
            Log.d(className, "Adding Debit To Database :: " +
                    ", purchaser_id = " + Integer.toString(debit.getUserID()) +
                    ", local_category_id = " + Integer.toString(debit.getLocalCategoryID()) +
                    ", server_category_id = " + Integer.toString(debit.getCategoryID()) +
                    ", local_store_id = " + Integer.toString(debit.getLocalCategoryID()) +
                    ", server_store_id = " + Integer.toString(debit.getStoreID()) +
                    ", debit_date = " + debit.getDateString() +
                    ", amount = " + Double.toString(debit.getAmount()) +
                    ", comment = " + debit.getComment() +
                    ", entry_on = " + debit.getEntryOnString());
            try {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(Common.colDEBIT_PURCHASER_ID, debit.getUserID());
                values.put(Common.colDEBIT_LOCAL_CATEGORY_ID, debit.getLocalCategoryID());
                values.put(Common.colDEBIT_SERVER_CATEGORY_ID, debit.getCategoryID());
                values.put(Common.colDEBIT_LOCAL_STORE_ID, debit.getLocalStoreID());
                values.put(Common.colDEBIT_SERVER_STORE_ID, debit.getStoreID());
                values.put(Common.colDEBIT_DEBIT_DATE, debit.getDateString());
                values.put(Common.colDEBIT_DEBIT_AMOUNT, numberFormat.format(debit.getAmount()));
                values.put(Common.colDEBIT_COMMENT, debit.getComment());
                values.put(Common.colDEBIT_ENTRY_ON, debit.getEntryOnString());
                Log.v(className, "Inserting into Debits Table");
                insertId = db.insert(Common.tblDebits, null, values);
            }
            catch (Exception e) {
                Log.e(className, "Exception Adding Debit :: name = '" + syncObject.getName() + "' :: " + e.getMessage());
            }

            dbHelper.close();
            Log.d(className, "DebitID from Insert = " + Long.toString(insertId));
        }
        else {
            Log.e(className, "Unable to add 'NULL' debit to Database");
        }
        return insertId;
    }

    @Override
    public int deleteObject(SyncObject syncObject) {
        int result = -1;
        Debit debit;
        if(syncObject != null) {
            debit = (Debit)syncObject;
            SQLiteDatabase db;
            Log.d(className, "Deleting Debit From Database :: " +
                    ", purchaser_id = " + Integer.toString(debit.getUserID()) +
                    ", local_category_id = " + Integer.toString(debit.getLocalCategoryID()) +
                    ", server_category_id = " + Integer.toString(debit.getCategoryID()) +
                    ", local_store_id = " + Integer.toString(debit.getLocalCategoryID()) +
                    ", server_store_id = " + Integer.toString(debit.getStoreID()) +
                    ", debit_date = " + debit.getDateString() +
                    ", amount = " + Double.toString(debit.getAmount()) +
                    ", comment = " + debit.getComment() +
                    ", entry_on = " + debit.getEntryOnString());
            try {
                db = dbHelper.getWritableDatabase();
                Log.v(className, "Performing Delete From Debit Table");
                result = db.delete(Common.tblDebits, Common.colDEBIT_ID + "=?", new String[] { Integer.toString(debit.getID()) });
            }
            catch (Exception e) {
                Log.e(className, "Exception Removing Debit :: id = '" + Integer.toString(debit.getID()) + "' :: " + e.getMessage());
            }
            dbHelper.close();
        }
        else {
            Log.e(className, "Unable to delete 'NULL' debit from Database");
        }
        return result;
    }

    @Override
    public SyncObject getObject(int id) {
        SQLiteDatabase db;
        Debit debit= new Debit();
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Querying Debits by ID = " + Integer.toString(id));
            Cursor cursor = db.query(Common.tblDebits, Common.colDEBITS_ALL, Common.colDEBIT_ID + " = ?", new String[]{Integer.toString(id)}, null, null, null);
            if(cursor.getCount() == 1) {
                cursor.moveToFirst();
                debit = (Debit)cursorToSyncObject(cursor);
                Log.d(className, "Debit Record Returned :: id = " + Integer.toString(debit.getID()) +
                        ", purchaser_id = " + Integer.toString(debit.getUserID()) +
                        ", local_category_id = " + Integer.toString(debit.getLocalCategoryID()) +
                        ", server_category_id = " + Integer.toString(debit.getCategoryID()) +
                        ", local_store_id = " + Integer.toString(debit.getLocalCategoryID()) +
                        ", server_store_id = " + Integer.toString(debit.getStoreID()) +
                        ", debit_date = " + debit.getDateString() +
                        ", amount = " + Double.toString(debit.getAmount()) +
                        ", comment = " + debit.getComment() +
                        ", entry_on = " + debit.getEntryOnString());
            }
            else {
                Log.d(className, "Debit Not Found :: Cursor Record Count = " + Integer.toString(cursor.getCount()));
                debit = null;
            }
            cursor.close();
        }
        catch (Exception e) {
            Log.e(className, "Exception Getting Debit By id :: " + e.getMessage());
        }

        dbHelper.close();
        return debit;
    }

    @Override
    public SyncObject getObject(String name) {
        //Retrieving Debits by Name is not a valid property
        return null;
    }

    @Override
    public List<SyncObject> getAllDatabaseObjects() {
        SQLiteDatabase db;
        List<SyncObject> debitList = new ArrayList<SyncObject>();
        Debit debit;
        Log.v(className, "Querying List of All Debits");
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(Common.tblDebits, Common.colDEBITS_ALL,null,null,null,null,Common.colDEBIT_DEBIT_DATE);
            Log.d(className, "Number of Debit Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    debit = (Debit)cursorToSyncObject(cursor);
                    debitList.add(debit);
                    Log.d(className, "Debit Record Returned :: id = " + Integer.toString(debit.getID()) +
                            ", purchaser_id = " + Integer.toString(debit.getUserID()) +
                            ", local_category_id = " + Integer.toString(debit.getLocalCategoryID()) +
                            ", server_category_id = " + Integer.toString(debit.getCategoryID()) +
                            ", local_store_id = " + Integer.toString(debit.getLocalCategoryID()) +
                            ", server_store_id = " + Integer.toString(debit.getStoreID()) +
                            ", debit_date = " + debit.getDateString() +
                            ", amount = " + Double.toString(debit.getAmount()) +
                            ", comment = " + debit.getComment() +
                            ", entry_on = " + debit.getEntryOnString());
                } while (cursor.moveToNext());
            }
            Log.d(className, "Debit List Populated :: Size = " + Integer.toString(debitList.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Debit List :: " + e.getMessage());
        }
        dbHelper.close();
        return debitList;
    }

    @Override
    public List<SyncObject> getActiveDatabaseObjects() {
        //All Debits are considered 'Active' - Not a valid property
        return null;
    }

    @Override
    public List<SyncObject> getUpdatedDatabaseObjects(List<Integer> objectSyncStatuses) {
        SQLiteDatabase db;
        List<SyncObject> debitList = new ArrayList<SyncObject>();
        List<String> statusListStrings = new ArrayList<String>();

        //TODO : check all debit records with UNKNOWN(-1) Server Category/Store ID's
        //TODO : and update any where the category/store records have a known(! -1) ServerID.

        //TODO : Needs updated to include records with ONLY Server Category/Store ID's
        String whereClause = Common.colDEBIT_SYNC_STATUS + " = ?";
        String[] whereArgs = new String[]{Integer.toString(Common.SYNC_STATUS_NEW)}; //Only new Statuses are supported - Ignore any passed in

        Debit debit;
        Log.v(className, "Querying List of Debits from Local Database based on SyncStatuses :: ".concat(statusListStrings.toString()));
        try {
            db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(Common.tblDebits, Common.colDEBITS_ALL, whereClause, whereArgs, null, null, Common.colDEBIT_DEBIT_DATE);
            Log.d(className, "Number of Debit Records = " + Integer.toString(cursor.getCount()));
            if(cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    debit = (Debit)cursorToSyncObject(cursor);
                    debitList.add(debit);
                    Log.d(className, "Debit Record Returned :: id = " + Integer.toString(debit.getID()) +
                            ", purchaser_id = " + Integer.toString(debit.getUserID()) +
                            ", local_category_id = " + Integer.toString(debit.getLocalCategoryID()) +
                            ", server_category_id = " + Integer.toString(debit.getCategoryID()) +
                            ", local_store_id = " + Integer.toString(debit.getLocalCategoryID()) +
                            ", server_store_id = " + Integer.toString(debit.getStoreID()) +
                            ", debit_date = " + debit.getDateString() +
                            ", amount = " + Double.toString(debit.getAmount()) +
                            ", comment = " + debit.getComment() +
                            ", entry_on = " + debit.getEntryOnString());
                } while (cursor.moveToNext());
            }
            Log.d(className, "Debit List Populated :: Size = " + Integer.toString(debitList.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Updated Debit List :: " + e.getMessage());
        }
        dbHelper.close();
        return debitList;
    }

    @Override
    public int updateDatabaseObjects(List<SyncObject> syncObjects) {
        //For each record in the List<Debit>, update SQLite
        SQLiteDatabase db;
        int updatedRecords = 0;
        String whereClause = Common.colDEBIT_ID + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();
        NumberFormat numberFormat = DecimalFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(1);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.DOWN);

        Log.v(className, "Updating Debits with Values from List");
        try {

            Debit existingDebit;
            for(SyncObject syncObject : syncObjects) {
                //Check for debit to exist,
                //  if exists, update
                //  else add
                existingDebit = (Debit)getObject(syncObject.getID());
                if(existingDebit!=null) {
                    db = dbHelper.getWritableDatabase();
                    values.clear();
                    values.put(Common.colDEBIT_PURCHASER_ID, existingDebit.getUserID());
                    values.put(Common.colDEBIT_LOCAL_CATEGORY_ID, existingDebit.getLocalCategoryID());
                    values.put(Common.colDEBIT_SERVER_CATEGORY_ID, existingDebit.getCategoryID());
                    values.put(Common.colDEBIT_LOCAL_STORE_ID, existingDebit.getLocalStoreID());
                    values.put(Common.colDEBIT_SERVER_STORE_ID, existingDebit.getStoreID());
                    values.put(Common.colDEBIT_DEBIT_DATE, existingDebit.getDateString());
                    values.put(Common.colDEBIT_DEBIT_AMOUNT, numberFormat.format(existingDebit.getAmount()));
                    values.put(Common.colDEBIT_COMMENT, existingDebit.getComment());
                    values.put(Common.colDEBIT_ENTRY_ON, existingDebit.getEntryOnString());
                    whereArgs = new String[]{Integer.toString(syncObject.getID())};
                    Log.d(className, "Updating Debit Record :: id = " + Integer.toString(existingDebit.getID()) +
                            ", purchaser_id = " + Integer.toString(existingDebit.getUserID()) +
                            ", local_category_id = " + Integer.toString(existingDebit.getLocalCategoryID()) +
                            ", server_category_id = " + Integer.toString(existingDebit.getCategoryID()) +
                            ", local_store_id = " + Integer.toString(existingDebit.getLocalCategoryID()) +
                            ", server_store_id = " + Integer.toString(existingDebit.getStoreID()) +
                            ", debit_date = " + existingDebit.getDateString() +
                            ", amount = " + Double.toString(existingDebit.getAmount()) +
                            ", comment = " + existingDebit.getComment() +
                            ", entry_on = " + existingDebit.getEntryOnString());
                    updatedRecords = updatedRecords + db.update(Common.tblDebits, values, whereClause, whereArgs);
                    dbHelper.close();
                }
                else {
                    if(addObject(syncObject) > -1) {
                        updatedRecords += 1;
                    }
                }
            }
            Log.d(className, "Number of Debit Records = " + Integer.toString(updatedRecords));
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Debits from Server :: " + e.getMessage());
        }
        return updatedRecords;
    }

    @Override
    public int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects, int syncStatus) {
        //For each record in the List<Debit>, update SQLite
        SQLiteDatabase db;
        int updatedRecords = 0;
        String whereClause = Common.colDEBIT_ID + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();
        NumberFormat numberFormat = DecimalFormat.getInstance();
        numberFormat.setMinimumIntegerDigits(1);
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setRoundingMode(RoundingMode.DOWN);

        Log.v(className, "Updating Debits with Values from List");
        try {

            Debit existingDebit;
            for(SyncObject syncObject : syncObjects) {
                //Check for debit to exist,
                //  if exists, update
                //  else add
                existingDebit = (Debit)getObject(syncObject.getName());
                if(existingDebit!=null) {
                    db = dbHelper.getWritableDatabase();
                    values.clear();
                    values.put(Common.colDEBIT_PURCHASER_ID, existingDebit.getUserID());
                    values.put(Common.colDEBIT_LOCAL_CATEGORY_ID, existingDebit.getLocalCategoryID());
                    values.put(Common.colDEBIT_SERVER_CATEGORY_ID, existingDebit.getCategoryID());
                    values.put(Common.colDEBIT_LOCAL_STORE_ID, existingDebit.getLocalStoreID());
                    values.put(Common.colDEBIT_SERVER_STORE_ID, existingDebit.getStoreID());
                    values.put(Common.colDEBIT_DEBIT_DATE, existingDebit.getDateString());
                    values.put(Common.colDEBIT_DEBIT_AMOUNT, numberFormat.format(existingDebit.getAmount()));
                    values.put(Common.colDEBIT_COMMENT, existingDebit.getComment());
                    values.put(Common.colDEBIT_ENTRY_ON, existingDebit.getEntryOnString());
                    whereArgs = new String[]{Integer.toString(syncObject.getID())};
                    Log.d(className, "Updating Debit Record :: id = " + Integer.toString(existingDebit.getID()) +
                            ", purchaser_id = " + Integer.toString(existingDebit.getUserID()) +
                            ", local_category_id = " + Integer.toString(existingDebit.getLocalCategoryID()) +
                            ", server_category_id = " + Integer.toString(existingDebit.getCategoryID()) +
                            ", local_store_id = " + Integer.toString(existingDebit.getLocalCategoryID()) +
                            ", server_store_id = " + Integer.toString(existingDebit.getStoreID()) +
                            ", debit_date = " + existingDebit.getDateString() +
                            ", amount = " + Double.toString(existingDebit.getAmount()) +
                            ", comment = " + existingDebit.getComment() +
                            ", entry_on = " + existingDebit.getEntryOnString());
                    updatedRecords = updatedRecords + db.update(Common.tblDebits, values, whereClause, whereArgs);
                    dbHelper.close();
                }
                else {
                    if(addObject(syncObject) > -1) {
                        updatedRecords += 1;
                    }
                }
            }
            Log.d(className, "Number of Debits Records = " + Integer.toString(updatedRecords));
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Debits from Server :: " + e.getMessage());
        }
        return updatedRecords;
    }

    @Override
    public JSONObject buildJSON(int httpType, List<Integer> objectSyncStatuses, String userName, String password) {
        List<SyncObject>    syncObjects         = new ArrayList<SyncObject>();
        Debit               debit;
        JSONObject          jsonObjectResult    = null;
        JSONObject          debitJSON;
        JSONArray           jsonDebitArray      = new JSONArray();

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
                    for(SyncObject syncObject: syncObjects) {
                        debitJSON = new JSONObject(((Debit)syncObject).getMap());
                        jsonDebitArray.put(debitJSON);
                    }
                    jsonObjectResult.put(Common.DEBIT_JSON_ARRAY, jsonDebitArray);
                    Log.d(this.className, jsonObjectResult.toString());
                }
                catch (Exception e) {
                    Log.e(this.className, "Exception Building Debit JSON For POST :: " + e.getMessage());
                }
            }
        }
        else if(httpType == Common.HTTP_TYPE_GET) {
            try {
                //GET Functionality Not implemented at this time.
//                jsonObjectResult = new JSONObject();
//                jsonObjectResult.put("type", Common.HTTP_GET_JSON_TEXT);
//                jsonObjectResult.put("user", userName);
//                jsonObjectResult.put(Common.DEBIT_JSON_ARRAY, jsonDebitArray);    //Just an Empty Array
                Log.d(this.className, jsonObjectResult.toString());
            }
            catch (Exception e) {
                Log.e(this.className, "Exception Building Debit JSON For GET :: " + e.getMessage());
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
                        debitJSON = new JSONObject(((Debit)syncObject).getMap());
                        jsonDebitArray.put(debitJSON);
                    }
                    jsonObjectResult.put(Common.DEBIT_JSON_ARRAY, jsonDebitArray);
                }
                Log.d(this.className, jsonObjectResult.toString());
            }
            catch (Exception e) {
                Log.e(this.className, "Exception Building Debit JSON For VERIFY :: " + e.getMessage());
            }

        }

        return jsonObjectResult; //Null if no records to verify
    }

    @Override
    public List<SyncObject> parseJSONList(JSONObject jsonObject) {
        List<SyncObject>    syncObjects = new ArrayList<SyncObject>();
        JSONArray           jsonArray;
        JSONObject          debitJSONObject;
        Debit               debit;

        try {
            jsonArray = jsonObject.getJSONArray(Common.DEBIT_JSON_ARRAY);
            for (int i = 0; i < jsonArray.length(); i++) {
                debitJSONObject = jsonArray.getJSONObject(i);
                debit = new Debit();
                debit.JSONToObject(debitJSONObject);
                syncObjects.add(debit);
            }
        }
        catch (Exception e) {
            syncObjects.clear(); //Wipe out anything that may have been put in.
            Log.e(this.className, "Exception parsing JSON To Debit List :: ".concat(e.getMessage()));
        }

        return syncObjects;
    }
}
