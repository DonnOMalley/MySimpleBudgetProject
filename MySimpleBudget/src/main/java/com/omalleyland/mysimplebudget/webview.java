package com.omalleyland.mysimplebudget;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class webview extends Activity {

    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        Intent intent = getIntent();
        String webAddress = intent.getStringExtra(Common.WEB_VIEW_INTENT);

        //Assign webView object to variable, Set JavaScript as enabled and load the URL
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true); 		//this allows the spending info graphs to load
        webView.setWebViewClient(new BudgetWebViewClient());	//keep links loading page in own webview

        webView.clearView();
        webView.loadUrl(webAddress);
    }

    private class BudgetWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
