package com.deaspostudios.devchats;

import android.app.Application;
import android.os.Handler;

/**
 * Created by polyc on 31/01/2017.
 */

public class App extends Application {
    public static volatile Handler applicationHandler = null;
    private static App Instance;

    public static App getInstance() {
        return Instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Instance = this;

        applicationHandler = new Handler(getInstance().getMainLooper());

        NativeLoader.initNativeLibs(App.getInstance());

    }
}
