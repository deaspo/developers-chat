/**
 * Copyright Google Inc. All Rights Reserved.
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
package adapter;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import activity.Status;
import activity.UserType;

public class Message {

    private String text;
    private String userName;
    private String photoUrl;
    private String timeStamp;
    private Status messageStatus;
    private UserType userType;
    private String userId;


    public Message() {
    }

    public Message(String text, String name, String photoUrl, String timeStamp, String userId) {
        this.text = text;
        this.userName = name;
        this.photoUrl = photoUrl;
        //this.profilePicUrl = profilePicUrl;
        this.timeStamp = timeStamp;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getTimeStamp() {
        return timeStamp;
    }


    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Status getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(Status messageStatus) {
        this.messageStatus = messageStatus;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("text", text);
        result.put("userName", userName);
        result.put("photoUrl", photoUrl);
        result.put("messageStatus", messageStatus);
        result.put("userType", userType);
        result.put("userId", userId);
        return result;
    }
}
