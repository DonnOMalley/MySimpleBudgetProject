package com.omalleyland.mysimplebudget;

/**
 * Created by omal310371 on 6/19/13.
 */
public interface IHttpObject {

    public abstract String postHTTP(String json);

    public abstract String getHTTP(String json);

    public abstract void setSyncTimestamp();

}
