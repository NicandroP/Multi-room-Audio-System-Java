package com.example.wifisignal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static com.example.wifisignal.MainActivity.out;
import static com.example.wifisignal.MainActivity.room;

public class PlayerActivity extends AppCompatActivity {

    TextView txtRoom;
    Button btnPlay;
    Button btnPause;
    TextView txtSname;
    String info="action";

    ArrayList<String> songToPlay=new ArrayList<>();
    String songName;

    //In this activity we can play or pause the song that we started in the previous activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        txtRoom=findViewById(R.id.textViewRoom);
        btnPlay=findViewById(R.id.playButton);
        btnPause=findViewById(R.id.pauseButton);
        txtSname=findViewById(R.id.txtSn);


        Intent i=getIntent();
        songName=i.getStringExtra("songname");
        txtSname.setText(songName);

        SetRoom t=new SetRoom();
        t.start();


    }
    class SetRoom extends Thread {
        @Override
        public void run() {
            while(true){
                try {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (room != "") {
                                txtRoom.setText(room);
                                txtRoom.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                    Thread.sleep(5000);
                } catch (Exception e) {

                    e.printStackTrace();
                }
            }
        }
    }


    public void pause(View view) {
        String action="pause";
        songToPlay.clear();
        songToPlay.add(info);
        songToPlay.add(songName);
        songToPlay.add(action);
        actionMusic actionMusic=new actionMusic();
        actionMusic.execute();
        btnPause.setVisibility(View.INVISIBLE);
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
        btnPause.setVisibility(View.VISIBLE);
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