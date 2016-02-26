package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs490.server.data.HTTPRequest;

import java.sql.*;



public class TCPHandler implements Runnable{

    Socket clientSocket;
    BufferedWriter outToClient;
    BufferedReader inFromClient;

    public TCPHandler(Socket client) {
        this.clientSocket = client;
        try {
            this.inFromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.outToClient = new BufferedWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()));
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            System.out.println("Server Socket Failed!");
        }
    }


    public HTTPRequest handleHTTPRequest(String request) {

        HTTPRequest req = new HTTPRequest();

        String[] tokRequest = request.split("\n");

        String[] tokRequestLine = tokRequest[0].split(" ");
        req.setMethod(tokRequestLine[0]);
        req.setUri(tokRequestLine[1]);
        req.setVersion(tokRequestLine[2]);

        for (int i = 1; i < tokRequest.length; i++) {
            String[] tokHeader = tokRequest[i].split(": ");
            req.setHeader(tokHeader[0], tokHeader[1]);
        }

        int contentLength = 0;
        if (req.hasHeader("Content-Length")) {
            contentLength = Integer.parseInt(req.getHeader("Content-Length"));
        } else {
            try {
                this.outToClient.write("HTTP/1.1 411 Length Required");
            } catch (IOException ie) {
                return req;
            }
        }

        try {
            char[] buffer = new char[contentLength];
            inFromClient.read(buffer, 0, contentLength);
            req.setBody(new String(buffer));

            // 200 = Success, and since we are always successful we always success.
            // In the future we can parse the body and validate it.
            this.outToClient.write("HTTP/1.1 200");

        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            System.out.println("Unable to write to socket");
        }

        return req;
    }

    //
    public void handlePUT(HTTPRequest req) {
        System.out.format("Received: %s %s %s\n", req.getMethod(), req.getUri(), req.getVersion());
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map data = mapper.readValue(req.getBody(), Map.class);
            System.out.print(data.get("name"));
            System.out.print(data.get("occupied"));

        } catch (IOException ieo) {
            System.out.println("Error while trying to map JSON");
        }
    }

    public void run() {
        try {
            String message = "";
            String read;
            while(((read = inFromClient.readLine()) != null) && !(read.equals(""))){
                message += read + '\n';
            }

            if (!message.contains("HTTP/1.1")) {
                // Only Accept HTTP Requests
                return;
            }

            HTTPRequest req = handleHTTPRequest(message);

            switch(req.getMethod()) {
                case "PUT":
                    handlePUT(req);
                    break;
                case "GET":
                    break;
                default:
                    System.out.println("Received unsupported HTTP Request: " + req.getMethod());
                    break;
            }

        } catch(IOException ioe) {
            // TODO: Use a real accepted practice, like not Exception e
            // Need specific Exceptions not general
            System.out.println("Problem with TCP I/O "  + ioe);
        } finally {
            try {
                this.outToClient.close();
                this.inFromClient.close();
            } catch (IOException ioe) {
                // If we are unable to close the connection, err it's proabably already closed.?
            }
        }
    }
}
