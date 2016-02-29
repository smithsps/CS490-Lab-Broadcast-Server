package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.purdue.cs490.server.data.HTTPRequest;
import edu.purdue.cs490.server.data.HTTPMethod;
import edu.purdue.cs490.server.data.HTTPResponse;

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

        String[] tokRequest = request.split("\\r?\\n");

        String[] tokRequestLine = tokRequest[0].split(" ");
        req.setMethod(tokRequestLine[0]);
        req.setUri(tokRequestLine[1]);
        req.setVersion(tokRequestLine[2]);

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

            } catch(Exception e) {
                // TODO: Use a real accepted practice, like not Exception e
                System.out.println("Unable to read from socket");
            }
        }

        return req;
    }

    public void handlePUT(HTTPRequest req) {
        System.out.format("Received: %s %s %s\n", req.getMethod(), req.getUri(), req.getVersion());
        ObjectMapper mapper = new ObjectMapper();
        try {
            System.out.println(req.getBody());
            Map data = mapper.readValue(req.getBody(), Map.class);
            Server.getInstance().occupied.put((String) data.get("name"), (Boolean) data.get("occupied"));

            // 200 = Success, and since we are always successful we always success.
            // In the future we can parse the body and validate it.
            this.outToClient.write("HTTP/1.1 200");
        } catch (IOException ieo) {
            System.out.println("Error while trying to map JSON");
        }
    }

    public void handleGET(HTTPRequest req) {
        System.out.format("Received: %s %s %s\n", req.getMethod(), req.getUri(), req.getVersion());

        HTTPResponse response = new HTTPResponse();

        int totalOccupied = 0;
        for (String computer : Server.getInstance().occupied.keySet()) {
            if (Server.getInstance().occupied.get(computer)) {
                totalOccupied += 1;
            }
        }

        response.setHeader("Access-Control-Allow-Origin:", "*");

        response.setBody("{'moore': "+ totalOccupied +"'}");

        try {
            this.outToClient.write(response.getResponse());
        } catch (IOException ieo) {
            System.err.println("Error while trying to write response" + ieo);
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
                case PUT:
                    handlePUT(req);
                    break;
                case GET:
                    handleGET(req);
                    break;
                default:
                    System.out.println("Received unsupported HTTP Request: " + req.getMethod());
                    this.outToClient.write("HTTP/1.1 500 Unsupported Request");
                    break;
            }
        } catch(IOException ioe) {
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
