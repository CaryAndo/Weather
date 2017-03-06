package codes.cary.weather.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import codes.cary.weather.R;
import codes.cary.weather.fragments.PlaceListFragment;
import codes.cary.weather.model.Place;

/**
 * A RecyclerView adapter to manage {@link Place}'s
 */

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {
    private List<Place> mItems;
    private Set<Place> mUpdatingSet; // Set of items that are currently updating
    private PlaceListFragment.PlaceListFragmentCallbacks mListener;

    public PlaceAdapter(List<Place> places, PlaceListFragment.PlaceListFragmentCallbacks callbacks) {
        this.mItems = places;
        this.mListener = callbacks;
        this.mUpdatingSet = new HashSet<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.fragment_place_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickPlace(mItems.get(holder.getAdapterPosition()));
            }
        });

        if (mUpdatingSet.contains(mItems.get(position))) {
            holder.mProgressBar.setVisibility(View.VISIBLE);
            holder.mWeatherTextView.setVisibility(View.GONE);
        } else {
            holder.mProgressBar.setVisibility(View.GONE);
            holder.mWeatherTextView.setVisibility(View.VISIBLE);
        }

        holder.mNameTextView.setText(mItems.get(position).getZipCode());
        holder.mWeatherTextView.setText(mItems.get(position).getWeather());
    }

    /**
     * Set the spinner bar on an item that is currently refreshing
     * @param place
     */
    public void setItemUpdating(Place place) {
        int index = mItems.indexOf(place);

        if (index > -1) {
            //Place place = mItems.get(index);
            mUpdatingSet.add(place);
            notifyItemChanged(index);
        }
    }

    /**
     * Remove spinner from item that has finished updating
     * @param place
     */
    public void itemFinishedUpdating(Place place) {
        mUpdatingSet.remove(place);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private View mView;
        private TextView mNameTextView;
        private TextView mWeatherTextView;
        private ProgressBar mProgressBar;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            mNameTextView = (TextView) mView.findViewById(R.id.place_name_textview);
            mWeatherTextView = (TextView) mView.findViewById(R.id.place_weather_textview);
            mProgressBar = (ProgressBar) mView.findViewById(R.id.place_progressbar);
        }
    }
}
