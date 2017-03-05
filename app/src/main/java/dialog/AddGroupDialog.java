package dialog;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import adapter.Items_forums;

import static com.deaspostudios.devchats.MainActivity.mUserEmail;
import static com.deaspostudios.devchats.MainActivity.mUsername;
import static fragment.group.gDatabaseReference;

/**
 * Created by polyc on 04/02/2017.
 */

public class AddGroupDialog extends DialogFragment {
    String mEncodedEmail;

    EditText addGroup;


    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */

    public static AddGroupDialog newInstance(String mEncodedEmail) {
        AddGroupDialog addGroupDialog = new AddGroupDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_ENCODED_EMAIL, mEncodedEmail);
        addGroupDialog.setArguments(bundle);
        return addGroupDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEncodedEmail = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
    }

    /**
     * Open the keyboard automatically when the dialog fragment is opened
     */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        /* Use the Builder class for convenient dialog construction */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomTheme_Dialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_group, null);
        addGroup = (EditText) view.findViewById(R.id.add_group_dialog);
        /**
         * Call addGroup() when user taps "Done" keyboard action
         */
        addGroup.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addGroup();
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
                        addGroup();
                    }
                })
                .setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**
                         * close the dialog
                         */
                        AddGroupDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /**
     * add new Group
     */

    public void addGroup() {
        String userEnteredName = addGroup.getText().toString();
        /**
         * checks if text is empty
         */
        if (!userEnteredName.equals("")) {
            /**
             * creates firebase reference
             */
            /* topicRef to maintain random id */
            DatabaseReference groupref = gDatabaseReference.getRef().push();
            final String groupId = groupref.getKey();

            /**
             * Set raw version of date to the ServerValue.TIMESTAMP value and save into
             * timestampCreatedMap
             */
            HashMap<String, Object> timestampCreated = new HashMap<>();
            timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            /* Build the group list */
            Items_forums newGroupList = new Items_forums(userEnteredName, mUsername, groupId, mUserEmail, timestampCreated);

            groupref.setValue(newGroupList);

            /* Close the dialog fragment */
            AddGroupDialog.this.getDialog().cancel();

        }

    }
}
