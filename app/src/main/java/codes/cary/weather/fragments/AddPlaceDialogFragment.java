package codes.cary.weather.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import codes.cary.weather.R;
import codes.cary.weather.callbacks.DialogClickCallbacks;
import codes.cary.weather.model.Place;

/**
 * Created by cary on 3/5/17.
 */

public class AddPlaceDialogFragment extends DialogFragment {

    private DialogClickCallbacks mClickCallbacks;
    private View mView;
    private EditText mZipCodeTextView;

    public static AddPlaceDialogFragment newInstance(DialogClickCallbacks callbacks) {
        AddPlaceDialogFragment fragment = new AddPlaceDialogFragment();
        fragment.setClickCallbacks(callbacks);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

        dialogBuilder.setTitle("Add place");

        LayoutInflater inflater = getActivity().getLayoutInflater();
        this.mView = inflater.inflate(R.layout.place_dialog_fragment, null);
        this.mZipCodeTextView = (EditText) mView.findViewById(R.id.zip_code_textview);

        dialogBuilder.setView(mView)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // TODO: Do some input validation
                        if (!TextUtils.isEmpty(mZipCodeTextView.getText().toString())) {
                            mClickCallbacks.onClickAdd(new Place(mZipCodeTextView.getText().toString()));
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mClickCallbacks.onClickCancel();
                        dismiss();
                    }
                });

        return dialogBuilder.create();
    }

    public void setClickCallbacks(DialogClickCallbacks callbacks) {
        this.mClickCallbacks = callbacks;
    }
}
