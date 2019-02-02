package com.zcmedical.tangpangquan.activity;

import java.net.URL;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zcmedical.common.base.BaseActivity;
import com.zcmedical.tangpangquan.R;

/*
 * 知识，故事，奇趣，等web页面
 */
public class WebViewActivity extends BaseActivity {

    ImageView ivOcuppy;
    WebView webview;
    String url = "";
    String title = "UDr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        if (getIntent() != null && getIntent().getExtras() != null) {
            url = getIntent().getExtras().getString("url");
            title = getIntent().getExtras().getString("title");
        }
        initUI();
        initToolbar();
    }

    private void initUI() {
        ivOcuppy = findView(R.id.ivOcuppy);
        webview = findView(R.id.webview);
        webview.setWebViewClient(new WebClient());
        webview.loadUrl(url);
        //Picasso.with(this).load(res).error(R.drawable.ic_launcher).into(ivOcuppy);
    }

    private class WebClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findView(R.id.layout_toolbar);
        if (null != toolbar) {
            toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
            setSupportActionBar(toolbar);
        }

        ActionBar actionBar = getSupportActionBar();
        if (null != actionBar) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.selector_btn_back));
            actionBar.setTitle(title);
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

}
