package com.omalleyland.mysimplebudget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omal310371 on 6/6/13.
 */
public class ValidateLogin {
    //Object References for managing Application and Asynchronous Login
    private UserLoginTask           mAuthTask               = null;
    private IBackgroundProcessor    bgProcessor             = null;
    private Context                 ctx                     = null;
    //private ProgressDlg             progressDlg             = null;
    private ProgressDialog          progressDlg             = null;

    //For managing login information and result.
    private String  userName            = "";
    private String  password            = "";
    private String  validationServer    = "";
    private int     resultCode          = Common.LOGIN_RESULT_UNKNOWN;

    private String  className           = "ValidateLogin";

    /* CONSTRUCTORS */
    public ValidateLogin() {
        Log.v(className, "Empty Constructor");

        this.bgProcessor        = null;
        this.ctx                = null;
        this.validationServer   = "";
        this.userName           = "";
        this.password           = "";
    }

    public ValidateLogin(String userName, String password) {
        Log.v(className, "ValidateLogin(userName, password) Constructor");
        this.bgProcessor        = null;
        this.ctx                = null;
        this.validationServer   = "";
        this.userName           = userName;
        this.password           = password;
    }

    public ValidateLogin(String validationServer, String userName, String password) {
        Log.v(className, "ValidateLogin(validationServer, userName, password) Constructor");
        this.bgProcessor        = null;
        this.ctx                = null;
        this.validationServer   = validationServer;
        this.userName           = userName;
        this.password           = password;
    }

    public ValidateLogin(IBackgroundProcessor bgProcessor, Context ctx, String validationServer, String userName, String password) {
        Log.v(className, "ValidateLogin(IValidateLogin, Context, validationServer, userName, password) Constructor");
        this.bgProcessor        = bgProcessor;
        this.ctx                = ctx;
        this.validationServer   = validationServer;
        this.userName           = userName;
        this.password           = password;
    }

    /* Setters for local private class variables */
    public void setBackgroundProcessor(IBackgroundProcessor bgProcessor) {
        this.bgProcessor = bgProcessor;
    }

    public void setContext(Context ctx) {
        this.ctx = ctx;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setValidationServer(String validationServer) {
        this.validationServer = validationServer;
    }

    /* Performs Login Validation against Validation Web Server */
    public void executeValidation() {
        Log.v(className, "executeValidation");
        if(ctx != null) {
            if(mAuthTask == null) {
                //Only attempt validation if server, username and password are assigned
                if(this.userName.length() > 0 && this.password.length() > 0 && this.validationServer.length() > 0) {
                    Log.d(className, "Validating Login against Server : " + validationServer);
                    mAuthTask = new UserLoginTask();
                    mAuthTask.execute((Void) null);
                }
                else {
                    Log.d(className, "Missing Server Information :: UserName = " + this.userName + " :: Password.length() = " + Integer.toString(this.password.length()) + " :: Validation Server = " + this.validationServer);
                    resultCode = Common.LOGIN_INFO_MISSING;
                    bgProcessor.processLoginResult(resultCode, "");
                }
            }
            else {
                Log.d(className, "Authentication Still in Progress");
            }
        }
    }

    public void finishValidation(String passwordHash) {
        Log.d(className, "Returning Login information back to calling class");
        progressDlg.dismiss();
        mAuthTask = null;
        bgProcessor.processLoginResult(resultCode, passwordHash);
    }

    public void cancelBackgroundProcess() {
        Log.d(className, "Canceling Background Task : mAuthTask Is Null = " + Boolean.toString(mAuthTask == null));
        if(mAuthTask != null) {
            Log.d(className, "mAuthTask.cancel(true)");
            try {
                mAuthTask.cancel(false);
                Log.d(className, "mAuthTask Canceled");
            }
            catch (Exception e) {
                Log.e(className, "Exception Canceling mAuthTask :: " + e.getMessage());
            }
        }
    }

    /* An asynchronous login task to authenticate the user. */
    public class UserLoginTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            Log.v(className, "Starting Background Login");
            HttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, Common.HTTP_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpParams, Common.HTTP_TIMEOUT);

            HttpPost httppost = new HttpPost(validationServer);
            String responseString = "";

            try {
                Log.v(className, "Building HTTP Post Entity");

                //Build HTTP Request to Authenticate with Server
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("username", userName));
                nameValuePairs.add(new BasicNameValuePair("password", password));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                Log.v(className, "Executing HTTP Post");
                httppost.setParams(httpParams);
                responseString = EntityUtils.toString(httpclient.execute(httppost).getEntity());
                Log.v(className, "HTTP Response = " + responseString);
                if(responseString.length() > 0) {
                    resultCode = Common.LOGIN_SUCCESSFUL;
                }
                else {
                    resultCode = Common.LOGIN_FAILED;
                }
            } catch (Exception e) {
                Log.e(className, "Error Making/Receiving HTTP Request");
                resultCode = Common.LOGIN_CONNECTION_ERROR;
                responseString = e.getMessage();
            }
            return responseString;
        }

        @Override
        protected void onPreExecute() {
            progressDlg = ProgressDialog.show(ctx, "", "Validating Login...", true, true, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    cancelBackgroundProcess();
                }
            });
        }

        @Override
        protected void onPostExecute(final String passwordHash) {
            Log.d(className, "onPostExecute");
            Log.d(className, "isCanceled = " + Boolean.toString(isCancelled()));
            finishValidation(passwordHash);
        }

        @Override
        protected void onCancelled() {
            Log.d(className, "Login Canceled");
            resultCode = Common.LOGIN_CANCELED;
            finishValidation("");
        }
    }
}
