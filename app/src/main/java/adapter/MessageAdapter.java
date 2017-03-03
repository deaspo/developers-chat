package adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import static com.deaspostudios.devchats.MainActivity.mUID;
import static com.deaspostudios.devchats.MainActivity.mUsername;

/**
 * Created by polyc on 31/01/2017.
 */

public class MessageAdapter extends BaseAdapter {
    private ArrayList<Message> chatMessage;
    private Context mContext;

    public MessageAdapter(ArrayList<Message> chatMessage, Context context) {
        this.chatMessage = chatMessage;
        this.mContext = context;
    }


    @Override
    public int getCount() {
        return chatMessage.size();
    }

    @Override
    public Object getItem(int i) {
        return chatMessage.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = null;
        Message message = chatMessage.get(i);

        ViewHolder1 holder1;
        ViewHolder2 holder2;

        if (message.getUserName() == mUsername) {
            message.setUserType(UserType.SELF);

        } else {
            message.setUserType(UserType.OTHER);
        }


        if (message.getUserId().contains(mUID)) {
            v = LayoutInflater.from(mContext).inflate(R.layout.incoming, viewGroup, false);
            holder1 = new ViewHolder1();

            //initialize the variables
            holder1.messageTextView = (TextView) v.findViewById(R.id.textview_message2);
            holder1.timeTextView = (TextView) v.findViewById(R.id.textview_time2);
            holder1.photoView = (ImageView) v.findViewById(R.id.photoUser2);
            holder1.messageStatus = (ImageView) v.findViewById(R.id.user_sent_status);
            holder1.photo_layout = (LinearLayout) v.findViewById(R.id.ppic);

            v.setTag(holder1);
            /*if (view == null) {

            } else {
                v = view;
                holder1 = (ViewHolder1) v.getTag();
            }*/

            holder1.timeTextView.setText(DateFormat.getDateTimeInstance().format(new Date()));
            boolean isPhoto = message.getPhotoUrl() != null;
            if (isPhoto) {
                holder1.messageTextView.setVisibility(View.GONE);
                holder1.photo_layout.setVisibility(View.VISIBLE);
                holder1.photoView.setVisibility(View.VISIBLE);
                Glide.with(holder1.photoView.getContext())
                        .load(message.getPhotoUrl())
                        .into(holder1.photoView);
            } else {
                holder1.messageTextView.setVisibility(View.VISIBLE);
                holder1.photo_layout.setVisibility(View.GONE);
                holder1.photoView.setVisibility(View.GONE);
                holder1.messageTextView.setText(Html.fromHtml(Emoji.replaceEmoji(message.getText(),
                        holder1.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16))
                        + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

            }

            if (message.getMessageStatus() == Status.DELIVERED) {
                holder1.messageStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.message_got_receipt_from_target));
            } else if (message.getMessageStatus() == Status.SENT) {
                holder1.messageStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.message_got_receipt_from_server));

            }

        } else {
            v = LayoutInflater.from(mContext).inflate(R.layout.outgoing, viewGroup, false);
            holder2 = new ViewHolder2();

            //initializes
            holder2.messageTextView = (TextView) v.findViewById(R.id.textview_message);
            holder2.timeTextView = (TextView) v.findViewById(R.id.textview_time);
            holder2.photoView = (ImageView) v.findViewById(R.id.photoView);
            holder2.senderName = (TextView) v.findViewById(R.id.other_user);
            holder2.photo = (LinearLayout) v.findViewById(R.id.photo);

            v.setTag(holder2);
            /*if (view == null) {


            } else {
                v = view;
                holder2 = (ViewHolder2) v.getTag();
            }*/

            holder2.timeTextView.setText(DateFormat.getDateTimeInstance().format(new Date()));
            holder2.senderName.setText(message.getUserName());
            boolean isPhoto = message.getPhotoUrl() != null;
            if (isPhoto) {
                holder2.messageTextView.setVisibility(View.GONE);
                holder2.photo.setVisibility(View.VISIBLE);
                holder2.photoView.setVisibility(View.VISIBLE);
                Glide.with(holder2.photoView.getContext())
                        .load(message.getPhotoUrl())
                        .into(holder2.photoView);
            } else {
                holder2.messageTextView.setVisibility(View.VISIBLE);
                holder2.photo.setVisibility(View.GONE);
                holder2.photoView.setVisibility(View.GONE);
                holder2.messageTextView.setText(Html.fromHtml(Emoji.replaceEmoji(message.getText(),
                        holder2.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16))
                        + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;"));

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
        Message message = chatMessage.get(position);
        return message.getUserType().ordinal();
    }

    private class ViewHolder1 {
        public ImageView messageStatus;
        public TextView messageTextView;
        public TextView timeTextView;
        public ImageView photoView;
        public LinearLayout photo_layout;


    }

    private class ViewHolder2 {
        public TextView senderName;
        public TextView messageTextView;
        public TextView timeTextView;
        public ImageView photoView;
        public LinearLayout photo;

    }
}
