/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat;

import java.net.*;
import java.util.*;

/**
 *
 * @author LSHARMA
 */
public class ClientParent implements Runnable{
    private Client client;
    private boolean running = false;
    private Thread run, listen;

    
    
    
    public ClientParent(String name, String address, int port){
        client = new Client(name, address, port);
        boolean connect = client.openConnection(address);
        if(!connect){
            System.out.println("Connection failed!");
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Attempting a connection to "+address+":"+port+", user: "+name);
        String connection = "/c/"+name+"/e/";
        client.send(connection.getBytes());
        
        running = true;
        run = new Thread(this, "Running");
        run.start();
        while(running){
            String usermsg = scanner.nextLine();
            send(usermsg, true);
        }
        
    }
    
    public void run(){
        listen();
    }
    
    private void send(String message, boolean text){
        if(message.equals(""))return;
        if(text){
            message = client.getName()+": "+message;
            message = "/m/"+message+"/e/";
            System.out.println("");
        }
        client.send(message.getBytes());
    }
    
    public void listen() {
		listen = new Thread("Listen") {
			public void run() {
				while (running) {
					String message = client.receive();
					if (message.startsWith("/c/")) {
						client.setID(Integer.parseInt(message.split("/c/|/e/")[1]));
						System.out.println("Successfully connected to server! ID: " + client.getID());
					} else if (message.startsWith("/m/")) {
						String text = message.substring(3);
						text = text.split("/e/")[0];
						System.out.println(text);
					} else if (message.startsWith("/i/")) {
						String text = "/i/" + client.getID() + "/e/";
						send(text, false);
					}
				}
			}
		};
		listen.start();
    }
    
}