package com.itech.reconapp.heading;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;
import android.content.Context;
public class HeadingManager implements HeadLocationListener {
    HUDHeadingManager mHUDHeadingManager = null;
    HeadingEventListener headingEventListener;
    Context context;

    public HeadingManager(Context context) {
        this.context = context;
        mHUDHeadingManager = (HUDHeadingManager) HUDOS.getHUDService(HUDOS.HUD_HEADING_SERVICE);
        headingEventListener = (HeadingEventListener) context;
    }

    public void start() {
        mHUDHeadingManager.register(this);
    }

    public void stop() {
        mHUDHeadingManager.unregister(this);
    }

    @Override
    public void onHeadLocation(float v, float v1, float v2) {
        headingEventListener.onHeadingChanged(v);
    }
}