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
public class CategoryDBInterface {

    private final Context context;
    private final String className;
    private DBHelper dbHelper;

    public CategoryDBInterface(Context ctx) {
        className = getClass().toString();
        Log.v(className, "CategoryDBInterface(Context) Constructor");
        this.context = ctx;
        dbHelper = new DBHelper(this.context);
    }

    private Category cursorToCategory(Cursor cursor) {

        Log.v(className, "Writing Cursor to Category Object");
        Category category = new Category();
        category.setID(cursor.getInt(Common.colCATEGORY_ID_INDEX));
        category.setCategoryName(cursor.getString(Common.colCATEGORY_NAME_INDEX));
        category.setServerID(cursor.getInt(Common.colCATEGORY_SERVER_ID_INDEX));
        category.setSyncStatus(cursor.getInt(Common.colCATEGORY_SYNC_STATUS_INDEX));
        category.setActiveStatus(cursor.getInt(Common.colCATEGORY_ACTIVE_STATUS_INDEX));
        return category;
    }

    public long addCategory(Category category) {
        SQLiteDatabase db;
        long insertId = -1;
        Log.d(className, "Adding Category To Database :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getCategoryName() +
                            ", serverID = " + Integer.toString(category.getServerID()) +
                            ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(category.getActiveStatus()));
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            if(category.getID() > -1) {
                values.put(Common.colCATEGORY_ID, category.getID());
            }
            values.put(Common.colCATEGORY_NAME, category.getCategoryName());
            if(category.getServerID() > -1) {
                values.put(Common.colCATEGORY_SERVER_ID, category.getServerID());
            }
            values.put(Common.colCATEGORY_SYNC_STATUS, category.getSyncStatus());
            values.put(Common.colCATEGORY_ACTIVE_STATUS, category.getActiveStatus());
            Log.v(className, "Inserting into Category Table");
            insertId = db.insert(Common.tblCATEGORIES, null, values);
        }
        catch (Exception e) {
            Log.e(className, "Exception Adding Category :: name = '" + category.getCategoryName() + "' :: " + e.getMessage());
        }

        dbHelper.close();
        Log.d(className, "CategoryID from Insert = " + Long.toString(insertId));
        return insertId;
    }

    public void deleteCategory(Category category) {
        SQLiteDatabase db;
        Log.d(className, "Deleting Category From Database :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getCategoryName() +
                            ", serverID = " + Integer.toString(category.getServerID()) +
                            ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(category.getActiveStatus()));
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Performing Delete From Category Table");
            db.delete(Common.tblCATEGORIES, Common.colCATEGORY_NAME + "=?", new String[] { category.getCategoryName() });
        }
        catch (Exception e) {
            Log.e(className, "Exception Removing Category :: name = '" + category.getCategoryName() + "' :: " + e.getMessage());
        }
        dbHelper.close();
    }

    public Category getCategory(int id) {
        SQLiteDatabase db;
        Category category = new Category();
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Querying Category by ID = " + Integer.toString(id));
            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL, Common.colCATEGORY_ID + " = " + id, null, null, null, null);
            if(cursor.getCount() == 1) {
                cursor.moveToFirst();
                category = cursorToCategory(cursor);
                Log.d(className, "Category Record Returned :: id = " + Integer.toString(category.getID()) +
                        ", name = " + category.getCategoryName() +
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
            Log.d(className, "Exception Getting Category By categoryID :: " + e.getMessage());
        }

        dbHelper.close();
        return category;
    }

    public Category getCategory(String categoryName) {
        SQLiteDatabase db;
        Category category = new Category();
        try {
            db = dbHelper.getWritableDatabase();
            Log.v(className, "Querying Category by Name = " + categoryName);
            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL, Common.colCATEGORY_NAME + " = '" + categoryName + "'", null, null, null, null);
            if(cursor.getCount()== 1) {
                cursor.moveToFirst();
                category = cursorToCategory(cursor);
                Log.d(className, "Category Record Returned :: id = " + Integer.toString(category.getID()) +
                        ", name = " + category.getCategoryName() +
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
            Log.d(className, "Exception Getting Category By Name :: " + e.getMessage());
        }
        dbHelper.close();
        return category;
    }

    public List<Category> getAllCategories() {
        SQLiteDatabase db;
        List<Category> categoryList = new ArrayList<Category>();
        Category category;
        Log.v(className, "Querying List of All Categories");
        try {
            db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query(Common.tblCATEGORIES, Common.colCATEGORIES_ALL,null,null,null,null,Common.colCATEGORY_NAME);
            Log.d(className, "Number of Category Records = " + Integer.toString(cursor.getCount()));
            if(cursor.moveToFirst()) {
                do {
                    category = cursorToCategory(cursor);
                    categoryList.add(category);
                    Log.d(className, "Category Record Returned :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getCategoryName() +
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

    public List<Category> getCategoryUpdates() {
        SQLiteDatabase db;
        List<Category> categoryList = new ArrayList<Category>();
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
                    category = cursorToCategory(cursor);
                    categoryList.add(category);
                    Log.d(className, "Category Record Returned :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getCategoryName() +
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

    //Update SQLite Category Records based on List<Categories>
    public boolean updateCategoryRecords(List<Category> updatedCategories, int syncStatus) {
        //For each record in the List<Category>, update SQLite
        SQLiteDatabase db;
        boolean result = false;
        int updatedRecords = 0;
        String whereClause = Common.colCATEGORY_NAME + " = ?";
        String[] whereArgs;
        ContentValues values = new ContentValues();

        Log.v(className, "Updating Categories with Values from List");
        try {
            db = dbHelper.getWritableDatabase();

            for(Category category : updatedCategories) {
                values.clear();
                values.put(Common.colCATEGORY_ID, category.getID());
                values.put(Common.colCATEGORY_NAME, category.getCategoryName());
                values.put(Common.colCATEGORY_SERVER_ID, category.getServerID());
                values.put(Common.colCATEGORY_SYNC_STATUS, syncStatus);
                values.put(Common.colCATEGORY_ACTIVE_STATUS, category.getActiveStatus());
                whereArgs = new String[]{category.getCategoryName()};
                Log.d(className, "Updating Category Record :: id = " + Integer.toString(category.getID()) +
                        ", name = " + category.getCategoryName() +
                        ", serverID = " + Integer.toString(category.getServerID()) +
                        ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                        ", activeStatus = " + Integer.toString(category.getActiveStatus()));
                updatedRecords = updatedRecords + db.update(Common.tblCATEGORIES, values, whereClause, whereArgs);
            }
            Log.d(className, "Number of Category Records = " + Integer.toString(updatedRecords));
            result = true;
        }
        catch (Exception e) {
            Log.e(className, "Exception Updating Categories from Server :: " + e.getMessage());
        }
        dbHelper.close();
        return result;
    }
}

