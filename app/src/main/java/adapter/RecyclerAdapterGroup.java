package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deaspostudios.devchats.R;

import java.util.ArrayList;
import java.util.List;

import ui.GroupActivity;

import static com.deaspostudios.devchats.MainActivity.mUserEmail;

/**
 * Created by polyc on 30/01/2017.
 */

public class RecyclerAdapterGroup extends RecyclerView.Adapter<RecyclerAdapterGroup.MyViewHolder> {

    private Context mContext;
    private List<Items_forums> list;

    public RecyclerAdapterGroup(Context mContext, List<Items_forums> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_forums, parent, false);
        return new MyViewHolder(listView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        Items_forums itemsForums = list.get(position);
        holder.itemName.setText(itemsForums.getName());
        holder.creator.setText(itemsForums.getOwner());

        //apply the glide libbrary
        //Glide.with(mContext).load(mlist.getThumbnail()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setFilter(List<Items_forums> items) {
        list = new ArrayList<>();
        list.addAll(items);
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName, creator;
        //public ImageView thumbnail;

        public MyViewHolder(final View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.text_view_list_name);
            creator = (TextView) view.findViewById(R.id.text_view_created_by);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (getAdapterPosition() != RecyclerView.NO_POSITION) {
                                Items_forums selectedForum = list.get(getAdapterPosition());
                                if (selectedForum != null) {
                                    Intent intent = new Intent(mContext, GroupActivity.class);
                                    String forumId = selectedForum.getForum_id();
                                    String forumName = selectedForum.getName();
                                    String currentUserMail = mUserEmail;
                                    intent.putExtra("forumKey", forumId);
                                    intent.putExtra("forumName", forumName);
                                    intent.putExtra("usermail", currentUserMail);
                                    /**
                                     * satrt activity
                                     */
                                    mContext.startActivity(intent);

                                }
                            }
                        }
                    });
                }
            });
        }
    }


}
