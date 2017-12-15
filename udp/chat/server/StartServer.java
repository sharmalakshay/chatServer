/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat.server;

/**
 *
 * @author LSHARMA
 */
public class StartServer {
    private int port;
    private Server server;
    
    public StartServer(int port){
        this.port = port;
        server = new Server(port);
    }
    
    public static void main(String[] args){
        int port;
        if(args.length!=1){
            System.out.println("Usage: java ServerMain <port>");
            return;
        }
        port = Integer.parseInt(args[0]);
        new StartServer(port);
    }
}
