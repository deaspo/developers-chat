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

/**
 * Created by polyc on 01/02/2017.
 */

public class ItemsAdapter extends ArrayAdapter<Items> {
    private List<Items> objects;


    public ItemsAdapter(Context context, int resources, List<Items> objects) {
        super(context, resources, objects);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.items, parent, false);
        }
        TextView uname = (TextView) convertView.findViewById(R.id.itemName);
        TextView uid = (TextView) convertView.findViewById(R.id.user_uid);


        Items current_user = getItem(position);


        uname.setText(current_user.getName());
        uid.setText(current_user.getUid());

        return convertView;
    }


}
