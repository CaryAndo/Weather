package codes.cary.weather.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import codes.cary.weather.R;
import codes.cary.weather.fragments.PlaceDetailFragment;
import codes.cary.weather.fragments.PlaceListFragment;
import codes.cary.weather.model.Place;


/**
 * Entry point for the app
 */
public class MainActivity extends BaseActivity implements
        PlaceListFragment.PlaceListFragmentCallbacks,
        PlaceDetailFragment.DetailFragmentCallbacks {

    private static final String TAG = MainActivity.class.getCanonicalName();

    private FragmentManager mFragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mFragmentManager = getFragmentManager();

        mFragmentManager
                .beginTransaction()
                .replace(R.id.main_activity_container, PlaceListFragment.newInstance())
                .commit();
    }

    @Override
    public void onClickPlace(Place place) {
        mFragmentManager
                .beginTransaction()
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.main_activity_container, PlaceDetailFragment.newInstance(place))
                .commit();
    }

    @Override
    public String getTag() {
        return TAG;
    }
}
