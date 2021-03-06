package codes.cary.weather.callbacks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import codes.cary.weather.service.WeatherApiService;

/**
 * A {@link BroadcastReceiver} to receive messages from the {@link WeatherApiService}
 */

public class ApiBroadcastReceiver extends BroadcastReceiver {

    private ApiCallbacks mCallbacks;

    public ApiBroadcastReceiver(ApiCallbacks callbacks) {
        this.mCallbacks = callbacks;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (WeatherApiService.ACTION_SUCCESS.equals(intent.getAction())) {
            mCallbacks.onApiRequestComplete(intent);
        } else if (WeatherApiService.ACTION_FAILURE.equals(intent.getAction())) {
            mCallbacks.onApiRequestFailed(intent);
        }
    }
}
