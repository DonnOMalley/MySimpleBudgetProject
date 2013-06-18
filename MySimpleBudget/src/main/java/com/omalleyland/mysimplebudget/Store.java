package com.omalleyland.mysimplebudget;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by omal310371 on 6/7/13.
 */
public class Store {
    private int     id;
    private String  storeName;
    private int     serverID;
    private int     syncStatus;
    private int     activeStatus;
    private String  className = "Store";

    public Store() {
        Log.v(className, "Store Empty Constructor");
        this.id             = -1;
        this.storeName      = "";
        this.serverID       = -1;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Store(String storeName) {
        Log.v(className, "Store(storeName) Constructor");
        this.id             = -1;
        this.storeName      = storeName;
        this.serverID       = -1;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Store(String storeName, int serverID) {
        Log.v(className, "Store(storeName, serverID) Constructor");
        this.id             = -1;
        this.storeName      = storeName;
        this.serverID       = serverID;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Store(int id, String storeName) {
        Log.v(className, "Store(id, storeName) Constructor");
        this.id             = id;
        this.storeName      = storeName;
        this.serverID       = -1;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Store(int id, String storeName, int serverID) {
        Log.v(className, "Store(id, storeName, serverID) Constructor");
        this.id             = id;
        this.storeName      = storeName;
        this.serverID       = serverID;
        this.syncStatus     = Common.SYNC_STATUS_SYNCHRONIZED;
        this.activeStatus   = Common.ACTIVE_STATUS_INACTIVE;
    }

    public Store(int id, String storeName, int serverID, int syncStatus, int activeStatus) {
        Log.v(className, "Store(id, storeName, serverID, syncStatus, activeStatus) Constructor");
        this.id             = id;
        this.storeName      = storeName;
        this.serverID       = serverID;
        this.syncStatus     = syncStatus;
        this.activeStatus   = activeStatus;
    }

    public void setID(int id) {
        this.id = id;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
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

    public String getStoreName() {
        return this.storeName;
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
        categoryMap.put(Common.colSTORE_ID, Integer.toString(this.id));
        categoryMap.put(Common.colSTORE_NAME, this.storeName);
        categoryMap.put(Common.colSTORE_ACTIVE_STATUS, Integer.toString(this.activeStatus));

        return categoryMap;
    }

    public void JSONToObject(JSONObject jsonObject) {
        try {
            this.serverID = jsonObject.getInt("id");
            this.storeName = jsonObject.getString("name");
            this.activeStatus = jsonObject.getInt("activeStatus");
        }
        catch (Exception e) {
            //Do nothing for now
        }
    }

    @Override
    public String toString() {
        return this.storeName;
    }
}

