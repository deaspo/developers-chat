package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Process;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.deaspostudios.devchats.AndroidUtilities;
import com.deaspostudios.devchats.ImageActivity;
import com.deaspostudios.devchats.R;
import com.deaspostudios.devchats.VideoActivity;

import java.io.ByteArrayOutputStream;
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
        final Message message = chatMessage.get(i);

        final ViewHolder1 holder1;
        final ViewHolder2 holder2;

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
            holder1.videoView = (VideoView) v.findViewById(R.id.videouser2);
            holder1.messageStatus = (ImageView) v.findViewById(R.id.user_sent_status);
            holder1.photo_layout = (LinearLayout) v.findViewById(R.id.ppic);
            holder1.innerpb = (ProgressBar) v.findViewById(R.id.incomingpb);

            /**
             * change message delivery status
             */
            message.setMessageStatus(Status.DELIVERED);

            v.setTag(holder1);
            /*if (view == null) {

            } else {
                v = view;
                holder1 = (ViewHolder1) v.getTag();
            }*/

            holder1.photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImageActivity.class);
                    holder1.photoView.buildDrawingCache();
                    Bitmap bitmappic = holder1.photoView.getDrawingCache();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmappic.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
                    intent.putExtra("picsbytearraye", byteArrayOutputStream.toByteArray());
                    mContext.startActivity(intent);
                }
            });

            holder1.videoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("videourl",message.getVideoUrl());
                    mContext.startActivity(intent);
                }
            });

            holder1.timeTextView.setText(DateFormat.getDateTimeInstance().format(new Date()));
            boolean isPhoto = message.getPhotoUrl() != null;
            boolean isVideo = message.getVideoUrl() != null;
            if (isPhoto) {
                holder1.innerpb.setVisibility(ProgressBar.VISIBLE);
                holder1.messageTextView.setVisibility(View.GONE);
                holder1.videoView.setVisibility(View.GONE);
                holder1.photo_layout.setVisibility(View.VISIBLE);
                holder1.photoView.setVisibility(View.VISIBLE);
                Glide.with(holder1.photoView.getContext())
                        .load(message.getPhotoUrl())
                        .into(holder1.photoView);
                holder1.innerpb.setVisibility(ProgressBar.GONE);
            } else if (isVideo) {
                holder1.innerpb.setVisibility(ProgressBar.VISIBLE);
                holder1.messageTextView.setVisibility(View.GONE);
                holder1.videoView.setVisibility(View.VISIBLE);
                holder1.photo_layout.setVisibility(View.VISIBLE);
                holder1.photoView.setVisibility(View.GONE);

                holder1.videoView.post(new Runnable() {
                    @Override
                    public void run() {
                        MediaController mc = new MediaController(mContext);
                        mc.setAnchorView(holder1.videoView);
                        mc.setMediaPlayer(holder1.videoView);
                        holder1.videoView.setMediaController(mc);
                        holder1.videoView.setVideoURI(Uri.parse(message.getVideoUrl()));
                        holder1.videoView.start();
                    }
                });

            } else {
                holder1.messageTextView.setVisibility(View.VISIBLE);
                holder1.photo_layout.setVisibility(View.GONE);
                holder1.photoView.setVisibility(View.GONE);
                holder1.videoView.setVisibility(View.GONE);
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
            holder2.videoView2 = (VideoView) v.findViewById(R.id.videouser);
            holder2.outerpb = (ProgressBar) v.findViewById(R.id.outgoingpb);

            v.setTag(holder2);
            /*if (view == null) {


            } else {
                v = view;
                holder2 = (ViewHolder2) v.getTag();
            }*/

            holder2.photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImageActivity.class);
                    holder2.photoView.buildDrawingCache();
                    Bitmap bitmappic = holder2.photoView.getDrawingCache();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmappic.compress(Bitmap.CompressFormat.PNG, 90, byteArrayOutputStream);
                    intent.putExtra("picsbytearraye", byteArrayOutputStream.toByteArray());
                    mContext.startActivity(intent);
                }
            });

            holder2.videoView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, VideoActivity.class);
                    intent.putExtra("videourl",message.getVideoUrl());
                    mContext.startActivity(intent);
                }
            });

            holder2.timeTextView.setText(DateFormat.getDateTimeInstance().format(new Date()));
            holder2.senderName.setText(message.getUserName());
            boolean isPhoto = message.getPhotoUrl() != null;
            boolean isVideo = message.getVideoUrl() != null;
            if (isPhoto) {
                holder2.outerpb.setVisibility(View.VISIBLE);
                holder2.messageTextView.setVisibility(View.GONE);
                holder2.videoView2.setVisibility(View.GONE);
                holder2.photo.setVisibility(ProgressBar.VISIBLE);
                holder2.photoView.setVisibility(View.VISIBLE);
                Glide.with(holder2.photoView.getContext())
                        .load(message.getPhotoUrl())
                        .into(holder2.photoView);
                holder2.outerpb.setVisibility(ProgressBar.GONE);
            } else if (isVideo) {
                holder2.outerpb.setVisibility(ProgressBar.VISIBLE);
                holder2.messageTextView.setVisibility(View.GONE);
                holder2.videoView2.setVisibility(View.VISIBLE);
                holder2.photo.setVisibility(View.VISIBLE);
                holder2.photoView.setVisibility(View.GONE);

                holder2.videoView2.post(new Runnable() {
                    @Override
                    public void run() {
                        MediaController mc = new MediaController(mContext);
                        mc.setAnchorView(holder2.videoView2);
                        mc.setMediaPlayer(holder2.videoView2);
                        holder2.videoView2.setMediaController(mc);
                        holder2.videoView2.setVideoURI(Uri.parse(message.getVideoUrl()));
                        holder2.videoView2.start();
                    }
                });

            } else {
                holder2.messageTextView.setVisibility(View.VISIBLE);
                holder2.photo.setVisibility(View.GONE);
                holder2.photoView.setVisibility(View.GONE);
                holder2.videoView2.setVisibility(View.GONE);
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

    private void downloadImages(final ImageView img1, final ImageView img2, final String messageurl, final ProgressBar progressBar) {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                try {
                    Glide.with(img1.getContext())
                            .load(messageurl)
                            .into(img2);
                    progressBar.setVisibility(ProgressBar.GONE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    //alternatively
    private static class PicturesViews extends AsyncTask {
        private int position;
        private ViewHolder1 viewHolder1;

        @Override
        protected Object doInBackground(Object[] params) {
            //download image here
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (viewHolder1.position1 == position) {

            }
            super.onPostExecute(o);
        }
    }

    private class ViewHolder1 {
        public ImageView messageStatus;
        public TextView messageTextView;
        public TextView timeTextView;
        public ImageView photoView;
        public VideoView videoView;
        public LinearLayout photo_layout;
        public FrameLayout innerframre;
        public ProgressBar innerpb;
        public int position1;


    }

    private class ViewHolder2 {
        public TextView senderName;
        public TextView messageTextView;
        public TextView timeTextView;
        public ImageView photoView;
        public VideoView videoView2;
        public LinearLayout photo;
        public FrameLayout outerframre;
        public ProgressBar outerpb;
        public int position2;

    }
}
