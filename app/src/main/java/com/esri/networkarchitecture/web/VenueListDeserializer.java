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
import com.google.gson.*;

import java.lang.reflect.Type;

/**
 * Created by scotts on 10/19/15.
 */
public class VenueListDeserializer implements JsonDeserializer<VenueSearchResponse> {

  @Override
  public VenueSearchResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonElement responseJson = json.getAsJsonObject().get("response");
    return new Gson().fromJson(responseJson, VenueSearchResponse.class);
  }

}
