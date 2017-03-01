package fragment;

import android.content.Context;
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
import adapter.Items_forums;
import adapter.RecycleAdapterTopic;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link topic.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link topic#newInstance} factory method to
 * create an instance of this fragment.
 */
public class topic extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static List<Items_forums> topics;
    public static RecycleAdapterTopic topicsAdapter;
    //firebase database instances
    public static FirebaseDatabase tFirebaseDatabase;
    public static DatabaseReference tDatabaseReference;
    private static ChildEventListener tChildEventListener;
    private static SwipeRefreshLayout swipeRefreshLayout;
    //adapters for topics
    private RecyclerView recyclerView;
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
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (tChildEventListener == null) {
            tChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Items_forums items_forums = dataSnapshot.getValue(Items_forums.class);
                    topics.add(items_forums);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Items_forums items_forums = dataSnapshot.getValue(Items_forums.class);
                    topics.add(items_forums);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Items_forums items_forums = dataSnapshot.getValue(Items_forums.class);
                    topics.add(items_forums);
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
        topicsAdapter.notifyDataSetChanged();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
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
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_topic);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_topic);

        //mMessageListView.setAdapter(groupsAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(topicsAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        attachTopicDatabaseListener();
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
        attachTopicDatabaseListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        attachTopicDatabaseListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachTopicDatabaseListener();
    }

    @Override
    public void onStop() {
        super.onStop();
        detachTopicDatabaseListener();
    }

    @Override
    public void onRefresh() {
        attachTopicDatabaseListener();
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
