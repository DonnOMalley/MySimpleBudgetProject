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
    private Spinner category;
    private Spinner store;
    private EditText amount;
    private EditText comment;

    public PostDebitClick(Context context, DatePicker debitDate, Spinner category, Spinner store, EditText amount, EditText comment) {
        this.context = context;
        this.debitDate = debitDate;
        this.category = category;
        this.store = store;
        this.amount = amount;
        this.comment = comment;
    }

    @Override
    public void onClick(View view) {

        String date;
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
            date = simpleDateFormat.format(parsedDate);
            Toast.makeText(this.context, "UTC Time = ".concat(date), Toast.LENGTH_LONG).show();

            simpleDateFormat.setTimeZone(TimeZone.getDefault());
            date = simpleDateFormat.format(parsedDate);
            Toast.makeText(this.context, "Local Time = ".concat(date), Toast.LENGTH_LONG).show();



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
