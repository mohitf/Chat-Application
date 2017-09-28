import java.net.*;
import javax.swing.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame{
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message;
	private String serverIP;
	private Socket connection;
	private String name;
	private int port;
	private boolean active;
	
	public Client(String host,String name,int port){
		super(name);
		this.name=name;
		this.port=port;
		userText=new JTextField();
		serverIP=host;
		userText.setEditable(false);
		userText.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					sendMessage(event.getActionCommand()+"\n");
					userText.setText("");
				}
			}
		);
		add(userText,BorderLayout.NORTH);
		chatWindow=new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
		active=false;
	}
	
	public void startRunning(){
		try{
			connectToServer();
			setUpStreams();
			whileChatting();
		}catch(IOException e){
			showMessage("Connection to the server failed\n");
		}finally{
			closeCrap();
		}
	}
	
	public void connectToServer() throws IOException{
		showMessage("Connecting to server\n");
		connection=new Socket(InetAddress.getByName(serverIP),port);
		showMessage("Connected to server "+connection.getInetAddress().getHostName()+"\n");
	}
	
	public void setUpStreams() throws IOException{
		output=new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input=new ObjectInputStream(connection.getInputStream());
		showMessage("Streams are now setup!\n");
		active=true;
	}
	
	public void whileChatting() throws IOException{
		ableToType(true);
		sendMessage(name);
		String message="";
		do{
			try{
				message=(String)input.readObject();
				showMessage(message);
			}catch(ClassNotFoundException e){
				showMessage("Unable to recieve the message to the server\n");
			}
		}while(active);
	}
	
	public void closeCrap(){
		showMessage("Closing connection to server...\n");
		ableToType(false);
		try{
			input.close();
			output.close();
			connection.close();
		}catch(IOException e){
			showMessage("Connection to the server failed\n");
		}
	}
	
	public void sendMessage(String msg){
		try{
			output.writeObject(msg);
			output.flush();
		}catch(IOException e){
			showMessage("Message couldn't be sent\n");
		}
		if(msg.length()==4){
			if(msg.charAt(0)=='E'&&msg.charAt(1)=='N'&&msg.charAt(2)=='D'){
				active=false;
				return;
			}
		}
	}
	
	private void showMessage(final String msg){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					chatWindow.append(msg);
				}
			}
		);
	}
	
	public void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
	
}