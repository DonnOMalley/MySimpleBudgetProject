package com.omalleyland.mysimplebudget;

import android.util.Log;

import java.net.PortUnreachableException;
import java.security.PublicKey;

/**
 * Created by omal310371 on 6/6/13.
 */
public class Common {
    /* Application/Debug Constant */
    public static final String      APPLICATION_NAME                    = "MySimpleBudget";
    public static final int         UNKNOWN                             = -1;

    /* SQLite DB Constants */
    public static final int         DATABASE_VERSION                    = 2;
    public static final String      DATABASE_NAME                       = APPLICATION_NAME + "DB";

    /* Database Table Status Constants */
    public static final int         SYNC_STATUS_SYNCHRONIZED            = 0;
    public static final int         SYNC_STATUS_NEW                     = 1;
    public static final int         SYNC_STATUS_UPDATED                 = 2;
    public static final int         SYNC_STATUS_PENDING_VERIFY          = 3;
    public static final int         ACTIVE_STATUS_INACTIVE              = 0;
    public static final int         ACTIVE_STATUS_ACTIVE                = 1;

    /* SQLite Categories Table Constants */
    public static final String      tblCATEGORIES                       = "Categories";
    public static final String      colCATEGORY_ID                      = "id";
    public static final int         colCATEGORY_ID_INDEX                = 0;
    public static final String      colCATEGORY_NAME                    = "name";
    public static final int         colCATEGORY_NAME_INDEX              = 1;
    public static final String      colCATEGORY_SERVER_ID               = "serverID";
    public static final int         colCATEGORY_SERVER_ID_INDEX         = 2;
    public static final String      colCATEGORY_SYNC_STATUS             = "syncStatus";
    public static final int         colCATEGORY_SYNC_STATUS_INDEX       = 3;
    public static final String      colCATEGORY_ACTIVE_STATUS           = "activeStatus";
    public static final int         colCATEGORY_ACTIVE_STATUS_INDEX     = 4;
    public static final String[]    colCATEGORIES_ALL                   = {colCATEGORY_ID,
                                                                            colCATEGORY_NAME,
                                                                            colCATEGORY_SERVER_ID,
                                                                            colCATEGORY_SYNC_STATUS,
                                                                            colCATEGORY_ACTIVE_STATUS};
    public static final String      CREATE_CATEGORY_TABLE               = "CREATE TABLE " + tblCATEGORIES + "(" +
                                                                            colCATEGORY_ID + " integer PRIMARY KEY, " +
                                                                            colCATEGORY_NAME + " text NOT NULL UNIQUE, " +
                                                                            colCATEGORY_SERVER_ID + " integer UNIQUE, " +
                                                                            colCATEGORY_SYNC_STATUS + " integer NOT NULL, " +
                                                                            colCATEGORY_ACTIVE_STATUS + " integer NOT NULL);";
    public static final String      DROP_CATEGORY_TABLE                 = "DROP TABLE IF EXISTS " + tblCATEGORIES;

    /* SQLite Stores Table Constants */
    public static final String      tblSTORES                           = "Stores";
    public static final String      colSTORE_ID                         = "id";
    public static final int         colSTORE_ID_INDEX                   = 0;
    public static final String      colSTORE_NAME                       = "name";
    public static final int         colSTORE_NAME_INDEX                 = 1;
    public static final String      colSTORE_SERVER_ID                  = "serverID";
    public static final int         colSTORE_SERVER_ID_INDEX            = 2;
    public static final String      colSTORE_SYNC_STATUS                = "syncStatus";
    public static final int         colSTORE_SYNC_STATUS_INDEX          = 3;
    public static final String      colSTORE_ACTIVE_STATUS              = "activeStatus";
    public static final int         colSTORE_ACTIVE_STATUS_INDEX        = 4;
    public static final String[]    colSTORES_ALL                       = {colSTORE_ID,
                                                                            colSTORE_NAME,
                                                                            colSTORE_SERVER_ID,
                                                                            colSTORE_SYNC_STATUS,
                                                                            colSTORE_ACTIVE_STATUS};
    public static final String      CREATE_STORE_TABLE                  = "CREATE TABLE " + tblSTORES + "(" +
                                                                            colSTORE_ID + " integer PRIMARY KEY, " +
                                                                            colSTORE_NAME + " text NOT NULL UNIQUE, " +
                                                                            colSTORE_SERVER_ID + " integer UNIQUE, " +
                                                                            colSTORE_SYNC_STATUS + " integer NOT NULL, " +
                                                                            colSTORE_ACTIVE_STATUS + " integer NOT NULL);";
    public static final String      DROP_STORE_TABLE                    = "DROP TABLE IF EXISTS " + tblSTORES;

    /* SQLite Debits Table Constants */
    public static final String      tblDebits                           = "Debits";
    public static final String      colDEBIT_ID                         = "id";
    public static final int         colDEBIT_ID_INDEX                   = 0;
    public static final String      colDEBIT_PURCHASER_ID               = "purchaser_id";
    public static final int         colDEBIT_PURCHASER_ID_INDEX         = 1;
    public static final String      colDEBIT_LOCAL_CATEGORY_ID          = "localCategoryID";
    public static final int         colDEBIT_LOCAL_CATEGORY_ID_INDEX    = 2;
    public static final String      colDEBIT_SERVER_CATEGORY_ID         = "serverCategoryID";
    public static final int         colDEBIT_SERVER_CATEGORY_ID_INDEX   = 3;
    public static final String      colDEBIT_LOCAL_STORE_ID             = "localStoreID";
    public static final int         colDEBIT_LOCAL_STORE_ID_INDEX       = 4;
    public static final String      colDEBIT_SERVER_STORE_ID            = "serverStoreID";
    public static final int         colDEBIT_SERVER_STORE_ID_INDEX      = 5;
    public static final String      colDEBIT_DEBIT_DATE                 = "debitDate";
    public static final int         colDEBIT_DEBIT_DATE_INDEX           = 6;
    public static final String      colDEBIT_ENTRY_ON                   = "entryOn";
    public static final int         colDEBIT_ENTRY_ON_INDEX             = 7;
    public static final String      colDEBIT_DEBIT_AMOUNT               = "debitAmount";
    public static final int         colDEBIT_DEBIT_AMOUNT_INDEX         = 8;
    public static final String      colDEBIT_COMMENT                    = "comment";
    public static final int         colDEBIT_COMMENT_INDEX              = 9;
    public static final String[]    colDEBITS_ALL                       = {colDEBIT_ID,
                                                                            colDEBIT_PURCHASER_ID,
                                                                            colDEBIT_LOCAL_CATEGORY_ID,
                                                                            colDEBIT_SERVER_CATEGORY_ID,
                                                                            colDEBIT_LOCAL_STORE_ID,
                                                                            colDEBIT_SERVER_STORE_ID,
                                                                            colDEBIT_DEBIT_DATE,
                                                                            colDEBIT_ENTRY_ON,
                                                                            colDEBIT_DEBIT_AMOUNT,
            colDEBIT_COMMENT};
    public static final String      CREATE_DEBIT_TABLE                  = "CREATE TABLE " + tblDebits + "(" +
                                                                            colDEBIT_ID + " integer PRIMARY KEY, " +
                                                                            colDEBIT_PURCHASER_ID + "integer NOT NULL, " +
                                                                            colDEBIT_LOCAL_CATEGORY_ID + " integer NOT NULL, " +
                                                                            colDEBIT_SERVER_CATEGORY_ID + " integer NOT NULL, " +
                                                                            colDEBIT_LOCAL_STORE_ID + " integer NOT NULL, " +
                                                                            colDEBIT_SERVER_STORE_ID + " integer NOT NULL, " +
                                                                            colDEBIT_DEBIT_DATE + " text NOT NULL, " +
                                                                            colDEBIT_ENTRY_ON + " text NOT NULL, " +
                                                                            colDEBIT_DEBIT_AMOUNT + " decimal(10,2) NOT NULL," +
                                                                            colDEBIT_COMMENT + " text NULL);";
    public static final String      DROP_DEBIT_TABLE                    = "DROP TABLE IF EXISTS " + tblDebits;

    /* Shared Preference Keys - Matches Preferences Activity UI Object Names */
    public static final String      SERVER_ADDRESS_PREFERENCE           = "preferenceServerAddress";
    public static final String      SERVER_LOGIN_ADDRESS_PREFERENCE     = "preferenceServerLoginAddress";
    public static final String      SERVER_CATEGORY_ADDRESS_PREFERENCE  = "preferenceServerCategoryAddress";
    public static final String      SERVER_STORE_ADDRESS_PREFERENCE     = "preferenceServerStoreAddress";
    public static final String      SERVER_DEBIT_ADDRESS_PREFERENCE     = "preferenceServerDebitAddress";
    public static final String      USER_NAME_PREFERENCE                = "preferenceLoginUserName";
    public static final String      PASSWORD_PREFERENCE                 = "preferenceLoginPassword";

    /* Login Activity Intent Extras/Constants */
    public static final String      LOGIN_RESULT_USER_EXTRA             = "LOGIN_RESULT_USER";
    public static final String      LOGIN_RESULT_PASSWORD_EXTRA         = "LOGIN_RESULT_PASSWORD";

    /* Preference Activity Result Code */
    public static final int         PREFERENCE_RESULT_CODE              = 1000;

    /* Activity/Login Result Codes and Constants */
    public static final int         MIN_PASSWORD_LENGTH                 = 6;
    public static final int         LOGIN_RESULT_CODE                   = 2000;
    public static final int         LOGIN_CANCELED                      = 2001;
    public static final int         LOGIN_CONNECTION_ERROR              = 2002;
    public static final int         LOGIN_SUCCESSFUL                    = 2003;
    public static final int         LOGIN_FAILED                        = 2004;
    public static final int         LOGIN_RESULT_UNKNOWN                = 2005;
    public static final int         LOGIN_INFO_MISSING                  = 2006;

    /* Create/Update Category Result Codes */
    public static final int         CREATE_STORE_RESULT_CODE            = 3000;
    public static final int         UPDATE_STORE_RESULT_CODE            = 3001;

    /* Create/Update Store Result Codes */
    public static final int         CREATE_CATEGORY_RESULT_CODE         = 4000;
    public static final int         UPDATE_CATEGORY_RESULT_CODE         = 4001;

    /* Arraylist indexes for updating UI Controls */
    public static final int         CATEGORY_UI_CONTROL_INDEX           = 0;
    public static final int         STORE_UI_CONTROL_INDEX              = 0;

    /* Miscellaneous helper variables */
    public static final char        NEW_LINE                            = '\n';

    public static final String      LAST_CATEGORY_SYNC_PREFERENCE       = "lastCategorySync";
    public static final String      LAST_STORE_SYNC_PREFERENCE          = "lastStoreSync";
    public static final String      LAST_DEBIT_SYNC_PREFERENCE          = "lastDebitSync";
    public static final String      HTTP_RESPONSE_RESULT                = "result";
    public static final String      HTTP_RESPONSE_RESULT_SUCCESS        = "success";
    public static final String      HTTP_RESPONSE_TIMESTAMP             = "timestamp";

    public static final int         SYNC_OBJECT_TYPE_CATEGORY           = 0;
    public static final int         SYNC_OBJECT_TYPE_STORE              = 1;
    public static final int         SYNC_OBJECT_TYPE_DEBIT              = 2;
    public static final int         HTTP_TYPE_POST                      = 0;
    public static final String      HTTP_POST_JSON_TEXT                 = "post";
    public static final int         HTTP_TYPE_GET                       = 1;
    public static final String      HTTP_GET_JSON_TEXT                  = "get";
    public static final int         HTTP_TYPE_VERIFY                    = 2;
    public static final String      HTTP_VERIFY_JSON_TEXT               = "verify";
    public static final String      DATE_FORMAT_STRING                  = "yyyy-MM-dd HH:mm:ss";
    public static final int         HTTP_TIMEOUT                        = 5000;

    public static final String      CATEGORY_JSON_ARRAY                 = "categoryArray";
    public static final String      STORE_JSON_ARRAY                    = "storeArray";

}
