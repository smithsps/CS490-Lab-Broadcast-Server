package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPResponse;



public class HTTPHandler implements Runnable{
    Socket clientSocket;
    BufferedWriter outToClient;
    BufferedReader inFromClient;
    SQLiteData sqlData;
    Boolean ssl;

    private static final Logger log = Logger.getLogger(HTTPHandler.class.getName());

    public HTTPHandler(Socket client, Boolean ssl) {
        this.clientSocket = client;
        this.ssl = ssl;
        this.sqlData = Server.getInstance().sqlData;

        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        } catch(IOException e) {
            log.log(Level.WARNING, "Could not create input reader for client.", e);
        }
        try {
            this.outToClient = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        } catch(IOException e) {
            log.log(Level.WARNING, "Could not create output writer for client.", e);
        }
    }


    public HTTPRequest handleHTTPRequest(String request) {

        HTTPRequest req = new HTTPRequest();

        String[] tokRequest = request.split("\\r?\\n");

        String[] tokRequestLine = tokRequest[0].split(" ");
        req.setMethod(tokRequestLine[0]);
        req.setUri(tokRequestLine[1]);
        req.setVersion(tokRequestLine[2]);
        req.setSSL(isSSL());

        for (int i = 1; i < tokRequest.length; i++) {
            String[] tokHeader = tokRequest[i].split(": ");
            req.setHeader(tokHeader[0], tokHeader[1]);
        }

        if (req.hasHeader("Content-Length")) {
            int contentLength = Integer.parseInt(req.getHeader("Content-Length"));

            try {
                char[] buffer = new char[contentLength];
                inFromClient.read(buffer, 0, contentLength);
                req.setBody(new String(buffer));
            } catch(IOException e) {
                log.log(Level.WARNING, "Unable to read from socket", e);
            }
        }

        return req;
    }

    public void run() {
        try {
            String message = "";
            String read;
            while (((read = inFromClient.readLine()) != null) && !(read.equals(""))) {
                message += read + '\n';
            }

            log.fine("msg: " + message);
            if (!message.contains("HTTP/1.1")) {
                // Only Accept HTTP Requests
                return;
            }

            HTTPRequest request = handleHTTPRequest(message);
            log.fine(String.format("Received: %s %s %s\n", request.getMethod(), request.getUri(), request.getVersion()));

            if (Server.getInstance().api.containsKey(request.getUri())) {
                HTTPResponse response = Server.getInstance().api.get(request.getUri()).run(request);
                response.setHeader("Access-Control-Allow-Origin", "*");
                response.setHeader("Content-Type" , "application/json");

                this.outToClient.write(response.getResponse());
            } else {
                log.log(Level.WARNING, "Received unsupported HTTP Request: " + request.getUri());
                this.outToClient.write(HTTPResponse.getHTTPError(404).getResponse());
            }
        } catch(IOException e) {
            log.log(Level.WARNING, "Problem with Socket Communication", e);
        } finally {
            try {
                this.outToClient.close();
                this.inFromClient.close();
            } catch (IOException e) {
                log.log(Level.WARNING, "Trouble trying to close socket.", e);
            }
        }
    }

    public Boolean isSSL() {
        return ssl;
    }
}
