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
package com.esri.networkarchitecture.helpers;

import android.net.Uri;

/**
 * Created by scotts on 10/19/15.
 */
public class FoursquareOauthUriHelper {

  private static final String ACCESS_TOKEN_PARAM = "access_token=";

  private Uri mOauthUri;

  public FoursquareOauthUriHelper(String oauthUri) {
    mOauthUri = Uri.parse(oauthUri);
  }

  public String getAccessToken() {
    String uriFragment = mOauthUri.getFragment();
    if (uriFragment.contains(ACCESS_TOKEN_PARAM)) {
      return uriFragment.substring(ACCESS_TOKEN_PARAM.length());
    }
    return null;
  }

  public boolean isAuthorized() {
    return getAccessToken() != null;
  }

}
