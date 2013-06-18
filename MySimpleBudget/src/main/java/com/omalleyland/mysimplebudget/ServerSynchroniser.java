package com.omalleyland.mysimplebudget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by omal310371 on 6/9/13.
 */
public class ServerSynchroniser {

    private IBackgroundProcessor    bgProcessor         = null;
    private Context                 context             = null;
    private ProgressDialog          progressDialog      = null;
    private SynchroniseProcess      synchroniseProcess  = null;
    private String                  className           = "ServerSynchroniser";

    public ServerSynchroniser() {
        Log.v(className, "Empty Constructor");
    }

    public ServerSynchroniser(IBackgroundProcessor bgProcessor) {
        Log.v(className, "ServerSynchroniser(IBackgroundProcessor) Constructor");
        this.bgProcessor = bgProcessor;
    }

    public ServerSynchroniser(IBackgroundProcessor bgProcessor, Context context) {
        Log.v(className, "ServerSynchroniser(IBackgroundProcessor, Context) Constructor");
        this.bgProcessor = bgProcessor;
        this.context = context;
    }

    /* Setter Routines */
    public void setContext(Context context) {
        this.context = context;
    }

    public void setBackgroundProcessor(IBackgroundProcessor bgProcessor) {
        this.bgProcessor = bgProcessor;
    }

    /* Calls Async Task for performing synchronisation with the server */
    public void synchroniseData() {
        Log.d(className, "Synchronising Data");
        synchroniseProcess = new SynchroniseProcess(this.context);
        synchroniseProcess.execute((Void) null);
    }

    public void cancelBackgroundProcess() {
        Log.d(className, "Canceling Background Task : synchroniseProcess Is Null = " + Boolean.toString(synchroniseProcess == null));
        if(synchroniseProcess != null) {
            Log.d(className, "synchroniseProcess.cancel(true)");
            synchroniseProcess.cancel(false);
        }
    }

    public class SynchroniseProcess extends AsyncTask<Void, String, ArrayList<Boolean>> {

        String dialogMessageText = "";
        Context context;

        SynchroniseProcess(Context context) {
            this.context = context;
        }

        @Override
        protected ArrayList<Boolean> doInBackground(Void... voids) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String userName;
            String password;
            String dateString;
            Date syncDate;
            List<Category> categoryList;
            List<Store> storeList;
            DefaultHttpClient httpClient;
            HttpPost httpPost;
            HttpParams httpParams;
            SimpleDateFormat simpleDateFormat;
            String serverAddress;
            String syncPage;
            String syncResult;
            String httpResponse = "";
            JSONObject httpResponseJSON;

            SyncObject syncObject;
            Boolean continueProcessing = false;

            //Initialize Date Format to match MySQL
            //Set Timezone to ensure dates against the server only evaluate with GMT
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            ArrayList<Boolean> updateUIList = new ArrayList<Boolean>();
            updateUIList.add(Common.CATEGORY_UI_CONTROL_INDEX, false);  //initialize Category failure
            updateUIList.add(Common.STORE_UI_CONTROL_INDEX, false);     //initialize store failure

            userName = prefs.getString(Common.USER_NAME_PREFERENCE, "");
            password = prefs.getString(Common.PASSWORD_PREFERENCE, "");
            serverAddress   = prefs.getString(Common.SERVER_ADDRESS_PREFERENCE, "");
            if(!serverAddress.endsWith("/")) {
                serverAddress += "/";
            }

            if((serverAddress.length() > 0) && (userName.length() > 0) && (password.length() > 0)) {

                try {

                    //Get date of last sync
                    dateString = prefs.getString(Common.LAST_CATEGORY_SYNC_PREFERENCE, "-1");
                    Log.d(className, "Last Category Sync Timestamp :: ".concat(dateString));
                    if(dateString.equals("-1")) {
                        dateString = simpleDateFormat.format(new Date(0));
                    }

                    //Get Category Sync Page
                    syncPage = prefs.getString(Common.SERVER_CATEGORY_ADDRESS_PREFERENCE, "");
                    syncPage = serverAddress.concat(syncPage);

                    //Synchronise Categories
                    continueProcessing = synchroniseCategories(dateString, userName, password, syncPage);
                    updateUIList.set(Common.CATEGORY_UI_CONTROL_INDEX, continueProcessing);

                    if(continueProcessing) {
                        //Process stores
                        //Get store's last sync timestamp and sync page
                        continueProcessing = true; //synchroniseStores(dateString, userName, password, syncPage)
                    }

                    //Synchronise Stores
                    publishProgress("Processing Stores...");

                    //Get date of last sync
                    dateString = prefs.getString(Common.LAST_STORE_SYNC_PREFERENCE, "-1");
                    Log.d(className, "Last Store Sync Timestamp :: ".concat(dateString));
                    if(dateString.equals("-1")) {
                        dateString = simpleDateFormat.format(new Date(0));
                    }

                    //Get Store Sync Page
                    syncPage = prefs.getString(Common.SERVER_STORE_ADDRESS_PREFERENCE, "");
                    if(syncPage.length() > 0) {
                        syncPage = serverAddress.concat(syncPage);

                        //Initialize HTTP Objects
                        httpClient = new DefaultHttpClient();

                        httpParams = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                        HttpConnectionParams.setSoTimeout(httpParams, 5000);

                        StoreDBInterface storeDBInterface = new StoreDBInterface(context);
                        storeList = storeDBInterface.getStoreUpdates();
                        if(storeList.size() > 0) {
                            JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray = new JSONArray();
                            jsonObject.put("type", "post");
                            jsonObject.put("user", userName);
                            for(int i = 0; i < storeList.size(); i++) {
                                Store store = storeList.get(i);
                                JSONObject storeJSON = new JSONObject(store.getMap());
                                jsonArray.put(storeJSON);
                            }
                            jsonObject.put("storeArray", jsonArray);
                            Log.d(className, jsonObject.toString());

                            httpPost = new HttpPost(syncPage);
                            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                            nameValuePairs.add(new BasicNameValuePair("username", userName));
                            nameValuePairs.add(new BasicNameValuePair("password", password));
                            nameValuePairs.add(new BasicNameValuePair("json", jsonObject.toString()));
                            Log.d(className, "Name Value Pairs built : ".concat(Integer.toString(nameValuePairs.size())));
                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                            Log.d(className, "Executing HTTP Request To :: ".concat(syncPage));
                            HttpEntity httpEntity = httpClient.execute(httpPost).getEntity();
                            httpResponse = EntityUtils.toString(httpEntity);
                            Log.d(className, "http Post Response = ".concat(httpResponse.toString()));
                        }

                        /*
                            Build HTTP Get Request to get list of updated Categories since last sync
                        */

                        //Build json based on last sync date time (or beginning of time if never sync'd)
                        JSONObject storeRequestJSON = new JSONObject();
                        storeRequestJSON.put("type", "get");
                        storeRequestJSON.put("lastUpdated", dateString);
                        Log.d(className, "storeRequestJSON = ".concat(storeRequestJSON.toString()));
                        httpPost = new HttpPost(syncPage);
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                        nameValuePairs.add(new BasicNameValuePair("username", userName));
                        nameValuePairs.add(new BasicNameValuePair("password", password));
                        nameValuePairs.add(new BasicNameValuePair("json", storeRequestJSON.toString()));
                        Log.d(className, "Name Value Pairs built : ".concat(Integer.toString(nameValuePairs.size())));
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        Log.d(className, "Executing HTTP Request To :: ".concat(syncPage));
                        HttpEntity httpEntity = httpClient.execute(httpPost).getEntity();
                        httpResponse = EntityUtils.toString(httpEntity);
                        if(httpResponse.length() > 0) {
                            Log.d(className, "HTTP Response Received :: ".concat(httpResponse.toString()));

                            //Parse JSON at this point.
                            JSONObject jsonResponse = new JSONObject(httpResponse.toString());
                            Log.d(className, "JSON Response = ".concat(jsonResponse.toString()));
                            String resultString = jsonResponse.getString("result");
                            Log.d(className, "Process Stores Get Result = ".concat(resultString));
                            JSONArray jsonArrayResponse = jsonResponse.getJSONArray("storeArray");
                            List<Store> storesToUpdate = new ArrayList<Store>();
                            for(int i=0; i< jsonArrayResponse.length(); i++) {
                                Store jsonStore = new Store();
                                jsonStore.JSONToObject(jsonArrayResponse.getJSONObject(i));
                                Log.d(className, "Store From JSON = ".concat(jsonStore.toString()));
                                //Insert category into database (or list in preparation for database
                                storesToUpdate.add(jsonStore);
                            }
                            storeDBInterface.updateStoreRecords(storesToUpdate, Common.SYNC_STATUS_SYNCHRONIZED);
                            updateUIList.set(Common.STORE_UI_CONTROL_INDEX, true);
                            Log.d(className, "Store Spinner Update List Set to True");
                        }
                        else {
                            Log.d(className, "Empty Response");
                        }
                        syncResult = "Done";
                        String updatedTimestamp = simpleDateFormat.format(new Date());
                        Log.d(className, "Updating Sync Timestamp :: ".concat(updatedTimestamp));
                        prefs.edit().putString(Common.LAST_STORE_SYNC_PREFERENCE, updatedTimestamp).commit();
                    }
                    else {
                        syncResult = "ERROR";
                    }

                    /////////////////////////////////

                    publishProgress(syncResult + '\n' + "Processing Debits...");
                    Thread.sleep(2000);

                    publishProgress("Done");
                }
                catch(Exception e) {
                    publishProgress("Error");
                }
            }
            return updateUIList;
        }

        @Override
        protected void onPreExecute() {
            Log.d(className, "PreExecute");
//            progressDlg = new ProgressDlg();
//            progressDlg.show(context, bgProcessor, Common.CONFIGURATION_SYNCHRONISATION, "Synchronising Configuration", "");

            dialogMessageText = "";
            progressDialog = ProgressDialog.show(context, "Synchronising Configuration", dialogMessageText, true, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    cancelBackgroundProcess();
                }
            });
        }

        @Override
        protected void onPostExecute(ArrayList<Boolean> result) {
            Log.d(className, "PostExecute");
            progressDialog.dismiss();
            bgProcessor.updateUIControls(result);
        }

        @Override
        protected void onProgressUpdate(String... message) {
            dialogMessageText = dialogMessageText + message[0];
            progressDialog.setMessage(dialogMessageText);
        }

        @Override
        protected void onCancelled() {
            Log.d(className, "Synchronisation Canceled");
            progressDialog.dismiss();
            ArrayList<Boolean> updateUIList = new ArrayList<Boolean>();
            updateUIList.add(false); //Update Categories
            updateUIList.add(false); //Update Stores
            bgProcessor.updateUIControls(updateUIList);
        }

        private Boolean synchroniseCategories(String lastSync, String userName, String password, String syncPage){
            //Return value, Progress Update Text and update sync timestamp
            Boolean             result              = false;
            String              syncResult          = "ERROR";
            SimpleDateFormat    simpleDateFormat    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SharedPreferences   prefs               = PreferenceManager.getDefaultSharedPreferences(context);

            //Objects for sending/receiving Http Request/Response
            DefaultHttpClient   httpClient;
            BasicHttpParams     httpParams;
            HttpPost            httpPost;
            HttpEntity          httpEntity;
            String              httpResponse;
            List<NameValuePair> nameValuePairs;

            //Http Request/Response Data Objects
            JSONArray           jsonArray;
            JSONObject          jsonPostObject;
            JSONObject          jsonResponse;

            publishProgress("Processing Categories...");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

            if(syncPage.length() > 0) {
                try {
                    //Initialize HTTP Objects
                    httpClient = new DefaultHttpClient();

                    httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                    HttpConnectionParams.setSoTimeout(httpParams, 5000);

                    CategoryDBInterface categoryDBInterface = new CategoryDBInterface(context);
                    List<SyncObject> syncObjects;
                    syncObjects = categoryDBInterface.getUpdatedDatabaseObjects();
                    //categoryList = categoryDBInterface.getCategoryUpdates();
                    if(syncObjects.size() > 0) {
                        jsonPostObject = new JSONObject();
                        jsonArray = new JSONArray();

                        try {
                            jsonPostObject.put("type", "post");
                            jsonPostObject.put("user", userName);
                            for(int i = 0; i < syncObjects.size(); i++) {
                                Category category = (Category)syncObjects.get(i);
                                JSONObject categoryJSON = new JSONObject(category.getMap());
                                jsonArray.put(categoryJSON);
                            }
                            jsonPostObject.put("categoryArray", jsonArray);
                            Log.d(className, jsonPostObject.toString());

                            httpPost = new HttpPost(syncPage);
                            nameValuePairs = new ArrayList<NameValuePair>(3);
                            nameValuePairs.add(new BasicNameValuePair("username", userName));
                            nameValuePairs.add(new BasicNameValuePair("password", password));
                            nameValuePairs.add(new BasicNameValuePair("json", jsonPostObject.toString()));
                            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                            Log.d(className, "Executing HTTP Request To :: ".concat(syncPage));
                            httpEntity = httpClient.execute(httpPost).getEntity();
                            httpResponse = EntityUtils.toString(httpEntity);
                            Log.d(className, "HTTP Post Response = ".concat(httpResponse.toString()));
                        }
                        catch (Exception e) {
                            throw new Exception(className + "Exception Posting Category Updates :: " + e.getMessage());
                        }
                    }

                    /*
                        Build HTTP Get Request to get list of updated Categories since last sync
                    */
                    //Build json based on last sync date time (or beginning of time if never sync'd)
                    jsonPostObject = new JSONObject();
                    try {
                        jsonPostObject.put("type", "get");
                        jsonPostObject.put("lastUpdated", lastSync);
                        Log.d(className, "categoryRequestJSON = ".concat(jsonPostObject.toString()));

                        httpPost = new HttpPost(syncPage);
                        nameValuePairs = new ArrayList<NameValuePair>(3);
                        nameValuePairs.add(new BasicNameValuePair("username", userName));
                        nameValuePairs.add(new BasicNameValuePair("password", password));
                        nameValuePairs.add(new BasicNameValuePair("json", jsonPostObject.toString()));
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                        Log.d(className, "Executing HTTP GET Request To :: ".concat(syncPage));
                        httpResponse = EntityUtils.toString(httpClient.execute(httpPost).getEntity());
                        Log.d(className, "HTTP Response Received :: ".concat(httpResponse.toString()));

                        if(httpResponse.length() > 0) {
                            //Parse JSON at this point.
                            jsonResponse = new JSONObject(httpResponse.toString());
                            Log.d(className, "HTTP JSON String = ".concat(jsonResponse.toString()));

                            httpResponse = jsonResponse.getString("result");
                            Log.d(className, "HTTP GET Result = ".concat(httpResponse));

                            jsonArray = jsonResponse.getJSONArray("categoryArray");
                            List<SyncObject> syncObjectsToUpdate = new ArrayList<SyncObject>();
                            for(int i=0; i< jsonArray.length(); i++) {
                                Category jsonCategory = new Category();
                                jsonCategory.JSONToObject(jsonArray.getJSONObject(i));
                                Log.d(className, "Category From JSON = ".concat(jsonCategory.toString()));
                                syncObjectsToUpdate.add(jsonCategory);
                            }
                            categoryDBInterface.updateDatabaseObjectsSyncStatus(syncObjectsToUpdate, Common.SYNC_STATUS_SYNCHRONIZED);
                        }
                        else {
                            Log.d(className, "Empty Response");
                        }
                        String updatedTimestamp = simpleDateFormat.format(new Date());
                        Log.d(className, "Updating Sync Timestamp :: ".concat(updatedTimestamp));
                        prefs.edit().putString(Common.LAST_CATEGORY_SYNC_PREFERENCE, updatedTimestamp).commit();
                        syncResult = "Done";
                    }
                    catch (Exception e) {
                        throw new Exception(className + "Exception Reading Categories from Server :: " + e.getMessage());
                    }
                }
                catch (Exception e) {
                    syncResult = "ERROR";
                    Log.e(className, "Exception Processing Category Sync :: ".concat(e.getMessage()));
                }
            }
            publishProgress(syncResult + '\n'); //Post result to dialog and append a new line character
            return result;
        }
    }
}
