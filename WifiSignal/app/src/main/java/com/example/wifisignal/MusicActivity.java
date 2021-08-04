package com.example.wifisignal;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.wifisignal.MainActivity.arrayMusic;
import static com.example.wifisignal.MainActivity.out;
import static com.example.wifisignal.MainActivity.s;

public class MusicActivity extends AppCompatActivity {

    ListView listView;
    private ArrayAdapter adapter;
    private ArrayList<String> stringMusic=new ArrayList<>();
    ArrayList<String> songToPlay=new ArrayList<>();





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        listView=findViewById(R.id.listViewSong);


        for(Object path : arrayMusic){
            String stringSong=path.toString().replace("C:/xampp/htdocs/music/","").replace(".wav","");
            stringMusic.add(stringSong);

        }

        SongListAdapter songListAdapter=new SongListAdapter(this,R.layout.list_item,stringMusic);
        listView.setAdapter(songListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String songName= (String) listView.getItemAtPosition(position);
                String action="p";
                songToPlay.add(songName);
                songToPlay.add(action);
                startMusic startMusic=new startMusic();
                startMusic.execute();


                startActivity(new Intent(getApplicationContext(), PlayerActivity.class).putExtra("songname", songName));

            }
        });

    }

    class SongListAdapter extends ArrayAdapter<String>{

        private Context mContext;
        int mResource;

        public SongListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
            super(context, resource, objects);
            mContext=context;
            mResource=resource;
        }

        public View getView(int i, View contextView,ViewGroup parent){
            String sName=stringMusic.get(i);
            LayoutInflater inflater=LayoutInflater.from(mContext);
            contextView=inflater.inflate(mResource,parent,false);
            TextView textView=(TextView) contextView.findViewById(R.id.txtSong);
            textView.setText(sName);

            return contextView;
        }
    }
    class startMusic extends AsyncTask<Void,Void,Void> {

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