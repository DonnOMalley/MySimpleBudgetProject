package com.omalleyland.mysimplebudget;

import android.text.style.SuperscriptSpan;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by omal310371 on 6/7/13.
 */
public class Category extends SyncObject{

    public Category() {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        this.className      = getClass().toString();
    }

    public Category(String categoryName) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        this.className      = getClass().toString();
        this.name           = categoryName;
    }

    public Category(String categoryName, int serverID) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        this.className      = getClass().toString();
        this.name           = categoryName;
        this.serverID       = serverID;
    }

    public Category(int id, String categoryName) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        this.className      = getClass().toString();
        this.id             = id;
        this.name           = categoryName;
    }

    public Category(int id, String categoryName, int serverID) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        this.className      = getClass().toString();
        this.id             = id;
        this.name           = categoryName;
        this.serverID       = serverID;
    }

    public Category(int id, String categoryName, int serverID, int syncStatus, int activeStatus) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        this.className      = getClass().toString();
        this.id             = id;
        this.name           = categoryName;
        this.serverID       = serverID;
        this.syncStatus     = syncStatus;
        this.activeStatus   = activeStatus;
    }

    public Map<String, String> getMap() {
        Map<String, String> categoryMap = new HashMap<String, String>();
        categoryMap.put(Common.colCATEGORY_ID, Integer.toString(this.id));
        categoryMap.put(Common.colCATEGORY_NAME, this.name);
        categoryMap.put(Common.colCATEGORY_ACTIVE_STATUS, Integer.toString(this.activeStatus));

        return categoryMap;
    }
}

