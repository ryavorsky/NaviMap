package com.navimap.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * User: Marat Shaykhutdinov
 * Company: Provectus-it
 * Date: 18.02.14
 * Time: 17:03
 */
public class ApplicationManager {
    private static ApplicationManager instance = new ApplicationManager();

    private Context context;

    public static ApplicationManager getInstance() {
        return instance;
    }

    public synchronized void init(Context context) {
        if (this.context == null) {
            this.context = context;
        }
    }


    public Context getContext() {
        return context;
    }


    public static boolean isOnline() {
        return isOnline(getInstance().getContext());
    }

    public static boolean isOnline(Context context) {
        if (context != null) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        return true;
    }
}
