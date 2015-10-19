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
package com.esri.networkarchitecture.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by scotts on 10/19/15.
 */
public class TokenStore {
  private static final String TOKEN_KEY = "TokenStore.TokenKey";

  private Context mContext;
  private SharedPreferences mSharedPreferences;

  public TokenStore(Context context) {
    mContext = context.getApplicationContext();
    mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
  }

  public String getAccessToken() {
    return mSharedPreferences.getString(TOKEN_KEY, null);
  }

  public void setAccessToken(String accessToken) {
    mSharedPreferences.edit()
        .putString(TOKEN_KEY, accessToken)
        .apply();
  }

}
