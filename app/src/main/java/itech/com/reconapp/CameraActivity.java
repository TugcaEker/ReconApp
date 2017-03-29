package itech.com.reconapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.reconinstruments.os.HUDOS;
import com.reconinstruments.os.connectivity.HUDConnectivityManager;
import com.reconinstruments.os.connectivity.http.HUDHttpRequest;
import com.reconinstruments.ui.carousel.CarouselActivity;
import com.reconinstruments.ui.carousel.CarouselItem;
import com.reconinstruments.ui.carousel.StandardCarouselItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import itech.com.reconapp.misc.HttpFileUpload;
import itech.com.reconapp.misc.RESTClient;

public class CameraActivity extends CarouselActivity {
    public static final String PREFS_NAME = "iTechPref";
    private static final String TAG = "CameraActivity";

    Camera camera;

    CameraPreview preview;
    VideoRecorder activeVideo;
    CarouselItem photoItem;
    private HUDConnectivityManager mHUDConnectivityManager = null;
    SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        System.load("/system/lib/libreconinstruments_jni.so");
        settings = getSharedPreferences(PREFS_NAME, 0);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_camera);

        photoItem = new StandardCarouselItem(R.drawable.photo_icon) {
            @Override
            public void onClick(Context context) {
                camera.takePicture(null, null, jpegSavedCallback);
            }
        };
        getCarousel().setContents(photoItem);
        preview = (CameraPreview) findViewById(R.id.preview);
        mHUDConnectivityManager = (HUDConnectivityManager) HUDOS.getHUDService(HUDOS.HUD_CONNECTIVITY_SERVICE);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        closeCamera();
    }

    PictureCallback jpegSavedCallback = new PictureCallback() {
        public void onPictureTaken(final byte[] data, Camera camera) {
            final RESTClient http = new RESTClient(mHUDConnectivityManager);
            final String url = settings.getString("api_path", "") + "media/uploadbyte";
            final JSONObject obj = new JSONObject();

                new Thread(new Runnable(){
                    public void run()
                    {
                        try {
                            obj.put("file", Base64.encodeToString(data,Base64.NO_WRAP));
                            JSONObject areas = http.execute(url, HUDHttpRequest.RequestMethod.POST, obj);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            preview.setCamera(camera);
        }
    };


    public boolean isRecording() {
        return activeVideo != null;
    }

    public void openCamera() {
        try {
            camera = Camera.open();
        } catch (RuntimeException ex) {
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show();
        }
        if (camera != null)
            preview.setCamera(camera);
    }

    public void closeCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}