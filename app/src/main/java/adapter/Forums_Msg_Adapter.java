package adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.deaspostudios.devchats.AndroidUtilities;
import com.deaspostudios.devchats.R;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import Widgets.Emoji;
import activity.Status;
import activity.UserType;

import static com.deaspostudios.devchats.MainActivity.mUsername;

/**
 * Created by polyc on 07/02/2017.
 */

public class Forums_Msg_Adapter extends ArrayAdapter<Message> {
    private ArrayList<Message> object;
    private Context context;

    /**
     * constructor method
     */
    public Forums_Msg_Adapter(Context context, int resources, ArrayList<Message> objects) {
        super(context, resources, objects);
        this.object = objects;
        this.context = context;


    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        Message message = object.get(position);

        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (message.getUserName() == mUsername) {
            message.setUserType(UserType.SELF);

        } else {
            message.setUserType(UserType.OTHER);
        }


        if (message.getUserName().contains(mUsername)) {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.outgoing, null, false);
                holder1 = new ViewHolder1();


                //initialize the variables
                holder1.messageTextView = (TextView) v.findViewById(R.id.textview_message);
                holder1.timeTextView = (TextView) v.findViewById(R.id.textview_time);
                holder1.photoView = (ImageView) v.findViewById(R.id.photoView);


                v.setTag(holder1);
            } else {
                v = convertView;
                holder1 = (ViewHolder1) v.getTag();
            }

            holder1.timeTextView.setText(DateFormat.getDateTimeInstance().format(new Date()));
            boolean isPhoto = message.getPhotoUrl() != null;
            if (isPhoto) {
                holder1.messageTextView.setVisibility(View.GONE);
                holder1.photoView.setVisibility(View.VISIBLE);
                Glide.with(holder1.photoView.getContext())
                        .load(message.getPhotoUrl())
                        .into(holder1.photoView);
            } else {
                holder1.messageTextView.setVisibility(View.VISIBLE);
                holder1.photoView.setVisibility(View.GONE);
                holder1.messageTextView.setText(Html.fromHtml(Emoji.replaceEmoji(message.getText(),
                        holder1.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16))
                        + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

            }

        } else {
            if (convertView == null) {
                v = LayoutInflater.from(context).inflate(R.layout.incoming, null, false);
                holder2 = new ViewHolder2();


                //initializes
                holder2.senderName = (TextView) v.findViewById(R.id.user_reply);
                holder2.messageTextView = (TextView) v.findViewById(R.id.textview_message2);
                holder2.timeTextView = (TextView) v.findViewById(R.id.textview_time2);
                holder2.photoView = (ImageView) v.findViewById(R.id.photoUser2);
                holder2.messageStatus = (ImageView) v.findViewById(R.id.user_reply_status);


                v.setTag(holder2);

            } else {
                v = convertView;
                holder2 = (ViewHolder2) v.getTag();
            }

            holder2.timeTextView.setText(DateFormat.getDateTimeInstance().format(new Date()));
            holder2.senderName.setText(message.getUserName());
            boolean isPhoto = message.getPhotoUrl() != null;
            if (isPhoto) {
                holder2.messageTextView.setVisibility(View.GONE);
                holder2.photoView.setVisibility(View.VISIBLE);
                Glide.with(holder2.photoView.getContext())
                        .load(message.getPhotoUrl())
                        .into(holder2.photoView);
            } else {
                holder2.messageTextView.setVisibility(View.VISIBLE);
                holder2.photoView.setVisibility(View.GONE);
                holder2.messageTextView.setText(Html.fromHtml(Emoji.replaceEmoji(message.getText(),
                        holder2.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16))
                        + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

            }
            if (message.getMessageStatus() == Status.DELIVERED) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_receipt_from_target));
            } else if (message.getMessageStatus() == Status.SENT) {
                holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.message_got_receipt_from_server));

            }

        }

        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        Message message = object.get(position);
        return message.getUserType().ordinal();
    }

    private class ViewHolder1 {
        public TextView messageTextView;
        public TextView timeTextView;
        public ImageView photoView;


    }

    private class ViewHolder2 {
        public TextView senderName;
        public ImageView messageStatus;
        public TextView messageTextView;
        public TextView timeTextView;
        public ImageView photoView;

    }

}
