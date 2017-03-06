package codes.cary.weather.fragments;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import codes.cary.weather.R;
import codes.cary.weather.adapters.PlaceAdapter;
import codes.cary.weather.callbacks.ApiBroadcastReceiver;
import codes.cary.weather.callbacks.ApiCallbacks;
import codes.cary.weather.callbacks.ContainerInteractionCallbacks;
import codes.cary.weather.callbacks.DialogClickCallbacks;
import codes.cary.weather.model.Place;
import codes.cary.weather.service.WeatherApiService;
import codes.cary.weather.utils.StorageUtils;

/**
 */
public class PlaceListFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<List<Place>>, ApiCallbacks {
    private static final String TAG = PlaceListFragment.class.getCanonicalName();

    private PlaceListFragmentCallbacks mListener;

    private ApiBroadcastReceiver mReceiver;
    private IntentFilter mFilter;

    private List<Place> mItems;
    private SwipeRefreshLayout mRefreshLayout;
    private RecyclerView mRecyclerView;
    private PlaceAdapter mAdapter;
    private FloatingActionButton mFab;
    private AddPlaceDialogFragment mAddPlaceDialogFragment;

    public PlaceListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlaceListFragment.
     */
    public static PlaceListFragment newInstance() {
        PlaceListFragment fragment = new PlaceListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_list, container, false);

        this.mItems = new ArrayList<>();
        this.mRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.list_refreshlayout);
        this.mRecyclerView = (RecyclerView) view.findViewById(R.id.place_list_recyclerview);
        this.mAdapter = new PlaceAdapter(mItems, mListener); // Let the Activity deal with clicks
        this.mFab = (FloatingActionButton) view.findViewById(R.id.place_list_fab);

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mItems.size() <= 0) {
                    mRefreshLayout.setRefreshing(false);
                    return;
                }

                for (Place place : mItems) {
                    place.setUpdatedAtTimeStamp(0L);
                }

                updateAllWeather();
            }
        });

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogClickCallbacks clickCallbacks = new DialogClickCallbacks() {
                    @Override
                    public void onClickAdd(Place place) {
                        if (!mItems.contains(place)) {
                            mItems.add(place);
                            int index = mItems.indexOf(place);
                            mAdapter.notifyItemInserted(index);
                            mAdapter.setItemUpdating(place);
                            WeatherApiService.getWeather(getActivity(), place.getZipCode());
                        }
                    }

                    @Override
                    public void onClickCancel() {
                        Log.d(getTagName(), "Add dialog canceled");
                    }
                };

                mAddPlaceDialogFragment = AddPlaceDialogFragment.newInstance(clickCallbacks);
                mAddPlaceDialogFragment.show(getFragmentManager(), "whatever");
            }
        });

        getLoaderManager().initLoader(0, null, this).forceLoad();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        mReceiver = new ApiBroadcastReceiver(this);
        mFilter = new IntentFilter();
        mFilter.addAction(WeatherApiService.ACTION_SUCCESS);
        mFilter.addAction(WeatherApiService.ACTION_FAILURE);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, mFilter);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mReceiver != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        }

        String fileName = getActivity().getResources().getString(R.string.list_file_name);
        StorageUtils.writeSerializableToFile(getActivity(), fileName, (Serializable) mItems);
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PlaceListFragmentCallbacks) {
            mListener = (PlaceListFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PlaceListFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<List<Place>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Place>>(getActivity()) {
            @Override
            public List<Place> loadInBackground() {
                String fileName = getActivity().getResources().getString(R.string.list_file_name);

                try {
                    List<Place> list = (List<Place>) StorageUtils.readSerializableFromFile(getActivity(), fileName);
                    return list;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Place>> loader, List<Place> data) {
        if (mItems == null) {
            mItems = new ArrayList<>();
        }

        mItems.clear();
        mAdapter.notifyDataSetChanged();

        if (data != null) {
            mItems.addAll(data);
            mAdapter.notifyDataSetChanged();
            /*for (Place place : data) {
                mItems.add(place);
                mAdapter.notifyItemInserted(mItems.indexOf(place)); // DefaultItemAnimator will give us some free animations
            }*/
        } else {
            // Items array is empty and there were no saved places, auto-populate with three places
            mItems.add(new Place("92801"));
            mAdapter.notifyItemInserted(0); // Animate

            mItems.add(new Place("90210"));
            mAdapter.notifyItemInserted(1); // Animate

            mItems.add(new Place("78727"));
            mAdapter.notifyItemInserted(2); // Animate
        }

        updateAllWeather();
    }

    private void updateAllWeather() {
        for (Place place : mItems) {
            long rightNow = System.currentTimeMillis();

            // If the weather here was updated more than a half hour ago, update the places' weather
            if (rightNow - place.getUpdatedAtTimeStamp() > 1800000) {
                mAdapter.setItemUpdating(place);
                WeatherApiService.getWeather(getActivity(), place.getZipCode());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Place>> loader) {
        Log.d(getTag(), "Loader reset");
    }

    @Override
    public void onApiRequestComplete(Intent intent) {
        String zipCode = intent.getStringExtra(WeatherApiService.ZIP_CODE);
        String weather = intent.getStringExtra(WeatherApiService.JSON_RESPONSE);

        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }

        if (!TextUtils.isEmpty(zipCode)) {
            Place tempPlace = new Place(zipCode);
            int index = mItems.indexOf(tempPlace);
            mItems.get(index).setWeather(weather);
            mItems.get(index).setUpdatedAtTimeStamp(System.currentTimeMillis());
            mAdapter.itemFinishedUpdating(mItems.get(index));
            mAdapter.notifyItemChanged(index);
        } else {
            // This shouldn't happen
        }
    }

    @Override
    public void onApiRequestFailed(Intent intent) {
        String zipCode = intent.getStringExtra(WeatherApiService.ZIP_CODE);
        String message = intent.getStringExtra(WeatherApiService.MESSAGE);
        Log.e(getTagName(), "Failed " + message);

        if (mRefreshLayout.isRefreshing()) {
            mRefreshLayout.setRefreshing(false);
        }

        mListener.showErrorDialog("Failed to fetch weather for zip code: " + zipCode);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface PlaceListFragmentCallbacks extends ContainerInteractionCallbacks {
        void onClickPlace(Place place);
    }
}
