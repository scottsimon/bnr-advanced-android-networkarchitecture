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
public class Location {
  @SerializedName("lat") private double mLatitude;
  @SerializedName("lng") private double mLongitude;
  @SerializedName("formattedAddress") private List<String>mFormattedAddress;

  public String getFormattedAddress() {
    String formattedAddress = "";

    for (String addressPart : mFormattedAddress) {
      formattedAddress += addressPart;
      if (mFormattedAddress.indexOf(addressPart) != (mFormattedAddress.size() - 1)) {
        formattedAddress += " ";
      }
    }

    return formattedAddress;
  }

}
