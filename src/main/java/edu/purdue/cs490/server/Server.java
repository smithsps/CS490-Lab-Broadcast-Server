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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run(){
                onShutdown();
            }
        });

        log.log(Level.INFO, "Server started at " + serverSocket.getLocalSocketAddress());
    }

    public static Server getInstance() {
        return Server.instance;
    }

    /**
     * Loads keystore.jks for cert and creates a SSL context for later ServerSocket
     * Grabs keystore filename and password from config.
     */
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

    /**
     * Mapping of string paths to handling functions, resolved in HTTPHandler
     */
    public void apiPaths() {
        api.put("/", Status::handleStatus);
        api.put("/status", Status::handleStatus);
        api.put("/status/update/linux", Status::handleUpdateLinux);
    }

    public SQLiteData getSQLData() {
        return sqlData;
    }

    /**
     * Start two threads that accept sockets, one for plaintext, one for SSL
     */
    public void serverLoop() {
        // HTTP Accept
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

        // HTTPS Accept
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

    /**
     * Triggered to run on server shutdown.
     * Used to correctly shutdown our threads and database
     *
     * Threads shutdown in two stages:
     *   shutdown() - Stop receiving tasks i.e. execute(incomingConnection)
     *   shutdownNow() - Stop any tasks continuing after 5 seconds.
     */
    private void onShutdown() {
        log.info("Exiting server.. stopping in five seconds.");
        executor.shutdown();
        try {
            if (executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        try {
            sqlData.c.close();
        } catch (SQLException e) {
            log.warning("Trouble to close sqlite db at shutdown.");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.serverLoop();
    }
}
