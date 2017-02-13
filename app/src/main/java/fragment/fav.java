package fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.util.List;

import adapter.User;
import adapter.UserAdapter;
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
public class fav extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static List<User> chattingUsers;
    //adapters for users
    public static UserAdapter chattingAdapter;
    //firebase database instances
    public static FirebaseDatabase cFirebaseDatabase, chatsFirebaseDatabase;
    //    private static ValueEventListener chatUserEventListener;
    public static DatabaseReference cDatabaseReference, chatsUsersDb;
    private static ChildEventListener cChildEventListener, chatUserEventListener;
    private static String userId;
    private ListView chatListView;
    private ProgressBar mProgressBar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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
            chatsUsersDb.addChildEventListener(chatUserEventListener);
        }
    }

    public static void deatchChatDb() {
        if (chatUserEventListener != null) {
            chatsUsersDb.removeEventListener(chatUserEventListener);
            chatUserEventListener = null;
        }
    }

    public static void addUserTotheList(final String uid) {
        usersChildEventListener = usersDbRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    if (user.getUid().contains(uid)) {
                        if (chattingAdapter != null) {
                            if (chattingAdapter.objects != null) {
                                if (!chattingAdapter.objects.contains(user)) {
                                    chattingAdapter.add(user);
                                }
                            } else {
                                chattingAdapter.add(user);
                            }
                        }
                    }
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
        });

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
        mProgressBar = (ProgressBar) view.findViewById(R.id.chatProgressBar);
        chatListView = (ListView) view.findViewById(R.id.chatListView);

        chatListView.setAdapter(chattingAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);


        chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
        if (chattingAdapter.objects != null) {
            chattingAdapter.objects.clear();
        }
        attachChatUsersDb();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (chattingAdapter != null) {
            chattingAdapter.clear();
        }
        deatchChatDb();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (chattingAdapter != null) {
            chattingAdapter.clear();
        }
        deatchChatDb();
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
