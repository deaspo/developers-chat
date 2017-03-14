/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.deaspostudios.devchats;

/**
 * Created by polyc on 18/02/2017.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import ui.Chat;
import ui.GroupActivity;
import ui.TopicActivity;

import static com.deaspostudios.devchats.MainActivity.unescapeSpace;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private NotificationUtils notificationUtils;

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage == null) {return;}
        //check if message contain notification and data payload  //messages coming from the app
        if (remoteMessage.getNotification() != null && remoteMessage.getData().size() > 0) {
            try {
                handleDataMessage(remoteMessage.getNotification().getTitle(), remoteMessage.getData());
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        } else { // notifications only containing notification payload
            sendNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
        }
    }

    private void handleDataMessage(String title, Map<String, String> data) {
        Log.e(TAG, "push json: " + data.toString());

        try {
            String flag = data.get("flag");
            String senderid = data.get("senderid");

            // skip the message if the message belongs to same user as
            // the user would be having the same message when he was sending
            // but it might differs in your scenario
//           if (senderid.equals(mUID)) {return;} //checks if the message is from the same user
//
            switch (Integer.parseInt(flag)) {
                case Constants.PUSH_TYPE_CHATROOM:
                    // push notification belongs to a chat room
                    processChatRoomPush(title, data);
                    break;
                case Constants.PUSH_TYPE_USER:
                    // push notification is specific to user
                    processUserMessage(title, data);
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }

    }

    private void processChatRoomPush(String title, Map<String, String> data) {
        try {
            String imageUrl = data.get("image");
            String chatRoomId = data.get("topicid");
            String chatRoomName = unescapeSpace(data.get("topicname"));
            String sender = unescapeSpace(data.get("sender"));
            String pager = data.get("pager");
            String message = unescapeSpace(data.get("message"));
            String usermail = data.get("usermail");

            /**
             * set the notification intentt
             * resultintent
             */
            Class<?> activityClass = null;
            if (pager.equals("Group")) {
                activityClass = GroupActivity.class;
            } else if (pager.equals("Topic")) {
                activityClass = TopicActivity.class;
            }

            Intent pendingIntent = new Intent(getApplicationContext(), activityClass);
            pendingIntent.putExtra("forumKey", chatRoomId);
            pendingIntent.putExtra("forumName", chatRoomName);
            pendingIntent.putExtra("usermail", usermail);
            showNotificationMessage(getApplicationContext(), title, sender + " : " + message, pendingIntent);

        } catch (Exception e) {
            Log.e(TAG, "Parsing error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Json parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void processUserMessage(String title, Map<String, String> data) {
        try {
            String imageUrl = data.get("image");
            String sender = unescapeSpace(data.get("sender"));
            String sendertoken = data.get("sendertoken");
            String senderid = data.get("senderid");
            String message = unescapeSpace(data.get("message"));

            Intent resultIntent = new Intent(getApplicationContext(), Chat.class);
            resultIntent.putExtra("username", sender);
            resultIntent.putExtra("userid", senderid);
            resultIntent.putExtra("token", sendertoken);

            // check for push notification image attachment
            if (imageUrl.equals("none")) {
                showNotificationMessage(getApplicationContext(), title, sender + " : " + message, resultIntent);
            } else {
                // push notification contains image
                // show it with the image
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, resultIntent, imageUrl);
            }

        } catch (Exception e) {
            Log.e(TAG, "Parsing error: " + e.getMessage());
            Toast.makeText(getApplicationContext(), "Parse error: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent);
    }

    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, Intent intent, String imageUrl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, intent, imageUrl);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String title,String messageBody) {
//        if ()

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                // Show controls on lock screen even when user hides sensitive content.
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setPriority(2)
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
