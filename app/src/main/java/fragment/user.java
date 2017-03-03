package fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deaspostudios.devchats.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import Widgets.DividerItemDecoration;
import Widgets.ItemClickSupport;
import adapter.RecyclerAdapterUser;
import adapter.User;
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
public class user extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    //adapters for users
    public static List<User> onlineUsers;
    public static RecyclerAdapterUser usersAdapter;
    //firebase database instances
    public static FirebaseDatabase uFirebaseDatabase;
    public static DatabaseReference uDatabaseReference;
    private static ChildEventListener uChildEventListener;
    /**
     *
     */
    private static SwipeRefreshLayout swipeRefreshLayout;
    //
    private RecyclerView recyclerView;
    //

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
        // showing refresh animation before making http call
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (uChildEventListener == null) {
            uChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getEmail().contains(mUserEmail)) {
                        if (Boolean.valueOf(user.getUser_visible())) {
                            onlineUsers.add(user);
                        }
                        //usersAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (!user.getEmail().contains(mUserEmail)) {
                            onlineUsers.remove(user);
                            usersAdapter.notifyDataSetChanged();
                        }
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
        usersAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
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
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_user);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_user);

        //mMessageListView.setAdapter(groupsAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addOnItemTouchListener();
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                User user = onlineUsers.get(position);
                if (user != null && !user.getName().equals(mUsername) && !user.getName().equals("You")) {
                    Intent intent = new Intent(getActivity(), Chat.class);
                    intent.putExtra("username", user.getName());
                    intent.putExtra("userid", user.getUid());
                    /**
                     * start activity
                     */
                    startActivity(intent);
                }
            }

        });

        recyclerView.setAdapter(usersAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        attachUserDatabaseListener();
                                    }
                                }
        );
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
        onlineUsers.clear();
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachUserDatabaseListener();
        onlineUsers.clear();
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mUsername != null) {
            attachUserDatabaseListener();
        }

    }

    @Override
    public void onRefresh() {
        attachUserDatabaseListener();
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
