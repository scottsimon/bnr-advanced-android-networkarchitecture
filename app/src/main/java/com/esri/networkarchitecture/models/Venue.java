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

import java.util.List;

/**
 * Created by scotts on 10/19/15.
 */
public class Venue {
  @SerializedName("id") private String mId;
  @SerializedName("name") private String mName;
  @SerializedName("verified") private boolean mVerified;
  @SerializedName("location") private Location mLocation;
  @SerializedName("categories") private List<Category> mCategory;

  public String getId() {
    return mId;
  }

  public String getName() {
    return mName;
  }

  public String getFormattedAddress() {
    return mLocation.getFormattedAddress();
  }

}
