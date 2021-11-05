package server;


import java.awt.EventQueue;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
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
import javax.swing.JFrame;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JTextField;
import javax.swing.SwingConstants;


import java.awt.Font;
import java.awt.Toolkit;
import java.awt.Color;
import java.awt.Cursor;

import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JProgressBar;

public class Server {
	static ServerSocket ss;
	static Socket s;
	static DataInputStream in;
	static DataOutputStream out;
	static int port=3345;
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
	static int room=0;
	static AudioInputStream audioStream;
	static Clip clip;
	static double durationInSecondsDecimal;
	static int durationInSeconds,minutes, seconds;
	static JTextField textField;
	static JLabel songLabel;
	private JLabel titleLabel;
	private JPictureBox frameIcon;
	static JLabel songDuration;
	static JLabel songTiming;
	static JProgressBar progressBar;
	
	
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
		arrayMusic.clear();
		
		for(int i=0;i<list.length;i++) {
			arrayMusic.add(list[i]);
		}
		
		
		System.out.println("Starting the server");
		ss=new ServerSocket(port);
		System.out.println("Server active on port: "+port);
		s=ss.accept();
		System.out.println("Connection succesfull");
		songLabel.setText("<html>Connection succesfull,<br/> &nbsp;ready to play music!</html>");
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
		frame.getContentPane().setBackground(new Color(64, 64, 64));
		frame.getContentPane().setForeground(Color.BLACK);
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\nican\\GitCAProject\\Multiroom\\ClientAndroid\\app\\src\\main\\res\\drawable\\music.png"));
		frame.setBounds(650, 300, 582, 361);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setUndecorated(true);
		frame.setShape(new RoundRectangle2D.Double(0, 0, frame.getWidth(), frame.getHeight(), 30, 30));
		
		FrameDragListener frameDragListener = new FrameDragListener(frame);
        frame.addMouseListener(frameDragListener);
        frame.addMouseMotionListener(frameDragListener);
		
		songLabel= new JLabel();
		songLabel.setForeground(Color.WHITE);
		songLabel.setFont(new Font("Tahoma", Font.PLAIN, 41));
		songLabel.setHorizontalAlignment(SwingConstants.CENTER);
		songLabel.setBounds(98, 133, 403, 96);
		songLabel.setHorizontalAlignment(SwingConstants.CENTER);
		songLabel.setText("Waiting for client..");
		
		frame.getContentPane().add(songLabel);
		
		frameIcon=new JPictureBox();
		frameIcon.setLocation(10, 6);
		ImageIcon imgIcon=new ImageIcon("C:\\Users\\nican\\GitCAProject\\Multiroom\\ClientAndroid\\app\\src\\main\\res\\drawable\\music.png");
		frameIcon.setIcon(imgIcon);
		frameIcon.setSize(50,50);
		frame.getContentPane().add(frameIcon);
		
		JLabel lblX=new JLabel("X");
		lblX.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
				System.out.println("System closed.");
				System.exit(0);
			}
		});
		lblX.setHorizontalAlignment(SwingConstants.CENTER);
		lblX.setForeground(Color.RED);
		lblX.setFont(new Font("Tahoma",Font.PLAIN,28));
		lblX.setBounds(532,6,40,45);
		lblX.setCursor(new Cursor(Cursor.HAND_CURSOR));
		frame.getContentPane().add(lblX);
		
		
		JLabel lblMinimize=new JLabel("—");
		lblMinimize.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				frame.setState(JFrame.ICONIFIED);
			}
		});
		lblMinimize.setHorizontalAlignment(SwingConstants.CENTER);
		lblMinimize.setForeground(Color.RED);
		lblMinimize.setFont(new Font("Tahoma",Font.PLAIN,28));
		lblMinimize.setBounds(488,6,40,45);
		lblMinimize.setCursor(new Cursor(Cursor.HAND_CURSOR));
		frame.getContentPane().add(lblMinimize);
		
		
		
		
		titleLabel = new JLabel("Multi-room Server");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 24));
		titleLabel.setBounds(147, 11, 275, 45);
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		frame.getContentPane().add(titleLabel);
		
		/*progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setBackground(Color.WHITE);
		progressBar.setBounds(147, 247, 310, 14);
		frame.getContentPane().add(progressBar);
		
		songDuration = new JLabel("");
		songDuration.setFont(new Font("Tahoma", Font.PLAIN, 16));
		songDuration.setForeground(Color.WHITE);
		songDuration.setBounds(467, 247, 46, 14);
		frame.getContentPane().add(songDuration);
		
		songTiming = new JLabel("");
		songTiming.setFont(new Font("Tahoma", Font.PLAIN, 16));
		songTiming.setForeground(Color.WHITE);
		songTiming.setBounds(105, 247, 46, 14);
		frame.getContentPane().add(songTiming);*/
		
		
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
								System.out.println("Playing: "+songName);
								songLabel.setText(songName);
								playSong(song);
								
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
					int[] inputArray=new int[] {-100,-100,-100,-100,-100,-100,-100,-100,-100};//array da dare al ML
					String[] string= msgin.toString().split(",");
					int n= string.length;
					int i=0;
					
					while(i<n) {
						
						
						String[] string2= string[i].split(":");
						level= string2[1];
						ssid= string2[0];
						
						if(i==0) {//tolto la parentesi dal primo ssid
							StringBuilder sb2 = new StringBuilder(ssid);
							sb2.setCharAt(0,' ');
							ssid=sb2.toString();
						}
						if(i+1==n) {//tolgo la parentesi dall'ultimo level
							StringBuilder sb3 = new StringBuilder(level);
							sb3.deleteCharAt(sb3.length()-1);
							level=sb3.toString();

						}
						switch(ssid) {
						case " FASTWEB-fd6deP ":
							inputArray[0]=Integer.parseInt(level);
							break;
						case " WOW FI - FASTWEB ":
							inputArray[1]=Integer.parseInt(level);
							break;
						case " D-Link-EAAFB1 ":
							inputArray[2]=Integer.parseInt(level);
							break;
						case " INCENTIVE ":
							inputArray[3]=Integer.parseInt(level);
							break;

						case " Vodafone-WiFi ":
							inputArray[4]=Integer.parseInt(level);
							break;
						case " TIM_MF920V_97FE ":
							inputArray[5]=Integer.parseInt(level);
							break;
						case " FASTWEB-H6D6XP ":
							inputArray[6]=Integer.parseInt(level);
							break;
						case " Grillandia ":
							inputArray[7]=Integer.parseInt(level);
							break;
						case " ginger ":
							inputArray[8]=Integer.parseInt(level);
							break;
						
							
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
					
					
					
					
					for(int j=0;j<inputArray.length;j++) {
				    	if(j==inputArray.length-1) {
				    		System.out.println(inputArray[j]);
				    	}else {
				    		System.out.print(inputArray[j]+", ");
				    	}
				    	
				    }
					
					
					//provare ad inserire qui
					String[] strArray = new String[inputArray.length];
					for (int k = 0; k < inputArray.length; k++) {
			            strArray[k] = String.valueOf(inputArray[k]);
			        }
					String arg = String.join(",", strArray);
					try {
						long startTime = System.currentTimeMillis();
						ProcessBuilder builder=new ProcessBuilder("python","C:\\Users\\nican\\\\GitCAProject\\Multiroom\\ProjectServer\\training.py", arg);
						Process process=builder.start();
						
						BufferedReader reader= new BufferedReader(new InputStreamReader(process.getInputStream()));

						BufferedReader readers= new BufferedReader(new InputStreamReader(process.getErrorStream()));
						
						long endTime = System.currentTimeMillis();
						long seconds = (endTime - startTime);
						//System.out.println("Programma eseguito in " + seconds + " millisecondi");
						
						String lines=null;
						while((lines=reader.readLine())!=null) {
							System.out.println(lines);
							
							out.writeUTF(lines);
							
							
							switch(lines) {
							case "[1]":
								if(room!=1) {
									try {
										Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\nican\\GitCAProject\\Multiroom\\win10-bluetooth-headphones-master\\disconnectSrs.vbs");
										//Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\nican\\GitCAProject\\Multiroom\\win10-bluetooth-headphones-master\\disconnectFree.vbs");
										System.out.println("Playing from pc audio speakers");
										room=1;
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								
								break;
							
							case "[2]":
								if(room!=2) {
									try {
										Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\nican\\GitCAProject\\Multiroom\\win10-bluetooth-headphones-master\\connectForceSrs.vbs");
										System.out.println("Playing from SRS audio speakers");
										room=2;
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								
								break;
							case "[3]":
								if(room!=3) {
									try {
										Runtime.getRuntime().exec("cmd /c start \"\" C:\\Users\\nican\\GitCAProject\\Multiroom\\win10-bluetooth-headphones-master\\connectForceFree.vbs");
										System.out.println("Playing from FreeBuds audio speakers");
										room=3;
									} catch (IOException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								
								break;
							}
						}
						
						while((lines=readers.readLine())!=null) {
							System.out.println("Error lines: "+lines);
						}
						
					}catch(Exception e) {
						e.printStackTrace();
					}
					
					
				    
				    try (PrintWriter writer = new PrintWriter(new File("wifiScan.csv"))) {      //COMMENTATO PERCHÈ NON CI SERVE PIÙ
				    	
				    	writer.append(res);
					}catch (FileNotFoundException e) {
					      System.out.println(e.getMessage());
					}
				}
				
				
			}
			try {
				msgin=in.readUTF();
			}catch(IOException e) {
				System.out.println("\nClosing the connection");
				s.close();
				if(started==true) {
					clip.stop();
				}
				msgin="";
				System.out.println("System closed.\n");
				songLabel.setText("Waiting for client...");
				startServer();
				
				
			}
		    
		    			
		}
		System.out.println("\nClosing the connection");
		if(started==true) {
			clip.stop();
		}
		msgin="";
		s.close();//qui non si dovrebbe chiudere la connessione
		songLabel.setText("Waiting for client...");
		startServer();
			
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
		System.out.println("Duration: "+minutes+":"+seconds);
		//songDuration.setText(minutes+":"+seconds);
		//songTiming.setText("0:00");
		//progressBar.setVisible(true);
		clip.open(audioStream);
		clip.start();
		
		
		
		
	}
	
}


	

	
	
