package com.omalleyland.mysimplebudget;

import android.database.Cursor;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by omal310371 on 6/20/13.
 */
public class DebitDBInterface implements IObjectDBInterface {
    @Override
    public SyncObject cursorToSyncObject(Cursor cursor) {
        return null;
    }

    @Override
    public long addObject(SyncObject syncObject) {
        return 0;
    }

    @Override
    public int deleteObject(SyncObject syncObject) {
        return 0;
    }

    @Override
    public SyncObject getObject(int id) {
        return null;
    }

    @Override
    public SyncObject getObject(String name) {
        return null;
    }

    @Override
    public List<SyncObject> getAllDatabaseObjects() {
        return null;
    }

    @Override
    public List<SyncObject> getActiveDatabaseObjects() {
        return null;
    }

    @Override
    public List<SyncObject> getUpdatedDatabaseObjects(List<Integer> objectSyncStatuses) {
        return null;
    }

    @Override
    public int updateDatabaseObjects(List<SyncObject> syncObjects) {
        return 0;
    }

    @Override
    public int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects, int syncStatus) {
        return 0;
    }

    @Override
    public JSONObject buildJSON(int httpType, List<Integer> objectSyncStatuses, String userName, String password) {
        return null;
    }

    @Override
    public List<SyncObject> parseJSONList(JSONObject jsonObject) {
        return null;
    }
}
