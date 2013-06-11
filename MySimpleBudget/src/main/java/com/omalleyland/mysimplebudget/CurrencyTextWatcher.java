package com.omalleyland.mysimplebudget;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

/**
 * Created by omal310371 on 6/8/13.
 */
public class CurrencyTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        Log.d(getClass().toString(), "afterTextChange :: Text to evaluate :: " + text);
        int decimalIndex = text.indexOf(".");
        if(decimalIndex > -1 && decimalIndex < text.length() - 3) {
            //if user is entering a 3rd digit after the decimal, prevent it from being presented by
            //deleting the last character from the text about to be refreshed into the control
            editable.delete(editable.length() - 1, editable.length());
        }
        Log.d(getClass().toString(), "afterTextChange :: Text Returned :: " + editable.toString());
    }
}
