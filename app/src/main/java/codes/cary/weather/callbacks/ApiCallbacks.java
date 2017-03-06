package codes.cary.weather.callbacks;

import android.content.Intent;

/**
 * Created by cary on 3/4/17.
 */

public interface ApiCallbacks {

    void onApiRequestComplete(Intent intent);

    void onApiRequestFailed(Intent intent);
}
