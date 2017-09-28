import java.util.*;
import java.io.*;
import java.net.*;

public class Server {
	
	private DatagramSocket ds;
	private ArrayList<String> al;
	private ArrayList<Integer> pn;
	
	public Server(){
		try{
			ds = new DatagramSocket(9000);
		}catch(IOException e){
			System.out.println("Unable to create a datagram socket");
		}
		al = new ArrayList<String>();
		pn = new ArrayList<Integer>();
	}
	
	public void startRunning(){
		byte[] buffer = new byte[1024];
		DatagramPacket dp = new DatagramPacket(buffer,buffer.length);
		while(true){
			try{
				ds.receive(dp);
				String str = new String(dp.getData(),0,dp.getLength());
				String ip = dp.getAddress().getHostAddress();
				int port = dp.getPort();
				if(check(ip,port)){
					al.add(ip);
					pn.add(dp.getPort());
				}
				broadcast(str);
			}catch(IOException e){
				System.out.println("Error in receiving message");
			}
		}
	}
	
	private boolean check(String ip,int port){
		for(int i=0;i<al.size();i++)
			if(al.get(i).equals(ip) && pn.get(i)==port)
				return false;
		return true;
	}
	
	private void broadcast(String str){
		byte[] arr = str.getBytes();
		for(int i=0;i<al.size();i++){
			try{
				DatagramPacket dp = new DatagramPacket(arr,arr.length,InetAddress.getByName(al.get(i)),pn.get(i));
				ds.send(dp);
			}catch(Exception e){
				System.out.println("Unable to send message to " + al.get(i));
			}
		}
	}

}