package com.omalleyland.mysimplebudget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
        synchroniseProcess = new SynchroniseProcess();
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

        @Override
        protected ArrayList<Boolean> doInBackground(Void... voids) {
            CategoryDBInterface categoryDBInterface = new CategoryDBInterface(context);
            StoreDBInterface storeDBInterface = new StoreDBInterface(context);

            List<Category> categoryList;
            List<Store> storeList;

            ArrayList<Boolean> updateUIList = new ArrayList<Boolean>();
            updateUIList.add(Common.CATEGORY_UI_CONTROL_INDEX, false);  //initialize Category failure
            updateUIList.add(Common.STORE_UI_CONTROL_INDEX, false);     //initialize store failure

            try {

                publishProgress("Processing Categories...");
                Thread.sleep(2000);
                categoryList = categoryDBInterface.getCategoryUpdates();

                JSONArray jsonArray = new JSONArray(categoryList);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("USERNAME", jsonArray);

                StringEntity stringEntity = new StringEntity(jsonObject.toString());

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost("http://server address");

                httpPost.setEntity(stringEntity);
                httpPost.setHeader("Accept", "application/json");
                httpPost.setHeader("content-type", "application/json");
                String responseString = EntityUtils.toString(httpClient.execute(httpPost).getEntity());

                //Create JSON To bulk insert any 'new' categories - Build 'Values' into json so PHP doesn't have to do any heavy lifting.
                //Create JSON To send all 'Updates' to the server -- Can't bulk update but build good data structure for php to process.

                //Query server for all changes since last category sync (store date/time in preferences)
                //  -Post Changes to local SQLite Database ('pending verify' => synchronized / <Not existing> => synchronized

                //Set Category sync preference date time\

                updateUIList.set(Common.CATEGORY_UI_CONTROL_INDEX, true);

                publishProgress("Done" + '\n' + "Processing Stores...");
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
