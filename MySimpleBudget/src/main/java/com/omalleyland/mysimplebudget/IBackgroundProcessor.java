package com.omalleyland.mysimplebudget;

import android.content.DialogInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by omal310371 on 6/6/13.
 */
public interface IBackgroundProcessor{

    //For handling login results
    public abstract void processLoginResult(int resultCode, String passwordHash);

    //For updating Activity UI Controls
    public abstract void updateUIControls(ArrayList<Boolean> updateUIList);
}
