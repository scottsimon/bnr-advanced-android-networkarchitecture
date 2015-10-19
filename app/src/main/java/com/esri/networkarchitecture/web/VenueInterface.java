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
package com.esri.networkarchitecture.web;

import com.esri.networkarchitecture.models.VenueSearchResponse;
import retrofit.Callback;
import retrofit.http.*;

/**
 * Created by scotts on 10/19/15.
 */
public interface VenueInterface {

  @GET("/venues/search")
  void venueSearch(@Query("ll") String latLngString, Callback<VenueSearchResponse> callback);

  @FormUrlEncoded
  @POST("/checkins/add")
  void venueCheckIn(@Field("venueId") String venueId, Callback<Object> callback);

//  // Chapter 2 Challenge
//  @GET("/venues/{id}")
//  void venueDetail(@Query("id") String venueId, Callback<Venue> callback);

}
