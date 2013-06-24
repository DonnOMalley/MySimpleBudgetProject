package com.omalleyland.mysimplebudget;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by omal310371 on 6/20/13.
 */
public class Debit extends SyncObject {

    private int userID;
    private int localCategoryID;
    private int categoryID;
    private int localStoreID;
    private int storeID;
    private String dateString;
    private double amount;
    private String comment;
    private String entryOnString;


    public Debit() {
        super(Common.SYNC_OBJECT_TYPE_DEBIT);
        this.className      = getClass().toString();
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getLocalCategoryID() {
        return localCategoryID;
    }

    public void setLocalCategoryID(int localCategoryID) {
        this.localCategoryID = localCategoryID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public int getLocalStoreID() {
        return localStoreID;
    }

    public void setLocalStoreID(int localStoreID) {
        this.localStoreID = localStoreID;
    }

    public int getStoreID() {
        return storeID;
    }

    public void setStoreID(int storeID) {
        this.storeID = storeID;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getEntryOnString() {
        return entryOnString;
    }

    public void setEntryOnString(String entryOnString) {
        this.entryOnString = entryOnString;
    }

    @Override
    protected void JSONToObject(JSONObject jsonObject) {
        try {
            this.id                 = jsonObject.getInt(Common.colDEBIT_ID);
            this.localCategoryID    = jsonObject.getInt(Common.colDEBIT_LOCAL_CATEGORY_ID);
            this.categoryID         = jsonObject.getInt(Common.colDEBIT_SERVER_CATEGORY_ID);
            this.localStoreID       = jsonObject.getInt(Common.colDEBIT_LOCAL_STORE_ID);
            this.storeID            = jsonObject.getInt(Common.colDEBIT_SERVER_STORE_ID);
            this.dateString         = jsonObject.getString(Common.colDEBIT_DEBIT_DATE);
            this.amount             = Double.parseDouble(jsonObject.getString(Common.colDEBIT_DEBIT_AMOUNT));
            this.comment            = jsonObject.getString(Common.colDEBIT_COMMENT);
            this.entryOnString      = jsonObject.getString(Common.colDEBIT_ENTRY_ON);
        }
        catch (Exception e) {
            //Do nothing for now
        }
    }

    public Map<String, String> getMap() {
        Map<String, String> debitMap = new HashMap<String, String>();
        debitMap.put(Common.colDEBIT_PURCHASER_ID, Integer.toString(this.userID));
        debitMap.put(Common.colDEBIT_ID, Integer.toString(this.id));
        debitMap.put(Common.colDEBIT_LOCAL_CATEGORY_ID, Integer.toString(this.localCategoryID));
        debitMap.put(Common.colDEBIT_SERVER_CATEGORY_ID, Integer.toString(this.categoryID));
        debitMap.put(Common.colDEBIT_SERVER_STORE_ID, Integer.toString(this.storeID));
        debitMap.put(Common.colDEBIT_LOCAL_STORE_ID, Integer.toString(this.localStoreID));
        debitMap.put(Common.colDEBIT_DEBIT_DATE, this.dateString);
        debitMap.put(Common.colDEBIT_DEBIT_AMOUNT, Double.toString(this.amount));
        debitMap.put(Common.colDEBIT_COMMENT, this.comment);
        debitMap.put(Common.colDEBIT_ENTRY_ON, this.entryOnString);

        return debitMap;
    }
}
