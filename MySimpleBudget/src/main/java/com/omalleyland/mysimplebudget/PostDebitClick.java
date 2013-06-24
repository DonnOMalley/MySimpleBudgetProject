package com.omalleyland.mysimplebudget;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by omal310371 on 6/14/13.
 */
public class PostDebitClick implements View.OnClickListener {

    private Context context;
    private DatePicker debitDate;
    private Spinner categorySpinner;
    private Spinner storeSpinner;
    private EditText amountEditText;
    private EditText commentEditText;

    public PostDebitClick(Context context, DatePicker debitDate, Spinner category, Spinner store, EditText amount, EditText comment) {
        this.context = context;
        this.debitDate = debitDate;
        this.categorySpinner = category;
        this.storeSpinner = store;
        this.amountEditText = amount;
        this.commentEditText = comment;
    }

    @Override
    public void onClick(View view) {

        String date;
        String entryOn;
        Date parsedDate;
        String month = Integer.toString(this.debitDate.getMonth() + 1);
        String day = Integer.toString(this.debitDate.getDayOfMonth());
        if(month.length() < 2) {
            month = "0".concat(month);
        }
        if(day.length() < 2) {
            day = "0".concat(day);
        }

        date = Integer.toString(this.debitDate.getYear()).concat("-".concat(month)).concat("-".concat(day));

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String localTimeZone = simpleDateFormat.getTimeZone().getDisplayName();
        try {
            parsedDate = new Date();
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
//            Toast.makeText(this.context, "UTC Time = ".concat(date), Toast.LENGTH_LONG).show();

            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            entryOn = simpleDateFormat.format(parsedDate);

            Category category = (Category)this.categorySpinner.getSelectedItem();
            Store store = (Store)this.storeSpinner.getSelectedItem();
            Double amount = Double.parseDouble(this.amountEditText.getText().toString());
            date = simpleDateFormat.format(parsedDate);
            String comment = this.commentEditText.getText().toString();

            Debit postDebit = new Debit();
            postDebit.setAmount(amount);
            postDebit.setLocalCategoryID(category.getID());
            if(category.getServerID() != Common.UNKNOWN) {
                postDebit.setCategoryID(category.getServerID());
            }

            postDebit.setLocalStoreID(store.getID());
            if(store.getServerID() != Common.UNKNOWN) {
                postDebit.setStoreID(store.getServerID());
            }
            date = debitDate.toString();
            postDebit.setDateString(date);
            postDebit.setComment(comment);
            postDebit.setEntryOnString(entryOn);








            //TODO : Try to post debit directly and no matter what, post to local sqlite database
            //          Check for Category/Store server ids, If either are not assigned, post ONLY to local.
            //          If successfully posted to server, mark as pending verify
            //          If NOT Successful, mark as new.



//            date = simpleDateFormat.format(parsedDate);
//            parsedDate = simpleDateFormat.parse(parsedDate.toString());
        }
        catch (Exception e) {
            parsedDate = new Date();
        }
//        Toast.makeText(this.context, "Parsed Date = ".concat(parsedDate.toString()), Toast.LENGTH_LONG).show();
//        Toast.makeText(this.context, "Formatted Date = ".concat(date), Toast.LENGTH_LONG).show();
    }
}
