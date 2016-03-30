package edu.purdue.cs490.server;

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.purdue.cs490.server.api.ApiRunnable;
import edu.purdue.cs490.server.api.Status;
import edu.purdue.cs490.server.data.HTTPRequest;


public class Server {
    private static Server instance;
    ExecutorService executor;
    ServerSocket serverSocket;
    SQLiteData sqlData;

    Map<String, ApiRunnable> api = new HashMap<>();

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

        apiPaths();

        log.log(Level.INFO, "Server started at " + serverSocket.getLocalSocketAddress());
    }

    public static Server getInstance() {
        return Server.instance;
    }

    public void apiPaths() {
        api.put("/", Status::handleStatus);
        api.put("/status", Status::handleStatus);
        api.put("/status/update/linux", Status::handleStatus);
    }

    public SQLiteData getSQLData() {
        return sqlData;
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
