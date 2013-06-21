package com.omalleyland.mysimplebudget;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by omal310371 on 6/7/13.
 */
public class Store extends SyncObject{

    public Store() {
        super(Common.SYNC_OBJECT_TYPE_STORE);
        this.className      = getClass().toString();
    }

    public Store(String storeName) {
        super(Common.SYNC_OBJECT_TYPE_STORE);
        this.className      = getClass().toString();
        this.name           = storeName;
    }

    public Store(String storeName, int serverID) {
        super(Common.SYNC_OBJECT_TYPE_STORE);
        this.className      = getClass().toString();
        this.name           = storeName;
        this.serverID       = serverID;
    }

    public Store(int id, String storeName) {
        super(Common.SYNC_OBJECT_TYPE_STORE);
        this.className      = getClass().toString();
        this.id             = id;
        this.name           = storeName;
    }

    public Store(int id, String storeName, int serverID) {
        super(Common.SYNC_OBJECT_TYPE_STORE);
        this.className      = getClass().toString();
        this.id             = id;
        this.name           = storeName;
        this.serverID       = serverID;
    }

    public Store(int id, String storeName, int serverID, int syncStatus, int activeStatus) {
        super(Common.SYNC_OBJECT_TYPE_STORE);
        this.className      = getClass().toString();
        this.id             = id;
        this.name           = storeName;
        this.serverID       = serverID;
        this.syncStatus     = syncStatus;
        this.activeStatus   = activeStatus;
    }

    public Map<String, String> getMap() {
        Map<String, String> categoryMap = new HashMap<String, String>();
        categoryMap.put(Common.colSTORE_ID, Integer.toString(this.id));
        categoryMap.put(Common.colSTORE_NAME, this.name);
        categoryMap.put(Common.colSTORE_ACTIVE_STATUS, Integer.toString(this.activeStatus));

        return categoryMap;
    }
}

