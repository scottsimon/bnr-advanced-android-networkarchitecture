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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.esri.networkarchitecture.R;

/**
 * Created by scotts on 10/19/15.
 */
public class VenueDetailActivity extends AppCompatActivity {

  private static final String ARG_VENUE_ID = "VenueDetailActivity.VenueId";

  public static Intent newIntent(Context context, String venueId) {
    Intent intent = new Intent(context, VenueDetailActivity.class);
    intent.putExtra(ARG_VENUE_ID, venueId);
    return intent;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    String venueId = getIntent().getStringExtra(ARG_VENUE_ID);
    setContentView(R.layout.activity_venue_detail);
    getSupportFragmentManager().beginTransaction()
        .add(R.id.fragment_container, VenueDetailFragment.newInstance(venueId))
        .commit();
  }
}
