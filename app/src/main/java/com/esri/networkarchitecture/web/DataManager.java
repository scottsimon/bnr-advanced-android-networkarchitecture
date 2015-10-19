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

import android.content.Context;
import android.util.Log;
import com.esri.networkarchitecture.listeners.VenueSearchListener;
import com.esri.networkarchitecture.models.Venue;
import com.esri.networkarchitecture.models.VenueSearchResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scotts on 10/19/15.
 */
public class DataManager {

  private static final String TAG = DataManager.class.getSimpleName();
  private static final String FOURSQUARE_ENDPOINT = "https://api.foursquare.com/v2";
  private static final String CLIENT_ID = "1UGKP22H5D3QK3WXWQMVG0Z2Y11TDARZONY34RY1NKUSDHAL";
  private static final String CLIENT_SECRET = "QQZDCMZCMOGGHBWBB25QN20JWYQ1KJCJSUOY1Z5J3WHFVDFW";
  private static final String FOURSQUARE_VERSION = "20150406";
  private static final String FOURSQUARE_MODE = "foursquare";
  private static final String TEST_LAT_LNG = "33.759,-84.332";

  private static DataManager sDataManager;

  private RestAdapter mBasicRestAdapter;
  private List<Venue> mVenueList;
  private List<VenueSearchListener> mSearchListenerList;

  protected DataManager(RestAdapter basicRestAdapter) {
    mBasicRestAdapter = basicRestAdapter;
    mSearchListenerList = new ArrayList<>();
  }

  public static DataManager get(Context context) {
    if (sDataManager == null) {
      Gson gson = new GsonBuilder()
          .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer())
          .create();

      RestAdapter basicRestAdapter = new RestAdapter.Builder()
          .setEndpoint(FOURSQUARE_ENDPOINT)
          .setConverter(new GsonConverter(gson))
          .setLogLevel(RestAdapter.LogLevel.FULL)
          .setRequestInterceptor(sRequestInterceptor)
          .build();
      sDataManager = new DataManager(basicRestAdapter);
    }

    return sDataManager;
  }

  public static RequestInterceptor sRequestInterceptor = new RequestInterceptor() {
    @Override
    public void intercept(RequestFacade request) {
      request.addQueryParam("client_id", CLIENT_ID);
      request.addQueryParam("client_secret", CLIENT_SECRET);
      request.addQueryParam("v", FOURSQUARE_VERSION);
      request.addQueryParam("m", FOURSQUARE_MODE);
    }
  };

  public List<Venue> getVenueList() {
    return mVenueList;
  }

  public void fetchVenueSearch() {
    VenueInterface venueInterface = mBasicRestAdapter.create(VenueInterface.class);
    venueInterface.venueSearch(TEST_LAT_LNG, new Callback<VenueSearchResponse>() {
      @Override
      public void success(VenueSearchResponse venueSearchResponse, Response response) {
        mVenueList = venueSearchResponse.getVenueList();
        notifySearchListeners();
      }

      @Override
      public void failure(RetrofitError error) {
        Log.e(TAG, "Failed to fetch venue search", error);
      }
    });
  }

  public void addVenueSearchListener(VenueSearchListener listener) {
    mSearchListenerList.add(listener);
  }

  public void removeVenueSearchListener(VenueSearchListener listener) {
    mSearchListenerList.remove(listener);
  }

  private void notifySearchListeners() {
    for (VenueSearchListener listener : mSearchListenerList) {
      listener.onVenueSearchFinished();
    }
  }

}
