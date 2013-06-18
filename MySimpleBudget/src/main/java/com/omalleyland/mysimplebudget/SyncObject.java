package com.omalleyland.mysimplebudget;

import android.util.Log;
import android.widget.Switch;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by omal310371 on 6/18/13.
 */
public class SyncObject {

    protected int       type;
    protected int       id;
    protected String    name;
    protected int       serverID;
    protected int       syncStatus;
    protected int       activeStatus;
    protected String    className;

    protected SyncObject() {
        this.type           = Common.UNKNOWN;
        this.id             = Common.UNKNOWN;
        this.name           = "";
        this.serverID       = Common.UNKNOWN;
        this.syncStatus     = Common.UNKNOWN;
        this.activeStatus   = Common.UNKNOWN;
        this.className      = getClass().toString();
        Log.d(className, "Default SyncObject Created");
    }

    protected SyncObject(int type) {
        this.type           = type;
        this.id             = Common.UNKNOWN;
        this.name           = "";
        this.serverID       = Common.UNKNOWN;
        this.syncStatus     = Common.UNKNOWN;
        this.activeStatus   = Common.UNKNOWN;
        this.className      = getClass().toString();
        switch (this.type) {
            case Common.SYNC_OBJECT_TYPE_CATEGORY:
                Log.d(className, "Category SyncObject Created");
                break;
            case Common.SYNC_OBJECT_TYPE_STORE:
                Log.d(className, "Store SyncObject Created");
                break;
            default:
                Log.e(className, "Invalid SyncObject Type Created");
        }
    }

    protected void setID(int id) {
        this.id = id;
    }

    protected void setName(String name) {
        this.name = name;
    }

    protected void setServerID(int serverID) {
        this.serverID = serverID;
    }

    protected void setSyncStatus(int syncStatus) {
        this.syncStatus = syncStatus;
    }

    protected void setActiveStatus(int activeStatus) {
        this.activeStatus = activeStatus;
    }

    protected int getID() {
        return this.id;
    }

    protected String getName() {
        return this.name;
    }

    protected int getServerID() {
        return this.serverID;
    }

    protected int getSyncStatus() {
        return this.syncStatus;
    }

    protected int getActiveStatus() {
        return this.activeStatus;
    }

    protected void JSONToObject(JSONObject jsonObject) {
        try {
            this.serverID = jsonObject.getInt("id");
            this.name = jsonObject.getString("name");
            this.activeStatus = jsonObject.getInt("activeStatus");
        }
        catch (Exception e) {
            //Do nothing for now
        }
    }

    @Override
    public String toString() {
        return this.name;
    }
}
