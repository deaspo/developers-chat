package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deaspostudios.devchats.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by polyc on 01/03/2017.
 */

public class RecycleAdapterTopic extends RecyclerView.Adapter<RecycleAdapterTopic.MyViewHolder> {
    private Context mContext;
    private List<Items_forums> list;

    public RecycleAdapterTopic(Context mContext, List<Items_forums> list) {
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
        holder.itemName.setText(itemsForums.getTopic_name());
        holder.creator.setText(itemsForums.getCreated_by());

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

        public MyViewHolder(final View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.text_view_list_name);
            creator = (TextView) view.findViewById(R.id.text_view_created_by);
        }
    }
}
