package codes.cary.weather.callbacks;

import android.content.Intent;

/**
 * Callbacks for when the {@link codes.cary.weather.service.WeatherApiService} has finished fetching
 */

public interface ApiCallbacks {

    void onApiRequestComplete(Intent intent);

    void onApiRequestFailed(Intent intent);
}
