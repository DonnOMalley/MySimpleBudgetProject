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

    private boolean validateDebitRequirements() {
        boolean result = false;

        //Only return true(valid) if all category, payee and Amount have been entered
        if((this.categorySpinner.getSelectedItemPosition() > 0) &&
                (this.storeSpinner.getSelectedItemPosition() >0) &&
                (Double.parseDouble(this.amountEditText.getText().toString()) > 0)) {
            result = true;
        }

        return result;
    }

    @Override
    public void onClick(View view) {

        DebitDBInterface dbInterface = new DebitDBInterface(this.context);
        boolean canPostToServer = true;
        String date;
        String entryOn;
        SimpleDateFormat simpleDateFormat;
        Date parsedDate;
        Double amount;
        String comment;
        Category category;
        Store store;
        Debit postDebit;
        try {
            if(validateDebitRequirements()) {
                parsedDate = new Date();
                simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                entryOn = simpleDateFormat.format(parsedDate);

                category = (Category)this.categorySpinner.getSelectedItem();
                store = (Store)this.storeSpinner.getSelectedItem();
                amount = Double.parseDouble(this.amountEditText.getText().toString());
                comment = this.commentEditText.getText().toString();
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

                postDebit = new Debit();
                postDebit.setAmount(amount);
                postDebit.setLocalCategoryID(category.getID());
                postDebit.setCategoryID(category.getServerID());
                if(postDebit.getCategoryID() == Common.UNKNOWN) {
                    canPostToServer = false;
                }

                postDebit.setLocalStoreID(store.getID());
                postDebit.setStoreID(store.getServerID());
                if(postDebit.getStoreID() == Common.UNKNOWN) {
                    canPostToServer = false;
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
                    Toast.makeText(this.context, "Debit Successfully Created", Toast.LENGTH_LONG).show();
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
            else {
                if(this.categorySpinner.getSelectedItemPosition()==0){
                    Toast.makeText(this.context, "Category Must be Selected", Toast.LENGTH_LONG).show();
                }
                else if(this.storeSpinner.getSelectedItemPosition()==0){
                    Toast.makeText(this.context, "Payee Must be Selected", Toast.LENGTH_LONG).show();
                }
                else if(Double.parseDouble(this.amountEditText.getText().toString())==0){
                    Toast.makeText(this.context, "Must Enter A Debit Amount", Toast.LENGTH_LONG).show();
                }
            }
        }
        catch (Exception e) {
            //TODO : Do something with this exception
            Toast.makeText(this.context, "Error Creating Debit - May Need to be Re-Entered", Toast.LENGTH_LONG).show();
        }
    }
}
