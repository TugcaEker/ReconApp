package itech.com.reconapp;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.reconinstruments.ui.carousel.CarouselActivity;
import com.reconinstruments.ui.carousel.CarouselItem;
import com.reconinstruments.ui.carousel.StandardCarouselItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CameraActivity extends CarouselActivity {

    private static final String TAG = "CameraActivity";
    Camera camera;
    CameraPreview preview;
    CarouselItem photoItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        photoItem = new StandardCarouselItem(R.drawable.photo_icon) {
            @Override
            public void onClick(Context context) {
                camera.takePicture(null, null, onPictureReceived);
            }
        };
        preview = (CameraPreview) findViewById(R.id.preview);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        /* kontrol edelim*/
        return true;
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

    PictureCallback onPictureReceived = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            /* Kaydedip gonderelim*/
        }
    };


    public void openCamera() {
        try {
            camera = Camera.open();
        } catch(RuntimeException ex) {
            /* Kontrol edelim */
        }
        if(camera!=null)
            preview.setCamera(camera);
    }

    public void closeCamera() {
        if(camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
}