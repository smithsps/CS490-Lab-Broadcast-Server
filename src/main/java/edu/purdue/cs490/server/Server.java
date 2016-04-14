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
import edu.purdue.cs490.server.api.user.Login;
import edu.purdue.cs490.server.api.user.Registration;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;


public class Server {
    private static Server instance;
    ExecutorService executor;
    ServerSocket serverSocket;
    ServerSocket serverSSLSocket;
    SQLiteData sqlData;
    Config config;

    SSLContext sslContext;

    Map<String, ApiRunnable> api = new HashMap<>();

    private static final Logger log = Logger.getLogger(Server.class.getName());

    public Server() {
        if (Server.instance != null) {
            return;
        }
        Server.instance = this;

        config = new Config("config.properties");
        int plainPort = Integer.parseInt(config.get("PlainPort"));
        int sslPort = Integer.parseInt(config.get("SSLPort"));
        int workerPoolSize = Integer.parseInt(config.get("WorkerPoolSize"));

        executor = Executors.newFixedThreadPool(workerPoolSize);
        sslInitialization();

        try {
            serverSocket = new ServerSocket(plainPort);
            serverSSLSocket = sslContext.getServerSocketFactory().createServerSocket(sslPort);
        } catch(IOException e){
            log.log(Level.SEVERE, "Unable to create server socket.", e);
            System.exit(0);
        }

        SQLiteJDBC sqlcreate = new SQLiteJDBC();
        sqlData = new SQLiteData();

        apiPaths();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run(){
                onShutdown();
            }
        });

        log.log(Level.INFO, "Server started on ports: " + plainPort + " and " + sslPort);
    }

    public static Server getInstance() {
        return Server.instance;
    }

    /**
     * Loads keystore.jks for cert and creates a SSL context for later ServerSocket
     * Grabs keystore filename and password from config.
     *
     * For generating jks:
     * keytool -genkey -keyalg RSA -keystore keystore.jks -validity 360 -keysize 2048
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
            fileKeystore = new FileInputStream(config.get("Keystore"));
            keyStore = KeyStore.getInstance("JKS");

            keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyStore.load(fileKeystore, config.get("KeystorePassword").toCharArray());
            keyManagerFactory.init(keyStore, config.get("KeystorePassword").toCharArray());

            sslContext.init(keyManagerFactory.getKeyManagers(), null, null); //Null defaults
        } catch (FileNotFoundException e) {
            log.severe("Keystore file not found, possible misconfiguration.");
            System.exit(0);
        } catch (KeyStoreException | KeyManagementException | UnrecoverableKeyException e) {
            log.severe("Unable to access key in keystore. Exiting..");
            System.exit(0);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Unhandled exception during SSL Init.", e);
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

        api.put("/user/registration", Registration::handleRegistration);
        api.put("/user/login", Login::handleLogin);
    }

    public SQLiteData getSQLData() {
        return sqlData;
    }

    /**
     * Start two threads that accept sockets, one for plaintext, one for SSL
     */
    public void serverLoop() {
        // HTTP
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

        // HTTPS
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
     *   shutdownNow() - Stop any tasks continuing after 2 seconds.
     */
    private void onShutdown() {
        log.info("Exiting server.. stopping in two seconds.");
        executor.shutdown();
        try {
            if (executor.awaitTermination(2, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        try {
            sqlData.c.close();
        } catch (SQLException e) {
            log.warning("Trouble closing sqlite db at shutdown.");
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.serverLoop();
    }
}
