package server;

import java.awt.Button;
import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Server {
	
	static TextArea textArea;
	static ServerSocket ss;
	static Socket s;
	static DataInputStream in;
	static DataOutputStream out;
	static int port=3342;
	private JFrame frame;
	static StringBuilder sb = new StringBuilder();
	static String level;
	static String ssid;
	static String res="";
	static String path="C:\\xampp\\htdocs\\music";
	static File f=new File(path);
	static File[] list=f.listFiles();
	static ArrayList<File> arrayMusic =new ArrayList<File>();
	static String msgin="";
	
	
	public static void main(String[] args) throws IOException {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server window = new Server();
					window.frame.setVisible(false);
					/*
					AudioInputStream audioStream=AudioSystem.getAudioInputStream(files.get(1));
					Clip clip=AudioSystem.getClip();
					clip.open(audioStream);
					clip.start();
					*/
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		startServer();
	}
	
	private static void startServer() throws IOException {
		
		for(int i=0;i<list.length;i++) {
			arrayMusic.add(list[i]);
		}
		
		System.out.println("Starting the server");
		ss=new ServerSocket(port);
		System.out.println("Server active on port: "+port);
		s=ss.accept();
		System.out.println("Connection succesfull");
		ss.close();
		in=new DataInputStream(s.getInputStream());
		out=new DataOutputStream(s.getOutputStream());
		ObjectOutputStream objectOutput=new ObjectOutputStream(s.getOutputStream());
		objectOutput.writeObject(arrayMusic);
		System.out.println("Music sent!");
		System.out.println("Waiting for a message from the client...");
		
		receive();
		
	}
	
	public Server() {
		initialize();
	}
	
	private void initialize() {
		frame = new JFrame("SERVER");
		frame.setBounds(100, 100, 582, 451);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		textArea = new TextArea();
		textArea.setBounds(23, 10, 510, 239);
		frame.getContentPane().add(textArea);
	}
	
	
	private static void receive() throws IOException {
		
		while(!msgin.toString().toLowerCase().equals("exit")) {
			
			if(msgin!="") {
				String[] string= msgin.toString().split(",");
				int n= string.length;
				int i=0;
				
				while(i<n) {					
					String[] string2= string[i].split(":");
					level= string2[1];
					ssid= string2[0];
					
					if(i!=0){
						sb.append('\n');
					}
					
					if(i!=(n-1)) {
						sb.append(ssid);
					    sb.append(',');
					    sb.append(level);

					}else{
						sb.append(ssid);
					    sb.append(',');
					    sb.append(level);
				    	sb.append('\n');
				     }
				      
					 if(i==(n-1)) {
						 sb.append('\n');
					 }
				 
				      res=sb.toString();
				      i++;
				      
				}
				
			    System.out.println(msgin.toString());
			    
			    try (PrintWriter writer = new PrintWriter(new File("wifiScan.csv"))) {
			    	
			    	writer.append(res);
				}catch (FileNotFoundException e) {
				      System.out.println(e.getMessage());
				}
			}
			
		    msgin=in.readUTF();
		    			
		}
		System.out.println("\nClosing the connection");
		s.close();
			
	}
	
}