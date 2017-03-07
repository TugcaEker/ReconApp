package itech.com.reconapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.reconinstruments.ui.list.SimpleListActivity;
import com.reconinstruments.ui.list.SimpleListItem;
import com.reconinstruments.ui.list.StandardListItem;

import java.util.ArrayList;

public class MainActivity extends SimpleListActivity {
    // ReconOS apps and components:
    static final String ENGAGE_PHONE_CONNECTION =
            "com.reconinstruments.connectdevice.CONNECT";
    static final String NOTIFICATION_CENTER = "com.reconinstruments.messagecenter.frontend";
    static final String CAMERA_APP= "com.reconinstruments.camera";
    static final String GALLERY_APP= "com.reconinstruments.camera.gallery";
    static final String COMPASS= "com.reconinstruments.compass.CALIBRATE";
    static final String INSTALLED_APPS= "com.reconinstruments.jetappsettings.apps";
    static final String RECON_SETTINGS = "com.reconinstruments.jetappsettings.settings";
    static final String POWER_MENU = "com.reconinstruments.power.RECON_POWER_MENU";
    // The setting page for the enterprise launcher app.
    static final String SETTINGS = "com.reconinstruments.enterprise.settings";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_standard_layout);
        ArrayList<SimpleListItem> components_items = new ArrayList<SimpleListItem>();

        components_items.add(new ListItem("Connect Smart Phone", new Intent(ENGAGE_PHONE_CONNECTION)));
        components_items.add(new ListItem("Open Camere", new Intent(CAMERA_APP)));
        components_items.add(new ListItem("Recon Settings", new Intent(RECON_SETTINGS)));
        setContents(components_items);
    }
}