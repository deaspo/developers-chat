package ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import activity.Status;
import activity.UploadActivity_Chat;
import activity.UserType;
import adapter.Message;
import adapter.MessageAdapter;
import github.ankushsachdeva.emojicon.EmojiconEditText;
import github.ankushsachdeva.emojicon.EmojiconGridView.OnEmojiconClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnEmojiconBackspaceClickedListener;
import github.ankushsachdeva.emojicon.EmojiconsPopup.OnSoftKeyboardOpenCloseListener;
import github.ankushsachdeva.emojicon.emoji.Emojicon;

import static com.deaspostudios.devchats.MainActivity.mDeviceToken;
import static com.deaspostudios.devchats.MainActivity.mUID;
import static com.deaspostudios.devchats.MainActivity.mUsername;
import static com.deaspostudios.devchats.MainActivity.sendChatNotification;
import static fragment.fav.cDatabaseReference;


public class Chat extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;
    private static final int RC_PHOTO_PICKER = 2;
    /**
     * Uploading media files
     * @param savedInstanceState
     */
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;
    public static MessageAdapter chat_messageAdapter;
    public static StorageReference senderStorageRef;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar chatspb;
    private ListView chatListView;
    private ImageView emojiButton;
    private ImageButton photopicker, enterButton;
    private DatabaseReference senderRef;
    private ChildEventListener messageRefListener;
    private String token,selected_user, selected_user_id;
    private ArrayList<Message> chat_messageList;
    private String sendKey;
    //Firebase storage & Database
    private FirebaseStorage senderStorage;
    private Uri fileUri; // file url to store image/video

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

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));

        //start the activity using the reference key passed to it
        Intent intent = this.getIntent();
        selected_user = intent.getStringExtra("username");
        selected_user_id = intent.getStringExtra("userid");
        token = intent.getStringExtra("token");

        if (selected_user == null || selected_user_id == null) {
            /* No point in continuing without a valid selected user. */
            finish();
            return;
        }

        senderStorage = FirebaseStorage.getInstance();

        senderStorageRef = senderStorage.getReference().child("chat_photos");
        senderRef = cDatabaseReference.child(mUID).child("conversations").child(selected_user_id).child("messages");


        InitializeScreen();
        /**
         * initialize the adapter
         */
        chat_messageList = new ArrayList<>();
        chat_messageAdapter = new MessageAdapter(chat_messageList, this);

        chatListView.setAdapter(chat_messageAdapter);


    }

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


    /**
     * ------------ Helper Methods ----------------------
     * */

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(Chat.this, UploadActivity_Chat.class);
        i.putExtra("filePath", fileUri.toString());
        i.putExtra("isImage", isImage);
        i.putExtra("selected_user_id", selected_user_id);
        i.putExtra("token", token);
        startActivity(i);
    }

    /**
     * end of capture/record
     * @return
     */


    private void InitializeScreen() {
        chatListView = (ListView) findViewById(R.id.chat_list_view);
        registerForContextMenu(chatListView);
        final EmojiconEditText emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        final View rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emojiButton_chat);
        photopicker = (ImageButton) findViewById(R.id.chat_photoPickerButton);
        enterButton = (ImageButton) findViewById(R.id.enter_chat1);
        chatspb = (ProgressBar) findViewById(R.id.chatspb);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_chat);

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

        /**
         * setting up the emoji keyboard
         */

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

        /**
         * click listener for imagers
         */
        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View outgoing = findViewById(R.id.outgoming_layout);
                View incoming = findViewById(R.id.incoming_layout);
                if (view == outgoing) {
                    ImageView incomingImage = (ImageView) view.findViewById(R.id.photoView);
                    if (incomingImage != null) {
                        incomingImage.animate().scaleXBy(2.0f).scaleYBy(2.0f).setDuration(2000);
                    }
                } else if (view == incoming) {
                    ImageView outgoingImage = (ImageView) findViewById(R.id.photoUser2);
                    if (outgoingImage != null) {
                        outgoingImage.animate().scaleXBy(2.0f).scaleYBy(2.0f).setDuration(2000);
                    }
                }
            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(selected_user);
        }

        /**
         * button click listeners
         */
        photopicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/* video/*");
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
                //chat_messageList.add(message);
                if (chat_messageAdapter != null)
                    chat_messageAdapter.notifyDataSetChanged();

                /**
                 * set the recognisable values for sender and receiver
                 */
                sendKey = cDatabaseReference.push().getKey();

                Map<String, Object> msgValues = message.toMap();

                Map<String, Object> childUpdates = new HashMap<>();


                childUpdates.put(mUID + "/" + "conversations" + "/" + selected_user_id + "/" + "messages" + "/" + sendKey, msgValues);
                childUpdates.put(selected_user_id + "/" + "conversations" + "/" + mUID + "/" + "messages" + "/" + sendKey, msgValues);

                cDatabaseReference.updateChildren(childUpdates);
                //Send notification
                sendChatNotification(mUID,mDeviceToken,"none","2",token,mUsername,emojiconEditText.getText().toString());

                //clear the input box
                emojiconEditText.setText("");
                //
            }
        });

    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.chat_list_view) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
//            menu.setHeaderTitle("Options");
            String[] menuItems = getResources().getStringArray(R.array.longpress);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        switch (menuItemIndex) {
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        /**
         * access the menu items
         */
        MenuItem file_upload = menu.findItem(R.id.action_file_upload);
        MenuItem refresh = menu.findItem(R.id.action_refresh);
        MenuItem record_audio = menu.findItem(R.id.action_audio);
        MenuItem record_video = menu.findItem(R.id.action_capture);
        MenuItem clear_conversation = menu.findItem(R.id.action_clear_conversation);
        MenuItem view_contact = menu.findItem(R.id.action_view_contact);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                attachMessengesListeners();
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

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
        iconToBeChanged.setImageResource(drawableResourceId);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            chatspb.setVisibility(ProgressBar.VISIBLE);
            /**
             * check if it was image or video
             */
            Uri selectedUmageUri = data.getData();

            if (selectedUmageUri.toString().contains("images")) {
                //handle image
                StorageReference sender_photoRef = senderStorageRef.child(selectedUmageUri.getLastPathSegment());
                /**
                 * compress the image
                 */
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedUmageUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);


                } catch (IOException e) {
                    e.printStackTrace();
                }

                //upload file to sender firebase
                sender_photoRef.putFile(selectedUmageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        final Message message = new Message();
                        message.setMessageStatus(Status.SENT);
                        message.setText(null);
                        message.setUserName(mUsername);
                        message.setUserId(mUID);
                        message.setUserType(UserType.SELF);
                        message.setPhotoUrl(downloadUri.toString());
                        message.setTimeStamp(DateFormat.getDateTimeInstance().format(new Date()));
                        //chat_messageList.add(message);
                        if (chat_messageAdapter != null)
                            chat_messageAdapter.notifyDataSetChanged();

                        /**
                         * update without overwriting
                         */
                        sendKey = cDatabaseReference.push().getKey();
                        final Map<String, Object> childUpdates = new HashMap<>();

                        Map<String, Object> msgValues = message.toMap();

                        childUpdates.put(mUID + "/" + "conversations" + "/" + selected_user_id + "/" + "messages" + "/" + sendKey, msgValues);

                        childUpdates.put(selected_user_id + "/" + "conversations" + "/" + mUID + "/" + "messages" + "/" + sendKey, msgValues);

                        cDatabaseReference.updateChildren(childUpdates);

                        //Send notification
                        sendChatNotification(mUID,mDeviceToken,downloadUri.toString(),"2",token,mUsername,"Picture message");
                        chatspb.setVisibility(ProgressBar.GONE);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        chatspb.setVisibility(ProgressBar.GONE);
                    }
                });

            } else  if (selectedUmageUri.toString().contains("video")) {
                //handle video
                Toast.makeText(getApplicationContext(),"Uploading video, work in progress",Toast.LENGTH_LONG).show();
            }

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

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        attachMessengesListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachMessageListener();
        chat_messageList.clear();
    }

    @Override
    protected void onStop() {
        super.onStop();
        detachMessageListener();
        chat_messageList.clear();
    }

    private void attachMessengesListeners() {
        swipeRefreshLayout.setRefreshing(true);
        messageRefListener = senderRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Message message = dataSnapshot.getValue(Message.class);
                //chat_messageAdapter.add(message);
                chat_messageList.add(message);
                chat_messageAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
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
        chat_messageAdapter.notifyDataSetChanged();
    }

    private void detachMessageListener() {
        if (messageRefListener != null) {
            senderRef.removeEventListener(messageRefListener);
            messageRefListener = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onRefresh() {
        chat_messageList.clear();
        attachMessengesListeners();
    }
}
