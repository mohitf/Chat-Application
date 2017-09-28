import java.net.*;
import java.util.ArrayList;
import java.io.*;

public class Server{

	private ServerSocket server;
	private ArrayList<ClientThread> al;
	int port;

	public Server(int port){
		al=new ArrayList<ClientThread>();
		this.port=port;
		try{
			server=new ServerSocket(port,100);
		}catch(IOException e){
			System.out.println("Could not make a server at the given port");
		}
	}
	
	public void startRunning(){
		while(true){
			try{
				Socket connection=server.accept();
				ClientThread ct=new ClientThread(connection,this);
				al.add(ct);
				ct.start();
			}catch(IOException e){
				System.out.println("Error while adding a client in the room");
			}
		}
	}
	
	public void removeClientThread(ClientThread ct){
		al.remove(ct);
	}
	
	public void broadcast(String msg){
		for(int i=0;i<al.size();i++){
			al.get(i).sendMessage(msg);
		}
	}
	

}

class ClientThread implements Runnable{
	
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private Server server;
	private String name;
	
	public ClientThread(Socket connection,Server server){
		this.connection=connection;
		this.server=server;
		try{
			output=new ObjectOutputStream(connection.getOutputStream());
			output.flush();
			input=new ObjectInputStream(connection.getInputStream());
			name=(String)input.readObject();
		}catch(IOException e){
			System.out.println("Error while setting up streams with the client");
		}catch(ClassNotFoundException e){
			System.out.println("Error while reading the client's name");
		}
	}
	
	public void run(){
		String msg="";
		do{
			try{
				msg=(String)input.readObject();
			}catch(ClassNotFoundException e){
				System.out.println("Could not read the message sent by "+name);
			}catch(IOException e){
				System.out.println("Error while receiving message from "+name);
			}
			if(msg.length()==4){
				if(msg.charAt(0)=='E'&&msg.charAt(1)=='N'&&msg.charAt(2)=='D'){
					server.removeClientThread(this);
					closeCrap();
					return;
				}
			}
			server.broadcast(name+" - "+msg);
		}while(true);
	}
	
	public void start(){
		Thread thread=new Thread(this);
		thread.start();
	}
	
	private void closeCrap(){
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException e){
			System.out.println("Error while closing the connection of "+name);
		}
	}
	
	public void sendMessage(String msg){
		try{
			output.writeObject(msg);
			output.flush();
		}catch(IOException e){
			System.out.println("Message could not be sent to "+name);
		}
	}
	
}