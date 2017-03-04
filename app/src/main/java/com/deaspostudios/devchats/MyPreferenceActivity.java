package com.deaspostudios.devchats;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import java.text.DateFormat;
import java.util.Date;

import adapter.User;

import static com.deaspostudios.devchats.MainActivity.mStatus;
import static com.deaspostudios.devchats.MainActivity.mStatusVisble;
import static com.deaspostudios.devchats.MainActivity.mUID;
import static com.deaspostudios.devchats.MainActivity.mUserEmail;
import static com.deaspostudios.devchats.MainActivity.mUsername;
import static com.deaspostudios.devchats.MainActivity.mUserphoto;
import static com.deaspostudios.devchats.MainActivity.mVisible;
import static com.deaspostudios.devchats.MainActivity.usersDbRef;
import static fragment.user.uDatabaseReference;

/**
 * Created by polyc on 02/03/2017.
 */

public class MyPreferenceActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String KEY_USER_NAME = "username";
    public static final String KEY_USER_STATUS = "userstatus";
    public static final String KEY_STATUS_VISIBILITY = "statusvisibility";
    public static final String KEY_ONLINE_VISIBILITY = "onlinevisibility";
    public static final String KEY_USER_PIC = "userpic";

    static EditTextPreference userName;
    static EditTextPreference userStatus;
    static CheckBoxPreference statusvisible;
    static CheckBoxPreference uservisible;
    static Preference userpic;

    public static void updateUser() {
        User user = new User(mUsername, mUserEmail, mUID, DateFormat.getDateTimeInstance().format(new Date()), mUserphoto, mStatus, mStatusVisble, mVisible);
        /**
         * updates the user db
         */
        usersDbRef.child(mUID).setValue(user);
        /**
         * update the active user entry
         */
        uDatabaseReference.child(mUID).setValue(user);
        /**
         * refresh the user icon on preference activity
         */
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_USER_NAME)) {
            Preference usernamePref = findPreference(key);
            //set the summary to be the user new name
            if (usernamePref instanceof EditTextPreference) {
                EditTextPreference editTextPreference = (EditTextPreference) usernamePref;
                if (editTextPreference.getText().trim().length() > 0) {
                    editTextPreference.setTitle(editTextPreference.getText());
                    mUsername = editTextPreference.getText();
                    Constants.USER_NAME = editTextPreference.getText();
                } else {
                    editTextPreference.setTitle("Your Name");
                }
            }
        } else if (key.equals(KEY_USER_STATUS)) {

            Preference userstatusPref = findPreference(key);
            //set the Title to be the user new name
            if (userstatusPref instanceof EditTextPreference) {
                EditTextPreference editTextPreference = (EditTextPreference) userstatusPref;
                if (editTextPreference.getText().trim().length() > 0) {
                    editTextPreference.setTitle(editTextPreference.getText());
                    mStatus = editTextPreference.getText();
                    Constants.USER_STATUS = editTextPreference.getText();
                } else {
                    editTextPreference.setTitle("Hey there am also a developer!");
                }
            }
        } else if (key.equals(KEY_STATUS_VISIBILITY)) {

            Preference statusPref = findPreference(key);
            //set the Title to be the user new name
            if (statusPref instanceof CheckBoxPreference) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) statusPref;
                if (checkBoxPreference.isChecked()) {
                    checkBoxPreference.setTitle("Hide your status");
                    checkBoxPreference.setSummary("This option if unchecked will make your status invisible");
                    Constants.STATUS_VISIBLE = "false";
                    mStatusVisble = false;
                } else {
                    checkBoxPreference.setTitle("Show your status");
                    checkBoxPreference.setSummary("This option if checked will make your status visible");
                    Constants.STATUS_VISIBLE = "true";
                    mStatusVisble = true;
                }
            }
        } else if (key.equals(KEY_ONLINE_VISIBILITY)) {

            Preference statusPref = findPreference(key);
            //set the Title to be the user new name
            if (statusPref instanceof CheckBoxPreference) {
                CheckBoxPreference checkBoxPreference = (CheckBoxPreference) statusPref;
                if (checkBoxPreference.isChecked()) {
                    checkBoxPreference.setTitle("Hide your online status");
                    checkBoxPreference.setSummary("This option if unchecked will make you invisible");
                    Constants.USER_VISIBLE = "false";
                    mVisible = false;
                } else {
                    checkBoxPreference.setTitle("Show your online status");
                    checkBoxPreference.setSummary("This option if checked will make you visible");
                    Constants.USER_VISIBLE = "true";
                    mVisible = true;
                }
            }
        }
        updateUser();

    }

    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            /**
             *
             */
            userName = (EditTextPreference) findPreference(KEY_USER_NAME);
            userName.setTitle(Constants.USER_NAME);
            /**
             *
             */
            userStatus = (EditTextPreference) findPreference(KEY_USER_STATUS);
            userStatus.setTitle(Constants.USER_STATUS);
            /**
             *
             */
            statusvisible = (CheckBoxPreference) findPreference(KEY_STATUS_VISIBILITY);
            if (Constants.STATUS_VISIBLE.equals("true")) {
                statusvisible.setDefaultValue("true");
            } else if (Constants.STATUS_VISIBLE.equals("true")) {
                statusvisible.setDefaultValue("false");
            }
            /**
             *
             */
            uservisible = (CheckBoxPreference) findPreference(KEY_ONLINE_VISIBILITY);
            if (Constants.USER_VISIBLE.equals("true")) {
                uservisible.setDefaultValue("true");
            } else if (Constants.USER_VISIBLE.equals("true")) {
                uservisible.setDefaultValue("false");
            }
        }
    }
}
