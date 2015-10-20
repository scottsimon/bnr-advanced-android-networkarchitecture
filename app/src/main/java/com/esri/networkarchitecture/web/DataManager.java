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
import android.net.Uri;
import android.util.Log;
import com.esri.networkarchitecture.exceptions.UnauthorizedException;
import com.esri.networkarchitecture.listeners.VenueCheckInListener;
import com.esri.networkarchitecture.listeners.VenueSearchListener;
import com.esri.networkarchitecture.models.TokenStore;
import com.esri.networkarchitecture.models.Venue;
import com.esri.networkarchitecture.models.VenueDetailResponse;
import com.esri.networkarchitecture.models.VenueSearchResponse;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import rx.android.schedulers.AndroidSchedulers;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by scotts on 10/19/15.
 */
public class DataManager {

  private static final String TAG = DataManager.class.getSimpleName();

  private static final String FOURSQUARE_ENDPOINT = "https://api.foursquare.com/v2";
  private static final String OAUTH_ENDPOINT = "https://foursquare.com/oauth2/authenticate";
  public static final String OAUTH_REDIRECT_URI = "http://www.bignerdranch.com";

  private static final String CLIENT_ID = "1UGKP22H5D3QK3WXWQMVG0Z2Y11TDARZONY34RY1NKUSDHAL";
  private static final String CLIENT_SECRET = "QQZDCMZCMOGGHBWBB25QN20JWYQ1KJCJSUOY1Z5J3WHFVDFW";
  private static final String FOURSQUARE_VERSION = "20150406";
  private static final String FOURSQUARE_MODE = "foursquare";
  private static final String SWARM_MODE = "swarm";
  private static final String TEST_LAT_LNG = "33.759,-84.332";

  private static DataManager sDataManager;
  private static TokenStore sTokenStore;
  private Context mContext;
  private RestAdapter mBasicRestAdapter;
  private RestAdapter mAuthenticatedRestAdapter;
  private List<Venue> mVenueList;
  private List<VenueSearchListener> mSearchListenerList;
  private List<VenueCheckInListener> mCheckInListenerList;

  protected DataManager(Context context, RestAdapter basicRestAdapter, RestAdapter authenticatedRestAdapter) {
    mContext = context;
    sTokenStore = new TokenStore(mContext);
    mBasicRestAdapter = basicRestAdapter;
    mAuthenticatedRestAdapter = authenticatedRestAdapter;
    mSearchListenerList = new ArrayList<>();
    mCheckInListenerList = new ArrayList<>();
  }

  //region Public methods...

  public static RequestInterceptor sRequestInterceptor = new RequestInterceptor() {
    @Override
    public void intercept(RequestFacade request) {
      request.addQueryParam("client_id", CLIENT_ID);
      request.addQueryParam("client_secret", CLIENT_SECRET);
      request.addQueryParam("v", FOURSQUARE_VERSION);
      request.addQueryParam("m", FOURSQUARE_MODE);
    }
  };

  private static RequestInterceptor sAuthenticatedRequestInterceptor = new RequestInterceptor() {
    @Override
    public void intercept(RequestFacade request) {
      request.addQueryParam("oauth_token", sTokenStore.getAccessToken());
      request.addQueryParam("v", FOURSQUARE_VERSION);
      request.addQueryParam("m", SWARM_MODE);
    }
  };

  public static DataManager get(Context context) {
    if (sDataManager == null) {
      Gson gson = new GsonBuilder()
          .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer())
          .registerTypeAdapter(VenueDetailResponse.class, new VenueDetailResponseDeserializer())
          .create();

      RestAdapter basicRestAdapter = new RestAdapter.Builder()
          .setEndpoint(FOURSQUARE_ENDPOINT)
          .setConverter(new GsonConverter(gson))
          .setLogLevel(RestAdapter.LogLevel.FULL)
          .setRequestInterceptor(sRequestInterceptor)
          .build();

      RestAdapter authenticatedRestAdapter = new RestAdapter.Builder()
          .setEndpoint(FOURSQUARE_ENDPOINT)
          .setConverter(new GsonConverter(gson))
          .setLogLevel(RestAdapter.LogLevel.FULL)
          .setRequestInterceptor(sAuthenticatedRequestInterceptor)
          .setErrorHandler(new RetrofitErrorHandler())
          .build();

      sDataManager = new DataManager(context.getApplicationContext(), basicRestAdapter, authenticatedRestAdapter);
    }

    return sDataManager;
  }

  public static DataManager get(Context context, RestAdapter basicRestAdapter, RestAdapter authenticatedRestAdapter) {
    if (sDataManager == null) {
      sDataManager = new DataManager(context.getApplicationContext(), basicRestAdapter, authenticatedRestAdapter);
    }

    return sDataManager;
  }

  public String getAuthenticationUrl() {
    return Uri.parse(OAUTH_ENDPOINT).buildUpon()
        .appendQueryParameter("client_id", CLIENT_ID)
        .appendQueryParameter("response_type", "token")
        .appendQueryParameter("redirect_uri", OAUTH_REDIRECT_URI)
        .build()
        .toString();
  }

  public List<Venue> getVenueList() {
    return mVenueList;
  }

  public Venue getVenue(String venueId) {
    for (Venue venue : mVenueList) {
      if (venue.getId().equals(venueId)) {
        return venue;
      }
    }
    return null;
  }

  public void addVenueSearchListener(VenueSearchListener listener) {
    mSearchListenerList.add(listener);
  }

  public void removeVenueSearchListener(VenueSearchListener listener) {
    mSearchListenerList.remove(listener);
  }

  public void addVenueCheckInListener(VenueCheckInListener listener) {
    mCheckInListenerList.add(listener);
  }

  public void removeVenueCheckInListener(VenueCheckInListener listener) {
    mCheckInListenerList.remove(listener);
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

  public void fetchVenueDetail(String venueId, final Callback<Venue> callback) {
    VenueInterface venueInterface = mBasicRestAdapter.create(VenueInterface.class);
    venueInterface.venueDetail(venueId, new Callback<VenueDetailResponse>() {
      @Override
      public void success(VenueDetailResponse venueDetailResponse, Response response) {
        callback.success(venueDetailResponse.getVenue(), response);
      }

      @Override
      public void failure(RetrofitError error) {
        Log.e(TAG, "Failed to fetch venue detail", error);
        callback.failure(error);
      }
    });
  }

  public void checkInToVenue(String venueId) {
    VenueInterface venueInterface = mAuthenticatedRestAdapter.create(VenueInterface.class);

//    // pre-Chapter 3 implementation
//    venueInterface.venueCheckIn(venueId, new Callback<Object>() {
//      @Override
//      public void success(Object o, Response response) {
//        notifyCheckInListeners();
//      }
//
//      @Override
//      public void failure(RetrofitError error) {
//        Log.e(TAG, "Failed to check in to venue", error);
//        if (error.getCause() instanceof UnauthorizedException) {
//          // clear token & prompt user to reauthorize
//          sTokenStore.setAccessToken(null);
//          notifyCheckInListenersTokenExpired();
//        }
//      }
//    });

//    // RxJava Observable implementation (Chapter 3 pg 56)
//    venueInterface.venueCheckIn(venueId)
//        .observeOn(AndroidSchedulers.mainThread())
//        .subscribe(new Action1<Object>() {
//          @Override
//          public void call(Object o) {
//            notifyCheckInListeners();
//          }
//        }, new Action1<Throwable>() {
//          @Override
//          public void call(Throwable throwable) {
//            Log.d("venueCheckIn", "Have error: " + throwable);
//            if (throwable instanceof UnauthorizedException) {
//              sTokenStore.setAccessToken(null);
//              notifyCheckInListenersTokenExpired();
//            }
//          }
//        });

    // RxJava/Retrolambda implementation (Chapter 3 pg 58)
    venueInterface.venueCheckIn(venueId)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
            result -> notifyCheckInListeners(),
            error -> handleCheckInException(error)
        );

  }

  //endregion Public methods...
  //region Private methods...

  private void notifySearchListeners() {
    for (VenueSearchListener listener : mSearchListenerList) {
      listener.onVenueSearchFinished();
    }
  }

  private void notifyCheckInListeners() {
    for (VenueCheckInListener listener : mCheckInListenerList) {
      listener.onVenueCheckInFinished();
    }
  }

  private void notifyCheckInListenersTokenExpired() {
    for (VenueCheckInListener listener : mCheckInListenerList) {
      listener.onTokenExpired();
    }
  }

  private void handleCheckInException(Throwable error) {
    if (error instanceof UnauthorizedException) {
      sTokenStore.setAccessToken(null);
      notifyCheckInListenersTokenExpired();
    }
  }

  //endregion Private methods...

}
