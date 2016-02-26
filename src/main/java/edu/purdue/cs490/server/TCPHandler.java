package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.lang.NoClassDefFoundError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonMappingException;


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


    // Probably best to abstract abstracted to a class
    public String handleHTTPRequest(String request) {
        
        HashMap<String, String>headers = new HashMap<String, String>(8);

        // Parse HTTP Requst and Headers
        String[] tokRequest = request.split("\n");

        String[] tokRequestLine = tokRequest[0].split(" ");
        String method = tokRequestLine[0];
        String uri = tokRequestLine[1];
        String httpVersion = tokRequestLine[2];

        for (int i = 1; i < tokRequest.length; i++) {
            String[] tokHeader = tokRequest[i].split(": ");
            headers.put(tokHeader[0], tokHeader[1]);
        }

        int contentLength = 0;
        if (headers.containsKey("Content-Length")) {
            contentLength = Integer.parseInt(headers.get("Content-Length"));
        } else {
            try {
                this.outToClient.write("HTTP/1.1 411 \n\n Length Required");
            } catch (IOException ie) {
                // If we are unable to write back, client probably closed connection.
            }
            return "";
        }

        try {
            char[] buffer = new char[contentLength];
            inFromClient.read(buffer, 0, contentLength);
            String body = new String(buffer);

            // 200 = Success, and since we are always successful we always success.
            this.outToClient.write("HTTP/1.1 200");

            return body;
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            System.out.println("Unable to write to socket");
        }

        return "";
    }

    // Best to have these as a entire java class eventually.
    public void handlePayload(String body) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map data = mapper.readValue(body, Map.class);
            System.out.println(data.get("name"));
            System.out.println(data.get("occupied"));

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

            if (message.contains("HTTP/1.1")) {
                String body = handleHTTPRequest(message);
                handlePayload(body);
                message += body;
            }
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            // Need specific Exceptions not general
            System.out.println("Connection Lost!");
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
