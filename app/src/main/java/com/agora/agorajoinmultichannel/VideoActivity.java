package com.agora.agorajoinmultichannel;

import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcChannelEventHandler;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcChannel;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.models.ChannelMediaOptions;

import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

public class VideoActivity extends AppCompatActivity {

    private RtcEngine mRtcEngine;
    private RtcChannel rtcChannel;
    private String appID;
    private String channelName;

    private IRtcEngineEventHandler mRtcEventHandler = new IRtcEngineEventHandler() {

        @Override
        public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setupRemoteVideo(uid);
                }
            });
        }

        @Override
        public void onUserOffline(int uid, int reason) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onRemoteUserLeft();
                }
            });
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        Intent intent = getIntent();
        appID = intent.getStringExtra(MainActivity.idMessage);
        channelName = intent.getStringExtra(MainActivity.channelMessage);

        initAgoraEngineAndJoinChannel();
        initAgoraRtcChannelEngineAndJoinChannel();
    }

    private void initAgoraRtcChannelEngineAndJoinChannel(){
        rtcChannel = mRtcEngine.createRtcChannel("demoChannel2");

        rtcChannel.setRtcChannelEventHandler(new IRtcChannelEventHandler(){
            @Override
            // Listen for the onJoinChannelSuccess callback.
            // This callback occurs when the local user successfully joins the channel.
            public void onJoinChannelSuccess(RtcChannel rtcChannel, int uid, int elapsed) {
                super.onJoinChannelSuccess(rtcChannel, uid, elapsed);
                Log.i("TAG", String.format("onJoinChannelSuccess channel %s uid %d", "demoChannel2", uid));

            }
            @Override
            // Listen for the onUserJoinedcallback.
            // This callback occurs when a remote host joins a channel.
            public void onUserJoined(RtcChannel rtcChannel,final int uid, int elapsed) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRemoteVideo2(uid);
                    }
                });
                super.onUserJoined(rtcChannel, uid, elapsed);
//                Log.i(TAG, "onUserJoined->" + uid);
            }


        });

        ChannelMediaOptions option = new ChannelMediaOptions();
        option.autoSubscribeVideo = true;
        option.autoSubscribeAudio = true;

        rtcChannel.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        rtcChannel.joinChannel(null, " ", 0, option);
        rtcChannel.publish();
    }

    private void initAgoraEngineAndJoinChannel() {
        initializeAgoraEngine();
        setupVideoProfile();
        setupLocalVideo();
        joinChannel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        leaveChannel();
        RtcEngine.destroy();
        rtcChannel.unpublish();
        rtcChannel = null;
        mRtcEngine = null;
    }


    // Tutorial Step 8
    public void onSwitchCameraClicked(View view) {
        mRtcEngine.switchCamera();
    }

    // Tutorial Step 6
    public void onEndCallClicked(View view) {
        finish();
    }

    public void onLocalAudioMuteClicked(View view){mRtcEngine.muteLocalAudioStream(true);}

    public void onLocalVideoMuteClicked(View view){mRtcEngine.muteLocalVideoStream(true);}

    // Tutorial Step 1
    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), "22824201a5dc45dbab44a08328774be3", mRtcEventHandler);
        } catch (Exception e) {
            throw new RuntimeException("NEED TO check rtc sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }

    // Tutorial Step 2
    private void setupVideoProfile() {
        mRtcEngine.enableVideo();

//      mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P, false); // Earlier than 2.3.0
        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(VideoEncoderConfiguration.VD_640x360, VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
    }

    // Tutorial Step 3
    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0));
    }

    // Tutorial Step 4
    private void joinChannel() {
        mRtcEngine.enableAudio();
        mRtcEngine.setDefaultAudioRoutetoSpeakerphone(true);
        mRtcEngine.joinChannel(null, "demoChannel1", "Extra Optional Data", 0); // if you do not specify the uid, we will generate the uid for you
    }

    // Tutorial Step 5
    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container2);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));

        surfaceView.setTag(uid); // for mark purpose
    }

    // Tutorial Step 5
    private void setupRemoteVideo2(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container3);

        if (container.getChildCount() >= 1) {
            return;
        }


        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);

        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, "demoChannel2",uid));

        surfaceView.setTag(uid); // for mark purpose
    }

    // Tutorial Step 6
    private void leaveChannel() {
        mRtcEngine.leaveChannel();
    }

    // Tutorial Step 7
    private void onRemoteUserLeft() {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container2);
        container.removeAllViews();

        // View tipMsg = findViewById(R.id.quick_tips_when_use_agora_sdk); // optional UI
        // tipMsg.setVisibility(View.VISIBLE);
    }

}
