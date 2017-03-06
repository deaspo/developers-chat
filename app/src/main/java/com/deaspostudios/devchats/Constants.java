package com.deaspostudios.devchats;

/**
 * Created by polyc on 31/01/2017.
 */

public final class Constants {

    public static final String TAG = "chatbubbles";


    /**
     * Constants for Firebase object properties
     */
    public static final String FIREBASE_PROPERTY_TIMESTAMP = "timestamp";


    /**
     * Constants for bundles, extras and shared preferences keys
     */
    public static final String KEY_LIST_NAME = "LIST_NAME";
    public static final String KEY_LISTREF = "LIST_REF";
    public static final String KEY_LIST_ID = "LIST_ID";
    public static final String KEY_ENCODED_EMAIL = "ENCODED_EMAIL";
    public static final String KEY_LIST_OWNER = "LIST_OWNER";
    public static final String KEY_PREF_SORT_ORDER_LISTS = "PERF_SORT_ORDER_LISTS";



    /**
     * Constant for sorting
     */
    public static final String ORDER_BY_KEY = "orderByPushKey";
    public static final String ORDER_BY_OWNER_EMAIL = "orderByOwnerEmail";

    /**
     * Constants for Settings
     */
    public static String USER_NAME = "USER_NAME";
    public static String USER_STATUS = "Hey there am also a developer!";
    public static String STATUS_VISIBLE = "true";
    public static String USER_PIC = "USER_PIC";
    public static String USER_BG = "USER_BG";
    public static String USER_VISIBLE = "true";

    /**
     *
     */
    public static final String SHARED_PREF = "ah_firebase";
    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // type of push messages
    public static final int PUSH_TYPE_CHATROOM = 1;
    public static final int PUSH_TYPE_USER = 2;


}
