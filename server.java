package chatServer;

import java.io.*;
import java.net.*;

public class server{
	
	private static ServerSocket server_socket = null;
	private static Socket client_socket = null;
	private static final int maxrooms = 5;
	//private static final roomThread[] rooms = new roomThread[maxrooms];
	//private static final int maxclients = 10;
	private static final clientThread[] clients = new clientThread[maxclients];
	
	public static void main(String[] args){
		if(args.length==1){
			int port = args[0];
			System.out.println("port entered:"+port);
			
			try{
				server_socket = new ServerSocket(port);
			}catch(IOException e){
				System.out.println("Error on server.java try 1:\n"+e.getMessage());
			}
			
			while(true){
				try{
					client_socket = server_socket.accept();
					int i = 0;
					for(i = 0; i < maxclients; i++){
						if(clients[i]==null){
							(threads[i] = new clientThread(client_socket, clients)).start();
							break;
						}
					}
					if(i == maxclients){
						System.out.println("Server busy");
						client_socket.close();
					}
				}catch(IOException e){
					System.out.println("Error on server.java try 2:\n"+e.getMessage());
				}
			}
		}
		else{
			System.out.println("enter a valid port");
		}
	}
}