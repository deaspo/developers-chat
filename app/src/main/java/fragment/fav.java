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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deaspostudios.devchats.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import Widgets.DividerItemDecoration;
import Widgets.ItemClickSupport;
import adapter.RecyclerAdapterUser;
import adapter.User;
import ui.Chat;

import static com.deaspostudios.devchats.MainActivity.mUsername;
import static com.deaspostudios.devchats.MainActivity.usersChildEventListener;
import static com.deaspostudios.devchats.MainActivity.usersDbRef;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link fav.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link fav#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fav extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static List<User> chattingUsers;
    //adapters for users
    public static RecyclerAdapterUser chattingAdapter;
    //firebase database instances
    public static FirebaseDatabase cFirebaseDatabase, chatsFirebaseDatabase;
    //    private static ValueEventListener chatUserEventListener;
    public static DatabaseReference cDatabaseReference, chatsUsersDb;
    private static ChildEventListener cChildEventListener, chatUserEventListener;
    /**
     *
     */
    private static SwipeRefreshLayout swipeRefreshLayout;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //
    private RecyclerView recyclerView;
    //

    private OnFragmentInteractionListener mListener;

    public fav() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment fav.
     */
    // TODO: Rename and change types and number of parameters
    public static fav newInstance(String param1, String param2) {
        fav fragment = new fav();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void attachChatUsersDb() {
        // showing refresh animation before making http call
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (chatUserEventListener == null) {
            chatUserEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String key = dataSnapshot.getKey();
                    if (key != null) {
                        addUserTotheList(key);
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            if (chatsUsersDb != null) {
                chatsUsersDb.addChildEventListener(chatUserEventListener);
            }

        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public static void deatchChatDb() {
        if (chatUserEventListener != null) {
            if (chatsUsersDb != null) {
                chatsUsersDb.removeEventListener(chatUserEventListener);
            }

            chatUserEventListener = null;
        }
    }

    public static void addUserTotheList(final String uid) {
        // showing refresh animation before making http call
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        usersChildEventListener = usersDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getUid().contains(uid)) {
                        if (chattingAdapter != null) {
                            if (chattingAdapter.objects != null) {
                                if (!chattingAdapter.objects.contains(user)) {
                                    chattingUsers.add(user);
                                }
                            } else {
                                chattingUsers.add(user);
                            }
                        }
                    }
                }
                chattingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //usersDbRef.addChildEventListener(usersChildEventListener);
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_fav, container, false);
        getActivity().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));

        //initializes
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_chat);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_chat);

        //mMessageListView.setAdapter(groupsAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addOnItemTouchListener();
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                User user = chattingUsers.get(position);
                if (user != null && !user.getName().equals(mUsername) && !user.getName().equals("You")) {
                    Intent intent = new Intent(getActivity(), Chat.class);
                    intent.putExtra("username", user.getName());
                    intent.putExtra("userid", user.getUid());
                    intent.putExtra("token", user.getDevicetoken());
                    /**
                     * start activity
                     */
                    startActivity(intent);
                }
            }

        });

        recyclerView.setAdapter(chattingAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        attachChatUsersDb();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart() {
        super.onStart();
        attachChatUsersDb();
    }

    @Override
    public void onResume() {
        super.onResume();
        attachChatUsersDb();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
        /*deatchChatDb();
        chattingUsers.clear();
        chattingAdapter.notifyDataSetChanged();*/
    }

    @Override
    public void onRefresh() {
        attachChatUsersDb();
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
