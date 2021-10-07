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

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;


public class Server {
	static ServerSocket ss;
	static Socket s;
	static DataInputStream in;
	static DataOutputStream out;
	static int port=3344;
	private JFrame frame;
	static StringBuilder sb = new StringBuilder();
	static String level;
	static String ssid;
	static String res="";
	static String path="C:\\xampp\\htdocs\\music";
	static File f=new File(path);
	static File[] list=f.listFiles();
	static File song;
	static ArrayList<File> arrayMusic =new ArrayList<File>();
	static String msgin="";
	static AudioInputStream audioStream;
	static Clip clip;
	static double durationInSecondsDecimal;
	static int durationInSeconds,minutes, seconds;
	static JTextField textField;
	private JButton srsBtn;
	private JButton freeBtn;
	private JButton pcBtn;
	
	
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server window = new Server();
					window.frame.setVisible(true);
					
					
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
		frame.setBounds(650, 300, 582, 451);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setFont(new Font("Tahoma", Font.PLAIN, 41));
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setBounds(10, 11, 546, 134);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JButton stopBtn = new JButton("STOP");
		stopBtn.setBounds(251, 364, 89, 37);
		frame.getContentPane().add(stopBtn);
		
		srsBtn = new JButton("SRS");
		srsBtn.setBounds(133, 156, 148, 55);
		frame.getContentPane().add(srsBtn);
		
		srsBtn.addActionListener(e -> {
			try {
				Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\nican\\GitCAProject\\Multiroom\\win10-bluetooth-headphones-master\\connectForceSrs.vbs");
				System.out.println("Playing from SRS audio speakers");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		freeBtn = new JButton("FREE");
		freeBtn.setBounds(312, 156, 148, 55);
		frame.getContentPane().add(freeBtn);
		
		
		
		freeBtn.addActionListener(e -> {
			try {
				Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\nican\\GitCAProject\\Multiroom\\win10-bluetooth-headphones-master\\connectForceFree.vbs");
				System.out.println("Playing from FreeBuds audio speakers");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		pcBtn = new JButton("PC");
		pcBtn.setBounds(222, 237, 148, 55);
		frame.getContentPane().add(pcBtn);
		
		pcBtn.addActionListener(e -> {
			try {
				Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\nican\\GitCAProject\\Multiroom\\win10-bluetooth-headphones-master\\disconnectSrs.vbs");
				Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\nican\\GitCAProject\\Multiroom\\win10-bluetooth-headphones-master\\disconnectFree.vbs");
				System.out.println("Playing from pc audio speakers");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		
		
		stopBtn.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) {
				  System.out.println("System closed.");
				  System.exit(0);
			  } 
		} );
		
	}
	
	
	private static void receive() throws IOException {
		Boolean started=false;
		long clipTime=0;
		String songName="";
		int count=0;
		boolean paused=false;
		
		while(!msgin.toString().toLowerCase().equals("exit")) {
			
			if(msgin!="") {
				String info=msgin.toString().split(",")[0].replace("[","").replace(" ","");
				
				if(info.equals("action")) {
					String temp=msgin.toString().split(",")[1].replace("[","").substring(1);
					if(!songName.equals(temp)) {
						started=false;
						paused=false;
					}
					songName=msgin.toString().split(",")[1].replace("[","").substring(1);
					String songAction=msgin.toString().split(",")[2].replace(" ","").replace("]","");
				
					switch(songAction) {
					case "play" :
						if(started==false) {
							
							if(count>0) {
								clip.stop();
								clipTime=0;
							}
							count++;
							song=new File(path+"/"+songName+".wav");
							try {
								playSong(song);
								System.out.println("Playing: "+songName);
								textField.setText(songName);
								started=true;
							} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
								e.printStackTrace();
							}
						}else if(started==true && paused==true) {
							paused=false;
							clip.setMicrosecondPosition(clipTime);
							clip.start();
							System.out.println("Song resumed");
						}
						
						break;
					case "pause":
						paused=true;
						clipTime= clip.getMicrosecondPosition();
						clip.stop();
						System.out.println("Song paused");
							
					}
				}else {
					String[] string= msgin.toString().split(",");
					int n= string.length;
					int i=0;
					
					while(i<n) {					
						String[] string2= string[i].split(":");
						level= string2[1];
						ssid= string2[0];
						
						
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
				
			
				
			}
			
		    msgin=in.readUTF();
		    			
		}
		System.out.println("\nClosing the connection");
		s.close();//qui non si dovrebbe chiudere la connessione
			
	}

	private static void playSong(File f) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
		audioStream=AudioSystem.getAudioInputStream(f);
		clip=AudioSystem.getClip();
		AudioFormat format = audioStream.getFormat();
		long frames = audioStream.getFrameLength();
		durationInSecondsDecimal = (frames + 0.0) / format.getFrameRate();
		durationInSeconds=(int) durationInSecondsDecimal;
		minutes=durationInSeconds/60;
		seconds=durationInSeconds%60;
		//System.out.println("Duration: "+minutes+":"+seconds);
		clip.open(audioStream);
		clip.start();
	}
}