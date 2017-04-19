package com.itech.reconapp;

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

import com.itech.reconapp.misc.HttpFileUpload;
import com.itech.reconapp.misc.RESTClient;

public class CameraActivity extends CarouselActivity {
    public static final String PREFS_NAME = "iTechPref";
    private static final String TAG = "CameraActivity";

    Camera camera;


    CameraPreview preview;
    TextView recordingTimeView;
    FrameLayout modeSwitchView;

    VideoRecorder activeVideo;
    CarouselItem photoItem;
    private HUDConnectivityManager mHUDConnectivityManager = null;
    SharedPreferenecs settings;

    SimpleDateFormat recordTimeFormatter = new SimpleDateFormat("mm:ss", Locale.getDefault());
    CarouselItem videoItem;


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
        recordingTimeView = (TextView) findViewById(R.id.recording_time);
        modeSwitchView = (FrameLayout) findViewById(R.id.mode_switcher);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_DPAD_CENTER&&getCarousel().getCurrentCarouselItem() == videoItem) {
            Log.d("TAG", "recording: " + isRecording());
            if (!isRecording()) {
                startRecording();
            } else {
                stopRecording();
            }
            return true;
        }

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

    private void startRecording() {
        activeVideo = new VideoRecorder(CameraActivity.this, camera);
        activeVideo.startRecording();

        recordingTimeView.setVisibility(View.VISIBLE);
        modeSwitchView.setVisibility(View.GONE);

        final Handler recordHandler = new Handler();
        final Runnable recordUpdater = new Runnable() {
            int recordTime = VideoRecorder.MAX_DURATION;
            @Override
            public void run() {
                recordTime--;
                String timeLeftString = recordTimeFormatter.format(new Date(recordTime * 1000));
                recordingTimeView.setText(timeLeftString);

                if(recordTime>0&&isRecording())
                    recordHandler.postDelayed(this,1000);
                else if(isRecording())
                    stopRecording();
            }
        };
        recordHandler.postDelayed(recordUpdater, 0);
    }

    public void stopRecording() {
        activeVideo.stopRecording();
        activeVideo = null;

        recordingTimeView.setVisibility(View.GONE);
        modeSwitchView.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Video captured!", Toast.LENGTH_LONG).show();
    }
}