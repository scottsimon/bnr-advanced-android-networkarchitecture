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

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.esri.networkarchitecture.R;
import com.esri.networkarchitecture.listeners.VenueCheckInListener;
import com.esri.networkarchitecture.models.Price;
import com.esri.networkarchitecture.models.TokenStore;
import com.esri.networkarchitecture.models.Venue;
import com.esri.networkarchitecture.models.VenueStats;
import com.esri.networkarchitecture.web.DataManager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by scotts on 10/19/15.
 */
public class VenueDetailFragment extends Fragment implements VenueCheckInListener {

  private static final String TAG = VenueDetailFragment.class.getSimpleName();

  private static final String ARG_VENUE_ID = "VenueDetailFragment.VenueId";
  private static final String EXPIRED_DIALOG = "expired_dialog";

  private TextView mVenueNameTextView;
  private TextView mVenueAddressTextView;
  private Button mCheckInButton;

  private DataManager mDataManager;
  private TokenStore mTokenStore;
  private String mVenueId;
  private Venue mVenue;

  private View.OnClickListener mCheckInClickListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      mDataManager.checkInToVenue(mVenueId);
    }
  };

  public static VenueDetailFragment newInstance(String venueId) {
    VenueDetailFragment fragment = new VenueDetailFragment();

    Bundle args = new Bundle();
    args.putString(ARG_VENUE_ID, venueId);
    fragment.setArguments(args);

    return fragment;
  }

  //region Fragment overrides

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_venue_detail, container, false);
    mVenueNameTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_VenueNameTextView);
    mVenueAddressTextView = (TextView) view.findViewById(R.id.fragment_venue_detail_VenueAddressTextView);
    mCheckInButton = (Button) view.findViewById(R.id.fragment_venue_detail_CheckInButton);

    mTokenStore = new TokenStore(getActivity());

    return view;
  }

  @Override
  public void onStart() {
    super.onStart();

    mVenueId = getArguments().getString(ARG_VENUE_ID);
    mDataManager = DataManager.get(getActivity());
    mDataManager.addVenueCheckInListener(this);

    mVenue = mDataManager.getVenue(mVenueId);
  }

  @Override
  public void onStop() {
    super.onStop();

    mDataManager.removeVenueCheckInListener(this);
  }

  @Override
  public void onResume() {
    super.onResume();

    mVenueNameTextView.setText(mVenue.getName());
    mVenueAddressTextView.setText(mVenue.getFormattedAddress());
    if (mTokenStore.getAccessToken() != null) {
      mCheckInButton.setVisibility(View.VISIBLE);
      mCheckInButton.setOnClickListener(mCheckInClickListener);
    }

    // Chapter 2 Challenge
    mDataManager.fetchVenueDetail(mVenue.getId(), new Callback<Venue>() {
      @Override
      public void success(Venue venue, Response response) {
        if (VenueDetailFragment.this.isAdded()) {
          updateVenueDetails(venue);
        } else {
          Log.d(TAG, "Finished fetching details but fragment is not added");
        }
      }

      @Override
      public void failure(RetrofitError error) {
        // error...
      }
    });

  }

  //endregion Fragment overrides
  //region VenueCheckInListener overrides

  @Override
  public void onTokenExpired() {
    mCheckInButton.setVisibility(View.GONE);
    ExpiredTokenDialogFragment dialogFragment = new ExpiredTokenDialogFragment();
    dialogFragment.show(getFragmentManager(), EXPIRED_DIALOG);
  }

  @Override
  public void onVenueCheckInFinished() {
    Toast.makeText(getActivity(), R.string.successful_check_in_message, Toast.LENGTH_SHORT)
        .show();
  }

  //endregion VenueCheckInListener overrides
  //region Private methods

  private void updateVenueDetails(Venue venue) {
    Price price = venue.getPrice();
    if (price != null) {
      Log.d(TAG, String.format("price tier: %d", price.getTier()));
      Log.d(TAG, String.format("price message: %s", price.getMessage()));
    }

    VenueStats stats = venue.getStats();
    if (stats != null) {
      Log.d(TAG, String.format("Checkins: %d", stats.getCheckinsCount()));
      Log.d(TAG, String.format("Users:    %d", stats.getUsersCount()));
      Log.d(TAG, String.format("Tips:     %d", stats.getTipCount()));
    }

  }

  //endregion Private methods

}
