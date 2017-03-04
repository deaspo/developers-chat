package com.deaspostudios.devchats;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.gestures.views.GestureImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import dialog.EditStatusDialog;
import other.CircleTransform;

import static com.deaspostudios.devchats.MainActivity.imageRef;
import static com.deaspostudios.devchats.MainActivity.mUID;
import static com.deaspostudios.devchats.MainActivity.mUsername;
import static com.deaspostudios.devchats.MainActivity.mUserphoto;
import static com.deaspostudios.devchats.MainActivity.profile_photos;
import static com.deaspostudios.devchats.MainActivity.userpreferences;
import static com.deaspostudios.devchats.MyPreferenceActivity.updateUser;

public class SettingsActivity extends AppCompatActivity {
    public static final int RC_PIC_PICKER = 5;
    private static String aboutstatus;
    private static TextView ustatus;
    private static GestureImageView upic;
    String imgpath;
    private ProgressBar aboutpb;
    private String aboutname;
    private String aboutpic;

    public static void refreshStatusImage() {
        String tmppicurl = userpreferences.getString("userpic", null);
        String tmpstatus = userpreferences.getString("userstatus", "Hey there am also a developer!");

        ustatus.setText(tmpstatus);
        aboutstatus = tmpstatus;

        if (tmppicurl == null) {
            Glide.with(upic.getContext()).using(new FirebaseImageLoader()).load(imageRef)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(upic.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(upic);
        } else {
            Glide.with(upic.getContext())
                    .load(tmppicurl)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(upic.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(upic);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /**
         * setting the toolbar
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //start the activity using the reference key passed to it
        Intent intent = this.getIntent();
        aboutname = mUsername;
        aboutpic = intent.getStringExtra("userpic");
        aboutstatus = intent.getStringExtra("userstatus");

        //checks
        if (aboutname == null || aboutstatus == null) {
            finish();
            return;
        }

        /**
         * initialize variables
         */
        initializeScreen();
    }

    private void initializeScreen() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        /**
         * variables from the view
         */
        TextView uname = (TextView) findViewById(R.id.aboutname);
        upic = (GestureImageView) findViewById(R.id.aboutpic);
        ustatus = (TextView) findViewById(R.id.aboutstatus);
        CardView cardView = (CardView) findViewById(R.id.cardstatus);
        CardView aboutmore = (CardView) findViewById(R.id.aboutmore);
        Button button = (Button) findViewById(R.id.aboutbutton);
        aboutpb = (ProgressBar) findViewById(R.id.aboutpb);


        /**
         * click actions
         */
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetStatusDialog();
            }
        });

        aboutmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPreferences();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PIC_PICKER);
                aboutpb.setVisibility(View.VISIBLE);
            }
        });

        /**
         * set the values
         */
        uname.setText(aboutname);
        ustatus.setText(aboutstatus);

        if (aboutpic == null) {
            Glide.with(upic.getContext()).using(new FirebaseImageLoader()).load(imageRef)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(upic.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(upic);
            aboutpb.setVisibility(View.GONE);
        } else {
            Glide.with(upic.getContext())
                    .load(aboutpic)
                    .crossFade()
                    .thumbnail(0.5f)
                    .bitmapTransform(new CircleTransform(upic.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(upic);
            aboutpb.setVisibility(View.GONE);
        }
    }

    private void startPreferences() {
        Intent intent = new Intent(this, MyPreferenceActivity.class);
        intent.putExtra("username", Constants.USER_NAME);
        intent.putExtra("userstatus", Constants.USER_STATUS);
        intent.putExtra("statusvisibility", Constants.STATUS_VISIBLE);
        intent.putExtra("onlinevisibility", Constants.USER_VISIBLE);

        startActivity(intent);
    }

    public void showSetStatusDialog() {
        /* creates the instance of the Edit Dialog */
        EditStatusDialog dialogSetStatus = EditStatusDialog.newInstance(aboutstatus);
        dialogSetStatus.show(SettingsActivity.this.getFragmentManager(), "EditStatusDialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_PIC_PICKER && resultCode == RESULT_OK) {
            Uri selectedUmageUri = data.getData();
            /**
             * rename file to user id
             */
            /**
             * To do
             */
            StorageReference sender_photoRef = profile_photos.child(mUID);
            /**
             * compress the image
             */
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedUmageUri);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);

            } catch (IOException e) {
                e.printStackTrace();
            }
            /**
             * upload and refresh view
             */
            sender_photoRef.putFile(selectedUmageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUri = taskSnapshot.getDownloadUrl();
                    mUserphoto = downloadUri.toString();
                    userpreferences.edit().putString("userpic", mUserphoto).apply();
                    updateUser();
                    refreshStatusImage();
                    aboutpb.setVisibility(View.GONE);
                }
            }).addOnFailureListener(this, new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), "Failed to upload photo", Toast.LENGTH_LONG).show();
                    aboutpb.setVisibility(View.GONE);
                    e.printStackTrace();
                }
            });

        }
    }
}
