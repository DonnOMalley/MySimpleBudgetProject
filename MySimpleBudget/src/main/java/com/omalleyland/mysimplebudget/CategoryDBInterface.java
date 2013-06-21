package com.omalleyland.mysimplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omal310371 on 6/7/13.
 */
public class CategoryDBInterface implements IObjectDBInterface {

    private final Context context;
    private final String className;
    private DBHelper dbHelper;

    public CategoryDBInterface(Context context) {
        className = getClass().toString();
        Log.v(className, "CategoryDBInterface(Context) Constructor");
        this.context = context;
        dbHelper = new DBHelper(this.context);
    }

    @Override
    public SyncObject cursorToSyncObject(Cursor cursor) {

        Log.v(className, "Writing Cursor to Category Object");

        Category category = new Category();

        category.setID(cursor.getInt(Common.colCATEGORY_ID_INDEX));
        category.setName(cursor.getString(Common.colCATEGORY_NAME_INDEX));
        category.setServerID(cursor.getInt(Common.colCATEGORY_SERVER_ID_INDEX));
        category.setSyncStatus(cursor.getInt(Common.colCATEGORY_SYNC_STATUS_INDEX));
        category.setActiveStatus(cursor.getInt(Common.colCATEGORY_ACTIVE_STATUS_INDEX));
        return category;
    }

    @Override
    public long addObject(SyncObject syncObject) {
        SQLiteDatabase db;
        long insertId = -1;
        if(syncObject != null) {
            Log.d(className, "Adding Category To Database :: id = " + Integer.toString(syncObject.getID()) +
                                ", name = " + syncObject.getName() +
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
        int result = -1;
        if(syncObject != null) {
            SQLiteDatabase db;
            Log.d(className, "Deleting Category From Database :: id = " + Integer.toString(syncObject.getID()) +
                                ", name = " + syncObject.getName() +
                                ", serverID = " + Integer.toString(syncObject.getServerID()) +
                                ", syncStatus = " + Integer.toString(syncObject.getSyncStatus()) +
                                ", activeStatus = " + Integer.toString(syncObject.getActiveStatus()));
            try {
                db = dbHelper.getWritableDatabase();
                Log.v(className, "Performing Delete From Category Table");
                result = db.delete(Common.tblCATEGORIES, Common.colCATEGORY_NAME + "=?", new String[] { syncObject.getName() });
            }
            catch (Exception e) {
                Log.e(className, "Exception Removing Category :: name = '" + syncObject.getName() + "' :: " + e.getMessage());
            }
            dbHelper.close();
        }
        else {
            Log.e(className, "Unable to delete 'NULL' category from Database");
        }
        return result;
    }

    @Override
    public SyncObject getObject(int id) {
        SQLiteDatabase db;
        Category category = new Category();
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Querying Category by ID = " + Integer.toString(id));
            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL, Common.colCATEGORY_ID + " = " + id, null, null, null, null);
            if(cursor.getCount() == 1) {
                cursor.moveToFirst();
                category = (Category)cursorToSyncObject(cursor);
                Log.d(className, "Category Record Returned :: id = " + Integer.toString(category.getID()) +
                                    ", name = " + category.getName() +
                                    ", serverID = " + Integer.toString(category.getServerID()) +
                                    ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                                    ", activeStatus = " + Integer.toString(category.getActiveStatus()));
            }
            else {
                Log.d(className, "Category Not Found :: Cursor Record Count = " + Integer.toString(cursor.getCount()));
                category = null;
            }
            cursor.close();
        }
        catch (Exception e) {
            Log.e(className, "Exception Getting Category By categoryID :: " + e.getMessage());
        }

        dbHelper.close();
        return category;
    }

    @Override
    public SyncObject getObject(String name) {
        SQLiteDatabase db;
        Category category = new Category();
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Querying Category by Name = " + name);
            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL, Common.colCATEGORY_NAME + " = '" + name + "'", null, null, null, null);
            if(cursor.getCount()== 1) {
                cursor.moveToFirst();
                category = (Category)cursorToSyncObject(cursor);
                Log.d(className, "Category Record Returned :: id = " + Integer.toString(category.getID()) +
                                    ", name = " + category.getName() +
                                    ", serverID = " + Integer.toString(category.getServerID()) +
                                    ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                                    ", activeStatus = " + Integer.toString(category.getActiveStatus()));
            }
            else {
                Log.d(className, "Category Not Found :: Cursor Record Count = " + Integer.toString(cursor.getCount()));
                category = null;
            }
            cursor.close();
        }
        catch (Exception e) {
            Log.e(className, "Exception Getting Category By Name :: " + e.getMessage());
        }
        dbHelper.close();
        return category;
    }

    @Override
    public List<SyncObject> getActiveDatabaseObjects(){
        SQLiteDatabase db;
        Category category;
        List<SyncObject> categoryList = new ArrayList<SyncObject>();
        String[] whereArgs = new String[]{Integer.toString(Common.ACTIVE_STATUS_ACTIVE)};
        String whereClause = Common.colCATEGORY_ACTIVE_STATUS + " = ?";
        Log.v(className, "Querying List of Active Categories");


        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL, whereClause, whereArgs, null, null, Common.colCATEGORY_NAME);
            Log.d(className, "Number of Category Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    category = (Category)cursorToSyncObject(cursor);
                    categoryList.add(category);
                    Log.d(className, "Store Record Returned :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getName() +
                            ", serverID = " + Integer.toString(category.getServerID()) +
                            ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(category.getActiveStatus()));
                } while (cursor.moveToNext());
            }
            Log.d(className, "Category List Populated :: Size = " + Integer.toString(categoryList.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Category List :: " + e.getMessage());
        }
        dbHelper.close();
        return categoryList;
    }

    @Override
    public List<SyncObject> getAllDatabaseObjects() {
        SQLiteDatabase db;
        List<SyncObject> categoryList = new ArrayList<SyncObject>();
        Category category;
        Log.v(className, "Querying List of All Categories");
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL,null,null,null,null,Common.colCATEGORY_NAME);
            Log.d(className, "Number of Category Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    category = (Category)cursorToSyncObject(cursor);
                    categoryList.add(category);
                    Log.d(className, "Category Record Returned :: id = " + Integer.toString(category.getID()) +
                                        ", name = " + category.getName() +
                                        ", serverID = " + Integer.toString(category.getServerID()) +
                                        ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                                        ", activeStatus = " + Integer.toString(category.getActiveStatus()));
                } while (cursor.moveToNext());
            }
            Log.d(className, "Category List Populated :: Size = " + Integer.toString(categoryList.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Category List :: " + e.getMessage());
        }
        dbHelper.close();
        return categoryList;
    }

    @Override
    public List<SyncObject> getUpdatedDatabaseObjects(List<Integer> objectSyncStatuses) {
        SQLiteDatabase db;
        List<SyncObject> categoryList = new ArrayList<SyncObject>();
        String whereClause = null;
        List<String> statusListStrings = new ArrayList<String>();
        String[] whereArgs = null;

        //Populate Where Clause/Arguments if SyncStatuses were provided
        // If no statuses were passed
        Log.d(this.className, "Building SQLite Where Clause");
        if(objectSyncStatuses != null) {
            for(Integer syncStatus : objectSyncStatuses) {
                if(whereClause == null) {
                    whereClause = Common.colCATEGORY_SYNC_STATUS + " IN (?";
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

        Category category;
        if(whereArgs != null) {

            Log.v(className, "Querying List of Categories from Local Database based on SyncStatuses :: ".concat(statusListStrings.toString()));
        }
        try {
            db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL, whereClause, whereArgs, null, null, Common.colCATEGORY_SYNC_STATUS);
            Log.d(className, "Number of Category Records = " + Integer.toString(cursor.getCount()));
            if(cursor.getCount() > 0 && cursor.moveToFirst()) {
                do {
                    category = (Category)cursorToSyncObject(cursor);
                    categoryList.add(category);
                    Log.d(className, "Category Record Returned :: id = " + Integer.toString(category.getID()) +
                                        ", name = " + category.getName() +
                                        ", serverID = " + Integer.toString(category.getServerID()) +
                                        ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                                        ", activeStatus = " + Integer.toString(category.getActiveStatus()));
                } while (cursor.moveToNext());
            }
            Log.d(className, "Category List Populated :: Size = " + Integer.toString(categoryList.size()));
        }
        catch (Exception e) {
            Log.e(className, "Exception Querying Updated Category List :: " + e.getMessage());
        }
        dbHelper.close();
        return categoryList;
    }

    @Override
    public int updateDatabaseObjects(List<SyncObject> syncObjects) {
        //For each record in the List<Category>, update SQLite
        SQLiteDatabase db;
        int updatedRecords = 0;
        String whereClause = Common.colCATEGORY_NAME + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();

        Log.v(className, "Updating Categories with Values from List");
        try {

            Category existingCategory;
            for(SyncObject syncObject : syncObjects) {
                //Check for category to exist,
                //  if exists, update
                //  else add
                existingCategory = (Category)getObject(syncObject.getName());
                if(existingCategory!=null) {
                    db = dbHelper.getWritableDatabase();
                    values.clear();
                    values.put(Common.colCATEGORY_NAME, syncObject.getName());
                    values.put(Common.colCATEGORY_SERVER_ID, syncObject.getServerID());
                    values.put(Common.colCATEGORY_SYNC_STATUS, syncObject.getSyncStatus());
                    values.put(Common.colCATEGORY_ACTIVE_STATUS, syncObject.getActiveStatus());
                    whereArgs = new String[]{syncObject.getName()};
                    Log.d(className, "Updating Category Record :: id = " + Integer.toString(syncObject.getID()) +
                            ", name = " + syncObject.getName() +
                            ", serverID = " + Integer.toString(syncObject.getServerID()) +
                            ", syncStatus = " + Integer.toString(syncObject.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(syncObject.getActiveStatus()));
                    updatedRecords = updatedRecords + db.update(Common.tblCATEGORIES, values, whereClause, whereArgs);
                    dbHelper.close();
                }
                else {
                    if(addObject(syncObject) > -1) {
                        updatedRecords += 1;
                    }
                }
            }
            Log.d(className, "Number of Category Records = " + Integer.toString(updatedRecords));
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Categories from Server :: " + e.getMessage());
        }
        return updatedRecords;
    }

    @Override
    public int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects, int syncStatus) {
        //For each record in the List<Category>, update SQLite
        SQLiteDatabase db;
        int updatedRecords = 0;
        String whereClause = Common.colCATEGORY_NAME + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();

        Log.v(className, "Updating Categories with Values from List");
        try {

            Category existingCategory;
            for(SyncObject syncObject : syncObjects) {
                //Check for category to exist,
                //  if exists, update
                //  else add
                existingCategory = (Category)getObject(syncObject.getName());
                if(existingCategory!=null) {
                    db = dbHelper.getWritableDatabase();
                    values.clear();
                    values.put(Common.colCATEGORY_NAME, syncObject.getName());
                    values.put(Common.colCATEGORY_SERVER_ID, syncObject.getServerID());
                    values.put(Common.colCATEGORY_SYNC_STATUS, syncStatus);
                    values.put(Common.colCATEGORY_ACTIVE_STATUS, syncObject.getActiveStatus());
                    whereArgs = new String[]{syncObject.getName()};
                    Log.d(className, "Updating Category Record :: id = " + Integer.toString(syncObject.getID()) +
                            ", name = " + syncObject.getName() +
                            ", serverID = " + Integer.toString(syncObject.getServerID()) +
                            ", syncStatus = " + Integer.toString(syncStatus) +
                            ", activeStatus = " + Integer.toString(syncObject.getActiveStatus()));
                    updatedRecords = updatedRecords + db.update(Common.tblCATEGORIES, values, whereClause, whereArgs);
                    dbHelper.close();
                }
                else {
                    if(addObject(syncObject) > -1) {
                        updatedRecords += 1;
                    }
                }
            }
            Log.d(className, "Number of Category Records = " + Integer.toString(updatedRecords));
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Categories from Server :: " + e.getMessage());
        }
        return updatedRecords;
    }

    @Override
    public JSONObject buildJSON(int httpType, List<Integer> objectSyncStatuses, String userName, String password) {
        List<SyncObject> syncObjects    = new ArrayList<SyncObject>();
        Category category;
        JSONObject  jsonObjectResult    = null;
        JSONObject  categoryJSON;
        JSONArray   jsonCategoryArray   = new JSONArray();

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
                        categoryJSON = new JSONObject(((Category)syncObject).getMap());
                        jsonCategoryArray.put(categoryJSON);
                    }
                    jsonObjectResult.put(Common.CATEGORY_JSON_ARRAY, jsonCategoryArray);
                    Log.d(this.className, jsonObjectResult.toString());
                }
                catch (Exception e) {
                    Log.e(this.className, "Exception Building Category JSON For POST :: " + e.getMessage());
                }
            }
        }
        else if(httpType == Common.HTTP_TYPE_GET) {
            try {
                jsonObjectResult = new JSONObject();
                jsonObjectResult.put("type", Common.HTTP_GET_JSON_TEXT);
                jsonObjectResult.put("user", userName);
                jsonObjectResult.put(Common.CATEGORY_JSON_ARRAY, jsonCategoryArray);    //Just an Empty Array
                Log.d(this.className, jsonObjectResult.toString());
            }
            catch (Exception e) {
                Log.e(this.className, "Exception Building Category JSON For GET :: " + e.getMessage());
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
                        categoryJSON = new JSONObject(((Category)syncObject).getMap());
                        jsonCategoryArray.put(categoryJSON);
                    }
                    jsonObjectResult.put(Common.CATEGORY_JSON_ARRAY, jsonCategoryArray);
                }
                Log.d(this.className, jsonObjectResult.toString());
            }
            catch (Exception e) {
                Log.e(this.className, "Exception Building Category JSON For GET :: " + e.getMessage());
            }

        }

        return jsonObjectResult; //Null if no records to verify
    }

    public List<SyncObject> parseJSONList(JSONObject jsonObject) {
        List<SyncObject>    syncObjects = new ArrayList<SyncObject>();
        JSONArray           jsonArray;
        JSONObject          categoryJSONObject;
        Category            category;

        try {
            jsonArray = jsonObject.getJSONArray(Common.CATEGORY_JSON_ARRAY);
            for (int i = 0; i < jsonArray.length(); i++) {
                categoryJSONObject = jsonArray.getJSONObject(i);
                category = new Category();
                category.JSONToObject(categoryJSONObject);
                syncObjects.add(category);
            }
        }
        catch (Exception e) {
            syncObjects.clear(); //Wipe out anything that may have been put in.
            Log.e(this.className, "Exception parsing JSON To Category List :: ".concat(e.getMessage()));
        }

        return syncObjects;
    }
}

