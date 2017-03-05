package dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.deaspostudios.devchats.Constants;
import com.deaspostudios.devchats.R;

import static com.deaspostudios.devchats.MainActivity.mStatus;
import static com.deaspostudios.devchats.MainActivity.userpreferences;
import static com.deaspostudios.devchats.MyPreferenceActivity.updateUser;
import static com.deaspostudios.devchats.SettingsActivity.refreshStatusImage;

/**
 * Created by polyc on 04/03/2017.
 */

public class EditStatusDialog extends DialogFragment {
    String currentStatus;
    EditText editStatus;

    public static EditStatusDialog newInstance(String currentStatus) {
        EditStatusDialog editStatusDialog = new EditStatusDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.USER_STATUS, currentStatus);
        editStatusDialog.setArguments(bundle);
        return editStatusDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentStatus = getArguments().getString(Constants.USER_STATUS);
    }

    /**
     * Open the keyboard automatically when the dialog fragment is opened
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_set_status, null);
        editStatus = (EditText) view.findViewById(R.id.set_status_dialog);
        editStatus.setText(currentStatus);
        /**
         * Call editGroup() when user taps "Done" keyboard action
         */
        editStatus.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    setStatus();
                }
                return true;
            }
        });

        /* Inflate and set the layout for the dialog */
        /* Pass null as the parent view because its going in the dialog layout */
        builder.setView(view)
                /* Add action buttons */
                .setPositiveButton(R.string.positive_button_create, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        setStatus();
                    }
                })
                .setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**
                         * close the dialog
                         */
                        EditStatusDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private void setStatus() {
        String userenteredtext = editStatus.getText().toString();
        /**
         * check if empty or equal to the original  name
         */
        if (!userenteredtext.equals("") && userenteredtext != currentStatus) {
            /**
             * updates the firebase ref
             */
            mStatus = userenteredtext;

            /**
             * update the preference
             */
            userpreferences.edit().putString("userstatus", userenteredtext).apply();
            refreshStatusImage();
            updateUser();
            /**
             * close the dialog
             */
            EditStatusDialog.this.getDialog().cancel();

        }
    }
}
