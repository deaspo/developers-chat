package ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.deaspostudios.devchats.Constants;
import com.deaspostudios.devchats.MainActivity;
import com.deaspostudios.devchats.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;
import android.os.Environment;

import activity.Status;
import activity.UploadActivity_Group;
import activity.UserType;
import adapter.Items_forums;
import adapter.Message;
import adapter.MessageAdapter;
import dialog.EditGroupDialog;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

import static com.deaspostudios.devchats.MainActivity.escapeSpace;
import static com.deaspostudios.devchats.MainActivity.mUID;
import static com.deaspostudios.devchats.MainActivity.mUsername;
import static com.deaspostudios.devchats.MainActivity.viewPager;
import static fragment.group.gDatabaseReference;

/**
 * emjicons
 */

public class GroupActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final int RC_PHOTO_PICKER = 2;
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static DatabaseReference currentForumRef, currentForumMessages;
    private static String groupId;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar forumspb;
    private ListView groupListView;
    private ImageView emojiButton;
    private ImageButton photopicker, enterButton;
    private String userMail;
    private String groupName;
    private ValueEventListener currentForumRefListener;

    //private MessageAdapter messageAdapter;
    private ChildEventListener CurrentMessageRefListener;
    /**
     * using the  new adapter
     */
    public static MessageAdapter messageAdapter_group;
    private ArrayList<Message> messageList;
    private boolean currentUserIsCreator = false;
    //Firebase storage & Database
    private FirebaseStorage groupStorage;
    public static StorageReference groupStorageRef;

    /**
     * Uploading media files
     * @param savedInstanceState
     */
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);


        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));

        /**
         * gets the topic id and creator from the passed data
         */
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            groupId = bundle.getString("forumKey");
            groupName = bundle.getString("forumName");
            userMail = bundle.getString("usermail");

            if (groupId == null) {
                finish();//stop is no valid reference is passed
                return;
            }
            if (userMail == null) {
                finish();
                return;
            }
        }

        /**
         * current db ref
         */
        currentForumRef = gDatabaseReference.child(groupId);
        currentForumMessages = currentForumRef.child("messages");

        /**
         * topic storage
         */

        groupStorage = FirebaseStorage.getInstance();

        groupStorageRef = groupStorage.getReference().child("groups").child(groupId);

        InitializeScreen();
        /**
         * initialize the adapter
         */
        messageList = new ArrayList<>();
        messageAdapter_group = new MessageAdapter(messageList, this);

        groupListView.setAdapter(messageAdapter_group);


    }

    /**
     * Launching app to capture photo
     * @param items_forums
     * @param currentUserEmail
     * @return
     */
    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Launching camera app to record video
     */
    private void recordVideo() {
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Constants.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(GroupActivity.this, UploadActivity_Group.class);
        i.putExtra("filePath", fileUri.toString());
        i.putExtra("isImage", isImage);
        i.putExtra("groupid", groupId);
        startActivity(i);
    }

    /**
     * end of capture/record
     * @param items_forums
     * @param currentUserEmail
     * @return
     */

    private boolean checkOwnership(Items_forums items_forums, String currentUserEmail) {
        return (items_forums.getCreated_by() != null && items_forums.owner_email.equals(currentUserEmail));
    }

    private void InitializeScreen() {
        groupListView = (ListView) findViewById(R.id.forum_list_view);
        emojiButton = (ImageView) findViewById(R.id.forum_emojiButton);
        final EmojiconEditText emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        final View rootView = findViewById(R.id.root_group);
        photopicker = (ImageButton) findViewById(R.id.forum_photoPickerButton);
        enterButton = (ImageButton) findViewById(R.id.enter_forum);
        forumspb = (ProgressBar) findViewById(R.id.forumsspb);


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_forum);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                    }
                                }
        );

        // Give the topmost view of your activity layout hierarchy. This will be used to measure soft keyboard height
        final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiButton, R.drawable.smiley);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                if (emojiconEditText == null || emojicon == null) {
                    return;
                }

                int start = emojiconEditText.getSelectionStart();
                int end = emojiconEditText.getSelectionEnd();
                if (start < 0) {
                    emojiconEditText.append(emojicon.getEmoji());
                } else {
                    emojiconEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                emojiconEditText.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if (!popup.isShowing()) {

                    //If keyboard is visible, simply show the emoji popup
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else {
                        emojiconEditText.setFocusableInTouchMode(true);
                        emojiconEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(emojiconEditText, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiButton, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else {
                    popup.dismiss();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.group_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        /**
         * button click listeners
         */
        photopicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });

        // Enable Send button when there's text to send
        emojiconEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    enterButton.setEnabled(true);
                    enterButton.setBackground(getResources().getDrawable(R.drawable.input_circle_normal));
                } else {
                    enterButton.setEnabled(false);
                    enterButton.setBackground(getResources().getDrawable(R.drawable.ic_brightness_1_black_24dp));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        emojiconEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        // Send button sends a message and clears the EditText
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**
                 * implementing new changes from whatsapp
                 */
                final Message message = new Message();
                message.setMessageStatus(Status.SENT);
                message.setText(emojiconEditText.getText().toString());
                message.setUserName(mUsername);
                message.setUserId(mUID);
                message.setUserType(UserType.SELF);
                message.setTimeStamp(DateFormat.getDateTimeInstance().format(new Date()));
                if (messageAdapter_group != null)
                    messageAdapter_group.notifyDataSetChanged();
                currentForumMessages.push().setValue(message);
                // clear the input box
                emojiconEditText.setText("");
                /**
                 * subcribes the sender to the topic group
                 */
                //start subcribe

                FirebaseMessaging.getInstance().subscribeToTopic(escapeSpace(groupId));
                // [END subscribe_topics]
            }
        });

    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_active_group, menu);
        /**
         * access the menu items
         */
        MenuItem edit = menu.findItem(R.id.action_edit_group_name);
        MenuItem remove = menu.findItem(R.id.action_remove_group);


        edit.setVisible(currentUserIsCreator);
        remove.setVisible(currentUserIsCreator);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * action on menu item selected
         */
        switch (item.getItemId()) {
            case R.id.action_remove_group:
                showWarning(this.getApplicationContext(), "Remove " + groupName + "?", "By deleting this group, all the conversations will also be deleted", true, true, -1, MainActivity.class);
                return true;
            case R.id.action_edit_group_name:
                showEditGroupDialog();
                return true;
            case R.id.action_refresh_group:
                attachMessageListener();
                return true;
            case R.id.action_capture:
                captureImage();
                return true;
            case R.id.action_record:
                recordVideo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachForumListener();
        attachMessageListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachMessageListener();
        detachForumListener();
        messageList.clear();
        messageAdapter_group.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();
        detachMessageListener();
        detachForumListener();
        messageList.clear();
        messageAdapter_group.notifyDataSetChanged();
    }

    private void attachForumListener() {
        /**
         * add the value listeners?
         */
        currentForumRefListener = currentForumRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Items_forums items_forums = dataSnapshot.getValue(Items_forums.class);
                if (items_forums == null) {
                    finish();
                    return;
                }
                currentUserIsCreator = checkOwnership(items_forums, userMail);
                /* Calling invalidateOptionsMenu causes onCreateOptionsMenu to be called */
                invalidateOptionsMenu();
                /* Set title appropriately. */
                setTitle(items_forums.getTopic_name());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void attachMessageListener() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }

        CurrentMessageRefListener = currentForumMessages.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                //messageAdapter.add(message);
                messageList.add(message);
                if (messageAdapter_group != null) {
                    messageAdapter_group.notifyDataSetChanged();
                }
                if (swipeRefreshLayout != null) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //currentForumMessages.addChildEventListener(CurrentMessageRefListener);

    }

    private void detachMessageListener() {
        if (CurrentMessageRefListener != null) {
            currentForumMessages.removeEventListener(CurrentMessageRefListener);
            CurrentMessageRefListener = null;
        }
    }

    private void detachForumListener() {
        if (currentForumRefListener != null) {
            currentForumRef.removeEventListener(currentForumRefListener);
            currentForumRefListener = null;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            forumspb.setVisibility(ProgressBar.VISIBLE);
            Uri selectedUmageUri = data.getData();
            StorageReference group_photoRef = groupStorageRef.child(selectedUmageUri.getLastPathSegment());

            group_photoRef.putFile(selectedUmageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    final Message message = new Message();
                    message.setMessageStatus(Status.SENT);
                    message.setText(null);
                    message.setUserName(mUsername);
                    message.setUserId(mUID);
                    message.setPhotoUrl(downloadUri.toString());
                    message.setVideoUrl(null);
                    message.setUserType(UserType.SELF);
                    message.setTimeStamp(DateFormat.getDateTimeInstance().format(new Date()));
                    if (messageAdapter_group != null)
                        messageAdapter_group.notifyDataSetChanged();
                    currentForumMessages.push().setValue(message);
                    forumspb.setVisibility(View.GONE);


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    forumspb.setVisibility(View.GONE);
                }
            });


        }else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    private void showWarning(Context context, String title, String message, boolean showCancel,
                             boolean showOK, int iconID, Class<?> okClass) {
        AlertFragment.context = context;
        AlertFragment.iconID = iconID;
        AlertFragment.title = title;
        AlertFragment.message = message;
        AlertFragment.showOK = showOK;
        AlertFragment.showCancel = showCancel;
        AlertFragment.okClass = okClass;

        DialogFragment fragment = new AlertFragment();
        fragment.show(getSupportFragmentManager(), "Dialog");

    }

    public void showEditGroupDialog() {
        /* creates the instance of the Edit Dialog */
        EditGroupDialog dialogEditGroup = EditGroupDialog.newInstance(groupName, groupId);
        dialogEditGroup.show(GroupActivity.this.getFragmentManager(), "EditGroupDialog");
    }

    @Override
    public void onRefresh() {
        messageList.clear();
        attachMessageListener();
    }

    public static class AlertFragment extends DialogFragment {

        public static Context context;
        public static String message;
        public static String title = null;
        public static int iconID = -1;
        public static int theme = R.style.AppTheme;
        public static boolean showOK;
        public static boolean showCancel;
        public static Class<?> okClass = null;
        public static int buttonPressed;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            // Use the Builder class to construct the dialog.  Use the
            // form of the builder constructor that allows a theme to be set.


            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), theme);

            if (title != null) builder.setTitle(title);
            if (iconID != -1) builder.setIcon(iconID);
            builder.setMessage(message);
            if (showOK && okClass != null) {
                builder.setPositiveButton("Select this task", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        gDatabaseReference.child(groupId).removeValue();
                        Intent i = new Intent(context, okClass);
                        viewPager.setCurrentItem(0, true);
                        startActivity(i);
                    }
                });
            }
            if (showCancel) {
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Default is to cancel the dialog window.
                    }
                });
            }
            // Create the AlertDialog object and return it
            return builder.create();
        }

    }
}
