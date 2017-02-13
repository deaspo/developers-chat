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

import java.util.Comparator;
import java.util.List;

import adapter.ForumsAdapter;
import adapter.Items_forums;
import ui.GroupActivity;

import static com.deaspostudios.devchats.MainActivity.mUserEmail;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link group.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link group#newInstance} factory method to
 * create an instance of this fragment.
 */
public class group extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static List<Items_forums> groups;
    //adapter for groups
    public static ForumsAdapter groupsAdapter;
    //firebase database instances
    public static FirebaseDatabase gFirebaseDatabase;
    public static DatabaseReference gDatabaseReference;
    private static ChildEventListener gChildEventListener;
    private ListView mMessageListView;
    private ProgressBar mProgressBar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public group() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment group.
     */
    // TODO: Rename and change types and number of parameters
    public static group newInstance(String param1, String param2) {
        group fragment = new group();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static void attachGroupDatabaseListener() {
        if (gChildEventListener == null) {
            gChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Items_forums items_forums = dataSnapshot.getValue(Items_forums.class);
                    groupsAdapter.add(items_forums);
                    groupsAdapter.sort(new Comparator<Items_forums>() {
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
            gDatabaseReference.addChildEventListener(gChildEventListener);
        }

    }

    public static void detachGroupDatabaseListener() {
        if (gChildEventListener != null) {
            gDatabaseReference.removeEventListener(gChildEventListener);
            gChildEventListener = null;
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
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        getActivity().getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.background));

        //initializes
        mProgressBar = (ProgressBar) view.findViewById(R.id.groupProgressBar);
        mMessageListView = (ListView) view.findViewById(R.id.groupListView);

        mMessageListView.setAdapter(groupsAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

        mMessageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /**
                 * implementing the forums adapter
                 */
                Items_forums selectedforum = groupsAdapter.getItem(i);
                if (selectedforum != null) {
                    Intent intent = new Intent(getActivity(), GroupActivity.class);
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
        attachGroupDatabaseListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachGroupDatabaseListener();
        groupsAdapter.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachGroupDatabaseListener();
        groupsAdapter.clear();
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
