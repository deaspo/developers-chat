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

import static fragment.group.gDatabaseReference;

/**
 * Created by polyc on 02/03/2017.
 */

public class EditGroupDialog extends DialogFragment {
    String groupID;

    String mGroupName;

    EditText editGroupName;


    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */

    public static EditGroupDialog newInstance(String mGroupName, String mGroupID) {
        EditGroupDialog editGroupDialog = new EditGroupDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_LIST_NAME, mGroupName);
        bundle.putString(Constants.KEY_LIST_ID, mGroupID);
        editGroupDialog.setArguments(bundle);
        return editGroupDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGroupName = getArguments().getString(Constants.KEY_LIST_NAME);
        groupID = getArguments().getString(Constants.KEY_LIST_ID);
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
        View view = inflater.inflate(R.layout.dialog_edit_group, null);
        editGroupName = (EditText) view.findViewById(R.id.edit_group_dialog);
        editGroupName.setText(mGroupName);
        /**
         * Call editGroup() when user taps "Done" keyboard action
         */
        editGroupName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    editGroup();
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
                        editGroup();
                    }
                })
                .setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**
                         * close the dialog
                         */
                        EditGroupDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void editGroup() {
        String userenteredtext = editGroupName.getText().toString();
        /**
         * check if empty or equal to the original  name
         */
        if (!userenteredtext.equals("") && userenteredtext != mGroupName) {
            /**
             * updates the firebase ref
             */
            DatabaseReference topicRef = gDatabaseReference.child(groupID).child("topic_name");
            topicRef.setValue(userenteredtext);

        }
    }
}
