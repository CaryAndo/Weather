package codes.cary.weather.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import codes.cary.weather.R;

/**
 * Created by cary on 3/3/17.
 */

public abstract class BaseActivity extends Activity {
    public abstract String getTag();
    private AlertDialog mAlertDialog;

    protected ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logMethodName("OnCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        logMethodName("onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logMethodName("OnRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logMethodName("OnResume()");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        logMethodName("OnPostResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logMethodName("OnPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        logMethodName("OnStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        logMethodName("OnDestroy()");
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        logMethodName("onLowMemory()");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        logMethodName("OnBackPressed()");
    }

    /**
     * Log which lifecycle event is being called and by whom
     *
     * @param methodName The name of the callback
     */
    private void logMethodName(String methodName) {
        Log.d(getTag(), ">>>>>>>> " + methodName + " in activity: " + getTag() + " <<<<<<<<<<");
    }

    /**
     * Show an error to the user
     *
     * @param message The message to show
     */
    public void showErrorDialog(String message) {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            return;
        }

        mAlertDialog = new AlertDialog.Builder(this).create();
        mAlertDialog.setTitle("Error");
        mAlertDialog.setMessage(Html.fromHtml(message));
        mAlertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        mAlertDialog.show();
    }

}
