package com.omalleyland.mysimplebudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by omal310371 on 6/20/13.
 */
public class StoreHTTPObject implements IHttpObject {

    /* For Logging */
    private String              className       = "";

    //Objects for sending/receiving Http Request/Response
    private DefaultHttpClient httpClient;
    private BasicHttpParams httpParams;
    private HttpPost httpPost;
    private HttpEntity httpEntity;
    private List<NameValuePair> nameValuePairs;

    /* Global Variables for managing common data/objects */
    private SharedPreferences prefs;
    private String              serverAddress   = "";
    private String              syncPage        = "";
    private String              userName        = "";
    private String              password        = "";
    private String              dateString      = "";
    private SimpleDateFormat simpleDateFormat;

    public StoreHTTPObject(Context context, String serverAddress, String userName, String password) {
        className           = getClass().toString();
        this.prefs          = PreferenceManager.getDefaultSharedPreferences(context);
        this.serverAddress  = serverAddress;
        this.userName       = userName;
        this.password       = password;

        //initialize date format for strings to match MySQL
        simpleDateFormat = new SimpleDateFormat(Common.DATE_FORMAT_STRING);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        syncPage = prefs.getString(Common.SERVER_STORE_ADDRESS_PREFERENCE, "");
        syncPage = serverAddress.concat(syncPage);

        //Get date of last sync
        dateString = prefs.getString(Common.LAST_STORE_SYNC_PREFERENCE, "-1");
        Log.d(this.className, "Last Store Sync Timestamp :: ".concat(dateString));
        if(dateString.equals("-1")) {
            dateString = simpleDateFormat.format(new Date(0));
        }
    }

    @Override
    public String postHTTP(String json) {
        String httpResponse = "";
        List<NameValuePair> nameValuePairs;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, Common.HTTP_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, Common.HTTP_TIMEOUT);

        httpPost = new HttpPost(this.syncPage);
        httpPost.setParams(httpParams);
        nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("username", this.userName));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        nameValuePairs.add(new BasicNameValuePair("json", json));
        Log.d(this.className, "Name Value Pairs built : ".concat(Integer.toString(nameValuePairs.size())));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.d(this.className, "Executing Post HTTP Request To :: ".concat(this.syncPage));
            httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = httpClient.execute(httpPost).getEntity();
            httpResponse = EntityUtils.toString(httpEntity);
            Log.d(this.className, "http Post Response = ".concat(httpResponse.toString()));
        }
        catch (Exception e) {
            Log.e(this.className, "Exception building/executing HTTP Post :: ".concat(e.getMessage()));
        }

        return httpResponse;
    }

    @Override
    public String getHTTP(String json) {
        String httpResponse = "";
        List<NameValuePair> nameValuePairs;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, Common.HTTP_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParams, Common.HTTP_TIMEOUT);

        httpPost = new HttpPost(this.syncPage);
        httpPost.setParams(httpParams);
        nameValuePairs = new ArrayList<NameValuePair>(3);
        nameValuePairs.add(new BasicNameValuePair("username", this.userName));
        nameValuePairs.add(new BasicNameValuePair("password", this.password));
        nameValuePairs.add(new BasicNameValuePair("lastUpdated", this.dateString));
        nameValuePairs.add(new BasicNameValuePair("json", json));
        Log.d(this.className, "Name Value Pairs built : ".concat(Integer.toString(nameValuePairs.size())));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            Log.d(this.className, "Executing Get HTTP Request To :: ".concat(this.syncPage));
            httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = httpClient.execute(httpPost).getEntity();
            httpResponse = EntityUtils.toString(httpEntity);
            Log.d(this.className, "http Get Response = ".concat(httpResponse.toString()));
        }
        catch (Exception e) {
            Log.e(this.className, "Exception building/executing HTTP Post :: ".concat(e.getMessage()));
        }

        return httpResponse;
    }

    @Override
    public void setSyncTimestamp() {
        dateString = simpleDateFormat.format(new Date());
        Log.d(this.className, "Writing Store Sync Timestamp :: ".concat(dateString));
        prefs.edit().putString(Common.LAST_STORE_SYNC_PREFERENCE, dateString).commit();
    }
}
