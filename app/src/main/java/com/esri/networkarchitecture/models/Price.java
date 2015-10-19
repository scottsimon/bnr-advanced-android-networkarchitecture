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

import com.google.gson.annotations.SerializedName;

/**
 * Created by scotts on 10/19/15.
 */
public class Price {

  @SerializedName("tier") private int mTier;
  @SerializedName("message") private String mMessage;

  public int getTier() {
    return mTier;
  }

  public String getMessage() {
    return mMessage;
  }
}
