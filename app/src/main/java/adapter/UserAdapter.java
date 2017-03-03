package adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.deaspostudios.devchats.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.List;

import other.CircleTransform;

import static com.deaspostudios.devchats.MainActivity.imageRef;
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
        TextView user_status = (TextView) convertView.findViewById(R.id.user_status);
        ImageView user_ppic = (ImageView) convertView.findViewById(R.id.pprofile);


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

        /**
         * set the status visibilty
         */
        if (Boolean.valueOf(user.getStatus_visible())) {
            user_status.setVisibility(View.VISIBLE);
            user_status.setText(user.getStatus());
        } else {
            user_status.setVisibility(View.GONE);
        }
        /**
         * set the profile picture
         */
        Glide.with(user_ppic.getContext()).using(new FirebaseImageLoader()).load(imageRef)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(user_ppic.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(user_ppic);

        return convertView;
    }
}
