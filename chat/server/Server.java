/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 *
 * @author LSHARMA
 */
public class Server implements Runnable {
    private List<ServerClient> clients = new ArrayList<ServerClient>();
    private List<Integer> clientResponse = new ArrayList<Integer>();
    
    private DatagramSocket socket;
    private int port;
    private boolean running = false;
    private Thread run, manage, send, receive;
    private final int MAX_ATTEMPTS = 5;
    

    
    
    public Server(int port){
        this.port = port;
        try{
            socket = new DatagramSocket(port);
        }catch(SocketException e){
            System.out.println("Error: "+e.getMessage());
        }
        run = new Thread(this, "Server");
        run.start();
    }
    
    public void run(){
        running = true;
        System.out.println("Server started on port "+port);
        manageClients();
        receive();
    }
    
    private void manageClients(){
        manage = new Thread("Manage"){
            public void run(){
                while(running){
                    sendToAll("/i/server");
                    sendStatus();
                    try{
                        Thread.sleep(2000);
                    }catch(InterruptedException e){
                        System.out.println("Error: "+e.getMessage());
                    }
                    for(int i = 0; i < clients.size(); i++){
                        ServerClient c  = clients.get(i);
                        if(!clientResponse.contains(c.getID())){
                            if(c.attempt >= MAX_ATTEMPTS){
                                disconnect(c.getID(), false);
                            } else{
                                c.attempt++;
                            }
                        } else{
                            clientResponse.remove(new Integer(c.getID()));
                            c.attempt = 0;
                        }
                    }
                }
            }
        };
        manage.start();
    }
    
    private void sendStatus(){
        if(clients.size() <= 0) return;
        String users = "/u/";
        for(int i = 0; i < clients.size()-1; i++){
            users += clients.get(i).name + "/n/";
        }
        users += clients.get(clients.size() - 1).name + "/e/";
        sendToAll(users);
    }
    
    private void receive(){
        receive = new Thread("Receive"){
            public void run(){
                while(running){
                    byte[] data = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(data, data.length);
                    try{
                        socket.receive(packet);
                    } catch(SocketException e){
                        System.out.println("Error: "+e.getMessage());
                    } catch(IOException e){
                        System.out.println("Error: "+e.getMessage());
                    }
                    process(packet);
                }
            }
        };
        receive.start();
    }
    
    private void sendToAll(String message){
        if(message.startsWith("/m/")){
            String text = message.substring(3);
            //text = text.split("/e/")[0];
            //System.out.println(message);
        }
        for(int i = 0; i < clients.size(); i++){
            ServerClient client = clients.get(i);
            send(message.getBytes(), client.address, client.port);
        }
    }
    
    
    private void send(final byte[] data, final InetAddress address, final int port){
        send = new Thread("Send"){
            public void run(){
                DatagramPacket packet = new DatagramPacket(data, data.length, address, port);
                try{
                    socket.send(packet);
                } catch(IOException e){
                    System.out.println("Error: "+e.getMessage());
                }
            }
        };
        send.start();
    }
    
    private void send(String message, InetAddress address, int port){
        message += "/e/";
        send(message.getBytes(), address, port);
    }
    
    private void process(DatagramPacket packet){
        String string = new String(packet.getData());
        if(string.startsWith("/c/")){
            //UUID id = UUID.randomUUID();
            int id = UniqueIdentifier.getIdentifier();
            String name = string.split("/c/|/e/")[1];
            System.out.println(name+"("+id+") connected!");
            clients.add(new ServerClient(name, packet.getAddress(), packet.getPort(), id));
            String ID = "/c/"+id;
            send(ID, packet.getAddress(), packet.getPort());
        } else if(string.startsWith("/m/")){
            sendToAll(string);
        } else if(string .startsWith("/d/")){
            String id = string.split("/d/|/e/")[1];
            disconnect(Integer.parseInt(id), true);
        } else if(string.startsWith("/i/")){
            clientResponse.add(Integer.parseInt(string.split("/i/|/e/")[1]));
        } else{
            System.out.println(string);
        }  
    }
    
    private void quit(){
        for(int i = 0; i < clients.size(); i++){
            disconnect(clients.get(i).getID(), true);
        }
        running = false;
        socket.close();
    }
    
    private void disconnect(int id, boolean status){
        ServerClient c = null;
        boolean existed = false;
        for(int i = 0; i < clients.size(); i++){
            if(clients.get(i).getID() == id){
                c = clients.get(i);
                existed = true;
                break;
            }
        }
        if(!existed)return;
        String message = "";
        if(status){
            message = "Client "+c.name+" ("+c.getID()+") @ "+c.address.toString()+":"+c.port+" disconnected.";
        } else{
            message = "Client "+c.name+" ("+c.getID()+") @ "+c.address.toString()+":"+c.port+" timed out.";
        }
        System.out.println(message);
    }
    
    
    
}
