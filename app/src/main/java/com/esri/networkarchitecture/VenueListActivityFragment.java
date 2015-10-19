package com.esri.networkarchitecture;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.esri.networkarchitecture.listeners.VenueSearchListener;
import com.esri.networkarchitecture.models.Venue;
import com.esri.networkarchitecture.views.VenueListAdapter;
import com.esri.networkarchitecture.web.DataManager;

import java.util.Collections;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class VenueListActivityFragment extends Fragment implements VenueSearchListener {

  private RecyclerView mRecyclerView;
  private VenueListAdapter mVenueListAdapter;
  private DataManager mDataManager;
  private List<Venue> mVenueList;

  public VenueListActivityFragment() {
  }

  @Override
  public void onStart() {
    super.onStart();
    mDataManager = DataManager.get(getActivity());
    mDataManager.addVenueSearchListener(this);
    mDataManager.fetchVenueSearch();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_venue_list, container, false);

    mRecyclerView = (RecyclerView) view.findViewById(R.id.venueListRecyclerView);
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
    mRecyclerView.setLayoutManager(layoutManager);
    mVenueListAdapter = new VenueListAdapter(getActivity(), Collections.<Venue>emptyList());
    mRecyclerView.setAdapter(mVenueListAdapter);

    return view;
  }

  @Override
  public void onStop() {
    super.onStop();
    mDataManager.removeVenueSearchListener(this);
  }

  //region VenueSearchListener overrides

  @Override
  public void onVenueSearchFinished() {
    mVenueList = mDataManager.getVenueList();
    mVenueListAdapter.setVenueList(mVenueList);
  }

  //endregion VenueSearchListener overrides

}
