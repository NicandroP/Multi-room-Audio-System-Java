package com.example.wifisignal;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private Button button;
    private ListView listView;
    private RealTime t;
    private WifiManager wifiManager;
    private List<ScanResult> results;
    private ArrayAdapter adapter;
    private ArrayList<String> arrayList=new ArrayList<>();
    public static Socket s;
    private static String ip="192.168.1.63";
    private static int port=3344;
    public static DataInputStream in;
    public static DataOutputStream out;
    public StringBuilder sb=new StringBuilder();
    private static boolean running;
    private ArrayList<Object> list=new ArrayList<>();
    public static ArrayList<Object> arrayMusic=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.getWifiBtn);
        listView=(ListView)findViewById(R.id.listView);
        wifiManager=(WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayList);
        statusCheck();
        
        Connect conn=new Connect();
        conn.execute();
        Receive r=new Receive();
        r.execute();

    }

    public void getWifiInformation(View view) {
        running=true;
        t=new RealTime();
        t.start();

    }

    public void stopWifi(View view) {
        running=false;
        Disconnect disc=new Disconnect();
        disc.execute();

        Log.d("mytag","thread stopped!");
    }


    public void music(View view) {
        startActivity(new Intent(MainActivity.this,MusicActivity.class));

    }

    class RealTime extends Thread {
        @Override
        public void run() {

            while(running){
                try{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            arrayList.clear();
                            wifiManager.startScan();
                            results=wifiManager.getScanResults();
                            listView.setAdapter(adapter);
                            sb.delete(0,sb.length());
                            for(ScanResult result: results){
                                arrayList.add(result.SSID+" :"+result.level);
                                sb.append(result.SSID).append(":").append(result.level).append(",");
                            }
                            Log.d("mytag",arrayList.toString());
                            Send snd=new Send();
                            snd.execute();
                        }
                    });
                    Thread.sleep(3000);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }


        }
    }

    class Disconnect extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                out.writeUTF("exit");
                out.flush();

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    class Connect extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            try{
                s=new Socket(ip,port);
                in=new DataInputStream(s.getInputStream());
                out=new DataOutputStream(s.getOutputStream());
                Log.d("mytag","Connection succesfull");
            }catch(Exception e){
                e.printStackTrace();
            }


            return null;
        }
    }
    class Receive extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                try {
                    ObjectInputStream objectInput = new ObjectInputStream(s.getInputStream());
                    try {
                        Object object = objectInput.readObject();
                        list = (ArrayList<Object>) object;
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
                Log.d("mytag", String.valueOf(arrayMusic));
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }
    class Send extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {

            try{
                out.writeUTF(String.valueOf(arrayList));

            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }
    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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