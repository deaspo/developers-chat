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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.util.Date;

import adapter.Message;

import static com.deaspostudios.devchats.MainActivity.escapeSpace;
import static com.deaspostudios.devchats.MainActivity.mUID;
import static com.deaspostudios.devchats.MainActivity.mUsername;
import static com.deaspostudios.devchats.MainActivity.sendTopicNotification;
import static fragment.topic.tDatabaseReference;
import static ui.TopicActivity.messageAdapter_topic;
import static ui.TopicActivity.topicStorageRef;

/**
 * Created by polyc on 07/03/2017.
 */

public class UploadActivity_Topic extends Activity {
    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();
    private static DatabaseReference currentForumRef, currentForumMessages;
    boolean isImage;
    private ProgressBar progressBar;
    private String filePath = null;
    private String topicId = null;
    private String topicName;
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
        topicId = i.getStringExtra("topicid");
        topicName = i.getStringExtra("topicname");
        currentForumRef = tDatabaseReference.child(topicId);
        currentForumMessages = currentForumRef.child("messages");


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
                new UploadFileToServer().execute();

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
            StorageReference path_ref = topicStorageRef.child(fileUri.getLastPathSegment());

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
                        if (messageAdapter_topic != null) {
                            messageAdapter_topic.notifyDataSetChanged(); }
                        currentForumMessages.push().setValue(message);
                        /**
                         * subcribes the sender to the topic
                         *
                         */
                        //start subcribe
                        FirebaseMessaging.getInstance().subscribeToTopic(escapeSpace(topicId));
                        // [END subscribe_topics]
                        //send message to all the topic subscribers
                        sendTopicNotification(escapeSpace(topicId), topicName,mUsername, downloadUri.toString(),"Topic","1","Picture maessage");

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
                        if (messageAdapter_topic != null) {
                            messageAdapter_topic.notifyDataSetChanged(); }
                        currentForumMessages.push().setValue(message);
                        /**
                         * subcribes the sender to the topic
                         *
                         */
                        //start subcribe
                        FirebaseMessaging.getInstance().subscribeToTopic(escapeSpace(topicId));
                        // [END subscribe_topics]
                        //send message to all the topic subscribers
                        sendTopicNotification(escapeSpace(topicId), topicName,mUsername, "none","Topic","1","Video message");

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
