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
        this.className  = getClass().getName();
    }

    public Category(String categoryName) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        this.name          = categoryName;
        this.className     = getClass().getName();
    }

    public Category(String categoryName, int serverID) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        super.name          = categoryName;
        super.serverID      = serverID;
        super.className     = getClass().getName();
    }

    public Category(int id, String categoryName) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        super.id            = id;
        super.name          = categoryName;
        super.className     = getClass().getName();
    }

    public Category(int id, String categoryName, int serverID) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        super.id            = id;
        super.name          = categoryName;
        super.serverID      = serverID;
        super.className     = getClass().getName();
    }

    public Category(int id, String categoryName, int serverID, int syncStatus, int activeStatus) {
        super(Common.SYNC_OBJECT_TYPE_CATEGORY);
        super.id            = id;
        super.name          = categoryName;
        super.serverID      = serverID;
        super.className     = getClass().getName();
        super.syncStatus    = syncStatus;
        super.activeStatus  = activeStatus;
    }

    public Map<String, String> getMap() {
        Map<String, String> categoryMap = new HashMap<String, String>();
        categoryMap.put(Common.colCATEGORY_ID, Integer.toString(this.id));
        categoryMap.put(Common.colCATEGORY_NAME, this.name);
        categoryMap.put(Common.colCATEGORY_ACTIVE_STATUS, Integer.toString(this.activeStatus));

        return categoryMap;
    }
}

