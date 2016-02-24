package edu.purdue.cs490.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

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

    public void analyze(String msg) {
        System.out.println("msg: "+msg);
    }

    // Silly incomplete
    // Probably better abstracted to a class
    public String handleHTTPRequest(String request) {
        String body = "";

        //String[] reqTokens = request.split("\n");

        //String verb = reqTokens[0];
        //String requestURI = reqTokens[1];

        try {
            // We SHOULD use the content length in the header to read the body
            // but atm we are lazy and just add a newline in the script
            body = inFromClient.readLine();
            // 200 = Success, and since we are always successful we always success.
            this.outToClient.write("HTTP/1.1 200");
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            System.out.println("Unable to write to socket");
        }

        return body;
    }

    public void run() {
        try {
            String message = "";
            String read;

            while(((read = inFromClient.readLine()) != null) && !(read.equals(""))){
                message += read + '\n';
            }

            if (message.contains("HTTP")) {
                String body = handleHTTPRequest(message);
                message += body;
            }

            analyze(message);

            this.outToClient.close();
            this.inFromClient.close();
        } catch(Exception e) {
            // TODO: Use a real accepted practice, like not Exception e
            // Need specific Exceptions not general
            System.out.println("Connection Lost!");
        }
    }
}
