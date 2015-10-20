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

import com.esri.networkarchitecture.BuildConfig;
import com.esri.networkarchitecture.exceptions.UnauthorizedException;
import com.esri.networkarchitecture.listeners.VenueCheckInListener;
import com.esri.networkarchitecture.listeners.VenueSearchListener;
import com.esri.networkarchitecture.models.TokenStore;
import com.esri.networkarchitecture.models.Venue;
import com.esri.networkarchitecture.models.VenueSearchResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import retrofit.Callback;
import retrofit.RestAdapter;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by scotts on 10/20/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class DataManagerTest {

  @Captor
  private ArgumentCaptor<Callback<VenueSearchResponse>> searchCaptor;
  private DataManager mDataManager;
  private RestAdapter mBasicRestAdapter;
  private RestAdapter mAuthenticatedRestAdapter;
  private VenueInterface mVenueInterface;
  private VenueSearchListener mVenueSearchListener;
  private VenueCheckInListener mVenueCheckInListener;

  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
    mBasicRestAdapter = mock(RestAdapter.class);
    mAuthenticatedRestAdapter = mock(RestAdapter.class);
    mDataManager = new DataManager(RuntimeEnvironment.application, mBasicRestAdapter, mAuthenticatedRestAdapter);

    mVenueInterface = mock(VenueInterface.class);
    when(mBasicRestAdapter.create(VenueInterface.class))
        .thenReturn(mVenueInterface);
    when(mAuthenticatedRestAdapter.create(VenueInterface.class))
        .thenReturn(mVenueInterface);

    mVenueSearchListener = mock(VenueSearchListener.class);
    mDataManager.addVenueSearchListener(mVenueSearchListener);

    mVenueCheckInListener = mock(VenueCheckInListener.class);
    mDataManager.addVenueCheckInListener(mVenueCheckInListener);
  }

  @Test
  public void searchListenerTriggeredOnSuccessfulSearch() {
    mDataManager.fetchVenueSearch();

    verify(mVenueInterface).venueSearch(anyString(), searchCaptor.capture());

    VenueSearchResponse response = mock(VenueSearchResponse.class);
    searchCaptor.getValue().success(response, null);

    verify(mVenueSearchListener).onVenueSearchFinished();
  }

  @Test
  public void venueSearchListSavedOnSuccessfulSearch() {
    mDataManager.fetchVenueSearch();

    verify(mVenueInterface).venueSearch(anyString(), searchCaptor.capture());

    String firstVenueName = "Cool first venue";
    Venue firstVenue = mock(Venue.class);
    when(firstVenue.getName()).thenReturn(firstVenueName);

    String secondVenueName = "Awesome second venue";
    Venue secondVenue = mock(Venue.class);
    when(secondVenue.getName()).thenReturn(secondVenueName);

    List<Venue> venueList = new ArrayList<>();
    venueList.add(firstVenue);
    venueList.add(secondVenue);

    VenueSearchResponse response = mock(VenueSearchResponse.class);
    when(response.getVenueList()).thenReturn(venueList);

    searchCaptor.getValue().success(response, null);

    List<Venue> dataManagerVenueList = mDataManager.getVenueList();
    assertThat(dataManagerVenueList, is(equalTo(venueList)));
  }

  @Test
  public void checkInListenerTriggeredOnSuccessfulCheckIn() {
    Observable<Object> successObservable = Observable.just(new Object());
    when(mVenueInterface.venueCheckIn(anyString())).thenReturn(successObservable);

    String fakeVenueId = "fakeVenueId";
    mDataManager.checkInToVenue(fakeVenueId);

    verify(mVenueCheckInListener).onVenueCheckInFinished();
  }

  @Test
  public void checkInListenerNotifiesTokenExpiredOnUnauthorizedException() {
    Observable<Object> unauthorizedObservable = Observable.error(new UnauthorizedException(null));
    when(mVenueInterface.venueCheckIn(anyString()))
        .thenReturn(unauthorizedObservable);

    String fakeVenueId = "fakeVenueId";
    mDataManager.checkInToVenue(fakeVenueId);

    verify(mVenueCheckInListener).onTokenExpired();
  }

  @Test
  public void checkInListenerDoesNotNotifyTokenExpiredOnPlainException() {
    Observable<Object> runtimeObservable = Observable.error(new RuntimeException());
    when(mVenueInterface.venueCheckIn(anyString()))
        .thenReturn(runtimeObservable);

    String fakeVenueId = "fakeVenueId";
    mDataManager.checkInToVenue(fakeVenueId);

    verify(mVenueCheckInListener, never()).onTokenExpired();
  }

  @Test
  public void tokenClearedFromTokenStoreOnUnauthorizedException() {
    String testToken = "asdf1234";
    TokenStore tokenStore = new TokenStore(RuntimeEnvironment.application);
    tokenStore.setAccessToken(testToken);
    assertThat(tokenStore.getAccessToken(), is(equalTo(testToken)));

    Observable<Object> unauthorizedObservable = Observable.error(new UnauthorizedException(null));
    when(mVenueInterface.venueCheckIn(anyString()))
        .thenReturn(unauthorizedObservable);

    String fakeVenueId = "asdf1234";
    mDataManager.checkInToVenue(fakeVenueId);

    assertThat(tokenStore.getAccessToken(), is(equalTo(null)));
    assertNull(tokenStore.getAccessToken());
  }

}
