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
        prepareMediaRecorder(tmpVideoFile);
    }

    public void startRecording() {
        recordingStartTime = System.currentTimeMillis();
        mediaRecorder.start();
    }

    public void stopRecording() {
        boolean saveVideo = false;
        try {
            mediaRecorder.stop(); // stop the recording
            saveVideo = true;
        } catch(RuntimeException e) {
            boolean deleted = new File(tmpVideoFile).delete();
            Log.e(TAG,"Error stopping media recorder "+(deleted?"deleted tmp fail":"failed to delete tmp file"),e);
        } finally {
            releaseMediaRecorder();
        }
        if(saveVideo)
            storeVideo();
    }

    private void storeVideo() {
        long duration = System.currentTimeMillis() - recordingStartTime;
        if (duration <= 0) {
            Log.w(TAG, "Video duration <= 0 : " + duration);
        }
        StorageUtils.insertVideo(activity, tmpVideoFile, videoValues, duration);
    }

    private boolean prepareMediaRecorder(String fileName) {
        mediaRecorder = new MediaRecorder();

        camera.unlock();
        mediaRecorder.setCamera(camera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        mediaRecorder.setProfile(camProfile);

        mediaRecorder.setOutputFile(fileName);
        mediaRecorder.setMaxDuration(MAX_DURATION * 1000);
        mediaRecorder.setMaxFileSize(50000000);

        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.lock();
        }
    }
}
