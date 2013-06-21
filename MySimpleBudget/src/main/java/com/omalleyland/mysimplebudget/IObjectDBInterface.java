package com.omalleyland.mysimplebudget;

import android.database.Cursor;

import org.json.JSONObject;

import java.util.Collection;
import java.util.List;

/**
 * Created by omal310371 on 6/18/13.
 */
public interface IObjectDBInterface {

    /* For writing a cursor to a SyncObject Child */
    public abstract SyncObject cursorToSyncObject(Cursor cursor);

    public abstract long addObject(SyncObject syncObject);

    public abstract int deleteObject(SyncObject syncObject);

    public abstract SyncObject getObject(int id);

    public abstract SyncObject getObject(String name);

    public abstract List<SyncObject> getAllDatabaseObjects();

    public abstract List<SyncObject> getActiveDatabaseObjects();

    public abstract List<SyncObject> getUpdatedDatabaseObjects(List<Integer> objectSyncStatuses);

    public abstract int updateDatabaseObjects(List<SyncObject> syncObjects);

    public abstract int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects, int syncStatus);

    public abstract JSONObject buildJSON(int httpType, List<Integer> objectSyncStatuses, String userName, String password);

    public abstract List<SyncObject> parseJSONList(JSONObject jsonObject);

}
