package fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.deaspostudios.devchats.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import adapter.User;
import adapter.UserAdapter;
import ui.Chat;

import static com.deaspostudios.devchats.MainActivity.mProfile;
import static com.deaspostudios.devchats.MainActivity.mStatus;
import static com.deaspostudios.devchats.MainActivity.mStatusVisble;
import static com.deaspostudios.devchats.MainActivity.mUserEmail;
import static com.deaspostudios.devchats.MainActivity.mUsername;
import static com.deaspostudios.devchats.MainActivity.mVisible;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link user.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link user#newInstance} factory method to
 * create an instance of this fragment.
 */
public class user extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static List<User> onlineUsers;
    //adapters for users
    public static UserAdapter usersAdapter;
    public static Boolean show_hide_user = false;
    //firebase database instances
    public static FirebaseDatabase uFirebaseDatabase;
    public static DatabaseReference uDatabaseReference;
    private static ChildEventListener uChildEventListener;
    private ListView mMessageListView;
    private ProgressBar mProgressBar;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public user() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment user.
     */
    // TODO: Rename and change types and number of parameters
    public static user newInstance(String param1, String param2) {
        user fragment = new user();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void attachUserDatabaseListener() {
        if (uChildEventListener == null) {
            uChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getEmail().contains(mUserEmail)) {
                        if (Boolean.valueOf(user.getUser_visible())) {
                            usersAdapter.add(user);
                        }
                        /*usersAdapter.sort(new Comparator<User>() {
                            @Override
                            public int compare(User user1, User user2) {
                                return user1.getName().compareTo(user2.getName());
                            }
                        });*/
                        usersAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        usersAdapter.remove(user);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Failed to read value
                    Log.w("TAG:", "Failed to read value.", databaseError.toException());
                }
            };
            uDatabaseReference.addChildEventListener(uChildEventListener);
        }

    }

    public static void detachUserDatabaseListener() {
        if (uChildEventListener != null) {
            uDatabaseReference.removeEventListener(uChildEventListener);
            uChildEventListener = null;
        }

    }

    public static void setUsername(String uName, String mEncodedEmail, String uUid) {
        User user_logged = new User(uName, mEncodedEmail, uUid, DateFormat.getDateTimeInstance().format(new Date()), mProfile, mStatus, mStatusVisble, mVisible);
        if (user_logged != null) {
            uDatabaseReference.child(user_logged.getUid()).setValue(user_logged);
        }


    }

    public static void removeUserName(String uName, String mEncodedEmail, String uUid) {
        User user_logged = new User(uName, mEncodedEmail, uUid, DateFormat.getDateTimeInstance().format(new Date()), mProfile, mStatus, mStatusVisble, mVisible);
        if (user_logged != null) {
            uDatabaseReference.child(user_logged.getUid()).removeValue();
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        getActivity().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));

        //initializes
        mProgressBar = (ProgressBar) view.findViewById(R.id.userProgressBar);
        mMessageListView = (ListView) view.findViewById(R.id.userListView);

        mMessageListView.setAdapter(usersAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selected = (TextView) view.findViewById(R.id.itemName);
                TextView userid = (TextView) view.findViewById(R.id.user_uid);
                String sele_name = selected.getText().toString();
                String uid = userid.getText().toString();
                if (sele_name != null && sele_name != mUsername && sele_name != "You") {
                    Intent intent = new Intent(getActivity(), Chat.class);
                    /* Get the user name and id using
                     * ref and then grab the key.
                     */
                    intent.putExtra("username", sele_name);
                    intent.putExtra("userid", uid);
                    /* Starts an active showing the details for the selected list */
                    startActivity(intent);
                }
            }
        });

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        attachUserDatabaseListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachUserDatabaseListener();
        usersAdapter.clear();
        onlineUsers.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachUserDatabaseListener();
        usersAdapter.clear();
        onlineUsers.clear();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUsername != null) {
            attachUserDatabaseListener();
        }

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
