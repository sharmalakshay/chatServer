package chatserver;,

import java.io.*;
import java.net.*;

public class clientThread extends Thread{
	
	private int client_id = null;
	private DataInputStream from_client = null;
	private PrintStream to_client = null;
	private Socket client_socket = null;
	private final clientThread[] clients; 
	private int maxclients;
	
	public clientThread(Socket client_socket, clientThread[] clients){
		this.client_socket = client_socket;
		this.clients = clients;
		maxclients = clients.length;
	}
	
	public void run(){
		int maxclients = this.maxclients;
		clientThread[] clients = this.clients;
		
		try{
			Scanner from_client = new Scanner(client_socket.getInputStream());
			to_client = new PrintStream(client_socket.getOutputStream());
			String input;
			String name = null;
			String client_ip = null;
			int client_port = null;
			String room_name = null;
			Random rand = new Random();
			while(name==null || client_ip==null || client_port==null || room_name==null){
				input = from_client.nextLine().trim();
		 			if(input.startsWith("JOIN_CHATROOM:")) room_name = input.substr(12,input.length);
					else if(input.startsWith("CLIENT_IP:")) client_ip = input.substr(8,input.length);
					else if(input.startsWith("PORT:")) client_port = input.substr(5,input.length);
					else if(input.startsWith("CLIENT_NAME:")) name = input.substr(11,input.length);
					else to_client.println("invalid command");
				}
			}
			to_client.println("JOINED_CHATROOM: "+room_name);
			to_client.println("SERVER_IP: "+InetAddress.getLocalHost().getHostAddress());
			to_client.println("PORT: "+client_port);
			to_client.println("ROOM_REF: 956854");
			synchronized(this){
				for(int i = 0; i < maxclients; i++){
					if(clients[i] != null && clients[i] == this){
						client_id = rand.nextInt(88888)+11111;
						to_client.println("JOIN_ID: "+client_id);
						break;
					}
				}
				for(int i = 0; i < maxclients; i++){
					if(clients[i] != null && clients[i] != this){
						clients[i].to_client.println("new user: "+name+" connected");
					}
				}
			}
			while(true){
				String msg = from_client.nextLine();
				if(line.startsWith("LEAVE_CHATROOM:"){
					break;
				}
				
				
			}
			synchronized(this){
				for (int i = 0; i < maxclients; i++){
					if(clients[i] != null && clients[i] != this && clients[i].client_id != null){
						clients[i].to_client.println(""+name+" left");
					}
				}
			}
			to_client.println("LEFT_CHATROOM: 956854\nJOIN_ID: "+this.client_id);
			
			synchronized(this){
				for(int i=0; i < maxclients; i++){
					if(clients[i] == this){
						clients[i] = null;
					}
				}
			}
			
			from_client.close();
			to_client.close();
			client_socket.close();
		} catch(IOException e){
			System.out.println("Error in clientThread line 90");
		}
	}
}