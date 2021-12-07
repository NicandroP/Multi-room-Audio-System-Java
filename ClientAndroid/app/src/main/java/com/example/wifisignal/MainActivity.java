package com.example.wifisignal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

//In order to make the system works it is necessary to: deactivate the PC firewall, installate the python libraries on the PC, authorize the localization in the Android app and activate the high accuracy gps.
public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private Button musicBtn;
    private TextView textViewWaiting;
    private TextView textViewConnected;
    private TextView textViewDisconnected;
    private TextView textViewRoom;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    private ArrayAdapter adapter;
    private ArrayList<String> arrayList=new ArrayList<>();
    public static Socket s;
    private static String ip="192.168.1.63";
    private static int port=3345;
    public static DataInputStream in;
    public static DataOutputStream out;
    public StringBuilder sb=new StringBuilder();
    private static boolean running;
    private ArrayList<Object> list=new ArrayList<>();
    public static ArrayList<Object> arrayMusic=new ArrayList<>();
    static String room;
    static Boolean serverActive=true;
    static int count=0;
    static long startTime;
    static long media=0;
    static int counter=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        musicBtn=(Button)findViewById(R.id.musicBtn);
        listView=(ListView)findViewById(R.id.listView);
        textViewWaiting=(TextView)findViewById(R.id.textViewWaiting);
        textViewConnected=(TextView)findViewById(R.id.textViewConnected);
        textViewDisconnected=(TextView)findViewById(R.id.textViewDisconnected);
        textViewRoom=(TextView)findViewById(R.id.textViewRoom);
        wifiManager=(WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayList);
        statusCheck();
        Connect conn=new Connect();
        conn.execute();
        Receive r=new Receive();
        r.execute();
        getWifiInformation();

        TextPaint paint = textViewRoom.getPaint();
        float width = paint.measureText((String) textViewRoom.getText());
        Shader textShader = new LinearGradient(0, 0, width, textViewRoom.getTextSize(),
                new int[]{
                        Color.parseColor("#FFBB86FC"),
                        Color.parseColor("#8D41EA"),
                }, null, Shader.TileMode.CLAMP);
        textViewRoom.getPaint().setShader(textShader);

    }
    @Override
    protected void onDestroy () {

        stopWifi();
        super.onDestroy();

    }

    public void getWifiInformation() {

        running=true;
        RealTime t=new RealTime();
        t.start();

    }

    public void stopWifi() {
        running=false;
        Disconnect disc=new Disconnect();
        disc.execute();
        textViewConnected.setVisibility(View.INVISIBLE);
        textViewDisconnected.setVisibility(View.VISIBLE);
        Log.d("mytag","System disconnected.");

    }

    public void music(View view) {
        startActivity(new Intent(MainActivity.this,MusicActivity.class));

    }
    //Here we scan the frequencies of the access points nearby and we send them to the server
    class RealTime extends Thread {
        @Override
        public void run() {

            while(running){
                try{

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<String> arrayCheck=new ArrayList<>();
                            arrayList.clear();
                            startTime = System.currentTimeMillis();
                            wifiManager.startScan();
                            results=wifiManager.getScanResults();
                            //listView.setAdapter(adapter);
                            sb.delete(0,sb.length());
                            for(ScanResult result: results){
                                    //If to avoid the duplicates
                                    if(!arrayCheck.contains(result.SSID)){
                                        arrayCheck.add(result.SSID);
                                        arrayList.add(result.SSID+" :"+result.level);
                                        sb.append(result.SSID).append(":").append(result.level).append(",");
                                    }

                            }
                            if(count>0){

                                Send snd=new Send();
                                snd.execute();
                            }
                            count++;

                            if(serverActive==false){
                                stopWifi();
                            }

                        }
                    });
                    Thread.sleep(5000);
                }catch(Exception e){

                    e.printStackTrace();
                }
            }

        }
    }
    //This thread inizialize the socket and requires for a connection to the server
    class Connect extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                s=new Socket(ip,port);
                in=new DataInputStream(s.getInputStream());
                out=new DataOutputStream(s.getOutputStream());
                Log.d("mytag","Connection succesfull");
                textViewWaiting.setVisibility(View.INVISIBLE);
                textViewConnected.setVisibility(View.VISIBLE);

            }catch(Exception e){

                e.printStackTrace();
            }

            return null;
        }
    }

    class Disconnect extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                out.writeUTF("exit");
                out.flush();
                s.close();

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    //This thread is used to receive the music list from the server
    class Receive extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                try {
                    ObjectInputStream objectInput = new ObjectInputStream(s.getInputStream());

                    try {

                        Object object = objectInput.readObject();
                        list = (ArrayList<Object>) object;
                        Log.d("mytag","Music received!");
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                arrayMusic.clear();
                for(int i=0; i<list.size(); i++) {
                    arrayMusic.add(list.get(i));
                }

            }catch(Exception e){

                e.printStackTrace();
            }

            return null;
        }
    }

    //The frequencies acquired before are sent to the server that return the result of the ML
    class Send extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {


            try{
                if(arrayList.size()!=0){
                    out.writeUTF(String.valueOf(arrayList));
                    room= in.readUTF();
                    long endTime = System.currentTimeMillis();
                    long milliSeconds = (endTime - startTime);
                    media+=milliSeconds;
                    counter++;
                    Log.d("mytag","Program executed in " + milliSeconds/1000.0 + " seconds");
                    Log.d("mytag","Mean: "+(media/counter)/1000.0+" seconds");

                    room= String.valueOf(room.charAt(1));
                    switch (room){
                        case "1":
                            room="Bedroom";
                            break;
                        case "2":
                            room="Kitchen";
                            break;

                    }
                    setText(textViewRoom,"Your room: "+room);
                }


            }catch(Exception e){
                serverActive=false;
                e.printStackTrace();
            }

            return null;
        }
    }

    private void setText(final TextView text,final String value){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textViewRoom.setText(value);
            }
        });
    }
    //The two function statusCheck() and buildAlertMessageNoGps() serve to warn the user that the gps must be active to use the system
    public void statusCheck() {

        LocationManager manager= (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}