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
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import com.esri.networkarchitecture.models.Venue;

import java.util.List;

/**
 * Created by scotts on 10/19/15.
 */
public class VenueListAdapter extends RecyclerView.Adapter<VenueHolder> {

  private Context mContext;
  private List<Venue> mVenueList;

  public VenueListAdapter(Context context, List<Venue> venueList) {
    mContext = context;
    mVenueList = venueList;
  }

  public void setVenueList(List<Venue> venueList) {
    mVenueList = venueList;
    notifyDataSetChanged();
  }

  @Override
  public VenueHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    VenueView venueView = new VenueView(mContext);
    return new VenueHolder(venueView);
  }

  @Override
  public void onBindViewHolder(VenueHolder venueHolder, int position) {
    venueHolder.bindVenue(mVenueList.get(position));
  }

  @Override
  public int getItemCount() {
    return mVenueList.size();
  }
}
