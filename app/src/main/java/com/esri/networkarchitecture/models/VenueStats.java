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
public class VenueStats {
  @SerializedName("checkinsCount") private int mCheckinsCount;
  @SerializedName("usersCount") private int mUsersCount;
  @SerializedName("tipCount") private int mTipCount;
  @SerializedName("visitsCount") private int mVisitsCount;

  public int getCheckinsCount() {
    return mCheckinsCount;
  }

  public int getUsersCount() {
    return mUsersCount;
  }

  public int getTipCount() {
    return mTipCount;
  }

  public int getVisitsCount() {
    return mVisitsCount;
  }
}
