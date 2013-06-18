package com.omalleyland.mysimplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omal310371 on 6/7/13.
 */
public class CategoryDBInterface implements IObjectDBInterface {

    private final Context context;
    private final String className;
    private DBHelper dbHelper;

    public CategoryDBInterface(Context ctx) {
        className = getClass().toString();
        Log.v(className, "CategoryDBInterface(Context) Constructor");
        this.context = ctx;
        dbHelper = new DBHelper(this.context);
    }

    @Override
    public SyncObject cursorToSyncObject(Cursor cursor) {

        Log.v(className, "Writing Cursor to Category Object");

        Category category;
        category = new Category();

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
        Category category = (Category)syncObject;
        if(category != null) {
            Log.d(className, "Adding Category To Database :: id = " + Integer.toString(category.getID()) +
                                ", name = " + category.getName() +
                                ", serverID = " + Integer.toString(category.getServerID()) +
                                ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                                ", activeStatus = " + Integer.toString(category.getActiveStatus()));
            try {
                db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                if(category.getID() > -1) {
                    values.put(Common.colCATEGORY_ID, category.getID());
                }
                values.put(Common.colCATEGORY_NAME, category.getName());
                if(category.getServerID() > -1) {
                    values.put(Common.colCATEGORY_SERVER_ID, category.getServerID());
                }
                values.put(Common.colCATEGORY_SYNC_STATUS, category.getSyncStatus());
                values.put(Common.colCATEGORY_ACTIVE_STATUS, category.getActiveStatus());
                Log.v(className, "Inserting into Category Table");
                insertId = db.insert(Common.tblCATEGORIES, null, values);
            }
            catch (Exception e) {
                Log.e(className, "Exception Adding Category :: name = '" + category.getName() + "' :: " + e.getMessage());
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
        Category category = (Category)syncObject;
        int result = -1;
        if(category != null) {
            SQLiteDatabase db;
            Log.d(className, "Deleting Category From Database :: id = " + Integer.toString(category.getID()) +
                                ", name = " + category.getName() +
                                ", serverID = " + Integer.toString(category.getServerID()) +
                                ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                                ", activeStatus = " + Integer.toString(category.getActiveStatus()));
            try {
                db = dbHelper.getWritableDatabase();
                Log.v(className, "Performing Delete From Category Table");
                result = db.delete(Common.tblCATEGORIES, Common.colCATEGORY_NAME + "=?", new String[] { category.getName() });
            }
            catch (Exception e) {
                Log.e(className, "Exception Removing Category :: name = '" + category.getName() + "' :: " + e.getMessage());
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
    public List<SyncObject> getUpdatedDatabaseObjects() {
        SQLiteDatabase db;
        List<SyncObject> categoryList = new ArrayList<SyncObject>();
        String whereClause = Common.colCATEGORY_SYNC_STATUS + " IN (?,?)";
        String[] whereArgs = {Integer.toString(Common.SYNC_STATUS_NEW), Integer.toString(Common.SYNC_STATUS_UPDATED)};

        Category category;
        Log.v(className, "Querying List of All Categories To Post to Server");
        try {
            db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL, whereClause, whereArgs, null, null, Common.colCATEGORY_SYNC_STATUS);
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
            Log.e(className, "Exception Querying Updated Category List :: " + e.getMessage());
        }
        dbHelper.close();
        return categoryList;
    }

    @Override
    public int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects) {
        //For each record in the List<Category>, update SQLite
        SQLiteDatabase db;
        int updatedRecords = 0;
        String whereClause = Common.colCATEGORY_NAME + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();

        Log.v(className, "Updating Categories with Values from List");
        try {
            db = dbHelper.getWritableDatabase();

            Category existingCategory;
            for(SyncObject syncObject : syncObjects) {
                Category category = (Category)syncObject;
                //Check for category to exist,
                //  if exists, update
                //  else add
                existingCategory = (Category)getObject(category.getName());
                if(existingCategory!=null) {
                    values.clear();
                    values.put(Common.colCATEGORY_ID, category.getID());
                    values.put(Common.colCATEGORY_NAME, category.getName());
                    values.put(Common.colCATEGORY_SERVER_ID, category.getServerID());
                    values.put(Common.colCATEGORY_SYNC_STATUS, category.getSyncStatus());
                    values.put(Common.colCATEGORY_ACTIVE_STATUS, category.getActiveStatus());
                    whereArgs = new String[]{category.getName()};
                    Log.d(className, "Updating Category Record :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getName() +
                            ", serverID = " + Integer.toString(category.getServerID()) +
                            ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(category.getActiveStatus()));
                    updatedRecords = updatedRecords + db.update(Common.tblCATEGORIES, values, whereClause, whereArgs);
                }
                else {
                    if(addObject(category) > -1) {
                        updatedRecords += 1;
                    }
                }
            }
            Log.d(className, "Number of Category Records = " + Integer.toString(updatedRecords));
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Categories from Server :: " + e.getMessage());
        }
        dbHelper.close();
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
            db = dbHelper.getWritableDatabase();

            Category existingCategory;
            for(SyncObject syncObject : syncObjects) {
                Category category = (Category)syncObject;
                //Check for category to exist,
                //  if exists, update
                //  else add
                existingCategory = (Category)getObject(category.getName());
                if(existingCategory!=null) {
                    values.clear();
                    values.put(Common.colCATEGORY_ID, category.getID());
                    values.put(Common.colCATEGORY_NAME, category.getName());
                    values.put(Common.colCATEGORY_SERVER_ID, category.getServerID());
                    values.put(Common.colCATEGORY_SYNC_STATUS, syncStatus);
                    values.put(Common.colCATEGORY_ACTIVE_STATUS, category.getActiveStatus());
                    whereArgs = new String[]{category.getName()};
                    Log.d(className, "Updating Category Record :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getName() +
                            ", serverID = " + Integer.toString(category.getServerID()) +
                            ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(category.getActiveStatus()));
                    updatedRecords = updatedRecords + db.update(Common.tblCATEGORIES, values, whereClause, whereArgs);
                }
                else {
                    if(addObject(category) > -1) {
                        updatedRecords += 1;
                    }
                }
            }
            Log.d(className, "Number of Category Records = " + Integer.toString(updatedRecords));
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Categories from Server :: " + e.getMessage());
        }
        dbHelper.close();
        return updatedRecords;
    }
}

