package adapter;

import com.deaspostudios.devchats.Constants;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

/**
 * Created by polyc on 03/02/2017.
 */

public class Items_forums {
    public String topic_name;
    public String created_by;
    public String owner_email;
    public String forum_id;
    private HashMap<String, Object> timestampLastChanged;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChangedReverse;
    private HashMap<String, User> usersRegistered;

    public Items_forums() {
    }


    public Items_forums(String topic_name, String created_by, String forum_id, String owner_email, HashMap<String, Object> timestampCreated) {
        this.topic_name = topic_name;
        this.created_by = created_by;
        this.forum_id = forum_id;
        this.owner_email = owner_email;
        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
        this.timestampLastChangedReverse = null;
        this.usersRegistered = new HashMap<>();
    }

    public String getName() {
        return topic_name;
    }

    public String getOwner() {
        return created_by;
    }

    public String getForum_id() {
        return forum_id;
    }

    public String getOwner_email() {
        return owner_email;
    }

    public HashMap<String, Object> getTimestampLastChanged() {
        return timestampLastChanged;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    public HashMap<String, Object> getTimestampLastChangedReverse() {
        return timestampLastChangedReverse;
    }

    public HashMap getUsersRegistered() {
        return usersRegistered;
    }


}
