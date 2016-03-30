package edu.purdue.cs490.server;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {
    private static Server instance;
    ExecutorService executor;
    ServerSocket serverSocket;
    SQLiteData sqlData;

    private static final Logger log = Logger.getLogger(Server.class.getName());

    public Server() {
        if (Server.instance != null) {
            return;
        }
        Server.instance = this;

        executor = Executors.newFixedThreadPool(60);
        try {
            serverSocket = new ServerSocket(5000);
        } catch(IOException e){
            log.log(Level.SEVERE, "Unable to create server socket.", e);
        }

        SQLiteJDBC sqlcreate = new SQLiteJDBC();
        sqlcreate.createSQLdatabase();
        sqlData = new SQLiteData();

        log.log(Level.INFO, "Server started at " + serverSocket.getLocalSocketAddress());
    }

    public static Server getInstance() {
        return Server.instance;
    }

    public void serverLoop() {
        while(true) {
            try {
                Socket sock = this.serverSocket.accept();
                HTTPHandler thread = new HTTPHandler(sock);
                executor.execute(thread);
            } catch(IOException e) {
                log.log(Level.SEVERE, "Unable to create server socket.", e);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.serverLoop();
    }
}
