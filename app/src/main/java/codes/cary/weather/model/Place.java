package codes.cary.weather.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * POJO model object representing a place and some associated weather
 */

public class Place implements Serializable {
    private String mZipCode;
    private Long mUpdatedAtTimeStamp;
    private String mWeather;

    public Place(String zipCode) {
        this.mZipCode = zipCode;
        this.mUpdatedAtTimeStamp = 0L;
        this.mWeather = "";
    }

    public String getZipCode() {
        return mZipCode;
    }

    public void setZipCode(String zipCode) {
        mZipCode = zipCode;
    }

    public Long getUpdatedAtTimeStamp() {
        return mUpdatedAtTimeStamp;
    }

    public void setUpdatedAtTimeStamp(Long updatedAtTimeStamp) {
        mUpdatedAtTimeStamp = updatedAtTimeStamp;
    }

    // TODO: Refactor this so that the JSON keys are constants

    /**
     * Get a nice parsed weather string formatted as "Temperature in F : weather description"
     * This is the summary of the weather that the user should see
     *
     * @return a nicely formatted string describing the weather
     */
    public String getWeather() {
        try {
            JSONObject weatherJSON = new JSONObject(mWeather);
            JSONObject weatherObject = weatherJSON.getJSONArray("weather").getJSONObject(0);
            JSONObject temperatureObject = weatherJSON.getJSONObject("main");

            double doubleTemp = Double.parseDouble(temperatureObject.getString("temp"));
            int temp = (int) Math.round((doubleTemp-273.15)*(9.0/5.0)+32.0); // Kelvin -> Fahrenheit
            return temp + "° F : " + weatherObject.getString("description");
            //return weather;
        } catch (JSONException e) {
            return mWeather;
        }
    }

    /**
     * Get the un-parsed JSON string that the API sent
     * @return a JSON string
     */
    public String getWeatherJSON() {
        return mWeather;
    }

    public void setWeather(String weather) {
        this.mWeather = weather;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Place place = (Place) o;

        // We only care about zip code when it comes to equality
        return mZipCode != null ? mZipCode.equals(place.mZipCode) : place.mZipCode == null;
    }

    @Override
    public int hashCode() {
        return mZipCode != null ? mZipCode.hashCode() : 0;
    }
}
