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
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {
	
	static TextArea textArea;
	static TextField textField;
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

	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server window = new Server();
					window.frame.setVisible(false);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		startServer();
	
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
		textField = new TextField();
		textField.setBounds(23, 319, 372, 49);
		frame.getContentPane().add(textField);
		
		JButton button = new JButton("New button");
		frame.getRootPane().setDefaultButton(button);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String msgout="";
					msgout=textField.getText().trim();
					out.writeUTF(msgout);
					out.flush();
					System.out.println("Message sent to the client: "+msgout);
					textField.setText("");
				}catch(IOException e1) {
					System.out.println(e1);
				}
			}
		});
		button.setBounds(425, 319, 108, 49);
		frame.getContentPane().add(button);
	}
	
	private static void startServer() {
		String msgin="";
		try {
			System.out.println("Starting the server...");
			ss=new ServerSocket(port);
			System.out.println("Server active on the port: "+port);
			s=ss.accept();
			System.out.println("Connection succesfull");
			ss.close();
			in=new DataInputStream(s.getInputStream());
			out=new DataOutputStream(s.getOutputStream());
			System.out.println("Waiting for a message from the client...\n");
			msgin=in.readUTF();
			
			while(!msgin.toString().toLowerCase().equals("exit")) {
				
				
				String[] string= msgin.toString().split(",");
				int n= string.length;
				int i=0;
				res= new String();
				System.out.print("\t");
				
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
			    	
			    	writer.write(res);
				}catch (FileNotFoundException e) {
				      System.out.println(e.getMessage());
				}
			    msgin=in.readUTF();
			    			
			}
			System.out.println("\nClosing the connection");
			s.close();
			
			startServer();
			
			
		}catch(IOException e){
			System.err.println(e);
		}
	}
	
	
	
}