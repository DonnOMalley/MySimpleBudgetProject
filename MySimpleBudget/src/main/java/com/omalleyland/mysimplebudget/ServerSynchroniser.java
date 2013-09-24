package com.omalleyland.mysimplebudget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;
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
  private boolean                 postOnlyDebits      = false;
  private boolean                 showDebitPostMsg    = false;

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

  public void setPostOnlyDebits(boolean postOnlyDebits) {
    this.postOnlyDebits = postOnlyDebits;
  }

  public void setShowDebitPostMsg(boolean showDebitPostMsg) {
    this.showDebitPostMsg = showDebitPostMsg;
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
    String className;

    SynchroniseProcess(Context context) {
      this.context = context;
      this.className = getClass().toString();
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
      IObjectDBInterface dbInterface;
      IHttpObject httpObject;

      SyncObject syncObject;
      Boolean syncResultFlag = false;

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
          if(!postOnlyDebits) {
            //Synchronise Categories
            publishProgress("Processing Categories...");
            httpObject = new CategoryHTTPObject(this.context, serverAddress, userName, password);
            dbInterface = new CategoryDBInterface(this.context);
            syncResultFlag = synchronizeDataset(httpObject, dbInterface, userName, password);
            updateUIList.set(Common.CATEGORY_UI_CONTROL_INDEX, syncResultFlag);
            if(syncResultFlag) {
              syncResult = "Done";
            }
            else {
              syncResult = "Error";
            }
            publishProgress(syncResult + '\n');

            //Synchronise Stores
            publishProgress("Processing Stores...");
            httpObject = new StoreHTTPObject(this.context, serverAddress, userName, password);
            dbInterface = new StoreDBInterface(this.context);
            syncResultFlag = synchronizeDataset(httpObject, dbInterface, userName, password);
            updateUIList.set(Common.STORE_UI_CONTROL_INDEX, syncResultFlag);
            if(syncResultFlag) {
              syncResult = "Done";
            }
            else {
              syncResult = "Error";
            }
            publishProgress(syncResult + '\n');
          }

          //Synchronise Debits
          publishProgress("Processing Debits...");
          httpObject = new DebitHTTPObject(this.context, serverAddress, userName, password);
          dbInterface = new DebitDBInterface(this.context);

          //Update debit records with their corresponding server ids for Stores/Categories before synchronising
          ((DebitDBInterface)dbInterface).updateCategoryIDs();
          ((DebitDBInterface)dbInterface).updateStoreIDs();

          syncResultFlag = synchronizeDataset(httpObject, dbInterface, userName, password);
          if(syncResultFlag) {
            syncResult = "Done";
          }
          else {
            syncResult = "Error";
          }

          if(postOnlyDebits && showDebitPostMsg) {
            Toast.makeText(this.context, "Debits Have Been Posted", Toast.LENGTH_LONG).show();
          }
          else {
            publishProgress(syncResult);
          }
        }
        catch(Exception e) {
          Log.e(this.className, "Exception Synchronising :: ".concat(e.getMessage()));
          publishProgress("Error");
        }
      }
      return updateUIList;
    }

    @Override
    protected void onPreExecute() {
      Log.d(className, "PreExecute");

      if(!postOnlyDebits) {
        dialogMessageText = "";
        progressDialog = ProgressDialog.show(context, "Synchronising Configuration", dialogMessageText, true, true, new DialogInterface.OnCancelListener() {
          @Override
          public void onCancel(DialogInterface dialogInterface) {
            cancelBackgroundProcess();
          }
        });
      }
    }

    @Override
    protected void onPostExecute(ArrayList<Boolean> result) {
      Log.d(className, "PostExecute");
      if(!postOnlyDebits) {
        progressDialog.dismiss();
        bgProcessor.updateUIControls(result);
      }
    }

    @Override
    protected void onProgressUpdate(String... message) {
      if(!postOnlyDebits) {
        dialogMessageText = dialogMessageText.concat(message[0]);
        progressDialog.setMessage(dialogMessageText);
      }
    }

    @Override
    protected void onCancelled() {
      Log.d(className, "Synchronisation Canceled");
      if(!postOnlyDebits) {
        progressDialog.dismiss();
        ArrayList<Boolean> updateUIList = new ArrayList<Boolean>();
        updateUIList.add(false); //Update Categories
        updateUIList.add(false); //Update Stores
        bgProcessor.updateUIControls(updateUIList);
      }
    }

    private boolean postDataToServer(IHttpObject httpObject, IObjectDBInterface objectDBInterface, String userName, String password) {
      JSONObject          jsonObject;
      JSONObject          httpResponseJSON;
      String              jsonHTTPResponse    = "";
      List<Integer>       objectSyncStatuses  = null;
      List<SyncObject>    syncObjects         = null;
      boolean             result              = false;

      //Execute Posting of new data
      objectSyncStatuses = new ArrayList<Integer>();
      objectSyncStatuses.add(Common.SYNC_STATUS_NEW);
      objectSyncStatuses.add(Common.SYNC_STATUS_UPDATED);
      Log.d(this.className, "Building Post JSON");
      jsonObject = objectDBInterface.buildJSON(Common.HTTP_TYPE_POST, objectSyncStatuses, userName, password);
      if(jsonObject != null) {
        Log.d(this.className, "Post JSON Built :: ".concat(jsonObject.toString()));
        jsonHTTPResponse = httpObject.getHTTP(jsonObject.toString());
        if(jsonHTTPResponse.length() > 0) {
          try {
            //Web server will insert 'New' and Update 'Updated'
            httpResponseJSON = new JSONObject(jsonHTTPResponse);
            result = httpResponseJSON.get(Common.HTTP_RESPONSE_RESULT).equals(Common.HTTP_RESPONSE_RESULT_SUCCESS);
            if(result) {
              //process records as 'PENDING_VERIFY'
              syncObjects = objectDBInterface.parseJSONList(httpResponseJSON);
              objectDBInterface.updateDatabaseObjectsSyncStatus(syncObjects, Common.SYNC_STATUS_PENDING_VERIFY);
            }
          }
          catch (Exception e) {
            Log.e(this.className, "Exception parsing JSON response :: ".concat(e.getMessage()));
          }
        }
      }
      else {
          Log.d(this.className, "Null JSON - Nothing to Post.");
          result = true; //Nothing to post is not an error condition so set result to 'true'
      }
      return result;
    }

    private boolean verifyLocalDataWithServer(IHttpObject httpObject, IObjectDBInterface objectDBInterface, String userName, String password) {
      List<Integer>       objectSyncStatuses  = null;
      JSONObject          jsonObject;
      JSONObject          httpResponseJSON;
      String              jsonHTTPResponse    = "";
      List<SyncObject>    syncObjects         = null;
      boolean             result              = false;

      //If Data posted with a Success Result, update all to Pending Verify
      //Execute Verify for Local Data from Server
      //Get Request based on SyncObject Names
      objectSyncStatuses = new ArrayList<Integer>();
      objectSyncStatuses.add(Common.SYNC_STATUS_PENDING_VERIFY);
      jsonObject = objectDBInterface.buildJSON(Common.HTTP_TYPE_VERIFY, objectSyncStatuses, userName, password);
      if(jsonObject != null) {
        jsonHTTPResponse = httpObject.getHTTP(jsonObject.toString());
        if(jsonHTTPResponse.length() > 0) {
          try {
            //Web server will insert 'New' and Update 'Updated'
            httpResponseJSON = new JSONObject(jsonHTTPResponse);
            result = httpResponseJSON.get(Common.HTTP_RESPONSE_RESULT).equals(Common.HTTP_RESPONSE_RESULT_SUCCESS);
            if(result) {
              //process records as 'Synchronized'
              syncObjects = objectDBInterface.parseJSONList(httpResponseJSON);
              objectDBInterface.updateDatabaseObjectsSyncStatus(syncObjects, Common.SYNC_STATUS_SYNCHRONIZED);
            }
          }
          catch (Exception e) {
            Log.e(this.className, "Exception parsing JSON response :: ".concat(e.getMessage()));
          }
        }
        else {
          //Reset Statuses to 'New' to get Inserted??
        }
      }
      else {
        Log.d(this.className, "Nothing to Verify");
        result = true; //Nothing to verify so return true
      }
      return result;
    }

    private boolean getDataFromServer(IHttpObject httpObject, IObjectDBInterface objectDBInterface, String userName, String password) {
      JSONObject          jsonObject          = null;
      JSONObject          httpResponseJSON;
      String              jsonHTTPResponse    = "";
      List<Integer>       objectSyncStatuses  = null;
      List<SyncObject>    syncObjects         = null;
      boolean             result              = false;

      //Execute Get (New from Server)
      jsonObject = objectDBInterface.buildJSON(Common.HTTP_TYPE_GET, objectSyncStatuses, userName, password);
      if(jsonObject != null) {
        jsonHTTPResponse = httpObject.getHTTP(jsonObject.toString());
        if(jsonHTTPResponse.length() > 0) {
          try {
            httpResponseJSON = new JSONObject(jsonHTTPResponse);
            result = httpResponseJSON.get(Common.HTTP_RESPONSE_RESULT).equals(Common.HTTP_RESPONSE_RESULT_SUCCESS);
            if(result) {
              //process records as 'PENDING_VERIFY'
              syncObjects = objectDBInterface.parseJSONList(httpResponseJSON);
              objectDBInterface.updateDatabaseObjectsSyncStatus(syncObjects, Common.SYNC_STATUS_SYNCHRONIZED);
            }
            else {
              result = false;
            }
          }
          catch (Exception e) {
            Log.e(this.className, "Exception parsing JSON response :: ".concat(e.getMessage()));
          }
        }
      }
      else {
        Log.d(this.className, "Error Building Get JSON");
      }
      return  result;
    }

    private boolean synchronizeDataset(IHttpObject httpObject, IObjectDBInterface objectDBInterface, String userName, String password) {
      boolean             result              = false;

      //Execute Posting of new data
      Log.d(this.className, "Posting Data To Server");
      result = postDataToServer(httpObject, objectDBInterface, userName, password);

      if(!objectDBInterface.getClass().toString().equals(DebitDBInterface.class.toString())) {
        Log.d(this.className, "Verifying Server Data");
        result = verifyLocalDataWithServer(httpObject, objectDBInterface, userName, password);

        Log.d(this.className, "Getting Data From Server");
        //Execute Get from Server
        result = getDataFromServer(httpObject, objectDBInterface, userName, password);
      }

      //Only write sync timestamp if data fetched from server succeeded.
      if(result) {
        httpObject.setSyncTimestamp();
      }

      Log.d(this.className, "Data Synchronised");
      return result;
    }
  }
}
