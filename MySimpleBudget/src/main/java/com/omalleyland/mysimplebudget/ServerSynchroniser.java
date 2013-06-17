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

            //Initialize Date Format to match MySQL
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

                    //Get Category Sync Page
                    syncPage = prefs.getString(Common.SERVER_CATEGORY_ADDRESS_PREFERENCE, "");
                    publishProgress("Processing Categories...");
                    if(syncPage.length() > 0) {
                        syncPage = serverAddress.concat(syncPage);

                        //Initialize HTTP Objects
                        httpClient = new DefaultHttpClient();

                        httpParams = new BasicHttpParams();
                        HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                        HttpConnectionParams.setSoTimeout(httpParams, 5000);

                        CategoryDBInterface categoryDBInterface = new CategoryDBInterface(context);
//                        categoryList = categoryDBInterface.getCategoryUpdates();
//                        if(categoryList.size() > 0) {
//                            JSONObject jsonObject = new JSONObject();
//                            JSONArray jsonArray = new JSONArray();
//                            for(int i = 0; i < categoryList.size(); i++) {
//                                Category category = categoryList.get(i);
//                                JSONObject categoryJSON = new JSONObject(category.getMap());
//                                jsonArray.put(categoryJSON);
//                            }
//                            ////////////////////////////////////////////////////////////////////////////
//                            // jsonObject.put("post", jsonArray);
//
//                            StringEntity stringEntity = new StringEntity(jsonObject.toString());
//
//                            /////////
//                            httpPost = new HttpPost(serverAddress.concat(syncPage));
//                            httpPost.setEntity(stringEntity);
//                            httpPost.setHeader("Accept", "application/json");
//                            httpPost.setHeader("content-type", "application/json");
//                            httpResponse = EntityUtils.toString(httpClient.execute(httpPost).getEntity());
//                            Toast.makeText(context, httpResponse, Toast.LENGTH_LONG).show();
//                        }

//                        if(httpResponse.length() > 0) {
//                            httpResponseJSON = new JSONObject(httpResponse);
//
//                            //If http response = 'success'
//                            if(httpResponseJSON.getString(Common.HTTP_RESPONSE_RESULT).equals(Common.HTTP_RESPONSE_RESULT_SUCCESS)) {
//
//                                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//                                Date syncedDate = simpleDateFormat.parse(httpResponseJSON.getString(Common.HTTP_RESPONSE_TIMESTAMP));
//                                //For each category, update category in local SQLite DB
//                                categoryDBInterface.updateCategoryRecords(categoryList, Common.SYNC_STATUS_PENDING_VERIFY);
//                            }
//                        }

                        /*
                            If Http Post was sent and a result was returned, get data from server
                        */
                        //Set Timezone to ensure dates against the server only evaluate with GMT
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

                        //Get date of last sync
                        dateString = prefs.getString(Common.LAST_CATEGORY_SYNC_PREFERENCE, "-1");
                        Log.d(className, "Last Category Sync Timestamp :: ".concat(dateString));
                        if(dateString.equals("-1")) {
                            dateString = simpleDateFormat.format(new Date(0));
                        }

                        //Build json based on last sync date time (or beginning of time if never sync'd)
                        JSONObject categoryRequestJSON = new JSONObject();
                        categoryRequestJSON.put("type", "get");
                        categoryRequestJSON.put("lastUpdated", dateString);
                        Log.d(className, "categoryRequestJSON = ".concat(categoryRequestJSON.toString()));
                        httpPost = new HttpPost(syncPage);
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);
                        nameValuePairs.add(new BasicNameValuePair("username", userName));
                        nameValuePairs.add(new BasicNameValuePair("password", password));
                        nameValuePairs.add(new BasicNameValuePair("json", categoryRequestJSON.toString()));
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
                            Log.d(className, "Process Categories Get Result = ".concat(resultString));
                            JSONArray jsonArrayResponse = jsonResponse.getJSONArray("categoryArray");
                            List<Category> categoriesToUpdate = new ArrayList<Category>();
                            for(int i=0; i< jsonArrayResponse.length(); i++) {
                                Category jsonCategory = new Category();
                                jsonCategory.JSONToObject(jsonArrayResponse.getJSONObject(i));
                                Log.d(className, "Category From JSON = ".concat(jsonCategory.toString()));
                                //Insert category into database (or list in preparation for database
                                categoriesToUpdate.add(jsonCategory);
                            }
                            categoryDBInterface.updateCategoryRecords(categoriesToUpdate, Common.SYNC_STATUS_SYNCHRONIZED);
                            updateUIList.set(Common.CATEGORY_UI_CONTROL_INDEX, true);
                            Log.d(className, "Category Spinner Update List Set to True");
                        }
                        else {
                            Log.d(className, "Empty Response");
                        }
                        syncResult = "Done";
                        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                        String updatedTimestamp = simpleDateFormat.format(new Date());
                        Log.d(className, "Updating Sync Timestamp :: ".concat(updatedTimestamp));
                        prefs.edit().putString(Common.LAST_CATEGORY_SYNC_PREFERENCE, updatedTimestamp).commit();
                    }
                    else {
                        Thread.sleep(2000);
                        syncResult = "ERROR";
                    }

                    Thread.sleep(2000);
                    publishProgress(syncResult + '\n' + "Processing Stores...");
                    syncResult = "";

                    StoreDBInterface storeDBInterface = new StoreDBInterface(context);
                    Thread.sleep(2000);
                    storeList = storeDBInterface.getStoreUpdates();


                    //Create JSON To bulk insert any 'new' stores - Build 'Values' into json so PHP doesn't have to do any heavy lifting.
                    //Create JSON To send all 'Updates' to the server -- Can't bulk update but build good data structure for php to process.

                    //Query server for all changes since last store sync (store date/time in preferences)
                    //  -Post Changes to local SQLite Database ('pending verify' => synchronized / <Not existing> => synchronized

                    //Set Store sync preference date time

                    updateUIList.set(Common.STORE_UI_CONTROL_INDEX, true);
                    publishProgress("Done" + '\n' + "Processing Debits...");

                    Thread.sleep(2000);

                    //Create JSON To bulk insert any 'new' debits - Build 'Values' into json so PHP doesn't have to do any heavy lifting.

                    //Query? server to confirm debits occurred? by user where entry_on > last debit sych time

                    // Update local SQLite Database ('new' => synchronized);

                    //set debit sync preference data time.
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
    }
}
