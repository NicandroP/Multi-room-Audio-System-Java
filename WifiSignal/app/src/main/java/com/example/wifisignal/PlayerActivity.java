package com.example.wifisignal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlayerActivity extends AppCompatActivity {

    Button btnPlay;
    TextView txtSname;

    String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay=findViewById(R.id.playButton);
        txtSname=findViewById(R.id.txtSn);


        Intent i=getIntent();
        songName=i.getStringExtra("songname");
        txtSname.setText(songName);


    }
}