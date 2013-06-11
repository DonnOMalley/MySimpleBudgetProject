package com.omalleyland.mysimplebudget;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by omal310371 on 6/7/13.
 */
public class CategoryDBInterface {

    private final Context context;
    private final String className;
    private DBHelper dbHelper;
    private SQLiteDatabase db;

    public CategoryDBInterface(Context ctx) {
        className = getClass().toString();
        Log.v(className, "CategoryDBInterface(Context) Constructor");
        this.context = ctx;
        dbHelper = new DBHelper(this.context);
    }

    private void openDB() throws SQLException {
        Log.v(className, "Opening Database");
        db = dbHelper.getWritableDatabase();
    }

    private void closeDB() {
        Log.v(className, "Closing Database");
        dbHelper.close();
        db = null;
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
        long insertId = -1;
        Log.d(className, "Adding Category To Database :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getCategoryName() +
                            ", serverID = " + Integer.toString(category.getServerID()) +
                            ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(category.getActiveStatus()));
        try {
            openDB();
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
        catch (SQLException e) {
            Log.e(className, "Exception Adding Category :: name = '" + category.getCategoryName() + "' :: " + e.getMessage());
        }

        closeDB();
        Log.d(className, "CategoryID from Insert = " + Long.toString(insertId));
        return insertId;
    }

    public void deleteCategory(Category category) {
        Log.d(className, "Deleting Category From Database :: id = " + Integer.toString(category.getID()) +
                            ", name = " + category.getCategoryName() +
                            ", serverID = " + Integer.toString(category.getServerID()) +
                            ", syncStatus = " + Integer.toString(category.getSyncStatus()) +
                            ", activeStatus = " + Integer.toString(category.getActiveStatus()));
        try {
            openDB();
            Log.v(className, "Performing Delete From Category Table");
            db.delete(Common.tblCATEGORIES, Common.colCATEGORY_NAME + "=?", new String[] { category.getCategoryName() });
        }
        catch (Exception e) {
            Log.e(className, "Exception Removing Category :: name = '" + category.getCategoryName() + "' :: " + e.getMessage());
        }
        closeDB();
    }

    public Category getCategory(int id) {
        Category category = new Category();
        try {
            openDB();
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
        catch (SQLException e) {
            Log.d(className, "Exception Getting Category By categoryID :: " + e.getMessage());
        }

        closeDB();

        return category;
    }

    public Category getCategory(String categoryName) {
        Category category = new Category();
        try {
            openDB();
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
        catch (SQLException e) {
            Log.d(className, "Exception Getting Category By Name :: " + e.getMessage());
        }

        closeDB();

        return category;
    }

    public List<Category> getAllCategories() {
        List<Category> categoryList = new ArrayList<Category>();
        Category category;
        Log.v(className, "Querying List of All Categories");
        try {
            openDB();
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
        catch (SQLException e) {
            Log.e(className, "Exception Querying Category List :: " + e.getMessage());
        }
        closeDB();
        return categoryList;
    }
}

