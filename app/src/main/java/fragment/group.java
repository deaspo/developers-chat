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
import adapter.Items_forums;
import adapter.RecyclerAdapterGroup;
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
public class group extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    //firebase database instances
    public static FirebaseDatabase gFirebaseDatabase;
    public static DatabaseReference gDatabaseReference;
    //adapter for groups
    public static List<Items_forums> groups;
    public static RecyclerAdapterGroup adapter;
    private static ChildEventListener gChildEventListener;
    private static SwipeRefreshLayout swipeRefreshLayout;
    //
    private RecyclerView recyclerView;
    //
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
        // showing refresh animation before making http call
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(true);
        }
        if (gChildEventListener == null) {
            gChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Items_forums items_forums = dataSnapshot.getValue(Items_forums.class);
                    groups.add(items_forums);
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Items_forums items_forums = dataSnapshot.getValue(Items_forums.class);
                    groups.remove(items_forums);
                    adapter.notifyDataSetChanged();
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
        adapter.notifyDataSetChanged();
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
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

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        //mMessageListView.setAdapter(groupsAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addOnItemTouchListener();
        ItemClickSupport.addTo(recyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                Items_forums selectedForum = groups.get(position);
                if (selectedForum != null) {
                    Intent intent = new Intent(getActivity(), GroupActivity.class);
                    String forumId = selectedForum.getForum_id();
                    String forumName = selectedForum.getTopic_name();
                    String currentUserMail = mUserEmail;
                    intent.putExtra("forumKey", forumId);
                    intent.putExtra("forumName", forumName);
                    intent.putExtra("usermail", currentUserMail);
                    /**
                     * satrt activity
                     */
                    startActivity(intent);

                }
            }

        });

        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        attachGroupDatabaseListener();
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
        attachGroupDatabaseListener();
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
        groups.clear();
    }

    @Override
    public void onPause() {
        super.onPause();
        detachGroupDatabaseListener();
        groups.clear();
    }

    @Override
    public void onRefresh() {
        attachGroupDatabaseListener();
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
