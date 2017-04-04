package com.itech.reconapp.misc;

import android.util.Log;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.IHUDConnectivity;

/**
 * Controls access to the HUD Connectivity Manager
 */
public class HUDManager {
    static final String TAG = HUDManager.class.getName();
    private static HUDConnectivityManager mHUDConnectivityManager = null;

    protected HUDManager() {

    }

    public static HUDConnectivityManager getInstance() {
        if(mHUDConnectivityManager == null) {
            initConnectivity();
        }
        return mHUDConnectivityManager;
    }

    static IHUDConnectivity mConnectivityListener = new IHUDConnectivity() {
        @Override
        public void onDeviceName(String s) {
            Log.d(TAG, "onDeviceName " + s);
        }

        @Override
        public void onConnectionStateChanged(ConnectionState connectionState) {
            Log.d(TAG, "onConnectionStateChanged " + connectionState.toString());
        }

        @Override
        public void onNetworkEvent(NetworkEvent networkEvent, boolean b) {
            String bool = b ? "true" : "false";
            Log.d(TAG, "onNetworkEvent " + networkEvent.toString() + " " + bool);
        }
    };

    static void initConnectivity() {
        System.load("/system/lib/libreconinstruments_jni.so");
        mHUDConnectivityManager = (HUDConnectivityManager) HUDOS.getHUDService(HUDOS.HUD_CONNECTIVITY_SERVICE);
        mHUDConnectivityManager.register(mConnectivityListener);
    }

    public static void onDestroy() {
        mHUDConnectivityManager.unregister(mConnectivityListener);
    }
}