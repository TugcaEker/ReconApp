package itech.com.reconapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import com.reconinstruments.ui.list.ReconListView;
import com.reconinstruments.ui.list.SimpleListActivity;
import com.reconinstruments.ui.list.SimpleListItem;
import com.reconinstruments.ui.list.StandardListItem;
import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.metrics.HUDMetricsID;
import com.reconinstruments.os.metrics.HUDMetricsManager;
import com.reconinstruments.os.metrics.MetricsValueChangedListener;
import com.reconinstruments.os.hardware.sensors.HUDHeadingManager;
import com.reconinstruments.os.hardware.sensors.HeadLocationListener;

import java.util.ArrayList;

public class MainActivity extends Activity implements HeadLocationListener {
    // ReconOS apps and components:
    static final String ENGAGE_PHONE_CONNECTION =
            "com.reconinstruments.connectdevice.CONNECT";
    static final String NOTIFICATION_CENTER = "com.reconinstruments.messagecenter.frontend";
    static final String CAMERA_APP = "com.reconinstruments.camera";
    static final String GALLERY_APP = "com.reconinstruments.camera.gallery";
    static final String COMPASS = "com.reconinstruments.compass.CALIBRATE";
    static final String INSTALLED_APPS = "com.reconinstruments.jetappsettings.apps";
    static final String RECON_SETTINGS = "com.reconinstruments.jetappsettings.settings";
    static final String POWER_MENU = "com.reconinstruments.power.RECON_POWER_MENU";
    // The setting page for the enterprise launcher app.
    static final String SETTINGS = "com.reconinstruments.enterprise.settings";

    private final String TAG = this.getClass().getSimpleName();

    private HUDHeadingManager mHUDHeadingManager = null;

    private static double PIXELS_PER_45_DEGREES = 85.0;

    float mUserHeading = 0.0f;
    ImageView mCompassBar = null;
    ImageView mCompassUnderline = null;

    boolean mIsResumed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

		mHUDHeadingManager = (HUDHeadingManager) HUDOS.getHUDService(HUDOS.HUD_HEADING_SERVICE);


		mCompassBar = (ImageView) findViewById(R.id.compass_bar);
		mCompassUnderline = (ImageView) findViewById(R.id.compass_underline);

		Matrix matrix = new Matrix();
		matrix.reset();
		matrix.postTranslate(-428, 0);

		mCompassBar.setScaleType(ImageView.ScaleType.MATRIX);
		mCompassBar.setImageMatrix(matrix);

    }


    @Override
    public void onResume() {
        super.onResume();
        mHUDHeadingManager.register(this);
        mIsResumed = true;
    }

    @Override
    public void onPause()  {
        mHUDHeadingManager.unregister(this);
        mIsResumed = false;
        super.onPause();
    }


    @Override
    public void onHeadLocation(float yaw, float pitch, float roll) {
        if(!this.mIsResumed || (Float.isNaN(yaw))) {
            return;
        }

        float newHeading = yaw;

        if(mUserHeading > 270.0f && newHeading < 90.0f) {
            mUserHeading = mUserHeading - 360.0f;
        } else if (mUserHeading < 90.0f && newHeading > 270.0f) {
            newHeading = newHeading - 360.0f;
        }

        mUserHeading = (float) ((4.0*mUserHeading + newHeading)/5.0); // smooth heading

        /* Sinir kontrol */
        if(mUserHeading > 360.0f) mUserHeading -= 360.0f;
        else if(mUserHeading < 000.0f) mUserHeading += 360.0f;


        int offset = (mUserHeading >= 315f && mUserHeading <= 360) ? -(int)PIXELS_PER_45_DEGREES*7 : (int)PIXELS_PER_45_DEGREES;
        int x = (int)(mUserHeading / 360.0 * (8.0*PIXELS_PER_45_DEGREES)) + offset;

        mCompassBar.getImageMatrix().reset();
        mCompassBar.getImageMatrix().postTranslate(-x, 0);
        mCompassBar.invalidate();
    }
}