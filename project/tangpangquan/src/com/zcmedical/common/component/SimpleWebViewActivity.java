package com.zcmedical.common.component;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;

public class SimpleWebViewActivity extends BaseActivity {

    public static final String WEB_URL_KEY = "WEB_URL_KEY";

    public static final String ACTION_BAR_TITLE_KEY = "ACTION_BAR_TITLE_KEY";

    private WebView simple_web_view_browser_webview;

    private boolean isHistoryClearable = false;

    private String webURL;
    private String actionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple_web_view);

        Intent intent = getIntent();
        if (null != intent) {
            this.webURL = intent.getStringExtra(WEB_URL_KEY);
            this.actionBarTitle = intent.getStringExtra(ACTION_BAR_TITLE_KEY);
        }

        initToolbar("");

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        if (!TextUtils.isEmpty(actionBarTitle)) {
            actionBar.setTitle(actionBarTitle);
        } else {
            actionBar.setTitle(R.string.app_name);
        }

        simple_web_view_browser_webview = (WebView) findViewById(R.id.simple_web_view_browser_webview);
        final ProgressBar simple_web_view_browser_progress_bar_pb = (ProgressBar) findViewById(R.id.simple_web_view_browser_progress_bar_pb);
        simple_web_view_browser_progress_bar_pb.setVisibility(View.VISIBLE);

        WebSettings webSettings = simple_web_view_browser_webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        simple_web_view_browser_webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (isHistoryClearable) {
                    isHistoryClearable = false;
                    view.clearHistory();
                }
                simple_web_view_browser_progress_bar_pb.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

        });

        simple_web_view_browser_webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                simple_web_view_browser_progress_bar_pb.setProgress(newProgress);
                super.onProgressChanged(view, newProgress);
            }
        });

        //        simple_web_view_browser_webview.loadUrl(DEFAULT_ACCOUNT_SERVICE_INTRO_URL);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!TextUtils.isEmpty(webURL)) {
            simple_web_view_browser_webview.loadUrl(webURL);
            isHistoryClearable = true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (simple_web_view_browser_webview.canGoBack()) {
            simple_web_view_browser_webview.goBack();
        } else {
            simple_web_view_browser_webview.clearHistory();
            simple_web_view_browser_webview.clearFormData();
            simple_web_view_browser_webview.clearCache(true);
            //            deleteDatabase("webview.db");
            //            deleteDatabase("webviewCache.db");
            super.onBackPressed();
        }
    }
}
