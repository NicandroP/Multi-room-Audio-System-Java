package com.example.wifisignal;

import androidx.appcompat.app.AppCompatActivity;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
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
    private static Socket s;
    private static String ip="192.168.0.112";
    private static int port=3342;
    static DataInputStream in;
    static DataOutputStream out;
    public StringBuilder sb=new StringBuilder();
    private static boolean running;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button=(Button)findViewById(R.id.getWifiBtn);
        listView=(ListView)findViewById(R.id.listView);
        wifiManager=(WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        adapter=new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,arrayList);

    }

    public void getWifiInformation(View view) {
        Connect conn=new Connect();
        running=true;
        conn.execute();
        t=new RealTime();
        t.start();
    }

    public void stopWifi(View view) {
        running=false;
        Disconnect disc=new Disconnect();
        disc.execute();

        Log.d("mytag","thread stopped!");
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
                out.flush();
            }catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

}