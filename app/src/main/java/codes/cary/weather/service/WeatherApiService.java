package codes.cary.weather.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.io.IOException;

import codes.cary.weather.R;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by cary on 3/3/17.
 */

public class WeatherApiService extends IntentService {
    private static final String TAG = WeatherApiService.class.getCanonicalName();

    public static final String ACTION_SUCCESS = "codes.cary.weather.ACTION_SUCCESS";
    public static final String ACTION_FAILURE = "codes.cary.weather.ACTION_FAILURE";

    public static String MESSAGE = "codes.cary.weather.MESSAGE";
    public static String JSON_RESPONSE = "codes.cary.weather.JSON_RESPONSE";
    public static String ZIP_CODE = "codes.cary.weather.ZIP_CODE";

    private String mZipCode;

    public static void getWeather(Context context, String zipCode) {
        Intent getIntent = new Intent();
        getIntent.putExtra(ZIP_CODE, zipCode);
        getIntent.setClass(context, WeatherApiService.class);
        context.startService(getIntent);
    }

    /**
     * Creates an IntentService.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public WeatherApiService(String name) {
        super(name);
    }

    /**
     * Creates an IntentService.
     */
    public WeatherApiService() {
        super("ApiService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mZipCode = intent.getStringExtra(ZIP_CODE);
        String apiKey = getResources().getString(R.string.weather_api_key);
        String baseUrl = getResources().getString(R.string.weather_api_base_url);

        String url = baseUrl + "zip=" + mZipCode + ",us&APPID=" + apiKey;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            if (response.isSuccessful()) {
                broadcast(WeatherApiService.ACTION_SUCCESS, null, body);
            } else {
                broadcast(WeatherApiService.ACTION_FAILURE, "Server error: " + body, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            broadcast(WeatherApiService.ACTION_FAILURE, "Error communicating with the server", null);
        }
    }

    /**
     * Util method to broadcast the result of an API request
     *
     * @param action the intent action
     * @param message a message
     */
    protected void broadcast(String action,
                             @Nullable String message,
                             @Nullable String jsonResponse) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(MESSAGE, message);
        intent.putExtra(ZIP_CODE, mZipCode);
        intent.putExtra(JSON_RESPONSE, jsonResponse);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public String getTag() {
        return TAG;
    }
}
