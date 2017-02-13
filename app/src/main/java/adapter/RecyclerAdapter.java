package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.deaspostudios.devchats.R;

import java.util.List;

/**
 * Created by polyc on 30/01/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

    private Context mContext;
    private List<Items> list;

    public RecyclerAdapter(Context mContext, List<Items> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items, parent, false);
        return new MyViewHolder(listView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Items mlist = list.get(position);

        holder.itemName.setText(mlist.getName());

        //apply the glide libbrary
        //Glide.with(mContext).load(mlist.getThumbnail()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        //public ImageView thumbnail;

        public MyViewHolder(View view) {
            super(view);
            itemName = (TextView) view.findViewById(R.id.itemName);
            //thumbnail = (ImageView) view.findViewById(R.id.itemThumbanail);
        }
    }
}
