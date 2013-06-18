package com.omalleyland.mysimplebudget;

import android.database.Cursor;
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

    public abstract List<SyncObject> getUpdatedDatabaseObjects();

    public abstract int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects);

    public abstract int updateDatabaseObjectsSyncStatus(List<SyncObject> syncObjects, int syncStatus);

}
