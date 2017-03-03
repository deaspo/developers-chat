package com.deaspostudios.devchats;

import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import adapter.Items_forums;
import adapter.RecyclerAdapterGroup;
import adapter.RecyclerAdapterTopic;
import adapter.RecyclerAdapterUser;
import adapter.User;
import adapter.UserAdapter;
import dialog.AddGroupDialog;
import dialog.AddTopicDialog;
import fragment.fav;
import fragment.group;
import fragment.topic;
import fragment.user;

import static fragment.fav.attachChatUsersDb;
import static fragment.fav.cDatabaseReference;
import static fragment.fav.cFirebaseDatabase;
import static fragment.fav.chatsFirebaseDatabase;
import static fragment.fav.chatsUsersDb;
import static fragment.fav.chattingAdapter;
import static fragment.fav.chattingUsers;
import static fragment.fav.deatchChatDb;
import static fragment.group.adapter;
import static fragment.group.attachGroupDatabaseListener;
import static fragment.group.detachGroupDatabaseListener;
import static fragment.group.gDatabaseReference;
import static fragment.group.gFirebaseDatabase;
import static fragment.group.groups;
import static fragment.topic.attachTopicDatabaseListener;
import static fragment.topic.detachTopicDatabaseListener;
import static fragment.topic.tDatabaseReference;
import static fragment.topic.tFirebaseDatabase;
import static fragment.topic.topics;
import static fragment.topic.topicsAdapter;
import static fragment.user.attachUserDatabaseListener;
import static fragment.user.detachUserDatabaseListener;
import static fragment.user.onlineUsers;
import static fragment.user.removeUserName;
import static fragment.user.setUsername;
import static fragment.user.uDatabaseReference;
import static fragment.user.uFirebaseDatabase;
import static fragment.user.usersAdapter;

public class MainActivity extends AppCompatActivity implements fav.OnFragmentInteractionListener, group.OnFragmentInteractionListener, topic.OnFragmentInteractionListener, user.OnFragmentInteractionListener, SearchView.OnQueryTextListener {

    public static final String ANONYMOUS = "anonymous";
    public static final int RC_SIGN_IN = 1;
    // tags used to attach the fragments
    private static final String TAG_CHAT = "chats";
    private static final String TAG_GROUP = "groups";
    private static final String TAG_TOPIC = "topics";
    private static final String TAG_USERS = "active users";
    private static final String TAG_SETTINGS = "settings";
    public static int RC_Initial = 0;
    public static int pageItemIndex = 0;
    public static String mUsername, mUserphoto, mUserEmail;
    public static Boolean mVisible, mStatusVisble;
    public static DatabaseReference usersDbRef;
    public static ChildEventListener usersChildEventListener;
    public static String mUID;
    public static String mEncodedEmail;
    public static String mProfile;
    public static String mStatus;
    // index to identify current nav menu item
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_CHAT;
    public static StorageReference storageReference, imageRef;
    /**
     * to save db offline when there is no internet connection
     */
    static boolean localCache = false;
    private static int SORT_ORDER = 0;
    //tabs specific
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    //Firebase auth
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    //to new users to the db
    private FirebaseDatabase usersDb;
    private MenuItem searchMenuItem;
    private SearchView searchView;
    /**
     * tab icons
     *
     * @param savedInstanceState
     */

    private int[] tabIcons = {
            R.drawable.ic_group_add_black_24dp,
            R.drawable.ic_group_black_24dp,
            R.drawable.ic_chat_black_24dp,
            R.drawable.ic_supervisor_account_black_24dp
    };
    /**
     * Navigation Drawer imporst and @params
     *
     * @param
     * @return
     */
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;

    public static void detachUsersListeners() {
        if (usersChildEventListener != null) {
            usersDbRef.removeEventListener(usersChildEventListener);
            usersChildEventListener = null;
        }
    }

    public static String escapeEmail(String mail) {
        return mail.replace(".", ",");
    }

    public static String unescapeEmail(String mail) {
        return mail.replace(",", ".");
    }

    public static String escapeSpace(String name) {
        return name.replace(" ", "_");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * locally chache db
         */
        if (!localCache) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            localCache = true;
        }

        //auth
        mFirebaseAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();
        imageRef = storageReference.child("profile_photos/ic_person_black_24dp.png");

        //initialize database
        uFirebaseDatabase = FirebaseDatabase.getInstance();
        tFirebaseDatabase = FirebaseDatabase.getInstance();
        gFirebaseDatabase = FirebaseDatabase.getInstance();
        usersDb = FirebaseDatabase.getInstance();

        //active users db
        uDatabaseReference = uFirebaseDatabase.getReference().child("activeUsers");

        //active topic db
        tDatabaseReference = tFirebaseDatabase.getReference().child("topics");
        gDatabaseReference = gFirebaseDatabase.getReference().child("groups");

        //users
        usersDbRef = usersDb.getReference().child("users");

        /**
         * setting up  the chats viewer
         */
        cFirebaseDatabase = FirebaseDatabase.getInstance();
        cDatabaseReference = cFirebaseDatabase.getReference().child("chats");

        /**
         * setup the navigation drawer and initialiszes
         */
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        setUpNavigationView();

        //setting up the tabs
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        //initialize chats tab
        chattingUsers = new ArrayList<>();
        chattingAdapter = new UserAdapter(this, R.layout.items, chattingUsers);

        //initialize users
        onlineUsers = new ArrayList<>();
        usersAdapter = new RecyclerAdapterUser(this, onlineUsers);

        //initialize topics
        topics = new ArrayList<>();
        topicsAdapter = new RecyclerAdapterTopic(this, topics);
        /**
         * testing with the activelistadapter
         */

        //initialize groups
        groups = new ArrayList<>();
        adapter = new RecyclerAdapterGroup(this, groups);
        //groupsAdapter = new ForumsAdapter(this, R.layout.item_forums, groups);

        //listeners for pager
        setViewPager();

        //Authentication
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    OnSignedInialize(user.getDisplayName(), user.getEmail(), user.getUid());

                } else {
                    OnSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(true)//tells the app to stay logged on resumed opening of the app
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
        CheckConnection connection = new CheckConnection();
        /**
         * sets the initial preference values
         */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        this.registerReceiver(connection, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    /**
     * assigning the right fragment on navigation drawer item click
     *
     * @param
     */

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    case R.id.nav_chat:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_CHAT;
                        viewPager.setCurrentItem(2, true);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_groups:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_GROUP;
                        viewPager.setCurrentItem(0, true);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_topics:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_TOPIC;
                        viewPager.setCurrentItem(1, true);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_users:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_USERS;
                        viewPager.setCurrentItem(3, true);
                        drawer.closeDrawers();
                        break;
                    case R.id.nav_settings: /* need to have a settings activity */
                        startSettings();
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUs.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_CHAT;
                        viewPager.setCurrentItem(0, true);
                        break;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new group(), "Groups");
        adapter.addFragment(new topic(), "Topics");
        adapter.addFragment(new fav(), "Chats");
        adapter.addFragment(new user(), "Online");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<Items_forums> filteredItems = filter(topics, newText);
        final List<Items_forums> filteredGroup = filter(groups, newText);

        topicsAdapter.setFilter(filteredItems);
        adapter.setFilter(filteredGroup);
        return true;
    }

    private List<Items_forums> filter(List<Items_forums> models, String query) {
        query = query.toLowerCase();
        final List<Items_forums> filteredModelList = new ArrayList<>();
        for (Items_forums model : models) {
            final String text = model.getTopic_name().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        if (mUsername != null && mUserEmail != null && mUID != null) {
            removeUserName(mUsername, mUserEmail, mUID); /* only carry out the action if there is an existing user */
        }

        detachUserDatabaseListener();
        onlineUsers.clear();
        usersAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mUsername != null && mUserEmail != null && mUID != null) {
            removeUserName(mUsername, mUserEmail, mUID); /* only carry out the action if there is an existing user */
        }
        detachUserDatabaseListener();
        onlineUsers.clear();
        usersAdapter.notifyDataSetChanged();
    }

    private void OnSignedInialize(String username, String useremail, String uid) {
        if (username == null) {
            mUsername = "No name";
        } else {
            mUsername = username;
        }
        mUserEmail = useremail;
        /**
         * setting the preference values
         */

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        mStatus = preferences.getString(MyPreferenceActivity.KEY_USER_STATUS, "");
        if (mStatusVisble == null || mVisible == null) {
            mStatusVisble = Boolean.valueOf(preferences.getBoolean(MyPreferenceActivity.KEY_STATUS_VISIBILITY, true));
            mVisible = Boolean.valueOf(preferences.getBoolean(MyPreferenceActivity.KEY_ONLINE_VISIBILITY, true));
        }
        setDefaults();
        addUser(username, useremail, uid);
        setUsername(mUsername, useremail, uid);
        //chats
        chatsFirebaseDatabase = FirebaseDatabase.getInstance();
        chatsUsersDb = chatsFirebaseDatabase.getReference().child("chats").child(mUID).child("conversations");
        System.out.println("Logged in user " + username);
        if (mUsername != null) {
            attachChatUsersDb();
            attachUserDatabaseListener();
        }


    }

    private void setDefaults() {
        Constants.USER_NAME = mUsername;
        Constants.USER_STATUS = mStatus;
    }

    private void OnSignedOutCleanup() {
        if (mUID != null && mUsername != null && mUID != null) {
            removeUserName(mUsername, mEncodedEmail, mUID);
        }

        mUsername = ANONYMOUS;
        detachUserDatabaseListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (pageItemIndex == 2) {
            getMenuInflater().inflate(R.menu.main, menu);
        } else if (pageItemIndex == 0) {
            getMenuInflater().inflate(R.menu.menu_groups, menu);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchMenuItem = menu.findItem(R.id.app_bar_search_groups);
            searchView = (SearchView) searchMenuItem.getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);

            searchView.setOnQueryTextListener(this);
        } else if (pageItemIndex == 1) {
            getMenuInflater().inflate(R.menu.menu_topics, menu);
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchMenuItem = menu.findItem(R.id.app_bar_search_topic);
            searchView = (SearchView) searchMenuItem.getActionView();

            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setSubmitButtonEnabled(true);

            searchView.setOnQueryTextListener(this);

        } else if (pageItemIndex == 3) {
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    private void setViewPager() {
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    pageItemIndex = 0;
                    invalidateOptionsMenu();
                } else if (position == 1) {
                    pageItemIndex = 1;
                    invalidateOptionsMenu();
                } else if (position == 2) {
                    pageItemIndex = 2;
                    invalidateOptionsMenu();
                } else if (position == 3) {
                    pageItemIndex = 3;
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {


            } else if (resultCode == RESULT_CANCELED) {
                finish();

            } else {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void showAddTopicDialog(View view) {
        /*creates an instance of add topic dialog fragment and show it */
        DialogFragment dialogAddTopic = AddTopicDialog.newInstance(mEncodedEmail);
        dialogAddTopic.show(MainActivity.this.getFragmentManager(), "AddTopicDialog");

    }

    public void showAddGroupDialog(View view) {
        /*creates an instance of add topic dialog fragment and show it */
        DialogFragment dialogAddGroup = AddGroupDialog.newInstance(mEncodedEmail);
        dialogAddGroup.show(MainActivity.this.getFragmentManager(), "AddGroupDialog");

    }

    private void addUser(String mUsername, String mEncodedEmail, String uUid) {
        mUID = uUid;
        User user = new User(mUsername, mEncodedEmail, uUid, DateFormat.getDateTimeInstance().format(new Date()), mProfile, mStatus, mStatusVisble, mVisible);
        usersDbRef.child(uUid).setValue(user);

        /*chatsFirebaseDatabase =FirebaseDatabase.getInstance();
        chatsUsersDb = cDatabaseReference.child(mUID).child("conversations");*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                //signs out
                AuthUI.getInstance().signOut(this);
                return true;
            case R.id.action_sort_topic:
                sortTopics();
                return true;
            case R.id.action_sort_groups:
                sortGroups();
                return true;
            case R.id.action_refresh:
                refreshAllViews();
                return true;
            case R.id.action_setting:
                startSettings();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public void startSettings() {
        Intent intent = new Intent(this, MyPreferenceActivity.class);
        intent.putExtra("username", Constants.USER_NAME);
        intent.putExtra("userstatus", Constants.USER_STATUS);
        intent.putExtra("statusvisibility", Constants.STATUS_VISIBLE);
        intent.putExtra("onlinevisibility", Constants.USER_VISIBLE);

        /**
         * starts setting activity
         */
        startActivity(intent);
    }

    public void sortTopics() {
        if (SORT_ORDER == 1) {
            alphabeticalSorting(topics);
            SORT_ORDER = 0;
        } else if (SORT_ORDER == 0) {
            reverseSorting(topics);
            SORT_ORDER = 1;
        }
    }

    public void sortGroups() {
        if (SORT_ORDER == 1) {
            alphabeticalSorting(groups);
            SORT_ORDER = 0;
        } else if (SORT_ORDER == 0) {
            reverseSorting(groups);
            SORT_ORDER = 1;
        }
    }

    private void reverseSorting(List<Items_forums> forum) {
        Collections.sort(forum, new Comparator<Items_forums>() {
            @Override
            public int compare(Items_forums o1, Items_forums o2) {
                return -o1.getTopic_name().compareTo(o2.getTopic_name());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void alphabeticalSorting(List<Items_forums> forum) {
        Collections.sort(forum, new Comparator<Items_forums>() {
            @Override
            public int compare(Items_forums o1, Items_forums o2) {
                return o1.getTopic_name().compareTo(o2.getTopic_name());
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void userToken() {
        /**
         * extracts the user token
         */
        // Get token
        String token = FirebaseInstanceId.getInstance().getToken();

        // Log and toast
        Log.d("Token for Polycarp", token);
        System.out.println("Token for Polycarp " + token);
    }

    private void refreshAllViews() {
        attachGroupDatabaseListener();
        attachTopicDatabaseListener();
        attachUserDatabaseListener();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * check the network state
     */
    public class CheckConnection extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
                    mobile != null && mobile.isConnectedOrConnecting();

            if (isConnected) {

                if (mUsername != null) {
                    attachUserDatabaseListener();
                    attachChatUsersDb();
                }
                attachTopicDatabaseListener();
                attachGroupDatabaseListener();

            } else {

                detachUserDatabaseListener();
                onlineUsers.clear();
                usersAdapter.notifyDataSetChanged();
                /**
                 * topics db
                 */
                detachTopicDatabaseListener();
                /**
                 * groups db
                 */
                detachGroupDatabaseListener();
                /**
                 * detach chat
                 */
                deatchChatDb();
                Toast.makeText(context, "Connection Lost", Toast.LENGTH_LONG).show();
            }
        }
    }


}
