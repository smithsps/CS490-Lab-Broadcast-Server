package edu.purdue.cs490.server;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.KeyManagementException;
import java.security.UnrecoverableKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.purdue.cs490.server.api.ApiRunnable;
import edu.purdue.cs490.server.api.Status;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;


public class Server {
    private static Server instance;
    ExecutorService executor;
    ServerSocket serverSocket;
    ServerSocket serverSSLSocket;
    SQLiteData sqlData;

    SSLContext sslContext;

    Map<String, ApiRunnable> api = new HashMap<>();

    private static final Logger log = Logger.getLogger(Server.class.getName());

    public Server() {
        if (Server.instance != null) {
            return;
        }
        Server.instance = this;

        sslInitialization();

        executor = Executors.newFixedThreadPool(60);
        try {
            serverSocket = new ServerSocket(5000);
            serverSSLSocket = sslContext.getServerSocketFactory().createServerSocket(5001);
        } catch(IOException e){
            log.log(Level.SEVERE, "Unable to create server socket.", e);
            System.exit(0);
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

    private void sslInitialization(){
        FileInputStream fileKeystore;
        KeyStore keyStore;
        KeyManagerFactory keyManagerFactory = null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            log.severe("TLS for SSL not available. Exiting..");
            System.exit(0);
        }

        try {
            fileKeystore = new FileInputStream("keystore.jks");
            keyStore = KeyStore.getInstance("JKS");

            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyStore.load(fileKeystore, "cs490dev".toCharArray());
            keyManagerFactory.init(keyStore, "cs490dev".toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), null, null); //Null defaults
        } catch (FileNotFoundException e) {
            log.severe("Keystore file not found, possible misconfiguration.");
            System.exit(0);
        } catch (KeyStoreException | KeyManagementException | UnrecoverableKeyException e) {
            log.severe("Unable to access key in keystore. Exiting..");
            System.exit(0);
        } catch (Exception e) {
            log.severe("Unhandled exception during SSL Init.");
            System.exit(0);
        }
    }

    public void apiPaths() {
        api.put("/", Status::handleStatus);
        api.put("/status", Status::handleStatus);
        api.put("/status/update/linux", Status::handleUpdateLinux);
    }

    public SQLiteData getSQLData() {
        return sqlData;
    }

    public void serverLoop() {
        //HTTP Accept
        executor.execute(() -> {
            while(true) {
                try {
                    Socket sock = this.serverSocket.accept();
                    HTTPHandler thread = new HTTPHandler(sock, false);
                    executor.execute(thread);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Unable to create server socket.", e);
                }
            }
        });

        //HTTPS Accept
        executor.execute(() -> {
            while(true) {
                try {
                    Socket sock = this.serverSSLSocket.accept();
                    HTTPHandler thread = new HTTPHandler(sock, true);
                    executor.execute(thread);
                } catch (IOException e) {
                    log.log(Level.SEVERE, "Unable to create server socket.", e);
                }
            }
        });
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.serverLoop();
    }
}
