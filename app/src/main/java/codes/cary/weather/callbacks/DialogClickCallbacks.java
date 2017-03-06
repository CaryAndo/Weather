package codes.cary.weather.callbacks;

import codes.cary.weather.model.Place;

/**
 * Callbacks for when the user clicks the buttons in a DialogFragment to add a Place
 */

public interface DialogClickCallbacks {

    void onClickAdd(Place place);

    void onClickCancel();
}
