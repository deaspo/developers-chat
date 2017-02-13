package adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.deaspostudios.devchats.R;

import java.util.List;

import static com.deaspostudios.devchats.MainActivity.mUserEmail;

/**
 * Created by polyc on 09/02/2017.
 */

public class UserAdapter extends ArrayAdapter<User> {
    public List<User> objects;

    public UserAdapter(Context context, int resources, List<User> objects) {
        super(context, resources, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.items, parent, false);
        }
        TextView user_name = (TextView) convertView.findViewById(R.id.itemName);
        TextView last_active = (TextView) convertView.findViewById(R.id.date_user_joined);
        TextView user_uid = (TextView) convertView.findViewById(R.id.user_uid);


        User user = getItem(position);


        if (user.getEmail().contains(mUserEmail)) {
            user_name.setText("You");
            last_active.setText("Now");
            user_uid.setText("Just you");
        } else {
            user_name.setText(user.getName());
            last_active.setText("Last Active: " + user.getDate_joined());
            user_uid.setText(user.getUid());
        }

        return convertView;
    }
}
