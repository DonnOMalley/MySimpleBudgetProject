package com.omalleyland.mysimplebudget;

import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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

        DebitDBInterface dbInterface = new DebitDBInterface(this.context);
        boolean canPostToServer = true;
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
            if(this.categorySpinner.getSelectedItemPosition() > 0 && this.storeSpinner.getSelectedItemPosition() > 0) {
                parsedDate = new Date();
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

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
                else {
                    canPostToServer = false;
                }

                postDebit.setLocalStoreID(store.getID());
                if(store.getServerID() != Common.UNKNOWN) {
                    postDebit.setStoreID(store.getServerID());
                }
                else {
                    canPostToServer = false;
                }

                date = Integer.toString(debitDate.getYear());
                if(debitDate.getMonth() < 9) {
                    date += "-0" + Integer.toString(debitDate.getMonth() + 1);
                }
                else {
                    date += "-" + Integer.toString(debitDate.getMonth() + 1);
                }
                if(debitDate.getDayOfMonth() < 10) {
                    date += "-0" + Integer.toString(debitDate.getDayOfMonth());
                }
                else {
                    date += "-" + Integer.toString(debitDate.getDayOfMonth());
                }
                postDebit.setDateString(date);
                postDebit.setComment(comment);
                postDebit.setEntryOnString(entryOn);
                postDebit.setSyncStatus(Common.SYNC_STATUS_NEW);

                //Add debit to local SQLite Database
                if(dbInterface.addObject(postDebit) < 0) {
                    canPostToServer = false;
                    Toast.makeText(this.context, "Unable to Create Debit", Toast.LENGTH_LONG).show();
                }
                else {
                    //Clear out debit information since it was added to the local SQLite DB
                    this.categorySpinner.setSelection(0);
                    this.storeSpinner.setSelection(0);
                    this.amountEditText.setText("");
                    this.commentEditText.setText("");
                }

                //If Server IDs(Store/Category) exist, attempt to post to server.
                if(canPostToServer) {
                    //Synchronise any debits
                    ServerSynchroniser serverSynchroniser;
                    serverSynchroniser = new ServerSynchroniser();
                    serverSynchroniser.setContext(this.context);
                    serverSynchroniser.setPostOnlyDebits(true);
                    serverSynchroniser.synchroniseData();
                }
            }
        }
        catch (Exception e) {
            //TODO : Do something with this exception
        }
    }
}
