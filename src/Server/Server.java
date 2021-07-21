package Server;

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
	static int port=3337;
	private JFrame frame;

	public static void main(String[] args) {
		
		String level;
		String ssid;
		String res="";

	     StringBuilder sb = new StringBuilder();
		List<String[]> dataLines = new ArrayList<>();
		
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
			System.out.println("Waiting for a message from the client...");
			while(!msgin.toLowerCase().equals("exit")) {
				msgin=in.readUTF().toString();
			//	System.out.println("Message received: \n"+msgin+"\n");
		
				String[] stringa= msgin.split(",");
				
				int n= stringa.length;
				int i=0;
				res= new String();
				System.out.println("Vuota?: "+res.isEmpty()+res);
				System.out.print(n+"\t");
				sb.append("[");

			//	System.out.print(msgin);
			//	sb.append("[");
				
				while(i<n) {
					
							String[] stringa2= stringa[i].split(":");
							level= stringa2[1];
							ssid= stringa2[0];
						//	textArea.setText(textArea.getText()+"["+ssid+" "+level+"] ");

							//System.out.println("Message received: \n"+msgin+"\n");
				//			System.out.println("\tRete: "+ssid+"("+level+")    ");
								

						if(i!=0){
							
							 sb.append('\n');
						}
						 if(i!=(n-1)) {
							sb.append(ssid);
						    sb.append(',');
						    sb.append(level);
							//sb.append('\n');

						 }else
						      {sb.append(ssid);
							    sb.append(',');
							    sb.append(level);
						    	sb.append(']');
						    	sb.append('\n');
						      }
						      
						 if(i==(n-1)) {
							 sb.append('\n');
						 }
						 
						 
						      res=sb.toString();
						      i++;
				System.out.print(res+"\n");
				}	//	res.add("] \n");
				
						try (PrintWriter writer = new PrintWriter(new File("dati.csv"))) {
							//sb.append("\n");  
							
					//	      writer.write(sb.toString());
					    
							writer.write(res);
						} catch (FileNotFoundException e) {
						      System.out.println(e.getMessage());
						    }
					    
						//}
				  
				
			}
		
			s.close();
			System.out.println("Closing the connection");
			
		}catch(IOException e){
			System.err.println(e);
		}
		
	
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
	
	
	
}
