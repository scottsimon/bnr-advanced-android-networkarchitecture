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

import com.esri.networkarchitecture.exceptions.UnauthorizedException;
import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.net.HttpURLConnection;

/**
 * Created by scotts on 10/20/15.
 */
public class RetrofitErrorHandler implements ErrorHandler {

  @Override
  public Throwable handleError(RetrofitError cause) {
    Response response = cause.getResponse();
    if (response != null && response.getStatus() == HttpURLConnection.HTTP_UNAUTHORIZED) {
      return new UnauthorizedException(cause);
    }

    return cause;
  }

}
