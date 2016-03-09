package edu.purdue.cs490.server;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {
    private static Server instance;
    ExecutorService executor;
    ServerSocket serverSocket;

    public Server(){
        if (Server.instance != null) {
            return;
        }
        Server.instance = this;

        executor = Executors.newFixedThreadPool(60);
        try{
            serverSocket = new ServerSocket(5000);
        }catch(Exception e){
            // TODO: Use a real accepted practice, like not Exception e and logging
            System.out.println("Server Socket Failed!");
        }

        SQLiteJDBC sqlcreate = new SQLiteJDBC();
        sqlcreate.createSQLdatabase();

        System.out.println("Server started at " + serverSocket.getLocalSocketAddress());
    }

    public static Server getInstance() {
        return Server.instance;
    }

    public void serverLoop() {
        while(true){
            try{
                Socket cs2 = this.serverSocket.accept();
                TCPHandler cb2 = new TCPHandler(cs2);
                executor.execute(cb2);
            }catch(Exception e) {
                // TODO: Use a real accepted practice, like not Exception e
                System.out.println("Error while accepting socket.");
            }
        }
    }

    public static void main(String[] args){
        try {
            Server cb = new Server();
            cb.serverLoop();
        }catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            System.out.println("Whoops! It didn't work!");
        }
    }
}
