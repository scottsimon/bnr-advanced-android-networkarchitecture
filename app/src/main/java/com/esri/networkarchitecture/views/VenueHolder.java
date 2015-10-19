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
package com.esri.networkarchitecture.views;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.esri.networkarchitecture.controllers.VenueDetailActivity;
import com.esri.networkarchitecture.models.Venue;

/**
 * Created by scotts on 10/19/15.
 */
public class VenueHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

  private VenueView mVenueView;
  private Venue mVenue;

  public VenueHolder(View itemView) {
    super(itemView);

    itemView.setOnClickListener(this);
    mVenueView = (VenueView) itemView;
  }

  public void bindVenue(Venue venue) {
    mVenue = venue;
    mVenueView.setVenueTitle(mVenue.getName());
    mVenueView.setVenueAddress(mVenue.getFormattedAddress());
  }

  @Override
  public void onClick(View v) {
    Context context = v.getContext();
    Intent intent = VenueDetailActivity.newIntent(context, mVenue.getId());
    context.startActivity(intent);
  }
}
