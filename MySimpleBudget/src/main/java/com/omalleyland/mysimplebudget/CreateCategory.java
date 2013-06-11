package com.omalleyland.mysimplebudget;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Created by omal310371 on 6/7/13.
 */
public class CreateCategory extends Activity {

    private EditText etCategoryName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_category);

        etCategoryName = (EditText)findViewById(R.id.etCategoryName);
    }

    public void createCategoryClick(View v) {
        if(etCategoryName.getText().toString().length() > 0) {
            Category category = new Category(etCategoryName.getText().toString());
            CategoryDBInterface categoryDBIntfc = new CategoryDBInterface(getApplicationContext());
            categoryDBIntfc.addCategory(category);
            setResult(RESULT_OK);
            finish();
        }


    }
}