package com.itech.reconapp;

import android.app.Activity;
import android.content.ContentValues;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class VideoRecorder {

    private static final String TAG = "VideoRecorder";

    public static int MAX_DURATION = 15;
    public static CamcorderProfile camProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);

    ContentValues videoValues;
    String tmpVideoFile;
    long recordingStartTime;

    Activity activity;
    Camera camera;
    MediaRecorder mediaRecorder;

    public VideoRecorder(Activity activity, Camera camera) {
        this.activity = activity;
        this.camera = camera;
        videoValues = StorageUtils.getVideoData(camProfile,recordingStartTime);
        tmpVideoFile = StorageUtils.getVideoPath(videoValues)+".tmp";
    }

    public void startRecording() {
        recordingStartTime = System.currentTimeMillis();
        mediaRecorder.start();
    }

    public void stopRecording() {
        boolean saveVideo = false;
        try {
            mediaRecorder.stop();
            saveVideo = true;
        } catch(RuntimeException e) {
            boolean deleted = new File(tmpVideoFile).delete();
            Log.e(TAG,"Error stopping media recorder "+(deleted?"deleted tmp fail":"failed to delete tmp file"),e);
        } finally {
            /* release olacak */
        }
        if(saveVideo){
            /* kayit yapacagiz */
        }
    }


}
