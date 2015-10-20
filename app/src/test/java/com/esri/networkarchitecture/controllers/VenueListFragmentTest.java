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
package com.esri.networkarchitecture.controllers;

import android.support.v4.BuildConfig;
import android.support.v7.widget.RecyclerView;
import com.esri.networkarchitecture.R;
import com.esri.networkarchitecture.SynchronousExecutor;
import com.esri.networkarchitecture.VenueListActivity;
import com.esri.networkarchitecture.VenueListActivityFragment;
import com.esri.networkarchitecture.models.VenueSearchResponse;
import com.esri.networkarchitecture.web.DataManager;
import com.esri.networkarchitecture.web.VenueListDeserializer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * Created by scotts on 10/20/15.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(sdk = 21, constants = BuildConfig.class)
public class VenueListFragmentTest {
  @Rule
  public WireMockRule mWireMockRule = new WireMockRule(1111);

  private String mEndpoint = "http://localhost:1111/";

  private DataManager mDataManager;

  private VenueListActivity mVenueListActivity;

  private VenueListActivityFragment mVenueListFragment;

  @Before
  public void setup() {
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(VenueSearchResponse.class, new VenueListDeserializer())
        .create();

    SynchronousExecutor executor = new SynchronousExecutor();
    RestAdapter basicRestAdapter = new RestAdapter.Builder()
        .setEndpoint(mEndpoint)
        .setConverter(new GsonConverter(gson))
        .setExecutors(executor, executor)
        .build();

    mDataManager = DataManager.get(RuntimeEnvironment.application, basicRestAdapter, null);

    // stub out the venue search response
    stubFor(get(urlMatching("/venues/search.*"))
        .willReturn(aResponse()
            .withStatus(200)
            .withBodyFile("search.json")));

    // setup the VenueListActivity/VenueListActivityFragment to test the UI
    mVenueListActivity = Robolectric.buildActivity(VenueListActivity.class)
        .create().start().resume().get();

    mVenueListFragment = (VenueListActivityFragment) mVenueListActivity
        .getSupportFragmentManager()
        .findFragmentById(R.id.fragment_container);
  }

  @Test
  public void activityListsVenuesReturnedFromSearch() {
    assertThat(mVenueListFragment, is(notNullValue()));

    RecyclerView venueRecyclerView = (RecyclerView) mVenueListFragment
        .getView()
        .findViewById(R.id.venueListRecyclerView);

    assertThat(venueRecyclerView, is(notNullValue()));
    assertThat(venueRecyclerView.getAdapter().getItemCount(), is(2));
  }

}
