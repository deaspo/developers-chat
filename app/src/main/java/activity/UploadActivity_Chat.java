package activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.deaspostudios.devchats.MainActivity;
import com.deaspostudios.devchats.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import adapter.Message;

import static com.deaspostudios.devchats.MainActivity.escapeSpace;
import static com.deaspostudios.devchats.MainActivity.mDeviceToken;
import static com.deaspostudios.devchats.MainActivity.mUID;
import static com.deaspostudios.devchats.MainActivity.mUsername;
import static com.deaspostudios.devchats.MainActivity.sendChatNotification;
import static fragment.fav.cDatabaseReference;
import static ui.Chat.chat_messageAdapter;
import static ui.Chat.senderStorageRef;

/**
 * Created by polyc on 07/03/2017.
 */

public class UploadActivity_Chat extends Activity {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    boolean isImage;
    private ProgressBar progressBar;
    private String filePath = null;
    private String sendKey;
    private String selected_user_id = null;
    private String token = null;
    private Uri fileUri = null;
    private TextView txtPercentage;
    private GestureImageView imgPreview;
    private VideoView vidPreview;
    private Button btnUpload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        imgPreview = (GestureImageView) findViewById(R.id.imgPreview);
        vidPreview = (VideoView) findViewById(R.id.videoPreview);


        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");
        fileUri = Uri.parse(filePath);


        // boolean flag to identify the media type, image or video
        isImage = i.getBooleanExtra("isImage", true);

        /**
         * current db ref
         */
        selected_user_id = i.getStringExtra("selected_user_id");

        /**
         * selected user device token
         */
        token = i.getStringExtra("token");


        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        btnUpload.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // uploading the file to server
                new UploadActivity_Chat.UploadFileToServer().execute();

            }
        });
    }

    /**
     * Displaying captured image/video on the screen
     * */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
            imgPreview.setVisibility(View.VISIBLE);
            vidPreview.setVisibility(View.GONE);
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            imgPreview.setImageBitmap(bitmap);
        } else {
            imgPreview.setVisibility(View.GONE);
            vidPreview.setVisibility(View.VISIBLE);
            vidPreview.setVideoPath(fileUri.getPath());
            // start playing
            vidPreview.start();
        }
    }

    /**
     * Method to show alert dialog
     * */
    private void showAlert(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message).setTitle("Response from Servers")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // close the app
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {

        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            progressBar.setProgress(0);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            // Making progress bar visible
            progressBar.setVisibility(View.VISIBLE);

            // updating percentage value
            txtPercentage.setText("Uploading...");
        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            final String[] responseString = {null};
            StorageReference path_ref = senderStorageRef.child(fileUri.getLastPathSegment());

            if (isImage) {//file is image
                path_ref.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        final Message message = new Message();
                        message.setMessageStatus(activity.Status.SENT);
                        message.setText(null);
                        message.setUserName(mUsername);
                        message.setUserId(mUID);
                        message.setPhotoUrl(downloadUri.toString());
                        message.setVideoUrl(null);
                        message.setUserType(UserType.SELF);
                        message.setTimeStamp(DateFormat.getDateTimeInstance().format(new Date()));
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
                        String imageurl = downloadUri.toString();
                        String flag = "2";
                        sendChatNotification(mUID, mDeviceToken, imageurl, flag, token, escapeSpace(mUsername), escapeSpace("Picture message"));

                        // updating
                        progressBar.setVisibility(View.GONE);
                        txtPercentage.setText("");
                        responseString[0] = "Successfully uploaded";

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // updating
                        progressBar.setVisibility(View.GONE);
                        txtPercentage.setText("");
                        responseString[0] = e.toString();
                    }
                });

            }else {// file is video
                path_ref.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                        final Message message = new Message();
                        message.setMessageStatus(activity.Status.SENT);
                        message.setText(null);
                        message.setUserName(mUsername);
                        message.setUserId(mUID);
                        message.setPhotoUrl(null);
                        message.setVideoUrl(downloadUri.toString());
                        message.setUserType(UserType.SELF);
                        message.setTimeStamp(DateFormat.getDateTimeInstance().format(new Date()));
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
                        String imageurl = "none";
                        String flag = "2";
                        sendChatNotification(mUID, mDeviceToken, imageurl, flag, token, escapeSpace(mUsername), escapeSpace("Video message"));

                        // updating
                        progressBar.setVisibility(View.GONE);
                        txtPercentage.setText("");
                        responseString[0] = "Successfully uploaded";

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Failed to upload image", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        // updating
                        progressBar.setVisibility(View.GONE);
                        txtPercentage.setText("");
                        responseString[0] = e.toString();
                    }
                });

            }

            return responseString[0];
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(TAG, "Response from server: " + result);

            // showing the server response in an alert dialog
            showAlert(result);

            super.onPostExecute(result);
        }
    }

}
