package codes.cary.weather.service;

/**
 * Created by cary on 3/3/17.
 */

public class ApiException extends Exception {

    public ApiException(String message){
        super(message);
    }
}
