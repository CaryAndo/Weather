package codes.cary.weather.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import codes.cary.weather.R;
import codes.cary.weather.model.Place;

/**
 */
public class PlaceDetailFragment extends BaseFragment {
    private static final String TAG = PlaceDetailFragment.class.getCanonicalName();

    private DetailFragmentCallbacks mListener;
    private Place mPlace;

    private TextView mPlaceInfoTextView;
    private TextView mCoordinatesTextView;
    private TextView mTemperatureTextView;
    private TextView mWeatherTextView;
    private TextView mWindTextView;

    public PlaceDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PlaceDetailFragment.
     */
    public static PlaceDetailFragment newInstance(Place place) {
        PlaceDetailFragment fragment = new PlaceDetailFragment();
        Bundle args = new Bundle();
        fragment.setPlace(place);
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
        View view = inflater.inflate(R.layout.fragment_place_detail, container, false);

        this.mPlaceInfoTextView = (TextView) view.findViewById(R.id.detail_place_info);
        this.mCoordinatesTextView = (TextView) view.findViewById(R.id.detail_coordinates);
        this.mTemperatureTextView = (TextView) view.findViewById(R.id.detail_temperature);
        this.mWeatherTextView = (TextView) view.findViewById(R.id.detail_weather);
        this.mWindTextView = (TextView) view.findViewById(R.id.detail_wind);

        initUI();

        return view;
    }

    private void initUI() {
        try {
            JSONObject weatherObject = new JSONObject(mPlace.getWeatherJSON());
            mPlaceInfoTextView.setText(weatherObject.getString("name"));
            String lat = weatherObject.getJSONObject("coord").getString("lat");
            String lon = weatherObject.getJSONObject("coord").getString("lon");
            mCoordinatesTextView.setText("Lat: " + lat + " Lon: " + lon);
            JSONObject temperatureObject = weatherObject.getJSONObject("main");
            double doubleTemp = Double.parseDouble(temperatureObject.getString("temp"));
            int temp = (int) Math.round((doubleTemp-273.15)*(9.0/5.0)+32.0); // Kelvin -> Fahrenheit
            mTemperatureTextView.setText(temp + "° F");
            String weatherDescription = ((JSONObject) weatherObject.getJSONArray("weather").get(0)).getString("description");
            mWeatherTextView.setText(weatherDescription);
            String windInfo = "Wind speed: " + weatherObject.getJSONObject("wind").getString("speed") + " meters/second";
            mWindTextView.setText(windInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DetailFragmentCallbacks) {
            mListener = (DetailFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement DetailFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setPlace(Place place) {
        mPlace = place;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface DetailFragmentCallbacks {
    }
}
