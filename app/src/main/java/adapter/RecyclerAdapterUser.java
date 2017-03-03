package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.deaspostudios.devchats.R;
import com.firebase.ui.storage.images.FirebaseImageLoader;

import java.util.ArrayList;
import java.util.List;

import other.CircleTransform;

import static com.deaspostudios.devchats.MainActivity.imageRef;
import static com.deaspostudios.devchats.MainActivity.mUserEmail;

/**
 * Created by polyc on 03/03/2017.
 */

public class RecyclerAdapterUser extends RecyclerView.Adapter<RecyclerAdapterUser.MyViewHolder> {
    public List<User> objects;
    private Context mContext;

    public RecyclerAdapterUser(Context context, List<User> objects) {
        this.mContext = context;
        this.objects = objects;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items, parent, false);
        return new MyViewHolder(listView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        User user = objects.get(position);
        /**
         * set the values
         */
        if (user.getEmail().contains(mUserEmail)) {
            holder.user_name.setText("You");
            holder.last_active.setText("Now");
            holder.user_uid.setText("Just you");
        } else {
            holder.user_name.setText(user.getName());
            holder.last_active.setText("Last Active: " + user.getDate_joined());
            holder.user_uid.setText(user.getUid());
        }

        /**
         * set the status visibilty
         */
        if (Boolean.valueOf(user.getStatus_visible())) {
            holder.user_status.setVisibility(View.VISIBLE);
            holder.user_status.setText(user.getStatus());
        } else {
            holder.user_status.setVisibility(View.GONE);
        }
        /**
         * set the profile picture
         */
        Glide.with(holder.user_ppic.getContext()).using(new FirebaseImageLoader()).load(imageRef)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(holder.user_ppic.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.user_ppic);
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    public void setFilter(List<User> users) {
        objects = new ArrayList<>();
        objects.addAll(users);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView user_name, last_active, user_uid, user_status;
        ImageView user_ppic;

        public MyViewHolder(final View view) {
            super(view);
            user_name = (TextView) view.findViewById(R.id.itemName);
            last_active = (TextView) view.findViewById(R.id.date_user_joined);
            user_uid = (TextView) view.findViewById(R.id.user_uid);
            user_status = (TextView) view.findViewById(R.id.user_status);
            user_ppic = (ImageView) view.findViewById(R.id.pprofile);
        }
    }
}
