package adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import com.deaspostudios.devchats.R;

import java.util.List;

/**
 * Created by polyc on 03/02/2017.
 */

public class ForumsAdapter extends ArrayAdapter<Items_forums> implements Filterable {
    private List<Items_forums> object;


    public ForumsAdapter(Context context, int resources, List<Items_forums> ojects) {
        super(context, resources, ojects);
        getFilter();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_forums, parent, false);
        }
        TextView forum_name = (TextView) convertView.findViewById(R.id.text_view_list_name);
        TextView created_by = (TextView) convertView.findViewById(R.id.text_view_created_by);

        Items_forums current_forum = getItem(position);

        forum_name.setText(current_forum.getName());
        created_by.setText(current_forum.getOwner());

        return convertView;
    }


}
