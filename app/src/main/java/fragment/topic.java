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

import com.deaspostudios.devchats.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.Comparator;
import java.util.List;

import adapter.ForumsAdapter;
import adapter.Items_forums;
import ui.TopicActivity;

import static com.deaspostudios.devchats.MainActivity.mUserEmail;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link topic.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link topic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class topic extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static Query orderedActiveUserListsRef;
    public static List<Items_forums> topics;
    //adapters for topics
    public static ForumsAdapter topicsAdapter;
    //firebase database instances
    public static FirebaseDatabase tFirebaseDatabase;
    public static DatabaseReference tDatabaseReference;
    private static ChildEventListener tChildEventListener;
    private ListView mMessageListView;
    private ProgressBar mProgressBar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public topic() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment topic.
     */
    // TODO: Rename and change types and number of parameters
    public static topic newInstance(String param1, String param2) {
        topic fragment = new topic();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void attachTopicDatabaseListener() {
        if (tChildEventListener == null) {
            tChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Items_forums items_forums = dataSnapshot.getValue(Items_forums.class);
                    topicsAdapter.add(items_forums);
                    topicsAdapter.sort(new Comparator<Items_forums>() {
                        @Override
                        public int compare(Items_forums o1, Items_forums o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    });
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
            tDatabaseReference.addChildEventListener(tChildEventListener);
        }

    }

    public static void detachTopicDatabaseListener() {
        if (tChildEventListener != null) {
            tDatabaseReference.removeEventListener(tChildEventListener);
            tChildEventListener = null;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_topic, container, false);
        getActivity().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));

        //initializes
        mProgressBar = (ProgressBar) view.findViewById(R.id.topicProgressBar);
        mMessageListView = (ListView) view.findViewById(R.id.topicListView);

        /**
         * want to change to activelistadapter
         */
        //mMessageListView.setAdapter(topicsAdapter);
        mMessageListView.setAdapter(topicsAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /**
                 * implementing the forums adapter
                 */
                Items_forums selectedforum = topicsAdapter.getItem(i);
                if (selectedforum != null) {
                    Intent intent = new Intent(getActivity(), TopicActivity.class);
                    /*
                      * Gets the right data to pass
                     */
                    String forumId = selectedforum.getForum_id();
                    String currentUserMail = mUserEmail;
                    intent.putExtra("forumKey", forumId);
                    intent.putExtra("usermail", currentUserMail);
                    /**
                     * start the activity with details of the list
                     */
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
    }

    @Override
    public void onResume() {
        super.onResume();
        attachTopicDatabaseListener();
        Query orderedActiveUserListsRef;
    }

    @Override
    public void onPause() {
        super.onPause();
        detachTopicDatabaseListener();
        topicsAdapter.clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachTopicDatabaseListener();
        topicsAdapter.clear();
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
