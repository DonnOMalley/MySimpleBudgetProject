package com.omalleyland.mysimplebudget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by omal310371 on 6/7/13.
 */
public class CreateStore extends Activity {

    private EditText etStoreName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_store);

        etStoreName = (EditText)findViewById(R.id.etStoreName);
    }

    public void createStoreClick(View v) {
        if(etStoreName.getText().toString().length() > 0) {
            Store store = new Store(etStoreName.getText().toString());
            StoreDBInterface storeDBIntfc = new StoreDBInterface(getApplicationContext());
            store.setSyncStatus(Common.SYNC_STATUS_NEW);
            store.setActiveStatus(Common.ACTIVE_STATUS_ACTIVE);
            storeDBIntfc.addObject(store);
            setResult(RESULT_OK);
            finish();
        }

    }
}