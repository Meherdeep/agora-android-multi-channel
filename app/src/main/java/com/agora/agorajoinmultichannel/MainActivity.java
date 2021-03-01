package com.agora.agorajoinmultichannel;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import io.agora.rtc.Constants;

public class MainActivity extends AppCompatActivity {

    public static final String channelMessage = "com.agora.agorajoinmultichannel.CHANNEL";
    public static final String idMessage = "com.agora.agorajoinmultichannel.APPID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int MY_PERMISSIONS_REQUEST_CAMERA=0;
        // Here, this is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, MY_PERMISSIONS_REQUEST_CAMERA );
        }
    }


    public void onSubmit(View view) {

        EditText appIDField = (EditText) findViewById(R.id.appid);
        String appID = appIDField.getText().toString();
        EditText channel = (EditText) findViewById(R.id.channelname);
        String channelName = channel.getText().toString();
        Intent intent = new Intent(this, VideoActivity.class);
        intent.putExtra(idMessage, appID);
        intent.putExtra(channelMessage, channelName);
        startActivity(intent);
    }
}