package com.omalleyland.mysimplebudget;

import android.R;
import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by omal310371 on 6/8/13.
 */
public class CategoryArrayAdapter extends ArrayAdapter<Category> {

    public CategoryArrayAdapter(Context context, List<Category> categoryList) {
        super(context, R.layout.simple_spinner_dropdown_item, categoryList);

    }
}
