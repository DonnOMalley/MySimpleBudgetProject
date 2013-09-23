package com.omalleyland.mysimplebudget;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by omal310371 on 6/6/13.
 */
public class MainActivity extends Activity implements IBackgroundProcessor {

    private SharedPreferences prefs;

    private Menu        menu;
    private Spinner     spCategorySpinner;
    private Spinner     spStoreSpinner;
    private DatePicker  dpDebitDate;
    private EditText    etDebitAmount;
    private EditText    etComment;
    private Button      btnPostDebit;

    private String className;
    private String serverAddress;
    private String loginValidationPage;

    private ValidateLogin validateLogin;
    private ServerSynchroniser serverSynchroniser;

    //TODO : Add async tasks for interacting with local SQLite Database.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String userName;
        String password;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //Set className for Log Messaging
        className = getClass().toString();

        //set Preference and UI Variables
        prefs               = PreferenceManager.getDefaultSharedPreferences(this);
        spCategorySpinner   = (Spinner)findViewById(R.id.spinnerCategory);
        spStoreSpinner      = (Spinner)findViewById(R.id.spinnerStore);
        dpDebitDate         = (DatePicker)findViewById(R.id.datePicker);
        etDebitAmount       = (EditText)findViewById(R.id.etDebitAmount);
        etComment           = (EditText)findViewById(R.id.editText2);
        btnPostDebit        = (Button)findViewById(R.id.btnPostDebit);

        btnPostDebit.setOnClickListener(new PostDebitClick(this, dpDebitDate, spCategorySpinner, spStoreSpinner, etDebitAmount, etComment));
        //Create UI Events
        etDebitAmount.addTextChangedListener(new CurrencyTextWatcher());

        //if Username/password are saved, attempt to login
        //  Show Progress Dialog for Updating Configuration
        //  if login is successful:
        //      Upload data from local mySQL to Server
        //      Download data from Server to Local
        //  if login fails for any reason, work offline

        Log.d(className, "Assigning Preferences to Local Variables");
        this.serverAddress          = prefs.getString(Common.SERVER_ADDRESS_PREFERENCE, "");
        this.loginValidationPage    = prefs.getString(Common.SERVER_LOGIN_ADDRESS_PREFERENCE, "");
        userName                    = prefs.getString(Common.USER_NAME_PREFERENCE, "");
        password                    = prefs.getString(Common.PASSWORD_PREFERENCE, "");
        Log.d(className, "Preferences Assigned to Local Variables");

        //Check Server/Login Information and react accordingly
        if((this.serverAddress.length() == 0) || (this.loginValidationPage.length() == 0)) {
            //Launch Preference Dialog
            Log.d(className, "Missing Server Information - Launching Preferences Activity");
            startActivityForResult(new Intent(this, Preferences.class), Common.PREFERENCE_RESULT_CODE);
        }
        else if(userName.length() > 0 && password.length() > 0) {
            Log.d(className, "Starting Async Login Attempt");
            if(!this.serverAddress.endsWith("/")) {
                this.serverAddress = this.serverAddress.concat("/");
            }
            validateLogin = new ValidateLogin(this, this, this.serverAddress.concat(this.loginValidationPage), userName, password);
            validateLogin.executeValidation();
        }
        else {
            Log.d(className, "Missing Login Information - Launching Login Activity");
            startActivityForResult(new Intent(this, LoginActivity.class), Common.LOGIN_RESULT_CODE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(className, "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(className, "onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(className, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(className, "onResume");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v(className, "onRestart");
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        Log.v(className, "onConfigurationChanged");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.v(className, "Creating Menu");
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_create_category:
                startActivityForResult(new Intent(MainActivity.this, CreateCategory.class), Common.CREATE_CATEGORY_RESULT_CODE);
                return true;
            case R.id.action_create_store:
                startActivityForResult(new Intent(MainActivity.this, CreateStore.class), Common.CREATE_STORE_RESULT_CODE);
                return true;
            case R.id.action_upload_debits:
                //TODO : Upload Debits to Server
                return true;
            case R.id.action_login_preferences:
                startActivity(new Intent(MainActivity.this, Preferences.class));
                return true;
            case R.id.action_full_sync:
                prefs.edit().putString(Common.LAST_CATEGORY_SYNC_PREFERENCE, Integer.toString(Common.UNKNOWN)).commit();
                prefs.edit().putString(Common.LAST_STORE_SYNC_PREFERENCE, Integer.toString(Common.UNKNOWN)).commit();
                //Force Synchronization again
                synchroniseConfiguration();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String userName = "";

        if(requestCode == Common.LOGIN_RESULT_CODE) {
            String passwordHash = "";
            Log.v(className, "Login Activity Result :: " + Integer.toString(resultCode));

            if(resultCode == Common.LOGIN_SUCCESSFUL) {
                Log.d(className, "Login Successful");
                if(data.hasExtra(Common.LOGIN_RESULT_USER_EXTRA)) {
                    userName = data.getStringExtra(Common.LOGIN_RESULT_USER_EXTRA);
                }
                if(data.hasExtra(Common.LOGIN_RESULT_PASSWORD_EXTRA)) {
                    passwordHash = data.getStringExtra(Common.LOGIN_RESULT_PASSWORD_EXTRA);
                }
                Log.d(className, "User(" + userName + ") Token = " + passwordHash);

                synchroniseConfiguration();
            }
            else if(resultCode == Common.LOGIN_CONNECTION_ERROR) {
                Log.d(className, "Login Connection Failed - Working Offline");
                setTitle(Common.APPLICATION_NAME + " - OFFLINE");
            }
            else if(resultCode == Common.LOGIN_CANCELED) {
                //User Pressed Back button to exit login application - Only occurs if Login Information is not saved.
                Log.d(className, "Login Canceled - Launching Login Activity Again");
                Toast.makeText(this, "Login Information Must Be Entered", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(this, LoginActivity.class), Common.LOGIN_RESULT_CODE);
            }
            else if(resultCode == Common.LOGIN_FAILED) {
                Log.d(className, "Login Failed for " + userName);
                Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
                startActivityForResult(new Intent(this, LoginActivity.class), Common.LOGIN_RESULT_CODE);
            }

        }
        else if(requestCode == Common.PREFERENCE_RESULT_CODE) {
            String password;

            //This will always be RESULT_CANCELED since the back button is the only way to exit
            Log.v(className, "Preference Activity Result :: " + Integer.toString(resultCode));
            //Get Preferences and update variables accordingly
            this.serverAddress          = prefs.getString(Common.SERVER_ADDRESS_PREFERENCE, "");
            this.loginValidationPage    = prefs.getString(Common.SERVER_LOGIN_ADDRESS_PREFERENCE, "");
            userName                    = prefs.getString(Common.USER_NAME_PREFERENCE, "");
            password                    = prefs.getString(Common.PASSWORD_PREFERENCE, "");

            if((this.serverAddress.length() == 0) || (this.loginValidationPage.length() == 0)) {
                Log.d(className, "Validation Server Missing - Application Exiting");
                Toast.makeText(this, "VALIDATION SERVER STILL NOT ASSIGNED", Toast.LENGTH_LONG).show();
                this.finish();
            }
            else if(userName.length() > 0 && password.length() > 0) {
                Log.d(className, "Starting Async Login Attempt");
                if(!this.serverAddress.endsWith("/")) {
                    this.serverAddress = this.serverAddress.concat("/");
                }
                if(validateLogin == null) {
                    validateLogin = new ValidateLogin(this, this, this.serverAddress.concat(this.loginValidationPage), userName, password);
                }
                else {
                    validateLogin.setValidationServer(this.serverAddress.concat(this.loginValidationPage));
                    validateLogin.setUserName(userName);
                    validateLogin.setPassword(password);
                }
                validateLogin.executeValidation();
            }
            else {
                Log.d(className, "Username and/or Password still not assigned - Launching Login Activity");
                //Username or Password is still not saved, launch login activity to authenticate and store user/pass
                startActivityForResult(new Intent(this, LoginActivity.class), Common.LOGIN_RESULT_CODE);
            }
        }
        else if(requestCode == Common.CREATE_CATEGORY_RESULT_CODE) {
            Log.v(className, "Category Activity Result :: " + Integer.toString(resultCode));
            if(resultCode == RESULT_OK) {
                String selectedCategory = ((Category)spCategorySpinner.getSelectedItem()).getName();
                loadCategorySpinner();
                for(int i=0; i<spCategorySpinner.getAdapter().getCount(); i++) {
                    if(selectedCategory.equals(((Category)spCategorySpinner.getItemAtPosition(i)).getName())) {
                        spCategorySpinner.setSelection(i);
                        break;
                    }
                }
            }
        }
        else if(requestCode == Common.CREATE_STORE_RESULT_CODE) {
            Log.v(className, "Store Activity Result :: " + Integer.toString(resultCode));
            if(resultCode == RESULT_OK) {
                String selectedStore = ((Store)spStoreSpinner.getSelectedItem()).getName();
                loadStoreSpinner();
                for(int i=0; i<spStoreSpinner.getAdapter().getCount(); i++) {
                    if(selectedStore.equals(((Store)spStoreSpinner.getItemAtPosition(i)).getName())) {
                        spStoreSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }
    }


    private void loadCategorySpinner() {
        Log.d(className, "Loading Category Spinner");
        CategoryDBInterface categoryDBIntf = new CategoryDBInterface(this);
        List<SyncObject> categoryList;
        ArrayAdapter<SyncObject> categoryArrayAdapter;
        Log.d(className, "Getting Category List");
        categoryList = categoryDBIntf.getActiveDatabaseObjects();
        categoryList.add(0, new Category("<SELECT CATEGORY>"));
        categoryArrayAdapter = new ArrayAdapter<SyncObject>(this,android.R.layout.simple_spinner_item, categoryList);
        categoryArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spCategorySpinner.setOnItemSelectedListener(new onCategorySelected());
        spCategorySpinner.setAdapter(categoryArrayAdapter);

    }

    public class onCategorySelected implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Log.d(className, "Category onItemSelected Event being fired");
            Log.d(className, "Selected Category Index " + Integer.toString(position) + " :: ID = " + Long.toString(id));

            if(position > 0) {
                Log.d(className, "Selected Category(" + Integer.toString(position) + ") = " + (adapterView.getSelectedItem()).toString());
            }
            else {
                Log.d(className, "<SELECT CATEGORY> Item(0) Selected");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Log.d(className, "Category onNothingSelected Event being fired");
        }

    }

    private void loadStoreSpinner() {
        Log.d(className, "Loading Store Spinner");
        StoreDBInterface storeDBIntf = new StoreDBInterface(this);
        List<SyncObject> storeList;
        ArrayAdapter<SyncObject> storeArrayAdapter;
        Log.d(className, "Getting Store List");
        storeList = storeDBIntf.getActiveDatabaseObjects();
        storeList.add(0, new Store("<SELECT STORE>"));
        storeArrayAdapter = new ArrayAdapter<SyncObject>(this,android.R.layout.simple_spinner_item, storeList);
        storeArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spStoreSpinner.setOnItemSelectedListener(new onStoreSelected());
        spStoreSpinner.setAdapter(storeArrayAdapter);

    }

    public class onStoreSelected implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
            Log.d(className, "Store onItemSelected Event being fired");
            Log.d(className, "Selected Store Index " + Integer.toString(position) + " :: ID = " + Long.toString(id));

            if(position > 0) {
                Log.d(className, "Selected Store(" + Integer.toString(position) + ") = " + (adapterView.getSelectedItem()).toString());
            }
            else {
                Log.d(className, "<SELECT STORE> Item(0) Selected");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Log.d(className, "Store onNothingSelected Event being fired");
        }

    }

    public void synchroniseConfiguration() {
        //Start ASync Task to Synchronize with Server
        // Need to rename IValidateLogin Interface
        //      - Add Synchronization Callbacks for:
        //          - Categories
        //          - Stores
        //          - Debits
        //
        //      **Could maybe have one call back UpdateConfigurations() where UI Spinners are updated
        if(serverSynchroniser == null) {
            serverSynchroniser = new ServerSynchroniser(this, this);
        }
        serverSynchroniser.synchroniseData();
    }

    /*
        Interface Callbacks
     */

    /* Call back for ValidateLogin Class executeLogin call*/
    public void processLoginResult(int resultCode, String passwordHash) {
        Log.d(className, "Processing Login Result :: resultCode = " + Integer.toString(resultCode) + " :: " + passwordHash);
        if(resultCode == RESULT_OK || resultCode == Common.LOGIN_SUCCESSFUL) {
            String userName;
            Log.d(className, "Login Successful :: resultCode = " + Integer.toString(resultCode));
            userName = prefs.getString(Common.USER_NAME_PREFERENCE, "");
            Log.d(className, "User(" + userName + ") Token = " + passwordHash);

            synchroniseConfiguration();
        }
        else if (resultCode == Common.LOGIN_CANCELED) {
            Log.d(className, "Login Canceled - Exiting Application");
            finish();
        }
        else if (resultCode == Common.LOGIN_FAILED) {
            Log.d(className, "Login Failed - Launching Preference Dialog");
            Toast.makeText(this, "Login Failed - Update Preferences with Correct Login Information", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent(this, Preferences.class), Common.PREFERENCE_RESULT_CODE);
        }
        else if (resultCode == Common.LOGIN_CONNECTION_ERROR) {
            Log.d(className, "Login Connection Failed - Working Offline");
            setTitle(Common.APPLICATION_NAME + " - OFFLINE");
        }
        else {
            Log.d(className, "Unknown Login Result :: Result Code =  " + Integer.toString(resultCode));
        }
    }

    //Call back for Updating Category/Store Spinners after Synchronisation with Server
    public void updateUIControls(ArrayList<Boolean> updateUIControlList) {
        String selectedCategory = "";
        String selectedStore    = "";

        if(updateUIControlList.get(Common.CATEGORY_UI_CONTROL_INDEX)) {
            Log.d(className, "IBackgroundProcessor Call Back for Updating UI Categories :: Update = " + Boolean.toString(updateUIControlList.get(Common.CATEGORY_UI_CONTROL_INDEX)));

            //Get Selected Category by Name
            Log.d(className, "Getting Selected Category");
            if(spCategorySpinner.getCount() > 0) {
                selectedCategory = ((Category)spCategorySpinner.getSelectedItem()).getName();
            }

            Log.d(className, "Updating Category Spinner");
            //Reload Category Spinner and select the same one that was originally selected
            loadCategorySpinner();
            if(selectedCategory.length() > 0) {
                for(int i=0; i<spCategorySpinner.getAdapter().getCount(); i++) {
                    if(selectedCategory.equals(((Category)spCategorySpinner.getItemAtPosition(i)).getName())) {
                        spCategorySpinner.setSelection(i);
                        break;
                    }
                }
            }
        }

        if(updateUIControlList.get(Common.STORE_UI_CONTROL_INDEX)) {
            Log.d(className, "IBackgroundProcessor Call Back for Updating UI Categories :: Update = " + Boolean.toString(updateUIControlList.get(Common.STORE_UI_CONTROL_INDEX)));

            //Get Selected Store by Name
            Log.d(className, "Getting Selected Store");
            if(spStoreSpinner.getCount() > 0) {
                selectedStore = ((Store)spStoreSpinner.getSelectedItem()).getName();
            }

            //Reload Store Spinner and select the same one that was originally selected
            Log.d(className, "Updating Store Spinner");
            loadStoreSpinner();
            if(selectedStore.length() > 0) {
                for(int i=0; i<spStoreSpinner.getAdapter().getCount(); i++) {
                    if(selectedStore.equals(((Store)spStoreSpinner.getItemAtPosition(i)).getName())) {
                        spStoreSpinner.setSelection(i);
                        break;
                    }
                }
            }
        }
    }
}
