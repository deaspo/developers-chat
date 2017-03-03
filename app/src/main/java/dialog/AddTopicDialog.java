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
import static fragment.topic.attachTopicDatabaseListener;
import static fragment.topic.tDatabaseReference;

/**
 * Created by polyc on 03/02/2017.
 */

public class AddTopicDialog extends DialogFragment {
    String mEncodedEmail;

    EditText addTopic;


    /**
     * Public static constructor that creates fragment and
     * passes a bundle with data into it when adapter is created
     */

    public static AddTopicDialog newInstance(String mEncodedEmail) {
        AddTopicDialog addTopicDialog = new AddTopicDialog();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.KEY_ENCODED_EMAIL, mEncodedEmail);
        addTopicDialog.setArguments(bundle);
        return addTopicDialog;
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
        View view = inflater.inflate(R.layout.dialog_add_topic, null);
        addTopic = (EditText) view.findViewById(R.id.add_topic_dialog);
        /**
         * Call addMeal() when user taps "Done" keyboard action
         */
        addTopic.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    addTopic();
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
                        addTopic();
                    }
                })
                .setNegativeButton(R.string.negative_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        /**
                         * close the dialog
                         */
                        AddTopicDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    /**
     * add new topic
     */

    public void addTopic() {
        String userEnteredName = addTopic.getText().toString();
        /**
         * checks if text is empty
         */
        if (!userEnteredName.equals("")) {
            /**
             * creates firebase reference
             */
            /* topicRef to maintain random id */
            DatabaseReference topicref = tDatabaseReference.getRef().push();
            final String topicId = topicref.getKey();

            /**
             * Set raw version of date to the ServerValue.TIMESTAMP value and save into
             * timestampCreatedMap
             */
            HashMap<String, Object> timestampCreated = new HashMap<>();
            timestampCreated.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

            /* Build the topic list */
            Items_forums newTopicList = new Items_forums(userEnteredName, mUsername, topicId, mUserEmail, timestampCreated);

            topicref.setValue(newTopicList);
            /**
             * db listener
             */
            attachTopicDatabaseListener();

            /* Close the dialog fragment */
            AddTopicDialog.this.getDialog().cancel();
            /* go to that created topic */

        }

    }
}
