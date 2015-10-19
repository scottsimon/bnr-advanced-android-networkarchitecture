/*
  COPYRIGHT 1995-2015  ESRI

  TRADE SECRETS: ESRI PROPRIETARY AND CONFIDENTIAL
  Unpublished material - all rights reserved under the
  Copyright Laws of the United States.

  For additional information, contact:
  Environmental Systems Research Institute, Inc.
  Attn: Contracts Dept
  380 New York Street
  Redlands, California, USA 92373

  email: contracts@esri.com
*/
package com.esri.networkarchitecture.controllers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.esri.networkarchitecture.helpers.FoursquareOauthUriHelper;
import com.esri.networkarchitecture.models.TokenStore;
import com.esri.networkarchitecture.web.DataManager;

/**
 * Created by scotts on 10/19/15.
 */
public class AuthenticationActivity extends AppCompatActivity {

  private WebView mWebView;
  private DataManager mDataManager;

  private WebViewClient mWebViewClient = new WebViewClient() {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (url.contains(DataManager.OAUTH_REDIRECT_URI)) {
        FoursquareOauthUriHelper uriHelper = new FoursquareOauthUriHelper(url);
        if (uriHelper.isAuthorized()) {
          String accessToken = uriHelper.getAccessToken();
          TokenStore tokenStore = new TokenStore(AuthenticationActivity.this);
          tokenStore.setAccessToken(accessToken);
        }
        finish();
      }

      return true;
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mWebView = new WebView(this);
    setContentView(mWebView);

    mWebView.getSettings().setJavaScriptEnabled(true);
    mWebView.setWebViewClient(mWebViewClient);

    mDataManager = DataManager.get(this);
    mWebView.loadUrl(mDataManager.getAuthenticationUrl());
  }



}
