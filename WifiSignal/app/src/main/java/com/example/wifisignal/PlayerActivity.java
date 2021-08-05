package com.example.wifisignal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.wifisignal.MainActivity.out;

public class PlayerActivity extends AppCompatActivity {

    Button btnPlay;
    Button btnStop;
    TextView txtSname;
    String info="action";

    ArrayList<String> songToPlay=new ArrayList<>();
    String songName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        btnPlay=findViewById(R.id.playButton);
        btnStop=findViewById(R.id.stopButton);
        txtSname=findViewById(R.id.txtSn);


        Intent i=getIntent();
        songName=i.getStringExtra("songname");
        txtSname.setText(songName);


    }

    public void stop(View view) {
        String action="pause";
        songToPlay.clear();
        songToPlay.add(info);
        songToPlay.add(songName);
        songToPlay.add(action);
        actionMusic actionMusic=new actionMusic();
        actionMusic.execute();
        btnStop.setVisibility(View.INVISIBLE);
        btnPlay.setVisibility(View.VISIBLE);
    }

    public void play(View view) {
        String action="play";
        songToPlay.clear();
        songToPlay.add(info);
        songToPlay.add(songName);
        songToPlay.add(action);
        actionMusic actionMusic=new actionMusic();
        actionMusic.execute();
        btnStop.setVisibility(View.VISIBLE);
        btnPlay.setVisibility(View.INVISIBLE);
    }
    class actionMusic extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try {
                out.writeUTF(String.valueOf(songToPlay));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}