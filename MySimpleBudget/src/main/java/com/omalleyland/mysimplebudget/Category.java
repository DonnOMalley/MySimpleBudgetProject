package com.omalleyland.mysimplebudget;

import android.util.Log;

import org.json.JSONObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by omal310371 on 6/7/13.
 */
public class Category {
    private int     id;
    private String  categoryName;
    private int     serverID;
    private int     syncStatus;
    private int     activeStatus;
    private String  className = "Category";

    public Category() {
        Log.v(className, "Category empty Constructor");
        this.id             = -1;
        this.categoryName   = "";
        this.serverID       = -1;
        this.syncStatus     = 0;
        this.activeStatus   = 0;
    }

    public Category(String categoryName) {
        Log.v(className, "Store(categoryName) Constructor");
        this.id             = -1;
        this.categoryName   = categoryName;
        this.serverID       = -1;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Category(String categoryName, int serverID) {
        Log.v(className, "Category(categoryName, serverID) Constructor");
        this.id             = -1;
        this.categoryName   = categoryName;
        this.serverID       = serverID;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Category(int id, String categoryName) {
        Log.v(className, "Category(id, categoryName) Constructor");
        this.id             = id;
        this.categoryName   = categoryName;
        this.serverID       = -1;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Category(int id, String categoryName, int serverID) {
        Log.v(className, "Category(id, categoryName, serverID) Constructor");
        this.id             = id;
        this.categoryName   = categoryName;
        this.serverID       = serverID;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Category(int id, String categoryName, int serverID, int syncStatus, int activeStatus) {
        Log.v(className, "Category(id, categoryName, serverID, syncStatus, activeStatus) Constructor");
        this.id             = id;
        this.categoryName   = categoryName;
        this.serverID       = serverID;
        this.syncStatus     = syncStatus;
        this.activeStatus   = activeStatus;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setServerID(int serverID) {
        this.serverID = serverID;
    }

    public void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    public void setActiveStatus(int activeStatus) {
        this.activeStatus = activeStatus;
    }

    public int getID() {
        return this.id;
    }

    public String getCategoryName() {
        return this.categoryName;
    }

    public int getServerID() {
        return this.serverID;
    }

    public int getSyncStatus() {
        return this.syncStatus;
    }

    public int getActiveStatus() {
        return this.activeStatus;
    }

    public Map<String, String> getMap() {
        Map<String, String> categoryMap = new HashMap<String, String>();
        categoryMap.put(Common.colCATEGORY_ID, Integer.toString(this.id));
        categoryMap.put(Common.colCATEGORY_ACTIVE_STATUS, Integer.toString(this.activeStatus));

        return categoryMap;
    }

    public void JSONToObject(JSONObject jsonObject) {
        try {
            this.serverID = jsonObject.getInt("id");
            this.categoryName = jsonObject.getString("name");
            this.activeStatus = jsonObject.getInt("activeStatus");
        }
        catch (Exception e) {
            //Do nothing for now
        }
    }

    @Override
    public String toString() {
        return this.categoryName;
    }
}

