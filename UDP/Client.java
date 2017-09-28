import java.util.*;
import java.io.*;
import java.net.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class Client extends JFrame{
	
	private DatagramSocket ds;
	private JTextField userText;
	private JTextArea chatWindow;
	private String name;
	private boolean active;
	private String host;
	
	public Client(String name,String host){
		super(name);
		this.name = name;
		this.host = host;
		try{
			ds = new DatagramSocket();
		}catch(IOException e){
			System.out.println("Could not create socket");
		}
		userText=new JTextField();
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
		ableToType(true);
		sendMessage("has been added\n");
		while(true){
			byte[] buffer = new byte[1024];
			DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
			try{
				ds.receive(dp);
				String str = new String(dp.getData(),0,dp.getLength());
				showMessage(str);
			}catch(IOException e){
				System.out.println("Unable to receive the datagram");
			}
		}
	}
	
	private void sendMessage(String str){
		String msg = name + " - " + str;
		byte[] arr = msg.getBytes();
		try{
			DatagramPacket dp = new DatagramPacket(arr,arr.length,InetAddress.getByName(host),9000);
			ds.send(dp);
		}catch(IOException e){
			System.out.println("Unable to send the message");
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