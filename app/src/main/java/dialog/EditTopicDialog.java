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

import static fragment.topic.tDatabaseReference;

/**
 * Created by polyc on 02/03/2017.
 */

public class EditTopicDialog extends DialogFragment {
    String topicID;

    String mTopicName;

    EditText editTopicName;

    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */

    public static EditTopicDialog newInstance(String mTopicName, String mTopicID) {
        EditTopicDialog editTopicDialog = new EditTopicDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_LIST_NAME, mTopicID);
        bundle.putString(Constants.KEY_LIST_ID, mTopicID);
        editTopicDialog.setArguments(bundle);
        return editTopicDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTopicName = getArguments().getString(Constants.KEY_LIST_NAME);
        topicID = getArguments().getString(Constants.KEY_LIST_ID);
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
        View view = inflater.inflate(R.layout.dialog_edit_topic, null);
        editTopicName = (EditText) view.findViewById(R.id.edit_group_dialog);
        editTopicName.setText(mTopicName);
        /**
         * Call editGroup() when user taps "Done" keyboard action
         */
        editTopicName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    editTopic();
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
                        editTopic();
                    }
                })
                .setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**
                         * close the dialog
                         */
                        EditTopicDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public void editTopic() {
        String userenteredtext = editTopicName.getText().toString();
        /**
         * check if empty or equal to the original  name
         */
        if (!userenteredtext.equals("") && userenteredtext != mTopicName) {
            /**
             * updates the firebase ref
             */
            DatabaseReference topicRef = tDatabaseReference.child(topicID).child("topic_name");
            topicRef.setValue(userenteredtext);

        }
    }
}
